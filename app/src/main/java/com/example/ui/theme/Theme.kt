package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme =
  darkColorScheme(
    primary = RoyalBlue,
    secondary = SkyBlue,
    tertiary = GoldYellow,
    background = DarkBlue,
    surface = DarkBlue,
    onPrimary = DarkWhite,
    onSecondary = DarkWhite,
    onBackground = BackgroundGrey,
    onSurface = BackgroundGrey
  )

private val LightColorScheme =
  lightColorScheme(
    primary = RoyalBlue,
    secondary = SkyBlue,
    tertiary = GoldYellow,
    background = BackgroundGrey,
    surface = DarkWhite,
    onPrimary = DarkWhite,
    onSecondary = DarkWhite,
    onBackground = DarkBlue,
    onSurface = DarkBlue
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  // Set default to false to strictly respect Chispa Go fintech brand Identity
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  val colorScheme =
    when {
      dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
        val context = LocalContext.current
        if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
      }

      darkTheme -> DarkColorScheme
      else -> LightColorScheme
    }

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}

