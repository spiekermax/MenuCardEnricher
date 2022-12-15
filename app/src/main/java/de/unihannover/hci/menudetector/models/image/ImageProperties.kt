package de.unihannover.hci.menudetector.models.image


data class ImageProperties(
    val width: Int,
    val height: Int,
    val rotation: Int,
    val isMirrored: Boolean,
)