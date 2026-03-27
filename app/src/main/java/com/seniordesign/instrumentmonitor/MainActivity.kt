package com.seniordesign.instrumentmonitor

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.*

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {

            val navController = rememberNavController()

            NavHost(navController, startDestination = "signup") {

                composable("signup") { SignupScreen(navController) }
                composable("login") { LoginScreen(navController) }
                composable("main") { MainScreen(navController) }
            }
        }
    }
}