package com.thejan.proj.restaurant.tv.smartrestaurant_mobile.view.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.thejan.proj.restaurant.tablet.android.helper.*
import com.thejan.proj.restaurant.tablet.android.viewmodel.Status
import com.thejan.proj.restaurant.tablet.android.viewmodel.ViewModelState
import com.thejan.proj.restaurant.tv.smartrestaurant_mobile.R
import com.thejan.proj.restaurant.tv.smartrestaurant_mobile.helper.CURRENCY
import com.thejan.proj.restaurant.tv.smartrestaurant_mobile.helper.FORMAT_1
import com.thejan.proj.restaurant.tv.smartrestaurant_mobile.helper.OBJECT
import com.thejan.proj.restaurant.tv.smartrestaurant_mobile.model.Cart
import com.thejan.proj.restaurant.tv.smartrestaurant_mobile.viewmodel.InvoiceDetailsViewModel
import kotlinx.android.synthetic.main.activity_invoice_details.*
import java.text.SimpleDateFormat
import java.util.*

class InvoiceDetailsActivity : BaseActivity() {

    private lateinit var mViewModel: InvoiceDetailsViewModel
    private lateinit var messageFromResponse: String
    private lateinit var cart: Cart

    var message = Observer<String> { value ->
        messageFromResponse = value!!
    }

    var amount = Observer<Double> { value ->
        tvDiscount.text = " $CURRENCY " + String.format(
            "%.2f", cart.discount
        )
        tvTotal.text = " $CURRENCY " + String.format(
            "%.2f", value
        )

        tvNetamount.text = " $CURRENCY " + String.format(
            "%.2f",  (cart.amout!! - cart.discount!!)
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_invoice_details)
        init()
    }

    private fun init() {
        initViewModel()
        initSubscription()
        initRecyclerView()
        initData()
    }

    private fun initData() {
        cart = intent.getParcelableExtra(OBJECT)!!
        var formatter = SimpleDateFormat(FORMAT_1, Locale.UK)
        val dateString = formatter.format(cart.date)
        tvDateTime.text = dateString
        tvOrderID.text = cart.cartId

        mViewModel.getOrder(cart)
    }

    private fun initRecyclerView() {
        rvRecyclerView.setHasFixedSize(true)
        rvRecyclerView.layoutManager = LinearLayoutManager(this)
        rvRecyclerView.adapter = mViewModel.adapter
        rvRecyclerView.addOnItemTouchListener(
            RecyclerItemClickListenr(
                this,
                rvRecyclerView,
                object : RecyclerItemClickListenr.OnItemClickListener {
                    override fun onItemClick(view: View, position: Int) {
                    }

                    override fun onItemLongClick(view: View?, position: Int) {
                    }
                })
        )
    }

    private fun initViewModel() {
        mViewModel = ViewModelProviders.of(this).get(InvoiceDetailsViewModel::class.java)
    }

    private fun initSubscription() {
        mViewModel.message.observe(this, message)
        mViewModel.amount.observe(this, amount)
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
            Status.SUB_SUCCESS1 -> {
                hideProgress()
            }
            Status.SUB_SUCCESS2 -> {
                hideProgress()
                val returnIntent = Intent()
                setResult(RESULT_OK, returnIntent)
                finish()

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

    override fun onBackPressed() {
        super.onBackPressed()
        AppUtils.closeActivityLeftToRight(this)
    }
}