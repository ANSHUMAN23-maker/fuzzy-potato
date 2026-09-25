package com.offpay.app.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.QrCodeScanner

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.offpay.app.ui.activities.SettingsScreen
import com.offpay.app.ui.activities.TransactionHistoryScreen
import com.offpay.app.MainScreen
import com.offpay.app.ui.theme.OFFPAYDarkGray
import com.offpay.app.ui.theme.OFFPAYBlack

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppShell(
    navController: NavHostController = rememberNavController(),
    onInitiateTransfer: (String, String) -> Unit,
    onQRScanClick: () -> Unit,
    onRequestOverlayPermission: () -> Unit
) {
    val items = listOf(
        Screen.Home,
        Screen.History,
        Screen.Settings
    )
    
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = onQRScanClick,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White,
                shape = androidx.compose.foundation.shape.CircleShape,
                modifier = Modifier.padding(bottom = 0.dp)
            ) {
                Icon(Icons.Default.QrCodeScanner, contentDescription = "Scan QR")
            }
        },
        floatingActionButtonPosition = FabPosition.Center,
        bottomBar = {
            NavigationBar(
                containerColor = OFFPAYDarkGray,
                contentColor = Color.White,
                tonalElevation = 8.dp,
                modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars)
            ) {
                items.forEach { screen ->
                    val selected = currentRoute == screen.route
                    NavigationBarItem(
                        icon = { Icon(screen.icon, contentDescription = screen.title) },
                        label = { Text(screen.title) },
                        selected = selected,
                        onClick = {
                            navController.navigate(screen.route) {
                                // Pop up to the start destination of the graph to
                                // avoid building up a large stack of destinations
                                popUpTo(Screen.Home.route) {
                                    saveState = true
                                }
                                // Avoid multiple copies of the same destination
                                launchSingleTop = true
                                // Restore state when reselecting a previously selected item
                                restoreState = true
                            }
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = OFFPAYBlack,
                            unselectedIconColor = Color.LightGray,
                            unselectedTextColor = Color.LightGray
                        )
                    )
                }
            }
        },
        containerColor = OFFPAYBlack
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            NavHost(
                navController = navController,
                startDestination = Screen.Home.route
            ) {
                composable(Screen.Home.route) {
                    MainScreen(
                        onInitiateTransfer = onInitiateTransfer,
                        onQRScanClick = onQRScanClick,
                        onRequestOverlayPermission = onRequestOverlayPermission
                    )
                }
                composable(Screen.History.route) {
                    TransactionHistoryScreen(
                        onBackClick = { navController.navigateUp() }
                    )
                }
                composable(Screen.Settings.route) {
                    val context = androidx.compose.ui.platform.LocalContext.current
                    val app = context.applicationContext as? com.offpay.app.OFFPAYApplication
                    val repo = app?.settingsRepository ?: com.offpay.app.data.SettingsRepository(context.applicationContext)
                    val refreshTrigger = remember { mutableIntStateOf(0) }
                    val permissionLauncher = androidx.activity.compose.rememberLauncherForActivityResult(
                        androidx.activity.result.contract.ActivityResultContracts.RequestMultiplePermissions()
                    ) { refreshTrigger.intValue++ }
                    SettingsScreen(
                        onBackPressed = { navController.navigateUp() },
                        settingsRepository = repo,
                        onRequestPermissions = { permissionLauncher.launch(it) },
                        refreshTrigger = refreshTrigger
                    )
                }
            }
        }
    }
}










