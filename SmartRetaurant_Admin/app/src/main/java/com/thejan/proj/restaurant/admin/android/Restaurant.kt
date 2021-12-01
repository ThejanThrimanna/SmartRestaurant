package com.thejan.proj.restaurant.admin.android

import android.app.Application

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
