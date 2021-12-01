package com.thejan.proj.restaurant.admin.android.viewmodel

import androidx.lifecycle.MutableLiveData
import com.thejan.proj.restaurant.admin.android.helper.*
import com.thejan.proj.restaurant.admin.android.model.Cart
import com.thejan.proj.restaurant.admin.android.model.CatReport
import com.thejan.proj.restaurant.admin.android.model.Category
import com.thejan.proj.restaurant.admin.android.model.Food
import com.thejan.proj.restaurant.admin.android.view.adapter.CategoryReportAdapter
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by Thejan Thrimanna on 8/19/21.
 */
class CategoryReportViewModel : BaseViewModel() {
    var itemAdapter = CategoryReportAdapter(ArrayList())
    var cartItemList = ArrayList<Food>()

    init {
        state = MutableLiveData()
    }

    fun getCartDetailsToday() {
        cartItemList = ArrayList<Food>()
        val todayDateAndTime = Calendar.getInstance(Locale("en", "UK"))
        todayDateAndTime.set(Calendar.HOUR_OF_DAY, 0)
        todayDateAndTime.set(Calendar.MINUTE, 0)
        todayDateAndTime.set(Calendar.SECOND, 0)
        todayDateAndTime.set(Calendar.MILLISECOND, 0)

        getDetails(todayDateAndTime)
    }

    fun getCartDetailsThisWeek() {
        cartItemList = ArrayList<Food>()
        val weekDateAndTime = Calendar.getInstance(Locale("en", "UK"))
        weekDateAndTime.set(Calendar.DAY_OF_WEEK, weekDateAndTime.firstDayOfWeek)
        weekDateAndTime.set(Calendar.HOUR_OF_DAY, 0)
        weekDateAndTime.clear(Calendar.MINUTE)
        weekDateAndTime.clear(Calendar.SECOND)
        weekDateAndTime.clear(Calendar.MILLISECOND)
        getDetails(weekDateAndTime)
    }

    fun getCartDetailsThisMonth() {
        cartItemList = ArrayList<Food>()
        val monthDateAndTime = Calendar.getInstance(Locale("en", "UK"))
        monthDateAndTime.set(Calendar.HOUR_OF_DAY, 0)
        monthDateAndTime.clear(Calendar.MINUTE)
        monthDateAndTime.clear(Calendar.SECOND)
        monthDateAndTime.clear(Calendar.MILLISECOND)
        monthDateAndTime.set(Calendar.DAY_OF_MONTH, 1)
        getDetails(monthDateAndTime)
    }

    fun getCartDetailsThisYear() {
        cartItemList = ArrayList<Food>()
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
                            doc.getDouble(COLUMN_AMOUNT)
                        )
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
                                for (doc in dd.documents) {
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
                                    cartItemList.add(cartItem)
                                }
                                ++x
                                if (x >= subList.size) {
                                    val groupByCat = cartItemList.groupBy { it.cat }
                                    val catList = ArrayList<Category>()
                                    val summaryList = ArrayList<CatReport>()
                                    database!!.collection(TABLE_FOOD_CATEGORY)
                                        .get()
                                        .addOnSuccessListener { docs ->
                                            for (doc in docs.documents) {
                                                val catItem = Category(
                                                    doc.getString(COLUMN_ID),
                                                    doc.getString(COLUMN_NAME),
                                                    doc.getString(COLUMN_IMAGE)
                                                )
                                                catList.add(catItem)
                                            }

                                            for (gr in groupByCat) {
                                                for (cat in catList) {
                                                    if (gr.key == cat.catID) {
                                                        val catReport = CatReport(
                                                            cat.catID,
                                                            cat.name!!,
                                                            gr.value.size,
                                                            gr.value.sumByDouble { it.price!! * it.count }
                                                        )
                                                        summaryList.add(catReport)
                                                    }
                                                }
                                            }
                                            displayItemData(summaryList)
                                        }
                                }
                            }
                    }
                }
            }
    }

    private fun displayItemData(response: ArrayList<CatReport>) {
        var sortedList = response.sortedByDescending { it -> it.value }
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
}