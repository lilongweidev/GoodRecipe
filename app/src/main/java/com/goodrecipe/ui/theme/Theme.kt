package com.goodrecipe.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val Orange400 = Color(0xFFFF7043)
val Orange600 = Color(0xFFE64A19)
val Orange100 = Color(0xFFFFCCBC)
val Brown800 = Color(0xFF4E342E)
val Cream50  = Color(0xFFFFF8F5)

private val LightColorScheme = lightColorScheme(
    primary = Orange400,
    onPrimary = Color.White,
    primaryContainer = Orange100,
    onPrimaryContainer = Brown800,
    secondary = Brown800,
    onSecondary = Color.White,
    background = Cream50,
    surface = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
)

private val DarkColorScheme = darkColorScheme(
    primary = Orange400,
    onPrimary = Color.White,
    primaryContainer = Orange600,
    onPrimaryContainer = Color.White,
    secondary = Orange100,
    onSecondary = Brown800,
    background = Color(0xFF1A1210),
    surface = Color(0xFF2C1F1A),
    onBackground = Color(0xFFF5E6DF),
    onSurface = Color(0xFFF5E6DF),
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
