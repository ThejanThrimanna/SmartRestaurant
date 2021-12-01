package com.thejan.proj.restaurant.tablet.android.view.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.thejan.proj.restaurant.tablet.android.R
import com.thejan.proj.restaurant.tablet.android.helper.AppUtils.getUniqueId
import com.thejan.proj.restaurant.tablet.android.model.Table
import com.thejan.proj.restaurant.tablet.android.viewmodel.Status
import com.thejan.proj.restaurant.tablet.android.viewmodel.TableRegisterViewModel
import com.thejan.proj.restaurant.tablet.android.viewmodel.ViewModelState
import kotlinx.android.synthetic.main.activity_table_register.*


class TableRegisterActivity : BaseActivity() {

    private lateinit var messageFromResponse: String
    private var tablesFromResponse: List<Table> = ArrayList()
    private lateinit var mViewModel: TableRegisterViewModel
    var selectedTableNumber = ""

    var message = Observer<String> { newName ->
        messageFromResponse = newName!!
    }

    var tables = Observer<List<Table>> { tb ->
        tablesFromResponse = tb!!
        val productNameList: List<String> = tablesFromResponse.map { it.tableNumber!! }

        val spinnerArrayAdapter: ArrayAdapter<String> =
            ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, productNameList)
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerTableList.setAdapter(spinnerArrayAdapter)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_table_register)
        init()
    }

    private fun init() {
        initAction()
        initViewModel()
        initSubscription()
        initData()
    }

    private fun initData() {
        mViewModel.getTables()
    }

    private fun initAction() {
        btnAdd.setOnClickListener {
            if (!tablesFromResponse.isNullOrEmpty()) {
                val table = tablesFromResponse[spinnerTableList.selectedItemPosition]
                val address = getUniqueId(this)
                table.deviceId = address
                selectedTableNumber = table.tableNumber!!
                mViewModel.registerTable(table)
                Log.d("MAC", "initAction: "+ address)
            } else {
                showToast(getString(R.string.please_select_a_table))
            }
        }
    }

    private fun initViewModel() {
        mViewModel = ViewModelProviders.of(this).get(TableRegisterViewModel::class.java)
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

            Status.SUCCESS -> {
                hideProgress()
                val intent = Intent(this, LoginActivity::class.java)
                intent.putExtra("table", selectedTableNumber)
                startActivity(intent)
                finish()
            }

            Status.ERROR -> {
                hideProgress()
                showToast(getString(R.string.something_went_wrong))
            }

            Status.LIST_EMPTY -> {
                hideProgress()
                showToast(getString(R.string.no_tables_avilable))
            }
        }
    }
}