package com.cosmiccodecraft.nebulatranslator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.cosmiccodecraft.nebulatranslator.screens.Navigation
import com.cosmiccodecraft.nebulatranslator.screens.UIEvents
import com.cosmiccodecraft.nebulatranslator.ui.theme.NebulaTranslatorTheme

class MainActivity : ComponentActivity() {
    private val viewModel: SharedViewModel by viewModels()
    private lateinit var navHost : NavHostController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            navHost = rememberNavController()
            NebulaTranslatorTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Navigation(viewModel = viewModel, navHost = navHost)
                }
            }
        }
    }

    override fun onPause() {
        viewModel.onEvent(UIEvents.OnActivityPause)
        super.onPause()
    }

    override fun onDestroy() {
        viewModel.onEvent(UIEvents.OnActivityEnd)
        super.onDestroy()
    }
}