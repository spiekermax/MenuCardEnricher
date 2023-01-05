package de.unihannover.hci.menudetector.models.recognition

// Kotlin
import kotlinx.parcelize.Parcelize

// Android
import android.os.Parcelable


@Parcelize
data class MenuRecognitionResult(
    val language: String? = null,
    val dishes: List<DishRecognitionResult> = listOf(),
): Parcelable {

    /* UTILITY */

    fun isEmpty(): Boolean = dishes.isEmpty()

}