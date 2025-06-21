package com.example.financeapp.navigation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.ContactMail
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.StarRate
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.DrawerState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.launch

@Composable
fun MyDrawerSheet(navController: NavController, drawerState: DrawerState, currentRoute: String?) {
    if (currentRoute == null || currentRoute == "com.example.financeapp.navigation.Login") {
        return
    }

    val scope = rememberCoroutineScope()
    val drawerItems = listOf(
        DrawerItem("Home", Icons.Filled.Home, "com.example.financeapp.navigation.Home"),
        DrawerItem("Budgets", Icons.Filled.AccountBalanceWallet, "com.example.financeapp.navigation.Budget"),
        DrawerItem("Analysis", Icons.Filled.Analytics, "com.example.financeapp.navigation.Analysis"),
        DrawerItem("Settings", Icons.Filled.Settings, null),
        DrawerItem("Help and Feedback", Icons.AutoMirrored.Filled.HelpOutline, null),
        DrawerItem("Share with Friends", Icons.Filled.Share, null),
        DrawerItem("Follow Us", Icons.Filled.People, null),
        DrawerItem("Contact Us", Icons.Filled.ContactMail, null),
        DrawerItem("Rate the App", Icons.Filled.StarRate, null),
        DrawerItem("Log Out", Icons.AutoMirrored.Filled.Logout, "com.example.financeapp.navigation.Login")
    )

    val premiumItem = DrawerItem("Get EasySave Premium to remove ads and unlock more features!", Icons.Filled.WorkspacePremium, null)

    ModalDrawerSheet(
        drawerContainerColor = MaterialTheme.colorScheme.surfaceVariant,
        drawerContentColor = MaterialTheme.colorScheme.onSurfaceVariant
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .verticalScroll(rememberScrollState())
        ) {

            Spacer(Modifier.height(12.dp))

            Text(
                "EasySave",
                modifier = Modifier.padding(16.dp),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            HorizontalDivider(color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f))

            Spacer(Modifier.height(8.dp))

            NavigationDrawerItem(
                label = { Text(
                    premiumItem.label,
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp)) },
                selected = false,
                icon = { Icon(premiumItem.icon, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant) },
                onClick = {
                    scope.launch {
                        drawerState.close()
                    }
                },
                colors = NavigationDrawerItemDefaults.colors(
                    selectedTextColor = MaterialTheme.colorScheme.onPrimary,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    selectedIconColor = MaterialTheme.colorScheme.onPrimary,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                    unselectedContainerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f))

            drawerItems.forEach { item ->
                NavigationDrawerItem(
                    icon = {
                        Icon(
                            item.icon,
                            contentDescription = null,
                            tint = if (currentRoute == item.route) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    label = {
                        Text(
                            item.label,
                            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp),
                            color = if (currentRoute == item.route) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    selected = currentRoute == item.route,
                    onClick = {
                        item.route?.let { route ->
                            navController.navigate(route) {
                                popUpTo(navController.graph.startDestinationId) {
                                    saveState = true
                                }
                                launchSingleTop = true
                            }
                        }
                        scope.launch {
                            drawerState.close()
                        }
                    },
                    colors = NavigationDrawerItemDefaults.colors(
                        selectedTextColor = MaterialTheme.colorScheme.onPrimary,
                        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        selectedIconColor = MaterialTheme.colorScheme.onPrimary,
                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        unselectedContainerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                )
            }
        }
    }
}

data class DrawerItem(
    val label: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val route: String?
)