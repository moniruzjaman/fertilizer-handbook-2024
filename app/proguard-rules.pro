# ProGuard & R8 Optimization Rules for Google Play Release Build
# Application: FRG 2024: Fertilizer Guide (com.aistudio.frgguide.m2024)

# 1. Preserve Data Models & Serializable Classes
-keep class com.example.model.** { *; }
-keepclassmembers class com.example.model.** { *; }

# 2. Preserve Firebase Firestore & Firebase Auth Models
-keepattributes *Annotation*
-keepattributes Signature
-keepattributes InnerClasses
-keepattributes EnclosingMethod

-keep class com.google.firebase.firestore.** { *; }
-keepclassmembers class com.google.firebase.firestore.** { *; }
-keep class com.google.firebase.auth.** { *; }
-keepclassmembers class com.google.firebase.auth.** { *; }

# 3. Preserve Credential Manager & Google ID
-keep class androidx.credentials.** { *; }
-keep class com.google.android.libraries.identity.googleid.** { *; }

# 4. Preserve Coroutines & Flow
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}

# 5. Preserve OkHttp & Retrofit Network Models
-dontwarn okhttp3.**
-dontwarn okio.**
-keep class okhttp3.** { *; }

# 6. Preserve Line Numbers for Play Console Crash Reporting
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile
