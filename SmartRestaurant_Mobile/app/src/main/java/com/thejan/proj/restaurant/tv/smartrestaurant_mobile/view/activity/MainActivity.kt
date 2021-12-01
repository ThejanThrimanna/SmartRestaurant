package com.thejan.proj.restaurant.tv.smartrestaurant_mobile.view.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.thejan.proj.restaurant.tablet.android.helper.AppUtils
import com.thejan.proj.restaurant.tv.smartrestaurant_mobile.helper.FORMAT_1
import com.thejan.proj.restaurant.tablet.android.viewmodel.Status
import com.thejan.proj.restaurant.tablet.android.viewmodel.ViewModelState
import com.thejan.proj.restaurant.tv.smartrestaurant_mobile.R
import com.thejan.proj.restaurant.tv.smartrestaurant_mobile.helper.SharedPref
import com.thejan.proj.restaurant.tv.smartrestaurant_mobile.model.TableReservation
import com.thejan.proj.restaurant.tv.smartrestaurant_mobile.viewmodel.MainViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.toolbar
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : BaseActivity() {

    private lateinit var mViewModel: MainViewModel
    private lateinit var messageFromResponse: String
    var isReserved: Boolean = false
    private var responseReservation: TableReservation? = null
    var handler = Handler()
    var runnable: Runnable? = null
    var delay = 5000L


    var message = Observer<String> { value ->
        messageFromResponse = value!!
    }

    var reservation = Observer<TableReservation> { value ->
        responseReservation = value
        var formatter = SimpleDateFormat(FORMAT_1, Locale.UK)
        val dateString = formatter.format(Date(value.date!! + 1800000))
        tvExpiryTime.text = dateString

        tvNumberOfSeats.text = value.numberOfSeats.toString()
        tvTableNumber.text = value.tableNumber

        if (value.isActive!!) {
            tvReservationNotification.text = getString(R.string.you_have_reached_the_restaurant)
            tvReservationNotification.setTextColor(resources.getColor(R.color.green))
            tvExpiryTime.visibility = View.GONE
        } else {
            tvReservationNotification.text =
                getString(R.string.please_reach_the_restaurant_before_the_expiry_time)
            tvReservationNotification.setTextColor(resources.getColor(R.color.red))
            tvExpiryTime.visibility = View.VISIBLE
        }

        var time = Calendar.getInstance().timeInMillis
        if ((value.date!! + 1800000) < time) {//1800000
            mViewModel.removeReservation(SharedPref.getString(SharedPref.PHONE, "")!!, responseReservation!!.tableNumber!!)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        init()
    }

    private fun init() {
        initView()
        initAction()
        initViewModel()
        initSubscription()
        initAction()
        iniData()
    }

    private fun iniData() {
        mViewModel.getTableReservation()
        tvName.text = SharedPref.getString(SharedPref.NAME, "")
    }

    private fun initView() {
        toolbar.title = getString(R.string.home)
        setSupportActionBar(toolbar)
        handler = Handler(Looper.getMainLooper())
    }

    private fun initViewModel() {
        mViewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)
    }

    private fun initSubscription() {
        mViewModel.message.observe(this, message)
        mViewModel.reservation.observe(this, reservation)
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
            Status.SUB_SUCCESS2 -> {
                hideProgress()
                llWrapper.visibility = View.GONE
                tvNoReservation.visibility = View.VISIBLE
                isReserved = false

                if (handler != null && updateTask != null) {
                    handler.removeCallbacks(updateTask!!)
                }
                tvReservation.text = getString(R.string.reserve_a_table)

            }
            Status.SUCCESS -> {
                llWrapper.visibility = View.VISIBLE
                tvNoReservation.visibility = View.GONE
                isReserved = true
                hideProgress()
                handler.post(updateTask)
                tvReservation.text = getString(R.string.cancel_the_reservation)
            }
            Status.SUB_SUCCESS3 -> {
                hideProgress()
            }
            Status.ERROR -> {
                hideProgress()
            }
            Status.TIMEOUT -> {
                hideProgress()
            }
            Status.LIST_EMPTY -> {
                hideProgress()
            }
        }
    }

    private fun initAction() {
        llPastOrders.setOnClickListener {
            val intent = Intent(this, PastOrdersActivity::class.java)
            AppUtils.startActivityRightToLeft(this, intent)
        }

        llReserveTable.setOnClickListener {
            if (!isReserved) {
                val intent = Intent(this, ReserveTableActivity::class.java)
                AppUtils.startActivityRightToLeft(this, intent)
            } else {
                AlertDialog.Builder(this)
                    .setMessage(getString(R.string.do_you_realy_want_tologout))
                    .setIcon(R.drawable.ic_warning_black_transparent)
                    .setPositiveButton(
                        getString(R.string.yes)
                    ) { dialog, whichButton ->
                        mViewModel.removeReservation(responseReservation!!.phone!!,responseReservation!!.tableNumber!!)
                    }
                    .setNegativeButton(getString(R.string.no), null).show()
                    .show()
            }
        }

        ivQuite.setOnClickListener {
            AlertDialog.Builder(this)
                .setMessage(getString(R.string.do_you_realy_want_tologout))
                .setIcon(R.drawable.ic_warning_black_transparent)
                .setPositiveButton(
                    getString(R.string.yes)
                ) { dialog, whichButton ->
                    gotoLogin()
                }
                .setNegativeButton(getString(R.string.no), null).show()
                .show()
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
                    mViewModel.removeReservation(responseReservation!!.phone!!,responseReservation!!.tableNumber!!)
                }
                handler.postDelayed(this, delay)
            }
        }

    }
}