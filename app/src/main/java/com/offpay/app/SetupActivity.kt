// SPDX-License-Identifier: Apache-2.0
// Copyright 2026 OFFPAY

package com.offpay.app

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.SimCard
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.*
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.offpay.app.helpers.SetupHelper
import com.offpay.app.ui.theme.BlueAccentTheme
import com.offpay.app.ui.theme.OFFPAYDarkGray
import com.offpay.app.ui.theme.OFFPAYDisabledGray
import com.offpay.app.ui.theme.OFFPAYLightGray
import com.offpay.app.ui.theme.OFFPAYMediumGray
import com.offpay.app.ui.theme.OFFPAYSurfaceDim
import com.offpay.app.ui.theme.OFFPAYTextLightGray
import com.offpay.app.ui.theme.OFFPAYTheme
import com.offpay.app.ui.theme.LocalOFFPAYAccentTheme
import com.offpay.app.ui.theme.OFFPAYBlack

enum class SetupStep { BANK, SIM, DISCLAIMER }

class SetupActivity : ComponentActivity() {
    private lateinit var setupHelper: SetupHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize setup helper
        setupHelper = SetupHelper(
            this,
            object : SetupHelper.UICallback {
                override fun showToast(message: String) {
                    runOnUiThread { Toast.makeText(this@SetupActivity, message, Toast.LENGTH_LONG).show() }
                }

                override fun navigateToTestConfiguration() {
                    val intent = Intent(this@SetupActivity, TestConfigurationActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }
        )

        setTheme(R.style.Theme_OFFPAY)
        // Edge-to-edge: Compose insets are the single source of padding (see MainActivity).
        androidx.core.view.WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            CompositionLocalProvider(LocalOFFPAYAccentTheme provides BlueAccentTheme) {
                OFFPAYTheme {
                    SetupScreen(setupHelper = setupHelper)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SetupScreen(setupHelper: SetupHelper) {
    // Step tracker
    var currentStep by remember { mutableStateOf(SetupStep.BANK) }

    // Form data states
    var selectedBank by remember { mutableStateOf("") }
    var selectedPrimarySim by remember { mutableStateOf("") }
    var selectedSecondarySim by remember { mutableStateOf("") }
    var isDualSimEnabled by remember { mutableStateOf(false) }

    val banks = setupHelper.getBanks()
    val simCarriers = setupHelper.getSimCarriers()
    val secondarySimOptions = setupHelper.getSecondarySimOptions(selectedPrimarySim)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(OFFPAYBlack)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = 420.dp)
                .align(Alignment.Center)
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // Header — unchanged
            HeaderCard()

            Spacer(modifier = Modifier.height(20.dp))

            // Scrollable content area — only the current step is shown
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
            ) {
                when (currentStep) {
                    SetupStep.BANK -> BankSelectionSection(
                        banks = banks,
                        selectedBank = selectedBank,
                        onBankSelected = { selectedBank = it }
                    )
                    SetupStep.SIM -> SimCardSelectionSection(
                        simCarriers = simCarriers,
                        selectedPrimarySim = selectedPrimarySim,
                        selectedSecondarySim = selectedSecondarySim,
                        isDualSimEnabled = isDualSimEnabled,
                        onPrimarySimSelected = { selectedPrimarySim = it },
                        onSecondarySimSelected = { selectedSecondarySim = it },
                        onDualSimToggled = { isDualSimEnabled = it },
                        secondarySimOptions = secondarySimOptions
                    )
                    SetupStep.DISCLAIMER -> DisclaimerSection()
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Button enable logic per step
            val isNextEnabled = when (currentStep) {
                SetupStep.BANK -> selectedBank.isNotBlank()
                SetupStep.SIM -> selectedPrimarySim.isNotBlank() &&
                        (!isDualSimEnabled || selectedSecondarySim.isNotBlank())
                SetupStep.DISCLAIMER -> true
            }
            val nextButtonText = if (currentStep == SetupStep.DISCLAIMER) "I Understand" else "Next"

            // Bottom navigation row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Back button — only on SIM and DISCLAIMER steps
                if (currentStep != SetupStep.BANK) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .border(1.dp, OFFPAYLightGray, RoundedCornerShape(16.dp))
                            .clickable(
                                indication = null,
                                interactionSource = remember { MutableInteractionSource() }
                            ) {
                                currentStep = when (currentStep) {
                                    SetupStep.SIM -> SetupStep.BANK
                                    SetupStep.DISCLAIMER -> SetupStep.SIM
                                    else -> SetupStep.BANK
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Back",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                // Next / I Understand button
                CompleteSetupButton(
                    modifier = Modifier.weight(1f),
                    text = nextButtonText,
                    showArrow = currentStep != SetupStep.DISCLAIMER,
                    enabled = isNextEnabled,
                    onCompleteSetup = {
                        when (currentStep) {
                            SetupStep.BANK -> currentStep = SetupStep.SIM
                            SetupStep.SIM -> currentStep = SetupStep.DISCLAIMER
                            SetupStep.DISCLAIMER -> {
                                val setupData = SetupHelper.SetupData(
                                    selectedBank = selectedBank,
                                    selectedPrimarySim = selectedPrimarySim,
                                    isDualSimEnabled = isDualSimEnabled,
                                    selectedSecondarySim = selectedSecondarySim,
                                    disclaimerAccepted = true
                                )
                                setupHelper.completeSetup(setupData)
                            }
                        }
                    }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}


@Composable
fun HeaderCard() {
    val accent = LocalOFFPAYAccentTheme.current
    val headerShape = RoundedCornerShape(20.dp)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 8.dp,
                shape = headerShape,
                ambientColor = Color.Black.copy(alpha = 0.15f),
                spotColor = Color.Black.copy(alpha = 0.15f)
            ),
        shape = headerShape,
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = OFFPAYDarkGray,
                    shape = headerShape
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Icon in solid circle
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .background(
                                color = accent.primary,
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccountBalanceWallet,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column {
                        Text(
                            text = stringResource(R.string.setup_OFFPAY),
                            style = TextStyle(
                                color = Color.White,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.3.sp,
                                shadow = Shadow(
                                    color = Color.Black.copy(alpha = 0.15f),
                                    offset = Offset(0f, 2f),
                                    blurRadius = 6f
                                )
                            )
                        )
                        Text(
                            text = stringResource(R.string.setup_step_1_of_2),
                            fontSize = 14.sp,
                            color = Color.White.copy(alpha = 0.7f),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = stringResource(R.string.configure_upi_payments),
                    fontSize = 15.sp,
                    color = Color.White.copy(alpha = 0.85f),
                    fontWeight = FontWeight.Normal,
                    lineHeight = 22.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ProgressDot(isActive = true)
                    ProgressDot(isActive = false)
                }
            }
        }
    }
}

/**
 * Shared section-header pattern from MainScreen/Settings: small accent-tinted
 * circle icon, 16sp title, 12sp gray subtitle.
 */
@Composable
private fun SetupSectionHeader(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String
) {
    val accent = LocalOFFPAYAccentTheme.current
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .background(accent.primary.copy(alpha = 0.12f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = accent.primary,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = subtitle,
                fontSize = 12.sp,
                color = OFFPAYTextLightGray
            )
        }
    }
}

@Composable
private fun SetupFieldLabel(text: String) {
    Text(
        text = text,
        fontSize = 13.sp,
        fontWeight = FontWeight.Medium,
        color = OFFPAYTextLightGray,
        modifier = Modifier.padding(bottom = 8.dp)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BankSelectionSection(
    banks: List<Pair<String, String>>,
    selectedBank: String,
    onBankSelected: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(OFFPAYSurfaceDim, RoundedCornerShape(20.dp))
            .padding(16.dp)
    ) {
        SetupSectionHeader(
            icon = Icons.Default.AccountBalance,
            title = stringResource(R.string.bank_selection),
            subtitle = stringResource(R.string.choose_primary_bank)
        )

        Spacer(modifier = Modifier.height(16.dp))

        SetupFieldLabel(stringResource(R.string.setup_select_bank))

        var expanded by remember { mutableStateOf(false) }

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = banks.find { it.first == selectedBank }?.second
                    ?: stringResource(R.string.setup_choose_your_bank),
                onValueChange = {},
                readOnly = true,
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = OFFPAYDisabledGray,
                    unfocusedBorderColor = OFFPAYLightGray,
                    focusedContainerColor = OFFPAYMediumGray,
                    unfocusedContainerColor = OFFPAYDarkGray,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedTrailingIconColor = Color.White,
                    unfocusedTrailingIconColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp),
                textStyle = TextStyle(fontSize = 15.sp),
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.background(OFFPAYMediumGray)
            ) {
                banks.forEach { (value, label) ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                label,
                                color = Color.White,
                                fontSize = 15.sp
                            )
                        },
                        onClick = {
                            onBankSelected(value)
                            expanded = false
                        },
                        modifier = Modifier.background(OFFPAYMediumGray)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimCardSelectionSection(
    simCarriers: List<Pair<String, String>>,
    selectedPrimarySim: String,
    selectedSecondarySim: String,
    isDualSimEnabled: Boolean,
    onPrimarySimSelected: (String) -> Unit,
    onSecondarySimSelected: (String) -> Unit,
    onDualSimToggled: (Boolean) -> Unit,
    secondarySimOptions: List<Pair<String, String>>
) {
    val accent = LocalOFFPAYAccentTheme.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(OFFPAYSurfaceDim, RoundedCornerShape(20.dp))
            .padding(16.dp)
    ) {
        SetupSectionHeader(
            icon = Icons.Default.SimCard,
            title = stringResource(R.string.sim_card_selection),
            subtitle = stringResource(R.string.configure_sim_cards)
        )

        Spacer(modifier = Modifier.height(16.dp))

        SetupFieldLabel(stringResource(R.string.setup_primary_sim))

        // Primary SIM Selection
        var primaryExpanded by remember { mutableStateOf(false) }

        ExposedDropdownMenuBox(
            expanded = primaryExpanded,
            onExpandedChange = { primaryExpanded = !primaryExpanded }
        ) {
            OutlinedTextField(
                value = simCarriers.find { it.first == selectedPrimarySim }?.second
                    ?: stringResource(R.string.setup_select_primary_sim),
                onValueChange = {},
                readOnly = true,
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = primaryExpanded)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = OFFPAYDisabledGray,
                    unfocusedBorderColor = OFFPAYLightGray,
                    focusedContainerColor = OFFPAYMediumGray,
                    unfocusedContainerColor = OFFPAYDarkGray,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedTrailingIconColor = Color.White,
                    unfocusedTrailingIconColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp),
                textStyle = TextStyle(fontSize = 15.sp),
            )

            ExposedDropdownMenu(
                expanded = primaryExpanded,
                onDismissRequest = { primaryExpanded = false },
                modifier = Modifier.background(OFFPAYMediumGray)
            ) {
                simCarriers.forEach { (value, label) ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                label,
                                color = Color.White,
                                fontSize = 15.sp
                            )
                        },
                        onClick = {
                            onPrimarySimSelected(value)
                            primaryExpanded = false
                        },
                        modifier = Modifier.background(OFFPAYMediumGray)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Dual SIM Checkbox
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) { onDualSimToggled(!isDualSimEnabled) },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(22.dp)
                    .border(
                        width = 2.dp,
                        color = if (isDualSimEnabled) accent.accent else OFFPAYDisabledGray,
                        shape = CircleShape
                    )
                    .background(
                        color = if (isDualSimEnabled) accent.accent else Color.Transparent,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (isDualSimEnabled) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .background(Color.White, CircleShape)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = stringResource(R.string.enable_dual_sim),
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White
            )
        }

        // Secondary SIM Section
        if (isDualSimEnabled) {
            Spacer(modifier = Modifier.height(16.dp))

            HorizontalDivider(
                color = OFFPAYMediumGray,
                thickness = 0.5.dp
            )

            Spacer(modifier = Modifier.height(16.dp))

            SetupFieldLabel(stringResource(R.string.secondary_sim))

            // Secondary SIM Selection
            var secondaryExpanded by remember { mutableStateOf(false) }

            ExposedDropdownMenuBox(
                expanded = secondaryExpanded,
                onExpandedChange = { secondaryExpanded = !secondaryExpanded }
            ) {
                OutlinedTextField(
                    value = secondarySimOptions.find { it.first == selectedSecondarySim }?.second
                        ?: stringResource(R.string.setup_select_secondary_sim),
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = secondaryExpanded)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = OFFPAYDisabledGray,
                        unfocusedBorderColor = OFFPAYLightGray,
                        focusedContainerColor = OFFPAYMediumGray,
                        unfocusedContainerColor = OFFPAYDarkGray,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedTrailingIconColor = Color.White,
                        unfocusedTrailingIconColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp),
                    textStyle = TextStyle(fontSize = 15.sp),
                )

                ExposedDropdownMenu(
                    expanded = secondaryExpanded,
                    onDismissRequest = { secondaryExpanded = false },
                    modifier = Modifier.background(OFFPAYMediumGray)
                ) {
                    secondarySimOptions.forEach { (value, label) ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    label,
                                    color = Color.White,
                                    fontSize = 15.sp
                                )
                            },
                            onClick = {
                                onSecondarySimSelected(value)
                                secondaryExpanded = false
                            },
                            modifier = Modifier.background(OFFPAYMediumGray)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DisclaimerSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(OFFPAYSurfaceDim, RoundedCornerShape(20.dp))
            .padding(16.dp)
    ) {
        SetupSectionHeader(
            icon = Icons.Outlined.Info,
            title = stringResource(R.string.disclaimer),
            subtitle = stringResource(R.string.setup_please_read_before_continuing)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Full disclaimer text shown directly — no expand/collapse needed
        Text(
            text = stringResource(R.string.disclaimer_text),
            fontSize = 14.sp,
            color = Color.White.copy(alpha = 0.85f),
            lineHeight = 20.sp
        )
    }
}



@Composable
fun CompleteSetupButton(
    modifier: Modifier = Modifier,
    text: String,
    showArrow: Boolean = true,
    enabled: Boolean,
    onCompleteSetup: () -> Unit
) {
    val accent = LocalOFFPAYAccentTheme.current
    val buttonShape = RoundedCornerShape(16.dp)

    Box(
        modifier = modifier
            .height(56.dp)
            .background(
                color = if (enabled) accent.primary else OFFPAYMediumGray,
                shape = buttonShape
            )
            .border(
                width = 1.dp,
                color = Color.White.copy(alpha = if (enabled) 0.15f else 0.05f),
                shape = buttonShape
            )
            .clip(buttonShape)
            .clickable(enabled = enabled) { onCompleteSetup() }
            .alpha(if (enabled) 1f else 0.4f),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = text,
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = (-0.3).sp
            )
            if (showArrow) {
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
