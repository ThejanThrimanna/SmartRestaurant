package com.thejan.proj.restaurant.tv.smartrestaurant_mobile.view.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.thejan.proj.restaurant.tablet.android.helper.AppUtils
import com.thejan.proj.restaurant.tablet.android.viewmodel.Status
import com.thejan.proj.restaurant.tablet.android.viewmodel.ViewModelState
import com.thejan.proj.restaurant.tv.smartrestaurant_mobile.R
import com.thejan.proj.restaurant.tv.smartrestaurant_mobile.model.TableReservation
import com.thejan.proj.restaurant.tv.smartrestaurant_mobile.viewmodel.LoginViewModel
import kotlinx.android.synthetic.main.activity_login.*
import java.util.*

class LoginActivity : BaseActivity() {

    private lateinit var messageFromResponse: String
    private lateinit var tableNum: String
    private lateinit var mViewModel: LoginViewModel

    var message = Observer<String> { newName ->
        messageFromResponse = newName!!
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        init()
    }

    private fun init() {
        removeStatusBar()
        initAction()
        initViewModel()
        initSubscription()
    }

    private fun initViewModel() {
        mViewModel = ViewModelProviders.of(this).get(LoginViewModel::class.java)
    }

    private fun initSubscription() {
        mViewModel.message.observe(this, message)
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
                hideProgress()
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }

            Status.ERROR -> {
                hideProgress()
                showToast(getString(R.string.login_failed))
            }
        }
    }

    private fun initAction() {
        tvRegister.setOnClickListener {
            var intent = Intent(this, RegisterActivity::class.java)
            AppUtils.startActivityRightToLeft(this, intent)
        }
        btnLogin.setOnClickListener {
            if(etPhoneNumber.text.toString().isNotEmpty() && etPassword.text.toString().isNotEmpty())
            mViewModel.login(etPhoneNumber.text.toString(), AppUtils.encrypt(etPassword.text.toString())!!)
            else
                showToast(getString(R.string.please_fill_all_the_fields))
        }
    }

}