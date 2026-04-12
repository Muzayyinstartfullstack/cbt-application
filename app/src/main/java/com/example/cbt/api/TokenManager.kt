package com.example.cbt.api

import android.content.Context

object TokenManager {
    private const val PREFS_NAME = "cbt_prefs"
    private const val KEY_TOKEN = "jwt_token"

    private var token: String? = null

    fun init(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        token = prefs.getString(KEY_TOKEN, null)
    }

    fun saveToken(context: Context, newToken: String) {
        token = newToken
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit().putString(KEY_TOKEN, newToken).apply()
    }

    fun getToken(): String? = token

    fun clearToken(context: Context) {
        token = null
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit().remove(KEY_TOKEN).apply()
    }
}