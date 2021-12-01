package com.thejan.proj.restaurant.admin.android.viewmodel

import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.thejan.proj.restaurant.admin.android.helper.*
import com.thejan.proj.restaurant.admin.android.model.Table

/**
 * Created by Thejan Thrimanna on 9/2/21.
 */
class AddUpdateTableViewModel : BaseViewModel() {

    val tables: MutableLiveData<ArrayList<Table>> by lazy {
        MutableLiveData<ArrayList<Table>>()
    }

    init {
        state = MutableLiveData()
    }

    fun addAllData(
        tableNumber: String,
        numberOfSeats: Int
    ) {
        val item: MutableMap<String, Any> = HashMap()
        item[COLUMN_TABLE_NUMBER] = tableNumber
        item[COLUMN_NUMBER_OF_SEATS] = numberOfSeats
        item[COLUMN_BOOKING] = ""
        item[COLUMN_DEVICE_ID] = ""

        database!!.collection(TABLE_TABLES)
            .whereEqualTo(COLUMN_TABLE_NUMBER, tableNumber)
            .get()
            .addOnSuccessListener {
                if (it.documents.isEmpty()) {
                    database!!.collection(TABLE_TABLES)
                        .document(System.currentTimeMillis().toString() + tableNumber)
                        .set(item)
                        .addOnSuccessListener { documentReference ->
                            Log.d(
                                ContentValues.TAG,
                                "DocumentSnapshot added with ID: $documentReference"
                            )
                            state!!.postValue(ViewModelState.success())
                        }.addOnFailureListener {
                            println(it)
                            state!!.postValue(ViewModelState.error())
                        }
                } else {
                    state!!.postValue(ViewModelState.item_exists())
                }
            }
    }

    fun getTables() {
        state!!.postValue(ViewModelState.loading())
        database!!.collection(TABLE_TABLES)
            .get()
            .addOnSuccessListener { documents ->
                val tableList = ArrayList<Table>()
                for (doc in documents.documents) {
                    val table = Table(
                        doc.getString(COLUMN_DEVICE_ID),
                        doc.getString(COLUMN_TABLE_NUMBER),
                        doc.getLong(COLUMN_NUMBER_OF_SEATS)!!.toInt(),
                        doc.getString(COLUMN_BOOKING)
                    )
                    tableList.add(table)
                }
                tables.postValue(tableList)
                state!!.postValue(ViewModelState.sub_success1())
            }
            .addOnFailureListener { exception ->
                Log.w("TAG", "Error getting documents: ", exception)
                state!!.postValue(ViewModelState.error())
            }
    }

    fun updateTable(
        tableNumber: String,
        numberOfSeats: Int
    ) {
        database!!.collection(TABLE_TABLES)
            .whereEqualTo(COLUMN_TABLE_NUMBER, tableNumber)
            .get()
            .addOnSuccessListener {
                if (it.documents.isNotEmpty()) {
                    database!!.collection(TABLE_TABLES)
                        .document(it.documents[0].id)
                        .update(
                            mapOf(
                                COLUMN_NUMBER_OF_SEATS to numberOfSeats
                            )
                        )
                        .addOnSuccessListener {
                            state!!.postValue(ViewModelState.sub_success2())
                        }
                }
            }
    }
}