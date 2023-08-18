package com.cosmiccodecraft.nebulatranslator.screens

import android.content.Context
import android.content.Intent
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.result.ActivityResult
import com.cosmiccodecraft.nebulatranslator.utils.LanguagesAvailable.Languages

sealed class UIEvents {
    data class OnTranslateButtonClicked(val context: Context) : UIEvents()
    data class OnTextToSpeechClicked(val context: Context) : UIEvents()
    object OnActivityPause : UIEvents()
    object OnActivityEnd : UIEvents()
    data class OnSourceLanguageModelChange(val model: Languages) : UIEvents()
    data class OnTargetLanguageModelChange(val model: Languages) : UIEvents()
    object OnInvertModelsClicked : UIEvents()
    data class OnSourceTextFieldChange(val text: String) : UIEvents()
    data class OnTargetTextFieldChange(val text: String) : UIEvents()

    data class OnSpeakToTextButtonClicked(
        val launcher: ManagedActivityResultLauncher<Intent, ActivityResult>,
        val context: Context
    ) : UIEvents()

    object DismissError : UIEvents()
    object DismissDownloadDialog : UIEvents()
    object DismissConnectionDialog : UIEvents()
}
