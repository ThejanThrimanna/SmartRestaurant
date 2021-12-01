package com.thejan.proj.restaurant.tablet.android.viewmodel

import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.thejan.proj.restaurant.tablet.android.helper.*
import com.thejan.proj.restaurant.tablet.android.model.Cart
import com.thejan.proj.restaurant.tablet.android.model.Food
import com.thejan.proj.restaurant.tablet.android.view.adapter.CartAdapter
import java.util.*
import kotlin.collections.ArrayList

class CartViewModel : BaseViewModel() {
    var itemAdapter = CartAdapter(ArrayList())
    var currentSelectedCategoryIndex = -1
    var currentSelectedCategory = ""
    var currentCartItems = ArrayList<Food>()
    var currentSum = 0.0
    var currentDiscount = 0.0
    var currentCartDocId = ""
    var cart: Cart? = null

    val amount: MutableLiveData<Double> by lazy {
        MutableLiveData<Double>()
    }
    val discountL: MutableLiveData<Double> by lazy {
        MutableLiveData<Double>()
    }

    val isOffer: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }

    val status: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    val cartR: MutableLiveData<Cart> by lazy {
        MutableLiveData<Cart>()
    }

    init {
        state = MutableLiveData()
    }

    fun getCurrentCart() {
        state!!.postValue(ViewModelState.loading())
        currentCartItems.clear()
        database!!.collection(TABLE_CART)
            .whereEqualTo(COLUMN_PHONE, SharedPref.getString(SharedPref.PHONE, ""))
            .whereEqualTo(COLUMN_IS_ACTIVE, true)
            .addSnapshotListener { doc, error ->
                if (doc != null && doc.documents != null && doc!!.documents.isNotEmpty()) {
                    currentCartDocId = doc.documents[0].id
                    status.postValue(doc.documents[0].getString(COLUMN_STATUS))
                    cart = Cart(
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
                    cartR.postValue(cart)
                    isOffer.postValue(cart!!.isOffer)

                    database!!.collection(TABLE_CART_ITEMS)
                        .whereEqualTo(
                            COLUMN_CART_ID,
                            doc.documents[0].getString(COLUMN_CART_ID)
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
                                displayCart(doc.documents[0].id, currentCartItems)
                            } else {
                                state!!.postValue(ViewModelState.cartEmpty())
                            }
                        }

                } else {
                    state!!.postValue(ViewModelState.cartEmpty())
                }
            }
    }

    fun addItemToCart(position: Int) {
        val phone = SharedPref.getString(SharedPref.PHONE, "")
        val food = itemAdapter.getItem(position)
        state!!.postValue(ViewModelState.loading())
        database!!.collection(TABLE_CART)
            .whereEqualTo(COLUMN_PHONE, phone)
            .whereEqualTo(COLUMN_IS_ACTIVE, true)
            .get().addOnSuccessListener { documents ->
                if (documents.documents.isEmpty()) {
                    val cart: MutableMap<String, Any> = HashMap()
                    val cartId = ID_CART + Calendar.getInstance().timeInMillis.toString()
                    cart[COLUMN_CART_ID] = cartId
                    cart[COLUMN_PHONE] = phone!!
                    cart[COLUMN_IS_ACTIVE] = true
                    cart[COLUMN_STATUS] = STATUS_PLACE_AN_ORDER
                    cart[COLUMN_DATE] = Calendar.getInstance().timeInMillis
                    database!!.collection(TABLE_CART).document()
                        .set(cart)
                        .addOnSuccessListener { documentReference ->
                            Log.d(
                                ContentValues.TAG,
                                "Cart Created $documentReference"
                            )
                            addToCart(cartId, food)
                        }.addOnFailureListener {
                            println(it)
                            state!!.postValue(ViewModelState.phoneNumberExists())
                        }
                } else {
                    val doc = documents.documents[0].getString(COLUMN_CART_ID)
                    addToCart(doc!!, food)
                    state!!.postValue(ViewModelState.success())
                }
            }
    }


    private fun addToCart(cartId: String, food: Food) {
        food.cartId = cartId
        database!!.collection(TABLE_CART_ITEMS)
            .whereEqualTo(COLUMN_CART_ID, cartId)
            .whereEqualTo(COLUMN_FOOD_ID, food.foodId)
            .get().addOnSuccessListener { documents ->
                if (documents.documents.isEmpty()) {
                    food.cartItemId = ID_CART_ITEM + Calendar.getInstance().timeInMillis.toString()
                    database!!.collection(TABLE_CART_ITEMS).document()
                        .set(food)
                        .addOnSuccessListener { documentReference ->
                            Log.d(
                                ContentValues.TAG,
                                "DocumentSnapshot added with ID: $documentReference"
                            )
                            state!!.postValue(ViewModelState.success())
                        }.addOnFailureListener {
                            println(it)
                            state!!.postValue(ViewModelState.phoneNumberExists())
                        }
                } else {
                    if (food.isAdded)
                        database!!.collection(TABLE_CART_ITEMS).document(documents.documents[0].id)
                            .set(food)
                    else
                        removeFromCart(documents.documents[0].id)
                }
            }
    }


    fun removeFromCart(cartItemId: String) {
        database!!.collection(TABLE_CART_ITEMS).whereEqualTo(COLUMN_CART_ITEM_ID, cartItemId).get()
            .addOnSuccessListener { document ->
                database!!.collection(TABLE_CART_ITEMS).document(document.documents[0].id).delete()
            }
    }

    fun updateTheStatus(status: String) {
        database!!.collection(TABLE_CART)
            .document(currentCartDocId)
            .update(
                mapOf(
                    COLUMN_STATUS to status
                )
            )
    }

    private fun displayCart(docId: String, cartList: List<Food?>?) {
        itemAdapter.setItems(cartList)
        itemAdapter.notifyDataSetChanged()
        state!!.postValue(ViewModelState.success())
        state!!.postValue(ViewModelState.cartNotEmpty())
        calculateSum(docId, cartList)
    }

    private fun calculateSum(docId: String = "", cartList: List<Food?>?) {
        currentSum = 0.0
        for (cl in cartList!!) {
            currentSum += (cl!!.price!! * cl.count)
        }

        val currentDiscount = (currentSum * cart!!.perecentage!!) / 100

        if (docId.isNotEmpty()) {
            database!!.collection(TABLE_CART)
                .document(docId)
                .update(
                    mapOf(
                        COLUMN_AMOUNT to currentSum,
                        COLUMN_DISCOUNT to currentDiscount,
                    )
                )
        }

        discountL.postValue(currentDiscount)
        amount.postValue(currentSum)

    }
}