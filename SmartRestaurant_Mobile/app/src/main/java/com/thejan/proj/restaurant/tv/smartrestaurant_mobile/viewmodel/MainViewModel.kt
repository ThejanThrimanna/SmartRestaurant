package com.thejan.proj.restaurant.tv.smartrestaurant_mobile.viewmodel

import androidx.lifecycle.MutableLiveData
import com.thejan.proj.restaurant.tablet.android.viewmodel.ViewModelState
import com.thejan.proj.restaurant.tv.smartrestaurant_mobile.helper.*
import com.thejan.proj.restaurant.tv.smartrestaurant_mobile.model.TableReservation

/**
 * Created by Thejan Thrimanna on 9/22/21.
 */
class MainViewModel : BaseViewModel() {
    val reservation: MutableLiveData<TableReservation> by lazy {
        MutableLiveData<TableReservation>()
    }

    init {
        state = MutableLiveData()
    }

    fun getTableReservation() {
        state!!.postValue(ViewModelState.loading())
        database!!.collection(TABLE_TABLE_RESERVATION)
            .whereEqualTo(COLUMN_PHONE, SharedPref.getString(SharedPref.PHONE, ""))
            .addSnapshotListener { documents, error ->
                if (documents == null) {
                    state!!.postValue(ViewModelState.error())
                } else {
                    if (documents.documents.isEmpty()) {
                        state!!.postValue(ViewModelState.sub_success2())
                    } else {
                        val doc = documents.documents[0]
                        val tableReservation = TableReservation(
                            doc.getLong(COLUMN_DATE),
                            doc.getBoolean(COLUMN_IS_ACTIVE),
                            doc.getString(COLUMN_NAME),
                            doc.getString(COLUMN_PHONE),
                            doc.getString(COLUMN_TABLE_NUMBER),
                            doc.getLong(COLUMN_NUMBER_OF_SEATS)!!.toInt()
                        )
                        reservation.postValue(tableReservation)
                        state!!.postValue(ViewModelState.success())
                    }
                }
            }
    }

    fun removeReservation(phone:String, tabNumber:String) {
        state!!.postValue(ViewModelState.loading())
        database!!.collection(TABLE_TABLE_RESERVATION)
            .whereEqualTo(COLUMN_PHONE, phone).get()
            .addOnSuccessListener { document ->
                if (document.documents.isNotEmpty()) {
                    database!!.collection(TABLE_TABLE_RESERVATION)
                        .document(document.documents[0].id)
                        .delete().addOnSuccessListener {
                            database!!.collection(TABLE_TABLES)
                                .whereEqualTo(
                                    COLUMN_TABLE_NUMBER,
                                    tabNumber
                                )
                                .get()
                                .addOnSuccessListener {
                                    if (it.documents.isNotEmpty()) {
                                        database!!.collection(TABLE_TABLES)
                                            .document(it.documents[0].id)
                                            .update(
                                                mapOf(
                                                    COLUMN_BOOKING to ""
                                                )
                                            )
                                            .addOnSuccessListener {
                                                state!!.postValue(ViewModelState.sub_success3())
                                            }
                                    }
                                }
                        }
                } else {
                    state!!.postValue(ViewModelState.sub_success3())
                }
            }
    }
}