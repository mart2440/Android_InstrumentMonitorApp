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
    val maxHumidity: Double
)