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
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.*
import androidx.compose.ui.viewinterop.AndroidView

import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.components.Description

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


// ---------------- START UP SCREEN ----------------------
@Composable
fun StartScreen(navController: NavHostController) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "Instrument Monitor",
            style = MaterialTheme.typography.headlineLarge
        )

        Spacer(Modifier.height(40.dp))

        Button(
            onClick = { navController.navigate("login") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Log In")
        }

        Spacer(Modifier.height(12.dp))

        OutlinedButton(
            onClick = { navController.navigate("signup") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Create Account")
        }

        Spacer(Modifier.height(32.dp))

        Text(
            text = "Admin Access",
            style = MaterialTheme.typography.labelLarge
        )

        Spacer(Modifier.height(12.dp))

        Button(
            onClick = { navController.navigate("admin_login") },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.error
            )
        ) {
            Text("Admin Login")
        }
    }
}

// ---------------- ADMIN LOGIN SCREEN --------------------------
@Composable
fun AdminLoginScreen(navController: NavHostController) {

    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {

        Text("Admin Console", style = MaterialTheme.typography.headlineLarge)

        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Admin Username") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = {
                // simple placeholder auth
                if (username == "admin" && password == "admin") {
                    navController.navigate("admin_console")
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Enter Admin Console")
        }
    }
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
                "graphs" -> GraphsScreen()
                "profiles" -> InstrumentProfilesScreen(navController)
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
fun InstrumentProfilesScreen(navController: NavHostController) {

    val instruments = PresetData.instruments

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Text(
            text = "Instrument Profiles",
            fontSize = 30.sp,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // GRID
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            items(instruments) { instrument ->

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f),
                    onClick = {
                        SessionManager.selectedInstrument = instrument
                        navController.navigate("instrument_detail")
                    }
                ) {

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(12.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        Icon(
                            painter = painterResource(id = instrument.iconRes),
                            contentDescription = instrument.name,
                            modifier = Modifier.size(64.dp)
                        )

                        Spacer(Modifier.height(8.dp))

                        Text(
                            text = instrument.name,
                            fontSize = 18.sp
                        )
                    }
                }
            }
        }
    }
}

// -------------- INSTRUMENT DETAILS -------------
@Composable
fun InstrumentDetailScreen() {

    val instrument = SessionManager.selectedInstrument

    if (instrument == null) {
        Text("No instrument selected")
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        Icon(
            painter = painterResource(id = instrument.iconRes),
            contentDescription = instrument.name,
            modifier = Modifier.size(100.dp)
        )

        Text(instrument.name, fontSize = 30.sp)

        Text(instrument.description, fontSize = 18.sp)

        Divider()

        Text("Safe Ranges", fontSize = 22.sp)

        Text("Temperature: ${instrument.minTemp} - ${instrument.maxTemp} °F")
        Text("Humidity: ${instrument.minHumidity} - ${instrument.maxHumidity} %")

        Divider()

        Text("Care Tips", fontSize = 22.sp)

        Text(instrument.careTips)
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
    var autoSyncEnabled by remember { mutableStateOf(true) }
    var darkModeEnabled by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Text(
            text = "Settings",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {

            // ---------------- PROFILE ----------------
            item {
                SettingsSectionHeader("Profile")
            }

            item {
                SettingsTextField(
                    label = "First Name",
                    value = firstName,
                    onValueChange = { firstName = it }
                )
            }

            item {
                SettingsTextField(
                    label = "Last Name",
                    value = lastName,
                    onValueChange = { lastName = it }
                )
            }

            item {
                SettingsTextField(
                    label = "Email",
                    value = email,
                    onValueChange = { email = it }
                )
            }

            // ---------------- MONITORING ----------------
            item {
                SettingsSectionHeader("Instrument Monitoring")
            }

            item {
                SettingsSwitchRow(
                    title = "Enable Notifications",
                    checked = notificationsEnabled,
                    onCheckedChange = { notificationsEnabled = it }
                )
            }

            item {
                SettingsSwitchRow(
                    title = "Auto Sync Data",
                    checked = autoSyncEnabled,
                    onCheckedChange = { autoSyncEnabled = it }
                )
            }

            // ---------------- APPEARANCE ----------------
            item {
                SettingsSectionHeader("Appearance")
            }

            item {
                SettingsSwitchRow(
                    title = "Dark Mode",
                    checked = darkModeEnabled,
                    onCheckedChange = { darkModeEnabled = it }
                )
            }

            // ---------------- DATA / AWS ----------------
            item {
                SettingsSectionHeader("Data & Sync")
            }

            item {
                SettingsActionRow(
                    title = "Force Sync Now",
                    subtitle = "Upload latest sensor data to cloud",
                    onClick = {
                        // TODO: AwsRepository.sync()
                    }
                )
            }

            // ---------------- ACCOUNT ----------------
            item {
                SettingsSectionHeader("Account")
            }

            item {
                Button(
                    onClick = {
                        SessionManager.currentUser = SessionManager.currentUser?.copy(
                            firstName = firstName,
                            lastName = lastName,
                            email = email
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Save Changes")
                }
            }

            item {
                OutlinedButton(
                    onClick = {
                        SessionManager.currentUser = null
                        navController.navigate("login") {
                            popUpTo(0)
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Log Out")
                }
            }

            item {
                Spacer(Modifier.height(40.dp))
            }
        }
    }
}

// -------------- SECTION HEADER (SSETTINGS) ------------
@Composable
fun SettingsSectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(vertical = 8.dp)
    )
}

// -------------- SWITCH ROW (SETTINGS) -------------
@Composable
fun SettingsSwitchRow(
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, modifier = Modifier.weight(1f))

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}

// ----- FORCE SYNC IN SETTINGS -----------
@Composable
fun SettingsActionRow(
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp)
    ) {
        TextButton(onClick = onClick) {
            Column {
                Text(title)
                Text(
                    subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

// --------------- SETTINGS TEXT FIELDS ---------
@Composable
fun SettingsTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth()
    )
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

// ------------------ ADMIN CONSOLE SCREEN -----------------------
@Composable
fun AdminConsoleScreen(navController: NavHostController) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {

        Text("Admin Dashboard", style = MaterialTheme.typography.headlineLarge)

        Spacer(Modifier.height(20.dp))

        Text("• System status overview")
        Text("• User management")
        Text("• Sensor data logs")
        Text("• AWS sync controls")

        Spacer(Modifier.height(20.dp))

        Button(
            onClick = { navController.popBackStack("signup", inclusive = false) }
        ) {
            Text("Exit Admin")
        }
    }
}

// ----------- Graph View ------------------
@Composable
fun LineChartView(data: List<SensorPoint>) {

    if (data.isEmpty()) {
        Text("No data available")
        return
    }

    AndroidView(
        factory = { context ->

            val chart = LineChart(context)

            val entries = data.mapIndexed { index, point ->
                Entry(index.toFloat(), point.temperature)
            }

            val dataSet = LineDataSet(entries, "Temperature (°F)").apply {
                lineWidth = 2f
                setDrawCircles(false)
            }

            chart.data = LineData(dataSet)

            val desc = Description()
            desc.text = ""
            chart.description = desc

            chart.invalidate()

            chart
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(250.dp)
    )
}

// ---------------- GraphsScreen ----------------------
@Composable
fun GraphsScreen() {

    var data1m by remember { mutableStateOf<List<SensorPoint>>(emptyList()) }
    var data1h by remember { mutableStateOf<List<SensorPoint>>(emptyList()) }
    var data1d by remember { mutableStateOf<List<SensorPoint>>(emptyList()) }
    var data1w by remember { mutableStateOf<List<SensorPoint>>(emptyList()) }

    LaunchedEffect(Unit) {
        data1m = AwsRepository.getGraphData("1m.json")
        data1h = AwsRepository.getGraphData("1h.json")
        data1d = AwsRepository.getGraphData("1d.json")
        data1w = AwsRepository.getGraphData("1w.json")
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {

        item {
            Text("1 Minute", style = MaterialTheme.typography.titleLarge)
            LineChartView(data1m)
        }

        item {
            Text("1 Hour", style = MaterialTheme.typography.titleLarge)
            LineChartView(data1h)
        }

        item {
            Text("1 Day", style = MaterialTheme.typography.titleLarge)
            LineChartView(data1d)
        }

        item {
            Text("1 Week", style = MaterialTheme.typography.titleLarge)
            LineChartView(data1w)
        }
    }
}