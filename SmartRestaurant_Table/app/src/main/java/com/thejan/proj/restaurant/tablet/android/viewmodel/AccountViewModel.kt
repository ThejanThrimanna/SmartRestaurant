package com.thejan.proj.restaurant.tablet.android.viewmodel

import androidx.lifecycle.MutableLiveData
import com.thejan.proj.restaurant.tablet.android.helper.*
import com.thejan.proj.restaurant.tablet.android.model.Cart

/**
 * Created by Thejan Thrimanna on 9/21/21.
 */
class AccountViewModel : BaseViewModel() {
    init {
        state = MutableLiveData()
    }

    fun removeReservation() {
        state!!.postValue(ViewModelState.loading())
        database!!.collection(TABLE_TABLE_RESERVATION)
            .whereEqualTo(COLUMN_PHONE, SharedPref.getString(SharedPref.PHONE, "")).get()
            .addOnSuccessListener { document ->
                if (document.documents.isNotEmpty()) {
                    database!!.collection(TABLE_CART)
                        .whereEqualTo(COLUMN_PHONE, SharedPref.getString(SharedPref.PHONE, ""))
                        .whereEqualTo(COLUMN_IS_ACTIVE, true)
                        .get()
                        .addOnSuccessListener { doc ->
                            if (doc.documents.isNotEmpty()) {
                                val cart = Cart(
                                    doc.documents[0].getDouble(COLUMN_AMOUNT),
                                    doc.documents[0].getString(COLUMN_CART_ID),
                                    doc.documents[0].getString(COLUMN_CHEF),
                                    doc.documents[0].getString(COLUMN_CHEF_NAME),
                                    doc.documents[0].getLong(COLUMN_DATE),
                                    doc.documents[0].getBoolean(COLUMN_IS_ACTIVE),
                                    doc.documents[0].getString(COLUMN_NAME),
                                    doc.documents[0].getString(COLUMN_PHONE),
                                    doc.documents[0].getString(COLUMN_STATUS),
                                    doc.documents[0].getString(COLUMN_TABLE_NUMBER),
                                    doc.documents[0].getString(COLUMN_OFFER_ID),
                                    doc.documents[0].getLong(COLUMN_PERCENTAGE)!!.toInt(),
                                    doc.documents[0].getString(COLUMN_OFFER_NAME),
                                    doc.documents[0].getDouble(COLUMN_DISCOUNT),
                                    doc.documents[0].getBoolean(COLUMN_IS_OFFER)
                                )

                                if (cart.status == STATUS_PENDING) {
                                    database!!.collection(TABLE_CART)
                                        .document(doc.documents[0].id)
                                        .update(
                                            mapOf(
                                                COLUMN_STATUS to STATUS_CANCELED,
                                                COLUMN_IS_ACTIVE to false
                                            )
                                        )
                                        .addOnSuccessListener {
                                            database!!.collection(TABLE_TABLE_RESERVATION)
                                                .document(document.documents[0].id)
                                                .delete().addOnSuccessListener {
                                                    database!!.collection(TABLE_TABLES)
                                                        .whereEqualTo(
                                                            COLUMN_TABLE_NUMBER,
                                                            SharedPref.getString(
                                                                SharedPref.TABLE_NUMBER,
                                                                ""
                                                            )
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
                                                                        state!!.postValue(
                                                                            ViewModelState.success()
                                                                        )
                                                                    }
                                                            }
                                                        }
                                                }
                                        }

                                } else {
                                    state!!.postValue(ViewModelState.sub_success1())
                                }
                            } else {

                                database!!.collection(TABLE_TABLE_RESERVATION)
                                    .document(document.documents[0].id)
                                    .delete().addOnSuccessListener {
                                        database!!.collection(TABLE_TABLES)
                                            .whereEqualTo(
                                                COLUMN_TABLE_NUMBER,
                                                SharedPref.getString(
                                                    SharedPref.TABLE_NUMBER,
                                                    ""
                                                )
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
                                                            state!!.postValue(
                                                                ViewModelState.success()
                                                            )
                                                        }
                                                }
                                            }
                                    }

                            }
                        }
                } else {
                    state!!.postValue(ViewModelState.success())
                }
            }
    }

}