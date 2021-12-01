package com.thejan.proj.restaurant.admin.android.viewmodel

import androidx.lifecycle.MutableLiveData
import com.thejan.proj.restaurant.admin.android.helper.*
import com.thejan.proj.restaurant.admin.android.model.AdminUser
import com.thejan.proj.restaurant.admin.android.model.Cart
import com.thejan.proj.restaurant.admin.android.model.Performance
import com.thejan.proj.restaurant.admin.android.view.adapter.PerformanceReportAdapter
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by Thejan Thrimanna on 8/19/21.
 */
class PerformanceReportViewModel : BaseViewModel() {

    var itemAdapter = PerformanceReportAdapter(ArrayList())
    var adminUser = ArrayList<AdminUser>()
    init {
        state = MutableLiveData()
    }

    fun getCartDetailsToday() {
        adminUser = ArrayList<AdminUser>()
        val todayDateAndTime = Calendar.getInstance(Locale("en", "UK"))
        todayDateAndTime.set(Calendar.HOUR_OF_DAY, 0)
        todayDateAndTime.set(Calendar.MINUTE, 0)
        todayDateAndTime.set(Calendar.SECOND, 0)
        todayDateAndTime.set(Calendar.MILLISECOND, 0)

        getDetails(todayDateAndTime)
    }

    fun getCartDetailsThisWeek() {
        adminUser = ArrayList<AdminUser>()
        val weekDateAndTime = Calendar.getInstance(Locale("en", "UK"))
        weekDateAndTime.set(Calendar.DAY_OF_WEEK, weekDateAndTime.firstDayOfWeek)
        weekDateAndTime.set(Calendar.HOUR_OF_DAY, 0)
        weekDateAndTime.clear(Calendar.MINUTE)
        weekDateAndTime.clear(Calendar.SECOND)
        weekDateAndTime.clear(Calendar.MILLISECOND)
        getDetails(weekDateAndTime)
    }

    fun getCartDetailsThisMonth() {
        adminUser = ArrayList<AdminUser>()
        val monthDateAndTime = Calendar.getInstance(Locale("en", "UK"))
        monthDateAndTime.set(Calendar.HOUR_OF_DAY, 0)
        monthDateAndTime.clear(Calendar.MINUTE)
        monthDateAndTime.clear(Calendar.SECOND)
        monthDateAndTime.clear(Calendar.MILLISECOND)
        monthDateAndTime.set(Calendar.DAY_OF_MONTH, 1)
        getDetails(monthDateAndTime)
    }

    fun getCartDetailsThisYear() {
        adminUser = ArrayList<AdminUser>()
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
                    nonContributedEmpCheck(ArrayList(), ArrayList())
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
                            doc.getString(COLUMN_CHEF_NAME)
                        )
                        saleList.add(table)
                    }

                    val groupChef = saleList.groupBy { it.chef }
                    var chefIds = saleList.map { it.chef }
                    var uniqeIds = chefIds.distinctBy { it }
                    val listPerformance = ArrayList<Performance>()

                    for (i in groupChef) {
                        val perf = Performance()
                        perf.name = i.value[0].chefName
                        perf.emp_id = i.value[0].chef
                        perf.numberOfOrders = i.value.size
                        perf.value = i.value.sumByDouble { it.amount!! }
                        listPerformance.add(perf)
                    }
                    nonContributedEmpCheck(uniqeIds, listPerformance)
                }
            }
    }

    private fun nonContributedEmpCheck(
        chefIds: List<String?>,
        listPerformance: ArrayList<Performance>
    ) {
        if(chefIds.isNotEmpty()) {
            var subList: ArrayList<List<String?>> = ArrayList()
            var i = 0
            while (i < chefIds.size) {
                subList.add(
                    chefIds.subList(
                        i,
                        if (i + 10 > chefIds.size) chefIds.size else i + 10
                    )
                )
                i += 10
            }

            var x = 0
            subList.forEachIndexed { index, list ->
                database!!.collection(TABLE_ADMIN_USERS)
                    .whereEqualTo(COLUMN_USER_ROLE, ROLE_CHEF)
                    .whereNotIn(COLUMN_EMP_ID, subList[index])
                    .get()
                    .addOnSuccessListener { documents ->
                        for (doc in documents!!.documents) {
                            val user = AdminUser(
                                doc.getString(COLUMN_NAME),
                                doc.getString(COLUMN_USER_ROLE),
                                doc.getString(COLUMN_EMP_ID)
                            )
                            adminUser.add(user)
                        }
                        ++x
                        if (x >= subList.size) {
                            for (ad in adminUser) {
                                val perf = Performance()
                                perf.name = ad.name
                                perf.emp_id = ad.emp_id
                                perf.numberOfOrders = 0
                                perf.value = 0.0
                                listPerformance.add(perf)
                            }
                            displayItemData(listPerformance)
                        }

                    }
            }
        }else{
            database!!.collection(TABLE_ADMIN_USERS)
                .whereEqualTo(COLUMN_USER_ROLE, ROLE_CHEF)
                .get()
                .addOnSuccessListener { documents ->
                    val adminUser = ArrayList<AdminUser>()
                    for (doc in documents!!.documents) {
                        val user = AdminUser(
                            doc.getString(COLUMN_NAME),
                            doc.getString(COLUMN_USER_ROLE),
                            doc.getString(COLUMN_EMP_ID)
                        )
                        adminUser.add(user)
                    }

                    for (i in adminUser) {
                        val perf = Performance()
                        perf.name = i.name
                        perf.emp_id = i.emp_id
                        perf.numberOfOrders = 0
                        perf.value = 0.0
                        listPerformance.add(perf)
                    }
                    displayItemData(listPerformance)
                }
        }
    }

    private fun displayItemData(response: ArrayList<Performance>) {
        var sortedList = response.sortedByDescending { it -> it.value }
        if (response.isNullOrEmpty()) {
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