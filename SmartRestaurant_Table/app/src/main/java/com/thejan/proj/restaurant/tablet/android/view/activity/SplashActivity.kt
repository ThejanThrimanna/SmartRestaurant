package com.thejan.proj.restaurant.tablet.android.view.activity

import android.content.Context
import android.content.Intent
import android.net.wifi.WifiManager
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.thejan.proj.restaurant.tablet.android.R
import com.thejan.proj.restaurant.tablet.android.helper.AppUtils
import com.thejan.proj.restaurant.tablet.android.helper.SharedPref
import com.thejan.proj.restaurant.tablet.android.viewmodel.SplashScreenViewModel
import com.thejan.proj.restaurant.tablet.android.viewmodel.Status
import com.thejan.proj.restaurant.tablet.android.viewmodel.ViewModelState


class SplashActivity : BaseActivity() {

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
        setContentView(R.layout.activity_splash)
        init()
    }

    override fun onResume() {
        super.onResume()
        val view1 = findViewById<View>(R.id.tvCompanyName) as TextView
        val animation0 = AnimationUtils.loadAnimation(this, R.anim.zoom_out)
        view1.startAnimation(animation0)
        Handler().postDelayed(RunSplash(), 1)
    }

    internal inner class RunSplash : Runnable {
        override fun run() {
            val address = AppUtils.getUniqueId(this@SplashActivity)
            mViewModel.checkTable(address)
        }
    }

    private fun openNext() {
        if (SharedPref.getBoolean(SharedPref.IS_LOGIN, false)) {
            val intent = Intent(this@SplashActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        } else {
            val intent = Intent(this@SplashActivity, LoginActivity::class.java)
            intent.putExtra("table", tableFromResponse)
            startActivity(intent)
            finish()
        }
    }

    private fun initViewModel() {
        mViewModel = ViewModelProviders.of(this).get(SplashScreenViewModel::class.java)
    }

    private fun initSubscription() {
        mViewModel.message.observe(this, message)
        mViewModel.table.observe(this, table)
        mViewModel.state!!.observe(this, Observer<ViewModelState> {
            it?.let {
                update(it)
            }
        })
    }

    private fun update(state: ViewModelState) {
        when (state.status) {
            Status.LOADING -> {
                showProgress()
            }

            Status.SUCCESS -> {
                openNext()
            }

            Status.SUB_SUCCESS1 -> {
                val intent = Intent(this@SplashActivity, TableRegisterActivity::class.java)
                startActivity(intent)
                finish()
            }

            Status.ERROR -> {
                hideProgress()
                showToast(getString(R.string.login_failed))
            }
        }
    }

    private fun init() {
        initView()
        initViewModel()
        initSubscription()
    }

    private fun initView() {
        removeStatusBar()
    }
}
