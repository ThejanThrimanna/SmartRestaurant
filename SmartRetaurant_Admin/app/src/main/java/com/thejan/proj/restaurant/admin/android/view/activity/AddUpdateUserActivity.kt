package com.thejan.proj.restaurant.admin.android.view.activity

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.thejan.proj.restaurant.admin.android.R
import com.thejan.proj.restaurant.admin.android.helper.AppUtils
import com.thejan.proj.restaurant.admin.android.helper.IS_ADD
import com.thejan.proj.restaurant.admin.android.helper.OBJECT
import com.thejan.proj.restaurant.admin.android.helper.ROLE_CHEF
import com.thejan.proj.restaurant.admin.android.model.AdminUser
import com.thejan.proj.restaurant.admin.android.viewmodel.*
import kotlinx.android.synthetic.main.activity_add_update_user.*
import kotlinx.android.synthetic.main.activity_add_update_user.btnSave
import kotlinx.android.synthetic.main.activity_add_update_user.etPassword
import kotlinx.android.synthetic.main.activity_add_update_user.tvTitle
import java.util.*


class AddUpdateUserActivity : BaseActivity() {

    private lateinit var mViewModel: AddUpdateUserViewModel
    private lateinit var messageFromResponse: String
    private lateinit var responseRoles: ArrayList<String>
    private lateinit var userResponse: ArrayList<AdminUser>
    private var isAdd: Boolean = true
    private var currentUser: AdminUser? = null

    var message = Observer<String> { value ->
        messageFromResponse = value!!
    }

    var users = Observer<ArrayList<AdminUser>> { value ->
        userResponse = value!!
        if (isAdd) {
            if (value.isNotEmpty()) {
                var maxId = value.maxBy { it.emp_id!!.toInt() }
                etEmpNumber.setText((maxId!!.emp_id!!.toInt() + 1).toString())
            } else {
                etEmpNumber.setText("1")
            }
        }
    }

    var roles = Observer<ArrayList<String>> { value ->
        responseRoles = value!!
        setSpinner()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_update_user)
        init()
    }

    private fun init() {
        initViewModel()
        initSubscription()
        initData()
        initAction()
    }

    private fun initData() {
        mViewModel.getUsers()
        isAdd = intent.getBooleanExtra(IS_ADD, true)
        currentUser = intent.getParcelableExtra(OBJECT)
        if (!isAdd) {
            btnSave.text = getString(R.string.update)
            tvTitle.text = getString(R.string.update_user)
            etName.setText(currentUser!!.name)
            etEmpNumber.setText(currentUser!!.emp_id)
            etPin.setText(AppUtils.decrypt(currentUser!!.pin!!))
            etPassword.setText(AppUtils.decrypt(currentUser!!.password!!))
        }
    }

    private fun initAction() {
        btnSave.setOnClickListener {
            if (isAdd) {
                if (responseRoles[roleSpinner.selectedItemPosition] == ROLE_CHEF) {
                    if (etName.text.toString().isNotEmpty() && etPin.text.toString()
                            .isNotEmpty() && etPassword.text.toString().isNotEmpty()
                    ) {
                        val checkListPin = userResponse.map { it.pin }
                        if (!checkListPin.contains(AppUtils.encrypt(etPin.text.toString()))) {
                            mViewModel.addAllData(
                                etEmpNumber.text.toString(),
                                etName.text.toString(),
                                etPassword.text.toString(),
                                responseRoles[roleSpinner.selectedItemPosition],
                                etPin.text.toString()
                            )
                        } else {
                            showToast(getString(R.string.please_use_a_differen_pin))
                        }
                    } else {
                        showToast(getString(R.string.please_provide_all_details))
                    }
                } else {
                    if (etName.text.toString().isNotEmpty() && etPassword.text.toString()
                            .isNotEmpty()
                    ) {
                        mViewModel.addAllData(
                            etEmpNumber.text.toString(),
                            etName.text.toString(),
                            etPassword.text.toString(),
                            responseRoles[roleSpinner.selectedItemPosition]
                        )
                    } else {
                        showToast(getString(R.string.please_provide_all_details))
                    }
                }
            } else {
                if (responseRoles[roleSpinner.selectedItemPosition] == ROLE_CHEF) {
                    if (etName.text.toString().isNotEmpty() && etPin.text.toString()
                            .isNotEmpty() && etPassword.text.toString().isNotEmpty()
                    ) {
                        val checkListPin = userResponse.map { it.pin }
                        if (etPin.text.toString() != currentUser!!.pin && !checkListPin.contains(AppUtils.encrypt(etPin.text.toString()))) {
                            mViewModel.updateTable(
                                etEmpNumber.text.toString(),
                                etName.text.toString(),
                                etPassword.text.toString(),
                                responseRoles[roleSpinner.selectedItemPosition],
                                etPin.text.toString()
                            )
                        } else {
                            showToast(getString(R.string.please_use_a_differen_pin))
                        }
                    } else {
                        showToast(getString(R.string.please_provide_all_details))
                    }
                }else{
                    mViewModel.updateTable(
                        etEmpNumber.text.toString(),
                        etName.text.toString(),
                        etPassword.text.toString(),
                        responseRoles[roleSpinner.selectedItemPosition]
                    )
                }
            }
        }
    }

    private fun initViewModel() {
        mViewModel = ViewModelProviders.of(this).get(AddUpdateUserViewModel::class.java)
    }

    private fun initSubscription() {
        mViewModel.message.observe(this, message)
        mViewModel.users.observe(this, users)
        mViewModel.roles.observe(this, roles)
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
                showToast(getString(R.string.successfully_user_added))
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
                showToast(getString(R.string.user_exits))
            }
        }
    }

    private fun setSpinner() {
        val adapter: ArrayAdapter<String> = ArrayAdapter<String>(
            this, android.R.layout.simple_spinner_item, responseRoles
        )

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        roleSpinner.adapter = adapter


        roleSpinner.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(
                parentView: AdapterView<*>?,
                selectedItemView: View?,
                position: Int,
                id: Long
            ) {
                if (responseRoles[position] == ROLE_CHEF) {
                    etPin.visibility = View.VISIBLE
                } else {
                    etPin.visibility = View.GONE
                }
            }

            override fun onNothingSelected(parentView: AdapterView<*>?) {
                // your code here
            }
        }
        if (isAdd)
            roleSpinner.setSelection(0)
        else
            roleSpinner.setSelection(responseRoles.indexOf(currentUser!!.role))
    }


    override fun onBackPressed() {
        super.onBackPressed()
        AppUtils.closeActivityLeftToRight(this)
    }
}