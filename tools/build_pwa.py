#!/usr/bin/env python3
"""Build the final pwa/index.html: inject FRG data into the template,
bump sw.js cache version, update manifest description."""
import json, re, os

REPO = '/home/z/my-project/fertilizer-handbook-2024'
TPL = '/home/z/my-project/scripts/pwa_template.html'
DATA = '/home/z/my-project/scripts/frg_app_data.json'

html = open(TPL, encoding='utf-8').read()
data = open(DATA, encoding='utf-8').read()
assert '__FRG_DATA__' in html
html = html.replace('__FRG_DATA__', data)
assert '__FRG_DATA__' not in html

# sanity checks on final html
for needle in ['resp-title', 'class="card"', 'const FRG_DATA', 'tab-btn-book', 'crop-select']:
    assert needle in html, needle

open(os.path.join(REPO, 'pwa/index.html'), 'w', encoding='utf-8').write(html)
print('pwa/index.html written:', os.path.getsize(os.path.join(REPO, 'pwa/index.html')), 'bytes')

# bump service worker cache
sw_path = os.path.join(REPO, 'pwa/sw.js')
sw = open(sw_path, encoding='utf-8').read()
sw2 = re.sub(r"const CACHE_NAME = '[^']+';", "const CACHE_NAME = 'frg-2024-pwa-v4';", sw)
if sw2 == sw and "CACHE_NAME = 'frg-2024-pwa-v4'" in sw:
    print('sw.js already at frg-2024-pwa-v4')
open(sw_path, 'w', encoding='utf-8').write(sw2)
open(sw_path, 'w', encoding='utf-8').write(sw2)
print('sw.js cache bumped to frg-2024-pwa-v4')

# manifest description
mf_path = os.path.join(REPO, 'pwa/manifest.webmanifest')
mf = json.load(open(mf_path, encoding='utf-8'))
mf['description'] = ('BARC সার সুপারিশমালা হাতবই-২০২৪: AEZ ভিত্তিক ফসলধারাভিত্তিক সারের মাত্রা '
                     'ব্রাউজার, জেলা ভিত্তিক AEZ খোঁজ ও অফলাইন সার ক্যালকুলেটর')
json.dump(mf, open(mf_path, 'w', encoding='utf-8'), ensure_ascii=False, indent=2)
print('manifest description updated')
