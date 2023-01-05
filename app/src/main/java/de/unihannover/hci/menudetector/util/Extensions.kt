package de.unihannover.hci.menudetector.util

// Kotlin
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

// Google
import com.google.android.gms.tasks.Task


suspend fun <T> Task<T>.await(): T = suspendCoroutine { continuation ->
    addOnSuccessListener {
        continuation.resume(it)
    }
    addOnFailureListener {
        continuation.resumeWithException(it)
    }
}