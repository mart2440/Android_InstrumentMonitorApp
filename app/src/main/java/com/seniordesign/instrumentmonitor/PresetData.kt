package com.seniordesign.instrumentmonitor

object PresetData {

    val instruments = listOf(
        InstrumentProfile(
            "Violin",
            60.0, 75.0, 40.0, 60.0,
            "High-pitched string instrument used in orchestras and solos.",
            "* Keep in case when not in use\n* Avoid temperature shocks\n* Use a humidifier in winter",
            R.drawable.violin
        ),

        // Instrument Profile for the Profiles tab of navigation bar
        InstrumentProfile(
            "Viola",
            60.0, 75.0, 40.0, 60.0,
            "Mid-range string instrument, deeper than violin.",
            "• Store in stable humidity\n• Avoid direct sunlight\n• Clean after playing",
            R.drawable.viola
        ),

        // Instrument Profile for Cello
        InstrumentProfile(
            "Cello",
            60.0, 75.0, 40.0, 60.0,
            "Low warm tone, played seated with endpin.",
            "• Keep upright safely\n• Monitor humidity closely\n• Use case humidifier",
            R.drawable.cello
        ),

        InstrumentProfile(
            "Bass",
            60.0, 75.0, 40.0, 60.0,
            "Largest string instrument, deepest sound.",
            "• Avoid dry environments\n• Store carefully to prevent warping\n• Regular maintenance",
            R.drawable.bass
        )
    )
}