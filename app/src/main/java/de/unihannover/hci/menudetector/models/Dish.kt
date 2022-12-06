package de.unihannover.hci.menudetector.models

// Android
import android.os.Parcelable
import androidx.annotation.IntRange
import kotlinx.parcelize.Parcelize

@Parcelize
data class Dish(
    val id: String,
    val name: String,
    val price: Double,

    @IntRange(from = 0)
    val quantity: Int,

    val details: DishDetails? = null,
) : Parcelable
