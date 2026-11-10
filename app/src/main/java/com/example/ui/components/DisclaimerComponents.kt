package com.example.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.DisclaimerConstants
import com.example.ui.theme.AgrigreenPrimary
import com.example.util.AppLanguage
import com.example.util.AppStrings

@Composable
fun DisclaimerBanner(
    language: AppLanguage = AppLanguage.BANGLA,
    modifier: Modifier = Modifier
) {
    var showFullDialog by remember { mutableStateOf(false) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { showFullDialog = true }
            .testTag("disclaimer_banner"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF8E1))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = "Disclaimer",
                tint = Color(0xFFF57F17),
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = AppStrings.disclaimerBannerTitle(language),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFBF360C)
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = AppStrings.disclaimerBannerText(language),
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF4E342E),
                    lineHeight = 17.sp
                )
            }

            Spacer(modifier = Modifier.width(6.dp))

            Text(
                text = if (language == AppLanguage.BANGLA) "বিস্তারিত" else "Details",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFE65100),
                modifier = Modifier.testTag("button_disclaimer_details")
            )
        }
    }

    if (showFullDialog) {
        FullDisclaimerDialog(
            language = language,
            onDismiss = { showFullDialog = false }
        )
    }
}

@Composable
fun FullDisclaimerDialog(
    language: AppLanguage = AppLanguage.BANGLA,
    onDismiss: () -> Unit
) {
    val disclaimerText = if (language == AppLanguage.BANGLA)
        DisclaimerConstants.FULL_STATUTORY_DISCLAIMER_BN
    else
        DisclaimerConstants.FULL_STATUTORY_DISCLAIMER_EN

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = Color(0xFFE65100),
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (language == AppLanguage.BANGLA) "বিধিবদ্ধ সতর্কবার্তা ও দায়মুক্তি" else "Statutory Disclaimer",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = disclaimerText,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    lineHeight = 19.sp
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = AgrigreenPrimary),
                modifier = Modifier.testTag("button_dismiss_disclaimer")
            ) {
                Text(if (language == AppLanguage.BANGLA) "আমি অবগত হয়েছি" else "I Understand")
            }
        }
    )
}
