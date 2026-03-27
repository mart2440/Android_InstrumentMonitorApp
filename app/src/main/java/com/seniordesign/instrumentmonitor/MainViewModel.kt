package com.seniordesign.instrumentmonitor

import androidx.lifecycle.ViewModel
import androidx.compose.runtime.mutableStateOf

class MainViewModel : ViewModel() {

    var selectedInstrument = mutableStateOf(
        PresetData.instruments.first()
    )

    fun setInstrument(instrument: InstrumentProfile) {
        selectedInstrument.value = instrument
    }

    var temperature = mutableStateOf(72.0)
    var humidity = mutableStateOf(50.0)

    fun updateEnvironment(temp: Double, hum: Double) {
        temperature.value = temp
        humidity.value = hum
    }
}