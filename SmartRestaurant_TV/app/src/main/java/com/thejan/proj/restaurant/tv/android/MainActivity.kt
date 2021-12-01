package com.thejan.proj.restaurant.tv.android

import android.app.AlertDialog
import android.content.DialogInterface
import android.graphics.Color
import android.os.Bundle
import android.text.InputType
import android.text.method.PasswordTransformationMethod
import android.widget.EditText
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import com.thejan.proj.restaurant.tablet.android.helper.AppUtils
import com.thejan.proj.restaurant.tv.android.view.activity.BaseActivity
import com.thejan.proj.restaurant.tv.android.view.adapter.PendingOrderAdapter
import com.thejan.proj.restaurant.tv.android.view.adapter.ProcessingOrderAdapter
import com.thejan.proj.restaurant.tv.android.viewmodel.MainViewModel
import com.thejan.proj.restaurant.tv.android.viewmodel.Status
import com.thejan.proj.restaurant.tv.android.viewmodel.ViewModelState
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : BaseActivity(), PendingOrderAdapter.ClickSubItem,
    ProcessingOrderAdapter.ClickSubItem {

    private lateinit var mViewModel: MainViewModel
    private lateinit var messageFromResponse: String

    var message = Observer<String> { value ->
        messageFromResponse = value!!
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        init()
    }

    private fun init() {
        initViewModel()
        initSubscription()
        initRecyclerView()
        initData()
    }

    private fun initData() {
        mViewModel.getCarts()
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
                showToast(getString(R.string.successfully_updated))
                hideProgress()
            }
            Status.SUCCESS -> {
                hideProgress()
            }
            Status.ERROR -> {
                hideProgress()
                showToast(getString(R.string.something_went_wrong))
            }
            Status.VALIDATION_ERROR -> {
                hideProgress()
                showToast(getString(R.string.please_enter_a_valid_pin))
            }
            Status.LIST_EMPTY -> {
                hideProgress()
            }
        }
    }

    private fun initViewModel() {
        mViewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)
        mViewModel.pendingAdapter.setClick(this)
        mViewModel.processingAdapter.setClick(this)
    }

    private fun initRecyclerView() {
        rvPending.setHasFixedSize(true)
        rvPending.layoutManager = GridLayoutManager(this, 2)
        rvPending.adapter = mViewModel.pendingAdapter

        rvProcessing.setHasFixedSize(true)
        rvProcessing.layoutManager = GridLayoutManager(this, 2)
        rvProcessing.adapter = mViewModel.processingAdapter
    }


    override fun accept(position: Int, status: String) {
        val txtUrl = EditText(this)
        txtUrl.setTextColor(Color.WHITE)
        txtUrl.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_VARIATION_PASSWORD
        txtUrl.transformationMethod = PasswordTransformationMethod.getInstance()

        txtUrl.hint = getString(R.string.pin)

        AlertDialog.Builder(this, R.style.AlertDialogCustom)
            .setTitle(getString(R.string.enter_your_pin))
            .setView(txtUrl)
            .setPositiveButton("Ok",
                DialogInterface.OnClickListener { dialog, whichButton ->
                    if (txtUrl.text.isNotEmpty()) {
                        mViewModel.checkTheChef(
                            AppUtils.encrypt(txtUrl.text.toString())!!,
                            mViewModel.pendingAdapter.getItem(position)
                        )
                    } else {
                        showToast(getString(R.string.please_enter_a_valid_pin))
                    }

                })
            .setNegativeButton("Cancel",
                DialogInterface.OnClickListener { dialog, whichButton -> })
            .show()
    }

    override fun done(position: Int, status: String) {
        val txtUrl = EditText(this)
        txtUrl.setTextColor(Color.WHITE)
        txtUrl.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_VARIATION_PASSWORD
        txtUrl.transformationMethod = PasswordTransformationMethod.getInstance()

        txtUrl.hint = getString(R.string.pin)
        AlertDialog.Builder(this, R.style.AlertDialogCustom)
            .setTitle(getString(R.string.enter_your_pin))
            .setView(txtUrl)
            .setPositiveButton("Ok",
                DialogInterface.OnClickListener { dialog, whichButton ->
                    mViewModel.checkChefForTheCart(
                        AppUtils.encrypt(txtUrl.text.toString())!!, mViewModel.processingAdapter.getItem(
                            position
                        )
                    )
                })
            .setNegativeButton("Cancel",
                DialogInterface.OnClickListener { dialog, whichButton -> })
            .show()
    }


}