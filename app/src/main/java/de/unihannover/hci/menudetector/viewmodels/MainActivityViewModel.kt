package de.unihannover.hci.menudetector.viewmodels

// Android
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

// Internal dependencies
import de.unihannover.hci.menudetector.models.MainActivityState


class MainActivityViewModel : ViewModel() {
    val state: MainActivityState = MainActivityState()

    /**
     * This is just an example to demonstrate how view models work
     * and should not be used in the final app.
     */
    private val exampleCounter: MutableLiveData<Int> = MutableLiveData(0)
    val exampleCounterChanges: LiveData<Int> = exampleCounter

    fun incrementExampleCounter() {
        exampleCounter.value = exampleCounter.value?.inc()
    }
}