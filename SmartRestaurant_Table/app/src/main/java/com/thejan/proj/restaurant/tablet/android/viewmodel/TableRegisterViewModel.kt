package com.thejan.proj.restaurant.tablet.android.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.thejan.proj.restaurant.tablet.android.helper.*
import com.thejan.proj.restaurant.tablet.android.helper.SharedPref.NUMBER_OF_SEATS
import com.thejan.proj.restaurant.tablet.android.helper.SharedPref.TABLE_NUMBER
import com.thejan.proj.restaurant.tablet.android.model.Table

class TableRegisterViewModel : BaseViewModel() {

    val allTables = ArrayList<Table>()

    val tables: MutableLiveData<List<Table>> by lazy {
        MutableLiveData<List<Table>>()
    }

    init {
        state = MutableLiveData()
    }

    fun registerTable(table: Table) {
        state!!.postValue(ViewModelState.loading())
        database!!.collection(TABLE_TABLES)
            .whereEqualTo(COLUMN_TABLE_NUMBER, table.tableNumber)
            .get()
            .addOnSuccessListener { documents ->
                if (documents.documents.isEmpty()) {
                    state!!.postValue(ViewModelState.error())
                } else {
                    database!!.collection(TABLE_TABLES)
                        .document(documents.documents[0].id)
                        .set(table)
                        .addOnSuccessListener {
                            state!!.postValue(ViewModelState.success())
                            SharedPref.saveString(TABLE_NUMBER, table.tableNumber!!)
                            SharedPref.saveInteger(NUMBER_OF_SEATS, table.numberOfSeats!!)
                        }
                        .addOnFailureListener { exception ->
                            Log.w("TAG", "Error getting documents: ", exception)
                            state!!.postValue(ViewModelState.error())
                        }
                }
            }
            .addOnFailureListener { exception ->
                Log.w("TAG", "Error getting documents: ", exception)
                state!!.postValue(ViewModelState.error())
            }
    }

    fun getTables(){
        state!!.postValue(ViewModelState.loading())
        database!!.collection(TABLE_TABLES)
            .whereEqualTo(COLUMN_DEVICE_ID, "")
            .addSnapshotListener { documents, error ->
                if (documents!!.documents.isEmpty()) {
                    state!!.postValue(ViewModelState.list_empty())
                } else {
                    allTables.clear()
                    for(d in documents.documents){
                        val table = Table()
                        table.deviceId = d.getString(COLUMN_DEVICE_ID)
                        table.numberOfSeats = d.getLong(COLUMN_NUMBER_OF_SEATS)!!.toInt()
                        table.tableNumber = d.getString(COLUMN_TABLE_NUMBER)
                        allTables.add(table)
                    }
                    val sortedList = allTables.sortedBy { it.tableNumber!!.toInt() }
                    tables.postValue(sortedList)
                    state!!.postValue(ViewModelState.sub_success1())
                }
            }
    }
}