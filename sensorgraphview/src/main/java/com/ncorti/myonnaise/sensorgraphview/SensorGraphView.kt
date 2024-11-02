package com.ncorti.myonnaise.sensorgraphview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import kotlin.math.min

/** Default point circle size  */
private const val CIRCLE_SIZE_DEFAULT = 3
/** Now drawing point circle size  */
private const val CIRCLE_SIZE_ACTUAL = 20
/** Graph size  */
private const val MAX_DATA_SIZE = 150

private const val INITIAL_MAX_VALUE = 10.0f
private const val INITIAL_MIN_VALUE = -10.0f

class SensorGraphView(context: Context, attrs: AttributeSet) : View(context, attrs) {

    var running = false
    var channels = 0
        set(value) {
            field = value
            normalizedPoints = arrayOf()
            for (i in 0 until value) {
                normalizedPoints += FloatArray(MAX_DATA_SIZE)
            }
        }

    var maxValue = INITIAL_MAX_VALUE
    var minValue = INITIAL_MIN_VALUE

    private val spread: Float
        get() = maxValue - minValue

    private val zeroLine: Float
        get() = (0 - minValue) / spread

    /** Paint brush for drawing samples  */
    private val rectPaints = arrayListOf<Paint>()
    /** Paint brush for drawing info datas  */
    private val infoPaint: Paint

    /** Matrix of points  */
    private var normalizedPoints: Array<FloatArray> = arrayOf()
    /** Current index in matrix  */
    private var currentIndex = 0

    init {

        val colors = context.resources.getIntArray(R.array.graph_colors)
        for (element in colors) {
            val paint = Paint()
            paint.color = Color.parseColor("#${Integer.toHexString(element)}")
            rectPaints += paint
        }

        infoPaint = Paint()
        infoPaint.color = ContextCompat.getColor(context, R.color.graph_info)

        infoPaint.textSize = context.resources
            .getDimensionPixelSize(R.dimen.text_size).toFloat()
        infoPaint.isAntiAlias = true
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)

        val desiredWidth = suggestedMinimumWidth + paddingLeft + paddingRight
        val width = when (MeasureSpec.getMode(widthMeasureSpec)) {
            MeasureSpec.EXACTLY -> widthSize
            MeasureSpec.AT_MOST -> min(desiredWidth, widthSize)
            MeasureSpec.UNSPECIFIED -> desiredWidth
            else -> desiredWidth
        }
        val desiredHeight = suggestedMinimumHeight + paddingTop + paddingBottom
        val height = when (MeasureSpec.getMode(heightMeasureSpec)) {
            MeasureSpec.EXACTLY -> heightSize
            MeasureSpec.AT_MOST -> min(desiredHeight, heightSize)
            MeasureSpec.UNSPECIFIED -> desiredHeight
            else -> desiredHeight
        }
        setMeasuredDimension(width, height)
    }

    fun addPoint(points: FloatArray) {
        for (i in 0 until channels) {
            this.normalizedPoints[i][currentIndex] = (points[i] - minValue) / spread
        }
        currentIndex = (currentIndex + 1) % MAX_DATA_SIZE
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val height = height
        val width = width

        val zeroLine = height - height * zeroLine
        canvas.drawLine(0f, zeroLine, width.toFloat(), zeroLine, infoPaint)

        if (normalizedPoints.isEmpty()) {
            return
        }
        if (!running)
            return

        val pointSpan: Float = width.toFloat() / MAX_DATA_SIZE.toFloat()
        var previousX = -1f
        var previousY = -1f

        for (i in 0 until channels) {
            var currentX = pointSpan

            for (j in 0 until MAX_DATA_SIZE) {
                val y = height - height * normalizedPoints[i][j]
                if (previousX != -1f && previousY != -1f) {
                    canvas.drawLine(previousX, previousY, currentX, y, rectPaints[i])
                }
                if (j == (currentIndex - 1) % MAX_DATA_SIZE) {
                    canvas.drawCircle(currentX, y, CIRCLE_SIZE_ACTUAL.toFloat(), infoPaint)
                    previousX = -1f
                    previousY = -1f
                } else {
                    canvas.drawCircle(currentX, y, CIRCLE_SIZE_DEFAULT.toFloat(), rectPaints[i])
                    previousX = currentX
                    previousY = y
                }
                currentX += pointSpan
            }
            previousX = -1f
            previousY = -1f
        }
    }
}
