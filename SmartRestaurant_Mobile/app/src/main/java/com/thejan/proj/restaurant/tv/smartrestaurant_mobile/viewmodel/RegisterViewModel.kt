package com.thejan.proj.restaurant.tv.smartrestaurant_mobile.viewmodel

import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.thejan.proj.restaurant.tv.smartrestaurant_mobile.helper.COLUMN_PHONE
import com.thejan.proj.restaurant.tv.smartrestaurant_mobile.helper.TABLE_USERS
import com.thejan.proj.restaurant.tablet.android.viewmodel.ViewModelState
import com.thejan.proj.restaurant.tv.smartrestaurant_mobile.helper.SharedPref

/**
 * Created by Thejan Thrimanna on 9/16/21.
 */
class RegisterViewModel : BaseViewModel() {

    init {
        state = MutableLiveData()
    }

    fun saveUser(phone: String, name: String, pw: String) {
        state!!.postValue(ViewModelState.loading())
        database!!.collection(TABLE_USERS)
            .whereEqualTo(COLUMN_PHONE, phone)
            .get()
            .addOnSuccessListener { documents ->
                if (documents.documents.isEmpty()) {
                    addUser(phone, name, pw)
                    Log.d(ContentValues.TAG, "Document does not exist!")
                } else {
                    state!!.postValue(ViewModelState.phoneNumberExists())
                    Log.d(ContentValues.TAG, "Document exists!")
                }

            }
            .addOnFailureListener { exception ->
                Log.w("TAG", "Error getting documents: ", exception)
                addUser(phone, name, pw)
            }
    }

    private fun addUser(phone: String, name: String, pw: String) {
        val user: MutableMap<String, Any> = HashMap()
        user["phone"] = phone
        user["name"] = name
        user["password"] = pw

        database!!.collection("users").document(phone)
            .set(user)
            .addOnSuccessListener { documentReference ->
                Log.d(
                    ContentValues.TAG,
                    "DocumentSnapshot added with ID: $documentReference"
                )
                state!!.postValue(ViewModelState.success())
                SharedPref.saveString(SharedPref.PHONE, phone)
                SharedPref.saveString(SharedPref.NAME, name)
                SharedPref.saveBoolean(SharedPref.IS_LOGIN, true)
            }.addOnFailureListener {
                println(it)
                state!!.postValue(ViewModelState.phoneNumberExists())
            }
    }
}