# Google Play Store Listing Specification
## FRG 2024: Fertilizer Guide

---

### 1. App Title & Basic Details
- **App Title (Max 30 characters)**:
  `FRG 2024: Fertilizer Guide` *(27 characters — compliant with Google Play title length and policy restrictions)*
- **Package Name (Application ID)**:
  `com.aistudio.frgguide.m2024`
- **Category**:
  `Productivity` / `Tools` (Secondary: `Weather & Agriculture`)
- **Default Language**:
  `English (United States) - en-US` *(with full bilingual Bangla `bn-BD` localization inside app and listing)*
- **Content Rating**:
  `Everyone (PEGI 3, USK 0)`
- **Contact Email**:
  `mithun.hstu@gmail.com`
- **Developer Website / Portfolio**:
  `https://moniruzjaman.github.io/portfolio/`

---

### 2. Short Description (Max 80 characters)
`BARC FRG-2024 fertilizer calculator with AEZ, GPS mapping & DAE advisory.`
*(74 characters — within the strict 80-character Google Play limit)*

**বাংলা সংক্ষিপ্ত বিবরণ (Short Description - Bengali)**:
`বার্ক এফআরজি-২০২৪ সার নির্দেশিকা, এইজেড, প্লট ম্যাপিং ও ডিএই পরামর্শক।`

---

### 3. Full Description (Max 4,000 characters)

```text
FRG 2024: Fertilizer Guide is a scientific agronomy and crop nutrition application designed for farmers, agricultural extension officers (SAAO), students, and agronomists across Bangladesh. Powered by the official Fertilizer Recommendation Guide 2024 (FRG-2024) published by the Bangladesh Agricultural Research Council (BARC), this application calculates exact crop nutrient requirements based on regional Agro-Ecological Zones (AEZ), soil fertility status, and real-time GPS field mapping.

🏛️ MANDATORY DAE ADVISORY & STATUTORY DISCLAIMER:
=====================================================
1. SCIENTIFIC ESTIMATION: The fertilizer dosages computed by this app are baseline scientific estimates generated according to BARC FRG-2024 regional models and general AEZ soil characteristics.
2. LOCAL DAE CONSULTATION REQUIRED: Soil characteristics, pH, moisture, and salinity vary significantly across individual plots even within the same Union or Upazila. Prior to purchasing or applying bulk chemical fertilizers, ALWAYS consult your local Sub-Assistant Agriculture Officer (SAAO) at the Union level or your Upazila Agriculture Officer (Department of Agricultural Extension - DAE).
3. SOIL TESTING: Users are strongly encouraged to test their plot soil at the nearest Soil Resource Development Institute (SRDI) stationary or mobile laboratory for farm-specific precision.
4. LIABILITY LIMITATION: Neither BARC, DAE, nor the developers assume legal or financial liability for crop yield variances or agrochemical misuse.

🌾 KEY FEATURES:

1. BARC FRG-2024 FERTILIZER ENGINE:
• Comprehensive database covering major Bangladesh crops: Boro Rice (HYV & Hybrid), Aman (Transplanted & Broadcast), Aus, Wheat, Maize, Potato, Mustard, Lentil, Chickpea, Sugarcane, Jute, Onion, Garlic, Tomato, Cabbage, and more.
• Computes exact kilograms for Urea, TSP, DAP, MoP (Muriate of Potash), Gypsum, Zinc Sulphate (Heptahydrate/Monohydrate), and Boric Acid / Solubor.
• DAP Substitution Mode: Automatically recalibrates nitrogen balance when using DAP instead of TSP.
• Integrated Nutrient Management (INM): Credits decomposed cowdung/organic manure (tonnes/ha) to reduce chemical fertilizer expenditure while preserving soil health.

2. REAL-TIME AEZ & SOIL PROFILE LOOKUP:
• Instant GPS detection of all 30 Agro-Ecological Zones (AEZ 1 to AEZ 30).
• Displays dominant land types (Highland, Medium Highland, Medium Lowland, Lowland).
• Detailed soil physical profiles: General soil type, texture (Silt Loam, Clay, Sandy Loam), drainage status, and salinity risk.

3. GPS PLOT MAPPING & GEOFENCING:
• Walk your field perimeter to record boundary corners via GPS or set custom circular plot radiuses.
• Real-time Geofence status: Informs you whether you are physically inside the plot boundary, nearby, or outside.
• Auto Area Calculation in standard Bangladeshi land units: Decimal / Shatak (শতক), Bigha (বিঘা), Katha (কাঠা), Acre (একর), and Hectare (হেক্টর).
• Reverse Geocoded Address: Displays Village, Union, Upazila/Thana, District, and Division.
• One-Tap Calculation: Apply geofenced plot area directly to the FRG-2024 calculator.

4. REAL-TIME AGRI-WEATHER INDICES:
• Live weather diagnostics via Open-Meteo for your coordinates: Temperature, Humidity, Wind speed, and Rain forecast.
• Actionable indices: Chemical spraying suitability (Optimal/Caution/Unfavorable), Reference Evapotranspiration (ET0), Heat Stress Risk, and Top-Dressing Rain Advisories.

5. SPLIT-DOSE TIMELINES & DEFICIENCY DIAGNOSIS:
• Detailed stage-by-stage fertilizer application schedules (Basal, Early Tillering, Panicle Initiation, Flowering).
• High-resolution visual symptoms guide for N, P, K, S, Zn, and B deficiencies with corrective measures.

6. NOTEBOOKLM RESOURCE HUB & BUILDER PROFILE:
• Interactive slide decks, fertilizer purity field tests, and audio overviews.
• Links to the official BARC publication portal and Soil & Fertilizer Media Portal.
• Developed by Md. Moniruzzaman (Soil & Agricultural Data Specialist).
```

---

### 4. Graphic Assets Specifications & Checklist

| Asset | Dimensions | Format | Requirement | Location |
|---|---|---|---|---|
| **App Icon** | 512 × 512 px | 32-bit PNG (with alpha) | Mandatory | `play_store_assets/play_store_512.png` |
| **Feature Graphic** | 1024 × 500 px | 24-bit PNG / JPEG (no alpha) | Mandatory | `play_store_assets/feature_graphic.png` |
| **Phone Screenshots** | Minimum 2, recommended 4–8 (1080 × 1920 or 1080 × 2400) | 24-bit PNG or JPEG | Mandatory | Capture in streaming emulator / Play Console |
| **Privacy Policy URL** | Hosted HTTPS URL | Web page | Mandatory | `play_store_assets/privacy_policy.html` |

---

### 5. Google Play Policy Questionnaire Declarations
- **News App**: `No`
- **COVID-19 Contact Tracing**: `No`
- **Data Safety**:
  - *Location*: Ephemeral for AEZ lookup and hyper-local agro-weather; not stored without user calculation saving.
  - *Personal Info*: Name and Email collected solely if the user signs in with Google to sync saved calculations to Firebase Firestore.
  - *Third-party sharing*: `No data shared with third parties or advertising networks`.
- **Government App Declaration**:
  `This app is an independent agronomic calculator developed based on publicly available scientific research publications (BARC FRG-2024). It is not an official government agency entity and does not represent the Ministry of Agriculture. Users are instructed to consult official DAE officers.`
