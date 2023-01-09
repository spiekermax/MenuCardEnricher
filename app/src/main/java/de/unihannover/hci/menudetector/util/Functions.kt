package de.unihannover.hci.menudetector.util

// Android
import android.content.Context
import android.preference.PreferenceManager
import androidx.camera.core.impl.utils.ContextUtil.getApplicationContext
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import de.unihannover.hci.menudetector.models.Price
import java.lang.ref.WeakReference


@Suppress("UNCHECKED_CAST")
fun <A, B> zip(first: LiveData<A>, second: LiveData<B>): LiveData<Pair<A, B>> {
    val mediatorLiveData = MediatorLiveData<Pair<A, B>>()

    var isFirstEmitted = false
    var isSecondEmitted = false
    var firstValue: A? = null
    var secondValue: B? = null

    mediatorLiveData.addSource(first) {
        isFirstEmitted = true
        firstValue = it
        if (isSecondEmitted) {
            mediatorLiveData.value = Pair(firstValue as A, secondValue as B)
            isFirstEmitted = false
            isSecondEmitted = false
        }
    }
    mediatorLiveData.addSource(second) {
        isSecondEmitted = true
        secondValue = it
        if (isFirstEmitted) {
            mediatorLiveData.value = Pair(firstValue as A, secondValue as B)
            isFirstEmitted = false
            isSecondEmitted = false
        }
    }

    return mediatorLiveData
}

fun formatPrice(price: Double): String {
    return "€ ${String.format("%.2f", price)}"  // TODO: Use currency from shared preferences
}