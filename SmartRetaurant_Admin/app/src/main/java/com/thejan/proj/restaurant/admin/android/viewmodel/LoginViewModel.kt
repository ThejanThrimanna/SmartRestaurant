package com.thejan.proj.restaurant.admin.android.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.thejan.proj.restaurant.admin.android.helper.*

class LoginViewModel:BaseViewModel() {

    val tableNumber: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    init {
        state = MutableLiveData()
    }

    fun login(emp_id: String, pw: String) {
        state!!.postValue(ViewModelState.loading())
        database!!.collection(TABLE_ADMIN_USERS)
            .whereEqualTo(COLUMN_EMP_ID, emp_id)
            .whereEqualTo(COLUMN_PASSWORD, pw)
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    state!!.postValue(ViewModelState.error())
                } else {
                    SharedPref.saveString(SharedPref.EMP_ID, emp_id)
                    SharedPref.saveString(SharedPref.USER_ROLE, documents.documents[0].getString(COLUMN_USER_ROLE)!!)
                    SharedPref.saveBoolean(SharedPref.IS_LOGIN, true)
                    SharedPref.saveString(SharedPref.USER_NAME, documents.documents[0].getString(
                        COLUMN_NAME)!!)
                    state!!.postValue(ViewModelState.success())
                }
            }
            .addOnFailureListener { exception ->
                Log.w("TAG", "Error getting documents: ", exception)
                state!!.postValue(ViewModelState.error())
            }
    }
}