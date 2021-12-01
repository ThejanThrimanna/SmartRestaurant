package com.thejan.proj.restaurant.admin.android.viewmodel

import androidx.lifecycle.MutableLiveData
import com.thejan.proj.restaurant.admin.android.helper.*
import com.thejan.proj.restaurant.admin.android.model.Cart
import com.thejan.proj.restaurant.admin.android.view.adapter.*

class HomeViewModel : BaseViewModel() {
    var placeOrderAdapter = OrdersAdapter(ArrayList())
    var pendingAdapter = PendingOrderAdapter(ArrayList())
    var processingAdapter = ProcessingOrderAdapter(ArrayList())
    var servedAdapter = ServedOrderAdapter(ArrayList())
    var settleAdapter = SettleTheBillAdapter(ArrayList())

    init {
        state = MutableLiveData()
    }

    fun getCarts() {
        state!!.postValue(ViewModelState.loading())
        database!!.collection(TABLE_CART)
            .whereEqualTo(COLUMN_IS_ACTIVE, true)
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
                                d.getString(COLUMN_NAME),
                                d.getString(
                                    COLUMN_CHEF
                                ),
                                d.getDouble(COLUMN_AMOUNT),
                                d.getString(COLUMN_CHEF),
                                d.getDouble(COLUMN_DISCOUNT)
                            )

                        catList.add(category)
                    }

                }
                displayData(catList)
            }
    }

    private fun displayData(response: ArrayList<Cart>) {

        val listPlaceOrder = response.filter { it.status == STATUS_PLACE_AN_ORDER }
        val listPending = response.filter { it.status == STATUS_PENDING }
        val listProcessing = response.filter { it.status == STATUS_PROCESSING }
        val listServed = response.filter { it.status == STATUS_SERVED }
        val listSettle = response.filter { it.status == STATUS_BILL_SETTLEMENT_PENDING }

        displayPlaceOrder(listPlaceOrder)
        displayPending(listPending)
        displayProcessing(listProcessing)
        displayServed(listServed)
        displaySettleBill(listSettle)

        state!!.postValue(ViewModelState.success())
    }

    private fun displayPlaceOrder(list: List<Cart>) {
        if (list.isNullOrEmpty()) {
            placeOrderAdapter.setItems(ArrayList())
            placeOrderAdapter.notifyDataSetChanged()
        } else {
            placeOrderAdapter.setItems(list)
        }
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

    private fun displayServed(list: List<Cart>) {
        if (list.isNullOrEmpty()) {
            servedAdapter.setItems(ArrayList())
            servedAdapter.notifyDataSetChanged()
        } else {
            servedAdapter.setItems(list)
            servedAdapter.notifyDataSetChanged()
        }
    }

    private fun displaySettleBill(list: List<Cart>) {
        if (list.isNullOrEmpty()) {
            settleAdapter.setItems(ArrayList())
            settleAdapter.notifyDataSetChanged()
        } else {
            settleAdapter.setItems(list)
            settleAdapter.notifyDataSetChanged()
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
                                COLUMN_STATUS to STATUS_SETTLED
                            )
                        ).addOnSuccessListener {
                            database!!.collection(TABLE_TABLE_RESERVATION)
                                .whereEqualTo(COLUMN_PHONE, item.phone).get()
                                .addOnSuccessListener { document ->
                                    if (document.documents.isNotEmpty()) {
                                        database!!.collection(TABLE_TABLE_RESERVATION)
                                            .document(document.documents[0].id)
                                            .delete().addOnSuccessListener {
                                                database!!.collection(TABLE_TABLES)
                                                    .whereEqualTo(
                                                        COLUMN_TABLE_NUMBER,
                                                        item.tableNumber
                                                    )
                                                    .get()
                                                    .addOnSuccessListener {
                                                        if (it.documents.isNotEmpty()) {
                                                            database!!.collection(TABLE_TABLES)
                                                                .document(it.documents[0].id)
                                                                .update(
                                                                    mapOf(
                                                                        COLUMN_BOOKING to ""
                                                                    )
                                                                )
                                                                .addOnSuccessListener {
                                                                    state!!.postValue(ViewModelState.sub_success1())
                                                                }
                                                        }
                                                    }
                                            }
                                    } else {
                                        state!!.postValue(ViewModelState.success())
                                    }
                                }
                        }
                } else {
                    state!!.postValue(ViewModelState.error())
                }
            }
            .addOnFailureListener {
                state!!.postValue(ViewModelState.error())
            }

    }
}