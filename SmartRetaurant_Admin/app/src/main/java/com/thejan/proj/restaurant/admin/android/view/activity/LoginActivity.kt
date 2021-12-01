package com.thejan.proj.restaurant.admin.android.view.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.thejan.proj.restaurant.admin.android.R
import com.thejan.proj.restaurant.admin.android.helper.AppUtils
import com.thejan.proj.restaurant.admin.android.viewmodel.LoginViewModel
import com.thejan.proj.restaurant.admin.android.viewmodel.Status
import com.thejan.proj.restaurant.admin.android.viewmodel.ViewModelState
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : BaseActivity() {
    private lateinit var messageFromResponse: String
    private lateinit var tableNum: String
    private lateinit var mViewModel: LoginViewModel
    private var isReserved = false

    private var mCounter = 0
    private val mHandler: Handler = Handler()

    var message = Observer<String> { newName ->
        messageFromResponse = newName!!
    }

    var tableNumber = Observer<String> { newName ->
        tableNum = newName!!
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        init()
    }

    override fun onResume() {
        super.onResume()
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
        mViewModel.tableNumber.observe(this, tableNumber)
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
        btnLogin.setOnClickListener {
            mViewModel.login(etEmpId.text.toString(), AppUtils.encrypt(etPassword.text.toString())!!)
        }
    }

    private val mResetCounter = Runnable { mCounter = 0 }

}