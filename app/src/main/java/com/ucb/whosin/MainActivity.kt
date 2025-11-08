package com.ucb.whosin

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.ucb.whosin.features.event.presentation.EventScreen
import com.ucb.whosin.ui.theme.WhosinTheme
import io.sentry.Sentry

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    // waiting for view to draw to better represent a captured error with a screenshot
    findViewById<android.view.View>(android.R.id.content).viewTreeObserver.addOnGlobalLayoutListener {
      try {
        throw Exception("This app uses Sentry! :)")
      } catch (e: Exception) {
        Sentry.captureException(e)
      }
    }

        enableEdgeToEdge()
        setContent {
            WhosinTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    EventScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}