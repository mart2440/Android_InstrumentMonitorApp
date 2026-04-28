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
            humidity = Random.nextDouble(40.0, 80.0),
            battery = 4.0,              // mock value
            mode = "case"               // mock value
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

    suspend fun getAllSensorData(): List<SensorPoint> =
        withContext(Dispatchers.IO) {

            return@withContext try {

                DataStatus.lastStatus = "Loading full dataset..."

                val bucketUrl =
                    "https://senior-design-sensor-data.s3.us-east-2.amazonaws.com/sensors/real/"

                val fallback = mutableListOf<SensorPoint>()

                // Since we can't list S3 directly via HTTP:
                // we rely on parsing "demo" or indexed files if available

                val request = Request.Builder()
                    .url("${bucketUrl}latest.json")
                    .build()

                val response = client.newCall(request).execute()

                val latestJson = response.body?.string()

                if (!latestJson.isNullOrEmpty()) {
                    val obj = org.json.JSONObject(latestJson)

                    fallback.add(
                        SensorPoint(
                            timestamp = obj.optString("timestamp", ""),
                            temperature = obj.optDouble("temp", 0.0).toFloat(),
                            humidity = obj.optDouble("hum", 0.0).toFloat()
                        )
                    )
                }

                DataStatus.lastStatus = "Dataset loaded (partial)"
                fallback

            } catch (e: Exception) {
                DataStatus.lastStatus = "Failed loading dataset"
                DataStatus.lastError = e.message
                emptyList()
            }
        }

    // -------------- ADMIN CONSOLE: STUDENT CONDITION HISTORY ------------------
    suspend fun getStudentHistory(studentId: String): List<SensorPoint> =
        withContext(Dispatchers.IO) {

            return@withContext try {

                val result = mutableListOf<SensorPoint>()

                val url =
                    "https://senior-design-sensor-data.s3.us-east-2.amazonaws.com/sensors/real/"

                // ⚠️ BEST PRACTICE WOULD BE INDEX FILE OR DB
                // For now we simulate by fetching latest only

                val request = Request.Builder()
                    .url("${url}latest.json")
                    .build()

                val response = client.newCall(request).execute()

                val json = response.body?.string() ?: return@withContext emptyList()

                val obj = org.json.JSONObject(json)

                result.add(
                    SensorPoint(
                        timestamp = obj.optString("timestamp"),
                        temperature = obj.optDouble("temp", 0.0).toFloat(),
                        humidity = obj.optDouble("hum", 0.0).toFloat()
                    )
                )

                result

            } catch (e: Exception) {
                emptyList()
            }
        }

    suspend fun getLatestSensorData(): SensorData =
        withContext(Dispatchers.IO) {
            try {
                val url =
                    "https://senior-design-sensor-data.s3.us-east-2.amazonaws.com/sensors/real/latest.json"

                Log.d("AWS", "Fetching latest sensor data from: $url")

                val request = Request.Builder().url(url).build()
                val response = client.newCall(request).execute()

                if (!response.isSuccessful) {
                    Log.e("AWS", "HTTP error: ${response.code}")
                    throw Exception("HTTP ${response.code}")
                }

                val jsonString = response.body?.string()
                    ?: throw Exception("Empty response body")

                Log.d("AWS", "Response: $jsonString")

                val obj = org.json.JSONObject(jsonString)

                val temp = obj.optDouble("temp", 0.0)
                val hum = obj.optDouble("hum", 0.0)

                Log.d("AWS", "Parsed → temp: $temp, humidity: $hum")

                SensorData(
                    temperature = obj.optDouble("temp", 0.0),
                    humidity = obj.optDouble("hum", 0.0),
                    battery = obj.optDouble("batt", 0.0),
                    mode = obj.optString("location", "unknown")
                )

            } catch (e: Exception) {
                Log.e("AWS", "Live fetch failed", e)

                // fallback values (optional)
                SensorData(0.0, 0.0, 0.0, "unknown")
            }
        }
}