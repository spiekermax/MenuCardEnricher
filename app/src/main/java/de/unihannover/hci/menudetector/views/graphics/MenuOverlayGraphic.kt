package de.unihannover.hci.menudetector.views.graphics

// Kotlin
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.roundToInt

// Android
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF

// Internal dependencies
import de.unihannover.hci.menudetector.models.recognition.MenuRecognitionResult
import de.unihannover.hci.menudetector.util.color.Gradient
import de.unihannover.hci.menudetector.views.GraphicOverlayView


private const val STROKE_WIDTH = 4.0f

private const val TEXT_COLOR = Color.WHITE
private const val TEXT_SIZE = 54.0f

private val CONFIDENCE_COLORS: List<Int> = Gradient(Color.RED, Color.GREEN).steps(100)

class MenuOverlayGraphic(
    overlay: GraphicOverlayView,
    private val menu: MenuRecognitionResult,
) : GraphicOverlayView.Graphic(overlay) {

    /* ATTRIBUTES */

    private val rectPaint: Paint = Paint()
    private val textPaint: Paint = Paint()
    private val labelPaint: Paint = Paint()


    /* LIFECYCLE */

    init {
        rectPaint.color = CONFIDENCE_COLORS[0]
        rectPaint.style = Paint.Style.STROKE
        rectPaint.strokeWidth = STROKE_WIDTH

        textPaint.color = TEXT_COLOR
        textPaint.textSize = TEXT_SIZE

        labelPaint.color = CONFIDENCE_COLORS[0]
        labelPaint.style = Paint.Style.FILL

        postInvalidate()
    }


    /* METHODS */

    override fun draw(canvas: Canvas) {
        for (dish in menu.dishes) {
            drawText(
                canvas = canvas,
                text = dish.name,
                rect = RectF(dish.boundingBox),
                background = confidenceColor(dish.confidence),
            )
        }
    }

    private fun drawText(canvas: Canvas, text: String, rect: RectF, background: Int) {
        val x0 = translateX(rect.left)
        val x1 = translateX(rect.right)
        rect.left = min(x0, x1)
        rect.right = max(x0, x1)
        rect.top = translateY(rect.top)
        rect.bottom = translateY(rect.bottom)

        rectPaint.color = background
        canvas.drawRect(rect, rectPaint)

        val textWidth = textPaint.measureText(text)
        val textHeight = TEXT_SIZE + 2 * STROKE_WIDTH

        labelPaint.color = background
        canvas.drawRect(
            rect.left - STROKE_WIDTH,
            rect.top - textHeight,
            rect.left + textWidth + 2 * STROKE_WIDTH,
            rect.top,
            labelPaint,
        )

        canvas.drawText(text, rect.left, rect.top - STROKE_WIDTH, textPaint)
    }


    /* UTILITY */

    private fun confidenceColor(confidence: Float): Int {
        val sanitizedConfidence = confidence.toDouble().coerceIn(0.0, 1.0)
        val projectedIndex: Double = sanitizedConfidence * 100 - 1
        val bentIndex: Int = (0.5 * projectedIndex + 1.0398955028272.pow(projectedIndex)).roundToInt()

        return CONFIDENCE_COLORS[bentIndex]
    }

}