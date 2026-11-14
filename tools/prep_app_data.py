#!/usr/bin/env python3
"""Prepare final app data: normalize names, add English names, extract districts
per AEZ from soil description lines, emit compact frg_app_data.json."""
import json, re, unicodedata
def nfc(s):
    return unicodedata.normalize('NFC', s)

SRC = '/home/z/my-project/scripts/frg_final_data.json'
OUT = '/home/z/my-project/scripts/frg_app_data.json'

NAME_FIX = {
    'পঁুইশাক': 'পুঁইশাক', 'বঁাধাকপি': 'বাঁধাকপি', 'মৌসুম': 'মৌসুম',
    'বছর আখ + ব্যাপী মসুর': 'আখ + মসুর',
    'ফেলন': 'খেসারি', 'ওবি রো. আউশ': 'রো. আউশ', 'পেঁয াজ': 'পেঁয়াজ',
    'ফু ল কপি': 'ফুলকপি', 'রব বোরো': 'বোরো', 'রব মাসকালাই': 'মাসকালাই',
    'সূর্ষমুখী': 'সূর্যমুখী',
}

EN = {
    'বোরো': 'Boro Rice (HYV)', 'রো. আমন': 'Transplanted Aman Rice', 'রো. আউশ': 'Transplanted Aus Rice',
    'বোনা আমন': 'Broadcast Aman Rice', 'পাট': 'Jute', 'তোষা পাট': 'Tossa Jute', 'গম': 'Wheat',
    'ভুট্টা': 'Maize', 'আলু': 'Potato', 'সরিষা': 'Mustard', 'মসুর': 'Lentil', 'খেসারি': 'Grass Pea',
    'মুগকালাই': 'Mungbean', 'মাসকালাই': 'Blackgram', 'বাদাম': 'Groundnut',
    'পেঁয়াজ': 'Onion', 'রসুন': 'Garlic', 'মুলা': 'Radish', 'বাঁধাকপি': 'Cabbage',
    'ফুলকপি': 'Cauliflower', 'টমেটো': 'Tomato', 'বেগুন': 'Brinjal', 'ঢেঁড়শ': 'Okra',
    'চালকুমড়া': 'Pumpkin', 'লাউ': 'Bottle Gourd', 'করলা': 'Bitter Gourd', 'পটল': 'Pointed Gourd',
    'শসা': 'Cucumber', 'পুঁইশাক': 'Malabar Spinach', 'ডাটাশাক': 'Stem Amaranth', 'মরিচ': 'Chili',
    'হলুদ': 'Turmeric', 'আদা': 'Ginger', 'আখ': 'Sugarcane', 'আখ + মসুর': 'Sugarcane + Lentil (intercrop)',
    'তিল': 'Sesame', 'মিষ্টি আলু': 'Sweet Potato', 'শালগম': 'Turnip', 'পালংশাক': 'Spinach',
    'গাজর': 'Carrot', 'বীট': 'Beet', 'ধনেপাতা': 'Coriander Leaf', 'ব্রকলি': 'Broccoli',
    'মিষ্টি কুমড়া': 'Sweet Gourd', 'সূর্যমুখী': 'Sunflower', 'আনারস': 'Pineapple',
    'তরমুজ': 'Watermelon', 'সয়াবিন': 'Soybean', 'ছোলা': 'Chickpea', 'বরবটি': 'French Bean', 'গাঁদা': 'Marigold', 'ড্রাগন': 'Dragon Fruit',
    'স্ট্রবেরি': 'Strawberry', 'পেঁপে': 'Papaya', 'আম': 'Mango', 'কলা': 'Banana', 'মালটা': 'Clementine',
}

DISTRICTS = ['ঠাকুরগাঁও', 'পঞ্চগড়', 'দিনাজপুর', 'বগুড়া', 'সিরাজগঞ্জ', 'পাবনা', 'নাটোর', 'রাজশাহী',
    'চাঁপাইনবাবগঞ্জ', 'জয়পুরহাট', 'নওগাঁ', 'রংপুর', 'নীলফামারী', 'লালমনিরহাট', 'কুড়িগ্রাম',
    'গাইবান্ধা', 'গাজীপুর', 'টাঙ্গাইল', 'মানিকগঞ্জ', 'মুন্সিগঞ্জ', 'নারায়ণগঞ্জ', 'নরসিংদী', 'ঢাকা',
    'কিশোরগঞ্জ', 'ময়মনসিংহ', 'শেরপুর', 'জামালপুর', 'নেত্রকোণা', 'ফরিদপুর', 'গোপালগঞ্জ',
    'মাদারীপুর', 'শরীয়তপুর', 'রাজবাড়ী', 'কুষ্টিয়া', 'ঝিনাইদহ', 'মাগুরা', 'যশোর', 'সাতক্ষীরা',
    'খুলনা', 'বাগেরহাট', 'ঝালকাঠি', 'বরিশাল', 'পটুয়াখালী', 'পিরোজপুর', 'ভোলা', 'বরগুনা',
    'চাঁদপুর', 'কুমিল্লা', 'ব্রাহ্মণবাড়িয়া', 'ফেনী', 'নোয়াখালী', 'লক্ষ্মীপুর', 'চট্টগ্রাম',
    'কক্সবাজার', 'রাঙামাটি', 'খাগড়াছড়ি', 'বান্দরবান', 'সিলেট', 'মৌলভীবাজার', 'হবিগঞ্জ',
    'সুনামগঞ্জ', 'মেহেরপুর']

d = json.load(open(SRC, encoding='utf-8'))
for a in d:
    text = ' '.join(a.get('soils') or [])
    # also scan joined soil text with garble fixes applied downstream
    text_n = nfc(text)
    ds = [x for x in DISTRICTS if nfc(x) in text_n]
    a['districts'] = sorted(set(ds), key=DISTRICTS.index)
    crops = []
    for c in a['crops']:
        raw = nfc(c['name'])
        name = nfc(NAME_FIX.get(raw, raw))
        sec = None
        if c.get('sec'):
            sraw = nfc(c['sec']['name'])
            sname = nfc(NAME_FIX.get(sraw, sraw)).replace('ব্যাপী ', '').strip()
            sec = {'n': sname, 'y': c['sec'].get('yield'), 'yp': c['sec'].get('yield_pm')}
        crops.append({'s': c['season'], 'n': name, 'en': nfc(EN.get(name, '')),
                      'y': c.get('yield'), 'yp': c.get('yield_pm'),
                      'u': c.get('urea'), 't': c.get('tsp'), 'm': c.get('mop'),
                      'g': c.get('gypsum'), 'mg': c.get('mgso4'), 'z': c.get('zinc'),
                      'b': c.get('boric'), 'o': c.get('organic'), 'sec': sec})
    a['crops'] = crops
    a['soils'] = [re.sub(r'\s+', ' ', s).strip() for s in a['soils']]

out = {'aezs': d}
json.dump(out, open(OUT, 'w', encoding='utf-8'), ensure_ascii=False, separators=(',', ':'))
import os
print('saved', OUT, os.path.getsize(OUT), 'bytes')
# quick stats
nn = len({c['n'] for a in d for c in a['crops']})
withd = sum(1 for a in d if a['districts'])
print('distinct crops:', nn, '| aezs with districts:', withd, '/', len(d))
