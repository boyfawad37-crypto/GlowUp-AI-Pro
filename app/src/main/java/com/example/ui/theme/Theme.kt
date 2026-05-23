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

private val DarkColorScheme = darkColorScheme(
  primary = GlowAmber,
  secondary = CyberViolet,
  tertiary = NeonCyan,
  background = MidnightBlack,
  surface = DarkGreySurface,
  onPrimary = MidnightBlack,
  onSecondary = androidx.compose.ui.graphics.Color.White,
  onTertiary = MidnightBlack,
  onBackground = androidx.compose.ui.graphics.Color.White,
  onSurface = androidx.compose.ui.graphics.Color.White
)

private val LightColorScheme = lightColorScheme(
  primary = GlowAmber,
  secondary = CyberViolet,
  tertiary = NeonCyan,
  background = MidnightBlack,
  surface = DarkGreySurface,
  onPrimary = MidnightBlack,
  onSecondary = androidx.compose.ui.graphics.Color.White,
  onBackground = androidx.compose.ui.graphics.Color.White,
  onSurface = androidx.compose.ui.graphics.Color.White
)

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = true, // Force Dark theme for premium glow look
  dynamicColor: Boolean = false, // Use our handcrafted luxury palette
  content: @Composable () -> Unit,
) {
  val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
