package com.thejan.proj.restaurant.tv.smartrestaurant_mobile.view.activity

import android.app.AlertDialog
import android.os.Bundle
import com.thejan.proj.restaurant.tablet.android.helper.AppUtils
import com.thejan.proj.restaurant.tv.smartrestaurant_mobile.R
import com.thejan.proj.restaurant.tv.smartrestaurant_mobile.viewmodel.AccountViewModel
import kotlinx.android.synthetic.main.activity_account.*

class AccountActivity : BaseActivity() {
    private lateinit var mViewModel: AccountViewModel
    private lateinit var messageFromResponse: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account)
        init()
    }

    private fun init(){
        initAction()
    }

    private fun initAction(){
        btnLogout.setOnClickListener {
            AlertDialog.Builder(this)
                .setMessage(getString(R.string.are_you_sure_logout))
                .setIcon(R.drawable.ic_warning_black_transparent)
                .setPositiveButton(
                    getString(R.string.yes)
                ) { dialog, whichButton ->
                   gotoLogin()
                }
                .setNegativeButton(getString(R.string.cancel), null).show()
                .show()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        AppUtils.closeActivityLeftToRight(this)
    }
}