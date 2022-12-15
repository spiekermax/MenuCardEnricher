package de.unihannover.hci.menudetector.views.graphics

// Kotlin
import kotlin.math.max
import kotlin.math.min

// Android
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF

// Google
import com.google.mlkit.vision.text.Text

// Internal dependencies
import de.unihannover.hci.menudetector.views.GraphicOverlayView


private const val MARKER_COLOR = Color.WHITE
private const val STROKE_WIDTH = 4.0f
private const val TEXT_COLOR = Color.BLACK
private const val TEXT_SIZE = 54.0f

/**
 * Adopted from Google LLC. in compliance with Apache License Version 2.0.
 *
 * Graphic instance for rendering TextBlock position, size, and ID within an associated graphic
 * overlay view.
 */
class TextOverlayGraphic(
    overlay: GraphicOverlayView,
    private val text: Text,
    private val shouldGroupTextInBlocks: Boolean,
    private val showConfidence: Boolean
) : GraphicOverlayView.Graphic(overlay) {

    /* ATTRIBUTES */

    private val rectPaint: Paint = Paint()
    private val textPaint: Paint
    private val labelPaint: Paint


    /* LIFECYCLE */

    init {
        rectPaint.color = MARKER_COLOR
        rectPaint.style = Paint.Style.STROKE
        rectPaint.strokeWidth = STROKE_WIDTH
        textPaint = Paint()
        textPaint.color = TEXT_COLOR
        textPaint.textSize = TEXT_SIZE
        labelPaint = Paint()
        labelPaint.color = MARKER_COLOR
        labelPaint.style = Paint.Style.FILL

        postInvalidate()
    }


    /* METHODS */

    /**
     * Draws the text block annotations for position, size, and raw value on the supplied canvas.
     */
    override fun draw(canvas: Canvas) {
        // Renders the text at the bottom of the box.
        for (textBlock in text.textBlocks) {
            if (shouldGroupTextInBlocks) {
                drawText(
                    getFormattedText(textBlock.text, confidence = null),
                    RectF(textBlock.boundingBox),
                    TEXT_SIZE * textBlock.lines.size + 2 * STROKE_WIDTH,
                    canvas
                )
            } else {
                for (line in textBlock.lines) {
                    // Draws the bounding box around the TextBlock.
                    val rect = RectF(line.boundingBox)
                    drawText(
                        getFormattedText(line.text, line.confidence),
                        rect,
                        TEXT_SIZE + 2 * STROKE_WIDTH,
                        canvas
                    )
                }
            }
        }
    }

    private fun getFormattedText(text: String, confidence: Float?): String {
        return if (showConfidence && confidence != null) String.format("%s (%.2f)", text, confidence)
        else text
    }

    private fun drawText(text: String, rect: RectF, textHeight: Float, canvas: Canvas) {
        // If the image is flipped, the left will be translated to right, and the right to left.
        val x0 = translateX(rect.left)
        val x1 = translateX(rect.right)
        rect.left = min(x0, x1)
        rect.right = max(x0, x1)
        rect.top = translateY(rect.top)
        rect.bottom = translateY(rect.bottom)
        canvas.drawRect(rect, rectPaint)

        val textWidth = textPaint.measureText(text)
        canvas.drawRect(
            rect.left - STROKE_WIDTH,
            rect.top - textHeight,
            rect.left + textWidth + 2 * STROKE_WIDTH,
            rect.top,
            labelPaint,
        )

        // Renders the text at the bottom of the box.
        canvas.drawText(text, rect.left, rect.top - STROKE_WIDTH, textPaint)
    }
}