package com.thejan.proj.restaurant.tablet.android.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.thejan.proj.restaurant.tablet.android.helper.*
import com.thejan.proj.restaurant.tablet.android.helper.SharedPref.TABLE_NUMBER
import com.thejan.proj.restaurant.tablet.android.model.Table
import com.thejan.proj.restaurant.tablet.android.model.TableReservation
import java.util.*
import kotlin.collections.HashMap

class LoginViewModel : BaseViewModel() {

    val tableNumber: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }
    val reservation: MutableLiveData<TableReservation> by lazy {
        MutableLiveData<TableReservation>()
    }

    init {
        state = MutableLiveData()
    }

    fun login(phone: String, pw: String) {
        state!!.postValue(ViewModelState.loading())
        database!!.collection(TABLE_USERS)
            .whereEqualTo(COLUMN_PHONE, phone)
            .whereEqualTo(COLUMN_PASSWORD, AppUtils.encrypt(pw)!!)
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    state!!.postValue(ViewModelState.error())
                } else {
                    database!!.collection(TABLE_TABLE_RESERVATION)
                        .whereEqualTo(COLUMN_PHONE, phone)
                        .get()
                        .addOnSuccessListener { dd ->
                            if (dd.documents.isEmpty()) {
                                val reservation: MutableMap<String, Any> = HashMap()
                                reservation[COLUMN_PHONE] = phone
                                reservation[COLUMN_DATE] = Calendar.getInstance().timeInMillis
                                reservation[COLUMN_TABLE_NUMBER] =
                                    SharedPref.getString(SharedPref.TABLE_NUMBER, "0")!!
                                reservation[COLUMN_IS_ACTIVE] = true
                                reservation[COLUMN_NAME] =
                                    documents.documents[0].getString(COLUMN_NAME)!!
                                reservation[COLUMN_NUMBER_OF_SEATS] =
                                    SharedPref.getInteger(SharedPref.NUMBER_OF_SEATS, 0)

                                database!!.collection(TABLE_TABLE_RESERVATION)
                                    .document(SharedPref.getString(TABLE_NUMBER, "0")!!)
                                    .set(reservation)
                                    .addOnSuccessListener { documentReference ->
                                        database!!.collection(TABLE_TABLES)
                                            .whereEqualTo(
                                                COLUMN_TABLE_NUMBER,
                                                SharedPref.getString(SharedPref.TABLE_NUMBER, "0")!!
                                            )
                                            .get()
                                            .addOnSuccessListener { dm ->
                                                if (dm.documents.isNotEmpty()) {
                                                    database!!.collection(TABLE_TABLES)
                                                        .document(dm.documents[0].id)
                                                        .update(
                                                            mapOf(
                                                                COLUMN_BOOKING to phone
                                                            )
                                                        )
                                                        .addOnSuccessListener {
                                                            SharedPref.saveString(
                                                                SharedPref.PHONE,
                                                                phone
                                                            )
                                                            SharedPref.saveString(
                                                                SharedPref.NAME,
                                                                documents.documents[0].getString(
                                                                    COLUMN_NAME
                                                                )!!
                                                            )
                                                            SharedPref.saveBoolean(
                                                                SharedPref.IS_LOGIN,
                                                                true
                                                            )
                                                            state!!.postValue(ViewModelState.success())
                                                        }
                                                }
                                            }

                                    }.addOnFailureListener {
                                        println(it)
                                        state!!.postValue(ViewModelState.phoneNumberExists())
                                    }
                            } else if(dd.documents[0].getString(COLUMN_TABLE_NUMBER) == SharedPref.getString(SharedPref.TABLE_NUMBER, "0")!!){
                                database!!.collection(TABLE_TABLE_RESERVATION)
                                    .document(dd.documents[0].id)
                                    .update(
                                        mapOf(
                                            COLUMN_DATE to Calendar.getInstance().timeInMillis,
                                            COLUMN_IS_ACTIVE to true
                                        )
                                    )
                                    .addOnSuccessListener {
                                        database!!.collection(TABLE_TABLES)
                                            .whereEqualTo(
                                                COLUMN_TABLE_NUMBER,
                                                SharedPref.getString(SharedPref.TABLE_NUMBER, "0")!!
                                            )
                                            .get()
                                            .addOnSuccessListener {
                                                if (it.documents.isNotEmpty()) {
                                                    database!!.collection(TABLE_TABLES)
                                                        .document(it.documents[0].id)
                                                        .update(
                                                            mapOf(
                                                                COLUMN_BOOKING to phone
                                                            )
                                                        )
                                                        .addOnSuccessListener {
                                                            SharedPref.saveString(
                                                                SharedPref.PHONE,
                                                                phone
                                                            )
                                                            SharedPref.saveString(
                                                                SharedPref.NAME,
                                                                documents.documents[0].getString(
                                                                    COLUMN_NAME
                                                                )!!
                                                            )
                                                            SharedPref.saveBoolean(
                                                                SharedPref.IS_LOGIN,
                                                                true
                                                            )
                                                            state!!.postValue(ViewModelState.success())
                                                        }
                                                }
                                            }
                                    }
                            }else{
                                state!!.postValue(ViewModelState.validation_issue())
                            }
                        }


                }
            }
            .addOnFailureListener { exception ->
                Log.w("TAG", "Error getting documents: ", exception)
                state!!.postValue(ViewModelState.error())
            }
    }

    fun unRegisterTable(mac: String) {
        state!!.postValue(ViewModelState.loading())
        database!!.collection(TABLE_TABLES)
            .whereEqualTo(COLUMN_DEVICE_ID, mac)
            .get()
            .addOnSuccessListener { documents ->
                if (documents.documents.isEmpty()) {
                    state!!.postValue(ViewModelState.error())
                } else {
                    val table = Table()
                    table.deviceId = ""
                    table.numberOfSeats =
                        documents.documents[0].getLong(COLUMN_NUMBER_OF_SEATS)!!.toInt()
                    table.tableNumber = documents.documents[0].getString(COLUMN_TABLE_NUMBER)
                    database!!.collection(TABLE_TABLES)
                        .document(documents.documents[0].id)
                        .set(table)
                        .addOnSuccessListener {
                            state!!.postValue(ViewModelState.sub_success1())
                        }
                        .addOnFailureListener { exception ->
                            Log.w("TAG", "Error getting documents: ", exception)
                            state!!.postValue(ViewModelState.error_default())
                        }
                }
            }
            .addOnFailureListener { exception ->
                Log.w("TAG", "Error getting documents: ", exception)
                state!!.postValue(ViewModelState.error())
            }
    }

    fun getTableNumber(mac: String) {
        state!!.postValue(ViewModelState.loading())
        database!!.collection(TABLE_TABLES)
            .whereEqualTo(COLUMN_DEVICE_ID, mac)
            .get()
            .addOnSuccessListener { documents ->
                if (documents.documents.isEmpty()) {
                    state!!.postValue(ViewModelState.error_default())
                } else {
                    tableNumber.postValue(documents.documents[0].getString(COLUMN_TABLE_NUMBER))
                    state!!.postValue(ViewModelState.sub_success4())
                }
            }
    }

    fun getTheTable(tableNumber: String) {
        state!!.postValue(ViewModelState.loading())
        database!!.collection(TABLE_TABLES)
            .whereEqualTo(COLUMN_TABLE_NUMBER, tableNumber)
            .addSnapshotListener { documents, error ->
                if (documents!!.documents.isEmpty()) {
                    state!!.postValue(ViewModelState.tableNotAvailable())
                } else {
                    val booking = documents.documents[0].getString(COLUMN_BOOKING)
                    if (booking.isNullOrEmpty()) {
                        state!!.postValue(ViewModelState.sub_success2())
                    } else {
                        database!!.collection(TABLE_TABLE_RESERVATION)
                            .whereEqualTo(COLUMN_TABLE_NUMBER, tableNumber)
                            .addSnapshotListener { value, error ->
                                if (value!!.documents.isNotEmpty()) {
                                    val tableReservation = TableReservation(
                                        value.documents[0].getLong(COLUMN_DATE),
                                        value.documents[0].getBoolean(COLUMN_IS_ACTIVE),
                                        value.documents[0].getString(COLUMN_NAME),
                                        value.documents[0].getString(COLUMN_PHONE),
                                        value.documents[0].getString(COLUMN_TABLE_NUMBER),
                                        value.documents[0].getLong(COLUMN_NUMBER_OF_SEATS)!!.toInt()
                                    )
                                    reservation.postValue(tableReservation)
                                    state!!.postValue(ViewModelState.sub_success3())
                                } else {
                                    state!!.postValue(ViewModelState.sub_success2())
                                }
                            }
                    }
                }
            }

    }

    fun removeReservation(phone: String) {
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
                                    SharedPref.getString(SharedPref.TABLE_NUMBER, "")
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
                                                state!!.postValue(ViewModelState.sub_success5())
                                            }
                                    }
                                }
                        }
                } else {
                    state!!.postValue(ViewModelState.sub_success5())
                }
            }
    }

}