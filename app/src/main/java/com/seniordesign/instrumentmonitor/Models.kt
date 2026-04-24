package com.seniordesign.instrumentmonitor

data class SensorData(
    val temperature: Double,
    val humidity: Double
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
    val joinCode: String
)

data class Student(
    val id: String,
    val name: String,
    val instrument: String,
    val classroomCode: String
)



