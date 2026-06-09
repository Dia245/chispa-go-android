package com.example

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.ChispaViewModel
import com.example.ui.LoginScreen
import com.example.ui.MainClientScreen
import com.example.ui.ProspectScreen
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    
    // 1.1 PORTRAIT LOCK (ANTI-ROTATION) MANDATE
    try {
      requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    } catch (e: Exception) {
      Log.e("MainActivity", "Error locking screen orientation to portrait", e)
    }

    // EDGE-TO-EDGE HIGH-QUALITY SYSTEM-BAR TRANSPARENCY
    enableEdgeToEdge()

    // 1.2 RUNTIME PROTECTION TRY-CATCH WRAPPER
    try {
      setContent {
        val chispaVM: ChispaViewModel = viewModel()
        val isDarkMode by chispaVM.isDarkMode.collectAsState()
        MyApplicationTheme(darkTheme = isDarkMode) {
          val currentUserCode by chispaVM.currentUserCode.collectAsState()
          val showProspectScreen by chispaVM.showProspectScreen.collectAsState()

          androidx.compose.foundation.layout.Box(
            modifier = Modifier.fillMaxSize()
          ) {
            if (currentUserCode == null) {
              if (showProspectScreen) {
                ProspectScreen(
                  onBack = { chispaVM.showProspectScreen.value = false },
                  modifier = Modifier.fillMaxSize()
                )
              } else {
                LoginScreen(
                  viewModel = chispaVM,
                  modifier = Modifier.fillMaxSize()
                )
              }
            } else {
              MainClientScreen(
                viewModel = chispaVM,
                modifier = Modifier.fillMaxSize()
              )
            }
          }
        }
      }
    } catch (e: Exception) {
      Log.e("MainActivity", "Fatal rendering crash shield", e)
    }
  }
}

