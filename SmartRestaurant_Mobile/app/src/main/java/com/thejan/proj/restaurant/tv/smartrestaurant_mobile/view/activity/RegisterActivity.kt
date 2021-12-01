package com.thejan.proj.restaurant.tv.smartrestaurant_mobile.view.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.thejan.proj.restaurant.tablet.android.helper.AppUtils
import com.thejan.proj.restaurant.tablet.android.viewmodel.Status
import com.thejan.proj.restaurant.tablet.android.viewmodel.ViewModelState
import com.thejan.proj.restaurant.tv.smartrestaurant_mobile.R
import com.thejan.proj.restaurant.tv.smartrestaurant_mobile.viewmodel.RegisterViewModel
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : BaseActivity() {

    private lateinit var messageFromResponse: String
    private lateinit var mViewModel: RegisterViewModel

    var message = Observer<String> { newName ->
        messageFromResponse = newName!!
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        init()
    }
    private fun init() {
        removeStatusBar()
        initAction()
        initViewModel()
        initSubscription()
    }

    private fun initViewModel() {
        mViewModel = ViewModelProviders.of(this).get(RegisterViewModel::class.java)
    }

    private fun initAction() {
        btnLogin.setOnClickListener {
            if(etPhoneNumber.text.toString().isNotEmpty() && etName.toString().isNotEmpty() && etPassword.text.toString().isNotEmpty() && etConfirmPassword.text.toString().isNotEmpty()) {
                if(etPassword.text.toString() == etConfirmPassword.text.toString()) {
                    mViewModel.saveUser(
                        etPhoneNumber.text.toString(),
                        etName.text.toString(),
                        AppUtils.encrypt(etPassword.text.toString())!!
                    )
                }else{
                    showToast(getString(R.string.password_confrimation_should_be_similer_to_the_password))
                }
            }else{
                showToast(getString(R.string.please_fill_all_the_fields))
            }
        }

        tvBackToLogin.setOnClickListener {
            onBackPressed()
        }
    }

    private fun formValidation() {
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

            Status.PHONE_NUMBER_EXISTS -> {
                hideProgress()
                showToast(getString(R.string.user_is_exists))
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        AppUtils.closeActivityLeftToRight(this)
    }
}