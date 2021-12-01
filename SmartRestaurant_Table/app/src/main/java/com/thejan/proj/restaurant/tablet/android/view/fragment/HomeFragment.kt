package com.thejan.proj.restaurant.tablet.android.view.fragment

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.thejan.proj.restaurant.tablet.android.R
import com.thejan.proj.restaurant.tablet.android.helper.*
import com.thejan.proj.restaurant.tablet.android.view.activity.CartActivity
import com.thejan.proj.restaurant.tablet.android.view.activity.MainActivity
import com.thejan.proj.restaurant.tablet.android.view.activity.SettleActivity
import com.thejan.proj.restaurant.tablet.android.view.adapter.FoodAdapter
import com.thejan.proj.restaurant.tablet.android.viewmodel.HomeViewModel
import com.thejan.proj.restaurant.tablet.android.viewmodel.Status
import com.thejan.proj.restaurant.tablet.android.viewmodel.ViewModelState
import kotlinx.android.synthetic.main.fragment_home.*

class HomeFragment : Fragment(), FoodAdapter.ClickSubItem {

    private lateinit var mViewModel: HomeViewModel
    private lateinit var messageFromResponse: String
    private lateinit var amountFromResponse: String
    private lateinit var itemCountFromResponse: String

    var message = Observer<String> { value ->
        messageFromResponse = value!!
    }

    var amount = Observer<String> { value ->
        amountFromResponse = value!!
        tvTotal.text = CURRENCY + " " + String.format("%.2f", value.toDouble())
    }

    var status = Observer<String> { value ->
        mViewModel.itemAdapter.setCurrentStatus(value)
        statusChange(value)

        if(value == STATUS_BILL_SETTLEMENT_PENDING || value == STATUS_SETTLED){
            val intent = Intent(activity!!, SettleActivity::class.java)
            AppUtils.startActivityRightToLeft(activity!!, intent)
        }
    }

    private fun statusChange(status: String) {
        tvStatus.text = status
        when (status) {

        }
    }

    var count = Observer<String> { value ->
        itemCountFromResponse = value!!
        val intVal = value.toInt()
        if (intVal == 1) {
            tvTotalCount.text = value + " " + getString(R.string.item_added)
        } else {
            tvTotalCount.text = value + " " + getString(R.string.items_added)
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
        initAction()
    }

    override fun onResume() {
        super.onResume()
        initData()
    }

    private fun initAction() {
        llSelectClothNext.setOnClickListener {
            var intent = Intent(activity, CartActivity::class.java)
            AppUtils.startActivityRightToLeft(activity!!, intent)
        }
    }

    private fun initData() {
        mViewModel.getCategories()
        mViewModel.itemAdapter.setClick(this)
    }

    private fun initViewModel() {
        mViewModel = ViewModelProviders.of(this).get(HomeViewModel::class.java)
//        mViewModel.itemAdapter.setClick(this)
    }

    private fun initSubscription() {
        mViewModel.message.observe(this, message)
        mViewModel.amount.observe(this, amount)
        mViewModel.count.observe(this, count)
        mViewModel.status.observe(this, status)
        mViewModel.state!!.observe(this, Observer<ViewModelState> {
            it?.let {
                update(it)
            }
        })
    }

    private fun initRecyclerView() {
        rvCategory.setHasFixedSize(true)
        val layoutManager =
            LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        rvCategory.layoutManager = layoutManager
        rvCategory.adapter = mViewModel.catAdapter
        rvCategory.addOnItemTouchListener(
            RecyclerItemClickListenr(
                activity!!,
                rvCategory,
                object : RecyclerItemClickListenr.OnItemClickListener {
                    override fun onItemClick(view: View, position: Int) {
                        mViewModel.currentSelectedCategoryIndex = position
                        mViewModel.catAdapter.setCurrentSelectedItem(position)
                        mViewModel.catAdapter.notifyDataSetChanged()
                        mViewModel.currentSelectedCategory = mViewModel.allCats[position].catID!!
                        mViewModel.displayItemData(mViewModel.allProducts)
                    }

                    override fun onItemLongClick(view: View?, position: Int) {
                    }
                })
        )

        rvItems.setHasFixedSize(true)
        rvItems.layoutManager = GridLayoutManager(activity, 7)
        rvItems.adapter = mViewModel.itemAdapter
        rvCategory.addOnItemTouchListener(
            RecyclerItemClickListenr(
                activity!!,
                rvCategory,
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
                (activity as MainActivity).showProgress()
            }
            Status.SUB_SUCCESS1 -> {
                (activity as MainActivity).hideProgress()
//                mViewModel.getItems(0)
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
            Status.CART_EMPTY -> {
                AppUtils.slideDown(llSelectClothNext)
            }
            Status.CART_NOT_EMPTY -> {
                AppUtils.slideUp(llSelectClothNext)
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            HomeFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }


    override fun clickAddToCart(position: Int) {
        mViewModel.itemAdapter.getItem(position).isAdded =
            !mViewModel.itemAdapter.getItem(position).isAdded
//        mViewModel.itemAdapter.notifyItemChanged(position)
        mViewModel.addItemToCart(position)
    }

    override fun changeCount(position: Int, count: Int, added: Boolean) {
        if (mViewModel.itemAdapter.getItem(position).isAdded) {
//            mViewModel.updateItem(position, count.toString())
            mViewModel.itemAdapter.getItem(position).count = count
            mViewModel.addItemToCart(position)
        }
    }

    override fun clickModifyItem(position: Int) {

    }

    override fun add(position: Int) {
        if (mViewModel.itemAdapter.getItem(position).count < 100) {
            var c = mViewModel.itemAdapter.getItem(position).count
            var count = ++c
            mViewModel.itemAdapter.getItem(position).count = count

            if (mViewModel.itemAdapter.getItem(position).isAdded) {
                mViewModel.itemAdapter.getItem(position).count = count
                Handler().postDelayed({
                    mViewModel.addItemToCart(position)
                }, 20)

            }
            mViewModel.itemAdapter.notifyDataSetChanged()
        }
    }

    override fun remove(position: Int) {
        if (mViewModel.itemAdapter.getItem(position).count > 1) {
            var c = mViewModel.itemAdapter.getItem(position).count
            var count = (--c)
            mViewModel.itemAdapter.getItem(position).count = count
            if (mViewModel.itemAdapter.getItem(position).isAdded) {
                mViewModel.itemAdapter.getItem(position).count = count
                Handler().postDelayed({
                    mViewModel.addItemToCart(position)
                }, 20)

            }
            mViewModel.itemAdapter.notifyDataSetChanged()
        }
    }
}