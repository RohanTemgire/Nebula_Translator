package com.cosmiccodecraft.nebulatranslator.screens.translation_screen

import android.content.Context
import android.content.Intent
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.result.ActivityResult
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.cosmiccodecraft.nebulatranslator.R
import com.cosmiccodecraft.nebulatranslator.SharedViewModel
import com.cosmiccodecraft.nebulatranslator.screens.UIEvents
import com.cosmiccodecraft.nebulatranslator.ui.theme.iconColor
import com.cosmiccodecraft.nebulatranslator.utils.ConnectionState
import com.cosmiccodecraft.nebulatranslator.utils.currentConnectivityState

@Composable
fun TranslationPageContent(
    viewModel: SharedViewModel,
    context : Context,
    state : TranslationPageState,
    launcher : ManagedActivityResultLauncher<Intent, ActivityResult>
){
    if (state.isDownloading) {
        AlertDialog(
            onDismissRequest = { /*TODO*/ },
            title = { Text(text = stringResource(id = R.string.model_is_being_downloaded)) },
            text = {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.Start
                ) {
                    if (context.currentConnectivityState != ConnectionState.WifiAvailable) {
                        Text(text = stringResource(id = R.string.not_connected_to_wifi))
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                    Text(text = stringResource(id = R.string.turn_on_wifi_request))
                    Spacer(modifier = Modifier.height(8.dp))
                    CircularProgressIndicator()
                }
            },
            confirmButton = {
                Button(onClick = { viewModel.onEvent(UIEvents.DismissDownloadDialog) }) {
                    Text(text = stringResource(id = R.string.ok))
                }
            }
        )
    }

    if (state.error.isNotBlank()) {
        AlertDialog(
            onDismissRequest = { /*TODO*/ },
            title = { Text(text = stringResource(id = R.string.turn_on_wifi_request)) },
            text = { CircularProgressIndicator() },
            confirmButton = {
                Button(onClick = { viewModel.onEvent(UIEvents.DismissError) }) {
                    Text(text = stringResource(id = R.string.ok))
                }
            }
        )
    }

    if (state.showConnectionError) {
        AlertDialog(
            onDismissRequest = { /*TODO*/ },
            text = { Text(text = stringResource(id = R.string.turn_on_network_connectivity)) },
            confirmButton = {
                Button(onClick = { viewModel.onEvent(UIEvents.DismissConnectionDialog) }) {
                    Text(text = stringResource(id = R.string.ok))
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Select source language:", fontWeight = FontWeight.Bold)
            SelectLanguageDropdown(
                language = viewModel.sourceLanguageModel,
                onLanguageSelected = {
                    viewModel.onEvent(
                        UIEvents.OnSourceLanguageModelChange(it)
                    )
                }
            )
        }

        Spacer(modifier = Modifier.height(15.dp))

        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp),
            value = viewModel.sourceText,
            onValueChange = { viewModel.onEvent(UIEvents.OnSourceTextFieldChange(it)) },
            maxLines = 5,
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Sentences,
                imeAction = ImeAction.Done,
                autoCorrect = true,
                keyboardType = KeyboardType.Text
            )
        )

        Spacer(modifier = Modifier.height(15.dp))

        Row(modifier = Modifier.fillMaxWidth()) {
            OutlinedButton(
                onClick = {
                    viewModel.onEvent(UIEvents.OnSpeakToTextButtonClicked(launcher, context))
                },
                modifier = Modifier.weight(0.4f)
            ) {

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.speak),
                        contentDescription = "Speak"
                    )
                    Text(text = "Use voice")
                }
            }

            Spacer(modifier = Modifier.width(10.dp))

            Button(
                onClick = {
                    viewModel.onEvent(
                        UIEvents.OnTranslateButtonClicked(context = context)
                    )
                },
                modifier = Modifier.weight(0.4f)
            ) {
                Text("Translate")
            }
            IconButton(
                onClick = { viewModel.onEvent(UIEvents.OnInvertModelsClicked) },
                modifier = Modifier.weight(0.2f)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.change_models),
                    contentDescription = "Invert models"
                )
            }
        }

        Spacer(modifier = Modifier.height(15.dp))

        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Select target language:", fontWeight = FontWeight.Bold)
            SelectLanguageDropdown(
                language = viewModel.targetLanguageModel,
                onLanguageSelected = {
                    viewModel.onEvent(
                        UIEvents.OnTargetLanguageModelChange(it)
                    )
                }
            )
        }

        Spacer(modifier = Modifier.height(15.dp))

        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp),
            value = viewModel.targetText,
            onValueChange = { viewModel.onEvent(UIEvents.OnTargetTextFieldChange(it)) },
            maxLines = 5,
            enabled = false,
            leadingIcon = {
                Icon(
                    painter = painterResource(R.drawable.text_to_speech),
                    contentDescription = "Text to Speech",
                    tint = MaterialTheme.colorScheme.iconColor,
                    modifier = Modifier.clickable {
                        viewModel.onEvent(UIEvents.OnTextToSpeechClicked(context))
                    }
                )
            },
            colors = OutlinedTextFieldDefaults.colors(
                disabledBorderColor = MaterialTheme.colorScheme.onSurface,
                disabledTextColor = MaterialTheme.colorScheme.onSurface
            )
        )
    }
}