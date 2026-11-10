package com.example.ui.components

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.credentials.CredentialManager
import com.example.auth.AuthManager
import com.example.model.SavedCalculation
import com.example.ui.theme.AgrigreenDark
import com.example.ui.theme.AgrigreenPrimary
import com.example.util.AppLanguage
import com.google.firebase.auth.FirebaseUser

@Composable
fun UserAccountDialog(
    user: FirebaseUser?,
    savedCalculations: List<SavedCalculation>,
    language: AppLanguage,
    onDismiss: () -> Unit,
    onAuthSuccess: (FirebaseUser) -> Unit,
    onSignOut: () -> Unit,
    onDeleteCalculation: (String) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val credentialManager = remember { CredentialManager.create(context) }
    var isSigningIn by remember { mutableStateOf(false) }
    var authError by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = null,
                    tint = AgrigreenPrimary,
                    modifier = Modifier.size(26.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (language == AppLanguage.BANGLA) "কৃষক অ্যাকাউন্ট ও ক্লাউড সিঙ্ক" else "Farmer Account & Cloud Sync",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                if (user != null) {
                    // Signed In Profile Card
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(AgrigreenPrimary.copy(alpha = 0.10f))
                            .border(1.dp, AgrigreenPrimary.copy(alpha = 0.3f), RoundedCornerShape(14.dp))
                            .padding(14.dp)
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(42.dp)
                                        .clip(CircleShape)
                                        .background(AgrigreenPrimary),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = user.displayName?.take(1)?.uppercase() ?: user.email?.take(1)?.uppercase() ?: "K",
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 18.sp
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = user.displayName ?: "Farmer Profile",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = user.email ?: "",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.CloudDone,
                                    contentDescription = null,
                                    tint = AgrigreenDark,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (language == AppLanguage.BANGLA)
                                        "ডিভাইসের গুগল অ্যাকাউন্ট স্বয়ংক্রিয়ভাবে সিঙ্ক হয়েছে"
                                    else
                                        "Device Google Account synced with Firestore",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = AgrigreenDark
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Saved Calculations History Header
                    Text(
                        text = if (language == AppLanguage.BANGLA) "সংরক্ষিত সার হিসাব তালিকা (${savedCalculations.size})" else "Saved Calculations (${savedCalculations.size})",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    if (savedCalculations.isEmpty()) {
                        Text(
                            text = if (language == AppLanguage.BANGLA)
                                "এখনও কোনো হিসাব সংরক্ষণ করা হয়নি। ক্যালকুলেটর থেকে 'ক্লাউডে সংরক্ষণ করুন' চাপুন।"
                            else
                                "No calculations saved yet. Tap 'Save Calculation' in the Calculator.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxWidth().height(160.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            items(savedCalculations) { item ->
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                                        .padding(horizontal = 10.dp, vertical = 8.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = "${item.cropBengaliName} (${item.cropName})",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 12.sp,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                            Text(
                                                text = "${item.landArea} ${item.landUnit} | ৳ ${String.format("%.0f", item.totalCostTk)}",
                                                fontSize = 11.sp,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                        IconButton(
                                            onClick = { onDeleteCalculation(item.id) },
                                            modifier = Modifier.size(28.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Delete,
                                                contentDescription = "Delete",
                                                tint = Color(0xFFC62828),
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedButton(
                        onClick = {
                            AuthManager.signOut(credentialManager, scope) {
                                onSignOut()
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ExitToApp, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(if (language == AppLanguage.BANGLA) "অ্যাকাউন্ট থেকে লগআউট" else "Sign Out of Account")
                    }
                } else {
                    // Not Signed In Card
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = if (language == AppLanguage.BANGLA)
                                "আপনার ডিভাইসের গুগল অ্যাকাউন্ট দিয়ে সাইন-ইন করে সারের হিসাব ক্লাউডে সংরক্ষণ ও যেকোনো ডিভাইস থেকে এক্সেস করুন।"
                            else
                                "Sign in with your Google account to auto-fetch device email and save your fertilizer calculations securely to Firestore.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = {
                                isSigningIn = true
                                authError = null
                                AuthManager.onGoogleSignInClicked(
                                    activity = context as Activity,
                                    credentialManager = credentialManager,
                                    onAuthSuccess = { user ->
                                        isSigningIn = false
                                        onAuthSuccess(user)
                                    },
                                    onAuthError = { err ->
                                        isSigningIn = false
                                        authError = err
                                    },
                                    scope = scope,
                                    onAuthCancelled = { isSigningIn = false }
                                )
                            },
                            enabled = !isSigningIn,
                            colors = ButtonDefaults.buttonColors(containerColor = AgrigreenPrimary),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("button_google_signin")
                        ) {
                            if (isSigningIn) {
                                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(if (language == AppLanguage.BANGLA) "সাইন-ইন হচ্ছে..." else "Signing in...")
                            } else {
                                Icon(imageVector = Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(if (language == AppLanguage.BANGLA) "গুগল অ্যাকাউন্ট দিয়ে সাইন-ইন করুন" else "Sign in with Google")
                            }
                        }

                        if (authError != null) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = authError ?: "",
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(if (language == AppLanguage.BANGLA) "ঠিক আছে" else "Close")
            }
        }
    )
}
