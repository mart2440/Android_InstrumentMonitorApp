package com.seniordesign.instrumentmonitor

import kotlin.random.Random

object AwsRepository {

    fun getSensorData(): SensorData {
        return SensorData(
            temperature = Random.nextDouble(18.0, 30.0),
            humidity = Random.nextDouble(40.0, 80.0)
        )
    }
}