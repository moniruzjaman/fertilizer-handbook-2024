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
writeFileSync('dist/.nojekyll', '');
console.log('Build OK: dist/ ready for GitHub Pages (' + (html.length) + ' bytes index.html)');
