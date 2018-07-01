package com.ncorti.myonnaise.sensorgraphview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.hardware.Sensor
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import java.util.*

class SensorGraphView(context: Context, attrs: AttributeSet) : View(context, attrs) {

    /** Paint brush for drawing samples  */
    private val rectPaints = arrayListOf<Paint>()
    /** Paint brush for drawing info datas  */
    private val infoPaint: Paint

    /** Matrix of points  */
    private var normalizedPoints: Array<FloatArray>? = null
    /** Current index in matrix  */
    private var currentIndex = 0
    /** Number of different sample channels  */
    private var channels = 0

    /** Position of zeroline  */
    private var zeroline = 0f

    /** String for max value label  */
    private var maxValueLabel = ""
    /** String for max value label  */
    private var minValueValue = ""
    /** Boolean flag for running or not  */
    private var running = false

    init {

        val res = context.resources

        val colors = context.resources.getIntArray(R.array.graph_colors)
        for (i in 0 until colors.size) {
            val paint = Paint()
            paint.color = ContextCompat.getColor(context, colors[i])
        }

        infoPaint = Paint()
        infoPaint.color = ContextCompat.getColor(context, R.color.graph_info)

        // TODO Fix here
        infoPaint.textSize = 48f
        infoPaint.isAntiAlias = true
    }

    /**
     * Method for adding normalized data points
     * @param normalisedDataPoints Array of normalized data points
     * @param sensor               Sensor who generated data points
     */
    fun setNormalisedDataPoints(normalisedDataPoints: Array<LinkedList<Float>>, sensor: Sensor) {

        val beginIndex = currentIndex
        var loopIndex: Int
        // TODO
        channels = 8
        normalizedPoints = Array(channels) { FloatArray(MAX_DATA_SIZE) }


        for (chan in normalisedDataPoints.indices) {

            var start_point = 0
            if (normalisedDataPoints[chan].size > MAX_DATA_SIZE) {
                start_point = normalisedDataPoints[chan].size - MAX_DATA_SIZE - 1
            }

            loopIndex = beginIndex

            val toBeExtracted = Math.min(normalisedDataPoints[chan].size, MAX_DATA_SIZE)
            if (toBeExtracted != MAX_DATA_SIZE) {
                loopIndex -= toBeExtracted
                if (loopIndex < 0) loopIndex += MAX_DATA_SIZE
            }

            for (smp in 0 until toBeExtracted) {

                normalizedPoints!![chan][loopIndex] = normalisedDataPoints[chan][smp + start_point]
                loopIndex = (loopIndex + 1) % MAX_DATA_SIZE
            }
        }

    }

    /**
     * Set string for max value label
     * @param maxValue Max value label string
     */
    fun setMaxValueLabel(maxValue: String) {
        this.maxValueLabel = maxValue
    }

    /**
     * Set string for min value label
     * @param minValue Min value label string
     */
    fun setMinValueLabel(minValue: String) {
        this.minValueValue = minValue
    }

    /**
     * Set value for zeroline
     * @param zeroline Float value of zeroline
     */
    fun setZeroLine(zeroline: Float) {
        this.zeroline = zeroline
    }

    /**
     * Method for adding a new datapoint to the graph
     * @param points Array of points to be added
     */
    fun addNewDataPoint(points: FloatArray) {
        for (i in 0 until channels) {
            this.normalizedPoints!![i][currentIndex] = points[i]
        }
        currentIndex = (currentIndex + 1) % MAX_DATA_SIZE
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val height = canvas.height
        val width = canvas.width

//        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), backPaint)
        val zeroLine = height - height * zeroline

        canvas.drawLine(0f, zeroLine, width.toFloat(), zeroLine, infoPaint)

        if (zeroline < 0.8f && zeroline > 0.2f) {
            canvas.drawText("0", (width - 70).toFloat(), zeroLine - 5, infoPaint)
        }
        canvas.drawText(maxValueLabel, (width - 80).toFloat(), 60f, infoPaint)
        canvas.drawText(minValueValue, (width - 80).toFloat(), (height - 40).toFloat(), infoPaint)

        if (normalizedPoints == null || normalizedPoints!!.size <= 0) {
            return
        }
        if (!running)
            return

        // From here only in running mode

        val maxValues = MAX_DATA_SIZE
        val pointSpan = width / maxValues

        var previousX = -1f
        var previousY = -1f

        for (i in 0 until channels) {
            var currentX = 0//width - pointSpan;

            for (j in 0 until MAX_DATA_SIZE) {
                val y = height - height * normalizedPoints!![i][j]

                if (previousX != -1f && previousY != -1f) {
                    canvas.drawLine(previousX, previousY, currentX.toFloat(), y, rectPaints[i])
                }
                if (j == (currentIndex - 1) % MAX_DATA_SIZE) {
                    canvas.drawCircle(currentX.toFloat(), y, CIRCLE_SIZE_ACTUAL.toFloat(), infoPaint)
                    previousX = -1f
                    previousY = -1f
                } else {
                    canvas.drawCircle(currentX.toFloat(), y, CIRCLE_SIZE_DEFAULT.toFloat(), rectPaints[i])
                    previousX = currentX.toFloat()
                    previousY = y
                }
                currentX += pointSpan
            }
            previousX = -1f
            previousY = -1f
        }
    }

    /**
     * Method to start/stop running this view
     * @param setRunning Boolean value for start/stop
     */
    fun setRunning(setRunning: Boolean) {
        this.running = setRunning
    }

    companion object {

        /** Default point circle size  */
        private val CIRCLE_SIZE_DEFAULT = 3
        /** Now drawing point circle size  */
        private val CIRCLE_SIZE_ACTUAL = 20

        /** Graph size  */
        private val MAX_DATA_SIZE = 150
    }
}