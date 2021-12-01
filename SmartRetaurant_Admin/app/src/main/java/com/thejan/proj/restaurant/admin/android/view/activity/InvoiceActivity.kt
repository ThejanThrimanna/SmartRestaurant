package com.thejan.proj.restaurant.admin.android.view.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.thejan.proj.restaurant.admin.android.R
import com.thejan.proj.restaurant.admin.android.helper.*
import com.thejan.proj.restaurant.admin.android.model.Cart
import com.thejan.proj.restaurant.admin.android.viewmodel.InvoiceViewModel
import com.thejan.proj.restaurant.admin.android.viewmodel.Status
import com.thejan.proj.restaurant.admin.android.viewmodel.ViewModelState
import kotlinx.android.synthetic.main.activity_invoice.*
import java.text.SimpleDateFormat
import java.util.*

class InvoiceActivity : BaseActivity() {

    private lateinit var mViewModel: InvoiceViewModel
    private lateinit var messageFromResponse: String
    private lateinit var cart: Cart
    private lateinit var status: String

    var message = Observer<String> { value ->
        messageFromResponse = value!!
    }

    var amount = Observer<Double> { value ->
        tvTotal.text = " $CURRENCY " + String.format(
            "%.2f", value
        )
        tvDscount.text = " $CURRENCY " + String.format(
            "%.2f", cart.discount
        )

        tvNetAmount.text = " $CURRENCY " + String.format(
            "%.2f", (value-cart!!.discount!!)
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_invoice)

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
        tvOrderID.text = cart.cartID

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
        mViewModel = ViewModelProviders.of(this).get(InvoiceViewModel::class.java)
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