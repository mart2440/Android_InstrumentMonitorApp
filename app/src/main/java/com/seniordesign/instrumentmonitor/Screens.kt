package com.seniordesign.instrumentmonitor

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import kotlinx.coroutines.delay

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.unit.sp
import androidx.compose.material3.Switch
import androidx.compose.foundation.background
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color

// ---------------- SESSION ----------------

data class UserSession(
    val firstName: String,
    val lastName: String,
    val email: String,
    val instrument: String = "None"
)

object SessionManager {
    var currentUser: UserSession? = null
    var selectedInstrument: InstrumentProfile? = null
}

// ---------------- SIGNUP ----------------

@Composable
fun SignupScreen(navController: NavHostController) {

    var first by remember { mutableStateOf("") }
    var last by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var pass by remember { mutableStateOf("") }
    var verify by remember { mutableStateOf("") }

    Column(
        Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {

        Text("Create Account", style = MaterialTheme.typography.headlineLarge)

        Spacer(Modifier.height(16.dp))

        OutlinedTextField(first, { first = it }, label = { Text("First Name") })
        OutlinedTextField(last, { last = it }, label = { Text("Last Name") })
        OutlinedTextField(email, { email = it }, label = { Text("Email") })
        OutlinedTextField(pass, { pass = it }, label = { Text("Password") })
        OutlinedTextField(verify, { verify = it }, label = { Text("Verify Password") })

        Spacer(Modifier.height(16.dp))

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                if (pass == verify) {
                    SessionManager.currentUser =
                        UserSession(first, last, email)

                    navController.navigate("main")
                }
            }
        ) {
            Text("Create Account")
        }

        TextButton(onClick = { navController.navigate("login") }) {
            Text("Already have an account? Log in")
        }
    }
}

// ---------------- LOGIN ----------------

@Composable
fun LoginScreen(navController: NavHostController) {

    Column(Modifier.fillMaxSize().padding(24.dp)) {

        Text("Login", style = MaterialTheme.typography.headlineLarge)

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = { navController.navigate("main") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Login")
        }

        TextButton(onClick = { navController.navigate("signup") }) {
            Text("Create Account")
        }
    }
}

// ---------------- MAIN ----------------

@Composable
fun MainScreen(navController: NavHostController) {

    var tab by remember { mutableStateOf("status") }

    Scaffold(
        bottomBar = {
            NavigationBar {

                NavigationBarItem(
                    selected = tab == "status",
                    onClick = { tab = "status" },
                    label = { Text("Status") },
                    icon = { Icon(Icons.Default.Home, contentDescription = "Status") }
                )

                NavigationBarItem(
                    selected = tab == "graphs",
                    onClick = { tab = "graphs" },
                    label = { Text("Graphs") },
                    icon = { Icon(Icons.Default.Info, contentDescription = "Graphs") }
                )

                NavigationBarItem(
                    selected = tab == "profiles",
                    onClick = { tab = "profiles" },
                    label = { Text("Profiles") },
                    icon = { Icon(Icons.Default.List, contentDescription = "Profiles") }
                )

                NavigationBarItem(
                    selected = tab == "settings",
                    onClick = { tab = "settings" },
                    label = { Text("Settings") },
                    icon = { Icon(Icons.Default.Settings, contentDescription = "Settings") }
                )
            }
        }
    ) { padding ->

        Box(Modifier.padding(padding)) {

            when (tab) {
                "status" -> CurrentStatusScreen()
                "graphs" -> Text("Graphs")
                "profiles" -> InstrumentProfilesScreen()
                "settings" -> SettingsScreen(navController)
            }
        }
    }
}

// ---------------- STATUS ----------------

@Composable
fun CurrentStatusScreen() {

    val user = SessionManager.currentUser

    var temp by remember { mutableStateOf(70.0) }
    var humidity by remember { mutableStateOf(50.0) }

    var selectedInstrument by remember {
        mutableStateOf<InstrumentProfile?>(SessionManager.selectedInstrument)
    }

    var isSafe by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        while (true) {

            val data = AwsRepository.getSensorData()
            temp = data.temperature
            humidity = data.humidity

            selectedInstrument?.let { instrument ->
                isSafe =
                    temp in instrument.minTemp..instrument.maxTemp &&
                            humidity in instrument.minHumidity..instrument.maxHumidity
            }

            delay(4000)
        }
    }

    Column(
        Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        Text("Welcome ${user?.firstName ?: "User"}", fontSize = 28.sp)

        // 🔴🟢 STATUS BANNER
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .padding(vertical = 8.dp)
                .background(
                    if (isSafe) Color(0xFF4CAF50) else Color(0xFFF44336)
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = if (isSafe) "Instrument Safe" else "WARNING: Unsafe Conditions!",
                color = Color.White,
                fontSize = 20.sp
            )
        }

        Text("Temperature: ${"%.1f".format(temp)} °F", fontSize = 24.sp)
        Text("Humidity: ${"%.1f".format(humidity)} %", fontSize = 24.sp)

        Spacer(Modifier.height(10.dp))

        Text(
            text = "Instrument: ${selectedInstrument?.name ?: "None"}",
            fontSize = 20.sp
        )

        InstrumentDropdown { instrument ->
            selectedInstrument = instrument
            SessionManager.selectedInstrument = instrument
        }
    }
}

// ---------------- INSTRUMENT PROFILES ---------
@Composable
fun InstrumentProfilesScreen() {

    val instruments = listOf(
        "Violin" to "High-pitched string instrument used in orchestras and solos.",
        "Viola" to "Mid-range string instrument, deeper than violin.",
        "Cello" to "Low warm tone, played seated with endpin.",
        "Bass" to "Largest string instrument, deepest sound."
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        Text(
            text = "Instrument Profiles",
            fontSize = 30.sp
        )

        instruments.forEach { instrument ->

            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {

                    Text(
                        text = instrument.first,
                        fontSize = 24.sp
                    )

                    Text(
                        text = instrument.second,
                        fontSize = 18.sp
                    )
                }
            }
        }
    }
}

// --------------- SETTINGS -----------------
@Composable
fun SettingsScreen(navController: NavHostController) {

    val user = SessionManager.currentUser

    var firstName by remember { mutableStateOf(user?.firstName ?: "") }
    var lastName by remember { mutableStateOf(user?.lastName ?: "") }
    var email by remember { mutableStateOf(user?.email ?: "") }

    var notificationsEnabled by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {

        Text(
            text = "Settings",
            fontSize = 30.sp
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text = "Personal Profile",
            fontSize = 22.sp
        )

        OutlinedTextField(
            value = firstName,
            onValueChange = { firstName = it },
            label = { Text("First Name") },
            textStyle = LocalTextStyle.current.copy(fontSize = 18.sp)
        )

        OutlinedTextField(
            value = lastName,
            onValueChange = { lastName = it },
            label = { Text("Last Name") },
            textStyle = LocalTextStyle.current.copy(fontSize = 18.sp)
        )

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            textStyle = LocalTextStyle.current.copy(fontSize = 18.sp)
        )

        Spacer(Modifier.height(10.dp))

        Text(
            text = "Notifications",
            fontSize = 22.sp
        )

        Row(
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
        ) {
            Text("Enable Alerts", fontSize = 18.sp)

            Spacer(Modifier.weight(1f))

            Switch(
                checked = notificationsEnabled,
                onCheckedChange = { notificationsEnabled = it }
            )
        }

        Spacer(Modifier.height(20.dp))

        Button(
            onClick = {
                // Save updated profile locally
                SessionManager.currentUser = SessionManager.currentUser?.copy(
                    firstName = firstName,
                    lastName = lastName,
                    email = email
                )
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save Changes", fontSize = 18.sp)
        }

        OutlinedButton(
            onClick = {
                // LOG OUT
                SessionManager.currentUser = null
                navController.navigate("login") {
                    popUpTo(0)
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Log Out", fontSize = 18.sp)
        }
    }
}

// ---------------- DROPDOWN ----------------

@Composable
fun InstrumentDropdown(
    onSelect: (InstrumentProfile) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box {

        Button(onClick = { expanded = true }) {
            Text("Select Instrument")
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {

            PresetData.instruments.forEach { instrument ->

                DropdownMenuItem(
                    text = { Text(instrument.name) },
                    onClick = {
                        onSelect(instrument)
                        expanded = false
                    }
                )
            }
        }
    }
}