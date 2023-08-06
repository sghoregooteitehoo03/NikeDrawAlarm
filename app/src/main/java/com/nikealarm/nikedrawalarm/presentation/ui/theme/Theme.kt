package com.plcoding.cryptocurrencyappyt.presentation.ui.theme

import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable

private val DarkColorPalette = darkColors(
    primary = Black,
    primaryVariant = Black,
    onPrimary = White,
    secondary = White,
    secondaryVariant = White,
    onSecondary = Black,
    background = DarkBackground,
    onBackground = White
)

private val LightColorPalette = lightColors(
    primary = White,
    primaryVariant = White,
    onPrimary = Black,
    secondary = Black,
    secondaryVariant = Black,
    onSecondary = White,
    background = White,
    onBackground = Black
)

@Composable
fun NikeDrawAssistant(darkTheme: Boolean = false, content: @Composable() () -> Unit) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    MaterialTheme(
        colors = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}