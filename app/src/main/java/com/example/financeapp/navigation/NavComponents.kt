package com.example.financeapp.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController

@Composable
fun NavigationComponents(
    navController: NavController,
    currentRoute: String?,
    content: @Composable (PaddingValues) -> Unit,
) {
    //val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    if (currentRoute == null || currentRoute == "com.example.financeapp.navigation.Login") {
        Scaffold(
        ) { innerPadding ->
            Box(modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
            ) {
                content(innerPadding)
            }
        }
        return
    }

    Scaffold(
        // topBar = { MyTopBar(currentRoute, drawerState) },
        bottomBar = { MyBottomBar(navController = navController, currentRoute) },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
        ) {
            content(innerPadding)
        }
    }

    /*ModalNavigationDrawer(
        drawerContent = { MyDrawerSheet(navController = navController, drawerState, currentRoute) },
        drawerState = drawerState,
        modifier = Modifier.systemBarsPadding(),
    ) {
        Scaffold(
            bottomBar = { MyBottomBar(navController = navController, currentRoute) },
            containerColor = MaterialTheme.colorScheme.background
        ) { innerPadding ->
            Box(modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
            ) {
                content(innerPadding)
            }
        }
    }*/
}



