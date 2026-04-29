package com.seniordesign.instrumentmonitor

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.*
import androidx.compose.ui.platform.LocalContext

import androidx.navigation.NavType
import androidx.navigation.navArgument

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
                composable("join_class") { JoinClassScreen(navController) }

                composable("main") { MainScreen(navController) }

                composable("instrument_detail") { InstrumentDetailScreen() }

                composable("admin_login") { AdminLoginScreen(navController) }
                composable("admin_console") { AdminConsoleScreen(navController) }
                composable("forgot_password") { ForgotPasswordScreen(navController) }
                composable("join_class") { JoinClassScreen(navController) }

                composable(
                    route = "student_detail/{studentId}?classCode={classCode}",
                    arguments = listOf(
                        navArgument("studentId")  { type = NavType.StringType },
                        navArgument("classCode")  { type = NavType.StringType; defaultValue = "" }
                    )
                ) { backStackEntry ->
                    StudentDetailScreen(
                        studentId     = backStackEntry.arguments?.getString("studentId") ?: "",
                        classCode     = backStackEntry.arguments?.getString("classCode") ?: "",
                        navController = navController
                    )
                }
            }
        }
    }
}