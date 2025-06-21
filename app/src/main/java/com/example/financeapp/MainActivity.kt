package com.example.financeapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.financeapp.navigation.Analysis
import com.example.financeapp.navigation.Budget
import com.example.financeapp.navigation.Home
import com.example.financeapp.navigation.Login
import com.example.financeapp.navigation.More
import com.example.financeapp.navigation.NavigationComponents
import com.example.financeapp.ui.screens.AnalysisScreen
import com.example.financeapp.ui.screens.BudgetScreen
import com.example.financeapp.ui.screens.HomeScreen
import com.example.financeapp.ui.screens.LoginScreen
import com.example.financeapp.ui.screens.MoreScreen
import com.example.financeapp.ui.theme.FinanceAppTheme
import com.google.firebase.auth.FirebaseAuth

class MainActivity : ComponentActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = FirebaseAuth.getInstance()

        enableEdgeToEdge()
        setContent {
            FinanceAppTheme {
                val navController = rememberNavController()
                var currentRoute by remember { mutableStateOf<String?>(null) }

                DisposableEffect(navController) {
                    val listener = NavController.OnDestinationChangedListener { _, destination, _ ->
                        currentRoute = destination.route
                    }
                    navController.addOnDestinationChangedListener(listener)
                    onDispose {
                        navController.removeOnDestinationChangedListener(listener)
                    }
                }

                NavigationComponents(navController =  navController, currentRoute) {
                    NavHost(navController = navController, startDestination = Login) {
                        composable<Login> { LoginScreen(navController) }
                        composable<Home> { HomeScreen() }
                        composable<Budget> { BudgetScreen() }
                        composable<Analysis> { AnalysisScreen() }
                        composable<More> { MoreScreen(navController) }
                    }
                }
            }
        }
    }
}

