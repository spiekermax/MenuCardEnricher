package de.unihannover.hci.menudetector.models.state

// Internal dependencies
import de.unihannover.hci.menudetector.models.Menu
import de.unihannover.hci.menudetector.models.Order


data class MainActivityState(
    val menu: Menu? = null,
    val order: Order? = null,
)