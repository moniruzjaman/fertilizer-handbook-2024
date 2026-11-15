// Headless smoke test: run the PWA inline JS inside a vm sandbox with DOM stubs.
import { readFileSync, existsSync } from 'node:fs';
import vm from 'node:vm';
import { fileURLToPath } from 'node:url';
import path from 'node:path';

const ROOT = path.resolve(path.dirname(fileURLToPath(import.meta.url)), '..');
const html = readFileSync(path.join(ROOT, 'pwa/index.html'), 'utf-8');
const js = html.match(/<script>([\s\S]*)<\/script>/)[1];

function makeEl(id) {
  return {
    id, innerText: '', innerHTML: '', value: '', style: {},
    options: [{ value: '1' }],
    classList: { add() {}, remove() {} },
    setAttribute() {}, addEventListener() {},
  };
}
const els = {};
const documentStub = {
  getElementById(id) { if (!els[id]) els[id] = makeEl(id); return els[id]; },
  querySelectorAll() { return []; },
  addEventListener() {},
  createElement() { return { setAttribute() {}, click() {}, remove() {}, style: {} }; },
  body: { appendChild() {}, removeChild() {} },
};
els['land-area'] = makeEl('land-area'); els['land-area'].value = '33';
els['land-unit'] = makeEl('land-unit'); els['land-unit'].value = 'decimal';

const sandbox = {
  document: documentStub,
  localStorage: { getItem: () => null, setItem: () => {} },
  navigator: {},
  location: { hash: '' },
  history: { pushState() {}, replaceState() {}, back() {}, state: null },
  Image: class { set src(v) {} },
  alert: () => {},
  Blob: class {},
  URL: { createObjectURL: () => '', revokeObjectURL: () => {} },
  console, Date, JSON, Math, parseInt, parseFloat, String, Number, RegExp, Array, Object,
};
sandbox.window = { addEventListener: () => {} };
els['vh-stage'] = Object.assign(makeEl('vh-stage'), { scrollTo() {} });
sandbox.globalThis = sandbox;
vm.createContext(sandbox);

vm.runInContext(js + '\n;this.__api = {FRG_DATA, aezByNo, bnNum, HB_SLIDES, HB_SECTIONS};', sandbox, { timeout: 20000 });
const { FRG_DATA, aezByNo, bnNum, HB_SLIDES, HB_SECTIONS } = sandbox.__api;

const assert = (cond, msg) => { if (!cond) { console.error('FAIL:', msg); process.exitCode = 1; } else console.log('ok:', msg); };

assert(FRG_DATA.aezs.length === 29, '29 AEZs embedded (AEZ-6 absent from source scan)');
const a4 = aezByNo(4);
assert(a4 && a4.crops.length > 40, 'AEZ-4 has ' + a4.crops.length + ' crop rows');
const alu = a4.crops.find(c => c.n === 'আলু');
assert(alu && alu.y === 120 && alu.u === 1186 && alu.t === 607 && alu.m === 729 && alu.mg === 213 && alu.z === 58 && alu.b === 36 && alu.o === 12,
  'AEZ-4 আলু dose row matches handbook (120±12, 1186/607/729/340/213/58/36/12)');
const akh = FRG_DATA.aezs.map(a => a.crops.find(c => c.n === 'আখ + মসুর')).find(Boolean);
assert(akh && akh.sec && akh.sec.n === 'মসুর', 'আখ + মসুর intercrop with secondary yield present');
assert(bnNum('120') === '১২০', 'Bengali numeral conversion');
assert(a4.districts && a4.districts.length >= 2, 'AEZ-4 districts: ' + (a4.districts || []).join(', '));
// Visual Handbook deck
assert(HB_SLIDES.length === 42, 'visual handbook indexes 42 slides');
assert(HB_SLIDES.every(sl => sl[0] >= 0 && sl[0] < HB_SECTIONS.length && sl[1] && sl[2]), 'every slide has a valid section + bn/en titles');
assert(HB_SLIDES.every((_, i) => existsSync(path.join(ROOT, 'pwa/slides', String(i + 1).padStart(2, '0') + '.webp')) && existsSync(path.join(ROOT, 'pwa/slides/t', String(i + 1).padStart(2, '0') + '.webp'))), 'all 42 slide + thumbnail files exist');
console.log('calculator ইউরিয়া output (AEZ-1 first row, 33 শতাংশ):', els['res-urea'].innerText);
console.log('calculator জৈবসার output:', els['res-organic'].innerText);
console.log('dose table rows rendered:', els['dose-table-wrap'].innerHTML.includes('মৌসুম') ? 'yes' : 'NO');
console.log('SMOKE TEST DONE');
