package com.thejan.proj.restaurant.tablet.android.viewmodel

import android.content.ContentValues
import android.os.Handler
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.thejan.proj.restaurant.tablet.android.helper.*
import com.thejan.proj.restaurant.tablet.android.model.Cart
import com.thejan.proj.restaurant.tablet.android.model.Food
import com.thejan.proj.restaurant.tablet.android.model.Offer
import com.thejan.proj.restaurant.tablet.android.view.adapter.CartItemAdapter
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

/**
 * Created by Thejan Thrimanna on 9/17/21.
 */
class OrderDetailsViewModel : BaseViewModel() {
    var adapter = CartItemAdapter(ArrayList())
    val amount: MutableLiveData<Double> by lazy {
        MutableLiveData<Double>()
    }

    var cart: Cart? = null

    init {
        state = MutableLiveData()
    }

    fun getOrder(cart: Cart) {
        state!!.postValue(ViewModelState.loading())
        val phone = SharedPref.getString(SharedPref.PHONE, "")
        database!!.collection(TABLE_CART)
            .whereEqualTo(COLUMN_PHONE, phone)
            .whereEqualTo(COLUMN_IS_ACTIVE, true)
            .get().addOnSuccessListener { documents ->
                if (documents.documents.isNotEmpty()) {
                    if (documents.documents[0].getString(COLUMN_STATUS) == STATUS_PENDING || documents.documents[0].getString(
                            COLUMN_STATUS
                        ) == STATUS_PLACE_AN_ORDER
                    ) {
                        state!!.postValue(ViewModelState.sub_success4())
                    } else {
                        state!!.postValue(ViewModelState.sub_success3())
                    }
                }
                state!!.postValue(ViewModelState.sub_success4())
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

    fun reOrder() {
        state!!.postValue(ViewModelState.loading())
        val phone = SharedPref.getString(SharedPref.PHONE, "")
        database!!.collection(TABLE_CART)
            .whereEqualTo(COLUMN_PHONE, phone)
            .whereEqualTo(COLUMN_IS_ACTIVE, true)
            .get().addOnSuccessListener { documents ->
                if (documents.documents.isEmpty()) {
                    /*val cart: MutableMap<String, Any> = HashMap()
                    val cartId = ID_CART + Calendar.getInstance().timeInMillis.toString()
                    cart[COLUMN_CART_ID] = cartId
                    cart[COLUMN_PHONE] = phone!!
                    cart[COLUMN_IS_ACTIVE] = true
                    cart[COLUMN_STATUS] = STATUS_PLACE_AN_ORDER
                    cart[COLUMN_DATE] = Calendar.getInstance().timeInMillis
                    cart[COLUMN_TABLE_NUMBER] = SharedPref.getString(SharedPref.TABLE_NUMBER, "")!!
                    cart[COLUMN_CHEF] = ""
                    cart[COLUMN_CHEF] = ""
                    cart[COLUMN_NAME] = SharedPref.getString(SharedPref.NAME, "")!!
                    cart[COLUMN_CHEF_NAME] = ""
                    cart[COLUMN_AMOUNT] = 0.0*/

                    database!!.collection(TABLE_OFFER)
                        .get()
                        .addOnSuccessListener { documents ->
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

                            val cal = Calendar.getInstance().timeInMillis
                            var selectedOffer: Offer? = null
                            for (offer in offerList) {
                                if (offer.startDate!! <= cal && offer.endDate!! > cal) {
                                    selectedOffer = offer
                                }
                            }
                            val cart: MutableMap<String, Any> = HashMap()
                            val cartId = ID_CART + Calendar.getInstance().timeInMillis.toString()
                            if (selectedOffer != null) {
                                cart[COLUMN_CART_ID] = cartId
                                cart[COLUMN_PHONE] = phone!!
                                cart[COLUMN_IS_ACTIVE] = true
                                cart[COLUMN_STATUS] = STATUS_PLACE_AN_ORDER
                                cart[COLUMN_DATE] = Calendar.getInstance().timeInMillis
                                cart[COLUMN_TABLE_NUMBER] =
                                    SharedPref.getString(SharedPref.TABLE_NUMBER, "")!!
                                cart[COLUMN_CHEF] = ""
                                cart[COLUMN_CHEF] = ""
                                cart[COLUMN_NAME] = SharedPref.getString(SharedPref.NAME, "")!!
                                cart[COLUMN_CHEF_NAME] = ""
                                cart[COLUMN_AMOUNT] = 0.0
                                cart[COLUMN_OFFER_ID] = selectedOffer.offerId!!
                                cart[COLUMN_PERCENTAGE] = selectedOffer.presentage!!
                                cart[COLUMN_OFFER_NAME] = selectedOffer.name!!
                                cart[COLUMN_DISCOUNT] = 0.0
                                cart[COLUMN_IS_OFFER] = true
                            } else {
                                cart[COLUMN_CART_ID] = cartId
                                cart[COLUMN_PHONE] = phone!!
                                cart[COLUMN_IS_ACTIVE] = true
                                cart[COLUMN_STATUS] = STATUS_PLACE_AN_ORDER
                                cart[COLUMN_DATE] = Calendar.getInstance().timeInMillis
                                cart[COLUMN_TABLE_NUMBER] =
                                    SharedPref.getString(SharedPref.TABLE_NUMBER, "")!!
                                cart[COLUMN_CHEF] = ""
                                cart[COLUMN_CHEF] = ""
                                cart[COLUMN_NAME] = SharedPref.getString(SharedPref.NAME, "")!!
                                cart[COLUMN_CHEF_NAME] = ""
                                cart[COLUMN_AMOUNT] = 0.0
                                cart[COLUMN_OFFER_ID] = ""
                                cart[COLUMN_PERCENTAGE] = 0
                                cart[COLUMN_OFFER_NAME] = ""
                                cart[COLUMN_DISCOUNT] = 0.0
                                cart[COLUMN_IS_OFFER] = false
                            }


                            database!!.collection(TABLE_CART).document()
                                .set(cart)
                                .addOnSuccessListener { documentReference ->
                                    Log.d(
                                        ContentValues.TAG,
                                        "Cart Created $documentReference"
                                    )
//                            addToCart(cartId, food)
                                    for (item in adapter.getAll()) {
                                        Handler().postDelayed({
                                            addToCart(cartId, item!!)
                                        }, 20)
                                    }
                                }.addOnFailureListener {
                                    println(it)
                                    state!!.postValue(ViewModelState.error())
                                }
                        }
                } else {
                    val doc = documents.documents[0].getString(COLUMN_CART_ID)
//                    addToCart(doc!!, food)
                    for (item in adapter.getAll()) {
                        Handler().postDelayed({
                            addToCart(doc!!, item!!)
                        }, 20)
                    }
                    state!!.postValue(ViewModelState.success())
                }
            }


//        for(item in adapter.getAll()){
//            Handler().postDelayed({
//                addItemToCart(item!!)
//            }, 20)
//        }

        state!!.postValue(ViewModelState.sub_success2())
    }

    private fun addItemToCart(food: Food) {
        val phone = SharedPref.getString(SharedPref.PHONE, "")
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
                    cart[COLUMN_TABLE_NUMBER] = SharedPref.getString(SharedPref.TABLE_NUMBER, "")!!
                    cart[COLUMN_CHEF] = ""
                    cart[COLUMN_CHEF] = ""
                    cart[COLUMN_NAME] = SharedPref.getString(SharedPref.NAME, "")!!
                    cart[COLUMN_CHEF_NAME] = ""
                    cart[COLUMN_AMOUNT] = 0.0

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
                }
            }
    }
}