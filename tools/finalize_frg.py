#!/usr/bin/env python3
"""Finalize parsed AEZ data: drop unusable rows, rename empty-name rows by dose
signature, inherit missing seasons, validate, emit compact app data JSON."""
import json, sys

SRC = '/home/z/my-project/scripts/frg_aez_data.json'
OUT = '/home/z/my-project/scripts/frg_final_data.json'
CORE = ['urea', 'tsp', 'mop', 'gypsum', 'mgso4', 'zinc', 'boric']

d = json.load(open(SRC, encoding='utf-8'))

# 1) drop rows without usable doses (junk header merges / AEZ-30 note block)
dropped = 0
for a in d:
    keep = []
    for c in a['crops']:
        if any(c[k] is None for k in CORE) or (c['urea'] is not None and c['urea'] < 10 and c['name'] != 'পতিত'):
            dropped += 1
            continue
        keep.append(c)
    a['crops'] = keep

# 2) rename empty-name rows by dose signature (exact match on 7 core + organic)
sig_map = {}
for a in d:
    for c in a['crops']:
        if c['name'].strip() and c['name'] != 'পতিত':
            key = tuple(c[k] if c[k] is not None else -1 for k in CORE) + (c['organic'] if c['organic'] is not None else -1,)
            sig_map.setdefault(key, c['name'])
renamed = 0
for a in d:
    for c in a['crops']:
        if not c['name'].strip():
            key = tuple(c[k] if c[k] is not None else -1 for k in CORE) + (c['organic'] if c['organic'] is not None else -1,)
            if key in sig_map:
                c['name'] = sig_map[key]
                c['season_corrected'] = True
                renamed += 1

# 3) drop still-unnamed rows
for a in d:
    before = len(a['crops'])
    a['crops'] = [c for c in a['crops'] if c['name'].strip()]
    dropped += before - len(a['crops'])

# 4) inherit missing seasons from nearest neighbor with a real season
for a in d:
    crops = a['crops']
    for i, c in enumerate(crops):
        if c['season'] in ('—', None) and c['name'] != 'পতিত':
            for dist in range(1, len(crops)):
                for j in (i - dist, i + dist):
                    if 0 <= j < len(crops) and crops[j]['season'] not in ('—', None):
                        c['season'] = crops[j]['season']
                        c['season_inherited'] = True
                        break
                if c['season'] not in ('—', None):
                    break

# 5) season sanity counts + report
n = sum(len(a['crops']) for a in d)
seas = {}
for a in d:
    for c in a['crops']:
        seas[c['season']] = seas.get(c['season'], 0) + 1
print('rows:', n, '| dropped:', dropped, '| renamed:', renamed)
print('seasons:', seas)
bad = [(a['no'], c['name'], c[k]) for a in d for c in a['crops'] for k in CORE if c[k] is not None and c[k] > 3000]
print('dose >3000 g/dec rows:', bad)
unnamed = sum(1 for a in d for c in a['crops'] if not c['name'].strip())
dash = sum(1 for a in d for c in a['crops'] if c['season'] in ('—', None))
print('still unnamed:', unnamed, '| season-dash:', dash)

# 6) compact output: group crops by (season, name) per AEZ, keep all dose fields
out = []
for a in d:
    out.append({
        'no': a['no'], 'name': a['name'], 'area': a['area_acres'],
        'soils': a['soils'],
        'crops': [{k: c[k] for k in ('season', 'name', 'yield', 'yield_pm', 'urea', 'tsp', 'mop',
                                     'gypsum', 'mgso4', 'zinc', 'boric', 'organic')
                   if c.get(k) is not None} | ({'sec': c['secondary']} if c.get('secondary') else {})
                  for c in a['crops']]
    })
json.dump(out, open(OUT, 'w', encoding='utf-8'), ensure_ascii=False, separators=(',', ':'))
import os
print('saved', OUT, os.path.getsize(OUT), 'bytes')
