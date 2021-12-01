package com.thejan.proj.restaurant.admin.android.view.fragment

import android.os.Bundle
import android.os.SharedMemory
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import com.thejan.proj.restaurant.admin.android.R
import com.thejan.proj.restaurant.admin.android.helper.SharedPref
import com.thejan.proj.restaurant.admin.android.view.activity.MainActivity
import kotlinx.android.synthetic.main.fragment_account.*

class AccountFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_account, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        initAction()
        initData()
    }

    private fun initData() {
        tvName.text = ": " + SharedPref.getString(SharedPref.USER_NAME, "")
        tvEmpId.text = ": " + SharedPref.getString(SharedPref.EMP_ID, "")
        tvRole.text = ": " + SharedPref.getString(SharedPref.USER_ROLE, "")
    }

    private fun initAction() {
        btnLogout.setOnClickListener {
            AlertDialog.Builder(activity!!)
                .setMessage(getString(R.string.are_you_sure_logout))
                .setIcon(R.drawable.ic_warning_black_transparent)
                .setPositiveButton(
                    getString(R.string.yes)
                ) { dialog, whichButton ->
                    (activity as MainActivity).gotoLogin()
                }
                .setNegativeButton(getString(R.string.no), null).show()
                .show()

        }
    }

    companion object {
        fun newInstance() =
            AccountFragment().apply {

            }
    }
}