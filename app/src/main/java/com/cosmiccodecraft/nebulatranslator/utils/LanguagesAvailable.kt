package com.cosmiccodecraft.nebulatranslator.utils

import com.google.mlkit.nl.translate.TranslateLanguage
import java.util.Locale

object LanguagesAvailable {

    val localLangMap = hashMapOf(
        "English" to Locale.ENGLISH,
        "Chinese" to Locale.CHINESE,
        "French" to Locale.FRENCH,
        "German" to Locale.GERMAN,
        "Italian" to Locale.ITALIAN,
        "Japanese" to Locale.JAPANESE,
        "Korean" to Locale.KOREAN,
    )

    val translateLanguageMap = hashMapOf(
        "English" to TranslateLanguage.ENGLISH,
        "Chinese" to TranslateLanguage.CHINESE,
        "French" to TranslateLanguage.FRENCH,
        "German" to TranslateLanguage.GERMAN,
        "Italian" to TranslateLanguage.ITALIAN,
        "Japanese" to TranslateLanguage.JAPANESE,
        "Korean" to TranslateLanguage.KOREAN,
    )

    enum class Languages(val language : String){
        English("English"),
        Chinese("Chinese"),
        French("French"),
        German("German"),
        Italian("Italian"),
        Japanese("Japanese"),
        Korean("Korean"),
    }
}