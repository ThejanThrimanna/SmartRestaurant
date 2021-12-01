package com.thejan.proj.restaurant.tv.smartrestaurant_mobile.view.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.thejan.proj.restaurant.tablet.android.helper.AppUtils
import com.thejan.proj.restaurant.tv.smartrestaurant_mobile.helper.OBJECT
import com.thejan.proj.restaurant.tablet.android.helper.RecyclerItemClickListenr
import com.thejan.proj.restaurant.tablet.android.viewmodel.Status
import com.thejan.proj.restaurant.tablet.android.viewmodel.ViewModelState
import com.thejan.proj.restaurant.tv.smartrestaurant_mobile.R
import com.thejan.proj.restaurant.tv.smartrestaurant_mobile.viewmodel.OrderViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_past_orders.*
import kotlinx.android.synthetic.main.activity_past_orders.toolbar

class PastOrdersActivity : BaseActivity() {
    private lateinit var mViewModel: OrderViewModel
    private lateinit var messageFromResponse: String

    var message = Observer<String> { value ->
        messageFromResponse = value!!
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_past_orders)
        init()
    }

    private fun init() {
        initViewModel()
        initSubscription()
        initRecyclerView()
        initData()
        initView()
    }

    private fun initView() {
        toolbar.title = getString(R.string.past_orders)
    }

    private fun initData() {
        mViewModel.getOrders()
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
                          val intent = Intent(this@PastOrdersActivity, InvoiceDetailsActivity::class.java)
                          intent.putExtra(OBJECT, mViewModel.adapter.getItem(position))
                          AppUtils.startActivityRightToLeft(this@PastOrdersActivity, intent)
                    }

                    override fun onItemLongClick(view: View?, position: Int) {
                    }
                })
        )
    }

    private fun initViewModel() {
        mViewModel = ViewModelProviders.of(this).get(OrderViewModel::class.java)
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
            Status.SUB_SUCCESS1 -> {
                hideProgress()
            }
            Status.SUCCESS -> {
                hideProgress()
                tvNoItemsAvailable.visibility = View.GONE
            }
            Status.ERROR -> {
                hideProgress()
            }
            Status.TIMEOUT -> {
                hideProgress()
            }
            Status.LIST_EMPTY -> {
                hideProgress()
                tvNoItemsAvailable.visibility = View.VISIBLE
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        AppUtils.closeActivityLeftToRight(this)
    }
}