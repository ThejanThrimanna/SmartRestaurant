package com.thejan.proj.restaurant.tv.smartrestaurant_mobile.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.thejan.proj.restaurant.tablet.android.viewmodel.ViewModelState
import com.thejan.proj.restaurant.tv.smartrestaurant_mobile.helper.COLUMN_NAME
import com.thejan.proj.restaurant.tv.smartrestaurant_mobile.helper.SharedPref

/**
 * Created by Thejan Thrimanna on 9/15/21.
 */
class LoginViewModel : BaseViewModel() {

    init {
        state = MutableLiveData()
    }

    fun login(phone: String, pw: String) {
        state!!.postValue(ViewModelState.loading())
        database!!.collection("users")
            .whereEqualTo("phone", phone)
            .whereEqualTo("password", pw)
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    state!!.postValue(ViewModelState.error())
                } else {
                    SharedPref.saveString(SharedPref.PHONE, phone)
                    SharedPref.saveString(
                        SharedPref.NAME, documents.documents[0].getString(
                            COLUMN_NAME
                        )!!
                    )
                    SharedPref.saveBoolean(SharedPref.IS_LOGIN, true)
                    state!!.postValue(ViewModelState.success())
                }

            }
            .addOnFailureListener { exception ->
                Log.w("TAG", "Error getting documents: ", exception)
                state!!.postValue(ViewModelState.error())
            }
    }


}
