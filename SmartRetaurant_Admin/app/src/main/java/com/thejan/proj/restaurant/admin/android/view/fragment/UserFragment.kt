package com.thejan.proj.restaurant.admin.android.view.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.thejan.proj.restaurant.admin.android.R
import com.thejan.proj.restaurant.admin.android.helper.ADD_NEW_CATEGORY
import com.thejan.proj.restaurant.admin.android.helper.AppUtils
import com.thejan.proj.restaurant.admin.android.helper.RecyclerItemClickListenr
import com.thejan.proj.restaurant.admin.android.model.Category
import com.thejan.proj.restaurant.admin.android.view.activity.AddUpdateCategoryActivity
import com.thejan.proj.restaurant.admin.android.view.activity.MainActivity
import com.thejan.proj.restaurant.admin.android.view.adapter.UserAdapter
import com.thejan.proj.restaurant.admin.android.viewmodel.*
import kotlinx.android.synthetic.main.fragment_user.*
import java.util.ArrayList

class UserFragment : Fragment(), UserAdapter.ClickSubItem {

    private lateinit var mViewModel: UserViewModel
    private lateinit var messageFromResponse: String
    private lateinit var categoriesFromResponse: ArrayList<Category>

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
        return inflater.inflate(R.layout.fragment_user, container, false)
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

    private fun initData(){
        mViewModel.getUsers()
    }

    private fun initAction(){
        fbtnAdd.setOnClickListener {
            fbtnAdd.setOnClickListener {
                val intent = Intent(activity, AddUpdateCategoryActivity::class.java)
                AppUtils.startActivityRightToLeftForResult(activity!!, intent, ADD_NEW_CATEGORY)
            }
        }
    }

    private fun initRecyclerView() {
        rvUsers.setHasFixedSize(true)
        rvUsers.layoutManager = LinearLayoutManager(activity)
        rvUsers.adapter = mViewModel.userAdapter
        rvUsers.addOnItemTouchListener(
            RecyclerItemClickListenr(
                activity!!,
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

    override fun remove(position: Int) {
    }

    override fun edit(position: Int) {
    }
}