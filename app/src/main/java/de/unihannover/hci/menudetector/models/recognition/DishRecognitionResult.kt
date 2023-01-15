package de.unihannover.hci.menudetector.models.recognition

// Kotlin
import kotlinx.parcelize.Parcelize

// Android
import android.graphics.Rect
import android.os.Parcelable


@Parcelize
data class DishRecognitionResult(
    val name: String,
    val price: Double,
    val currency: String,
    val language: String?,
    val boundingBox: Rect,
    val confidence: Float,
) : Parcelable