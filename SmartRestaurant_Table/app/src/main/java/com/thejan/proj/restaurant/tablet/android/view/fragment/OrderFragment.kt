package com.thejan.proj.restaurant.tablet.android.view.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.thejan.proj.restaurant.tablet.android.R
import com.thejan.proj.restaurant.tablet.android.helper.AppUtils
import com.thejan.proj.restaurant.tablet.android.helper.OBJECT
import com.thejan.proj.restaurant.tablet.android.helper.RE_ORDER
import com.thejan.proj.restaurant.tablet.android.helper.RecyclerItemClickListenr
import com.thejan.proj.restaurant.tablet.android.view.activity.MainActivity
import com.thejan.proj.restaurant.tablet.android.view.activity.OrderDetailsActivity
import com.thejan.proj.restaurant.tablet.android.viewmodel.HomeViewModel
import com.thejan.proj.restaurant.tablet.android.viewmodel.OrderViewModel
import com.thejan.proj.restaurant.tablet.android.viewmodel.Status
import com.thejan.proj.restaurant.tablet.android.viewmodel.ViewModelState
import kotlinx.android.synthetic.main.fragment_order.*

class OrderFragment : Fragment() {

    private lateinit var mViewModel: OrderViewModel
    private lateinit var messageFromResponse: String

    var message = Observer<String> { value ->
        messageFromResponse = value!!
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_order, container, false)
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
    }

    private fun initData() {
        mViewModel.getOrders()
    }

    private fun initRecyclerView() {
        rvRecyclerView.setHasFixedSize(true)
        rvRecyclerView.layoutManager = LinearLayoutManager(activity)
        rvRecyclerView.adapter = mViewModel.adapter
        rvRecyclerView.addOnItemTouchListener(
            RecyclerItemClickListenr(
                activity!!,
                rvRecyclerView,
                object : RecyclerItemClickListenr.OnItemClickListener {
                    override fun onItemClick(view: View, position: Int) {
                        val intent = Intent(activity, OrderDetailsActivity::class.java)
                        intent.putExtra(OBJECT, mViewModel.adapter.getItem(position))
                        AppUtils.startActivityRightToLeftForResult(activity!!, intent, RE_ORDER)
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
                (activity as MainActivity).showProgress()
            }
            Status.SUB_SUCCESS1 -> {
                (activity as MainActivity).hideProgress()
            }
            Status.SUCCESS -> {
                (activity as MainActivity).hideProgress()
                tvNoItemsAvailable.visibility = View.GONE
            }
            Status.ERROR -> {
                (activity as MainActivity).hideProgress()
            }
            Status.TIMEOUT -> {
                (activity as MainActivity).hideProgress()
            }
            Status.LIST_EMPTY -> {
                (activity as MainActivity).hideProgress()
                tvNoItemsAvailable.visibility = View.VISIBLE
                tvStatus.visibility = View.GONE
            }
        }
    }

    companion object {
        fun newInstance() =
            OrderFragment().apply {
            }
    }
}