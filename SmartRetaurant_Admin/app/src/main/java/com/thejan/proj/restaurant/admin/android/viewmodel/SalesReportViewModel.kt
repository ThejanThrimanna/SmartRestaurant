package com.thejan.proj.restaurant.admin.android.viewmodel

import androidx.lifecycle.MutableLiveData
import com.thejan.proj.restaurant.admin.android.helper.*
import com.thejan.proj.restaurant.admin.android.model.Cart
import com.thejan.proj.restaurant.admin.android.model.Food
import com.thejan.proj.restaurant.admin.android.view.adapter.SalesReportAdapter
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by Thejan Thrimanna on 8/18/21.
 */
class SalesReportViewModel : BaseViewModel() {

    var itemAdapter = SalesReportAdapter(ArrayList())
    var cartItemList = ArrayList<Food>()

    val revenue: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }
    val cost: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }
    val profit: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    init {
        state = MutableLiveData()
    }

    fun getCartDetailsToday() {
        cartItemList = ArrayList()
        val todayDateAndTime = Calendar.getInstance(Locale("en", "UK"))
        todayDateAndTime.set(Calendar.HOUR_OF_DAY, 0)
        todayDateAndTime.set(Calendar.MINUTE, 0)
        todayDateAndTime.set(Calendar.SECOND, 0)
        todayDateAndTime.set(Calendar.MILLISECOND, 0)

        getDetails(todayDateAndTime)
    }

    fun getCartDetailsThisWeek() {
        cartItemList = ArrayList()
        val weekDateAndTime = Calendar.getInstance(Locale("en", "UK"))
        weekDateAndTime.set(Calendar.DAY_OF_WEEK, weekDateAndTime.firstDayOfWeek)
        weekDateAndTime.set(Calendar.HOUR_OF_DAY, 0)
        weekDateAndTime.clear(Calendar.MINUTE)
        weekDateAndTime.clear(Calendar.SECOND)
        weekDateAndTime.clear(Calendar.MILLISECOND)
        getDetails(weekDateAndTime)
    }

    fun getCartDetailsThisMonth() {
        cartItemList = ArrayList()
        val monthDateAndTime = Calendar.getInstance(Locale("en", "UK"))
        monthDateAndTime.set(Calendar.HOUR_OF_DAY, 0)
        monthDateAndTime.clear(Calendar.MINUTE)
        monthDateAndTime.clear(Calendar.SECOND)
        monthDateAndTime.clear(Calendar.MILLISECOND)
        monthDateAndTime.set(Calendar.DAY_OF_MONTH, 1)
        getDetails(monthDateAndTime)
    }

    fun getCartDetailsThisYear() {
        cartItemList = ArrayList()
        val yearDateAndTime = Calendar.getInstance(Locale("en", "UK"))
        yearDateAndTime.set(Calendar.HOUR_OF_DAY, 0)
        yearDateAndTime.clear(Calendar.MINUTE)
        yearDateAndTime.clear(Calendar.SECOND)
        yearDateAndTime.clear(Calendar.MILLISECOND)
        yearDateAndTime.set(Calendar.DAY_OF_MONTH, 1)
        yearDateAndTime.set(Calendar.MONTH, 0)
        getDetails(yearDateAndTime)
    }

    private fun getDetails(calendar: Calendar) {
        state!!.postValue(ViewModelState.loading())
        database!!.collection(TABLE_CART)
            .whereEqualTo(COLUMN_STATUS, STATUS_SETTLED)
            .whereGreaterThan(COLUMN_DATE, calendar.timeInMillis)
            .get()
            .addOnSuccessListener { documents ->

                if (documents != null && documents.isEmpty) {
                    state!!.postValue(ViewModelState.error())
                    displayItemData(ArrayList())
                    getSum(ArrayList())
                } else {
                    val saleList = ArrayList<Cart>()
                    for (doc in documents!!.documents) {
                        val table = Cart(
                            doc.getString(COLUMN_CART_ID),
                            doc.getLong(COLUMN_DATE),
                            doc.getBoolean(COLUMN_IS_ACTIVE),
                            doc.getString(COLUMN_PHONE),
                            doc.getString(COLUMN_STATUS),
                            doc.getString(COLUMN_TABLE_NUMBER),
                            doc.getString(COLUMN_NAME),
                            doc.getString(COLUMN_CHEF),
                            doc.getDouble(COLUMN_AMOUNT),
                            percentage = doc.getLong(COLUMN_PERCENTAGE)!!.toInt()
                        )
                        println("Percent " + doc.getLong(COLUMN_PERCENTAGE)!!.toInt())
                        saleList.add(table)
                    }
                    var cartIds = saleList.map { it.cartID }

                    var subList: ArrayList<List<String?>> = ArrayList()
                        var i = 0
                        while (i < cartIds.size) {
                            subList.add(
                                cartIds.subList(
                                    i,
                                    if (i + 10 > cartIds.size) cartIds.size else i + 10
                                )
                            )
                            i += 10
                        }

                    var x = 0
                    subList.forEachIndexed { index, list ->

                        database!!.collection(TABLE_CART_ITEMS)
                            .whereIn(COLUMN_CART_ID, subList[index])
                            .get()
                            .addOnSuccessListener { dd ->
                                for (ds in dd.documents) {
                                    val cartItem = Food(
                                        ds.getString(COLUMN_CART_ITEM_ID),
                                        ds.getString(COLUMN_FOOD_ID),
                                        ds.getString(COLUMN_CATEGORY),
                                        ds.getDouble(COLUMN_COST),
                                        ds.getString(COLUMN_DESC),
                                        ds.getString(COLUMN_IMAGE),
                                        ds.getString(COLUMN_NAME),
                                        ds.getDouble(COLUMN_PRICE),
                                        ds.getString(COLUMN_TYPE),
                                        ds.getBoolean(COLUMN_ADDED)!!,
                                        ds.getLong(COLUMN_COUNT)!!.toInt(),
                                        ds.getString(COLUMN_CART_ID)!!
                                    )
                                    cartItemList.add(cartItem)
                                }
                                ++x
                                if (x >= subList.size) processLimitedDocumentList(saleList)
                            }
                    }
                }

            }
    }


    private fun processLimitedDocumentList(saleList: ArrayList<Cart>) {
        for (cartItem in cartItemList) {
            val c =
                saleList.find { it.cartID == cartItem.cartId }
            val cost = cartItem.price
            val count = cartItem.count
            val persent = c!!.percentage!! / 100.toDouble()
            var discount = count * cost!! * persent
            cartItem.discount = discount
        }

        for (cart in saleList) {
            val selectedCarts = cartItemList.filter { it.cartId == cart.cartID }
            for (s in selectedCarts) {
                s.date = cart.date!!
            }
        }

        displayItemData(cartItemList)
        getSum(cartItemList)
    }

    private fun displayItemData(response: ArrayList<Food>) {
        var sortedList = response.sortedBy { it -> it.date }
        if (sortedList.isNullOrEmpty()) {
            setItemListIsEmpty()
        } else {
            state!!.postValue(ViewModelState.success())
            itemAdapter.setItems(sortedList)
            itemAdapter.notifyDataSetChanged()
        }
    }

    private fun setItemListIsEmpty() {
        state!!.postValue(ViewModelState.list_empty())
        itemAdapter.setItems(ArrayList())
        itemAdapter.notifyDataSetChanged()
    }

    private fun getSum(list: ArrayList<Food>) {
        val revenue = list.sumByDouble { (it.price!! * it.count) - (it.discount * it.count) }
        val cost = list.sumByDouble { it.cost!! * it.count }
        val profit = revenue - cost

        this.revenue.postValue(String.format("%.2f", revenue))
        this.profit.postValue(String.format("%.2f", profit))
        this.cost.postValue(String.format("%.2f", cost))
    }
}