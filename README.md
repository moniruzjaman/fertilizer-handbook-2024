# FRG 2024: Fertilizer Guide (PWA)

[![CI — build & validate](https://github.com/moniruzjaman/fertilizer-handbook-2024/actions/workflows/ci.yml/badge.svg?branch=main)](https://github.com/moniruzjaman/fertilizer-handbook-2024/actions/workflows/ci.yml)
[![Deploy PWA to gh-pages branch](https://github.com/moniruzjaman/fertilizer-handbook-2024/actions/workflows/gh-pages-branch-deploy.yml/badge.svg?branch=main)](https://github.com/moniruzjaman/fertilizer-handbook-2024/actions/workflows/gh-pages-branch-deploy.yml)

An offline-first Progressive Web App companion for the **BARC Fertilizer Recommendation Guide 2024 (FRG-2024)** for Bangladesh crops. It calculates exact Urea, TSP/DAP, MoP, Gypsum, Zinc and Boron requirements by land size and soil fertility class, includes respiratory & air-quality safety guidance, and ships a Bengali (`bn`) UI.

**Live app:** <https://moniruzjaman.github.io/fertilizer-handbook-2024/>

## Project layout

| Path | Purpose |
| --- | --- |
| `pwa/` | The static PWA source (`index.html`, `manifest.webmanifest`, service worker, icons) |
| `build.mjs` | Build script — copies `pwa/` into `dist/`, validates the output, writes `.nojekyll` |
| `firestore.rules` | Security rules for the optional Firestore sync (user profiles + saved calculations) |
| `firestore.test.js` | Rules unit tests (require the Firestore emulator) |
| `.github/workflows/ci.yml` | CI: installs, builds, runs the rules tests and smoke-checks `dist/` on every push/PR |
| `.github/workflows/gh-pages-branch-deploy.yml` | CD: builds `dist/` on every push to `main` and publishes it to the `gh-pages` branch, which GitHub Pages serves |

## Getting started

```bash
npm install
npm run dev     # serve pwa/ locally on http://localhost:8080
npm run build   # produce dist/ ready for GitHub Pages
npm test        # Firestore rules tests (skipped unless the emulator is running)
```

The rules tests expect the Firestore emulator on `127.0.0.1:8085`:

```bash
firebase emulators:start --only firestore
npm test
```

## Deployment

Every push to `main` triggers the deploy workflow:

1. `npm ci` installs dependencies.
2. `npm run build` copies `pwa/` to `dist/` and validates it.
3. [`peaceiris/actions-gh-pages`](https://github.com/peaceiris/actions-gh-pages) publishes `dist/` to the `gh-pages` branch.
4. GitHub Pages (configured with **Deploy from a branch: `gh-pages` / root**) serves the updated site, typically within a minute.

No secrets are required — the workflow authenticates with the built-in `GITHUB_TOKEN`.
