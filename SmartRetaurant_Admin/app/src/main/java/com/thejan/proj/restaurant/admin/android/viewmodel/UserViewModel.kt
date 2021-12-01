package com.thejan.proj.restaurant.admin.android.viewmodel

import androidx.lifecycle.MutableLiveData
import com.thejan.proj.restaurant.admin.android.helper.*
import com.thejan.proj.restaurant.admin.android.model.AdminUser
import com.thejan.proj.restaurant.admin.android.view.adapter.UserAdapter

class UserViewModel : BaseViewModel() {

    var userAdapter = UserAdapter(ArrayList())
    val users: MutableLiveData<ArrayList<AdminUser>> by lazy {
        MutableLiveData<ArrayList<AdminUser>>()
    }

    init {
        state = MutableLiveData()
    }

    fun getUsers() {
        state!!.postValue(ViewModelState.loading())
        database!!.collection(TABLE_ADMIN_USERS)
            .addSnapshotListener { documents, error ->
                if (documents!!.isEmpty) {
                    state!!.postValue(ViewModelState.error())
                } else {
                    val userList = ArrayList<AdminUser>()
                    for (doc in documents.documents) {
                        val adminUser = AdminUser(
                            doc.getString(COLUMN_NAME),
                            doc.getString(COLUMN_USER_ROLE),
                            doc.getString(COLUMN_EMP_ID),
                            doc.getString(COLUMN_PASSWORD),
                            doc.getString(COLUMN_PIN)
                        )
                        userList.add(adminUser)
                    }
                    users.postValue(userList)
                    displayCategoryData(userList)
                }
            }
    }

    private fun displayCategoryData(response: ArrayList<AdminUser>) {
        if (response.isNullOrEmpty()) {
            setEmpty()
        } else {
            state!!.postValue(ViewModelState.sub_success1())
            val sorted = response.sortedBy { it.emp_id }
            userAdapter.setItems(sorted)
            userAdapter.notifyDataSetChanged()
        }
    }

    private fun setEmpty() {
        state!!.postValue(ViewModelState.list_empty())
        userAdapter.setItems(ArrayList())
        userAdapter.notifyDataSetChanged()
    }

    fun removeItem(empId: String?) {
        database!!.collection(TABLE_ADMIN_USERS).whereEqualTo(COLUMN_EMP_ID, empId).get()
            .addOnSuccessListener { document ->
                database!!.collection(TABLE_ADMIN_USERS).document(document.documents[0].id).delete()
            }
    }
}