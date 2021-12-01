package com.thejan.proj.restaurant.tv.android.view.activity

import android.app.Dialog
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.thejan.proj.restaurant.tablet.android.helper.AppUtils

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

}
