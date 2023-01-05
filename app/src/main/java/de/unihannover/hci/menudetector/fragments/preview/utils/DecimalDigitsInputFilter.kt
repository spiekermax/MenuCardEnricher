package de.unihannover.hci.menudetector.fragments.preview.utils

import android.text.InputFilter
import android.text.Spanned

class DecimalDigitsInputFilter
    (private val decimalDigits: Int) : InputFilter {
    override fun filter(
        source: CharSequence,
        start: Int,
        end: Int,
        dest: Spanned,
        dstart: Int,
        dend: Int
    ): CharSequence? {
        var dotPos = -1
        val len = dest.length
        for (i in 0 until len) {
            val c = dest[i]
            if (c == '.' || c == ',') {
                dotPos = i
                break
            }
        }
        if (dotPos >= 0) {
            if (source == "." || source == ",") {
                return ""
            }
            if (dend <= dotPos) {
                return null
            }
            if (len - dotPos > decimalDigits) {
                return ""
            }
        }
        return null
    }
}