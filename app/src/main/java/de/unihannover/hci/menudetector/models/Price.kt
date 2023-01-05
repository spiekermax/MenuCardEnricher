package de.unihannover.hci.menudetector.models

// Java
import java.math.BigDecimal
import java.util.Currency


data class Price(
    val value: BigDecimal,
    val currency: Currency,
)