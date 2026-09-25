// SPDX-License-Identifier: Apache-2.0
// Copyright 2026 OFFPAY

package com.offpay.app.ui.theme

import androidx.compose.ui.graphics.Color
import com.offpay.app.data.TransactionStatus

// Matte / Dull Blue Brand Palette
val OFFPAYBrandPrimary = Color(0xFF4A6572)       // Muted/Dull Blue
val OFFPAYBrandVariant = Color(0xFF344955)       // Darker Matte Blue
val OFFPAYBrandLight = Color(0xFF7A93A0)         // Soft Blue Gray
val OFFPAYBrandAccent = Color(0xFFF9AA33)        // Muted Orange/Gold for contrast (optional, keeps UI popping without being loud)

// Neutral Palette (Dark mode optimized)
val OFFPAYBlack = Color(0xFF101214)              // Very dark slate, almost black
val OFFPAYDarkGray = Color(0xFF1E2428)           // Elevated dark background
val OFFPAYMediumGray = Color(0xFF2C353B)         // Slightly lighter gray-blue
val OFFPAYLightGray = Color(0xFF3A464D)          // Outlines and dividers
val OFFPAYOutlineGray = Color(0xFF4A565C)        // Stronger outlines
val OFFPAYDisabledGray = Color(0xFF5A666D)

// Text Colors
val OFFPAYTextWhite = Color(0xFFF0F4F8)          // Primary text (off-white/blueish)
val OFFPAYTextLightGray = Color(0xFFCFD8DC)      // Secondary text
val OFFPAYTextGray = Color(0xFF90A4AE)           // Tertiary text
val OFFPAYTextPale = Color(0xFF78909C)           // Disabled/Pale text

// Status Colors
val OFFPAYStatusSuccess = Color(0xFF4CAF50)      // Kept standard green for success
val OFFPAYStatusError = Color(0xFFD32F2F)        // Kept standard red for error
val OFFPAYStatusWarning = Color(0xFFFBC02D)      // Kept standard yellow for warning
val OFFPAYAccentGreen = Color(0xFF388E3C)        // Accessible green accent
val OFFPAYAccentGreenBright = Color(0xFF4CAF50)  // Light green

// UI specific semantics
val OFFPAYSurfaceDim = Color(0xFF15191C)
val OFFPAYSurfaceBright = Color(0xFF252C32)

// Card Colors
val OFFPAYCardBackground = Color(0xFFE8E8E8)
val OFFPAYCardText = Color(0xFF000000)
val OFFPAYCardSubtext = Color(0xFF4A4A4A)

// Status colors
val OFFPAYStatusPending = Color(0xFFFBC02D)
val OFFPAYStatusSent = Color(0xFF388E3C)
val OFFPAYStatusFailed = Color(0xFFD32F2F)

// Text
val OFFPAYTextWarning = Color(0xFFFBC02D)
val OFFPAYTextSuccess = Color(0xFF4CAF50)

fun statusColor(status: String): Color = when (status.uppercase()) {
    TransactionStatus.SUCCESS, "SUCCESSFUL", "COMPLETED" -> OFFPAYStatusSuccess
    TransactionStatus.UNVERIFIED, TransactionStatus.NEEDS_REVIEW, TransactionStatus.PENDING -> OFFPAYStatusWarning
    TransactionStatus.FAILED, "DECLINED", TransactionStatus.CANCELLED -> OFFPAYStatusError
    else -> OFFPAYDisabledGray
}
