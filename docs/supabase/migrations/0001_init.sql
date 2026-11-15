-- FRG-2024 PWA: initial Supabase schema (replaces firestore.rules)
-- Applied automatically by .github/workflows/supabase-apply.yml
-- Matches the localStorage backup schema: {app:'frg-2024-pwa', schema:1, calculations:[...]}

-- ============ profiles (was UserProfile in firebase-blueprint.json) ============
create table if not exists public.profiles (
  id                uuid primary key references auth.users(id) on delete cascade,
  email             text,
  display_name      text,
  photo_url         text,
  detected_aez      integer check (detected_aez between 1 and 30),
  detected_district text,
  language          text not null default 'bn',
  created_at        timestamptz not null default now(),
  updated_at        timestamptz not null default now()
);

-- ============ calculations (was FertilizerCalculation) ============
create table if not exists public.calculations (
  id          bigint generated always as identity primary key,
  user_id     uuid not null references auth.users(id) on delete cascade,
  client_ref  text,                    -- localStorage id, keeps backups idempotent
  crop        text not null,           -- e.g. 'রবি — আলু'
  season      text,
  aez_no      integer,
  aez_name    text,
  area        numeric,
  unit        text,                    -- decimal | bigha | katha | acre | hectare
  doses       jsonb,                   -- {urea, tsp, mop, gypsum, mgso4, zinc, boric, organic} in kg
  total_cost  numeric,
  created_at  timestamptz not null default now()
);
create unique index if not exists calculations_user_clientref_idx
  on public.calculations (user_id, coalesce(client_ref, ''));
create index if not exists calculations_user_created_idx
  on public.calculations (user_id, created_at desc);

-- ============ Row Level Security (replaces firestore.rules) ============
alter table public.profiles    enable row level security;
alter table public.calculations enable row level security;

-- profiles: users see and edit only their own row
drop policy if exists "profiles_select_own" on public.profiles;
create policy "profiles_select_own" on public.profiles
  for select using (auth.uid() = id);
drop policy if exists "profiles_insert_own" on public.profiles;
create policy "profiles_insert_own" on public.profiles
  for insert with check (auth.uid() = id);
drop policy if exists "profiles_update_own" on public.profiles;
create policy "profiles_update_own" on public.profiles
  for update using (auth.uid() = id) with check (auth.uid() = id);

-- calculations: users see and edit only their own rows
drop policy if exists "calculations_select_own" on public.calculations;
create policy "calculations_select_own" on public.calculations
  for select using (auth.uid() = user_id);
drop policy if exists "calculations_insert_own" on public.calculations;
create policy "calculations_insert_own" on public.calculations
  for insert with check (auth.uid() = user_id);
drop policy if exists "calculations_update_own" on public.calculations;
create policy "calculations_update_own" on public.calculations
  for update using (auth.uid() = user_id) with check (auth.uid() = user_id);
drop policy if exists "calculations_delete_own" on public.calculations;
create policy "calculations_delete_own" on public.calculations
  for delete using (auth.uid() = user_id);
