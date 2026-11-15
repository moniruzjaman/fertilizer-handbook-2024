# Enhancement plan — after integrating the Visual Handbook

Status: the 42-slide Visual Handbook ships as the **স্লাইড** tab (v1.2.0). This plan lists what to do next and what is needed from the **main handbook PDF** (BARC, FRG-2024) to do it. Items marked ⚠ need the main handbook attached before work can start.

## 0. Verify the slides against the handbook (do first) ⚠
The slides are AI-generated summaries of four decks. They contradict each other in places; the handbook is the authority.

| Topic | Conflict seen | Action |
| --- | --- | --- |
| Urea 3rd split (rice) | one deck says 15–20 days before panicle initiation, others 5–7 days | confirm in handbook ch. 11/14; fix or drop slide |
| Urea splits timing | days after transplanting differ (10–15 / 25–30 / 40–45 vs. stage-based) | confirm per variety duration |
| TSP/DAP solubility tests | one deck says "does not dissolve easily", another says real TSP dissolves clear | confirm; keep only handbook-backed tests |
| Guti urea saving | "30%" in decks | confirm figure and conditions (transplanted rice only?) |
| Doses on slides (boro per শতাংশ, maize, mango) | AEZ-19 examples only | replace with link to the AEZ table rows |
| Dolochun 4–8 kg/শতাংশ | single figure | confirm vs. lime requirement table |

Deliverable: a `docs/slide-verification.csv` (slide no., claim, handbook page, status) and corrected/removed slides. Add a small "source: summary, verify with handbook" footer to the viewer.

## 1. Connect slides ↔ data (quick wins)
- From any AEZ dose row → "related slides" chips (rice → urea splits/guti; potato/maize → slide 30; acid soil → slide 35).
- From a slide → "see doses for this crop" (opens Handbook tab filtered).
- Calculator result → "how to apply" slide for the chosen crop.
- Full-text search across slide titles and handbook rules.

## 2. Close data gaps ⚠
- **AEZ-6** table is missing from the scanned PDF used so far — find it in the main handbook.
- Chapters not yet in the app: soil-test interpretation classes and critical levels, nutrient content of organic manures, lime requirement, micronutrient doses by crop, crop-wise fertilizer for non-AEZ crops, mixing/compatibility rules.
- Add a **checksum pass**: re-parse each AEZ table and compare row counts/totals with the printed pages; keep a per-AEZ provenance page number.

## 3. Soil-test-based recommendation mode ⚠
Enter SRDI soil-test values (pH, OM, N, P, K, S, Zn, B) → interpretation class → handbook dose. Needs the interpretation tables from the main handbook.

## 4. Field workflow
- Split-dose **schedule** from sowing/transplanting date (urea splits, top dressing) with `.ics` export / reminders.
- One-page **advisory card** (PDF/print/WhatsApp image): farmer, land, AEZ, crop, doses, dates, DAE disclaimer.
- Photo-based deficiency checker ➜ link to slides 9–11 (optional, later).

## 5. Quality, access and delivery
- Add the headless smoke test and a Playwright slide-viewer test to CI (`tools/smoke_pwa.mjs` exists).
- Bengali audio narration (TTS) per slide for low-literacy farmers; English deck optional.
- Image budget: slides are ~58 KB each as WebP (2.4 MB total); keep total precache small — only cover slide is precached, rest cached on demand or via *Save offline*.
- Lighthouse PWA/accessibility pass (alt text exists; add captions/contrast checks).

## 6. Android app parity
The Kotlin app keeps its own crop data (`FrgCropsData.kt`, `AezDatabase.kt`), which will drift from the PWA dataset. Plan: generate both from `tools/frg_app_data.json`, then add a Compose pager screen reading the same 42 WebP slides from `assets/`.
