package com.thejan.proj.restaurant.admin.android.view.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.thejan.proj.restaurant.admin.android.R
import com.thejan.proj.restaurant.admin.android.helper.*
import com.thejan.proj.restaurant.admin.android.view.activity.InvoiceActivity
import com.thejan.proj.restaurant.admin.android.view.activity.MainActivity
import com.thejan.proj.restaurant.admin.android.view.adapter.*
import com.thejan.proj.restaurant.admin.android.viewmodel.HomeViewModel
import com.thejan.proj.restaurant.admin.android.viewmodel.Status
import com.thejan.proj.restaurant.admin.android.viewmodel.ViewModelState
import kotlinx.android.synthetic.main.fragment_home.*

class HomeFragment : Fragment(), OrdersAdapter.ClickSubItem, PendingOrderAdapter.ClickSubItem,
    ProcessingOrderAdapter.ClickSubItem, ServedOrderAdapter.ClickSubItem,
    SettleTheBillAdapter.ClickSubItem {

    private lateinit var mViewModel: HomeViewModel
    private lateinit var messageFromResponse: String
    private lateinit var amountFromResponse: String
    private lateinit var itemCountFromResponse: String

    var message = Observer<String> { value ->
        messageFromResponse = value!!
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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

    }

    private fun initData() {
        mViewModel.getCarts()
    }

    private fun initViewModel() {
        mViewModel = ViewModelProviders.of(this).get(HomeViewModel::class.java)
        mViewModel.placeOrderAdapter.setClick(this)
        mViewModel.pendingAdapter.setClick(this)
        mViewModel.processingAdapter.setClick(this)
        mViewModel.servedAdapter.setClick(this)
        mViewModel.settleAdapter.setClick(this)
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
                (activity as MainActivity).showProgress()
            }
            Status.SUB_SUCCESS1 -> {
                (activity as MainActivity).hideProgress()

            }
            Status.SUCCESS -> {
                (activity as MainActivity).hideProgress()
//                mViewModel.checkCartIsEmpty()
            }
            Status.ERROR -> {
                (activity as MainActivity).hideProgress()
            }
            Status.TIMEOUT -> {
                (activity as MainActivity).hideProgress()
            }
            Status.LIST_EMPTY -> {
                (activity as MainActivity).hideProgress()
            }
        }
    }

    private fun initRecyclerView() {
        rvOrderPlacing.setHasFixedSize(true)
        rvOrderPlacing.layoutManager = LinearLayoutManager(activity)
        rvOrderPlacing.adapter = mViewModel.placeOrderAdapter
        rvOrderPlacing.addOnItemTouchListener(
            RecyclerItemClickListenr(
                activity!!,
                rvOrderPlacing,
                object : RecyclerItemClickListenr.OnItemClickListener {
                    override fun onItemClick(view: View, position: Int) {
                    }

                    override fun onItemLongClick(view: View?, position: Int) {
                    }
                })
        )

        rvPending.setHasFixedSize(true)
        rvPending.layoutManager = LinearLayoutManager(activity)
        rvPending.adapter = mViewModel.pendingAdapter

        rvProcessing.setHasFixedSize(true)
        rvProcessing.layoutManager = LinearLayoutManager(activity)
        rvProcessing.adapter = mViewModel.processingAdapter

        rvServed.setHasFixedSize(true)
        rvServed.layoutManager = LinearLayoutManager(activity)
        rvServed.adapter = mViewModel.servedAdapter

        rvSettleTheBill.setHasFixedSize(true)
        rvSettleTheBill.layoutManager = LinearLayoutManager(activity)
        rvSettleTheBill.adapter = mViewModel.settleAdapter
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            HomeFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }

    override fun view(position: Int, status: String) {
        when (status) {
            STATUS_PENDING -> {
                val intent = Intent(activity, InvoiceActivity::class.java)
                intent.putExtra(OBJECT, mViewModel.pendingAdapter.getItem(position))
                intent.putExtra(STATUS, status)
                AppUtils.startActivityRightToLeft(activity!!, intent)
            }
            STATUS_PROCESSING -> {
                val intent = Intent(activity, InvoiceActivity::class.java)
                intent.putExtra(OBJECT, mViewModel.processingAdapter.getItem(position))
                intent.putExtra(STATUS, status)
                AppUtils.startActivityRightToLeft(activity!!, intent)
            }
            STATUS_SERVED -> {
                val intent = Intent(activity, InvoiceActivity::class.java)
                intent.putExtra(OBJECT, mViewModel.servedAdapter.getItem(position))
                intent.putExtra(STATUS, status)
                AppUtils.startActivityRightToLeft(activity!!, intent)
            }
            STATUS_BILL_SETTLEMENT_PENDING -> {
                val intent = Intent(activity, InvoiceActivity::class.java)
                intent.putExtra(OBJECT, mViewModel.settleAdapter.getItem(position))
                intent.putExtra(STATUS, status)
                AppUtils.startActivityRightToLeft(activity!!, intent)
            }
        }
    }

    override fun settle(position: Int, status: String) {
        when (status) {
            STATUS_BILL_SETTLEMENT_PENDING -> {
                AlertDialog.Builder(activity!!)
                    .setMessage(getString(R.string.do_you_want_to_settle_this_order))
                    .setIcon(R.drawable.ic_warning_black_transparent)
                    .setPositiveButton(
                        getString(R.string.yes)
                    ) { dialog, whichButton ->
                        mViewModel.updateStatus(mViewModel.settleAdapter.getItem(position))
                    }
                    .setNegativeButton(getString(R.string.no), null).show()
                    .show()
            }
        }
    }
}