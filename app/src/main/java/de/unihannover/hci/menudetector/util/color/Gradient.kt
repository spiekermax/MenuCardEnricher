package de.unihannover.hci.menudetector.util.color

// Android
import android.graphics.Color


class Gradient(
    start: Int,
    end: Int,
) {

    private val startColor: Color = Color.valueOf(start)
    private val endColor: Color = Color.valueOf(end)

    fun steps(count: Int): List<Int> {
        val steps: MutableList<Int> = mutableListOf()
        for (step in 0..count) {
            val color = Color.rgb(
                (startColor.red() * (count - step) + endColor.red() * step) / count,
                (startColor.green() * (count - step) + endColor.green() * step) / count,
                (startColor.blue() * (count - step) + endColor.blue() * step) / count,
            )

            steps.add(color)
        }

        return steps
    }

}