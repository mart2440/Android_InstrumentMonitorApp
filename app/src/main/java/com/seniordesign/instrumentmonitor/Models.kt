package com.seniordesign.instrumentmonitor

// Data collected per JSON file from ESP32 sensor
data class SensorData(
    val temperature: Double,
    val humidity: Double,
    val battery: Double,
    val mode: String
)

data class InstrumentProfile(
    val name: String,
    val minTemp: Double,
    val maxTemp: Double,
    val minHumidity: Double,
    val maxHumidity: Double,
    val description: String,
    val careTips: String,
    val iconRes: Int
)

data class SensorPoint(
    val timestamp: String,
    val temperature: Float,
    val humidity: Float
)

data class Classroom(
    val id: String,
    val name: String,
    val teacherId: String,
    val joinCode: String
)

data class Student(
    val id: String,
    val name: String,
    val instrument: String,
    val classroomCode: String
)



