package com.seniordesign.instrumentmonitor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.compose.runtime.mutableStateOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay

class MainViewModel : ViewModel() {

    var selectedInstrument = mutableStateOf(
        PresetData.instruments.first()
    )

    fun setInstrument(instrument: InstrumentProfile) {
        selectedInstrument.value = instrument
    }

    var temperature = mutableStateOf(72.0)
    var humidity = mutableStateOf(50.0)

    var battery = mutableStateOf(0.0)
    var mode = mutableStateOf("unknown")

    init {
        startLiveUpdates()
    }

    fun updateEnvironment(temp: Double, hum: Double) {
        temperature.value = temp
        humidity.value = hum
    }

    private fun startLiveUpdates() {
        viewModelScope.launch {
            while (true) {
                try {
                    val data = AwsRepository.getLatestSensorData()

                    temperature.value = data.temperature
                    humidity.value = data.humidity
                    battery.value = data.battery
                    mode.value = data.mode

                } catch (e: Exception) {
                    e.printStackTrace()
                }

                delay(3000)
            }
        }
    }
}