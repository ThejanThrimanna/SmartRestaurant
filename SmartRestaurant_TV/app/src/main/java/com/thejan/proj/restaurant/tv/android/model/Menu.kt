package com.thejan.proj.restaurant.tv.android.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Category(
    var catID: String? = "",
    var name: String? = "",
    var image: String? = ""
) : Parcelable

@Parcelize
data class Type(
    var typeId: String? = "",
    var name: String? = ""
) : Parcelable

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
