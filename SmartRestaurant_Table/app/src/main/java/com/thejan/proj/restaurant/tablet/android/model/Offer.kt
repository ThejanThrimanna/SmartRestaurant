package com.thejan.proj.restaurant.tablet.android.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Created by Thejan Thrimanna on 9/24/21.
 */

@Parcelize
data class Offer(
    var offerId: String? = "",
    var name: String? = "",
    var presentage: Int? = 0,
    var startDate: Long? = 0L,
    var endDate: Long? = 0L,
    var isActive: Boolean? = true,
) : Parcelable