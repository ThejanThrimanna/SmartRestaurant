package com.thejan.proj.restaurant.admin.android.view.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.thejan.proj.restaurant.admin.android.R
import com.thejan.proj.restaurant.admin.android.helper.AppUtils
import com.thejan.proj.restaurant.admin.android.view.activity.CategoryReportActivity
import com.thejan.proj.restaurant.admin.android.view.activity.PerformanceReportActivity
import com.thejan.proj.restaurant.admin.android.view.activity.SalesReportActivity
import kotlinx.android.synthetic.main.fragment_report.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ReportFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ReportFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_report, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sales.setOnClickListener {
            val intent = Intent(activity, SalesReportActivity::class.java)
            AppUtils.startActivityRightToLeft(activity!!, intent)
        }

        performance.setOnClickListener {
            val intent = Intent(activity, PerformanceReportActivity::class.java)
            AppUtils.startActivityRightToLeft(activity!!, intent)
        }

        category.setOnClickListener {
            val intent = Intent(activity, CategoryReportActivity::class.java)
            AppUtils.startActivityRightToLeft(activity!!, intent)
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            ReportFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}