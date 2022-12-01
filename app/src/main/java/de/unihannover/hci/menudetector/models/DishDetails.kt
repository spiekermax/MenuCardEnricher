package de.unihannover.hci.menudetector.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class DishDetails(
    val description: String? = null,
    val imageUrl: String? = null,
    val weight: Double? = null,
) : Parcelable