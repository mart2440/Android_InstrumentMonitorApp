package com.seniordesign.instrumentmonitor

import android.util.Log
import kotlin.random.Random
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


object AwsRepository {

    private val client = OkHttpClient()

    // Function for Sensor Data (mock/local)
    fun getSensorData(): SensorData {
        return SensorData(
            temperature = Random.nextDouble(18.0, 30.0),
            humidity = Random.nextDouble(40.0, 80.0)
        )
    }

    // Function for graph (S3 fetch)
    suspend fun getGraphData(fileName: String): List<SensorPoint> =
        withContext(Dispatchers.IO) {

            return@withContext try {

                DataStatus.lastStatus = "Fetching data from AWS..."

                val url =
                    "https://senior-design-sensor-data.s3.us-east-2.amazonaws.com/sensors/demo/$fileName"

                Log.d("AWS", "Request URL: $url")

                val request = Request.Builder().url(url).build()
                val response = client.newCall(request).execute()

                Log.d("AWS", "Response code: ${response.code}")

                if (!response.isSuccessful) {
                    DataStatus.lastStatus = "Failed (HTTP ${response.code})"
                    DataStatus.lastError = response.message
                    throw Exception("HTTP ${response.code}")
                }

                val jsonString = response.body?.string()

                if (jsonString.isNullOrEmpty()) {
                    DataStatus.lastStatus = "Empty response"
                    DataStatus.lastError = "Body was null"
                    throw Exception("Empty response body")
                }

                val jsonArray = JSONArray(jsonString)

                val list = mutableListOf<SensorPoint>()

                for (i in 0 until jsonArray.length()) {
                    val obj = jsonArray.getJSONObject(i)

                    list.add(
                        SensorPoint(
                            timestamp = obj.optString("timestamp"),
                            temperature = obj.optDouble("temperatureF", 0.0).toFloat(),
                            humidity = obj.optDouble("humidity", 0.0).toFloat()
                        )
                    )
                }

                DataStatus.lastStatus = "Live data loaded ✔"
                DataStatus.lastError = null

                list

            } catch (e: Exception) {
                Log.e("AWS", "Error fetching data", e)
                DataStatus.lastStatus = "Error loading data"
                DataStatus.lastError = e.message
                emptyList()
            }
        }
}