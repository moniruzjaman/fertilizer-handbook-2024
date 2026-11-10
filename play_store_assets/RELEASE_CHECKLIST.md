# Google Play Store Release & Submission Checklist
## App: FRG 2024: Fertilizer Guide (`com.aistudio.frgguide.m2024`)

This document provides a step-by-step guide to publishing the app to the Google Play Store.

---

### Step 1: Pre-Generated Play Store Assets (Ready in `play_store_assets/`)
The following compliant files and assets have been pre-generated for you:

1. **High-Res App Icon**:
   - File: `play_store_assets/play_store_512.png`
   - Spec: 512 × 512 px, 32-bit PNG (with alpha).
   - Upload in: **Play Console > Store presence > Main store listing > App icon**.
2. **Feature Graphic**:
   - File: `play_store_assets/feature_graphic.png`
   - Spec: 1024 × 500 px, 24-bit PNG.
   - Includes official branding, AEZ, and DAE advisory badge.
   - Upload in: **Play Console > Store presence > Main store listing > Feature graphic**.
3. **Store Listing Metadata**:
   - File: `play_store_assets/PLAY_STORE_LISTING.md`
   - App Title (27 chars): `FRG 2024: Fertilizer Guide` (Under the 30-character limit, no emojis or buzzwords).
   - Short Description (74 chars): `BARC FRG-2024 fertilizer calculator with AEZ, GPS mapping & DAE advisory.`
   - Full Description: Ready to copy-paste into Google Play Console.
4. **Privacy Policy**:
   - File: `play_store_assets/PRIVACY_POLICY.md` and `play_store_assets/privacy_policy.html`
   - Upload `privacy_policy.html` to GitHub Pages (`https://moniruzjaman.github.io/privacy-policy.html`) or your domain.
   - Enter the live HTTPS URL into **Play Console > Policy > App content > Privacy policy**.
5. **Data Safety Questionnaire**:
   - File: `play_store_assets/DATA_SAFETY_DECLARATION.md`
   - Step-by-step answers for Location (`ACCESS_FINE_LOCATION`, `ACCESS_COARSE_LOCATION`) and User Profile (Google Auth) declarations.

---

### Step 2: In-App DAE & BARC Disclaimers (Implemented & Active)
- **First-Launch Modal**: When a user launches the app for the first time, a mandatory dialog presents the DAE consultation requirement (`"⚠️ জমিতে রাসায়নিক বা জৈব সার প্রয়োগের পূর্বে আপনার ব্লকের উপসহকারী কৃষি কর্মকর্তা (SAAO) বা উপজেলা কৃষি অফিসের (DAE) সাথে সরাসরি পরামর্শ করা বাধ্যতামূলক"`). The user must acknowledge to proceed.
- **Top Disclaimer Banner**: Prominent banner on the main calculator screen opening the full statutory disclaimer.
- **Result Hero Card**: Permanent DAE advisory notice directly under the estimated fertilizer cost and bag requirements.
- **Exported Reports**: Included in all generated `.TXT` and `.CSV` spreadsheet downloads.

---

### Step 3: Generating the Production Android App Bundle (.aab)
To build the release App Bundle for Google Play:
1. In Android Studio, go to **Build > Generate Signed Bundle / APK...**
2. Choose **Android App Bundle (.aab)**.
3. Select your release keystore.
4. Select destination folder and choose build variant: **release**.
5. Alternatively, run:
   ```bash
   gradle :app:bundleRelease
   ```
   The generated `.aab` file will be located at:
   `app/build/outputs/bundle/release/app-release.aab`

---

### Step 4: ProGuard & R8 Optimization
The ProGuard rules in `app/proguard-rules.pro` are pre-configured to keep:
- Data models (`com.example.model.**`)
- Firebase Firestore and Authentication
- Jetpack Credential Manager and Google ID
- Network models and Coroutines
- Line numbers for stack traces in Google Play Console Android Vitals.

---

### Step 5: Final Submission
1. In Play Console, create a new release in **Production** (or **Closed testing** first).
2. Upload `app-release.aab`.
3. Complete Content Rating (`Everyone`), Target Audience (`18 and over`), and Data Safety.
4. Review release and roll out!
