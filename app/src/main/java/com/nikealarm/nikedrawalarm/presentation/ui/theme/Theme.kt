package com.nikealarm.nikedrawalarm.presentation.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import com.plcoding.cryptocurrencyappyt.presentation.ui.theme.Black
import com.plcoding.cryptocurrencyappyt.presentation.ui.theme.DarkBackground
import com.plcoding.cryptocurrencyappyt.presentation.ui.theme.Shapes
import com.plcoding.cryptocurrencyappyt.presentation.ui.theme.Typography
import com.plcoding.cryptocurrencyappyt.presentation.ui.theme.White

private val DarkColorPalette = darkColorScheme(
    primary = Black,
    primaryContainer = Black,
    onPrimary = White,
    secondary = White,
    secondaryContainer = White,
    onSecondary = Black,
    background = DarkBackground,
    onBackground = White
)

private val LightColorPalette = lightColorScheme(
    primary = White,
    primaryContainer = White,
    onPrimary = Black,
    secondary = Black,
    secondaryContainer = Black,
    onSecondary = White,
    background = White,
    onBackground = Black
)

@Composable
fun NikeDrawAssistant(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable() () -> Unit) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    MaterialTheme(
        colorScheme = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}