package com.thejan.proj.restaurant.admin.android.view.activity

import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.thejan.proj.restaurant.admin.android.R
import com.thejan.proj.restaurant.admin.android.helper.AppUtils
import com.thejan.proj.restaurant.admin.android.helper.IS_ADD
import com.thejan.proj.restaurant.admin.android.helper.OBJECT
import com.thejan.proj.restaurant.admin.android.model.Table
import com.thejan.proj.restaurant.admin.android.viewmodel.AddUpdateTableViewModel
import com.thejan.proj.restaurant.admin.android.viewmodel.Status
import com.thejan.proj.restaurant.admin.android.viewmodel.ViewModelState
import kotlinx.android.synthetic.main.activity_add_updatetable.*
import java.util.ArrayList

class AddUpdateTableActivity : BaseActivity() {

    private lateinit var mViewModel: AddUpdateTableViewModel
    private lateinit var messageFromResponse: String
    private lateinit var tableFromResponse: ArrayList<Table>
    private var isAdd: Boolean = true
    private var currentTable: Table? = null

    var message = Observer<String> { value ->
        messageFromResponse = value!!
    }

    var tables = Observer<ArrayList<Table>> { value ->
        tableFromResponse = value!!
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_updatetable)
        init()
    }

    private fun init() {
        initViewModel()
        initSubscription()
        initAction()
        initData()
    }

    private fun initData() {
        mViewModel.getTables()
        isAdd = intent.getBooleanExtra(IS_ADD, true)
        currentTable = intent.getParcelableExtra(OBJECT)
        if (!isAdd) {
            btnSave.text = getString(R.string.update)
            tvTitle.text = getString(R.string.update_table)
            etNumberOfSeats.setText(currentTable!!.numberOfSeats.toString())
            etTableNumber.setText(currentTable!!.tableNumber.toString())
            etTableNumber.isEnabled = false
        }
    }


    private fun initAction() {
        btnSave.setOnClickListener {
            if(isAdd) {
                if (etTableNumber.text.toString().isNotEmpty() && etNumberOfSeats.text.toString()
                        .isNotEmpty()
                ) {
                    val x =
                        tableFromResponse.filter { it.tableNumber == etTableNumber.text.toString() }
                    if (x.isNullOrEmpty()) {
                        mViewModel.addAllData(
                            etTableNumber.text.toString(),
                            etNumberOfSeats.text.toString().toInt()
                        )
                    } else {
                        showToast(getString(R.string.table_number_exits))
                    }
                } else {
                    showToast(getString(R.string.please_provide_all_details))
                }
            }else{
                if (etNumberOfSeats.text.toString().isNotEmpty() && etNumberOfSeats.text.toString() != currentTable!!.numberOfSeats.toString()) {
                    mViewModel.updateTable(
                        etTableNumber.text.toString(),
                        etNumberOfSeats.text.toString().toInt()
                    )
                } else {
                    showToast(getString(R.string.please_make_a_change_to_update))
                }
            }
        }
    }

    private fun initViewModel() {
        mViewModel = ViewModelProviders.of(this).get(AddUpdateTableViewModel::class.java)
    }

    private fun initSubscription() {
        mViewModel.message.observe(this, message)
        mViewModel.tables.observe(this, tables)
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
            Status.SUB_SUCCESS2 -> {
                hideProgress()
                showToast(getString(R.string.successfully_updated))
                finish()
            }
            Status.SUCCESS -> {
                hideProgress()
                showToast(getString(R.string.successfully_table_added))
                finish()
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
            Status.ITEM_EXISTS -> {
                hideProgress()
                showToast(getString(R.string.table_number_exits))
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        AppUtils.closeActivityLeftToRight(this)
    }
}