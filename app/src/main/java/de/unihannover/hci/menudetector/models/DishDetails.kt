package de.unihannover.hci.menudetector.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import android.graphics.Bitmap


@Parcelize
data class DishDetails(
    val description: String? = null,
    val bitmap: Bitmap? = null,
    val weight: Double? = null,
) : Parcelable