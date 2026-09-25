// SPDX-License-Identifier: Apache-2.0
// Copyright 2026 OFFPAY

package com.offpay.app.ui.theme

import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color

data class OFFPAYAccentTheme(
    val primary: Color,
    val primaryDark: Color,
    val headerGradientStart: Color,
    val headerGradientEnd: Color,
    val accent: Color,
    val accentLight: Color
)

val BlueAccentTheme = OFFPAYAccentTheme(
    primary = OFFPAYBrandPrimary,
    primaryDark = OFFPAYBrandVariant,
    headerGradientStart = OFFPAYBrandLight,
    headerGradientEnd = OFFPAYBrandPrimary,
    accent = OFFPAYBrandPrimary,
    accentLight = OFFPAYBrandLight
)

val LocalOFFPAYAccentTheme = compositionLocalOf { BlueAccentTheme }
