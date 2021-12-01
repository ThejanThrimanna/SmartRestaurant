package com.thejan.proj.restaurant.admin.android.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Cart(
    var cartID: String? = "",
    var date: Long? = 0L,
    var isActive: Boolean? = false,
    var phone: String? = "",
    var status: String? = "",
    var tableNumber: String? = "",
    var name: String? = "",
    var chef: String? = "",
    var amount: Double? = 0.0,
    var chefName: String? = "",
    var discount: Double? = 0.0,
    var percentage: Int? = 0
) : Parcelable