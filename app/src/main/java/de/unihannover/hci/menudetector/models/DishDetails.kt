package de.unihannover.hci.menudetector.models

import android.graphics.Bitmap


data class DishDetails(
    val description: String? = null,
    val bitmap: Bitmap? = null,
    val weight: Double? = null,
)