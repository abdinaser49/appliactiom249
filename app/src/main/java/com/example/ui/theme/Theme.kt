package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme =
  darkColorScheme(
    primary = EmeraldLight,
    onPrimary = IslamicGreenDark,
    primaryContainer = IslamicGreenPrimary,
    onPrimaryContainer = EmeraldContainerLight,
    secondary = SoftGold,
    onSecondary = IslamicGreenDark,
    secondaryContainer = GoldContainerDark,
    onSecondaryContainer = GoldContainerLight,
    tertiary = EmeraldSecondary,
    onTertiary = Color.Black,
    background = DarkBackground,
    onBackground = DarkTextPrimary,
    surface = DarkSurface,
    onSurface = DarkTextPrimary,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkTextSecondary,
    outline = Color(0xFF334155),
    outlineVariant = Color(0xFF1E293B),
  )

private val LightColorScheme =
  lightColorScheme(
    primary = IslamicGreenPrimary,
    onPrimary = Color.White,
    primaryContainer = EmeraldContainerLight,
    onPrimaryContainer = IslamicGreenDark,
    secondary = SoftGoldDark,
    onSecondary = Color.White,
    secondaryContainer = GoldContainerLight,
    onSecondaryContainer = GoldContainerDark,
    tertiary = IslamicGreenMedium,
    onTertiary = Color.White,
    background = HighDensityBg,
    onBackground = HighDensitySlate900,
    surface = HighDensitySurface,
    onSurface = HighDensitySlate900,
    surfaceVariant = HighDensitySurfaceVariant,
    onSurfaceVariant = HighDensitySlate500,
    outline = HighDensityBorder,
    outlineVariant = HighDensityBorderSubtle,
  )

@Composable
fun MandeqIslamicTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  content: @Composable () -> Unit,
) {
  val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}

