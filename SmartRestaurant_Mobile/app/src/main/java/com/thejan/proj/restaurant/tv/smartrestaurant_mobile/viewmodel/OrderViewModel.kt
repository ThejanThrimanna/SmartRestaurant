package com.thejan.proj.restaurant.tv.smartrestaurant_mobile.viewmodel

import androidx.lifecycle.MutableLiveData
import com.thejan.proj.restaurant.tablet.android.viewmodel.ViewModelState
import com.thejan.proj.restaurant.tv.smartrestaurant_mobile.helper.*
import com.thejan.proj.restaurant.tv.smartrestaurant_mobile.model.Cart
import com.thejan.proj.restaurant.tv.smartrestaurant_mobile.view.adapter.OrderAdapter

/**
 * Created by Thejan Thrimanna on 9/17/21.
 */
class OrderViewModel : BaseViewModel() {

    var adapter = OrderAdapter(ArrayList())

    val carts: MutableLiveData<ArrayList<Cart>> by lazy {
        MutableLiveData<ArrayList<Cart>>()
    }

    init {
        state = MutableLiveData()
    }

    fun getOrders() {
        state!!.postValue(ViewModelState.loading())
        database!!.collection(TABLE_CART)
            .whereEqualTo(COLUMN_PHONE, SharedPref.getString(SharedPref.PHONE, ""))
            .whereEqualTo(COLUMN_STATUS, STATUS_SETTLED)
            .addSnapshotListener { documents, error ->
                if (documents == null) {
                    state!!.postValue(ViewModelState.error())
                } else {
                    val cartList = ArrayList<Cart>()
                    for (doc in documents.documents) {
                        val cart = Cart(
                            doc.getDouble(COLUMN_AMOUNT),
                            doc.getString(COLUMN_CART_ID),
                            doc.getString(COLUMN_CHEF),
                            doc.getString(COLUMN_CHEF_NAME),
                            doc.getLong(COLUMN_DATE),
                            doc.getBoolean(COLUMN_IS_ACTIVE),
                            doc.getString(COLUMN_NAME),
                            doc.getString(COLUMN_PHONE),
                            doc.getString(COLUMN_STATUS),
                            doc.getString(COLUMN_TABLE_NUMBER),
                            doc.getDouble(COLUMN_DISCOUNT)
                        )
                        cartList.add(cart)
                    }
                    carts.postValue(cartList)
                    val cartSortedList = cartList.sortedByDescending { list->list.date }
                    val cartSortedArrayList = ArrayList<Cart>()
                    cartSortedArrayList.addAll(cartSortedList)
                    displayCategoryData(cartSortedArrayList)
                }
            }
    }

    private fun displayCategoryData(response: ArrayList<Cart>) {
        if (response.isNullOrEmpty()) {
            setEmpty()
        } else {
            state!!.postValue(ViewModelState.success())
            val sorted = response.sortedByDescending { it.date }
            adapter.setItems(sorted)
            adapter.notifyDataSetChanged()
        }
    }

    private fun setEmpty() {
        adapter.setItems(ArrayList())
        adapter.notifyDataSetChanged()
        state!!.postValue(ViewModelState.list_empty())
    }

}