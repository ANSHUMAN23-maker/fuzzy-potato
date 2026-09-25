// SPDX-License-Identifier: Apache-2.0
// Copyright 2026 OFFPAY

package com.offpay.app.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = OFFPAYBrandPrimary,
    secondary = OFFPAYBrandVariant,
    tertiary = OFFPAYBrandLight,
    background = OFFPAYBlack,
    surface = OFFPAYDarkGray,
    onPrimary = OFFPAYTextWhite,
    onSecondary = OFFPAYTextWhite,
    onTertiary = OFFPAYTextWhite,
    onBackground = OFFPAYTextWhite,
    onSurface = OFFPAYTextLightGray,
    error = OFFPAYStatusError,
    onError = OFFPAYTextWhite
)

@Composable
fun OFFPAYTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = false
        }
    }

    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
