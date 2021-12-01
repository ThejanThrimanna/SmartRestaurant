package com.thejan.proj.restaurant.tv.smartrestaurant_mobile.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Created by Thejan Thrimanna on 9/23/21.
 */
@Parcelize
data class Food(
    var cartItemId: String? = "",
    var foodId: String? = "",
    var cat: String? = "",
    var cost: Double? = 0.0,
    var desc: String? = "",
    var image: String? = "",
    var name: String? = "",
    var price: Double? = 0.0,
    var type: String? = "",
    var isAdded: Boolean = false,
    var count: Int = 1,
    var cartId: String = ""
) : Parcelable