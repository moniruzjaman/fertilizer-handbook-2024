// FRG-2024 PWA → Supabase sync module (Phase 2 client snippet)
// Drop into pwa/index.html (script tag) when the Supabase project is live.
// Fill SUPABASE.url / SUPABASE.anonKey from your project settings after creation.

// <script src="https://cdn.jsdelivr.net/npm/@supabase/supabase-js@2"></script>
const SUPABASE = {
  url: 'https://YOUR-PROJECT-REF.supabase.co',   // replace after project creation
  anonKey: 'YOUR-ANON-KEY',                      // replace after project creation
};

function sb() {
  return window.supabase.createClient(SUPABASE.url, SUPABASE.anonKey, {
    auth: { storageKey: 'frg-2024-auth' }
  });
}

// ---- auth: OTP magic link (no Google dependency — works on any phone) ----
async function syncSignIn(email) {
  const c = sb();
  await c.auth.signInWithOtp({ email, options: { shouldCreateUser: true } });
  // user clicks the emailed link; session is stored in localStorage
}

async function syncCurrentUser() {
  const c = sb();
  const { data } = await c.auth.getUser();
  return data.user;
}

// ---- mirror the localStorage backup file up to the cloud ----
// items: same shape as localStorage 'frg_saved_calcs' / the export backup
async function syncPushCalculations(items) {
  const user = await syncCurrentUser();
  if (!user) return { pushed: 0 };
  const rows = items.map(x => ({
    client_ref: String(x.id || ''),
    crop: x.crop,
    season: (x.crop || '').match(/^[^\]]+]/) ? x.crop.split(']')[0].replace('[', '') : null,
    aez_name: x.aez,
    area: parseFloat(x.area) || null,
    unit: (x.area || '').split(' ')[1] || null,
    total_cost: parseFloat(String(x.cost || '').replace(/[^\d.]/g, '')) || null,
    doses: null
  }));
  const c = sb();
  const { error } = await c.from('calculations').upsert(rows, { onConflict: 'user_id,client_ref' });
  if (error) throw error;
  return { pushed: rows.length };
}

async function syncPullCalculations() {
  const user = await syncCurrentUser();
  if (!user) return [];
  const c = sb();
  const { data, error } = await c
    .from('calculations')
    .select('*')
    .order('created_at', { ascending: false })
    .limit(60);
  if (error) throw error;
  return data.map(r => ({
    id: r.client_ref || r.id,
    crop: r.crop,
    aez: r.aez_name,
    area: r.area ? `${r.area} ${r.unit || 'decimal'}` : '',
    cost: r.total_cost != null ? `৳ ${r.total_cost}` : '',
    date: r.created_at
  }));
}

// Wire-up suggestion: after each saveCalculationLocally(), if a session exists,
// call syncPushCalculations(JSON.parse(localStorage.getItem('frg_saved_calcs') || '[]'))
// and show a subtle "☁️ সিঙ্ক হয়েছে" indicator. Failures must stay silent (offline-first).
