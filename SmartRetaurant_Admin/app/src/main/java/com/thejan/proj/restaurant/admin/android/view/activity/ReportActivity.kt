package com.thejan.proj.restaurant.admin.android.view.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.thejan.proj.restaurant.admin.android.R
import com.thejan.proj.restaurant.admin.android.helper.AppUtils
import com.thejan.proj.restaurant.admin.android.helper.CURRENCY
import com.thejan.proj.restaurant.admin.android.model.Category
import com.thejan.proj.restaurant.admin.android.viewmodel.ReportViewModel
import com.thejan.proj.restaurant.admin.android.viewmodel.Status
import com.thejan.proj.restaurant.admin.android.viewmodel.TableViewModel
import com.thejan.proj.restaurant.admin.android.viewmodel.ViewModelState
import kotlinx.android.synthetic.main.fragment_report.*
import java.util.ArrayList

class ReportActivity : BaseActivity() {

    private lateinit var mViewModel: ReportViewModel
    private lateinit var messageFromResponse: String
    private lateinit var categoriesFromResponse: ArrayList<Category>

    var message = Observer<String> { value ->
        messageFromResponse = value!!
    }
    private var todaySalesAmount = Observer<String> { value ->
        tvTodaySales.text = getString(R.string.today) + "\n" + CURRENCY + " " + value
    }
    private var weekSalesAmount = Observer<String> { value ->
        tvWeekSales.text = getString(R.string.this_week) + "\n" + CURRENCY + " " + value
    }
    private var monthSalesAmount = Observer<String> { value ->
        tvMonthSales.text = getString(R.string.this_month) + "\n" + CURRENCY + " " + value
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_report)
        init()
    }

    private fun init() {
        initViewModel()
        initSubscription()
        initAction()
        initData()
    }

    private fun initViewModel() {
        mViewModel = ViewModelProviders.of(this).get(ReportViewModel::class.java)
    }

    private fun initSubscription() {
        mViewModel.message.observe(this, message)
        mViewModel.todaySalesAmount.observe(this, todaySalesAmount)
        mViewModel.weekSalesAmount.observe(this, weekSalesAmount)
        mViewModel.monthSalesAmount.observe(this, monthSalesAmount)
        mViewModel.state!!.observe(this, Observer<ViewModelState> {
            it?.let {
                update(it)
            }
        })
    }

    fun initData() {
        mViewModel.getCartDetails()
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

    private fun initAction() {
        sales.setOnClickListener {
            val intent = Intent(this, SalesReportActivity::class.java)
            AppUtils.startActivityRightToLeft(this, intent)
        }

        performance.setOnClickListener {
            val intent = Intent(this, PerformanceReportActivity::class.java)
            AppUtils.startActivityRightToLeft(this, intent)
        }

        category.setOnClickListener {
            val intent = Intent(this, CategoryReportActivity::class.java)
            AppUtils.startActivityRightToLeft(this, intent)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        AppUtils.closeActivityLeftToRight(this)
    }
}