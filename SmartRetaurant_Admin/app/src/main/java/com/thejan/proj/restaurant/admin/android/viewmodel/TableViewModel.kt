package com.thejan.proj.restaurant.admin.android.viewmodel

import androidx.lifecycle.MutableLiveData
import com.thejan.proj.restaurant.admin.android.helper.*
import com.thejan.proj.restaurant.admin.android.model.Table
import com.thejan.proj.restaurant.admin.android.view.adapter.TableAdapter

/**
 * Created by Thejan Thrimanna on 9/1/21.
 */
class TableViewModel : BaseViewModel() {
    var adapter = TableAdapter(ArrayList())
    val tables: MutableLiveData<ArrayList<Table>> by lazy {
        MutableLiveData<ArrayList<Table>>()
    }

    init {
        state = MutableLiveData()
    }

    fun getTables() {
        state!!.postValue(ViewModelState.loading())
        database!!.collection(TABLE_TABLES)
            .addSnapshotListener { documents, error ->
                if (documents == null) {
                    state!!.postValue(ViewModelState.error())
                } else {
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
                    displayCategoryData(tableList)
                }
            }
    }

    private fun displayCategoryData(response: ArrayList<Table>) {
        if (response.isNullOrEmpty()) {
            setEmpty()
        } else {
            state!!.postValue(ViewModelState.sub_success1())
            val sorted = response.sortedBy { it.tableNumber!!.toInt() }
            adapter.setItems(sorted)
            adapter.notifyDataSetChanged()
        }
    }

    private fun setEmpty() {
        state!!.postValue(ViewModelState.list_empty())
        adapter.setItems(ArrayList())
        adapter.notifyDataSetChanged()
    }

    fun removeItem(id: String) {
        database!!.collection(TABLE_TABLES).whereEqualTo(COLUMN_TABLE_NUMBER, id).get()
            .addOnSuccessListener { document ->
                database!!.collection(TABLE_TABLES).document(document.documents[0].id).delete()
            }
    }

}