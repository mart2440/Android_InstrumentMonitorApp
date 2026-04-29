package com.seniordesign.instrumentmonitor

import android.util.Log
import kotlin.random.Random
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import kotlin.code
import kotlin.text.toFloat


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

                Log.d("JOIN_CLASS", "Response: ${response.code}")
                Log.d("JOIN_CLASS", response.body?.string() ?: "empty")

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
                // Path: sensors/students/{studentId}.json
                // Your ESP32 should write to this path per student
                val url =
                    "https://senior-design-sensor-data.s3.us-east-2.amazonaws.com/sensors/students/$studentId.json"

                val request = Request.Builder().url(url).build()
                val response = client.newCall(request).execute()

                if (!response.isSuccessful) return@withContext emptyList()

                val json = response.body?.string() ?: return@withContext emptyList()

                // Handles both a JSON array of points OR a single object
                return@withContext try {
                    val array = JSONArray(json)
                    val list = mutableListOf<SensorPoint>()
                    for (i in 0 until array.length()) {
                        val obj = array.getJSONObject(i)
                        list.add(
                            SensorPoint(
                                timestamp = obj.optString("timestamp"),
                                temperature = obj.optDouble("temp", 0.0).toFloat(),
                                humidity    = obj.optDouble("hum",  0.0).toFloat()
                            )
                        )
                    }
                    list
                } catch (e: Exception) {
                    // Single-object fallback (matches your current latest.json shape)
                    val obj = JSONObject(json)
                    listOf(
                        SensorPoint(
                            timestamp   = obj.optString("timestamp"),
                            temperature = obj.optDouble("temp", 0.0).toFloat(),
                            humidity    = obj.optDouble("hum",  0.0).toFloat()
                        )
                    )
                }

            } catch (e: Exception) {
                Log.e("AWS", "getStudentHistory failed for $studentId", e)
                emptyList()
            }
        }

    suspend fun getStudentsByClass(classId: String): List<Student> {
        return withContext(Dispatchers.IO) {
            try {

                val url =
                    "https://5og4l4qmyl.execute-api.us-east-2.amazonaws.com/dev/getStudentsByClass"

                val jsonBody = """{"classCode":"$classId"}"""

                val requestBody =
                    jsonBody.toRequestBody("application/json".toMediaType())

                val request = okhttp3.Request.Builder()
                    .url(url)
                    .post(requestBody)
                    .build()

                val response = client.newCall(request).execute()
                val jsonString = response.body?.string() ?: return@withContext emptyList()

                val array = org.json.JSONArray(jsonString)

                val list = mutableListOf<Student>()

                for (i in 0 until array.length()) {
                    val obj = array.getJSONObject(i)

                    list.add(
                        Student(
                            id = obj.optString("studentId"),
                            name = obj.optString("name"),
                            instrument = obj.optString("instrument"),
                            classroomCode = obj.optString("classCode")
                        )
                    )
                }

                list

            } catch (e: Exception) {
                emptyList()
            }
        }
    }

    // Create class for student
    suspend fun createClass(): String {
        return withContext(Dispatchers.IO) {

            val url =
                "https://5og4l4qmyl.execute-api.us-east-2.amazonaws.com/dev/createClass"

            val jsonBody = """
        {
            "teacherId": "${SessionManager.currentUser?.email ?: "unknown"}"
        }
        """.trimIndent()

            val requestBody =
                jsonBody.toRequestBody("application/json".toMediaType())

            val request = Request.Builder()
                .url(url)
                .post(requestBody)
                .build()

            val response = client.newCall(request).execute()
            val bodyString = response.body?.string()

            if (!response.isSuccessful) {
                throw Exception("HTTP error: ${response.code}")
            }

            if (bodyString.isNullOrBlank()) {
                throw Exception("Empty response from AWS")
            }

            val outer = org.json.JSONObject(bodyString)

            // CASE 1: API Gateway proxy format
            val innerString = if (outer.has("body")) {
                outer.getString("body")
            } else {
                bodyString
            }

            val inner = org.json.JSONObject(innerString)

            if (!inner.has("classId")) {
                throw Exception("Missing classId in response: $inner")
            }

            return@withContext inner.getString("classId")
        }
    }

    // API call to add student
    suspend fun joinClass(student: Student) {
        withContext(Dispatchers.IO) {
            val url = "https://5og4l4qmyl.execute-api.us-east-2.amazonaws.com/dev/joinClass"

            // ✅ Log all fields so we can see exactly what's being sent
            Log.d("JOIN_CLASS", "studentId:    ${student.id}")
            Log.d("JOIN_CLASS", "name:         ${student.name}")
            Log.d("JOIN_CLASS", "instrument:   ${student.instrument}")
            Log.d("JOIN_CLASS", "classroomCode:${student.classroomCode}")

            if (student.classroomCode.isBlank()) {
                throw Exception("classroomCode is blank — student will not be findable by class")
            }

            val jsonBody = JSONObject().apply {
                put("studentId",  student.id)
                put("name",       student.name)
                put("instrument", student.instrument)
                put("classCode",  student.classroomCode)
            }.toString()

            Log.d("JOIN_CLASS", "JSON body: $jsonBody")

            val requestBody = jsonBody.toRequestBody("application/json".toMediaType())

            val request = Request.Builder()
                .url(url)
                .post(requestBody)
                .build()

            val response = client.newCall(request).execute()
            val responseBody = response.body?.string() ?: "null"

            Log.d("JOIN_CLASS", "Response code: ${response.code}")
            Log.d("JOIN_CLASS", "Response body: $responseBody")

            if (!response.isSuccessful) {
                throw Exception("Join failed: ${response.code} — $responseBody")
            }
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

                val modeValue = when {
                    obj.has("mode") -> obj.optString("mode")
                    obj.has("loc") -> obj.optString("loc")
                    obj.has("location") -> obj.optString("location")
                    else -> "unknown"
                }

                SensorData(
                    temperature = obj.optDouble("temp", 0.0),
                    humidity = obj.optDouble("hum", 0.0),
                    battery = obj.optDouble("batt", 0.0),
                    mode = modeValue
                )

            } catch (e: Exception) {
                Log.e("AWS", "Live fetch failed", e)

                // fallback values (optional)
                SensorData(0.0, 0.0, 0.0, "unknown")
            }
        }

    // Remove a student from the class roster
    suspend fun removeStudent(studentId: String, classCode: String) {
        withContext(Dispatchers.IO) {
            val url = "https://5og4l4qmyl.execute-api.us-east-2.amazonaws.com/dev/removeStudent"

            val jsonBody = """{"studentId":"$studentId","classCode":"$classCode"}"""

            val requestBody = jsonBody.toRequestBody("application/json".toMediaType())

            val request = Request.Builder()
                .url(url)
                .post(requestBody)
                .build()

            val response = client.newCall(request).execute()

            if (!response.isSuccessful) {
                throw Exception("Remove failed: ${response.code}")
            }
        }
    }
}


