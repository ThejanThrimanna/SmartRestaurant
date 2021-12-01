package com.thejan.proj.restaurant.tv.android.model

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
    var chef: String? = "",
    var chefName: String? = "",
    var items: List<Food>? = ArrayList()

) : Parcelable