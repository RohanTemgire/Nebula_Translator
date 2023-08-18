package com.cosmiccodecraft.nebulatranslator.screens.translation_screen

import android.annotation.SuppressLint
import android.app.Activity
import android.speech.RecognizerIntent
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.cosmiccodecraft.nebulatranslator.R
import com.cosmiccodecraft.nebulatranslator.SharedViewModel
import com.cosmiccodecraft.nebulatranslator.screens.UIEvents


@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun TranslationPage(viewModel: SharedViewModel) {
    val context = LocalContext.current
    val state by viewModel.state.collectAsState()

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = { result ->
            if (result.resultCode == Activity.RESULT_OK && result.data != null) {
                val data = result.data!!.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                viewModel.onEvent(UIEvents.OnSourceTextFieldChange(data!![0].toString()))
            } else {
                Toast.makeText(context, result.resultCode.toString(), Toast.LENGTH_LONG).show()
            }
        }
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = stringResource(id = R.string.app_name),color = MaterialTheme.colorScheme.onPrimary)
                },
                colors = TopAppBarDefaults.largeTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            )
        },
        content = {
            TranslationPageContent(
                viewModel = viewModel,
                context = context,
                state = state,
                launcher = launcher
            )
        }
    )
}