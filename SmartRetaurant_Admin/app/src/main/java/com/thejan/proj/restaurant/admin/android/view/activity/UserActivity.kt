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
import com.thejan.proj.restaurant.admin.android.view.adapter.UserAdapter
import com.thejan.proj.restaurant.admin.android.view.fragment.UserFragment
import com.thejan.proj.restaurant.admin.android.viewmodel.Status
import com.thejan.proj.restaurant.admin.android.viewmodel.UserViewModel
import com.thejan.proj.restaurant.admin.android.viewmodel.ViewModelState
import kotlinx.android.synthetic.main.fragment_user.*
import java.util.ArrayList

class UserActivity : BaseActivity(), UserAdapter.ClickSubItem {
    private lateinit var mViewModel: UserViewModel
    private lateinit var messageFromResponse: String
    private lateinit var categoriesFromResponse: ArrayList<Category>

    var message = Observer<String> { value ->
        messageFromResponse = value!!
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_user)
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
        mViewModel.getUsers()
    }

    private fun initAction() {
        fbtnAdd.setOnClickListener {
            val intent = Intent(this, AddUpdateUserActivity::class.java)
            AppUtils.startActivityRightToLeftForResult(this!!, intent, ADD_NEW_CATEGORY)

        }
    }

    private fun initRecyclerView() {
        rvUsers.setHasFixedSize(true)
        rvUsers.layoutManager = LinearLayoutManager(this)
        rvUsers.adapter = mViewModel.userAdapter
        rvUsers.addOnItemTouchListener(
            RecyclerItemClickListenr(
                this!!,
                rvUsers,
                object : RecyclerItemClickListenr.OnItemClickListener {
                    override fun onItemClick(view: View, position: Int) {
                    }

                    override fun onItemLongClick(view: View?, position: Int) {
                    }
                })
        )
    }

    private fun initViewModel() {
        mViewModel = ViewModelProviders.of(this).get(UserViewModel::class.java)
        mViewModel.userAdapter.setClick(this)
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
//                mViewModel.getItems(0)
            }
            Status.SUCCESS -> {
                hideProgress()
//                mViewModel.checkCartIsEmpty()
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


    companion object {
        @JvmStatic
        fun newInstance() =
            UserFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        AppUtils.closeActivityLeftToRight(this)
    }

    override fun remove(position: Int) {
        if (mViewModel.userAdapter.getItem(position).emp_id != SharedPref.getString(
                SharedPref.EMP_ID,
                ""
            )
        ) {
            AlertDialog.Builder(this)
                .setMessage(getString(R.string.are_you_sure_delete_user))
                .setIcon(R.drawable.ic_warning_black_transparent)
                .setPositiveButton(
                    getString(R.string.yes)
                ) { dialog, whichButton ->
                    mViewModel.removeItem(mViewModel.userAdapter.getItem(position).emp_id)
                }
                .setNegativeButton(getString(R.string.cancel), null).show()
                .show()
        } else {
            showToast(getString(R.string.can_not_delete_own_user))
        }
    }

    override fun edit(position: Int) {
        if (mViewModel.userAdapter.getItem(position).emp_id != SharedPref.getString(
                SharedPref.EMP_ID,
                ""
            )
        ) {
            val intent = Intent(this, AddUpdateUserActivity::class.java)
            intent.putExtra(IS_ADD, false)
            intent.putExtra(OBJECT, mViewModel.userAdapter.getItem(position))
            AppUtils.startActivityRightToLeftForResult(this!!, intent, ADD_NEW_CATEGORY)
        } else {
            showToast(getString(R.string.can_not_update_own_user))
        }
    }

}