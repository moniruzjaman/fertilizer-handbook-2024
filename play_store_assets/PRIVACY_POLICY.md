# Privacy Policy for FRG 2024: Fertilizer Guide
**Effective Date:** November 10, 2026  
**Last Updated:** November 10, 2026  
**Application ID:** `com.aistudio.frgguide.m2024`  
**Contact:** `mithun.hstu@gmail.com`  
**Website:** `https://moniruzjaman.github.io/portfolio/`

---

## 1. Introduction
This Privacy Policy outlines how **FRG 2024: Fertilizer Guide** ("we", "our", or "the app") collects, uses, and safeguards user information when you utilize our Android application. We respect your privacy and are committed to maintaining transparent, minimal data practices in strict accordance with the Google Play Developer Program policies.

---

## 2. Information We Collect

### A. Location Data (`ACCESS_FINE_LOCATION` and `ACCESS_COARSE_LOCATION`)
- **Purpose**: The app collects your device's geographical coordinates (latitude and longitude) strictly while the application is active in the foreground.
- **Usage**: Location data is used exclusively to:
  1. Determine your regional Agro-Ecological Zone (AEZ 1 through AEZ 30) across Bangladesh.
  2. Resolve reverse-geocoded administrative locations (Union, Upazila, District) to match BARC regional soil recommendations.
  3. Fetch hyper-local agro-weather indices (spraying suitability, heat stress, rain forecast) from Open-Meteo.
  4. Enable GPS field plot mapping and geofencing to compute land area in decimals and bigha.
- **Retention**: Location data is processed on-device and is never continuously tracked in the background.

### B. Account & Authentication Information
- **Google Sign-In**: The app supports optional Google Sign-In via Android Jetpack `CredentialManager`.
- **Data Retrieved**: If you authenticate using Google, we retrieve your Google account email address, display name, and avatar URL.
- **Usage**: Used solely to maintain your user session, associate your saved fertilizer calculations with your private account, and secure your cloud records in Firebase Firestore.
- **Guest Mode**: All fertilizer calculations and guides function completely without signing in.

### C. User-Authored Agricultural Data
- Cultivated plot boundaries, selected crops, soil fertility ratings, manure application doses, and saved fertilizer dosage records.

---

## 3. Third-Party Services
The application integrates the following third-party infrastructure providers:

1. **Google Firebase (Authentication & Cloud Firestore)**:
   - Managed cloud database for authenticated users to synchronize saved calculation records across devices. Protected with zero-trust Firestore Security Rules.
   - [Google Privacy Policy](https://policies.google.com/privacy)
2. **Open-Meteo Weather API**:
   - Fetches hourly agro-meteorological data based on coordinate lookups. No personal identifiable information is transmitted.
   - [Open-Meteo Terms & Privacy](https://open-meteo.com/en/terms)
3. **OpenStreetMap / Nominatim**:
   - Translates GPS coordinates into administrative boundaries (Union, Upazila, District). No personal data is stored or logged.

---

## 4. Data Sharing & Disclosure
- **No Commercial Sale**: We never sell, rent, monetize, or trade your personal or location data to advertising networks, brokers, or marketing firms.
- **Zero Advertising**: This application contains no third-party advertisements or tracking SDKs.

---

## 5. Data Retention & User Deletion Rights
Under Google Play data safety regulations, users have complete control over their stored data:

- **In-App Deletion**: Authenticated users can delete any saved fertilizer calculation or mapped plot at any time via the user account interface.
- **Account & Data Purge Request**: To request the complete deletion of your user profile and all associated calculation history from Firebase Firestore, email your request to:  
  **`mithun.hstu@gmail.com`** with the subject line `"FRG 2024 Account Data Deletion Request"`. Requests are fulfilled within 7 business days.

---

## 6. Official Statutory Disclaimer & DAE Advisory
- **Scientific Estimation**: The fertilizer recommendations provided by this application are scientific models calculated from the Bangladesh Agricultural Research Council (BARC) Fertilizer Recommendation Guide 2024 (FRG-2024).
- **Mandatory DAE Consultation**: Because soil properties vary between plots, users are strictly advised to consult their local Sub-Assistant Agriculture Officer (SAAO) or Upazila Agriculture Officer (Department of Agricultural Extension - DAE) and test their soil with the Soil Resource Development Institute (SRDI) prior to purchasing or applying bulk fertilizers.
- **Non-Governmental Disclaimer**: This app is an independent agronomic calculator developed for educational and agricultural utility. It does not represent an official government ministry entity.

---

## 7. Children's Privacy
Our services are designed for farmers, agricultural students, and agronomy professionals. We do not knowingly collect personal information from children under 13 years of age.

---

## 8. Changes to this Policy
We may periodically update this Privacy Policy. Any modifications will be reflected with an updated "Last Updated" date at the top of this document.

---

## 9. Contact Us
If you have any questions or concerns regarding this Privacy Policy:
- **Email**: `mithun.hstu@gmail.com`
- **Developer Portfolio**: [https://moniruzjaman.github.io/portfolio/](https://moniruzjaman.github.io/portfolio/)
