package com.thejan.proj.restaurant.tv.smartrestaurant_mobile.view.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.lifecycle.Observer
import com.thejan.proj.restaurant.tablet.android.helper.AppUtils
import com.thejan.proj.restaurant.tv.smartrestaurant_mobile.helper.SharedPref
import com.thejan.proj.restaurant.tv.smartrestaurant_mobile.R

class SplashActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        init()
    }

    override fun onResume() {
        super.onResume()
        val view1 = findViewById<View>(R.id.tvCompanyName) as TextView
        val animation0 = AnimationUtils.loadAnimation(this, R.anim.zoom_out)
        view1.startAnimation(animation0)
        Handler().postDelayed(RunSplash(), 1000)
    }

    internal inner class RunSplash : Runnable {
        override fun run() {
            openNext()
        }
    }

    private fun openNext() {
        if (SharedPref.getBoolean(SharedPref.IS_LOGIN, false)) {
            val intent = Intent(this@SplashActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        } else {
            val intent = Intent(this@SplashActivity, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }


    private fun init() {
        initView()
    }

    private fun initView() {
        removeStatusBar()
    }
}