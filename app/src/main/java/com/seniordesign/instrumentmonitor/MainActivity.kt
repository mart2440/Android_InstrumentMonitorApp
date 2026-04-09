package com.seniordesign.instrumentmonitor

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.*
import androidx.compose.ui.platform.LocalContext

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {

            val navController = rememberNavController()
            val context = LocalContext.current

            NavHost(navController, startDestination = "start") {

                composable("start") { StartScreen(navController, context) }

                composable("signup") { SignupScreen(navController) }
                composable("login") { LoginScreen(navController) }

                composable("main") { MainScreen(navController) }

                composable("instrument_detail") { InstrumentDetailScreen() }

                composable("admin_login") { AdminLoginScreen(navController) }
                composable("admin_console") { AdminConsoleScreen(navController) }
                composable("forgot_password") { ForgotPasswordScreen(navController) }
            }
        }
    }
}