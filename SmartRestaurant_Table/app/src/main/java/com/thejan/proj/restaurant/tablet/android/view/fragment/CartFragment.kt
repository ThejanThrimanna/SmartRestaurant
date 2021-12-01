package com.thejan.proj.restaurant.tablet.android.view.fragment

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.thejan.proj.restaurant.tablet.android.R
import com.thejan.proj.restaurant.tablet.android.helper.*
import com.thejan.proj.restaurant.tablet.android.model.Cart
import com.thejan.proj.restaurant.tablet.android.view.activity.MainActivity
import com.thejan.proj.restaurant.tablet.android.view.activity.SettleActivity
import com.thejan.proj.restaurant.tablet.android.view.adapter.CartAdapter
import com.thejan.proj.restaurant.tablet.android.viewmodel.CartViewModel
import com.thejan.proj.restaurant.tablet.android.viewmodel.Status
import com.thejan.proj.restaurant.tablet.android.viewmodel.ViewModelState
import kotlinx.android.synthetic.main.fragment_cart.*

class CartFragment : Fragment(), CartAdapter.ClickSubItem {

    private lateinit var mViewModel: CartViewModel
    private lateinit var messageFromResponse: String
    private var amountFromResponse: Double = 0.0
    private var discount: Double = 0.0
    private var currentStatus: String = ""
    private var cartRespone: Cart? = null
    private lateinit var itemCountFromResponse: String

    var message = Observer<String> { value ->
        messageFromResponse = value!!
    }

    var discountL = Observer<Double> { value ->
        discount = value!!
    }

    var isOffer = Observer<Boolean> { value ->
        if (value)
            clOffers.visibility = View.VISIBLE
        else
            clOffers.visibility = View.GONE
    }

    var cart = Observer<Cart> { value ->
        cartRespone = value
        if (cartRespone!!.isOffer!!)
            tvOffer.text = cartRespone!!.offerName
    }

    var amount = Observer<Double> { value ->
        amountFromResponse = value!!
        tvSubTotal.text = CURRENCY + " " + String.format("%.2f", value.toDouble())
        tvDiscount.text = CURRENCY + " " + String.format("%.2f", discount.toDouble())
        tvPaybleAmount.text =
            CURRENCY + " " + String.format("%.2f", (amountFromResponse - discount).toDouble())
    }

    var status = Observer<String> { value ->
        mViewModel.itemAdapter.setCurrentStatus(value)
        currentStatus = value
        statusChange(value)
    }

    private fun statusChange(status: String) {
        tvStatus.text = status
        when (status) {
            STATUS_PLACE_AN_ORDER -> {
                AppUtils.slideUp(btnSubmit)
                btnSubmit.setBackgroundResource(R.drawable.drawable_button_background)
                btnSubmit.text = getString(R.string.submit)
            }
            STATUS_PENDING, STATUS_PROCESSING, STATUS_SETTLED -> {
                AppUtils.slideDown(btnSubmit)
            }
            STATUS_SERVED -> {
                AppUtils.slideUp(btnSubmit)
                btnSubmit.setBackgroundResource(R.drawable.drawable_button_background)
                btnSubmit.text = getString(R.string.ready_to_pay)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_cart, container, false)
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
        initData()
    }

    private fun initAction() {
        btnSubmit.setOnClickListener {
            when (currentStatus) {
                STATUS_PLACE_AN_ORDER -> {
                    mViewModel.updateTheStatus(STATUS_PENDING)
                }
                STATUS_SERVED -> {
                    AlertDialog.Builder(activity!!)
                        .setMessage(getString(R.string.are_you_sure))
                        .setIcon(R.drawable.ic_warning_black_transparent)
                        .setPositiveButton(
                            getString(R.string.yes)
                        ) { dialog, whichButton ->
                            mViewModel.updateTheStatus(STATUS_BILL_SETTLEMENT_PENDING)
                            val intent = Intent(activity!!, SettleActivity::class.java)
                            AppUtils.startActivityRightToLeft(activity!!, intent)
                        }
                        .setNegativeButton(getString(R.string.cancel), null).show()
                        .show()
                }
            }
        }
    }


    private fun initData() {
        mViewModel.getCurrentCart()
    }

    private fun initViewModel() {
        mViewModel = ViewModelProviders.of(this).get(CartViewModel::class.java)
        mViewModel.itemAdapter.setClick(this)
    }

    private fun initSubscription() {
        mViewModel.message.observe(this, message)
        mViewModel.amount.observe(this, amount)
        mViewModel.status.observe(this, status)
        mViewModel.discountL.observe(this, discountL)
        mViewModel.isOffer.observe(this, isOffer)
        mViewModel.cartR.observe(this, cart)
        mViewModel.state!!.observe(this, Observer<ViewModelState> {
            it?.let {
                update(it)
            }
        })
    }

    private fun initRecyclerView() {
        rvCart.setHasFixedSize(true)
        rvCart.layoutManager = LinearLayoutManager(activity!!)
        rvCart.adapter = mViewModel.itemAdapter
        rvCart.addOnItemTouchListener(
            RecyclerItemClickListenr(
                activity!!,
                rvCart,
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
                llEmptyCartWrapper.visibility = View.VISIBLE
                clServiceTypeLayout.visibility = View.GONE
                (activity as MainActivity).hideProgress()
            }
            Status.CART_NOT_EMPTY -> {
                llEmptyCartWrapper.visibility = View.GONE
                clServiceTypeLayout.visibility = View.VISIBLE
                (activity as MainActivity).hideProgress()
            }
        }
    }


    override fun clickRemoveFromCart(position: Int) {
        mViewModel.removeFromCart(mViewModel.itemAdapter.getItem(position).cartItemId!!)
    }

    override fun changeCount(position: Int, count: Int) {

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

    companion object {
        @JvmStatic
        fun newInstance() =
            CartFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }
}