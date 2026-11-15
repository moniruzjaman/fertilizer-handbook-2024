// Simple static build: copy the PWA into ./dist for GitHub Pages deployment.
import { cpSync, rmSync, mkdirSync, readFileSync, writeFileSync, existsSync } from 'node:fs';

rmSync('dist', { recursive: true, force: true });
mkdirSync('dist', { recursive: true });
cpSync('pwa', 'dist', { recursive: true });

// Basic validation of the built output
const html = readFileSync('dist/index.html', 'utf-8');
if (!html.includes('resp-title')) throw new Error('Build check failed: respiratory information section missing');
if (!/<div class="card">/.test(html)) throw new Error('Build check failed: no cards found');
if (!html.includes('const FRG_DATA')) throw new Error('Build check failed: FRG handbook data missing');
if (!html.includes('page-book') || !html.includes('page-calc')) throw new Error('Build check failed: handbook/calc tabs missing');

// Visual Handbook (slide deck): viewer wired in + 42 slides, 42 thumbnails and the PDF present
if (!html.includes('id="vh-viewer"') || !html.includes('page-slides')) throw new Error('Build check failed: visual handbook tab/viewer missing');
if (!html.includes('HB_SLIDES')) throw new Error('Build check failed: slide index missing');
const HB_COUNT = 42;
for (let i = 1; i <= HB_COUNT; i++) {
  const n = String(i).padStart(2, '0');
  for (const p of [`dist/slides/${n}.webp`, `dist/slides/t/${n}.webp`]) {
    if (!existsSync(p)) throw new Error('Build check failed: missing ' + p);
  }
}
if (!existsSync('dist/slides/FRG-2024-Visual-Handbook.pdf')) throw new Error('Build check failed: handbook PDF missing');
writeFileSync('dist/.nojekyll', '');
console.log('Build OK: dist/ ready for GitHub Pages (' + (html.length) + ' bytes index.html)');
