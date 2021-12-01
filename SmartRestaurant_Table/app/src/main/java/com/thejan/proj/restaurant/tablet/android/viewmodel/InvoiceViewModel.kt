package com.thejan.proj.restaurant.tablet.android.viewmodel

import androidx.lifecycle.MutableLiveData
import com.thejan.proj.restaurant.tablet.android.helper.*
import com.thejan.proj.restaurant.tablet.android.model.Cart
import com.thejan.proj.restaurant.tablet.android.model.Food
import com.thejan.proj.restaurant.tablet.android.view.adapter.InvoiceAdapter

/**
 * Created by Thejan Thrimanna on 9/12/21.
 */
class InvoiceViewModel:BaseViewModel() {
    var adapter = InvoiceAdapter(ArrayList())

    val amount: MutableLiveData<Double> by lazy {
        MutableLiveData<Double>()
    }

    init {
        state = MutableLiveData()
    }

    fun getOrder(cart: Cart) {
        state!!.postValue(ViewModelState.loading())
        database!!.collection(TABLE_CART_ITEMS)
            .whereEqualTo(COLUMN_CART_ID, cart.cartId)
            .get()
            .addOnSuccessListener { documents ->
                if (documents == null) {
                    state!!.postValue(ViewModelState.error())
                } else {
                    val cartList = ArrayList<Food>()
                    for (doc in documents.documents) {
                        val cartItem = Food(
                            doc.getString(COLUMN_CART_ITEM_ID),
                            doc.getString(COLUMN_FOOD_ID),
                            doc.getString(COLUMN_CATEGORY),
                            doc.getDouble(COLUMN_COST),
                            doc.getString(COLUMN_DESC),
                            doc.getString(COLUMN_IMAGE),
                            doc.getString(COLUMN_NAME),
                            doc.getDouble(COLUMN_PRICE),
                            doc.getString(COLUMN_TYPE),
                            doc.getBoolean(COLUMN_ADDED)!!,
                            doc.getLong(COLUMN_COUNT)!!.toInt(),
                            doc.getString(COLUMN_CART_ID)!!
                        )
                        cartList.add(cartItem)
                    }
                    displayCategoryData(cartList)
                    calculateSum(cartList)
                    state!!.postValue(ViewModelState.success())
                }
            }
    }

    private fun displayCategoryData(response: ArrayList<Food>) {
        if (response.isNullOrEmpty()) {
            setEmpty()
        } else {
            state!!.postValue(ViewModelState.sub_success1())
            val sorted = response.sortedBy { it.name }
            adapter.setItems(sorted)
            adapter.notifyDataSetChanged()
        }
    }

    private fun setEmpty() {
        state!!.postValue(ViewModelState.list_empty())
        adapter.setItems(ArrayList())
        adapter.notifyDataSetChanged()
    }

    private fun calculateSum(cartList: List<Food?>?) {
        var currentSum = 0.0
        for (cl in cartList!!) {
            currentSum += (cl!!.price!! * cl.count!!)
        }
        amount.postValue(currentSum)
    }

}