package com.thejan.proj.restaurant.admin.android.view.activity

import android.os.Bundle
import android.view.View
import android.widget.RadioButton
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.thejan.proj.restaurant.admin.android.R
import com.thejan.proj.restaurant.admin.android.helper.AppUtils
import com.thejan.proj.restaurant.admin.android.helper.CURRENCY
import com.thejan.proj.restaurant.admin.android.helper.RecyclerItemClickListenr
import com.thejan.proj.restaurant.admin.android.viewmodel.SalesReportViewModel
import com.thejan.proj.restaurant.admin.android.viewmodel.Status
import com.thejan.proj.restaurant.admin.android.viewmodel.ViewModelState
import kotlinx.android.synthetic.main.activity_sales_report.*

class SalesReportActivity : BaseActivity() {

    private lateinit var mViewModel: SalesReportViewModel
    private lateinit var messageFromResponse: String

    var message = Observer<String> { value ->
        messageFromResponse = value!!
    }

    private var cost = Observer<String> { value ->
        tvCost.text = "Cost $CURRENCY $value"
    }

    private var revenu = Observer<String> { value ->
        tvRevenue.text = "Revenue $CURRENCY $value"
    }

    private var profit = Observer<String> { value ->
        tvProfit.text = "Profit $CURRENCY $value"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sales_report)
        init()
    }

    private fun init() {
        initViewModel()
        initSubscription()
        initRecyclerView()
        initAction()
        initData()
        initView()
    }

    private fun initView() {
        tvCost.text = "Cost"
        tvProfit.text = "Profit"
        tvRevenue.text = "Revenue"
    }

    private fun initAction() {
        radioGroup.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.rbToday->{
                    mViewModel.getCartDetailsToday()
                }
                R.id.rbThisWeek->{
                    mViewModel.getCartDetailsThisWeek()
                }
                R.id.rbThisMonth->{
                    mViewModel.getCartDetailsThisMonth()
                }
                R.id.rbThisYear->{
                    mViewModel.getCartDetailsThisYear()
                }
            }
        }
    }

    private fun initData() {
        (radioGroup.getChildAt(0) as RadioButton).isChecked = true
    }

    private fun initViewModel() {
        mViewModel = ViewModelProviders.of(this).get(SalesReportViewModel::class.java)
    }

    private fun initSubscription() {
        mViewModel.message.observe(this, message)
        mViewModel.cost.observe(this, cost)
        mViewModel.revenue.observe(this, revenu)
        mViewModel.profit.observe(this, profit)
        mViewModel.state!!.observe(this, Observer<ViewModelState> {
            it?.let {
                update(it)
            }
        })
    }

    private fun initRecyclerView() {
        rvRecyclerView.setHasFixedSize(true)
        val layoutManager =
            LinearLayoutManager(this)
        rvRecyclerView.layoutManager = layoutManager
        rvRecyclerView.adapter = mViewModel.itemAdapter
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