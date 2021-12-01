package com.thejan.proj.restaurant.admin.android.view.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.thejan.proj.restaurant.admin.android.R
import com.thejan.proj.restaurant.admin.android.helper.*
import com.thejan.proj.restaurant.admin.android.model.Category
import com.thejan.proj.restaurant.admin.android.view.adapter.TableAdapter
import com.thejan.proj.restaurant.admin.android.viewmodel.Status
import com.thejan.proj.restaurant.admin.android.viewmodel.TableViewModel
import com.thejan.proj.restaurant.admin.android.viewmodel.ViewModelState
import kotlinx.android.synthetic.main.activity_table.*
import kotlinx.android.synthetic.main.activity_table.fbtnAdd
import java.util.ArrayList

class TableActivity : BaseActivity(), TableAdapter.ClickSubItem {

    private lateinit var mViewModel: TableViewModel
    private lateinit var messageFromResponse: String
    private lateinit var categoriesFromResponse: ArrayList<Category>

    var message = Observer<String> { value ->
        messageFromResponse = value!!
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_table)
        init()
    }

    private fun init() {
        initViewModel()
        initSubscription()
        initRecyclerView()
        initAction()
        initData()
    }

    private fun initData() {
        mViewModel.getTables()
    }

    private fun initAction() {
        fbtnAdd.setOnClickListener {
            val intent = Intent(this, AddUpdateTableActivity::class.java)
            intent.putExtra(IS_ADD, true)
            AppUtils.startActivityRightToLeftForResult(this!!, intent, ADD_NEW_CATEGORY)
        }

    }

    private fun initRecyclerView() {
        rvTable.setHasFixedSize(true)
        rvTable.layoutManager = LinearLayoutManager(this)
        rvTable.adapter = mViewModel.adapter
        rvTable.addOnItemTouchListener(
            RecyclerItemClickListenr(
                this!!,
                rvTable,
                object : RecyclerItemClickListenr.OnItemClickListener {
                    override fun onItemClick(view: View, position: Int) {
                    }

                    override fun onItemLongClick(view: View?, position: Int) {
                    }
                })
        )
    }

    private fun initViewModel() {
        mViewModel = ViewModelProviders.of(this).get(TableViewModel::class.java)
        mViewModel.adapter.setClick(this)
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

    override fun remove(position: Int) {
        if (mViewModel.adapter.getItem(position).booking.isNullOrEmpty()) {
            AlertDialog.Builder(this)
                .setMessage(getString(R.string.are_you_sure_delete))
                .setIcon(R.drawable.ic_warning_black_transparent)
                .setPositiveButton(
                    getString(R.string.yes)
                ) { dialog, whichButton ->
                    mViewModel.removeItem(mViewModel.adapter.getItem(position).tableNumber!!)
                }
                .setNegativeButton(getString(R.string.cancel), null).show()
                .show()
        } else {
            showToast(getString(R.string.booking_avilable))
        }
    }

    override fun edit(position: Int) {
        if (mViewModel.adapter.getItem(position).booking.isNullOrEmpty()) {
            val intent = Intent(this, AddUpdateTableActivity::class.java)
            intent.putExtra(IS_ADD, false)
            intent.putExtra(OBJECT, mViewModel.adapter.getItem(position))
            AppUtils.startActivityRightToLeftForResult(this!!, intent, ADD_NEW_CATEGORY)
        }else {
            showToast(getString(R.string.booking_avilable))
        }
    }
}