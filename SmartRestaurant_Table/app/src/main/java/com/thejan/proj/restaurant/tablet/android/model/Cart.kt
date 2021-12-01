package com.thejan.proj.restaurant.tablet.android.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Created by Thejan Thrimanna on 9/17/21.
 */
@Parcelize
data class Cart(
    var amout: Double? = 0.0,
    var cartId: String? = "",
    var chef: String? = "",
    var chef_name: String? = "",
    var date: Long? = 0L,
    var isActive: Boolean? = false,
    var name: String? = "",
    var phone: String? = "",
    var status: String? = "",
    var tableNumber: String? = "",
    var offerId: String? = "",
    var perecentage: Int? = 0,
    var offerName: String? = "",
    var discount: Double? = 0.0,
    var isOffer: Boolean? = false
) : Parcelable
