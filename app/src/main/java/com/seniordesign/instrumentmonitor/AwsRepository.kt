package com.seniordesign.instrumentmonitor

import kotlin.random.Random
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object AwsRepository {

    private val client = OkHttpClient()

    // Function for Sensor Data
    fun getSensorData(): SensorData {
        return SensorData(
            temperature = Random.nextDouble(18.0, 30.0),
            humidity = Random.nextDouble(40.0, 80.0)
        )
    }

    // Function for graph
    suspend fun getGraphData(fileName: String): List<SensorPoint> =
        withContext(Dispatchers.IO) {

            try {
                val url =
                    "https://senior-design-sensor-data.s3.amazonaws.com/sensors/demo/$fileName"

                val request = Request.Builder().url(url).build()
                val response = client.newCall(request).execute()

                val jsonString = response.body?.string() ?: return@withContext emptyList()

                val jsonArray = JSONArray(jsonString)

                val list = mutableListOf<SensorPoint>()

                for (i in 0 until jsonArray.length()) {
                    val obj = jsonArray.getJSONObject(i)

                    list.add(
                        SensorPoint(
                            timestamp = obj.optString("timestamp"),
                            temperature = obj.optDouble("temperatureF", 0.0).toFloat(), // ✅ FIXED
                            humidity = obj.optDouble("humidity", 0.0).toFloat()
                        )
                    )
                }

                list

            } catch (e: Exception) {
                e.printStackTrace()
                emptyList()
            }
        }
}