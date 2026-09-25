package com.offpay.app.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.offpay.app.helpers.QrEncoder
import com.offpay.app.ui.theme.OFFPAYDarkGray

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReceiveQrDialog(
    phoneNumber: String,
    onDismiss: () -> Unit
) {
    var amount by remember { mutableStateOf("") }
    
    // Construct the custom offpay URI
    val qrContent = remember(amount, phoneNumber) {
        val base = "offpay://pay?phone=$phoneNumber"
        if (amount.isNotBlank() && amount.toIntOrNull() != null) {
            "$base&amount=$amount"
        } else {
            base
        }
    }

    val qrBitmap = remember(qrContent) {
        QrEncoder.generateQrBitmap(qrContent)
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = OFFPAYDarkGray),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .imePadding()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Scan to Pay Me",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                if (qrBitmap != null) {
                    Box(
                        modifier = Modifier
                            .background(Color.White, RoundedCornerShape(12.dp))
                            .padding(16.dp)
                    ) {
                        Image(
                            bitmap = qrBitmap,
                            contentDescription = "My QR Code",
                            modifier = Modifier.size(200.dp)
                        )
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .size(200.dp)
                            .background(Color.DarkGray, RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("QR Error", color = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = phoneNumber,
                    color = Color.LightGray,
                    fontSize = 16.sp,
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.height(24.dp))

                OutlinedTextField(
                    value = amount,
                    onValueChange = { if (it.length <= 5 && it.all { char -> char.isDigit() }) amount = it },
                    label = { Text("Set Amount (Optional)", color = Color.LightGray) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = Color.Gray,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Close")
                }
            }
        }
    }
}

