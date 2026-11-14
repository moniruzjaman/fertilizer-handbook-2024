#!/usr/bin/env python3
"""Parse the BARC FRG-2024 handbook (Bijoy-encoded 2-up PDF) into structured AEZ JSON.

v3: decode cache over all tokens; halves split at AEZ-header y (no double-count);
season cells (incl. vertically merged ones spanning rows) resolved by y-nearest then
reading-order inheritance; dose continuation lines merge with y-guard.
"""
import re, json, sys, warnings
warnings.filterwarnings('ignore')
sys.path.insert(0, '/tmp/bijoy-shahad')
import unicodeconverter

BBOX = '/tmp/frg_bbox.xml'
OUT = '/home/z/my-project/scripts/frg_aez_data.json'

BN_DIGITS = {'০':'0','১':'1','২':'2','৩':'3','৪':'4','৫':'5','৬':'6','৭':'7','৮':'8','৯':'9'}
def bn2en(s):
    return ''.join(BN_DIGITS.get(c, c) for c in s)

YIELD_RE = re.compile(r'(\d+(?:\.\d+)?)\s*[+±]\s*(\d+(?:\.\d+)?)')
SEASON_RE = re.compile(r'(রবি|খরিফ-১|খরিফ-২|বছরব্যাপী)')
SEASONS_ALL = ('রবি', 'খরিফ-১', 'খরিফ-২', 'বছরব্যাপী')

def parse_pages():
    pages = []
    cur = None
    pat = re.compile(r'<page width="([\d.]+)" height="([\d.]+)">|<word xMin="([\d.]+)" yMin="([\d.]+)" xMax="([\d.]+)" yMax="([\d.]+)">([^<]*)</word>')
    for m in pat.finditer(open(BBOX, encoding='utf-8').read()):
        if m.group(1):
            cur = {'w': float(m.group(1)), 'h': float(m.group(2)), 'words': []}
            pages.append(cur)
        else:
            cur['words'].append((float(m.group(3)), float(m.group(4)), float(m.group(5)), float(m.group(6)), m.group(7)))
    return pages

def group_lines(words, ytol=5.0):
    if not words:
        return []
    ws = sorted(words, key=lambda w: (w[1], w[0]))
    lines, curline, cury = [], [], None
    for w in ws:
        if cury is None or abs(w[1] - cury) <= ytol:
            curline.append(w)
            cury = w[1] if cury is None else min(cury, w[1])
        else:
            lines.append(sorted(curline, key=lambda t: t[0]))
            curline, cury = [w], w[1]
    if curline:
        lines.append(sorted(curline, key=lambda t: t[0]))
    return lines

DECODE_CACHE = {}
def dec(tok):
    if tok not in DECODE_CACHE:
        try:
            DECODE_CACHE[tok] = unicodeconverter.convert_bijoy_to_unicode(tok)
        except Exception:
            DECODE_CACHE[tok] = tok
    return DECODE_CACHE[tok]

def warm_cache(pages):
    for p in pages:
        for w in p['words']:
            dec(w[4])

def is_num(tok):
    t = bn2en(tok.replace(',', ''))
    return bool(re.fullmatch(r'\d+(?:\.\d+)?', t)) or t in ('-', '—')

def dline(ln):
    return ' '.join(dec(w[4]) for w in ln)

def half_split(page):
    mid = page['w'] / 2
    return ([w for w in page['words'] if w[2] < mid],
            [w for w in page['words'] if w[0] >= mid])

def is_season_word(w):
    return dec(w[4]).strip() in SEASONS_ALL

GARBLE = [
    ('ন¤œি', 'নিম্ন'), ('নিম্নি ', 'নিম্ন '), ('ন¤œ', 'নিম্ন'), ('ৌ', 'ৌ'), ('ো', 'ো'), ('ঁা', 'াঁ'),
    ('ে্র', '্রে'), ('কেজ/ি', 'কেজি/'), ('পারবেশ', 'পরিবেশ'),
    ('শে্র', 'শ্রে'), ('ডে্র', 'ড্রে'), ('সে্র', 'স্রে'),
]
def fix(s):
    for a, b in GARBLE:
        s = s.replace(a, b)
    return s

def make_row(season, name, yv, ypm, nums, extra):
    def f(x):
        x = bn2en(x).replace(',', '').strip('.,;:')
        m = re.fullmatch(r'(\d+(?:\.\d+)?)', x)
        return float(m.group(1)) if m else None
    return {'season': season, 'name': name, 'yield': yv, 'yield_pm': ypm,
            'urea': f(nums[0]), 'tsp': f(nums[1]), 'mop': f(nums[2]), 'gypsum': f(nums[3]),
            'mgso4': f(nums[4]), 'zinc': f(nums[5]), 'boric': f(nums[6]), 'organic': f(nums[7]),
            'extra': extra.strip()}

def process_segment(words, cur, anomalies):
    """Parse one half-page segment (below one AEZ header) for crop rows + metadata."""
    season_xs = [w[0] for w in words if is_season_word(w)]
    x_table = min(season_xs) - 8 if season_xs else None
    if x_table:
        meta_w = [w for w in words if w[2] < x_table]
        tab_w = [w for w in words if w[0] >= x_table - 4]
    else:
        meta_w, tab_w = [], words

    for ln in group_lines(words):
        t = fix(dline(ln).strip())
        if cur is None:
            continue
        am = re.search(r'\((\d+)\s*একর\)', t)
        if am and cur['area_acres'] is None:
            cur['area_acres'] = int(bn2en(am.group(1)))
        if re.match(r'^(উঁচু জমি|মাঝারি উঁচু জমি|মাঝারি নিচু জমি|নিচু জমি|সমতল পাহাড়ি|পাহাড়ি ঢাল|চূড়া|শীর্ষ)', t):
            cur['soils'].append(t)

    rows, cells, pending, completed = [], [], None, []
    for ln in group_lines(tab_w):
        y = ln[0][1]
        text = fix(dline(ln)).strip()
        if not text:
            continue
        norm = re.sub(r'\s*\+\s*', '+', text).strip()
        norm = re.sub(r'বছর\s*ব্যাপী', 'বছরব্যাপী', norm)
        if pending is not None and y - pending['y'] < 20:
            has_yield = bool(YIELD_RE.search(norm))
            has_season = bool(SEASON_RE.search(norm))
            if not has_yield:
                vals = [t2 for t2 in norm.split() if is_num(t2)]
                if vals and len(vals) >= 4:
                    need = 8 - len(pending['nums'])
                    pending['nums'].extend(vals[:need])
                    if has_season:
                        sm2 = SEASON_RE.search(norm)
                        pending['season'] = sm2.group(1)
                    if len(pending['nums']) == 8:
                        rows.append(make_row(pending['season'], pending['name'], pending['yield'],
                                             pending['yield_pm'], pending['nums'], pending['extra']))
                        rows[-1]['y'] = pending['y']
                        completed.append(rows[-1])
                        pending = None
                    continue
        # strip an AEZ header merged into this line (row follows on same y)
        hm = re.search(r'কৃষি পরিবেশ অঞ্চল-\d+[:\s]', norm)
        if hm:
            rest_txt = norm[hm.end():]
            nm = SEASON_RE.search(rest_txt)
            norm = rest_txt[nm.start():] if nm else rest_txt
        # standalone season cell
        if norm in SEASONS_ALL:
            cells.append((y, norm))
            continue
        sm = SEASON_RE.search(norm)
        season, rest = (sm.group(1), norm[sm.end():].strip()) if sm else (None, norm)
        ym = YIELD_RE.search(rest)
        if ym and season is None and not YIELD_RE.search(rest[ym.end():]):
            # name + yield only, no doses -> secondary crop of the last completed intercrop row
            nm = rest[:ym.start()].strip(' ।,;+')
            if completed and nm:
                r0 = completed[-1]
                if len(completed) and not r0.get('secondary') and rows and rows[-1] is r0:
                    r0['secondary'] = {'name': nm, 'yield': float(bn2en(ym.group(1))), 'yield_pm': float(bn2en(ym.group(2)))}
                    r0['name'] = (r0['name'] + ' + ' + nm).strip('+ ')
                    continue
        if not ym:
            toks = norm.split()
            if len(toks) >= 3 and toks[1] in ('পতিত', 'পতিৎ'):
                rows.append(make_row(season, 'পতিত', None, None, ['-'] * 8, ''))
                rows[-1]['y'] = y
                continue
            anomalies.append(('non-row', cur['no'] if cur else -1, text[:70]))
            continue
        name = rest[:ym.start()].strip(' ।,;+')
        nums, extra = [], []
        for tok in rest[ym.end():].split():
            (nums if is_num(tok) else extra).append(tok)
        if len(nums) == 8:
            r = make_row(season, name, float(bn2en(ym.group(1))), float(bn2en(ym.group(2))), nums, ' '.join(extra))
            r['y'] = y
            rows.append(r)
        elif len(nums) > 8:
            r = make_row(season, name, float(bn2en(ym.group(1))), float(bn2en(ym.group(2))), nums[:8],
                         (' '.join(extra) + ' |n9+:' + ','.join(nums[8:])).strip())
            r['y'] = y
            rows.append(r)
            anomalies.append(('extra-nums', cur['no'] if cur else -1, text[:80]))
        else:
            pending = {'y': y, 'season': season, 'name': name, 'yield': float(bn2en(ym.group(1))),
                       'yield_pm': float(bn2en(ym.group(2))), 'nums': nums, 'extra': ' '.join(extra)}

    if pending is not None:
        rows.append(make_row(pending['season'], pending['name'], pending['yield'], pending['yield_pm'],
                             pending['nums'] + ['-'] * (8 - len(pending['nums'])), pending['extra']))
        rows[-1]['y'] = pending['y']
        anomalies.append(('uncompleted-row', cur['no'] if cur else -1, pending['name']))

    # 1) global greedy 1:1 cell->row matching by |dy| (closest pairs win)
    pairs = []
    for cy, s in cells:
        for r in rows:
            if r['season'] is None:
                pairs.append((abs(r['y'] - cy), cy, s, id(r)))
    pairs.sort()
    used_cells, used_rows = set(), set()
    for dy, cy, s, rid in pairs:
        if cy in used_cells or rid in used_rows or dy > 15:
            continue
        used_cells.add(cy); used_rows.add(rid)
        for r in rows:
            if id(r) == rid:
                r['season'] = s
                break
    for cy, s in cells:
        if cy not in used_cells:
            anomalies.append(('unused-cell', cur['no'] if cur else -1, s))
    # 2) reading-order inheritance, max distance 2, clean sources only
    for i, r in enumerate(rows):
        if r['season'] is None:
            for d in range(1, 3):
                if i - d >= 0 and rows[i - d]['season'] and not rows[i - d]['season'].endswith(('?', '—')):
                    r['season'] = rows[i - d]['season'] + '?'
                    break
                if i + d < len(rows) and rows[i + d]['season'] and not rows[i + d]['season'].endswith(('?', '—')):
                    r['season'] = rows[i + d]['season'] + '?'
                    break
            if r['season'] is None:
                r['season'] = '—'
                anomalies.append(('no-season', cur['no'] if cur else -1, r['name']))
    for r in rows:
        cur['crops'].append(r)

def main():
    pages = parse_pages()
    warm_cache(pages)
    print(f"pages: {len(pages)}, words: {sum(len(p['words']) for p in pages)}, unique tokens: {len(DECODE_CACHE)}")

    aezs, anomalies = [], []
    cur = None
    for page in pages:
        if not page['words']:
            continue
        for half in half_split(page):
            # find AEZ headers in this half (y positions)
            heads = []
            for ln in group_lines(half):
                t = fix(dline(ln)).strip()
                m = re.search(r'কৃষি পরিবেশ অঞ্চল-(\d+)[:\s]+(.+)', t)
                if m and 'ভাগ করা' not in t and 'অঞ্চলে' not in t and 'মানচিত্র' not in t:
                    heads.append((ln[0][1], int(bn2en(m.group(1))), m.group(2).strip(' :।')))
            if not heads:
                if cur:
                    process_segment(half, cur, anomalies)
                continue
            # split the half at each header y
            bounds = [h[0] for h in heads] + [10 ** 9]
            segs = []
            prev = -1
            for (hy, no, nm), nxt in zip(heads, bounds[1:]):
                segs.append((prev, hy, no, nm))
                prev = hy
            for start_y, end_y, no, nm in segs:
                # rows above the first header belong to previous AEZ
                above = [w for w in half if start_y < 0 or (w[1] < end_y and (start_y < 0 or w[1] >= start_y))]
                if start_y == -1:
                    above = [w for w in half if w[1] < end_y]
                else:
                    above = [w for w in half if start_y <= w[1] < end_y]
                if cur is not None and start_y == -1:
                    pass
                # first segment (before first header) handled below
            # simpler: build segments list with owner
            # segment 0: words above first header -> previous AEZ (if any)
            first_y = heads[0][0]
            if cur is not None:
                above = [w for w in half if w[1] < first_y - 2]
                process_segment(above, cur, anomalies)
            # per header: new AEZ + words from this header to next header
            for idx, (hy, no, nm) in enumerate(heads):
                nxt_y = heads[idx + 1][0] if idx + 1 < len(heads) else 10 ** 9
                seg = [w for w in half if hy - 2 <= w[1] < nxt_y]
                cur = {'no': no, 'name': nm, 'area_acres': None, 'soils': [], 'districts': [], 'crops': []}
                aezs.append(cur)
                process_segment(seg, cur, anomalies)

    print(f"AEZs: {len(aezs)} -> {[a['no'] for a in aezs]}")
    print(f"crops: {sum(len(a['crops']) for a in aezs)}, anomalies: {len(anomalies)}")
    from collections import Counter
    print('anomaly kinds:', dict(Counter(a[0] for a in anomalies)))
    for a in anomalies[:15]:
        print('  ANOM', a[0], 'aez', a[1], str(a[2])[:80])
    for a in aezs:
        inh = sum(1 for c in a['crops'] if c['season'].endswith('?'))
        print(f"  AEZ {a['no']:>2} {a['name'][:40]:40} area={str(a['area_acres']):>8} soils={len(a['soils'])} crops={len(a['crops'])} inh={inh}")
    # agronomic sanity report
    CROP_SEASON = {'আলু': 'রবি', 'টমেটো': 'রবি', 'বাঁধাকপি': 'রবি', 'ফুলকপি': 'রবি', 'মুলা': 'রবি',
        'গম': 'রবি', 'সরিষা': 'রবি', 'মসুর': 'রবি', 'খেসারি': 'রবি', 'বোরো': 'রবি', 'পিঁয়াজ': 'রবি',
        'আমন': 'খরিফ', 'আউশ': 'খরিফ', 'পাট': 'খরিফ', 'তিল': 'খরিফ', 'আখ': 'বছরব্যাপী', 'আদা': 'বছরব্যাপী',
        'হলুদ': 'বছরব্যাপী'}
    flags = 0
    for a in aezs:
        for c in a['crops']:
            for k, want in CROP_SEASON.items():
                if k in c['name']:
                    got = c['season'].replace('?', '')
                    ok = (got == want) or (want == 'খরিফ' and got.startswith('খরিফ')) or want == 'বছরব্যাপী'
                    if not ok and not c['season'].endswith('?'):
                        print(f"  SEASON-FLAG AEZ {a['no']}: {c['name']} season={c['season']} expected~{want}")
                        flags += 1
                    break
    print('season flags:', flags)

    # agronomic auto-correction for unambiguous crops (kept transparent)
    FIX_SEASON = [('আখ', 'বছরব্যাপী'), ('আদা', 'বছরব্যাপী'), ('হলুদ', 'বছরব্যাপী'), ('সরিষা', 'রবি'), ('আউশ', 'খরিফ-১'),
                  ('আলু', 'রবি'), ('মুলা', 'রবি'), ('টমেটো', 'রবি'), ('বাঁধাকপি', 'রবি'),
                  ('ফুলকপি', 'রবি'), ('বোরো', 'রবি'), ('মসুর', 'রবি'), ('খেসারি', 'রবি'),
                  ('তোষা', 'খরিফ-১')]
    for a in aezs:
        for c in a['crops']:
            if c['name'] in ('পতিত',):
                continue
            for k, want in FIX_SEASON:
                if k in c['name']:
                    got = c['season'].replace('?', '')
                    if got != want and got != '—':
                        c['season'] = want
                        c['season_corrected'] = True
                    elif got == '—':
                        c['season'] = want
                        c['season_corrected'] = True
                    break

    # strip helper keys, fix seasons
    for a in aezs:
        for c in a['crops']:
            c.pop('y', None)
            if c['season'].endswith('?'):
                c['season'] = c['season'][:-1]
                c['season_inherited'] = True
    with open(OUT, 'w', encoding='utf-8') as f:
        json.dump(aezs, f, ensure_ascii=False, indent=1)
    print('saved', OUT)

if __name__ == '__main__':
    main()
