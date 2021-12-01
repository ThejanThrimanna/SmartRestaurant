package com.thejan.proj.restaurant.tablet.android.view.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.thejan.proj.restaurant.tablet.android.R
import com.thejan.proj.restaurant.tablet.android.helper.AppUtils
import com.thejan.proj.restaurant.tablet.android.helper.CURRENCY
import com.thejan.proj.restaurant.tablet.android.helper.OBJECT
import com.thejan.proj.restaurant.tablet.android.helper.RecyclerItemClickListenr
import com.thejan.proj.restaurant.tablet.android.model.Cart
import com.thejan.proj.restaurant.tablet.android.model.Food
import com.thejan.proj.restaurant.tablet.android.viewmodel.OrderDetailsViewModel
import com.thejan.proj.restaurant.tablet.android.viewmodel.Status
import com.thejan.proj.restaurant.tablet.android.viewmodel.ViewModelState
import kotlinx.android.synthetic.main.activity_order_details.*
import kotlinx.android.synthetic.main.fragment_order.rvRecyclerView


class OrderDetailsActivity : BaseActivity() {
    private lateinit var mViewModel: OrderDetailsViewModel
    private lateinit var messageFromResponse: String
    private lateinit var cart: Cart

    var message = Observer<String> { value ->
        messageFromResponse = value!!
    }

    var amount = Observer<Double> { value ->
        tvTotal.text = " $CURRENCY " + String.format(
            "%.2f", value
        )
        tvDiscount.text = " $CURRENCY " + String.format(
            "%.2f", cart.discount
        )
        tvNetAmount.text = " $CURRENCY " + String.format(
            "%.2f", (value - cart.discount!!)
        )
    }

    var cartItems = Observer<Food> { value ->

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_details)
        init()
    }

    private fun init() {
        initViewModel()
        initSubscription()
        initRecyclerView()
        initData()
        initAction()
    }

    private fun initAction() {
        btnReorder.setOnClickListener {
            AlertDialog.Builder(this)
                .setMessage(getString(R.string.do_you_want_to_add_these_items_to_the_cart))
                .setIcon(R.drawable.ic_warning_black_transparent)
                .setPositiveButton(
                    getString(R.string.yes)
                ) { dialog, whichButton ->
                    mViewModel.reOrder()
                }
                .setNegativeButton(getString(R.string.no), null).show()
                .show()
        }
    }

    private fun initData() {
        cart = intent.getParcelableExtra(OBJECT)
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
        mViewModel = ViewModelProviders.of(this).get(OrderDetailsViewModel::class.java)
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
            Status.SUB_SUCCESS3 -> {
                btnReorder.visibility = View.GONE
                println("Hide")
            }
            Status.SUB_SUCCESS4->{
                btnReorder.visibility = View.VISIBLE
                println("VISIBLE")
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