package com.thejan.proj.restaurant.tablet.android.viewmodel

import androidx.lifecycle.MutableLiveData
import com.thejan.proj.restaurant.tablet.android.helper.*
import com.thejan.proj.restaurant.tablet.android.model.Cart
import com.thejan.proj.restaurant.tablet.android.model.Food

class SettleViewModel : BaseViewModel() {
    var currentCartItems = ArrayList<Food>()
    var currentSum = 0.0
    var currentCartDocId = ""
    var cart: Cart? = null

    val amount: MutableLiveData<Double> by lazy {
        MutableLiveData<Double>()
    }

     val dicountL: MutableLiveData<Double> by lazy {
        MutableLiveData<Double>()
    }
    val cartR: MutableLiveData<Cart> by lazy {
        MutableLiveData<Cart>()
    }

    val status: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    init {
        state = MutableLiveData()
    }

    fun getCurrentCart() {
        currentCartItems.clear()
        database!!.collection(TABLE_CART)
            .whereEqualTo(COLUMN_PHONE, SharedPref.getString(SharedPref.PHONE, ""))
            .whereEqualTo(COLUMN_IS_ACTIVE, true)
            .addSnapshotListener { documents, error ->
                if (documents != null && documents.documents != null && documents!!.documents.isNotEmpty()) {
                    currentCartDocId = documents.documents[0].id
                    status.postValue(documents.documents[0].getString(COLUMN_STATUS))

                    cart = Cart(
                        documents.documents[0].getDouble(COLUMN_AMOUNT),
                        documents.documents[0].getString(COLUMN_CART_ID),
                        documents.documents[0].getString(COLUMN_CHEF),
                        documents.documents[0].getString(COLUMN_CHEF_NAME),
                        documents.documents[0].getLong(COLUMN_DATE),
                        documents.documents[0].getBoolean(COLUMN_IS_ACTIVE),
                        documents.documents[0].getString(COLUMN_NAME),
                        documents.documents[0].getString(COLUMN_PHONE),
                        documents.documents[0].getString(COLUMN_STATUS),
                        documents.documents[0].getString(COLUMN_TABLE_NUMBER),
                        documents.documents[0].getString(COLUMN_OFFER_ID),
                        documents.documents[0].getLong(COLUMN_PERCENTAGE)!!.toInt(),
                        documents.documents[0].getString(COLUMN_OFFER_NAME),
                        documents.documents[0].getDouble(COLUMN_DISCOUNT),
                        documents.documents[0].getBoolean(COLUMN_IS_OFFER)
                    )

                    cartR.postValue(cart)

                    database!!.collection(TABLE_CART_ITEMS)
                        .whereEqualTo(
                            COLUMN_CART_ID,
                            documents.documents[0].getString(COLUMN_CART_ID)
                        )
                        .addSnapshotListener { documents, error ->
                            currentCartItems.clear()
                            if (documents != null && documents!!.documents.isNotEmpty()) {
                                for (d in documents.documents) {
                                    val f = Food(
                                        foodId = d.getString("foodId"),
                                        cat = d.getString("cat"),
                                        cost = d.getDouble("cost"),
                                        desc = d.getString("desc"),
                                        image = d.getString("image"),
                                        name = d.getString("name"),
                                        price = d.getDouble("price"),
                                        type = d.getString("type"),
                                        isAdded = true,
                                        cartItemId = d.getString("cartItemId"),
                                        count = d.getLong("count")!!.toInt(),
                                        cartId = d.getString(COLUMN_CART_ID)!!
                                    )
                                    currentCartItems.add(f)
                                }
                                display(currentCartItems)
                            } else {
                                state!!.postValue(ViewModelState.cartEmpty())
                            }
                        }

                } else {
                    state!!.postValue(ViewModelState.cartEmpty())
                }
            }
    }

    fun closeOrder() {
        database!!.collection(TABLE_CART)
            .document(currentCartDocId)
            .update(mapOf(
                COLUMN_IS_ACTIVE to false
            ))
            .addOnSuccessListener {
                state!!.postValue(ViewModelState.sub_success1())
            }
    }

    private fun display(cartList: List<Food?>?) {
        state!!.postValue(ViewModelState.success())
        calculateSum(cartList)
    }

    private fun calculateSum(cartList: List<Food?>?) {
        currentSum = 0.0

        for (cl in cartList!!) {
            currentSum += (cl!!.price!! * cl.count)
        }
        dicountL.postValue(cart!!.discount)
        amount.postValue(currentSum)
    }
}