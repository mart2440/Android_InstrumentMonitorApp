package com.seniordesign.instrumentmonitor

import android.content.Context

object SessionStorage {

    private const val PREFS_NAME = "session_prefs"
    private const val KEY_EMAIL = "email"
    private const val KEY_FIRST = "first"
    private const val KEY_LAST = "last"

    fun saveSession(context: Context, user: UserSession) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit()
            .putString(KEY_EMAIL, user.email)
            .putString(KEY_FIRST, user.firstName)
            .putString(KEY_LAST, user.lastName)
            .apply()
    }

    fun loadSession(context: Context): UserSession? {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

        val email = prefs.getString(KEY_EMAIL, null) ?: return null
        val first = prefs.getString(KEY_FIRST, "") ?: ""
        val last = prefs.getString(KEY_LAST, "") ?: ""

        return UserSession(first, last, email)
    }

    fun clearSession(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().clear().apply()
    }
}