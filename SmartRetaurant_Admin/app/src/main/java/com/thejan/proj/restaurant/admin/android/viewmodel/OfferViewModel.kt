package com.thejan.proj.restaurant.admin.android.viewmodel

import androidx.lifecycle.MutableLiveData
import com.thejan.proj.restaurant.admin.android.helper.*
import com.thejan.proj.restaurant.admin.android.model.Offer
import com.thejan.proj.restaurant.admin.android.model.Table
import com.thejan.proj.restaurant.admin.android.view.adapter.OfferAdapter

/**
 * Created by Thejan Thrimanna on 9/24/21.
 */
class OfferViewModel:BaseViewModel() {

    var adapter = OfferAdapter(ArrayList())
    val offers: MutableLiveData<ArrayList<Offer>> by lazy {
        MutableLiveData<ArrayList<Offer>>()
    }
    init {
        state = MutableLiveData()
    }

    fun getOffers() {
        state!!.postValue(ViewModelState.loading())
        database!!.collection(TABLE_OFFER)
            .addSnapshotListener { documents, error ->
                if (documents == null) {
                    state!!.postValue(ViewModelState.error())
                } else {
                    val offers = ArrayList<Offer>()
                    for (doc in documents.documents) {
                        val offer = Offer(
                            doc.getString(COLUMN_OFFER_ID),
                            doc.getString(COLUMN_NAME),
                            doc.getLong(COLUMN_PERCENTAGE)!!.toInt(),
                            doc.getLong(COLUMN_START_DATE),
                            doc.getLong(COLUMN_END_DATE),
                            doc.getBoolean(COLUMN_IS_ACTIVE)
                        )
                        offers.add(offer)
                    }
                    displayCategoryData(offers)
                }
            }
    }

    private fun displayCategoryData(response: ArrayList<Offer>) {
        if (response.isNullOrEmpty()) {
            setEmpty()
        } else {
            state!!.postValue(ViewModelState.sub_success1())
            val sorted = response.sortedBy { it.startDate }
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
        database!!.collection(TABLE_OFFER).whereEqualTo(COLUMN_OFFER_ID, id).get()
            .addOnSuccessListener { document ->
                database!!.collection(TABLE_OFFER).document(document.documents[0].id).delete()
            }
    }

}