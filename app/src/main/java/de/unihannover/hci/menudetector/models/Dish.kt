package de.unihannover.hci.menudetector.models

// Android
import android.os.Parcelable
import androidx.annotation.FloatRange
import androidx.annotation.IntRange

// Kotlin
import kotlinx.parcelize.Parcelize


@Parcelize
data class Dish(
    val id: String,

    val name: String,
    val originalName: String,

    val currency: String,
    val language: String?,

    @FloatRange(from = 0.0)
    val price: Double,

    @IntRange(from = 0)
    val quantity: Int,

    val details: DishDetails? = null,
) : Parcelable