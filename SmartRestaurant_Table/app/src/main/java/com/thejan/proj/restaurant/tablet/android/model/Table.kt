package com.thejan.proj.restaurant.tablet.android.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Table(
    var deviceId: String? = "",
    var tableNumber: String? = "",
    var numberOfSeats: Int? = 0,
    var booking: String? = "",
) : Parcelable

@Parcelize
data class TableReservation(
    var date: Long? = 0L,
    var isActive: Boolean? = false,
    var name: String? = "",
    var phone: String? = "",
    var tableNumber: String? = "",
    var numberOfSeats: Int? = 0
) : Parcelable