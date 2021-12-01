package com.thejan.proj.restaurant.admin.android.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.thejan.proj.restaurant.admin.android.helper.COLUMN_DEVICE_ID
import com.thejan.proj.restaurant.admin.android.helper.COLUMN_TABLE_NUMBER
import com.thejan.proj.restaurant.admin.android.helper.TABLE_TABLES

class SplashScreenViewModel:BaseViewModel() {

    val table: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    init {
        state = MutableLiveData()
    }

    fun checkTable(mac: String) {
        database!!.collection(TABLE_TABLES)
            .whereEqualTo(COLUMN_DEVICE_ID, mac)
            .get()
            .addOnSuccessListener { documents ->
                if (documents.documents.isEmpty()) {
                    state!!.postValue(ViewModelState.sub_success1())
                } else {
                    table.postValue(documents.documents[0].getString(COLUMN_TABLE_NUMBER))
                    state!!.postValue(ViewModelState.success())
                }

            }
            .addOnFailureListener { exception ->
                Log.w("TAG", "Error getting documents: ", exception)
                state!!.postValue(ViewModelState.error())
            }
    }
}