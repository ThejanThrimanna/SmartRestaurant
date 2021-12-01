package com.thejan.proj.restaurant.tv.android

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate

/**
 * Created by thejanthrimanna on 2020-08-26.
 */
class Restaurant : Application() {
    companion object {
        lateinit var instance: Restaurant
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }
}
