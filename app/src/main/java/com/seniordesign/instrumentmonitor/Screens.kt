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
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet

import com.github.mikephil.charting.formatter.ValueFormatter
import java.time.Instant
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.components.LimitLine
import androidx.compose.ui.text.input.PasswordVisualTransformation
import android.content.Context
import androidx.compose.runtime.toMutableStateList
import java.util.UUID
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope
import android.util.Log
import androidx.compose.material.icons.filled.Close

// Imports for Notifications
import android.app.NotificationChannel
import android.app.NotificationManager
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

// ---------------- SESSION ----------------

data class UserSession(
    val firstName: String,
    val lastName: String,
    val email: String,
    val instrument: String = "None",
    val role: String = "student"
)

object SessionManager {
    var currentUser: UserSession? = null
    var selectedInstrument: InstrumentProfile? = null
}


// ---------------- START UP SCREEN ----------------------
@Composable
fun StartScreen(navController: NavHostController, context: Context) {

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

        LaunchedEffect(Unit) {
            val savedUser = SessionStorage.loadSession(context)

            if (savedUser != null) {
                SessionManager.currentUser = savedUser

                if (savedUser.role == "admin") {
                    navController.navigate("admin_console") {
                        popUpTo("start") { inclusive = true }
                    }
                } else {
                    navController.navigate("main") {
                        popUpTo("start") { inclusive = true }
                    }
                }
            }
        }

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
                if (username == "admin" && password == "admin") {

                    SessionManager.currentUser = UserSession(
                        firstName = "Admin",
                        lastName = "",
                        email = "admin@system",
                        role = "admin" // 👈 IMPORTANT
                    )

                    navController.navigate("admin_console") {
                        popUpTo("start") { inclusive = true }
                    }
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

// --------------- STATUS BANNER --------------------------
@Composable
fun LiveDataStatusBanner() {
    val status = DataStatus.lastStatus
    val error = DataStatus.lastError

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {

            Text(
                text = "Data Status: $status"
            )

            if (error != null) {
                Text(
                    text = "Error: $error",
                    color = Color.Red
                )
            }
        }
    }
}

// ---------------- LOGIN ----------------

@Composable
fun LoginScreen(navController: NavHostController) {

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    var rememberMe by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = "Login",
            style = MaterialTheme.typography.headlineLarge
        )

        Spacer(Modifier.height(16.dp))

        // EMAIL
        OutlinedTextField(
            value = email,
            onValueChange = {
                email = it
                error = null
            },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(Modifier.height(12.dp))

        // PASSWORD
        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it
                error = null
            },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            visualTransformation = PasswordVisualTransformation()
        )

        Spacer(Modifier.height(8.dp))

        // FORGOT PASSWORD
        TextButton(
            onClick = {
                navController.navigate("forgot_password")
            },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("Forgot Password?")
        }



        Spacer(Modifier.height(8.dp))

        // ERROR TEXT
        if (error != null) {
            Text(
                text = error!!,
                color = MaterialTheme.colorScheme.error
            )
            Spacer(Modifier.height(8.dp))
        }

        // LOGIN BUTTON
        Button(
            onClick = {

                if (email.isBlank() || password.isBlank()) {
                    error = "Please enter email and password"
                    return@Button
                }

                val user = UserSession(
                    firstName = "User",
                    lastName = "",
                    email = email,
                    role = "student"
                )

                SessionManager.currentUser = user

                if (rememberMe) {
                    SessionStorage.saveSession(navController.context, user)
                }

                navController.navigate("main") {
                    popUpTo("login") { inclusive = true }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Login")
        }

        Spacer(Modifier.height(12.dp))

        // NAV TO SIGNUP
        TextButton(
            onClick = { navController.navigate("signup") },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
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
    val context = LocalContext.current

    var temp by remember { mutableStateOf(0.0) }
    var humidity by remember { mutableStateOf(0.0) }
    var battery by remember { mutableStateOf(0.0) }
    var mode by remember { mutableStateOf("unknown") }

    var selectedInstrument by remember {
        mutableStateOf<InstrumentProfile?>(SessionManager.selectedInstrument)
    }

    // Remembers user's instrument selection
    var instrumentRequired by remember { mutableStateOf(selectedInstrument == null) }

    var isSafe by remember { mutableStateOf(true) }
    var isConnected by remember { mutableStateOf(true) }

    var lastUpdatedTime by remember { mutableStateOf(System.currentTimeMillis()) }
    var now by remember { mutableStateOf(System.currentTimeMillis()) }

    val safeColor = if (isSafe) Color(0xFF4CAF50) else Color(0xFFF44336)

    val batteryColor = when {
        battery >= 3.8 -> Color(0xFF4CAF50)
        battery >= 3.5 -> Color(0xFFFFC107)
        else -> Color(0xFFF44336)
    }

    fun timeAgo(lastTime: Long): String {
        val seconds = (now - lastTime) / 1000
        return when {
            seconds < 60 -> "$seconds sec ago"
            else -> "${seconds / 60} min ago"
        }
    }

    // LIVE CLOCK (fixes frozen "0 seconds ago")
    LaunchedEffect(Unit) {
        while (true) {
            now = System.currentTimeMillis()
            delay(1000)
        }
    }

    // AWS POLLING LOOP
    LaunchedEffect(Unit) {
        while (true) {
            try {
                val data = AwsRepository.getLatestSensorData()

                temp = data.temperature
                humidity = data.humidity
                battery = data.battery
                mode = data.mode

                isConnected = true
                lastUpdatedTime = System.currentTimeMillis()

                isSafe = selectedInstrument?.let { instrument ->
                    temp in instrument.minTemp..instrument.maxTemp &&
                            humidity in instrument.minHumidity..instrument.maxHumidity
                } ?: false

                if (!isSafe && selectedInstrument != null) {
                    sendSafetyNotification(context, temp, humidity)
                }

                instrumentRequired = selectedInstrument == null

            } catch (e: Exception) {
                isConnected = false
            }

            delay(3000)
        }
    }

    Column(
        Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        Text("Welcome ${user?.firstName ?: "User"}", fontSize = 28.sp)

        // ---------------- SAFETY BANNER ----------------
        val bannerColor = when {
            instrumentRequired -> Color(0xFF607D8B) // gray-blue
            isSafe -> Color(0xFF4CAF50)
            else -> Color(0xFFF44336)
        }

        val bannerText = when {
            instrumentRequired -> "Select an instrument to begin"
            isSafe -> "Instrument Safe"
            else -> "WARNING: Unsafe Conditions!"
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .padding(vertical = 8.dp)
                .background(bannerColor),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = bannerText,
                color = Color.White,
                fontSize = 18.sp
            )
        }

        // ---------------- CONNECTION ----------------
        Text(
            text = if (isConnected) "AWS Connected" else "Offline",
            color = if (isConnected) Color(0xFF4CAF50) else Color.Red
        )

        // ---------------- LAST UPDATED ----------------
        Text(
            text = "Last updated: ${timeAgo(lastUpdatedTime)}",
            color = Color.Gray
        )

        if (selectedInstrument == null) {
            Text(
                text = "⚠ Please select an instrument profile to enable monitoring",
                color = Color.Red
            )
        }

        // ---------------- SENSOR DATA ----------------
        Text("Temperature: ${"%.1f".format(temp)} °F", fontSize = 24.sp)
        Text("Humidity: ${"%.1f".format(humidity)} %", fontSize = 24.sp)

        Text(
            text = "Battery: ${"%.2f".format(battery)} V",
            fontSize = 20.sp,
            color = batteryColor
        )

        // ---------------- MODE CHIPS ----------------
        // ---------------- MODE STATUS ----------------
        Row(verticalAlignment = Alignment.CenterVertically) {

            val isCase = mode.lowercase() == "case"
            val isAmbient = mode.lowercase() == "ambient"

            Box(
                modifier = Modifier
                    .background(
                        color = if (isCase) Color(0xFF1976D2) else Color.LightGray,
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp)
                    )
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    text = if (isCase) "CASE ACTIVE" else "CASE",
                    color = if (isCase) Color.White else Color.DarkGray
                )
            }

            Spacer(Modifier.width(8.dp))

            Box(
                modifier = Modifier
                    .background(
                        when (mode.lowercase()) {
                            "case" -> Color(0xFF1976D2)
                            "ambient" -> Color(0xFF7B1FA2)
                            else -> Color.Gray
                        },
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp)
                    )
                    .padding(horizontal = 14.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "Mode: ${mode.uppercase()}",
                    color = Color.White
                )
            }
        }

        Spacer(Modifier.height(10.dp))

        // ---------------- INSTRUMENT ----------------
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

            // --------------- JOIN CLASS ----------------
            item {
                SettingsSectionHeader("Join Class")
            }

            item {
                Button(
                    onClick = { navController.navigate("join_class") },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Join a Class")
                }
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

    val context = LocalContext.current
    val scope   = rememberCoroutineScope()

    var classroom by remember {
        mutableStateOf(
            SessionStorage.loadClassroom(context) ?: Classroom(
                id        = System.currentTimeMillis().toString(),
                name      = "Music Class",
                teacherId = SessionManager.currentUser?.email ?: "unknown",
                joinCode  = "TEMP"
            )
        )
    }

    var students    by remember { mutableStateOf<List<Student>>(emptyList()) }
    var isLoading   by remember { mutableStateOf(true) }
    var showDialog  by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var errorMsg    by remember { mutableStateOf<String?>(null) }

    // Auto-refresh roster every 5 seconds
    LaunchedEffect(classroom.joinCode) {
        while (true) {
            isLoading = true
            try {
                students = AwsRepository.getStudentsByClass(classroom.joinCode)
            } catch (e: Exception) {
                errorMsg = "Failed to load students"
            }
            isLoading = false
            delay(5000)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {

        // ── Title ──────────────────────────────────────────
        Text("Admin Dashboard", style = MaterialTheme.typography.headlineLarge)

        // ── Stat row ───────────────────────────────────────
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(modifier = Modifier.weight(1f)) {
                StatCard(title = "Students", value = "${students.size} / 10+")
            }
            Box(modifier = Modifier.weight(1f)) {
                StatCard(title = "Class Code", value = classroom.joinCode)
            }
        }

        // ── Capacity badge ─────────────────────────────────
        val capacityColor = if (students.size >= 10)
            Color(0xFF4CAF50) else Color(0xFFFFC107)

        Surface(
            modifier = Modifier.fillMaxWidth(),
            color    = capacityColor,
            shape    = MaterialTheme.shapes.small
        ) {
            Text(
                text     = if (students.size >= 10)
                    "✓ Classroom capacity met (${students.size} students)"
                else
                    "Roster: ${students.size} / 10 minimum students",
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                color    = Color.White,
                style    = MaterialTheme.typography.labelLarge
            )
        }

        // ── Classroom code card ────────────────────────────
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp)) {
                Text("Classroom Code", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(4.dp))
                Text(
                    text  = classroom.joinCode,
                    style = MaterialTheme.typography.headlineMedium
                )
                Spacer(Modifier.height(8.dp))
                Button(onClick = {
                    scope.launch {
                        try {
                            val newCode = AwsRepository.createClass()
                            classroom = classroom.copy(joinCode = newCode)
                            SessionStorage.saveClassroom(context, classroom)
                            students = emptyList()
                        } catch (e: Exception) {
                            errorMsg = "Failed to create class: ${e.message}"
                        }
                    }
                }) {
                    Text("Generate New Code")
                }
            }
        }

        // ── Error banner ───────────────────────────────────
        if (errorMsg != null) {
            Text(
                text  = errorMsg!!,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        // ── Search + Add row ───────────────────────────────
        Row(
            modifier              = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment     = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value         = searchQuery,
                onValueChange = { searchQuery = it },
                label         = { Text("Search students") },
                modifier      = Modifier.weight(1f),
                singleLine    = true
            )
            Button(onClick = { showDialog = true }) {
                Text("Add")
            }
        }

        // ── Loading indicator ──────────────────────────────
        if (isLoading) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        }

        // ── Student roster ─────────────────────────────────
        val filtered = students.filter {
            it.name.contains(searchQuery, ignoreCase = true) ||
                    it.instrument.contains(searchQuery, ignoreCase = true)
        }

        LazyColumn(
            modifier              = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalArrangement   = Arrangement.spacedBy(8.dp)
        ) {
            items(filtered, key = { it.id }) { student ->
                StudentRosterCard(
                    student   = student,
                    onTap     = {
                        // Navigate to detail screen — passes studentId + classCode
                        navController.navigate(
                            "student_detail/${student.id}?classCode=${classroom.joinCode}"
                        )
                    },
                    onRemove  = {
                        scope.launch {
                            try {
                                AwsRepository.removeStudent(student.id, classroom.joinCode)
                                students = students.filter { it.id != student.id }
                                SessionStorage.saveStudents(context, students)
                            } catch (e: Exception) {
                                errorMsg = "Could not remove ${student.name}"
                            }
                        }
                    }
                )
            }
        }

        // ── Exit ───────────────────────────────────────────
        OutlinedButton(
            onClick  = {
                SessionManager.currentUser = null
                navController.navigate("start") { popUpTo(0) }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Exit Admin")
        }
    }

    // ── Add student dialog ─────────────────────────────────
    if (showDialog) {
        AddStudentDialog(
            onDismiss = { showDialog = false },
            onAdd     = { student ->
                val newStudent = student.copy(classroomCode = classroom.joinCode)
                scope.launch {
                    try {
                        AwsRepository.joinClass(newStudent)
                        students = students + newStudent
                        SessionStorage.saveStudents(context, students)
                    } catch (e: Exception) {
                        errorMsg = "Failed to add student"
                    }
                }
                showDialog = false
            }
        )
    }
}

// ----------- Add Students to Admin Console View -----------------------
@Composable
fun AddStudentDialog(
    onDismiss: () -> Unit,
    onAdd: (Student) -> Unit
) {

    var name by remember { mutableStateOf("") }
    var instrument by remember { mutableStateOf("Violin") }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        onAdd(
                            Student(
                                id = UUID.randomUUID().toString(),
                                name = name,
                                instrument = instrument,
                                classroomCode = ""
                            )
                        )
                    }
                }
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        title = { Text("Add Student") },
        text = {

            Column {

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Student Name") }
                )

                Spacer(Modifier.height(8.dp))

                // SIMPLE INSTRUMENT DROPDOWN
                InstrumentDropdown { selected ->
                    instrument = selected.name
                }
            }
        }
    )
}

// ── Tappable student card with remove button ───────────────
@Composable
fun StudentRosterCard(
    student  : Student,
    onTap    : () -> Unit,
    onRemove : () -> Unit
) {
    var showConfirm by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick  = onTap
    ) {
        Row(
            modifier          = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(student.name, style = MaterialTheme.typography.titleMedium)
                Text(
                    "Instrument: ${student.instrument}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    "ID: ${student.id.take(8)}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Tap hint
            Text(
                "View →",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(Modifier.width(8.dp))

            // Remove button
            IconButton(onClick = { showConfirm = true }) {
                Icon(
                    imageVector        = Icons.Default.Close,
                    contentDescription = "Remove student",
                    tint               = MaterialTheme.colorScheme.error
                )
            }
        }
    }

    // Confirm before deleting
    if (showConfirm) {
        AlertDialog(
            onDismissRequest = { showConfirm = false },
            title            = { Text("Remove student?") },
            text             = { Text("Remove ${student.name} from this class?") },
            confirmButton    = {
                Button(
                    onClick = { showConfirm = false; onRemove() },
                    colors  = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) { Text("Remove") }
            },
            dismissButton    = {
                OutlinedButton(onClick = { showConfirm = false }) { Text("Cancel") }
            }
        )
    }
}

// -------------- STAT CARD FOR ADMIN ------------------
@Composable
fun StatCard(title: String, value: String) {
    Card(
        modifier = Modifier
            .padding(4.dp)
            .fillMaxWidth()   // ✅ safe here
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(title, style = MaterialTheme.typography.labelMedium)
            Text(value, style = MaterialTheme.typography.titleLarge)
        }
    }
}

// ----------- Graph View ------------------
// function for index to timestamp conversion
fun parseTimestampToMillis(timestamp: String): Long {
    return Instant.parse(timestamp).toEpochMilli()
}

@Composable
fun LineChartView(
    data: List<SensorPoint>,
    mode: String
) {

    if (data.isEmpty()) {
        Text("No data available")
        return
    }

    AndroidView(
        factory = { context ->
            LineChart(context).apply {
                description.isEnabled = false
                setTouchEnabled(true)
                setPinchZoom(true)
            }
        },

        update = { chart ->

            // baseline for Time on graphs
            val baseTime = parseTimestampToMillis(data.first().timestamp)

            val dateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

            // Entries for the x-axis data
            val tempEntries = data.map { p ->
                Entry(
                    (parseTimestampToMillis(p.timestamp) - baseTime).toFloat(),
                    p.temperature
                )
            }

            val humidityEntries = data.map { p ->
                Entry(
                    (parseTimestampToMillis(p.timestamp) - baseTime).toFloat(),
                    p.humidity
                )
            }

            // Datasets for graphical view of temp, humidity, or both
            val dataSets = mutableListOf<ILineDataSet>()

            if (mode == "temp" || mode == "both") {
                val tempSet = LineDataSet(tempEntries, "Temperature (°F)").apply {
                    color = android.graphics.Color.RED
                    lineWidth = 2f
                    setDrawCircles(false)
                    axisDependency = YAxis.AxisDependency.LEFT
                }
                dataSets.add(tempSet)
            }

            if (mode == "humidity" || mode == "both") {
                val humSet = LineDataSet(humidityEntries, "Humidity (%)").apply {
                    color = android.graphics.Color.BLUE
                    lineWidth = 2f
                    setDrawCircles(false)
                    axisDependency = YAxis.AxisDependency.RIGHT
                }
                dataSets.add(humSet)
            }

            chart.data = LineData(dataSets)

            // Scaling done for the y-axis
            // Reduces garbled data of mixed temp and humidity
            val tempMin = data.minOf { it.temperature }
            val tempMax = data.maxOf { it.temperature }

            val humMin = data.minOf { it.humidity }
            val humMax = data.maxOf { it.humidity }

            chart.axisLeft.apply {
                axisMinimum = (tempMin - 2).coerceAtLeast(0f)
                axisMaximum = tempMax + 2
                setDrawLimitLinesBehindData(true)
            }

            chart.axisRight.apply {
                axisMinimum = (humMin - 2).coerceAtLeast(0f)
                axisMaximum = humMax + 2
                setDrawLimitLinesBehindData(true)
            }

            // Shaded danger zones for temperature
            chart.axisLeft.removeAllLimitLines()

            chart.axisLeft.addLimitLine(
                LimitLine(68f).apply {
                    lineColor = android.graphics.Color.parseColor("#3300FF00") // green zone start
                    lineWidth = 15f
                }
            )

            chart.axisLeft.addLimitLine(
                LimitLine(78f).apply {
                    lineColor = android.graphics.Color.parseColor("#33FF0000") // red zone start
                    lineWidth = 15f
                }
            )

            // Shaded danger zones for humidity
            chart.axisRight.removeAllLimitLines()

            chart.axisRight.addLimitLine(
                LimitLine(40f).apply {
                    lineColor = android.graphics.Color.parseColor("#3300FF00")
                    lineWidth = 15f
                }
            )

            chart.axisRight.addLimitLine(
                LimitLine(55f).apply {
                    lineColor = android.graphics.Color.parseColor("#33FF0000")
                    lineWidth = 15f
                }
            )

            // x-axis with real time labels
            chart.xAxis.apply {
                granularity = 1f
                setDrawGridLines(false)
                labelRotationAngle = -45f

                valueFormatter = object : ValueFormatter() {
                    override fun getFormattedValue(value: Float): String {
                        val actualTime = baseTime + value.toLong()
                        return dateFormat.format(Date(actualTime))
                    }
                }
            }

            // Final settings for graph view
            chart.axisLeft.isEnabled = true
            chart.axisRight.isEnabled = true

            chart.invalidate()
        },

        modifier = Modifier
            .fillMaxWidth()
            .height(280.dp)
    )
}

// ---------------- GraphsScreen ----------------------
@Composable
fun GraphsScreen() {

    var data1m by remember { mutableStateOf<List<SensorPoint>>(emptyList()) }
    var data1h by remember { mutableStateOf<List<SensorPoint>>(emptyList()) }
    var data1d by remember { mutableStateOf<List<SensorPoint>>(emptyList()) }
    var data1w by remember { mutableStateOf<List<SensorPoint>>(emptyList()) }

    var mode by remember { mutableStateOf("both") }

    val liveBuffer = remember { mutableStateListOf<SensorPoint>() }
    LaunchedEffect(Unit) {
        while (true) {

            val latest = AwsRepository.getLatestSensorData()

            val timestamp = System.currentTimeMillis()

            val newPoint = SensorPoint(
                timestamp = Instant.ofEpochMilli(timestamp).toString(), // ISO format
                temperature = latest.temperature.toFloat(),
                humidity = latest.humidity.toFloat()
            )

            // ✅ Add to buffer
            liveBuffer.add(newPoint)

            // Keep only last 7 days (prevents memory blowup)
            val cutoff = System.currentTimeMillis() - 604_800_000

            liveBuffer.removeAll {
                parseTimestampToMillis(it.timestamp) < cutoff
            }

            val now = System.currentTimeMillis()

            // Use buffer instead of getAllSensorData()
            data1m = liveBuffer.filter {
                now - parseTimestampToMillis(it.timestamp) <= 60_000
            }

            data1h = liveBuffer.filter {
                now - parseTimestampToMillis(it.timestamp) <= 3_600_000
            }

            data1d = liveBuffer.filter {
                now - parseTimestampToMillis(it.timestamp) <= 86_400_000
            }

            data1w = liveBuffer.filter {
                now - parseTimestampToMillis(it.timestamp) <= 604_800_000
            }

            delay(3000) // match CurrentStatus
        }
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {

        // Toggle buttons for graph view
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(onClick = { mode = "temp" }) { Text("Temp") }
            Button(onClick = { mode = "humidity" }) { Text("Humidity") }
            Button(onClick = { mode = "both" }) { Text("Both") }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {

            item {
                Text("1 Minute", style = MaterialTheme.typography.titleLarge)
                LineChartView(data1m, mode)
            }

            item {
                Text("1 Hour", style = MaterialTheme.typography.titleLarge)
                LineChartView(data1h, mode)
            }

            item {
                Text("1 Day", style = MaterialTheme.typography.titleLarge)
                LineChartView(data1d, mode)
            }

            item {
                Text("1 Week", style = MaterialTheme.typography.titleLarge)
                LineChartView(data1w, mode)
            }
        }
    }
}

// ------------------------- FORGOT PASSWORD ----------------------------
@Composable
fun ForgotPasswordScreen(navController: NavHostController) {

    var email by remember { mutableStateOf("") }
    var sent by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {

        Text("Reset Password", style = MaterialTheme.typography.headlineLarge)

        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Enter email") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = {
                sent = true
                // TODO: hook AWS Cognito / Firebase email reset here
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Send Reset Link")
        }

        if (sent) {
            Spacer(Modifier.height(12.dp))
            Text("If this email exists, a reset link has been sent.")
        }

        Spacer(Modifier.height(12.dp))

        TextButton(
            onClick = { navController.popBackStack() }
        ) {
            Text("Back to Login")
        }
    }
}

// --------------------- ADMIN CONSOLE: STUDENT DETAILS ----------------------
@Composable
fun StudentDetailScreen(
    studentId : String,
    classCode : String,
    navController: NavHostController
) {
    var student by remember { mutableStateOf<Student?>(null) }
    var history by remember { mutableStateOf<List<SensorPoint>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(studentId) {
        // Look up student from the real class roster
        val roster = AwsRepository.getStudentsByClass(classCode)
        student   = roster.firstOrNull { it.id == studentId }

        // Load their sensor history from S3
        history   = AwsRepository.getStudentHistory(studentId)
        isLoading = false
    }

    Column(
        modifier            = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        // ── Back button ────────────────────────────────────
        TextButton(onClick = { navController.popBackStack() }) {
            Text("← Back to Dashboard")
        }

        if (isLoading) {
            CircularProgressIndicator()
            return@Column
        }

        val s = student

        if (s == null) {
            Text("Student not found.", color = MaterialTheme.colorScheme.error)
            return@Column
        }

        // ── Student header ─────────────────────────────────
        Text(s.name, style = MaterialTheme.typography.headlineMedium)
        Text(
            "Class: $classCode",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Divider()

        // ── Instrument details card ────────────────────────
        // Looks up the preset safe-range profile for their instrument
        val profile = PresetData.instruments.firstOrNull {
            it.name.equals(s.instrument, ignoreCase = true)
        }

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {

                Text("Instrument Details", style = MaterialTheme.typography.titleMedium)

                Text("Instrument: ${s.instrument}", style = MaterialTheme.typography.bodyLarge)

                if (profile != null) {
                    Divider()
                    Text("Safe Temperature: ${profile.minTemp}°F – ${profile.maxTemp}°F")
                    Text("Safe Humidity:    ${profile.minHumidity}% – ${profile.maxHumidity}%")
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "Care Tips",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(profile.careTips, style = MaterialTheme.typography.bodySmall)
                } else {
                    Text(
                        "No preset profile found for ${s.instrument}",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        Divider()

        // ── Sensor history chart ───────────────────────────
        Text("Condition History", style = MaterialTheme.typography.titleMedium)

        if (history.isEmpty()) {
            Text(
                "No sensor history available yet for this student.",
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            LineChartView(data = history, mode = "both")
        }
    }
}

// ----------------- STUDENT JOIN CLASS SCREEN --------------------------------
@Composable
fun JoinClassScreen(navController: NavHostController) {

    val context = LocalContext.current
    val scope   = rememberCoroutineScope()

    var joinCode   by remember { mutableStateOf("") }
    var name       by remember { mutableStateOf("") }
    var instrument by remember { mutableStateOf("Violin") }
    var error      by remember { mutableStateOf<String?>(null) }
    var isLoading  by remember { mutableStateOf(false) }

    Column(
        modifier            = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {

        Text("Join Class", style = MaterialTheme.typography.headlineLarge)

        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value         = joinCode,
            onValueChange = { joinCode = it.uppercase().trim() }, // normalize code
            label         = { Text("Class Code") },
            modifier      = Modifier.fillMaxWidth(),
            singleLine    = true
        )

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value         = name,
            onValueChange = { name = it },
            label         = { Text("Your Name") },
            modifier      = Modifier.fillMaxWidth(),
            singleLine    = true
        )

        Spacer(Modifier.height(12.dp))

        Text("Instrument: $instrument")

        InstrumentDropdown { selected -> instrument = selected.name }

        Spacer(Modifier.height(16.dp))

        if (error != null) {
            Text(error!!, color = MaterialTheme.colorScheme.error)
            Spacer(Modifier.height(8.dp))
        }

        Button(
            onClick = {
                Log.d("JOIN_CLASS", "joinCode value = '$joinCode'")
                Log.d("JOIN_CLASS", "name value = '$name'")

                if (joinCode.isBlank() || name.isBlank()) {
                    error = "Please enter your name and class code"
                    return@Button
                }

                val student = Student(
                    id            = UUID.randomUUID().toString(),
                    name          = name,
                    instrument    = instrument,
                    classroomCode = joinCode
                )

                isLoading = true
                error     = null

                scope.launch {
                    try {
                        // ✅ WAIT for AWS to confirm before doing anything else
                        AwsRepository.joinClass(student)

                        // Update session after confirmed write
                        SessionManager.currentUser = SessionManager.currentUser?.copy(
                            firstName  = name,
                            instrument = instrument
                        )

                        // Now safe to navigate
                        navController.navigate("main") {
                            popUpTo("join_class") { inclusive = true }
                        }

                    } catch (e: Exception) {
                        // Show the real error instead of silently failing
                        error     = "Failed to join class: ${e.message}"
                        isLoading = false
                    }
                }
            },
            enabled  = !isLoading,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color    = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = 2.dp
                )
                Spacer(Modifier.width(8.dp))
                Text("Joining...")
            } else {
                Text("Join")
            }
        }
    }
}

// ------------------------ APP NOTIFICATIONS -------------------
// Add this function anywhere in Screens.kt
fun sendSafetyNotification(context: Context, temp: Double, humidity: Double) {
    val channelId = "instrument_safety"
    val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    val channel = NotificationChannel(
        channelId,
        "Instrument Safety Alerts",
        NotificationManager.IMPORTANCE_HIGH
    ).apply { description = "Alerts when conditions are unsafe for instruments" }
    manager.createNotificationChannel(channel)

    val notification = NotificationCompat.Builder(context, channelId)
        .setSmallIcon(android.R.drawable.ic_dialog_alert)
        .setContentTitle("Unsafe Instrument Conditions!")
        .setContentText("Temp: ${"%.1f".format(temp)}°F  Humidity: ${"%.1f".format(humidity)}%")
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setAutoCancel(true)
        .build()

    NotificationManagerCompat.from(context).notify(1001, notification)
}

