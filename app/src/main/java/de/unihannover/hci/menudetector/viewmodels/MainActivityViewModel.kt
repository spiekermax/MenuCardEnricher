package de.unihannover.hci.menudetector.viewmodels

// Android
import androidx.lifecycle.ViewModel

// Internal dependencies
import de.unihannover.hci.menudetector.models.state.MainActivityState


class MainActivityViewModel : ViewModel() {
    val state: MainActivityState = MainActivityState()
}