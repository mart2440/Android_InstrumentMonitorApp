package com.seniordesign.instrumentmonitor

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID

object SessionStorage {
    private const val KEY_ROLE = "role"
    private const val PREFS_NAME = "session_prefs"
    private const val KEY_EMAIL = "email"
    private const val KEY_FIRST = "first"
    private const val KEY_LAST = "last"
    private const val KEY_STUDENTS = "students"
    private const val KEY_CLASSROOM = "classroom"

    fun saveSession(context: Context, user: UserSession) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit()
            .putString(KEY_EMAIL, user.email)
            .putString(KEY_FIRST, user.firstName)
            .putString(KEY_LAST, user.lastName)
            .putString(KEY_ROLE, user.role)
            .apply()
    }

    fun loadSession(context: Context): UserSession? {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

        val email = prefs.getString(KEY_EMAIL, null) ?: return null
        val first = prefs.getString(KEY_FIRST, "") ?: ""
        val last = prefs.getString(KEY_LAST, "") ?: ""
        val role = prefs.getString(KEY_ROLE, "student") ?: "student"

        return UserSession(first, last, email, role = role)
    }

    fun clearSession(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().clear().apply()
    }

    // Student lists for admin console view
    fun saveStudents(context: Context, students: List<Student>) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

        val json = JSONArray()

        students.forEach {
            val obj = JSONObject().apply {
                put("id", it.id)
                put("name", it.name)
                put("instrument", it.instrument)
                put("classroomCode", it.classroomCode)
            }
            json.put(obj)
        }

        prefs.edit().putString(KEY_STUDENTS, json.toString()).apply()
    }

    fun loadStudents(context: Context): List<Student> {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val raw = prefs.getString(KEY_STUDENTS, null) ?: return emptyList()

        return try {
            val array = JSONArray(raw)
            val list = mutableListOf<Student>()

            for (i in 0 until array.length()) {
                val obj = array.optJSONObject(i) ?: continue

                list.add(
                    Student(
                        id = obj.optString("id", ""),
                        name = obj.optString("name", "Unknown"),
                        instrument = obj.optString("instrument", "Unknown"),
                        classroomCode = obj.optString("classroomCode", "")
                    )
                )
            }

            list
        } catch (e: Exception) {
            prefs.edit().remove(KEY_STUDENTS).apply()
            emptyList()
        }
    }

    fun generateJoinCode(): String {
        val chars = ('A'..'Z') + ('0'..'9')
        return (1..6)
            .map { chars.random() }
            .joinToString("")
    }

    fun saveClassroom(context: Context, classroom: Classroom) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

        val json = JSONObject().apply {
            put("id", classroom.id)
            put("name", classroom.name)
            put("joinCode", classroom.joinCode)
        }

        prefs.edit().putString("classroom", json.toString()).apply()
    }

    fun loadClassroom(context: Context): Classroom? {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val raw = prefs.getString("classroom", null) ?: return null

        return try {
            val obj = JSONObject(raw)

            Classroom(
                id = obj.optString("id", ""),
                name = obj.optString("name", "Music Class"),
                joinCode = obj.optString("joinCode", generateJoinCode())
            )
        } catch (e: Exception) {
            prefs.edit().remove("classroom").apply()
            null
        }
    }
}