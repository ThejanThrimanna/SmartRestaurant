package com.thejan.proj.restaurant.admin.android.view.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.RadioButton
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.thejan.proj.restaurant.admin.android.R
import com.thejan.proj.restaurant.admin.android.helper.AppUtils
import com.thejan.proj.restaurant.admin.android.helper.RecyclerItemClickListenr
import com.thejan.proj.restaurant.admin.android.viewmodel.PerformanceReportViewModel
import com.thejan.proj.restaurant.admin.android.viewmodel.SalesReportViewModel
import com.thejan.proj.restaurant.admin.android.viewmodel.Status
import com.thejan.proj.restaurant.admin.android.viewmodel.ViewModelState
import kotlinx.android.synthetic.main.activity_sales_report.*

class PerformanceReportActivity : BaseActivity() {

    private lateinit var mViewModel: PerformanceReportViewModel
    private lateinit var messageFromResponse: String

    var message = Observer<String> { value ->
        messageFromResponse = value!!
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_performance_report)
        init()
    }

    private fun init() {
        initViewModel()
        initSubscription()
        initRecyclerView()
        initAction()
        initData()
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
        mViewModel = ViewModelProviders.of(this).get(PerformanceReportViewModel::class.java)
    }

    private fun initSubscription() {
        mViewModel.message.observe(this, message)
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