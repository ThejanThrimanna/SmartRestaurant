package com.thejan.proj.restaurant.admin.android.view.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import com.thejan.proj.restaurant.admin.android.R
import com.thejan.proj.restaurant.admin.android.helper.*
import com.thejan.proj.restaurant.admin.android.view.adapter.CategoryAdapter
import com.thejan.proj.restaurant.admin.android.viewmodel.CategoryViewModel
import com.thejan.proj.restaurant.admin.android.viewmodel.Status
import com.thejan.proj.restaurant.admin.android.viewmodel.ViewModelState
import kotlinx.android.synthetic.main.fragment_category.*

class CategoryActivity : BaseActivity(), CategoryAdapter.ClickSubItem {

    private lateinit var mViewModel: CategoryViewModel
    private lateinit var messageFromResponse: String

    var message = Observer<String> { value ->
        messageFromResponse = value!!
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_category)
        init()
    }

    private fun init() {
        initViewModel()
        initSubscription()
        initRecyclerView()
        initData()
        initAction()
    }

    private fun initData() {
        mViewModel.getCategories()
    }

    private fun initViewModel() {
        mViewModel = ViewModelProviders.of(this).get(CategoryViewModel::class.java)
        mViewModel.catAdapter.setClick(this)
    }

    private fun initSubscription() {
        mViewModel.message.observe(this, message)
        mViewModel.state!!.observe(this, Observer<ViewModelState> {
            it?.let {
                update(it)
            }
        })
    }

    private fun initAction() {
        fbtnAdd.setOnClickListener {
            val intent = Intent(this, AddUpdateCategoryActivity::class.java)
            AppUtils.startActivityRightToLeftForResult(this!!, intent, ADD_NEW_CATEGORY)
        }

    }

    private fun initRecyclerView() {
        rvCategory.setHasFixedSize(true)
        rvCategory.layoutManager = GridLayoutManager(this, 10)
        rvCategory.adapter = mViewModel.catAdapter
        rvCategory.addOnItemTouchListener(
            RecyclerItemClickListenr(
                this!!,
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
        AlertDialog.Builder(this)
            .setMessage(getString(R.string.are_you_sure_delete))
            .setIcon(R.drawable.ic_warning_black_transparent)
            .setPositiveButton(
                getString(R.string.yes)
            ) { dialog, whichButton ->
                mViewModel.removeItem(mViewModel.catAdapter.getItem(position).catID!!)
            }
            .setNegativeButton(getString(R.string.cancel), null).show()
            .show()
    }

    override fun edit(position: Int) {
        val intent = Intent(this, AddUpdateCategoryActivity::class.java)
        intent.putExtra(IS_ADD, false)
        intent.putExtra(OBJECT, mViewModel.catAdapter.getItem(position))
        AppUtils.startActivityRightToLeftForResult(this!!, intent, ADD_NEW_CATEGORY)
    }
}