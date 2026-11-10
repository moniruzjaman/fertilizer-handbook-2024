# Google Play Console Data Safety Questionnaire Guide
## FRG 2024: Fertilizer Guide (`com.aistudio.frgguide.m2024`)

Use these exact answers when completing the **Data safety** questionnaire in Google Play Console.

---

### Section 1: Data Collection & Security
1. **Does your app collect or share any of the required user data types?**  
   👉 **Yes**
2. **Is all of the user data collected by your app encrypted in transit?**  
   👉 **Yes** (All network transmissions to Firebase, Open-Meteo, and Nominatim use HTTPS / TLS 1.3).
3. **Do you provide a way for users to request that their data is deleted?**  
   👉 **Yes** (Users can delete calculations in-app and request account deletion via email: `mithun.hstu@gmail.com`).

---

### Section 2: Data Types Declared

#### 1. Location
- **Approximate location (`ACCESS_COARSE_LOCATION`)**:
  - *Collected*: Yes
  - *Shared*: No
  - *Processed ephemerally*: Yes (coordinates are used on-device to match AEZ and fetch agro-weather)
  - *Required or Optional*: Optional (app provides manual AEZ picker if permission is denied)
  - *Purposes*: **App functionality** (determining AEZ 1-30, soil characteristics, and weather advisories)
- **Precise location (`ACCESS_FINE_LOCATION`)**:
  - *Collected*: Yes
  - *Shared*: No
  - *Processed ephemerally*: Yes (for plot boundary geofencing and area measurement)
  - *Required or Optional*: Optional
  - *Purposes*: **App functionality** (field boundary polygon mapping and decimal area calculation)

#### 2. Personal Info (Only for users who sign in with Google)
- **Name**:
  - *Collected*: Yes
  - *Shared*: No
  - *Processed ephemerally*: No (stored in Firebase Firestore user document)
  - *Required or Optional*: Optional (Google Sign-In is optional; Guest mode is fully supported)
  - *Purposes*: **Account management**
- **Email address**:
  - *Collected*: Yes
  - *Shared*: No
  - *Processed ephemerally*: No (stored in Firebase Firestore user document)
  - *Required or Optional*: Optional
  - *Purposes*: **Account management** & **App functionality** (associating user's saved fertilizer calculations)

---

### Section 3: Third Parties & Tracking
- **Is data shared with any third party?**  
  👉 **No**
- **Does the app use Advertising ID or tracking SDKs?**  
  👉 **No** (Zero advertising SDKs or tracking libraries)
