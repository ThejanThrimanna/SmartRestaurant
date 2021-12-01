package com.thejan.proj.restaurant.tablet.android.viewmodel

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.thejan.proj.restaurant.tablet.android.helper.*


class RegisterViewModel : BaseViewModel() {

    init {
        state = MutableLiveData()
    }

    fun saveUser(phone: String, name: String, pw: String) {
        state!!.postValue(ViewModelState.loading())
        database!!.collection("users")
            .whereEqualTo("phone", phone)
            .get()
            .addOnSuccessListener { documents ->
                if (documents.documents.isEmpty()) {
                    addUser(phone, name, pw)
                    Log.d(TAG, "Document does not exist!")
                } else {
                    state!!.postValue(ViewModelState.phoneNumberExists())
                    Log.d(TAG, "Document exists!")
                }

            }
            .addOnFailureListener { exception ->
                Log.w("TAG", "Error getting documents: ", exception)
                addUser(phone, name, pw)
            }
    }

    private fun addUser(phone: String, name: String, pw: String) {
        val user: MutableMap<String, Any> = HashMap()
        user[COLUMN_PHONE] = phone
        user[COLUMN_NAME] = name
        user[COLUMN_PASSWORD] = AppUtils.encrypt(pw)!!

        database!!.collection("users").document(phone)
            .set(user)
            .addOnSuccessListener { documentReference ->
                Log.d(
                    TAG,
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