package com.cosmiccodecraft.nebulatranslator

import android.content.Context
import android.content.Intent
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.cosmiccodecraft.nebulatranslator.screens.UIEvents
import com.cosmiccodecraft.nebulatranslator.screens.translation_screen.TranslationPageState
import com.cosmiccodecraft.nebulatranslator.utils.ConnectionState
import com.cosmiccodecraft.nebulatranslator.utils.LanguagesAvailable.Languages
import com.cosmiccodecraft.nebulatranslator.utils.LanguagesAvailable.localLangMap
import com.cosmiccodecraft.nebulatranslator.utils.LanguagesAvailable.translateLanguageMap
import com.cosmiccodecraft.nebulatranslator.utils.currentConnectivityState
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.Locale

class SharedViewModel : ViewModel() {

    var sourceText by mutableStateOf("")
        private set

    var targetText by mutableStateOf("")
        private set

    var sourceLanguageModel: Languages by mutableStateOf(Languages.English)

    var targetLanguageModel: Languages by mutableStateOf(Languages.English)


    private val _state = MutableStateFlow(TranslationPageState())
    val state: StateFlow<TranslationPageState> = _state


    private var textToSpeechEngine: TextToSpeech? = null


    fun onEvent(event: UIEvents) {
        when (event) {
            is UIEvents.OnTranslateButtonClicked -> {
                if (sourceText.trim().isBlank()) {
                    Toast.makeText(event.context, "Text cannot be blank", Toast.LENGTH_SHORT).show()
                    return
                } else {
                    translateFromSourceToTarget(event.context)
                }
            }

            is UIEvents.OnTextToSpeechClicked -> {
                if (targetText.trim().isBlank()) {
                    Toast.makeText(event.context, "Text cannot be blank", Toast.LENGTH_SHORT).show()
                    return
                } else {
                    textToSpeech(event.context)
                }
            }

            UIEvents.OnActivityEnd -> {
                if (textToSpeechEngine != null) {
                    textToSpeechEngine!!.stop()
                }
                return
            }

            UIEvents.OnActivityPause -> {
                if (textToSpeechEngine != null) {
                    textToSpeechEngine!!.shutdown()
                }
                return
            }

            is UIEvents.OnSourceLanguageModelChange -> {
                sourceLanguageModel = event.model
            }

            is UIEvents.OnTargetLanguageModelChange -> {
                targetLanguageModel = event.model
            }

            UIEvents.OnInvertModelsClicked -> {
                val temp = sourceLanguageModel
                sourceLanguageModel = targetLanguageModel
                targetLanguageModel = temp
                val tempText = sourceText
                sourceText = targetText
                targetText = tempText
            }

            is UIEvents.OnSourceTextFieldChange -> {
                sourceText = event.text
            }

            is UIEvents.OnTargetTextFieldChange -> {
                targetText = event.text
            }

            is UIEvents.OnSpeakToTextButtonClicked -> {
                if (!SpeechRecognizer.isRecognitionAvailable(event.context)) {
                    Toast.makeText(
                        event.context,
                        "Speech is not available currently.",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
                    intent.putExtra(
                        RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                        RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH
                    )
                    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())

                    intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak Something")

                    event.launcher.launch(intent)
                }
            }

            UIEvents.DismissError -> {
                _state.value = TranslationPageState(error = "")
            }

            UIEvents.DismissDownloadDialog -> {
                _state.value = TranslationPageState(isDownloading = false)
            }

            UIEvents.DismissConnectionDialog -> {
                _state.value = TranslationPageState(showConnectionError = false)
            }
        }
    }

    private fun translateFromSourceToTarget(context: Context) {
        val sourceLangModel = translateLanguageMap[sourceLanguageModel.language]!!
        val targetLangModel = translateLanguageMap[targetLanguageModel.language]!!
        val options = TranslatorOptions.Builder()
            .setSourceLanguage(sourceLangModel)
            .setTargetLanguage(targetLangModel)
            .build()
        val translator = Translation.getClient(options)

        val conditions = DownloadConditions.Builder()
            .requireWifi()
            .build()

        _state.value = TranslationPageState(isDownloading = true)
        translator.downloadModelIfNeeded(conditions)
            .addOnSuccessListener {
                _state.value = TranslationPageState(isDownloading = false)
                translator.translate(sourceText.trim())
                    .addOnSuccessListener { translatedText ->
                        targetText = translatedText
                    }
                    .addOnFailureListener {
                        Toast.makeText(context, "Some error occurred", Toast.LENGTH_SHORT).show()
                        Log.d("translation error", it.toString())
                    }
            }
            .addOnFailureListener { exception ->
                _state.value = TranslationPageState(error = exception.toString())
                Toast.makeText(
                    context,
                    "Model download fail, please check your internet connection",
                    Toast.LENGTH_SHORT
                ).show()
                Log.d("download error", exception.toString())
            }

    }

    private fun textToSpeech(context: Context) {
        // if already speaking stop it
        textToSpeechEngine?.stop()
        if (
            context.currentConnectivityState == ConnectionState.CellularAvailable ||
            context.currentConnectivityState == ConnectionState.WifiAvailable
        ) {
            textToSpeechEngine = TextToSpeech(
                context
            ) {
                if (it == TextToSpeech.SUCCESS) {
                    textToSpeechEngine?.let { txtTospch ->
                        txtTospch.language = localLangMap[targetLanguageModel.language]!!
                        txtTospch.speak(targetText, TextToSpeech.QUEUE_FLUSH, null, null)
                    }
                }
            }
        } else {
            _state.value = TranslationPageState(showConnectionError = true)
        }
    }
}
