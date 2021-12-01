package com.thejan.proj.restaurant.tablet.android.viewmodel

import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.thejan.proj.restaurant.tablet.android.helper.*
import com.thejan.proj.restaurant.tablet.android.helper.SharedPref.NAME
import com.thejan.proj.restaurant.tablet.android.helper.SharedPref.TABLE_NUMBER
import com.thejan.proj.restaurant.tablet.android.model.Category
import com.thejan.proj.restaurant.tablet.android.model.Food
import com.thejan.proj.restaurant.tablet.android.model.Offer
import com.thejan.proj.restaurant.tablet.android.view.adapter.FoodAdapter
import com.thejan.proj.restaurant.tablet.android.view.adapter.MenuCatAdapter
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class HomeViewModel : BaseViewModel() {

    var catAdapter = MenuCatAdapter(ArrayList())
    var itemAdapter = FoodAdapter(ArrayList())
    var currentSelectedCategoryIndex = -1
    var currentSelectedCategory = ""
    var currentCartItems = ArrayList<Food>()

    var allProducts = ArrayList<Food>()
    var allCats = ArrayList<Category>()

    val amount: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }
    val count: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    val status: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    init {
        state = MutableLiveData()
    }

    fun getCategories() {
        state!!.postValue(ViewModelState.loading())
        database!!.collection("food_category")
            .get().addOnSuccessListener { documents ->
                var catList = ArrayList<Category>()
                var docs = documents.documents
                if (docs.isNotEmpty()) {
                    for (d in docs) {
                        val category =
                            Category(d.getString("id"), d.getString("name"), d.getString("image"))
                        catList.add(category)
                    }

                    if (currentSelectedCategoryIndex == -1) currentSelectedCategoryIndex = 0
                    if (currentSelectedCategory.isNullOrEmpty()) currentSelectedCategory =
                        catList!![0].catID!!
                }
                displayCategoryData(catList)
                allCats.clear()
                allCats.addAll(catList)
                getCurrentCart()
            }
            .addOnFailureListener { exception ->
                Log.w("TAG", "Error getting documents: ", exception)
                state!!.postValue(ViewModelState.error())
            }
    }

    private fun getCurrentCart() {
        currentCartItems.clear()
        database!!.collection(TABLE_CART)
            .whereEqualTo(COLUMN_PHONE, SharedPref.getString(SharedPref.PHONE, ""))
            .whereEqualTo(COLUMN_IS_ACTIVE, true)
            .addSnapshotListener { docs, error ->
                if (docs != null && docs.documents != null && docs!!.documents.isNotEmpty()) {
                    status.postValue(docs.documents[0].getString(COLUMN_STATUS))
                    database!!.collection(TABLE_CART_ITEMS)
                        .whereEqualTo(
                            COLUMN_CART_ID,
                            docs.documents[0].getString(COLUMN_CART_ID)
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
                                calculateTheTotal(
                                    docs.documents[0].getDouble(COLUMN_AMOUNT)!!,
                                    docs.documents[0].id
                                )
                                getItems()
                            } else {
                                state!!.postValue(ViewModelState.cartEmpty())
                                status.postValue(STATUS_PLACE_AN_ORDER)
                                calculateTheTotal(docs.documents[0].getDouble(COLUMN_AMOUNT)!!,  docs.documents[0].id)
                                getItems()
                            }
                        }

                } else {
                    state!!.postValue(ViewModelState.cartEmpty())
                    status.postValue(STATUS_PLACE_AN_ORDER)
                    getItems()
                }
            }
    }

    private fun calculateTheTotal(currentAmount: Double = 0.0, docId: String = "") {
        var cost = 0.0
        for (f in currentCartItems) {
            cost += f.price!!.toDouble() * f.count
        }

        if (docId.isNotEmpty()) {
            if (currentAmount != cost)
                database!!.collection(TABLE_CART)
                    .document(docId)
                    .update(
                        mapOf(
                            COLUMN_AMOUNT to cost
                        )
                    )
        } else {
            database!!.collection(TABLE_CART)
                .document(docId)
                .update(
                    mapOf(
                        COLUMN_AMOUNT to currentAmount
                    )
                )
        }
        amount.postValue(cost.toString())
        if (cost > 0)
            state!!.postValue(ViewModelState.cartNotEmpty())
        else
            state!!.postValue(ViewModelState.cartEmpty())

    }

    private fun getItems() {
        database!!.collection(TABLE_MENU)
            .whereEqualTo(COLUMN_IS_ACTIVE, true)
            .addSnapshotListener { documents, error ->
                var docs = documents!!.documents
                var foodList = ArrayList<Food>()
                if (docs.isNotEmpty()) {
                    for (d in docs) {
                        val filterFromCurrentList =
                            currentCartItems.filter { it.foodId == d.getString("id") }
                        if (filterFromCurrentList.isEmpty()) {
                            val food =
                                Food(
                                    foodId = d.getString("id"),
                                    cat = d.getString("cat"),
                                    cost = d.getDouble("cost"),
                                    desc = d.getString("desc"),
                                    image = d.getString("image"),
                                    name = d.getString("name"),
                                    price = d.getDouble("price"),
                                    type = d.getString("type"),
                                    isAdded = false
                                )
                            foodList.add(food)
                        } else {
                            val food =
                                Food(
                                    foodId = d.getString("id"),
                                    cat = d.getString("cat"),
                                    cost = d.getDouble("cost"),
                                    desc = d.getString("desc"),
                                    image = d.getString("image"),
                                    name = d.getString("name"),
                                    price = d.getDouble("price"),
                                    type = d.getString("type"),
                                    count = filterFromCurrentList[0].count,
                                    isAdded = true
                                )
                            foodList.add(food)
                        }

                    }

                }
                allProducts.clear()
                allProducts.addAll(foodList)
                displayItemData(allProducts)
            }
    }

    private fun displayCategoryData(response: ArrayList<Category>) {
        if (response.isNullOrEmpty()) {
            setCategoryListIsEmpty()
        } else {
            state!!.postValue(ViewModelState.sub_success1())
            catAdapter.setItems(response)
            catAdapter.notifyDataSetChanged()
        }
    }

    private fun setCategoryListIsEmpty() {
        state!!.postValue(ViewModelState.list_empty())
        catAdapter.setItems(ArrayList())
        catAdapter.notifyDataSetChanged()
    }

    fun displayItemData(response: ArrayList<Food>) {
        var filteredList = response.filter { it -> it.cat!! == currentSelectedCategory!! }
//        var cartList = SnowWhiteLaundary.database.cartDAO()!!.getAll()
        if (filteredList.isNullOrEmpty()) {
            setItemListIsEmpty()
        } else {
            state!!.postValue(ViewModelState.success())
            itemAdapter.setItems(filteredList)
            itemAdapter.notifyDataSetChanged()
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

                            val cal =Calendar.getInstance().timeInMillis
                            var selectedOffer:Offer? = null
                            for(offer in offerList){
                                if(offer.startDate!!<=cal && offer.endDate!!>cal){
                                    selectedOffer = offer
                                }
                            }
                            val cart: MutableMap<String, Any> = HashMap()
                            val cartId = ID_CART + Calendar.getInstance().timeInMillis.toString()
                            if(selectedOffer!= null) {
                                cart[COLUMN_CART_ID] = cartId
                                cart[COLUMN_PHONE] = phone!!
                                cart[COLUMN_IS_ACTIVE] = true
                                cart[COLUMN_STATUS] = STATUS_PLACE_AN_ORDER
                                cart[COLUMN_DATE] = Calendar.getInstance().timeInMillis
                                cart[COLUMN_TABLE_NUMBER] = SharedPref.getString(TABLE_NUMBER, "")!!
                                cart[COLUMN_CHEF] = ""
                                cart[COLUMN_CHEF] = ""
                                cart[COLUMN_NAME] = SharedPref.getString(NAME, "")!!
                                cart[COLUMN_CHEF_NAME] = ""
                                cart[COLUMN_AMOUNT] = 0.0
                                cart[COLUMN_OFFER_ID] = selectedOffer.offerId!!
                                cart[COLUMN_PERCENTAGE] = selectedOffer.presentage!!
                                cart[COLUMN_OFFER_NAME] = selectedOffer.name!!
                                cart[COLUMN_DISCOUNT] = 0.0
                                cart[COLUMN_IS_OFFER] = true
                            }else{
                                cart[COLUMN_CART_ID] = cartId
                                cart[COLUMN_PHONE] = phone!!
                                cart[COLUMN_IS_ACTIVE] = true
                                cart[COLUMN_STATUS] = STATUS_PLACE_AN_ORDER
                                cart[COLUMN_DATE] = Calendar.getInstance().timeInMillis
                                cart[COLUMN_TABLE_NUMBER] = SharedPref.getString(TABLE_NUMBER, "")!!
                                cart[COLUMN_CHEF] = ""
                                cart[COLUMN_CHEF] = ""
                                cart[COLUMN_NAME] = SharedPref.getString(NAME, "")!!
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
                                    addToCart(cartId, food)
                                }.addOnFailureListener {
                                    println(it)
                                    state!!.postValue(ViewModelState.phoneNumberExists())
                                }
                        }
                        .addOnFailureListener { exception ->
                            Log.w("TAG", "Error getting documents: ", exception)
                            state!!.postValue(ViewModelState.error())
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

    private fun removeFromCart(doc: String) {
        database!!.collection(TABLE_CART_ITEMS).document(doc).delete()
    }

    private fun setItemListIsEmpty() {
        state!!.postValue(ViewModelState.list_empty())
        itemAdapter.setItems(ArrayList())
        itemAdapter.notifyDataSetChanged()
    }

}