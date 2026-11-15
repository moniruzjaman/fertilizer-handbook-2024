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
- **স্লাইড (Visual Handbook)** — a 42-slide illustrated guide merged from four BARC-based decks (soil health, nutrients, deficiency symptoms, organic/bio-fertilizer, dose-setting, urea splits & guti urea, crop-wise methods, liming, safety, adulteration tests). Swipe/arrow/keyboard navigation, section jump, zoom, deep links (`#slide-12`), one-tap *Save offline*, and a downloadable PDF. Slides are explanatory summaries — **the AEZ dose tables in the Handbook tab are the authoritative doses.**
- **তথ্য ও সতর্কতা** — the handbook's fertilizer nutrient-content table (সারণি-১), nutrient-deficiency symptoms, application rules (অধ্যায় ১১ ও ১৪), DAE advisory and air-quality guidance.

### Visual Handbook notes

Slide order, titles (Bengali/English) and section grouping live in `HB_SECTIONS` / `HB_SLIDES` in `tools/pwa_template.html`; slide *N* is `pwa/slides/NN.webp`. To add or reorder slides, drop the image in, edit that list, run `python3 tools/build_pwa.py`, then `npm run build`. `build.mjs` fails the build if any slide, thumbnail or the PDF is missing. Source decks were AI-generated summaries of the handbook; see `docs/ENHANCEMENT_PLAN.md` for the verification plan and known discrepancies between decks.

### Data provenance

The AEZ dataset was extracted from the official handbook PDF (BARC, ISBN 978-984-35-5737-7, chapter 18) — 29 AEZ tables, ~1,015 crop-pattern rows. AEZ-6 (নিম্ন পুনর্ভবা প্লাবনভূমি) is absent from the circulated PDF scan, so its table is not included. Doses are shown exactly as printed (গ্রাম/শতাংশ; জৈবসার কেজি/শতাংশ); the DAP substitution note (প্রতি কেজি ডিএপির জন্য ৪০০ গ্রাম ইউরিয়া কম) and the zinc-heptahydrate basis follow the handbook footnotes. Verify with your local SAAO/SRDI soil test before applying.

## Social preview (official handbook branding)

Link previews and share assets are generated from the official handbook design (BARC green `#108449` → deep green gradient, golden wave, cream band — as on the হাতবই cover). They live in `pwa/og/` and ship on Pages; `og:`/`twitter:` meta tags in `index.html` wire them up:

| File | Use |
| --- | --- |
| `og-1200x630.png` | Web embeds, Facebook, LinkedIn, Telegram, WhatsApp |
| `twitter-1200x675.png` | X/Twitter large-summary card |
| `youtube-1280x720.png` | YouTube thumbnail |
| `instagram-1080x1080.png` | Instagram/Facebook square post |
| `story-1080x1920.png` | Instagram/Facebook/WhatsApp story |
| `github-1280x640.png` | Repository social preview (uploaded via GitHub API) |

Regenerate with the HTML source in `tools/social_previews.html` (headless Chrome screenshot → crop).

## Backend migration (Firebase → local-first + Supabase)

The live PWA runs **without any backend** — saved calculations stay on-device with **export/import backup** buttons (Phase 0, shipped). The full plan for the optional cloud sync lives in [`docs/BACKEND_MIGRATION_PLAN.md`](docs/BACKEND_MIGRATION_PLAN.md): a platform decision matrix, the recommended Supabase target with **auto-applied schema + RLS** (`docs/supabase/`, `.github/workflows/supabase-apply.yml` — no-op until 2 secrets are added), a 5-minute setup checklist, and the Firebase cleanup list. The old Firestore rules/blueprint remain only as legacy scaffolding until that cleanup runs.

## Project layout

| Path | Purpose |
| --- | --- |
| `pwa/` | The static PWA source (`index.html` with embedded handbook dataset, `manifest.webmanifest`, service worker, icons) |
| `pwa/slides/` | Visual Handbook assets: `NN.webp` (42 slides), `t/NN.webp` (thumbnails), `FRG-2024-Visual-Handbook.pdf` |
| `tools/build_pwa.py` | Regenerates `pwa/index.html` from `tools/pwa_template.html` + `tools/frg_app_data.json` (repo-relative; run after editing the template) |
| `tools/smoke_pwa.mjs` | Headless smoke test of the embedded data, calculator and slide index (`node tools/smoke_pwa.mjs`) |
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
