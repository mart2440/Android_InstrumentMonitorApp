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