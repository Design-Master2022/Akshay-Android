package com.newsapp.newsapp.utils

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson

class CommonSharedPreferences {

    companion object {
        // Following are keys of shared preference data
        const val SHARED_PREFS_FILENAME = "biometric_prefs"
        const val CIPHERTEXT_WRAPPER = "ciphertext_wrapper"
        private const val mPreferenceName = "news-app"
        private const val mPreferenceMode = Context.MODE_PRIVATE
        private var sharedPreference: SharedPreferences? = null
        const val IS_LOGGED_IN = "isLoggedIn"
        const val ACCESS_TOKEN = "ACCESS_TOKEN"
        const val TOKEN = "TOKEN"
        const val LANG_ID = "lang_id"
        const val DARK_MODE_ENABLED = "dark_mode_enabled"
        const val SELECTED_CATEGORY = "selected_category"
        const val SELECTED_COUNTRY = "selected_country"



        /** This method is to initialize CommonSharedPreferences
         * @param context which is required to access app shared preference
         */
        fun initialize(context: Context) {
            sharedPreference = context.getSharedPreferences(mPreferenceName, mPreferenceMode)
        }

        fun getToken(): String? {
            return sharedPreference?.getString(ACCESS_TOKEN, null)
        }
        /** This method is to write String data in Shared Preference
         * @param key reference key to write data
         * @param data which is stored in shared preference
         */
        fun writeString(key: String, data: String) {
            sharedPreference?.edit()?.putString(key, data)?.apply()
        }

        /** This method is to write String data in Shared Preference
         * @param key reference key to write data
         * @param data which is stored in shared preference
         */
        fun writeStringSet(key: String, data: Set<String>) {
            val set: Set<String> = HashSet<String>()
            sharedPreference?.edit()?.putStringSet(key, set)?.apply()
            sharedPreference?.edit()?.putStringSet(key, data)?.apply()
        }

        fun removeStringSet(key: String) {
            sharedPreference?.edit()?.remove(key)?.apply()
        }

        /** This method is to write Boolean data in Shared Preference
         * @param key reference key to write  data
         * @param data which is stored in shared preference
         */
        fun writeBoolean(key: String, data: Boolean) {
            sharedPreference?.edit()?.putBoolean(key, data)?.apply()
        }

        /** This method is to write Int data in Shared Preference
         * @param key reference key to write data
         * @param data which is stored in shared preference
         */
        fun writeInt(key: String, data: Int) {
            sharedPreference?.edit()?.putInt(key, data)?.apply()
        }

        /** This method is to write Float data in Shared Preference
         * @param key reference key to write data
         * @param data which is stored in shared preference
         */
        fun writeFloat(key: String, data: Float) {
            sharedPreference?.edit()?.putFloat(key, data)?.apply()
        }

        /** This method is to read Int data from Shared Preference
         * @param key reference key to read data
         * @param default which is default value if actual value is not available
         */
        fun readInt(key: String, default: Int = -1): Int {
            return sharedPreference?.getInt(key, default) ?: default
        }

        /** This method is to read String data from Shared Preference
         * @param key reference key to read data
         * @return default "" value
         */
        @JvmStatic
        fun readString(key: String, default: String = ""): String {
            return sharedPreference?.getString(key, default).toString()
        }

        /** This method is to read String data from Shared Preference
         * @param key reference key to read data
         * @return default "" value
         */
        fun readStringSet(key: String): Set<String> {
            val set: Set<String> = HashSet<String>()
            return sharedPreference?.getStringSet(key, set) as Set<String>
        }

        /** This method is to read Boolean data from Shared Preference
         * @param key reference key to read data
         * @return default value as false
         */
        fun readBoolean(key: String): Boolean {
            return sharedPreference?.getBoolean(key, false) ?: false
        }


        /** This method is to read Float data from Shared Preference
         * @param key reference key to read data
         * @param default default value of float
         */
        fun readFloat(key: String, default: Float): Float {
            return sharedPreference?.getFloat(key, default) ?: default
        }

        fun clearPreferences() {

            sharedPreference?.edit()?.remove(IS_LOGGED_IN)?.apply()
        }
    }
}