package com.thejan.proj.restaurant.admin.android.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class AdminUser(
    var name: String? = "",
    var role: String? = "",
    var emp_id: String? = "",
    var password: String? = "",
    var pin: String? = ""
):Parcelable

@Parcelize
data class Performance(
    var name: String? = "",
    var emp_id: String? = "",
    var numberOfOrders: Int? = 0,
    var value: Double? = 0.0
):Parcelable