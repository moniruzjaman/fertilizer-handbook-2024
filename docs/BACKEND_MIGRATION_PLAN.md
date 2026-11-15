# Backend Migration Plan: Firebase → local-first + Supabase

**Scope:** the Firebase scaffolding in this repo (`firebase.json`, `firebase-blueprint.json`, `firestore.rules`, `firestore.test.js`) and any Firestore sync ambitions for the PWA. Goal: end Firebase usage and land on a backend we can provision as automatically as possible from this repo.

---

## 1. Current state (audited 2026-11)

| Asset | What it does | Still needed? |
| --- | --- | --- |
| `pwa/index.html` (live PWA) | **No Firebase code at all.** Saved calculations live in `localStorage`; everything else is offline data baked into the file. Hosted on GitHub Pages. | ✅ works as-is |
| `firebase-blueprint.json` | Firebase Studio blueprint that auto-created a Firestore project with `UserProfile` + `FertilizerCalculation` entities (for the Android app experiment). | ❌ superseded by this plan |
| `firestore.rules` + `firestore.test.js` | Security rules + emulator tests. CI runs `npm test`, which *skips* unless the Firestore emulator is running. | ❌ replaced by Supabase RLS (phase 2) |
| `firebase.json` | Points at an external Firestore DB (`ai-studio-android-ecotrack-…`) we don't control here. | ❌ phase 2 removal |

**Key finding:** the deployed web app never depended on Firebase. Nothing user-facing breaks if we simply stop maintaining the scaffolding — so migration is about the *future* sync feature, not a rescue.

## 2. What can be auto-created from this sandbox/repo (decision matrix)

| Platform | Auto-create project? | Auto-create schema/RLS? | Free tier fits? | Verdict |
| --- | --- | --- | --- | --- |
| **GitHub Pages (current)** | ✅ already done | n/a (no backend) | ✅ unlimited for static | **Phase 0 target — shipped** |
| **Supabase** | ❌ needs a human to click "New project" once | ✅ SQL + RLS + client + CI auto-apply prepared here (`docs/supabase/`, workflow) | ✅ 500 MB DB, 50k MAU | **Phase 2 target — recommended** |
| Cloudflare Workers + D1/KV | ❌ needs API token | ✅ could automate fully with token | ✅ generous | Good alternative; slightly more code (Worker + JWT handling) |
| PocketBase (single binary on any VPS) | ❌ needs a server | ✅ schema auto-migrates from a JSON file | n/a (server cost) | Best when you want full data ownership |
| Firebase | ❌ (and the old blueprint needs Firebase Studio) | partial | ✅ | being removed |

Honest constraint: no third-party database can be *fully* created without one human-authenticated step (account + token). Supabase gets closest: **everything except the project itself and 2 GitHub secrets is prepared in this repo** — after you create the project and paste the secrets, one CI run applies the schema, RLS policies and indexes automatically.

## 3. Phase 0 — local-first sync (shipped now, zero servers)

Already live in `pwa/index.html`:

- Saved calculations persist in `localStorage` (offline by design, 100% of functionality without any backend).
- **Export backup** — downloads `frg-2024-saved-calculations.json` (schema `{app, schema:1, exportedAt, calculations[]}`).
- **Import backup** — validates, sanitizes, merges (cap 60), works across devices/browsers.
- A visible privacy note: data never leaves the device.

This is the migration *destination* for users of the current PWA: no accounts, no lock-in, exportable data. Rollout = nothing to do; it's already in production.

## 4. Phase 2 — Supabase (recommended cloud target)

### 4.1 One-time human steps (~5 minutes)

1. Create a free project at supabase.com (region: Singapore `ap-southeast-1` is closest to Bangladesh).
2. Save the **project ref** and create an **access token** at supabase.com/dashboard/account/tokens.
3. In this GitHub repo → *Settings → Secrets and variables → Actions*, add:
   - `SUPABASE_PROJECT_REF`
   - `SUPABASE_ACCESS_TOKEN`
4. Run the GitHub action **“Apply Supabase migrations”** (`workflow_dispatch`) once. CI links the project, pushes `docs/supabase/migrations/*.sql` (tables, RLS, indexes), and the DB is production-ready.
5. Copy the anon key into the PWA config (`pwa/index.html` → `SUPABASE.url` / `SUPABASE.anonKey` constants at the top of the sync module) and redeploy.

From then on, any change to `docs/supabase/migrations/*.sql` is applied to the database by CI on push — schema evolution is fully automatic.

### 4.2 What is already prepared

- `docs/supabase/migrations/0001_init.sql` — `profiles` + `calculations` tables, RLS (users can only read/write their own rows), indexes.
- `docs/supabase/client-snippet.js` — drop-in `supabase-js` sync module (auth via OTP magic link; upsert/fetch/delete saved calculations) matching the localStorage schema from Phase 0.
- `.github/workflows/supabase-apply.yml` — workflow_dispatch + push-to-migrations trigger, no-ops safely when secrets are absent (so CI stays green today).

### 4.3 Why Supabase over the alternatives

- Postgres + RLS maps 1:1 to the intent of the old Firestore rules (`request.auth.uid == userId` → `auth.uid() = user_id`).
- Bengali-first users can sign in with OTP (no Google account required on a shared phone).
- Data export stays trivial (`calculations` is plain JSONB; same backup file works).
- Free tier comfortably covers this app's profile (tiny rows, low frequency).

## 5. Cleanup checklist (execute when phase 2 ships, or immediately if you prefer)

- [ ] Delete `firebase.json`, `firebase-blueprint.json`, `firestore.rules`, `firestore.test.js`.
- [ ] Remove `firebase-tools`/emulator bits from `package.json` scripts and the CI rules-test step (replace with the Supabase migration apply step).
- [ ] In the Android app (`app/`), replace the Firestore client with the Supabase REST endpoint or drop sync there too (it was experimental).
- [ ] Delete/deprecate the old Firebase project (Firebase console → project settings → delete) so no stray DB keeps collecting data.
- [ ] Bump `sw.js` CACHE_NAME.

## 6. Rollback plan

Phase 0 is self-contained — reverting the backup UI is a one-file revert. In phase 2, the PWA keeps localStorage as the primary store; Supabase is a mirror. If Supabase is down or unwanted, the app never blocks: sync failures are silent retries, and a user's data is always in their export file.
