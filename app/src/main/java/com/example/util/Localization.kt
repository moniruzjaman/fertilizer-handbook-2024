package com.example.util

enum class AppLanguage(val code: String, val label: String) {
    BANGLA("bn", "বাংলা"),
    ENGLISH("en", "English")
}

object AppStrings {

    fun appTitle(lang: AppLanguage) = when (lang) {
        AppLanguage.BANGLA -> "এফআরজি ২০২৪ সার নির্দেশিকা"
        AppLanguage.ENGLISH -> "FRG 2024 Fertilizer Guide"
    }

    fun navCalculator(lang: AppLanguage) = when (lang) {
        AppLanguage.BANGLA -> "ক্যালকুলেটর"
        AppLanguage.ENGLISH -> "Calculator"
    }

    fun navSchedule(lang: AppLanguage) = when (lang) {
        AppLanguage.BANGLA -> "প্রয়োগ সূচি"
        AppLanguage.ENGLISH -> "Schedule"
    }

    fun navDeficiencies(lang: AppLanguage) = when (lang) {
        AppLanguage.BANGLA -> "পুষ্টি ঘাটতি"
        AppLanguage.ENGLISH -> "Deficiencies"
    }

    fun navAiAdvisor(lang: AppLanguage) = when (lang) {
        AppLanguage.BANGLA -> "এআই পরামর্শ"
        AppLanguage.ENGLISH -> "AI Advisor"
    }

    fun navResources(lang: AppLanguage) = when (lang) {
        AppLanguage.BANGLA -> "রিসোর্স ও ডাউনলোড"
        AppLanguage.ENGLISH -> "Resources"
    }

    fun cropCategoryLabel(lang: AppLanguage) = when (lang) {
        AppLanguage.BANGLA -> "ফসলের শ্রেণি নির্বাচন করুন"
        AppLanguage.ENGLISH -> "Select Crop Category"
    }

    fun cropVarietyLabel(lang: AppLanguage) = when (lang) {
        AppLanguage.BANGLA -> "নির্দিষ্ট ফসল নির্বাচন করুন"
        AppLanguage.ENGLISH -> "Select Crop Variety"
    }

    fun landAreaLabel(lang: AppLanguage) = when (lang) {
        AppLanguage.BANGLA -> "জমির পরিমাণ নির্ধারণ করুন"
        AppLanguage.ENGLISH -> "Cultivated Land Area"
    }

    fun landAreaSubtitle(lang: AppLanguage) = when (lang) {
        AppLanguage.BANGLA -> "প্রমিত বাংলাদেশ কৃষি এককে জমির আয়তন দিন"
        AppLanguage.ENGLISH -> "Specify size in standard Bangladesh agricultural units"
    }

    fun soilFertilityLabel(lang: AppLanguage) = when (lang) {
        AppLanguage.BANGLA -> "মাটির উর্বরতা মাত্রা (মাটি পরীক্ষা ভিত্তিক)"
        AppLanguage.ENGLISH -> "Soil Fertility Status (Soil-Test Based)"
    }

    fun organicManureLabel(lang: AppLanguage) = when (lang) {
        AppLanguage.BANGLA -> "জৈব সার / পচা গোবর (টন/হেক্টর)"
        AppLanguage.ENGLISH -> "Organic Manure / Cowdung (t/ha)"
    }

    fun dapToggleLabel(lang: AppLanguage, useDap: Boolean) = when (lang) {
        AppLanguage.BANGLA -> if (useDap) "ফসফরাসের উৎস: ডিএপি (১৮% নাইট্রোজেন + ২০% ফসফরাস)" else "ফসফরাসের উৎস: টিএসপি (২০% ফসফরাস)"
        AppLanguage.ENGLISH -> if (useDap) "Phosphorus Source: DAP (18% N + 20% P)" else "Phosphorus Source: TSP (20% P)"
    }

    fun estCostLabel(lang: AppLanguage) = when (lang) {
        AppLanguage.BANGLA -> "আনুমানিক সার ক্রয় খরচ"
        AppLanguage.ENGLISH -> "Estimated Fertilizer Cost"
    }

    fun totalBagsLabel(lang: AppLanguage) = when (lang) {
        AppLanguage.BANGLA -> "মোট ৫০ কেজি বস্তা"
        AppLanguage.ENGLISH -> "Total 50kg Bags"
    }

    fun splitScheduleTitle(lang: AppLanguage) = when (lang) {
        AppLanguage.BANGLA -> "সার প্রয়োগের কিস্তি ও সময়সূচি"
        AppLanguage.ENGLISH -> "Split Application Timeline"
    }

    fun deficiencyTitle(lang: AppLanguage) = when (lang) {
        AppLanguage.BANGLA -> "পুষ্টি উপাদানের অভাবজনিত লক্ষণ ও প্রতিকার"
        AppLanguage.ENGLISH -> "Nutrient Deficiency Field Diagnosis"
    }

    fun autoGpsTitle(lang: AppLanguage) = when (lang) {
        AppLanguage.BANGLA -> "স্বয়ংক্রিয় লাইভ লোকেশন ও এইজেড (AEZ)"
        AppLanguage.ENGLISH -> "Auto Realtime Location & AEZ"
    }

    fun autoGpsSubtitle(lang: AppLanguage) = when (lang) {
        AppLanguage.BANGLA -> "জিপিএস অবস্থান অনুযায়ী স্বয়ংক্রিয় কৃষি পরিবেশ অঞ্চল চিহ্নিত"
        AppLanguage.ENGLISH -> "Agro-Ecological Zone auto-detected from GPS"
    }

    fun disclaimerBannerTitle(lang: AppLanguage) = when (lang) {
        AppLanguage.BANGLA -> "🏛️ ডিএই (DAE) ও বার্ক বিধিবদ্ধ সতর্কবার্তা"
        AppLanguage.ENGLISH -> "🏛️ MANDATORY DAE ADVISORY & BARC DISCLAIMER"
    }

    fun disclaimerBannerText(lang: AppLanguage) = when (lang) {
        AppLanguage.BANGLA -> "এই হিসাবটি বার্ক (BARC) সার সুপারিশমালা হাতবই ২০২৪-এর মডেল অনুযায়ী অনুমিত। জমিতে সার প্রয়োগের পূর্বে অবশ্যই আপনার এলাকার উপসহকারী কৃষি কর্মকর্তা (SAAO) বা উপজেলা কৃষি অফিসের (DAE) সাথে পরামর্শ করুন এবং SRDI ল্যাবে মাটি পরীক্ষা করান।"
        AppLanguage.ENGLISH -> "Estimations are based on BARC FRG-2024 regional models. Always consult your local Sub-Assistant Agriculture Officer (SAAO) or Upazila Agriculture Office (DAE) and test soil with SRDI before bulk application."
    }

    fun downloadReportButton(lang: AppLanguage) = when (lang) {
        AppLanguage.BANGLA -> "সার সুপারিশ রিপোর্ট শেয়ার করুন"
        AppLanguage.ENGLISH -> "Share Fertilizer Report"
    }

    fun downloadCsvButton(lang: AppLanguage) = when (lang) {
        AppLanguage.BANGLA -> "CSV স্প্রেডশিট ডাউনলোড (Excel/Sheets)"
        AppLanguage.ENGLISH -> "Download CSV Spreadsheet (Excel)"
    }

    fun downloadTxtButton(lang: AppLanguage) = when (lang) {
        AppLanguage.BANGLA -> "প্রিন্টযোগ্য হিসাব রিপোর্ট ডাউনলোড (.TXT)"
        AppLanguage.ENGLISH -> "Download Printable Report (.TXT)"
    }

    fun copyClipboardButton(lang: AppLanguage) = when (lang) {
        AppLanguage.BANGLA -> "হিসাব ক্লিপবোর্ডে কপি করুন"
        AppLanguage.ENGLISH -> "Copy Calculation Summary"
    }

    fun copiedToClipboardToast(lang: AppLanguage) = when (lang) {
        AppLanguage.BANGLA -> "সার হিসাব ক্লিপবোর্ডে কপি করা হয়েছে!"
        AppLanguage.ENGLISH -> "Calculation copied to clipboard!"
    }

    fun fileReadyToast(lang: AppLanguage, filename: String) = when (lang) {
        AppLanguage.BANGLA -> "$filename ডাউনলোড ও প্রস্তুত হয়েছে!"
        AppLanguage.ENGLISH -> "$filename is ready and downloaded!"
    }

    fun officialGuideDownload(lang: AppLanguage) = when (lang) {
        AppLanguage.BANGLA -> "বার্ক এফআরজি ২০২৪ মূল নির্দেশিকা (অফিশিয়াল ওয়েবসাইট)"
        AppLanguage.ENGLISH -> "BARC Official FRG-2024 Guide (Official Portal)"
    }
}
