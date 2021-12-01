package com.thejan.proj.restaurant.admin.android.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Created by Thejan Thrimanna on 8/27/21.
 */

@Parcelize
data class AdminModel(
    var itemName: String? = "",
    var imgid: Int? = 0
) : Parcelable