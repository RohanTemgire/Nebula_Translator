package com.cosmiccodecraft.nebulatranslator.screens.translation_screen

data class TranslationPageState(
    val isDownloading: Boolean = false,
    val error: String = "",
    val showConnectionError : Boolean = false
)
