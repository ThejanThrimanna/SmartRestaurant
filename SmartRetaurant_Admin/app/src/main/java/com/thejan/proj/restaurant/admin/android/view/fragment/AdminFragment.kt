package com.thejan.proj.restaurant.admin.android.view.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridView
import androidx.fragment.app.Fragment
import com.thejan.proj.restaurant.admin.android.R
import com.thejan.proj.restaurant.admin.android.helper.ADD_NEW_FOOD
import com.thejan.proj.restaurant.admin.android.helper.AppUtils
import com.thejan.proj.restaurant.admin.android.model.AdminModel
import com.thejan.proj.restaurant.admin.android.view.activity.*
import com.thejan.proj.restaurant.admin.android.view.adapter.AdminAdapter
import kotlinx.android.synthetic.main.fragment_admin.*

class AdminFragment : Fragment() {
    var adminGrid: GridView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_admin, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    fun init() {
        initView()
    }

    private fun initView() {
        val courseModelArrayList: ArrayList<AdminModel> = ArrayList<AdminModel>()
        courseModelArrayList.add(AdminModel(getString(R.string.categories), R.drawable.ic_category))
        courseModelArrayList.add(AdminModel(getString(R.string.users), R.drawable.ic_user))
        courseModelArrayList.add(AdminModel(getString(R.string.tables), R.drawable.ic_table))
        courseModelArrayList.add(AdminModel(getString(R.string.offers), R.drawable.ic_offerse))
        courseModelArrayList.add(AdminModel(getString(R.string.reports), R.drawable.ic_report))

        val adapter = AdminAdapter(activity!!, courseModelArrayList)
        gvItems.adapter = adapter

        gvItems.setOnItemClickListener { parent, view, position, id ->
            when (position) {
                0 -> {
                    val intent = Intent(activity, CategoryActivity::class.java)
                    AppUtils.startActivityRightToLeft(activity!!, intent)
                }
                1 -> {
                    val intent = Intent(activity, UserActivity::class.java)
                    AppUtils.startActivityRightToLeft(activity!!, intent)
                }
                2 -> {
                    val intent = Intent(activity, TableActivity::class.java)
                    AppUtils.startActivityRightToLeft(activity!!, intent)
                }
                3 -> {
                    val intent = Intent(activity, OffersActivity::class.java)
                    AppUtils.startActivityRightToLeft(activity!!, intent)
                }
                4 -> {
                    val intent = Intent(activity, ReportActivity::class.java)
                    AppUtils.startActivityRightToLeft(activity!!, intent)
                }

            }
        }
    }

    companion object {
        fun newInstance() =
            AdminFragment().apply {

            }
    }
}