package com.thejan.proj.restaurant.admin.android.view.fragment

import androidx.appcompat.app.AlertDialog
import android.content.Intent
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
import com.thejan.proj.restaurant.admin.android.helper.*
import com.thejan.proj.restaurant.admin.android.view.activity.AddUpdateFoodActivity
import com.thejan.proj.restaurant.admin.android.view.activity.MainActivity
import com.thejan.proj.restaurant.admin.android.viewmodel.FoodViewModel
import com.thejan.proj.restaurant.admin.android.viewmodel.Status
import com.thejan.proj.restaurant.admin.android.viewmodel.ViewModelState
import com.thejan.proj.restaurant.admin.android.view.adapter.FoodAdapter
import kotlinx.android.synthetic.main.fragment_food.*

class FoodFragment : Fragment(), FoodAdapter.ClickSubItem {

    private lateinit var mViewModel: FoodViewModel
    private lateinit var messageFromResponse: String
    private lateinit var amountFromResponse: String
    private lateinit var itemCountFromResponse: String

    var message = Observer<String> { value ->
        messageFromResponse = value!!
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_food, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        initViewModel()
        initSubscription()
        initRecyclerView()
        initData()
        initAction()
    }

    private fun initAction() {
        fbtnAdd.setOnClickListener {
            val intent = Intent(activity, AddUpdateFoodActivity::class.java)
            AppUtils.startActivityRightToLeftForResult(activity!!, intent, ADD_NEW_FOOD)
        }
    }

    private fun initData() {
        mViewModel.getCategories()
        mViewModel.itemAdapter.setClick(this)
    }

    private fun initViewModel() {
        if(SharedPref.getString(SharedPref.USER_ROLE, "") == USER_ROLE_ADMIN){
            fbtnAdd.visibility = View.VISIBLE
        }else{
            fbtnAdd.visibility = View.GONE
        }
        mViewModel = ViewModelProviders.of(this).get(FoodViewModel::class.java)
        mViewModel.itemAdapter.setClick(this)
    }

    private fun initSubscription() {
        mViewModel.message.observe(this, message)
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
        rvItems.layoutManager = GridLayoutManager(activity, 8)
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
            Status.SUB_SUCCESS2 -> {
                (activity as MainActivity).hideProgress()
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
            FoodFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }

    override fun remove(position: Int) {
        AlertDialog.Builder(activity!!)
            .setMessage(getString(R.string.are_you_sure_delete))
            .setIcon(R.drawable.ic_warning_black_transparent)
            .setPositiveButton(
                getString(R.string.yes)
            ) { dialog, whichButton ->
                mViewModel.removeItem(mViewModel.itemAdapter.getItem(position).foodId!!)
            }
            .setNegativeButton(getString(R.string.cancel), null).show()
            .show()
    }

    override fun edit(position: Int) {
        val intent = Intent(activity, AddUpdateFoodActivity::class.java)
        intent.putExtra(IS_ADD, false)
        intent.putExtra(OBJECT, mViewModel.itemAdapter.getItem(position))
        AppUtils.startActivityRightToLeftForResult(activity!!, intent, ADD_NEW_FOOD)
    }

    override fun setActive(position: Int) {
        mViewModel.itemAdapter.getItem(position).isActive =
            !mViewModel.itemAdapter.getItem(position).isActive
        mViewModel.updateIsActive(mViewModel.itemAdapter.getItem(position), mViewModel.itemAdapter.getItem(position).isActive)
    }

}