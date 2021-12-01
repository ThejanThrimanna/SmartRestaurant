package com.thejan.proj.restaurant.tablet.android.viewmodel

import androidx.lifecycle.MutableLiveData
import com.thejan.proj.restaurant.tablet.android.helper.*
import com.thejan.proj.restaurant.tablet.android.model.Offer
import com.thejan.proj.restaurant.tablet.android.view.adapter.OfferAdapter
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by Thejan Thrimanna on 9/24/21.
 */
class OfferViewModel : BaseViewModel() {
    var adapter = OfferAdapter(ArrayList())

    val carts: MutableLiveData<ArrayList<Offer>> by lazy {
        MutableLiveData<ArrayList<Offer>>()
    }

    /**
     * Initiate the ViewModel
     */
    init {
        state = MutableLiveData()
    }

    /**
     * Get all offers from the Firestore
     */
    fun getOffers() {
        state!!.postValue(ViewModelState.loading())
        val cal = Calendar.getInstance().timeInMillis
        database!!.collection(TABLE_OFFER)
            .addSnapshotListener { documents, error ->
                if (documents == null) {
                    state!!.postValue(ViewModelState.error())
                } else {
                    val offerList = ArrayList<Offer>()
                    for (doc in documents.documents) {

                        val offer = Offer(
                            doc.getString(COLUMN_OFFER_ID),
                            doc.getString(COLUMN_NAME),
                            doc.getLong(COLUMN_PERCENTAGE)!!.toInt(),
                            doc.getLong(COLUMN_START_DATE),
                            doc.getLong(COLUMN_END_DATE),
                            doc.getBoolean(COLUMN_IS_ACTIVE)
                        )
                        offerList.add(offer)
                    }
                    carts.postValue(offerList)
                    val cal = Calendar.getInstance().timeInMillis
                    val filteredList =
                        offerList.filter { list -> (list.startDate!! > cal || list.endDate!!>cal) }
                    val offerSortedList = filteredList.sortedByDescending { list -> list.startDate }
                    val offerSortedArrayList = ArrayList<Offer>()
                    offerSortedArrayList.addAll(offerSortedList)
                    displayData(offerSortedArrayList)
                }
            }
    }

    /**
     * Populate the RecyclerView
     */
    private fun displayData(response: ArrayList<Offer>) {
        if (response.isNullOrEmpty()) {
            setEmpty()
        } else {
            state!!.postValue(ViewModelState.success())
            adapter.setItems(response)
            adapter.notifyDataSetChanged()
        }
    }

    /**
     * Populate the empty list for the RecyclerView
     */
    private fun setEmpty() {
        adapter.setItems(ArrayList())
        adapter.notifyDataSetChanged()
        state!!.postValue(ViewModelState.list_empty())
    }
}