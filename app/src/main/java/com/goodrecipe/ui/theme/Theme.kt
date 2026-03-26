package com.goodrecipe.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val OceanBlue = Color(0xFF2B7BFF)
val DeepBlue = Color(0xFF0B1A3A)
val BlueSurface = Color(0xFFF3F7FF)
val BlueContainer = Color(0xFFDDE9FF)
val BlueText = Color(0xFF0E1A2B)

private val LightColorScheme = lightColorScheme(
    primary = OceanBlue,
    onPrimary = Color.White,
    primaryContainer = BlueContainer,
    onPrimaryContainer = BlueText,
    secondary = Color(0xFF2E5EA8),
    onSecondary = Color.White,
    background = BlueSurface,
    surface = Color.White,
    onBackground = BlueText,
    onSurface = BlueText,
    outlineVariant = Color(0xFFB9CCEE)
)

private val DarkColorScheme = darkColorScheme(
    primary = OceanBlue,
    onPrimary = Color.White,
    primaryContainer = Color(0xFF14315F),
    onPrimaryContainer = Color.White,
    secondary = Color(0xFF84B0FF),
    onSecondary = DeepBlue,
    background = Color(0xFF071226),
    surface = Color(0xFF0E1A33),
    onBackground = Color(0xFFDDE8FF),
    onSurface = Color(0xFFDDE8FF),
    outlineVariant = Color(0xFF2A426A)
)

@Composable
fun GoodRecipeTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography(),
        content = content
    )
}
