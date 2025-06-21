package com.example.financeapp.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController

@Composable
fun MyBottomBar(navController: NavController, currentRoute: String?) {
    if (currentRoute == null || currentRoute == "com.example.financeapp.navigation.Login") {
        return
    }

    val items = listOf("Home", "Budgets", "Analysis", "More")
    val routes = listOf("com.example.financeapp.navigation.Home", "com.example.financeapp.navigation.Budget", "com.example.financeapp.navigation.Analysis", "com.example.financeapp.navigation.More")
    val icons = listOf(Icons.Filled.Home, Icons.Filled.AccountBalanceWallet, Icons.Filled.Analytics, Icons.Filled.MoreHoriz)

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary
    ) {
        items.forEachIndexed { index, item ->
            val route = routes[index]
            val isSelected = currentRoute == route

            NavigationBarItem(
                icon = {
                    Icon(
                        icons[index],
                        contentDescription = null,
                    )
                },
                label = {
                    Text(
                        item,
                    )
                },
                selected = isSelected,
                onClick = {
                    if (!isSelected) {
                        navController.navigate(route) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            restoreState = true
                            launchSingleTop = true
                        }
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedTextColor = MaterialTheme.colorScheme.onPrimary,
                    unselectedTextColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.6f),
                    selectedIconColor = MaterialTheme.colorScheme.onPrimary,
                    unselectedIconColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.6f),
                    indicatorColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    }
}