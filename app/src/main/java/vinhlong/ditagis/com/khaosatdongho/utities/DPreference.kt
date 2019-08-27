package vinhlong.ditagis.com.khaosatdongho.utities

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences

import android.content.Context.MODE_PRIVATE

/**
 * Created by ThanLe on 4/11/2018.
 */

class DPreference private constructor() {
    private var mContext: Context? = null

    private val preferences: SharedPreferences
        get() = mContext!!.getSharedPreferences("LOGGED_IN", MODE_PRIVATE)

    fun setContext(context: Context) {
        mContext = context
    }

    /**
     * Method used to save Preferences
     */
    fun savePreferences(key: String, value: String) {
        val sharedPreferences = preferences
        val editor = sharedPreferences.edit()
        editor.putString(key, value)
        editor.apply()
    }

    fun savePreferences(key: String, values: Set<String>) {
        val sharedPreferences = preferences
        val editor = sharedPreferences.edit()
        editor.putStringSet(key, values)
        editor.apply()
    }

    fun deletePreferences(key: String): Boolean {
        val editor = preferences.edit()
        editor.remove(key).apply()
        return false
    }

    fun deletePreferences(): Boolean {
        val editor = preferences.edit()
        editor.clear().apply()
        return false
    }

    /**
     * Method used to load Preferences
     */
    fun loadPreference(key: String): String? {
        try {
            val sharedPreferences = preferences
            return sharedPreferences.getString(key, "")
        } catch (nullPointerException: NullPointerException) {
            return null
        }

    }

    fun loadPreferences(key: String): Set<String>? {
        try {
            val sharedPreferences = preferences
            return sharedPreferences.getStringSet(key, null)
        } catch (nullPointerException: NullPointerException) {
            return null
        }

    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        private var mInstance: DPreference? = null

        val instance: DPreference
            get() {
                if (mInstance == null)
                    mInstance = DPreference()
                return mInstance as DPreference
            }
    }
}
