package com.yy4787.firebasefleemarket.data

import android.content.Context
import android.preference.PreferenceManager

class AppPreferences(context: Context) {

    companion object {
        private const val KEY_CURRENT_UID = "current_uid"

        private var instance: AppPreferences? = null

        fun getInstance(context: Context): AppPreferences {
            if (instance == null) {
                instance = AppPreferences(context)
            }
            return instance!!
        }
    }

    private val preferences = PreferenceManager.getDefaultSharedPreferences(context)

    var currentUid: String?
        get() = preferences.getString(KEY_CURRENT_UID, null)
        set(value) {
            preferences.edit().putString(KEY_CURRENT_UID, value).apply()
        }
}