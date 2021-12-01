package com.thejan.proj.restaurant.admin.android.helper

import android.content.Context
import com.thejan.proj.restaurant.admin.android.Restaurant

/**
 * Created by thejanthrimanna on 2020-08-26.
 */
object SharedPref {

    const val FILE_KEY = "restaurant.admin.file.key"
    const val IS_LOGIN = "restaurant.file.key.is.login"
    const val PHONE = "restaurant.file.key.is.phone"
    const val EMP_ID = "restaurant.file.key.is.emp.id"
    const val USER_ROLE = "restaurant.file.key.is.user.role"
    const val USER_NAME = "restaurant.file.key.is.user.name"

    fun saveString(key: String, value: String) {
        val editor = Restaurant.instance.getSharedPreferences(
            FILE_KEY, Context.MODE_PRIVATE).edit()
        //        editor.putString(Encryption.getInstance().encrypt(key), Encryption.getInstance().encrypt(value));
        editor.putString(key, value)
        editor.commit()
    }


    fun saveBoolean(key: String, value: Boolean) {
        val editor = Restaurant.instance.getSharedPreferences(
            FILE_KEY, Context.MODE_PRIVATE).edit()
        editor.putBoolean(key, value)
        editor.commit()
    }

    fun getString(key: String, defaultval: String): String? {
        val prefs = Restaurant.instance.getSharedPreferences(
            FILE_KEY,
            Context.MODE_PRIVATE)
        //        return Encryption.getInstance().decrypt(prefs.getString(Encryption.getInstance().encrypt(key), defaultval));
        return prefs.getString(key, defaultval)
    }

    fun getBoolean(key: String, defaultval: Boolean): Boolean {
        val prefs = Restaurant.instance.getSharedPreferences(
            FILE_KEY,
            Context.MODE_PRIVATE)
        return prefs.getBoolean(key, defaultval)
    }

    fun saveInteger(key: String, value: Int?) {
        val editor = Restaurant.instance.getSharedPreferences(
            FILE_KEY, Context.MODE_PRIVATE).edit()
        editor.putInt(key, value!!)
        //        editor.putString(Encryption.getInstance().encrypt(key), Encryption.getInstance().encrypt(String.valueOf(value)));
        editor.commit()
    }

    fun getInteger(key: String, defaultval: Int): Int {
        val prefs = Restaurant.instance.getSharedPreferences(
            FILE_KEY,
            Context.MODE_PRIVATE)
        return prefs.getInt(key, defaultval)
        //        return Integer.parseInt(Encryption.getInstance().decrypt(prefs.getString(Encryption.getInstance().encrypt(key), String.valueOf(defaultval))));
    }

    fun saveLong(key: String, value: Long) {
        val editor = Restaurant.instance.getSharedPreferences(
            FILE_KEY, Context.MODE_PRIVATE).edit()
        editor.putLong(key, value)
        editor.commit()
    }

    fun getLong(key: String, defaultval: Long): Long {
        val prefs = Restaurant.instance.getSharedPreferences(
            FILE_KEY,
            Context.MODE_PRIVATE)
        return prefs.getLong(key, defaultval)
    }

    fun saveFloat(key: String, value: Float) {
        val editor = Restaurant.instance.getSharedPreferences(
            FILE_KEY, Context.MODE_PRIVATE).edit()
        editor.putFloat(key, value)
        editor.commit()
    }


    fun getFloat(key: String, defaultval: Float): Float {
        val prefs = Restaurant.instance.getSharedPreferences(
            FILE_KEY,
            Context.MODE_PRIVATE)
        return prefs.getFloat(key, defaultval)
    }


    fun deleteString(key: String) {
        val editor = Restaurant.instance.getSharedPreferences(
            FILE_KEY, Context.MODE_PRIVATE).edit()
        editor.remove(key)
        editor.commit()
    }

    fun deleteInt(key: String) {
        val editor = Restaurant.instance.getSharedPreferences(
            FILE_KEY, Context.MODE_PRIVATE).edit()
        editor.remove(key)
        editor.commit()
    }

    fun clearSharedPref() {
        val editor = Restaurant.instance.getSharedPreferences(
            FILE_KEY, Context.MODE_PRIVATE).edit()
        editor.clear()
        editor.commit()
    }
}


