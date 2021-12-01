package com.thejan.proj.restaurant.admin.android.viewmodel

import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.thejan.proj.restaurant.admin.android.helper.*
import com.thejan.proj.restaurant.admin.android.model.Offer
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

/**
 * Created by Thejan Thrimanna on 9/24/21.
 */
class AddUpdateOfferViewModel : BaseViewModel() {

    val offers: MutableLiveData<ArrayList<Offer>> by lazy {
        MutableLiveData<ArrayList<Offer>>()
    }


    init {
        state = MutableLiveData()
    }

    fun addAllData(
        name: String,
        percentage: Int,
        startDate: Long,
        endDate: Long
    ) {
        val item: MutableMap<String, Any> = HashMap()
        item[COLUMN_OFFER_ID] = "Offer" + Calendar.getInstance().timeInMillis
        item[COLUMN_NAME] = name
        item[COLUMN_PERCENTAGE] = percentage
        item[COLUMN_START_DATE] = startDate
        item[COLUMN_END_DATE] = endDate
        item[COLUMN_IS_ACTIVE] = true

        database!!.collection(TABLE_OFFER)
            .document(Calendar.getInstance().timeInMillis.toString())
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

    }


    fun getOffers() {
        state!!.postValue(ViewModelState.loading())
        database!!.collection(TABLE_OFFER)
            .get()
            .addOnSuccessListener { documents ->
                val tableList = ArrayList<Offer>()
                for (doc in documents.documents) {
                    val table = Offer(
                        doc.getString(COLUMN_OFFER_ID),
                        doc.getString(COLUMN_NAME),
                        doc.getLong(COLUMN_PERCENTAGE)!!.toInt(),
                        doc.getLong(COLUMN_START_DATE),
                        doc.getLong(COLUMN_END_DATE),
                        doc.getBoolean(COLUMN_IS_ACTIVE)
                    )
                    tableList.add(table)
                }
                offers.postValue(tableList)
                state!!.postValue(ViewModelState.sub_success1())
            }
            .addOnFailureListener { exception ->
                Log.w("TAG", "Error getting documents: ", exception)
                state!!.postValue(ViewModelState.error())
            }
    }

    fun updateOffer(
        id:String,
        name: String,
        percentage: Int,
        startDate: Long,
        endDate: Long
    ) {
        database!!.collection(TABLE_OFFER)
            .whereEqualTo(COLUMN_OFFER_ID, id)
            .get()
            .addOnSuccessListener {
                if (it.documents.isNotEmpty()) {
                    database!!.collection(TABLE_OFFER)
                        .document(it.documents[0].id)
                        .update(
                            mapOf(
                                COLUMN_NAME to name,
                                COLUMN_PERCENTAGE to percentage,
                                COLUMN_START_DATE to startDate,
                                COLUMN_END_DATE to endDate
                            )
                        )
                        .addOnSuccessListener {
                            state!!.postValue(ViewModelState.sub_success2())
                        }
                }
            }
    }

}