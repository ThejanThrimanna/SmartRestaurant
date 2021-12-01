package com.thejan.proj.restaurant.tablet.android.view.activity

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.MotionEvent
import android.view.View
import androidx.annotation.RequiresApi
import androidx.core.view.MotionEventCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.thejan.proj.restaurant.tablet.android.R
import com.thejan.proj.restaurant.tablet.android.helper.AppUtils
import com.thejan.proj.restaurant.tablet.android.helper.SharedPref
import com.thejan.proj.restaurant.tablet.android.model.TableReservation
import com.thejan.proj.restaurant.tablet.android.viewmodel.LoginViewModel
import com.thejan.proj.restaurant.tablet.android.viewmodel.Status
import com.thejan.proj.restaurant.tablet.android.viewmodel.ViewModelState
import kotlinx.android.synthetic.main.activity_login.*
import java.util.*


class LoginActivity : BaseActivity() {

    private lateinit var messageFromResponse: String
    private var responseReservedName: String? = null
    private var responseReservation: TableReservation? = null
    private lateinit var tableNum: String
    private lateinit var mViewModel: LoginViewModel
    private var isReserved = false

    var handler = Handler()
    var runnable: Runnable? = null
    var delay = 5000L


    private var mCounter = 0
    private val mHandler: Handler = Handler()

    var message = Observer<String> { newName ->
        messageFromResponse = newName!!
    }
    var reservation = Observer<TableReservation> { newName ->
        responseReservedName = newName.name!!
        responseReservation = newName
    }

    var tableNumber = Observer<String> { newName ->
        tableNum = newName!!
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        init()
    }

    override fun onResume() {
        super.onResume()
        initData()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun init() {
        removeStatusBar()
        initAction()
        initViewModel()
        initSubscription()
        initView()
    }

    /**
     * Initiated the view for the LoginActivity
     */
    private fun initView() {
        handler = Handler(Looper.getMainLooper())
    }

    /**
     * Initiate Data for the activity
     */
    private fun initData() {
        val tableNumber = intent.getStringExtra("table")
        val address = AppUtils.getUniqueId(this)
        mViewModel.getTableNumber(address)
    }

    private fun initViewModel() {
        mViewModel = ViewModelProviders.of(this).get(LoginViewModel::class.java)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun initSubscription() {
        mViewModel.message.observe(this, message)
        mViewModel.reservation.observe(this, reservation)
        mViewModel.tableNumber.observe(this, tableNumber)
        mViewModel.state!!.observe(this, Observer<ViewModelState> {
            it?.let {
                update(it)
            }
        })
    }

    @RequiresApi(Build.VERSION_CODES.M)
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

             Status.ERROR_DEFAULT -> {
                hideProgress()
                showToast(getString(R.string.something_went_wrong))
            }

            Status.SUB_SUCCESS1 -> {
                startActivity(Intent(this, TableRegisterActivity::class.java))
                finishAffinity()
            }

            Status.SUB_SUCCESS2 -> {
                hideProgress()
                isReserved = false
                tvStatus.text = getString(R.string.available)
                tvStatus.setTextColor(getColor(R.color.green))
                if (handler != null && updateTask != null) {
                    handler.removeCallbacks(updateTask!!)
                }
                if (isReserved) tvRegister.visibility = View.GONE
                else tvRegister.visibility = View.VISIBLE
            }

            Status.SUB_SUCCESS3 -> {
                hideProgress()
                isReserved = true
                if (isReserved) tvRegister.visibility = View.GONE
                else tvRegister.visibility = View.VISIBLE
                tvStatus.text = getString(R.string.reserved) + " " + responseReservedName
                tvStatus.setTextColor(getColor(R.color.red))
                handler.post(updateTask)
            }

            Status.SUB_SUCCESS4 -> {
                tvTableNumber.text =
                    getString(R.string.login_here) + " to Table $tableNum" + "\n " + SharedPref.getInteger(
                        SharedPref.NUMBER_OF_SEATS,
                        4
                    )+" Seats"
                mViewModel.getTheTable(tableNum)
            }
            Status.SUB_SUCCESS5 -> {
                hideProgress()
            }
            Status.VALIDATION_ERROR -> {
                hideProgress()
                showToast(getString(R.string.you_have_reserved_another_table))
            }
            Status.TABLE_NOT_AVAILABLE -> {
                startActivity(Intent(this, TableRegisterActivity::class.java))
                finishAffinity()
            }
        }
    }

    private fun initAction() {
        tvRegister.setOnClickListener {
            var intent = Intent(this, RegisterActivity::class.java)
            AppUtils.startActivityRightToLeft(this, intent)
        }
        btnLogin.setOnClickListener {
            if (etPhoneNumber.text.toString().isNotEmpty() && etPassword.text.toString()
                    .isNotEmpty()
            )
                if (isReserved) {
                    if (responseReservation!!.phone == etPhoneNumber.text.toString()) {
                        mViewModel.login(etPhoneNumber.text.toString(), etPassword.text.toString())
                    } else {
                        showToast(getString(R.string.please_enter_the_correct_phone_number))
                    }
                } else
                    mViewModel.login(etPhoneNumber.text.toString(), etPassword.text.toString())
            else
                showToast(getString(R.string.please_fill_all_the_fields))
        }
    }

    private val mResetCounter = Runnable { mCounter = 0 }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return when (MotionEventCompat.getActionMasked(event)) {
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                if (mCounter === 0) mHandler.postDelayed(mResetCounter, 3000)
                mCounter++
                if (mCounter === 5) {
                    mHandler.removeCallbacks(mResetCounter)
                    mCounter = 0
                    val address = AppUtils.getUniqueId(this)
                    mViewModel.unRegisterTable(address)
                }
                false
            }
            else -> super.onTouchEvent(event)
        }
    }

    override fun onPause() {
        super.onPause()
        if (handler != null && updateTask != null) {
            handler.removeCallbacks(updateTask!!)
        }
    }

    private val updateTask = object : Runnable {
        override fun run() {
            if (isReserved && responseReservation != null && !responseReservation!!.isActive!!) {
                var time = Calendar.getInstance().timeInMillis
                if ((responseReservation!!.date!! + 1800000) < time) {//1800000
                    mViewModel.removeReservation(responseReservation!!.phone!!)
                }
                handler.postDelayed(this, delay)
            }
        }

    }
}
