package com.example.data.repository

import android.content.Context
import android.util.Log
import com.example.R
import com.example.model.FertilizerCalculationResult
import com.example.model.SavedCalculation
import com.example.model.UserProfile
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class FirestoreUserRepository(private val db: FirebaseFirestore) {

    constructor(context: Context) : this(
        FirebaseFirestore.getInstance(
            context.applicationContext.getString(R.string.firestore_database_id)
        )
    )

    private val auth = Firebase.auth
    private val TAG = "FirestoreUserRepository"

    private fun requireUserId(): String {
        return auth.currentUser?.uid
            ?: throw IllegalStateException("User must be signed in with Google to access Firestore.")
    }

    suspend fun syncUserProfile(user: FirebaseUser, detectedAez: Int?, detectedDistrict: String?) = withContext(Dispatchers.IO) {
        val uid = user.uid
        val docRef = db.collection("users").document(uid)

        try {
            val snapshot = docRef.get().await()
            val email = user.email ?: "farmer@frg2024.local"
            val displayName = user.displayName ?: user.email?.substringBefore("@") ?: "Farmer"
            val photoUrl = user.photoUrl?.toString()

            if (!snapshot.exists()) {
                val newProfile = hashMapOf(
                    "userId" to uid,
                    "email" to email,
                    "displayName" to displayName,
                    "createdAt" to FieldValue.serverTimestamp(),
                    "updatedAt" to FieldValue.serverTimestamp()
                )
                if (photoUrl != null) newProfile["photoUrl"] = photoUrl
                if (detectedAez != null) newProfile["detectedAez"] = detectedAez
                if (detectedDistrict != null) newProfile["detectedDistrict"] = detectedDistrict

                docRef.set(newProfile).await()
                Log.d(TAG, "Created new user profile in Firestore for $email")
            } else {
                val updates = hashMapOf<String, Any>(
                    "email" to email,
                    "displayName" to displayName,
                    "updatedAt" to FieldValue.serverTimestamp()
                )
                if (photoUrl != null) updates["photoUrl"] = photoUrl
                if (detectedAez != null) updates["detectedAez"] = detectedAez
                if (detectedDistrict != null) updates["detectedDistrict"] = detectedDistrict

                docRef.update(updates).await()
                Log.d(TAG, "Updated existing user profile in Firestore for $email")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to sync user profile: ${e.message}", e)
        }
    }

    suspend fun saveCalculation(result: FertilizerCalculationResult): String = withContext(Dispatchers.IO) {
        val uid = requireUserId()
        val docRef = db.collection("users").document(uid).collection("calculations").document()

        val payload = hashMapOf<String, Any>(
            "userId" to uid,
            "cropName" to result.crop.name,
            "cropBengaliName" to result.crop.bengaliName,
            "landArea" to result.landArea.toDouble(),
            "landUnit" to result.landUnit.name,
            "areaInHectares" to result.areaInHectares.toDouble(),
            "soilFertilityClass" to result.fertilityClass.name,
            "cowdungTonnesPerHa" to result.cowdungTonnesPerHa.toDouble(),
            "totalCostTk" to result.totalCostTk.toDouble(),
            "totalBags50kg" to result.totalBags50kg.toDouble(),
            "createdAt" to FieldValue.serverTimestamp(),
            "updatedAt" to FieldValue.serverTimestamp()
        )

        result.detectedAez?.let {
            payload["aezNumber"] = it.aezNumber
            payload["aezName"] = it.name
        }

        docRef.set(payload).await()
        Log.d(TAG, "Saved calculation with id ${docRef.id}")
        docRef.id
    }

    fun observeSavedCalculations(): Flow<List<SavedCalculation>> = flow {
        val uid = auth.currentUser?.uid
        if (uid == null) {
            emit(emptyList())
            return@flow
        }

        val collectionRef = db.collection("users").document(uid).collection("calculations")
        collectionRef.snapshots()
            .map { snapshot -> snapshot.toObjects(SavedCalculation::class.java) }
            .catch { e ->
                Log.e(TAG, "Error observing saved calculations: ${e.message}", e)
                emit(emptyList())
            }
            .collect { emit(it) }
    }

    suspend fun deleteCalculation(calcId: String) = withContext(Dispatchers.IO) {
        val uid = requireUserId()
        db.collection("users").document(uid).collection("calculations").document(calcId).delete().await()
    }
}
