package com.thejan.proj.restaurant.admin.android.view.activity

import android.content.Intent
import android.net.wifi.WifiManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.thejan.proj.restaurant.admin.android.R
import com.thejan.proj.restaurant.admin.android.helper.SharedPref
import com.thejan.proj.restaurant.admin.android.viewmodel.SplashScreenViewModel

class SplashScreenActivity : BaseActivity() {

    private lateinit var messageFromResponse: String
    private lateinit var tableFromResponse: String
    private lateinit var mViewModel: SplashScreenViewModel

    var message = Observer<String> { newName ->
        messageFromResponse = newName!!
    }

    var table = Observer<String> { newName ->
        tableFromResponse = newName!!
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

    }

    override fun onResume() {
        super.onResume()
        val view1 = findViewById<View>(R.id.tvCompanyName) as TextView
        val animation0 = AnimationUtils.loadAnimation(this, R.anim.zoom_out)
        view1.startAnimation(animation0)
        Handler().postDelayed(RunSplash(), 1500)
    }

    internal inner class RunSplash : Runnable {
        override fun run() {
            openNext()
        }
    }

    private fun openNext() {
        if (SharedPref.getBoolean(SharedPref.IS_LOGIN, false)) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        } else {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

}