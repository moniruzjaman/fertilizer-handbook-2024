# FRG 2024: Fertilizer Guide (PWA)

[![CI — build & validate](https://github.com/moniruzjaman/fertilizer-handbook-2024/actions/workflows/ci.yml/badge.svg?branch=main)](https://github.com/moniruzjaman/fertilizer-handbook-2024/actions/workflows/ci.yml)
[![Deploy PWA to gh-pages branch](https://github.com/moniruzjaman/fertilizer-handbook-2024/actions/workflows/gh-pages-branch-deploy.yml/badge.svg?branch=main)](https://github.com/moniruzjaman/fertilizer-handbook-2024/actions/workflows/gh-pages-branch-deploy.yml)
[![GitHub Pages deployment](https://github.com/moniruzjaman/fertilizer-handbook-2024/actions/workflows/pages/pages-build-deployment/badge.svg?branch=gh-pages)](https://moniruzjaman.github.io/fertilizer-handbook-2024/)

An offline-first Progressive Web App companion for the **BARC সার সুপারিশমালা হাতবই-2024 (Fertilizer Recommendation Handbook 2024 / FRG-2024)** for Bangladesh crops. It is a *practical handbook*: it carries the guide's **AEZ-wise, cropping-pattern-based approved fertilizer doses** (কৃষি পরিবেশ অঞ্চল ও ফসলধারা ভিত্তিক অনুমোদিত মাত্রা), a district-based AEZ finder, a dose calculator, and field-reference tables — all in Bengali, fully offline.

**Live app:** <https://moniruzjaman.github.io/fertilizer-handbook-2024/>

## Features

- **হাতবই ব্রাউজ (Handbook browser)** — every কৃষি পরিবেশ অঞ্চল (AEZ) from the handbook with its area, land classes, fertility levels and districts; the full crop-pattern table (মৌসুম, ফসল, কাঙ্ক্ষিত ফলন ± এবং গ্রাম/শতাংশ-ভিত্তিক মাত্রা) for ইউরিয়া, টিএসপি/ডিএপি, এমওপি, জিপসাম, ম্যাগনেসিয়াম সালফেট, জিংক সালফেট, বোরিক এসিড ও জৈবসার.
- **জেলা ভিত্তিক AEZ খোঁজ** — type your district and get the AEZs that cover it (from the handbook's own district descriptions).
- **ক্যালকুলেটর** — any handbook dose row × your land size (শতক/বিঘা/কাঠা/একর/হেক্টর) → fertilizer kg + estimated subsidized cost. One tap moves a table row into the calculator.
- **তথ্য ও সতর্কতা** — the handbook's fertilizer nutrient-content table (সারণি-১), nutrient-deficiency symptoms, application rules (অধ্যায় ১১ ও ১৪), DAE advisory and air-quality guidance.

### Data provenance

The AEZ dataset was extracted from the official handbook PDF (BARC, ISBN 978-984-35-5737-7, chapter 18) — 29 AEZ tables, ~1,015 crop-pattern rows. AEZ-6 (নিম্ন পুনর্ভবা প্লাবনভূমি) is absent from the circulated PDF scan, so its table is not included. Doses are shown exactly as printed (গ্রাম/শতাংশ; জৈবসার কেজি/শতাংশ); the DAP substitution note (প্রতি কেজি ডিএপির জন্য ৪০০ গ্রাম ইউরিয়া কম) and the zinc-heptahydrate basis follow the handbook footnotes. Verify with your local SAAO/SRDI soil test before applying.

## Project layout

| Path | Purpose |
| --- | --- |
| `pwa/` | The static PWA source (`index.html` with embedded handbook dataset, `manifest.webmanifest`, service worker, icons) |
| `build.mjs` | Build script — copies `pwa/` into `dist/`, validates the output (handbook data + tabs present), writes `.nojekyll` |
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
