package com.shepherdapp.app.utils

/**
 * Created by Deepak Rattan on 27/05/22
 */
import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.shepherdapp.app.data.dto.resource.CategoryData
import java.lang.reflect.Type


class Prefs @SuppressLint("CommitPrefEdits") internal constructor(context: Context) {
    fun save(key: String?, value: Boolean) {
        editor.putBoolean(key, value).apply()
    }

    fun save(key: String?, value: String?) {
        editor.putString(key, value).apply()
    }

    fun save(key: String?, value: Int) {
        editor.putInt(key, value).apply()
    }

    fun save(key: String?, value: Float) {
        editor.putFloat(key, value).apply()
    }

    fun save(key: String?, value: Long) {
        editor.putLong(key, value).apply()
    }

    fun save(key: String?, value: Set<String?>?) {
        editor.putStringSet(key, value).apply()
    }

    fun save(key: String?, value: Double) {
        editor.putLong(key, java.lang.Double.doubleToRawLongBits(value)).apply()
    }

    // to save object in preference
    fun save(key: String?, `object`: Any?) {

        editor.putString(key, GSON.toJson(`object`)).apply()
    }

    // To get object from prefrences
    fun <T> getObject(key: String, a: Class<T>?): T? {
        val gson = preferences.getString(key, null)
        return if (gson == null) {
            null
        } else {
            try {
                GSON.fromJson(gson, a)
            } catch (e: Exception) {
                throw IllegalArgumentException(
                    "Object storaged with key "
                            + key + " is instanceof other class"
                )
            }
        }
    }

    fun getBoolean(key: String?, defValue: Boolean = false): Boolean {
        return preferences.getBoolean(key, defValue)
    }

    fun getString(key: String?, defValue: String? = ""): String? {
        return preferences.getString(key, defValue)
    }

    fun getInt(key: String?, defValue: Int = 0): Int {
        return preferences.getInt(key, defValue)
    }

    fun getFloat(key: String?, defValue: Float = 0f): Float {
        return preferences.getFloat(key, defValue)
    }

    fun getDouble(key: String?, defaultValue: Double = 0.0): Double {
        return java.lang.Double.longBitsToDouble(
            preferences.getLong(
                key,
                java.lang.Double.doubleToLongBits(defaultValue)
            )
        )
    }

    fun getLong(key: String?, defValue: Long = 0): Long {
        return preferences.getLong(key, defValue)
    }

    fun getStringSet(
        key: String?,
        defValue: Set<String?>?
    ): Set<String>? {
        return preferences.getStringSet(key, defValue)
    }

    val all: Map<String, *>
        get() = preferences.all

    fun remove(key: String?) {
        editor.remove(key).apply()
    }

    fun removeAll() {
        editor.clear()
        editor.apply()
    }

    // Save array

    fun saveArrayList(key: String, list: ArrayList<Int>) {
        val gson = Gson()
        val json: String = gson.toJson(list)
        editor.putString(key, json)
        editor.apply()
    }

    fun getArrayList(key: String?): java.util.ArrayList<Int?>? {
        val gson = Gson()
        val json: String? = preferences.getString(key, null)
        val type: Type? = object : TypeToken<ArrayList<String?>?>() {}.type
        return gson.fromJson(json, type)
    }


    fun saveArrayLst(key: String, list: ArrayList<CategoryData>) {
        val gson = Gson()
        val json: String = gson.toJson(list)
        editor.putString(key, json)
        editor.apply()
    }

    fun getArrayLst(key: String?): java.util.ArrayList<CategoryData?>? {
        val gson = Gson()
        val json: String? = preferences.getString(key, null)
        val type: Type? = object : TypeToken<ArrayList<String?>?>() {}.type
        return gson.fromJson(json, type)
    }

    private class Builder(context: Context?) {
        private val context: Context

        /**
         * Method that creates an instance of Prefs
         *
         * @return an instance of Prefs
         */
        fun build(): Prefs {
            return Prefs(context)
        }

        init {
            requireNotNull(context) { "Context must not be null." }
            this.context = context.applicationContext
        }
    }

    var preferences: SharedPreferences
    var editor: SharedPreferences.Editor

    companion object {
        private const val TAG = "ShepherdPreferences"
        var singleton: Prefs? = null


        private val GSON = Gson()
        fun with(context: Context?): Prefs? {
            if (singleton == null) {
                singleton = Builder(context).build()
            }
            return singleton
        }
    }

    init {
        preferences =
            context.getSharedPreferences(TAG, Context.MODE_PRIVATE)
        editor = preferences.edit()
    }
}