package com.example.financeapp.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.StarRate
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun MoreScreen(navController: NavController) {
    val drawerItems = listOf(
        DrawerItem("Home", Icons.Filled.Home, "com.example.financeapp.navigation.Home"),
        DrawerItem("Settings", Icons.Filled.Settings, null),
        DrawerItem("Help", Icons.AutoMirrored.Filled.HelpOutline, null),
        DrawerItem("Follow Us", Icons.Filled.People, null),
        DrawerItem("Share with Friends", Icons.Filled.Share, null),
        DrawerItem("Rate the App", Icons.Filled.StarRate, null),
        DrawerItem("Log Out", Icons.AutoMirrored.Filled.Logout, "com.example.financeapp.navigation.Login")
    )
    val premiumItem = DrawerItem("Get EasySave Premium to remove ads and unlock more features!", Icons.Filled.WorkspacePremium, null)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 8.dp)
            .verticalScroll(rememberScrollState())
    ) {

        Spacer(Modifier.height(8.dp))

        Text(
            "EasySave",
            modifier = Modifier.padding(
                start = 16.dp,
                top = 12.dp,
                end = 16.dp,
                bottom = 8.dp
            ),
            style = MaterialTheme.typography.titleLarge.copy(fontSize = 24.sp),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        HorizontalDivider(modifier = Modifier.padding(16.dp), color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f))

        NavigationDrawerItem(
            label = { Text(
                premiumItem.label,
                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp)) },
            selected = false,
            icon = { Icon(premiumItem.icon, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant) },
            onClick = {
            },
            colors = NavigationDrawerItemDefaults.colors(
                selectedTextColor = MaterialTheme.colorScheme.onPrimary,
                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                selectedIconColor = MaterialTheme.colorScheme.onPrimary,
                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                selectedContainerColor = MaterialTheme.colorScheme.background,
                unselectedContainerColor = MaterialTheme.colorScheme.background
            )
        )
        HorizontalDivider(
            modifier = Modifier.padding(
                start = 16.dp,
                top = 16.dp,
                end = 16.dp,
                bottom = 8.dp
            ),
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f))

        drawerItems.forEach { item ->
            NavigationDrawerItem(
                icon = {
                    Icon(
                        item.icon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                label = {
                    Text(
                        item.label,
                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                selected = false,
                onClick = {
                    item.route?.let { route ->
                        navController.navigate(route) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                        }
                    }
                },
                colors = NavigationDrawerItemDefaults.colors(
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedContainerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    }
}

data class DrawerItem(
    val label: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val route: String?
)