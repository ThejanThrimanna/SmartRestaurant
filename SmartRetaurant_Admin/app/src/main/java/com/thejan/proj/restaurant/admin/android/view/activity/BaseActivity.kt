package com.thejan.proj.restaurant.admin.android.view.activity

import android.app.Dialog
import android.content.Intent
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.thejan.proj.restaurant.admin.android.helper.AppUtils
import com.thejan.proj.restaurant.admin.android.helper.SharedPref

/**
 * Created by thejanthrimanna on 2020-10-19.
 */
open class BaseActivity : AppCompatActivity() {

    lateinit var dialog: Dialog

    fun removeStatusBar() {
        this.window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
    }

    override fun onStart() {
        super.onStart()
        setProgress()
    }

    private fun setProgress() {
        dialog = AppUtils.showProgress(this)
    }

    fun showProgress() {
        if (!dialog.isShowing) {
            dialog.setCancelable(true)
            dialog.show()
        }
    }

    fun hideProgress() {
        try {
            dialog.dismiss()
        } catch (e: Exception) {
            println(e)
        }
    }

    fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    fun gotoLogin() {
        SharedPref.saveBoolean(SharedPref.IS_LOGIN, false)
        startActivity(Intent(this, LoginActivity::class.java))
        finishAffinity()
    }
}
