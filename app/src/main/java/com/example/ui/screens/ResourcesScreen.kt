package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Grass
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Slideshow
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.material.icons.filled.ViewCarousel
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import com.example.model.DisclaimerConstants
import com.example.model.FertilizerCalculationResult
import com.example.ui.components.DisclaimerBanner
import com.example.ui.theme.AgrigreenDark
import com.example.ui.theme.AgrigreenPrimary
import com.example.ui.theme.HarvestGold
import com.example.util.AppLanguage
import com.example.util.AppStrings
import java.io.File
import java.io.FileOutputStream
import java.nio.charset.StandardCharsets

@Composable
fun ResourcesScreen(
    result: FertilizerCalculationResult,
    language: AppLanguage,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var showSlides by remember { mutableStateOf(false) }
    var showInfographics by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("resources_screen"),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Mandatory Statutory Disclaimer Banner
        item {
            DisclaimerBanner(language = language)
        }

        // 2. Publication Header: সার সুপারিশমালা হাতবই ২০২৪
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("resource_header_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    AgrigreenDark,
                                    Color(0xFF0F321B)
                                )
                            )
                        )
                        .padding(20.dp)
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.MenuBook,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = if (language == AppLanguage.BANGLA) "মূল উৎস প্রকাশনা" else "ORIGINAL SOURCE PUBLICATION",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White.copy(alpha = 0.9f),
                                letterSpacing = 1.1.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = if (language == AppLanguage.BANGLA)
                                "সার সুপারিশমালা হাতবই' ২০২৪ (FRG-2024)"
                            else
                                "Fertilizer Recommendation Guide Handbook 2024",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = if (language == AppLanguage.BANGLA)
                                "মৃত্তিকা বিজ্ঞান বিভাগ, বাংলাদেশ কৃষি গবেষণা কাউন্সিল (BARC) ও মৃত্তিকা সম্পদ উন্নয়ন ইনস্টিটিউট (SRDI), কৃষি মন্ত্রণালয়, গণপ্রজাতন্ত্রী বাংলাদেশ সরকার।"
                            else
                                "Soils Science Division, Bangladesh Agricultural Research Council (BARC) & Soil Resource Development Institute (SRDI), Ministry of Agriculture, Dhaka.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.85f),
                            lineHeight = 18.sp
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedButton(
                            onClick = { openWebUrl(context, "http://www.barc.gov.bd/") },
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                            border = ButtonDefaults.outlinedButtonBorder(enabled = true).copy(brush = Brush.linearGradient(listOf(Color.White, Color.White))),
                            modifier = Modifier.testTag("button_open_barc_source_pdf")
                        ) {
                            Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (language == AppLanguage.BANGLA) "বার্ক হাতবই পোর্টাল দেখুন" else "Visit BARC Official Portal",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // 3. App Builder Profile Card: Md. Moniruzzaman
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("app_builder_card"),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.65f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(CircleShape)
                                    .background(AgrigreenPrimary),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "M",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 20.sp
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "Md. Moniruzzaman",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(AgrigreenPrimary.copy(alpha = 0.15f))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = if (language == AppLanguage.BANGLA) "অ্যাপ নির্মাতা" else "App Builder",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = AgrigreenPrimary
                                        )
                                    }
                                }
                                Text(
                                    text = "Agricultural Software Engineer & Soil Researcher",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = if (language == AppLanguage.BANGLA)
                            "বাংলাদেশ কৃষি গবেষণা কাউন্সিলের সার নির্দেশিকা ২০২৪ (FRG-2024) কে ডিজিটাল প্ল্যাটফর্মে রূপান্তর ও কৃষকদের সহজে সারের হিসাব নির্ধারণে এই অ্যাপ্লিকেশনটি তৈরি করা হয়েছে।"
                        else
                            "Developed this digital FRG-2024 calculator to empower farmers, extension agents, and agronomists across Bangladesh with location-based nutrient stewardship.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = { openWebUrl(context, "https://moniruzjaman.github.io/portfolio/") },
                        colors = ButtonDefaults.buttonColors(containerColor = AgrigreenPrimary),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("button_open_portfolio")
                    ) {
                        Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (language == AppLanguage.BANGLA) "নির্মাতার পোর্টফোলিও দেখুন (moniruzjaman.github.io)" else "View Builder Portfolio (moniruzjaman.github.io)",
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(Icons.AutoMirrored.Filled.OpenInNew, contentDescription = null, modifier = Modifier.size(14.dp))
                    }
                }
            }
        }

        // 4. Soil Media Portal Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("soil_media_portal_card"),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.65f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF6D4C41).copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Public,
                                contentDescription = null,
                                tint = Color(0xFF6D4C41),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = if (language == AppLanguage.BANGLA) "মৃত্তিকা মিডিয়া ও গবেষণা পোর্টাল" else "Soil Media & Research Portal",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "soil-media-portal.mithun-hstu.workers.dev",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = if (language == AppLanguage.BANGLA)
                            "মৃত্তিকা সম্পদ উন্নয়ন ইনস্টিটিউট (SRDI) এর ডিজিটাল মৃত্তিকা ডাটাবেজ, পুষ্টি মানচিত্র, ল্যাবরেটরি টেস্ট নির্দেশনা ও গবেষণা মাল্টিমিডিয়া রিসোর্স।"
                        else
                            "Access the comprehensive digital soil database, spatial soil nutrient maps, SRDI testing protocols, and agricultural media portal.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedButton(
                        onClick = { openWebUrl(context, "https://soil-media-portal.mithun-hstu.workers.dev/") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("button_open_soil_media_portal")
                    ) {
                        Icon(Icons.Default.Language, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (language == AppLanguage.BANGLA) "মৃত্তিকা পোর্টাল ওপেন করুন" else "Open Soil Media Portal",
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(Icons.AutoMirrored.Filled.OpenInNew, contentDescription = null, modifier = Modifier.size(14.dp))
                    }
                }
            }
        }

        // 5. NotebookLM Interactive AI Study Hub, Slide Decks & Infographics
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("notebooklm_hub_card"),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.65f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF7B1FA2).copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = Color(0xFF7B1FA2),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = if (language == AppLanguage.BANGLA) "নোটবুকএলএম এআই জ্ঞানভাণ্ডার (NotebookLM)" else "NotebookLM AI Study Hub & Media",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = if (language == AppLanguage.BANGLA) "স্লাইড ডেক্স, ইনফোগ্রাফিক্স ও অডিও আলোচনা" else "Slide Decks, Infographics & Deep Dive Audio",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFF7B1FA2)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = if (language == AppLanguage.BANGLA)
                            "গুগল নোটবুকএলএম (NotebookLM) দ্বারা বার্ক সার নির্দেশিকা ২০২৪ কে বিশ্লেষণ করে তৈরি করা ইন্টারঅ্যাক্টিভ স্লাইড ডেক্স, সার চেনার ইনফোগ্রাফিক্স ও অডিও ব্রিফিং।"
                        else
                            "Curated AI knowledge synthesis from BARC FRG-2024 powered by Google NotebookLM, featuring slide decks, infographics, and podcast summaries.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Open NotebookLM Link Button
                    Button(
                        onClick = { openWebUrl(context, "https://notebook.google.com/notebook/1218c86f-beef-4a00-a0e2-1b2624c12128") },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7B1FA2)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("button_open_notebooklm")
                    ) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (language == AppLanguage.BANGLA) "নোটবুকএলএম এ ওপেন করুন (NotebookLM Portal)" else "Open in Google NotebookLM",
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(Icons.AutoMirrored.Filled.OpenInNew, contentDescription = null, modifier = Modifier.size(14.dp))
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                    Spacer(modifier = Modifier.height(10.dp))

                    // Slide Decks Expandable Section
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { showSlides = !showSlides }
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Slideshow, contentDescription = null, tint = Color(0xFF7B1FA2), modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (language == AppLanguage.BANGLA) "স্লাইড ডেক্স প্রিভিউ (Slide Decks)" else "In-App Slide Deck Previews",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Icon(
                            imageVector = if (showSlides) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = null,
                            tint = Color(0xFF7B1FA2)
                        )
                    }

                    AnimatedVisibility(visible = showSlides) {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(top = 8.dp)) {
                            SlidePreviewItem(
                                slideNumber = 1,
                                title = if (language == AppLanguage.BANGLA) "স্লাইড ১: ৪আর (4R) সার পুষ্টি ব্যবস্থাপনা" else "Slide 1: 4R Nutrient Stewardship",
                                bulletPoints = listOf(
                                    if (language == AppLanguage.BANGLA) "সঠিক উৎস (Right Source): মাটির পিএইচ ও ফসলের চাহিদা অনুযায়ী সার নির্বাচন।" else "Right Source: Select fertilizers aligned with soil pH and crop physiology.",
                                    if (language == AppLanguage.BANGLA) "সঠিক মাত্রা (Right Rate): বার্ক এফআরজি ২০২৪ এর সুপারিশকৃত পরিমাণ।" else "Right Rate: Calibrate exact dosage from BARC FRG-2024.",
                                    if (language == AppLanguage.BANGLA) "সঠিক সময় (Right Time): ফসলের কুশি ও থোড় পর্যায়ে কিস্তিতে প্রয়োগ।" else "Right Time: Match split applications with tillering and panicle stages.",
                                    if (language == AppLanguage.BANGLA) "সঠিক পদ্ধতি (Right Place): মাটির নিচে ৫-৭ সেমি গভীরে স্থাপন।" else "Right Place: Deep placement 5-7 cm below surface."
                                )
                            )
                            SlidePreviewItem(
                                slideNumber = 2,
                                title = if (language == AppLanguage.BANGLA) "স্লাইড ২: ডিএপি ও ইউরিয়া সার সমন্বয়" else "Slide 2: DAP & Urea Chemical Balancing",
                                bulletPoints = listOf(
                                    if (language == AppLanguage.BANGLA) "ডিএপিতে ১৮% নাইট্রোজেন এবং ২০% ফসফরাস (৪৬% P2O5) থাকে।" else "DAP supplies 18% elemental Nitrogen and 20% elemental Phosphorus.",
                                    if (language == AppLanguage.BANGLA) "১০০ কেজি ডিএপি প্রয়োগ করলে ১৮ কেজি খাঁটি নাইট্রোজেন পাওয়া যায়।" else "100 kg DAP automatically provides 18 kg elemental Nitrogen.",
                                    if (language == AppLanguage.BANGLA) "ফলে ইউরিয়া সার থেকে প্রায় ৩৯ কেজি ইউরিয়া স্বয়ংক্রিয়ভাবে বাদ দেওয়া হয়।" else "Urea dosage is proportionately reduced by ~39.1 kg.",
                                    if (language == AppLanguage.BANGLA) "ডিএপি ব্যবহারের ফলে অর্থ সাশ্রয় ও জমির অম্লতা বৃদ্ধি রোধ হয়।" else "Saves farmer money and prevents excessive soil acidification."
                                )
                            )
                            SlidePreviewItem(
                                slideNumber = 3,
                                title = if (language == AppLanguage.BANGLA) "স্লাইড ৩: অণুখাদ্য (জিংক ও বোরন) এর ভূমিকা" else "Slide 3: Secondary & Micronutrients",
                                bulletPoints = listOf(
                                    if (language == AppLanguage.BANGLA) "জিপসাম (Gypsum - ১৮% S): ধানের কুশি ও প্রোটিন সংশ্লেষ বাড়ায়।" else "Gypsum (18% S): Essential for protein synthesis and grain filling.",
                                    if (language == AppLanguage.BANGLA) "জিংক সালফেট (২১% Zn): খায়রা রোগ প্রতিরোধ ও ফলন বৃদ্ধি করে।" else "Zinc Sulfate (21% Zn): Prevents Khaira disease in wetland rice.",
                                    if (language == AppLanguage.BANGLA) "বোরিক এসিড (১৭% B): গম, সরিষা ও আলুর পরাগায়ন ও আকার বড় করে।" else "Boric Acid (17% B): Prevents sterility in wheat, mustard, and maize."
                                )
                            )
                            SlidePreviewItem(
                                slideNumber = 4,
                                title = if (language == AppLanguage.BANGLA) "স্লাইড ৪: জৈব সার ও মাটির স্বাস্থ্য সুরক্ষা" else "Slide 4: Integrated Organic Manure Credits",
                                bulletPoints = listOf(
                                    if (language == AppLanguage.BANGLA) "প্রতি ১ টন পচা গোবরে ৫ কেজি N, ২.৫ কেজি P ও ৫ কেজি K থাকে।" else "Every 1 ton cowdung supplies 5 kg N, 2.5 kg P, 5 kg K.",
                                    if (language == AppLanguage.BANGLA) "হেক্টরে ৫ টন গোবর দিলে রাসায়নিক সারের খরচ উল্লেখযোগ্যভাবে কমে।" else "Applying 5 t/ha substantially reduces synthetic chemical fertilizer costs.",
                                    if (language == AppLanguage.BANGLA) "মাটির পানি ধারণ ক্ষমতা ও অণুজীবের সক্রিয়তা বহুগুণ বৃদ্ধি পায়।" else "Boosts microbial health, soil CEC, and drought resilience."
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Infographics Expandable Section
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { showInfographics = !showInfographics }
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.ViewCarousel, contentDescription = null, tint = Color(0xFF0288D1), modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (language == AppLanguage.BANGLA) "সার ও মাটির ইনফোগ্রাফিক্স (Infographics)" else "Visual Infographics & Field Guides",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Icon(
                            imageVector = if (showInfographics) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = null,
                            tint = Color(0xFF0288D1)
                        )
                    }

                    AnimatedVisibility(visible = showInfographics) {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(top = 8.dp)) {
                            InfographicItem(
                                title = if (language == AppLanguage.BANGLA) "ইনফোগ্রাফিক ১: সার চেনার সহজ উপায় ও ভেজাল পরীক্ষা" else "Infographic 1: Fertilizer Purity Field Tests",
                                description = if (language == AppLanguage.BANGLA)
                                    "• ইউরিয়া: পানিতে সম্পূর্ণ দ্রবীভূত হয়ে পানি বরফের মতো ঠান্ডা হবে।\n• টিএসপি: কুসুম গরম পানিতে ঝাঁকালে সহজে গলবে না, সামান্য পলি থাকবে।\n• এমওপি: লালচে দানা পানিতে গুললে লালচে দ্রবণ তৈরি হবে এবং নিচে লবণাক্ত স্বাদ থাকবে।\n• জিপসাম: পানিতে অদ্রবণীয় ও আঙুলে ঘষলে মসৃণ পাউডারের মতো লাগবে।"
                                else
                                    "• Urea: Dissolves completely in water causing noticeable drop in water temperature.\n• TSP: Granular, slow dissolving in water with minimal precipitate.\n• MoP: Reddish crystal flakes, highly soluble with characteristic saline taste.\n• Gypsum: White insoluble fine powder."
                            )

                            InfographicItem(
                                title = if (language == AppLanguage.BANGLA) "ইনফোগ্রাফিক ২: ফসলে পুষ্টি ঘাটতির তাৎক্ষণিক ফিল্ড লক্ষণ" else "Infographic 2: Nutrient Deficiency Symptoms",
                                description = if (language == AppLanguage.BANGLA)
                                    "• নাইট্রোজেন (N): পুরাতন নিচের পাতা হলুদ হয়ে যায়, গাছের বৃদ্ধি থমকে যায়।\n• ফসফরাস (P): পাতা গাঢ় সবুজ থেকে বেগুনী বর্ণ ধারণ করে, শিকড় বাড়ে না।\n• পটাশিয়াম (K): পাতার অগ্রভাগ ও কিনারা পুড়ে যাওয়ার মতো শুকিয়ে বাদামী হয়।\n• জিংক (Zn): ধানের পাতায় মরিচার মতো বাদামী ছোপ ছোপ দাগ (খায়রা রোগ) দেখা দেয়।"
                                else
                                    "• Nitrogen: Chlorosis starting from older lower leaves.\n• Phosphorus: Dark green/purplish coloration and stunted root network.\n• Potassium: Marginal scorch and curling of leaf tips.\n• Zinc: Rusty bronze lesions on middle leaves in paddy."
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Audio Deep Dive Podcast Summary Box
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFEDE7F6))
                            .padding(12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.Top) {
                            Icon(Icons.Default.Headphones, contentDescription = null, tint = Color(0xFF7B1FA2), modifier = Modifier.size(20.dp).padding(top = 2.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = if (language == AppLanguage.BANGLA) "নোটবুকএলএম অডিও পডকাস্ট সারসংক্ষেপ" else "NotebookLM Audio Deep Dive Summary",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF4A148C)
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = if (language == AppLanguage.BANGLA)
                                        "কৃষি বিজ্ঞানী ও বিশেষজ্ঞদের একটি ইন্টারঅ্যাক্টিভ অডিও পডকাস্ট, যেখানে বাংলাদেশের ৩০টি কৃষি পরিবেশ অঞ্চলের মাটির পরিবর্তন, বৈশ্বিক উষ্ণায়নের প্রভাবে সারের প্রয়োজনীয়তা এবং সমন্বিত পুষ্টি ব্যবস্থার গুরুত্ব তুলে ধরা হয়েছে।"
                                    else
                                        "Interactive deep-dive audio conversation discussing Bangladesh's 30 AEZ calibrations, climatic soil shifts, and sustainable 4R nutrient stewardship.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFF311B92),
                                    lineHeight = 17.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        // 6. Active Live Calculation Summary Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("active_calculation_preview_card"),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Grass,
                                contentDescription = null,
                                tint = AgrigreenPrimary,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (language == AppLanguage.BANGLA) "বর্তমান হিসাব সারাংশ (রেডি টু ডাউনলোড)" else "Active Calculation Summary (Ready to Download)",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(AgrigreenPrimary.copy(alpha = 0.15f))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "${result.crop.bengaliName} / ${result.crop.name}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = AgrigreenPrimary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = if (language == AppLanguage.BANGLA) "জমির পরিমাণ:" else "Land Area:",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "${result.landArea} ${result.landUnit.displayName} (${String.format("%.3f", result.areaInHectares)} ha)",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = if (language == AppLanguage.BANGLA) "কৃষি পরিবেশ অঞ্চল (AEZ):" else "Agro-Ecological Zone:",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = result.detectedAez?.let { "AEZ ${it.aezNumber} (${it.name})" } ?: "General Zone",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.SemiBold,
                            color = AgrigreenPrimary
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = if (language == AppLanguage.BANGLA) "মাটির উর্বরতা মাত্রা:" else "Soil Fertility Class:",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = result.fertilityClass.displayName,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                    Spacer(modifier = Modifier.height(8.dp))

                    result.items.forEach { item ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 2.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "${item.fertilizerType.bengaliName} (${item.fertilizerType.englishName}):",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "${String.format("%.1f", item.recommendedKg)} kg (৳ ${String.format("%.0f", item.estimatedCostTk)})",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold,
                                color = AgrigreenDark
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = if (language == AppLanguage.BANGLA) "সর্বমোট আনুমানিক খরচ:" else "Total Estimated Cost:",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "৳ ${String.format("%.0f", result.totalCostTk)} BDT (${String.format("%.1f", result.totalBags50kg)} bags)",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = AgrigreenPrimary
                        )
                    }
                }
            }
        }

        // 7. Ready to Download / Export Section
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("download_actions_card"),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
                ) {
                    Text(
                        text = if (language == AppLanguage.BANGLA) "হিসাব ডাউনলোড ও শেয়ার অপশন" else "Ready to Download & Export Options",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (language == AppLanguage.BANGLA)
                            "আপনার কাস্টমাইজড সার হিসাবটি সরাসরি CSV এক্সেল ফাইল অথবা টেক্সট ফাইল আকারে ডিভাইসে সংরক্ষণ ও ডাউনলোড করুন।"
                        else
                            "Download and save your exact fertilizer recommendation directly as an Excel CSV or Text report on your device.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { downloadCsvReport(context, result, language) },
                        colors = ButtonDefaults.buttonColors(containerColor = AgrigreenPrimary),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("button_download_csv")
                    ) {
                        Icon(Icons.Default.TableChart, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = AppStrings.downloadCsvButton(language),
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = { downloadTxtReport(context, result, language) },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E6B4F)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("button_download_txt")
                    ) {
                        Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = AppStrings.downloadTxtButton(language),
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedButton(
                        onClick = { copyReportToClipboard(context, result, language) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("button_copy_clipboard")
                    ) {
                        Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = AppStrings.copyClipboardButton(language),
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedButton(
                        onClick = { shareFertilizerReport(context, result, language) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("button_share_report")
                    ) {
                        Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = AppStrings.downloadReportButton(language),
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }

        // 8. Official Government Portals Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("official_portals_card"),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
                ) {
                    Text(
                        text = if (language == AppLanguage.BANGLA) "অফিশিয়াল প্রকাশনা ও সরকারি পোর্টাল" else "Official Publications & Government Portals",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = if (language == AppLanguage.BANGLA)
                            "বার্ক এফআরজি ২০২৪ এর মূল মুদ্রিত রেফারেন্স বই, মৃত্তিকা সম্পদ উন্নয়ন ইনস্টিটিউট এবং কৃষি সম্প্রসারণ অধিদপ্তরের মূল ডেটাবেজ।"
                        else
                            "Access the original BARC Fertilizer Recommendation Guide 2024 sourcebook, SRDI soil testing data, and DAE portals.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedButton(
                        onClick = { openWebUrl(context, "http://www.barc.gov.bd/") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("button_open_barc_web")
                    ) {
                        Icon(Icons.Default.Language, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = AppStrings.officialGuideDownload(language),
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedButton(
                        onClick = { openWebUrl(context, "http://www.srdi.gov.bd/") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("button_open_srdi_web")
                    ) {
                        Icon(Icons.Default.OpenInBrowser, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (language == AppLanguage.BANGLA)
                                "মৃত্তিকা সম্পদ উন্নয়ন ইনস্টিটিউট (SRDI) পোর্টাল"
                            else
                                "Soil Resource Development Institute (SRDI)",
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedButton(
                        onClick = { openWebUrl(context, "http://www.dae.gov.bd/") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("button_open_dae_web")
                    ) {
                        Icon(Icons.Default.OpenInBrowser, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (language == AppLanguage.BANGLA)
                                "কৃষি সম্প্রসারণ অধিদপ্তর (DAE) পোর্টাল"
                            else
                                "Department of Agricultural Extension (DAE)",
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }

        // 9. Calculation Formulas Explained Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("calculation_formulas_card"),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Calculate,
                            contentDescription = null,
                            tint = AgrigreenPrimary,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (language == AppLanguage.BANGLA) "হিসাবের গাণিতিক সূত্রসমূহ (FRG-2024)" else "Mathematical Formulas Used",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    FormulaExplanationRow(
                        title = "১. বাণিজ্যিক সার রূপান্তর সূত্র (Commercial Fertilizer Formula):",
                        formula = "বাণিজ্যিক সারের পরিমাণ (কেজি) = (সুপারিশকৃত পুষ্টি উপাদান কেজি / সারে পুষ্টির শতকরা হার) × ১০০",
                        details = "যেমন: ১০০ কেজি নাইট্রোজেন পেতে হলে ইউরিয়া প্রয়োজন = (১০০ / ৪৬) × ১০০ = ২১৭.৪ কেজি ইউরিয়া।"
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    FormulaExplanationRow(
                        title = "২. সারে পুষ্টি উপাদানের শতকরা হার (Nutrient Percentages in Fertilizers):",
                        formula = "• ইউরিয়া (Urea): ৪৬% N\n• টিএসপি (TSP): ২০% P (৪৬% P2O5)\n• ডিএপি (DAP): ১৮% N + ২০% P\n• এমওপি (MoP): ৫০% K (৬০% K2O)\n• জিপসাম (Gypsum): ১৮% S\n• জিংক সালফেট হেপ্টা (Zinc): ২১% Zn\n• বোরিক এসিড / সলুবোর (Boron): ১৭% B",
                        details = "ডিএপি সার ব্যবহার করলে ডিএপি থেকে প্রাপ্ত ১৮% নাইট্রোজেনের সমপরিমাণ ইউরিয়া স্বয়ংক্রিয়ভাবে বাদ দেওয়া হয়।"
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    FormulaExplanationRow(
                        title = "৩. সমন্বিত পুষ্টি ব্যবস্থাপনা ও জৈব সারের অবদান (INM Manure Credit):",
                        formula = "প্রয়োজনীয় সার (A) = মোট সুপারিশ (B) - গোবর থেকে প্রাপ্ত পুষ্টি (C)",
                        details = "প্রতি ১ টন পচা গোবর প্রয়োগে হেক্টর প্রতি প্রায় ৫ কেজি নাইট্রোজেন, ২.৫ কেজি ফসফরাস এবং ৫ কেজি পটাশিয়াম রাসায়নিক সারের থেকে হ্রাস করা হয়।"
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    FormulaExplanationRow(
                        title = "৪. মাটির উর্বরতা মাত্রা সমন্বয় (Fertility Class Multipliers):",
                        formula = "খুব কম (VL): +২৫% | কম (L): +১০% | মধ্যম (M): ১০০% (ভিত্তি) | অনুকূল (Opt): -২৫% | উচ্চ (H): -৫৫%",
                        details = "মাটি পরীক্ষা বা কৃষি পরিবেশ অঞ্চলের উর্বরতা অনুযায়ী এই গুণক দ্বারা অনুমোদিত সার সমন্বয় করা হয়।"
                    )
                }
            }
        }
    }
}

@Composable
private fun SlidePreviewItem(
    slideNumber: Int,
    title: String,
    bulletPoints: List<String>
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, Color(0xFF7B1FA2).copy(alpha = 0.25f), RoundedCornerShape(12.dp))
            .padding(12.dp)
    ) {
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF7B1FA2)
            )
            Spacer(modifier = Modifier.height(6.dp))
            bulletPoints.forEach { pt ->
                Row(modifier = Modifier.padding(vertical = 1.dp)) {
                    Text(text = "• ", fontWeight = FontWeight.Bold, color = Color(0xFF7B1FA2))
                    Text(
                        text = pt,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface,
                        lineHeight = 17.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun InfographicItem(title: String, description: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, Color(0xFF0288D1).copy(alpha = 0.25f), RoundedCornerShape(12.dp))
            .padding(12.dp)
    ) {
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0288D1)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface,
                lineHeight = 18.sp
            )
        }
    }
}

@Composable
private fun FormulaExplanationRow(title: String, formula: String, details: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(12.dp)
    ) {
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = AgrigreenPrimary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = formula,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
                lineHeight = 18.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = details,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 16.sp
            )
        }
    }
}

private fun openWebUrl(context: Context, url: String) {
    try {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        context.startActivity(intent)
    } catch (e: Exception) {
        Toast.makeText(context, "Could not open browser", Toast.LENGTH_SHORT).show()
    }
}

private fun buildReportContent(result: FertilizerCalculationResult, language: AppLanguage): String {
    val aezDesc = result.detectedAez?.let { "AEZ ${it.aezNumber}: ${it.name} (${it.bengaliName})" } ?: "General"
    return buildString {
        appendLine("==================================================")
        appendLine("BARC FERTILIZER RECOMMENDATION GUIDE 2024 REPORT")
        appendLine("বাংলাদেশ কৃষি গবেষণা কাউন্সিল - সার সুপারিশ প্রতিবেদন")
        appendLine("==================================================")
        appendLine()
        appendLine("ফসল (Crop): ${result.crop.bengaliName} / ${result.crop.name}")
        appendLine("ফসলের শ্রেণি (Category): ${result.crop.category.displayName}")
        appendLine("লক্ষ্যমাত্রা ফলন (Target Yield): ${result.crop.targetYieldText}")
        appendLine("জমির পরিমাণ (Land Area): ${result.landArea} ${result.landUnit.displayName} (${String.format("%.4f", result.areaInHectares)} Hectare)")
        appendLine("কৃষি পরিবেশ অঞ্চল (AEZ): $aezDesc")
        result.detectedAez?.let { aez ->
            appendLine("প্রধান জমির শ্রেণি (Land Type): ${aez.dominantLandTypeBn} / ${aez.dominantLandType}")
            appendLine("মৃত্তিকা প্রোফাইল (Soil Profile): ${aez.generalSoilTypeBn} | বুনট: ${aez.soilTextureBn}")
            appendLine("মাটির পিএইচ (pH): ${aez.phRange} | জৈব পদার্থ: ${aez.organicMatterStatus}")
        }
        appendLine("মাটির উর্বরতা (Soil Fertility): ${result.fertilityClass.displayName}")
        if (result.cowdungTonnesPerHa > 0) {
            appendLine("জৈব সার প্রয়োগ (Organic Manure): ${result.cowdungTonnesPerHa} t/ha")
        }
        appendLine()
        appendLine("--------------------------------------------------")
        appendLine("প্রয়োজনীয় সারের পরিমাণ (FERTILIZER REQUIREMENTS):")
        appendLine("--------------------------------------------------")
        result.items.forEach { item ->
            val bagStr = if (item.bags50kg >= 1f) "${String.format("%.1f", item.bags50kg)} bags" else "< 1 bag"
            appendLine("• ${item.fertilizerType.bengaliName} (${item.fertilizerType.englishName}): ${String.format("%.2f", item.recommendedKg)} কেজি | $bagStr | আনুমানিক খরচ: ৳ ${String.format("%.0f", item.estimatedCostTk)}")
        }
        appendLine()
        appendLine("সর্বমোট আনুমানিক খরচ (Total Cost): ৳ ${String.format("%.0f", result.totalCostTk)} BDT")
        appendLine("মোট ৫০ কেজি বস্তা (Total 50kg Bags): ${String.format("%.1f", result.totalBags50kg)}")
        appendLine()
        appendLine("--------------------------------------------------")
        appendLine("সার প্রয়োগের কিস্তি (SPLIT DOSES):")
        appendLine("--------------------------------------------------")
        result.scheduleDoses.forEachIndexed { idx, dose ->
            appendLine("${idx + 1}. ${dose.bengaliStageName} (${dose.stageName}):")
            appendLine("   সময়: ${dose.timingDescription}")
            if (dose.ureaKg > 0.01f) appendLine("   - ইউরিয়া: ${String.format("%.2f", dose.ureaKg)} কেজি")
            if (dose.tspKg > 0.01f) appendLine("   - টিএসপি/ডিএপি: ${String.format("%.2f", dose.tspKg)} কেজি")
            if (dose.mopKg > 0.01f) appendLine("   - এমওপি (পটাশ): ${String.format("%.2f", dose.mopKg)} কেজি")
            if (dose.gypsumKg > 0.01f) appendLine("   - জিপসাম: ${String.format("%.2f", dose.gypsumKg)} কেজি")
            if (dose.zincKg > 0.01f) appendLine("   - জিংক সালফেট: ${String.format("%.2f", dose.zincKg)} কেজি")
            if (dose.boronKg > 0.01f) appendLine("   - বোরিক এসিড: ${String.format("%.2f", dose.boronKg)} কেজি")
        }
        appendLine()
        appendLine("--------------------------------------------------")
        appendLine("অ্যাপ নির্মাতা (App Builder): Md. Moniruzzaman (https://moniruzjaman.github.io/portfolio/)")
        appendLine("মৃত্তিকা পোর্টাল (Soil Media Portal): https://soil-media-portal.mithun-hstu.workers.dev/")
        appendLine("নোটবুকএলএম (NotebookLM): https://notebook.google.com/notebook/1218c86f-beef-4a00-a0e2-1b2624c12128")
        appendLine("--------------------------------------------------")
        appendLine("বিধিবদ্ধ সতর্কবার্তা (OFFICIAL STATUTORY DISCLAIMER):")
        appendLine(DisclaimerConstants.FULL_STATUTORY_DISCLAIMER_BN)
        appendLine()
        appendLine(DisclaimerConstants.FULL_STATUTORY_DISCLAIMER_EN)
        appendLine("==================================================")
    }
}

private fun buildCsvContent(result: FertilizerCalculationResult): String {
    val aezDesc = result.detectedAez?.let { "AEZ ${it.aezNumber} - ${it.name}" } ?: "General"
    return buildString {
        append("\uFEFF")
        appendLine("Parameter,Value")
        appendLine("\"Crop / ফসল\",\"${result.crop.bengaliName} (${result.crop.name})\"")
        appendLine("\"Crop Category\",\"${result.crop.category.displayName}\"")
        appendLine("\"Target Yield\",\"${result.crop.targetYieldText}\"")
        appendLine("\"Land Area\",\"${result.landArea} ${result.landUnit.displayName}\"")
        appendLine("\"Land Area (Hectares)\",\"${String.format("%.4f", result.areaInHectares)}\"")
        appendLine("\"AEZ (Agro-Ecological Zone)\",\"$aezDesc\"")
        result.detectedAez?.let { aez ->
            appendLine("\"Dominant Land Type\",\"${aez.dominantLandTypeBn} / ${aez.dominantLandType}\"")
            appendLine("\"Soil Texture\",\"${aez.soilTextureBn}\"")
            appendLine("\"Soil pH\",\"${aez.phRange}\"")
            appendLine("\"Organic Matter\",\"${aez.organicMatterStatus}\"")
        }
        appendLine("\"Soil Fertility Class\",\"${result.fertilityClass.displayName}\"")
        appendLine("\"Organic Manure (t/ha)\",\"${result.cowdungTonnesPerHa}\"")
        appendLine()
        appendLine("Fertilizer Name,Nutrient,Recommended (kg),50kg Bags,Estimated Cost (BDT)")
        result.items.forEach { item ->
            appendLine("\"${item.fertilizerType.bengaliName} (${item.fertilizerType.englishName})\",\"${item.nutrientName}\",${String.format("%.2f", item.recommendedKg)},${String.format("%.2f", item.bags50kg)},${String.format("%.2f", item.estimatedCostTk)}")
        }
        appendLine("\"Total Estimated Cost\",,,,\"${String.format("%.2f", result.totalCostTk)}\"")
        appendLine("\"Total 50kg Bags\",,,\"${String.format("%.2f", result.totalBags50kg)}\",")
        appendLine()
        appendLine("Split Application Stage,Timing,Urea (kg),TSP/DAP (kg),MoP (kg),Gypsum (kg),Zinc (kg),Boron (kg)")
        result.scheduleDoses.forEach { dose ->
            appendLine("\"${dose.bengaliStageName} (${dose.stageName})\",\"${dose.timingDescription}\",${String.format("%.2f", dose.ureaKg)},${String.format("%.2f", dose.tspKg)},${String.format("%.2f", dose.mopKg)},${String.format("%.2f", dose.gypsumKg)},${String.format("%.2f", dose.zincKg)},${String.format("%.2f", dose.boronKg)}")
        }
        appendLine()
        appendLine("\"App Builder\",\"Md. Moniruzzaman (https://moniruzjaman.github.io/portfolio/)\"")
        appendLine("\"Soil Media Portal\",\"https://soil-media-portal.mithun-hstu.workers.dev/\"")
        appendLine("\"NotebookLM Knowledge Hub\",\"https://notebook.google.com/notebook/1218c86f-beef-4a00-a0e2-1b2624c12128\"")
        appendLine("\"Disclaimer\",\"${DisclaimerConstants.FULL_STATUTORY_DISCLAIMER_BN.replace("\"", "\"\"")}\"")
    }
}

private fun downloadCsvReport(context: Context, result: FertilizerCalculationResult, language: AppLanguage) {
    try {
        val safeCrop = result.crop.name.replace(" ", "_").replace("/", "_")
        val fileName = "FRG_2024_${safeCrop}_Report.csv"
        val reportsDir = File(context.cacheDir, "reports").apply { mkdirs() }
        val file = File(reportsDir, fileName)

        FileOutputStream(file).use { out ->
            out.write(buildCsvContent(result).toByteArray(StandardCharsets.UTF_8))
        }

        val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
        val sendIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/csv"
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_SUBJECT, "FRG 2024 Fertilizer CSV - ${result.crop.name}")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(sendIntent, "Download / Open CSV Spreadsheet"))
        Toast.makeText(context, AppStrings.fileReadyToast(language, fileName), Toast.LENGTH_LONG).show()
    } catch (e: Exception) {
        Toast.makeText(context, "Export error: ${e.message}", Toast.LENGTH_SHORT).show()
    }
}

private fun downloadTxtReport(context: Context, result: FertilizerCalculationResult, language: AppLanguage) {
    try {
        val safeCrop = result.crop.name.replace(" ", "_").replace("/", "_")
        val fileName = "FRG_2024_${safeCrop}_Calculation.txt"
        val reportsDir = File(context.cacheDir, "reports").apply { mkdirs() }
        val file = File(reportsDir, fileName)

        FileOutputStream(file).use { out ->
            out.write(buildReportContent(result, language).toByteArray(StandardCharsets.UTF_8))
        }

        val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
        val sendIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_SUBJECT, "FRG 2024 Fertilizer Report - ${result.crop.name}")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(sendIntent, "Download / Save Calculation Sheet"))
        Toast.makeText(context, AppStrings.fileReadyToast(language, fileName), Toast.LENGTH_LONG).show()
    } catch (e: Exception) {
        Toast.makeText(context, "Export error: ${e.message}", Toast.LENGTH_SHORT).show()
    }
}

private fun copyReportToClipboard(context: Context, result: FertilizerCalculationResult, language: AppLanguage) {
    try {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("FRG 2024 Fertilizer Report", buildReportContent(result, language))
        clipboard.setPrimaryClip(clip)
        Toast.makeText(context, AppStrings.copiedToClipboardToast(language), Toast.LENGTH_SHORT).show()
    } catch (e: Exception) {
        Toast.makeText(context, "Copy failed: ${e.message}", Toast.LENGTH_SHORT).show()
    }
}

private fun shareFertilizerReport(context: Context, result: FertilizerCalculationResult, language: AppLanguage) {
    val reportText = buildReportContent(result, language)
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_SUBJECT, "FRG 2024 Fertilizer Report - ${result.crop.name}")
        putExtra(Intent.EXTRA_TEXT, reportText)
    }
    context.startActivity(Intent.createChooser(intent, "Share Fertilizer Report"))
}
