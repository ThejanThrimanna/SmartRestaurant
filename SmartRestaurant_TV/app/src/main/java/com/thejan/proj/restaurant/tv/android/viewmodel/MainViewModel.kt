package com.thejan.proj.restaurant.tv.android.viewmodel

import androidx.lifecycle.MutableLiveData
import com.thejan.proj.restaurant.tv.android.view.adapter.ProcessingOrderAdapter
import com.thejan.proj.restaurant.tv.android.helper.*
import com.thejan.proj.restaurant.tv.android.model.Cart
import com.thejan.proj.restaurant.tv.android.model.Food
import com.thejan.proj.restaurant.tv.android.view.adapter.PendingOrderAdapter

class MainViewModel : BaseViewModel() {
    var pendingAdapter = PendingOrderAdapter(ArrayList())
    var processingAdapter = ProcessingOrderAdapter(ArrayList())
    var allCartItems = ArrayList<Food>()

    init {
        state = MutableLiveData()
    }

    fun getCarts() {
        state!!.postValue(ViewModelState.loading())
        val listFilters = ArrayList<String>()
        listFilters.add(STATUS_PENDING)
        listFilters.add(STATUS_PROCESSING)
        database!!.collection(TABLE_CART)
            .whereEqualTo(COLUMN_IS_ACTIVE, true)
            .whereIn(COLUMN_STATUS, listFilters)
            .addSnapshotListener { documents, error ->
                var catList = ArrayList<Cart>()
                var docs = documents!!.documents
                if (docs.isNotEmpty()) {
                    for (d in docs) {
                        val category =
                            Cart(
                                d.getString(COLUMN_CART_ID),
                                d.getLong(COLUMN_DATE),
                                d.getBoolean(
                                    COLUMN_IS_ACTIVE
                                ),
                                d.getString(COLUMN_PHONE),
                                d.getString(
                                    COLUMN_STATUS
                                ),
                                d.getString(COLUMN_TABLE_NUMBER),
                                d.getString(COLUMN_CHEF),
                                d.getString(
                                    COLUMN_CHEF_NAME
                                )
                            )
                        catList.add(category)
                    }

                }
//                displayData(catList)
                val cartIdList = catList.map { it.cartID }
                getItems(catList, cartIdList)
            }
    }

    private fun getItems(cartList: List<Cart>, cartIdList: List<String?>) {
        if(cartIdList.isNotEmpty()) {
            database!!.collection(TABLE_CART_ITEMS)
                .whereIn(COLUMN_CART_ID, cartIdList)
                .addSnapshotListener { documents, error ->
                    var docs = documents!!.documents
                    allCartItems.clear()
                    if (docs.isNotEmpty()) {
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
                            allCartItems.add(f)
                        }
                        val itemsGroup = allCartItems.groupBy { it.cartId }

                        for (ig in itemsGroup) {
                            val cart = cartList.find { it.cartID == ig.key }
                            if (cart != null)
                                cart.items = ig.value
                        }

                    }
                    displayData(cartList)
                }
        }else{
            displayData(cartList)
        }
    }

    private fun displayData(response: List<Cart>) {

        val listPending = response.filter { it.status == STATUS_PENDING  && it.items!!.isNotEmpty()}
        val listProcessing = response.filter { it.status == STATUS_PROCESSING }

        displayPending(listPending)
        displayProcessing(listProcessing)

        state!!.postValue(ViewModelState.success())
    }

    private fun displayPending(list: List<Cart>) {
        if (list.isNullOrEmpty()) {
            pendingAdapter.setItems(ArrayList())
            pendingAdapter.notifyDataSetChanged()
        } else {
            pendingAdapter.setItems(list)
            pendingAdapter.notifyDataSetChanged()
        }
    }

    private fun displayProcessing(list: List<Cart>) {
        if (list.isNullOrEmpty()) {
            processingAdapter.setItems(ArrayList())
            processingAdapter.notifyDataSetChanged()
        } else {
            processingAdapter.setItems(list)
            processingAdapter.notifyDataSetChanged()
        }
    }

    fun checkTheChef(pin: String, item: Cart) {
        state!!.postValue(ViewModelState.loading())
        database!!.collection(TABLE_ADMIN_USERS)
            .whereEqualTo(COLUMN_ROLE, ROLE_CHEF)
            .whereEqualTo(COLUMN_PIN, pin)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.documents.isNullOrEmpty()) {
                    updateStatus(
                        documents.documents[0].getString(COLUMN_EMP_ID)!!,
                        documents.documents[0].getString(
                            COLUMN_NAME
                        )!!,
                        item
                    )
                } else {
                    state!!.postValue(ViewModelState.validation_issue())
                }
            }
            .addOnFailureListener {
                state!!.postValue(ViewModelState.validation_issue())
            }
    }

    private fun updateStatus(chefId: String, chefName: String, item: Cart) {
        database!!.collection(TABLE_CART)
            .whereEqualTo(COLUMN_CART_ID, item.cartID)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.documents.isNullOrEmpty()) {
                    database!!.collection(TABLE_CART)
                        .document(documents.documents[0].id)
                        .update(
                            mapOf(
                                COLUMN_STATUS to STATUS_PROCESSING,
                                COLUMN_CHEF to chefId,
                                COLUMN_CHEF_NAME to chefName
                            )
                        ).addOnSuccessListener {
                            state!!.postValue(ViewModelState.sub_success1())
                        }
                } else {
                    state!!.postValue(ViewModelState.error())
                }
            }
            .addOnFailureListener {
                state!!.postValue(ViewModelState.error())
            }

    }

    fun updateStatus(item: Cart) {
        database!!.collection(TABLE_CART)
            .whereEqualTo(COLUMN_CART_ID, item.cartID)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.documents.isNullOrEmpty()) {
                    database!!.collection(TABLE_CART)
                        .document(documents.documents[0].id)
                        .update(
                            mapOf(
                                COLUMN_STATUS to STATUS_SERVED
                            )
                        ).addOnSuccessListener {
                            state!!.postValue(ViewModelState.sub_success1())
                        }
                } else {
                    state!!.postValue(ViewModelState.error())
                }
            }
            .addOnFailureListener {
                state!!.postValue(ViewModelState.error())
            }
    }

    fun checkChefForTheCart(pin: String, item: Cart){
        state!!.postValue(ViewModelState.loading())
        database!!.collection(TABLE_ADMIN_USERS)
            .whereEqualTo(COLUMN_EMP_ID, item.chef)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.documents.isNullOrEmpty()) {
                   if(documents.documents[0].getString(COLUMN_PIN) == pin){
                       updateStatus(item)
                   }else{
                       state!!.postValue(ViewModelState.validation_issue())
                   }
                } else {
                    state!!.postValue(ViewModelState.validation_issue())
                }
            }
            .addOnFailureListener {
                state!!.postValue(ViewModelState.error())
            }
    }

}