package com.thejan.proj.restaurant.admin.android.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.thejan.proj.restaurant.admin.android.helper.*
import com.thejan.proj.restaurant.admin.android.model.Cart
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by Thejan Thrimanna on 9/11/21.
 */
class ReportViewModel : BaseViewModel() {
    val todaySalesAmount: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }
    val weekSalesAmount: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }
    val monthSalesAmount: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }


    init {
        state = MutableLiveData()
    }

    fun getCartDetails() {
        val monthDateAndTime = Calendar.getInstance(Locale("en", "UK"))
        monthDateAndTime.set(Calendar.HOUR_OF_DAY, 0)
        monthDateAndTime.clear(Calendar.MINUTE)
        monthDateAndTime.clear(Calendar.SECOND)
        monthDateAndTime.clear(Calendar.MILLISECOND)

        monthDateAndTime.set(Calendar.DAY_OF_MONTH, 1)

        state!!.postValue(ViewModelState.loading())
        database!!.collection(TABLE_CART)
            .whereEqualTo(COLUMN_STATUS, STATUS_SETTLED)
            .whereGreaterThan(COLUMN_DATE, monthDateAndTime.timeInMillis)
            .addSnapshotListener { documents, error ->
                if (error == null) {
                    if (documents != null && documents.isEmpty) {
                        state!!.postValue(ViewModelState.error())
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
                                discount = doc.getDouble(COLUMN_DISCOUNT)
                            )
                            saleList.add(table)
                        }

                        val todayDateAndTime = Calendar.getInstance(Locale("en", "UK"))
                        todayDateAndTime.set(Calendar.HOUR_OF_DAY, 0)
                        todayDateAndTime.set(Calendar.MINUTE, 0)
                        todayDateAndTime.set(Calendar.SECOND, 0)
                        todayDateAndTime.set(Calendar.MILLISECOND, 0)
                        val todayList =
                            saleList.filter { it.date!! > todayDateAndTime.timeInMillis }
                        val todaySum = todayList.sumByDouble { (it.amount!! - it.discount!!) }
                        todaySalesAmount.postValue(String.format("%.2f", todaySum))

                        val weekDateAndTime = Calendar.getInstance(Locale("en", "UK"))
                        weekDateAndTime.set(Calendar.DAY_OF_WEEK, weekDateAndTime.firstDayOfWeek)
                        weekDateAndTime.set(Calendar.HOUR_OF_DAY, 0)
                        weekDateAndTime.clear(Calendar.MINUTE)
                        weekDateAndTime.clear(Calendar.SECOND)
                        weekDateAndTime.clear(Calendar.MILLISECOND)


                        val weekList =
                            saleList.filter { it.date!! > weekDateAndTime.timeInMillis }
                        val weekSum = weekList.sumByDouble { (it.amount!! - it.discount!!) }

                        weekSalesAmount.postValue(String.format("%.2f", weekSum))

                        val monthSum = saleList.sumByDouble { (it.amount!! - it.discount!!)}
                        monthSalesAmount.postValue(String.format("%.2f", monthSum))

                        state!!.postValue(ViewModelState.success())
                    }
                } else {
                    message.postValue(error.toString())
                    Log.e("Error ", error.toString())
                    state!!.postValue(ViewModelState.error())
                }
            }

    }
}