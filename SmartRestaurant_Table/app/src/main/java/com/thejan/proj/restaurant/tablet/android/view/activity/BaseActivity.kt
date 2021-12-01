package com.thejan.proj.restaurant.tablet.android.view.activity

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.thejan.proj.restaurant.tablet.android.helper.AppUtils
import com.thejan.proj.restaurant.tablet.android.helper.SharedPref

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
        val table = SharedPref.getString(SharedPref.TABLE_NUMBER, "0")
        val numberOfSeats = SharedPref.getInteger(SharedPref.NUMBER_OF_SEATS, 0)
        SharedPref.clearSharedPref()
        SharedPref.saveString(SharedPref.TABLE_NUMBER, table!!)
        SharedPref.saveInteger(SharedPref.NUMBER_OF_SEATS, numberOfSeats!!)
        startActivity(Intent(this, LoginActivity::class.java))
        finishAffinity()
    }
}
