package com.thejan.proj.restaurant.tablet.android.view.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.thejan.proj.restaurant.tablet.android.R
import com.thejan.proj.restaurant.tablet.android.helper.*
import com.thejan.proj.restaurant.tablet.android.model.Cart
import com.thejan.proj.restaurant.tablet.android.viewmodel.SettleViewModel
import com.thejan.proj.restaurant.tablet.android.viewmodel.Status
import com.thejan.proj.restaurant.tablet.android.viewmodel.ViewModelState
import kotlinx.android.synthetic.main.activity_settle.*
import kotlinx.android.synthetic.main.activity_settle.tvPaybleAmount
import kotlinx.android.synthetic.main.activity_settle.tvStatus
import kotlinx.android.synthetic.main.activity_settle.tvSubTotal

class SettleActivity : BaseActivity() {
    private lateinit var mViewModel: SettleViewModel
    private lateinit var messageFromResponse: String
    private var amountFromResponse: Double = 0.0
    private var discount: Double = 0.0
    private var currentStatus: String = ""
    private lateinit var itemCountFromResponse: String
    private lateinit var cartFromRespone: Cart

    var message = Observer<String> { value ->
        messageFromResponse = value!!
    }

    var discountL = Observer<Double> { value ->
        discount = value
    }

    var cartL = Observer<Cart> { value ->
        cartFromRespone = value
    }

    var amount = Observer<Double> { value ->
        amountFromResponse = value!!
        tvSubTotal.text = CURRENCY + " " + String.format("%.2f", value.toDouble())
        tvDiscount.text = CURRENCY + " " + String.format("%.2f", discount.toDouble())
        tvPaybleAmount.text =
            CURRENCY + " " + String.format("%.2f", (amountFromResponse - discount).toDouble())
    }

    var status = Observer<String> { value ->
        currentStatus = value
        statusChange(value)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settle)
        init()
    }

    private fun init() {
        initViewModel()
        initSubscription()
        initData()
        initAction()
    }

    private fun initAction() {
        btnInvoice.setOnClickListener {
            val intent = Intent(this, InvoiceActivity::class.java)
            intent.putExtra(OBJECT, cartFromRespone)
            intent.putExtra(STATUS, currentStatus)
            AppUtils.startActivityRightToLeft(this, intent)
        }
    }

    private fun initData() {
        mViewModel.getCurrentCart()
    }

    private fun initViewModel() {
        mViewModel = ViewModelProviders.of(this).get(SettleViewModel::class.java)
    }

    private fun update(state: ViewModelState) {
        when (state.status) {
            Status.LOADING -> {
                showProgress()
            }
            Status.SUB_SUCCESS1 -> {
                gotoLogin()
            }
            Status.SUCCESS -> {
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

    private fun initSubscription() {
        mViewModel.message.observe(this, message)
        mViewModel.amount.observe(this, amount)
        mViewModel.status.observe(this, status)
        mViewModel.dicountL.observe(this, discountL)
        mViewModel.cartR.observe(this, cartL)
        mViewModel.state!!.observe(this, Observer<ViewModelState> {
            it?.let {
                update(it)
            }
        })
    }

    private fun statusChange(status: String) {
        tvStatus.text = status
        when (status) {
            STATUS_SETTLED -> {
                tvThankYou.visibility = View.VISIBLE
                tvStatus.setTextColor(getColor(R.color.green))
                Handler().postDelayed({
                    mViewModel.closeOrder()
                }, 7000)
            }
        }
    }

    override fun onBackPressed() {
    }

}


