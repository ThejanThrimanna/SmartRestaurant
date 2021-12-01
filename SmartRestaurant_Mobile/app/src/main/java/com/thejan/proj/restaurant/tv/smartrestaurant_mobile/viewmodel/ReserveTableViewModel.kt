package com.thejan.proj.restaurant.tv.smartrestaurant_mobile.viewmodel

import androidx.lifecycle.MutableLiveData
import com.thejan.proj.restaurant.tablet.android.viewmodel.ViewModelState
import com.thejan.proj.restaurant.tv.smartrestaurant_mobile.helper.*
import com.thejan.proj.restaurant.tv.smartrestaurant_mobile.helper.SharedPref.TABLE_NUMBER
import com.thejan.proj.restaurant.tv.smartrestaurant_mobile.model.Table
import com.thejan.proj.restaurant.tv.smartrestaurant_mobile.view.adapter.TableAdapter
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

/**
 * Created by Thejan Thrimanna on 9/22/21.
 */
class ReserveTableViewModel : BaseViewModel() {

    var adapter = TableAdapter(ArrayList())

    val tables: MutableLiveData<ArrayList<Table>> by lazy {
        MutableLiveData<ArrayList<Table>>()
    }

    init {
        state = MutableLiveData()
    }

    fun getTables(numberOfSeats: Int) {
        state!!.postValue(ViewModelState.loading())
        database!!.collection(TABLE_TABLES)
            .whereEqualTo(COLUMN_BOOKING, "")
            .whereNotEqualTo(COLUMN_DEVICE_ID, "")
            .whereEqualTo(COLUMN_NUMBER_OF_SEATS, numberOfSeats)
            .get()
            .addOnSuccessListener { documents ->
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

    fun reserveTable(table: Table) {
        state!!.postValue(ViewModelState.loading())
        val reservation: MutableMap<String, Any> = HashMap()
        reservation[COLUMN_PHONE] = SharedPref.getString(SharedPref.PHONE, "")!!
        reservation[COLUMN_DATE] = Calendar.getInstance().timeInMillis
        reservation[COLUMN_TABLE_NUMBER] = table.tableNumber!!
        reservation[COLUMN_IS_ACTIVE] = false
        reservation[COLUMN_NAME] = SharedPref.getString(SharedPref.NAME, "")!!
        reservation[COLUMN_NUMBER_OF_SEATS] = table.numberOfSeats!!

        database!!.collection(TABLE_TABLE_RESERVATION)
            .document(SharedPref.getString(TABLE_NUMBER, "0")!!)
            .set(reservation)
            .addOnSuccessListener { documentReference ->
                database!!.collection(TABLE_TABLES)
                    .whereEqualTo(
                        COLUMN_TABLE_NUMBER,
                        table.tableNumber
                    )
                    .get()
                    .addOnSuccessListener { dm ->
                        if (dm.documents.isNotEmpty()) {
                            database!!.collection(TABLE_TABLES)
                                .document(dm.documents[0].id)
                                .update(
                                    mapOf(
                                        COLUMN_BOOKING to SharedPref.getString(
                                            SharedPref.PHONE,
                                            ""
                                        )!!
                                    )
                                )
                                .addOnSuccessListener {
                                    state!!.postValue(ViewModelState.sub_success1())
                                }
                        }
                    }

            }

    }

    private fun displayCategoryData(response: ArrayList<Table>) {
        if (response.isNullOrEmpty()) {
            setEmpty()
        } else {
            state!!.postValue(ViewModelState.success())
            val sorted = response.sortedBy { it.tableNumber }
            adapter.setItems(sorted)
            adapter.notifyDataSetChanged()
        }
    }

    private fun setEmpty() {
        state!!.postValue(ViewModelState.list_empty())
        adapter.setItems(ArrayList())
        adapter.notifyDataSetChanged()
    }
}