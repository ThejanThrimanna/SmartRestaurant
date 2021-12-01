package com.thejan.proj.restaurant.admin.android.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Table(
    var deviceId: String? = "",
    var tableNumber: String? = "",
    var numberOfSeats: Int? = 0,
    var booking: String? = "",
) : Parcelable