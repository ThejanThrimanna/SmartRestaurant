package com.thejan.proj.restaurant.tablet.android.view.fragment

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.thejan.proj.restaurant.tablet.android.R
import com.thejan.proj.restaurant.tablet.android.helper.AppUtils
import com.thejan.proj.restaurant.tablet.android.helper.STATUS_BILL_SETTLEMENT_PENDING
import com.thejan.proj.restaurant.tablet.android.helper.SharedPref
import com.thejan.proj.restaurant.tablet.android.view.activity.MainActivity
import com.thejan.proj.restaurant.tablet.android.view.activity.SettleActivity
import com.thejan.proj.restaurant.tablet.android.viewmodel.AccountViewModel
import com.thejan.proj.restaurant.tablet.android.viewmodel.LoginViewModel
import com.thejan.proj.restaurant.tablet.android.viewmodel.Status
import com.thejan.proj.restaurant.tablet.android.viewmodel.ViewModelState
import kotlinx.android.synthetic.main.fragment_account.*
import kotlinx.android.synthetic.main.fragment_order.*

class AccountFragment : Fragment() {

    private lateinit var messageFromResponse: String
    private lateinit var mViewModel: AccountViewModel

    var message = Observer<String> { newName ->
        messageFromResponse = newName!!
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        initAction()
        initData()
        initViewModel()
        initSubscription()
    }

    private fun initAction() {
        btnLogout.setOnClickListener {
            AlertDialog.Builder(activity!!)
                .setMessage(getString(R.string.are_you_sure_want_to_exit))
                .setIcon(R.drawable.ic_warning_black_transparent)
                .setPositiveButton(
                    getString(R.string.yes)
                ) { dialog, whichButton ->
                    mViewModel.removeReservation()
                }
                .setNegativeButton(getString(R.string.cancel), null).show()
                .show()
        }
    }

    private fun initViewModel() {
        mViewModel = ViewModelProviders.of(this).get(AccountViewModel::class.java)
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
            Status.SUCCESS -> {
                (activity as MainActivity).hideProgress()
                (activity as MainActivity).gotoLogin()
            }
            Status.SUB_SUCCESS1 -> {
                (activity as MainActivity).hideProgress()
                (activity as MainActivity).showToast(getString(R.string.you_can_not_logout))
            }
            Status.ERROR -> {
                (activity as MainActivity).hideProgress()
            }
            Status.TIMEOUT -> {
                (activity as MainActivity).hideProgress()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_account, container, false)
    }


    private fun initData() {
        tvName.text = ": " + SharedPref.getString(SharedPref.NAME, "")
        tvPhone.text = ": " + SharedPref.getString(SharedPref.PHONE, "")
        tvTableNumber.text = ": " + SharedPref.getString(SharedPref.TABLE_NUMBER, "")
        tvNumberOfSeats.text = ": " + SharedPref.getInteger(SharedPref.NUMBER_OF_SEATS, 0).toString()
    }


    companion object {
        @JvmStatic
        fun newInstance() =
            AccountFragment().apply {

            }
    }
}