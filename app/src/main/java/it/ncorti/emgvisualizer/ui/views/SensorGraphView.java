package it.ncorti.emgvisualizer.ui.views;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import java.util.LinkedList;

import it.ncorti.emgvisualizer.R;
import it.ncorti.emgvisualizer.model.Sensor;

/**
 * Custom view for viewing graph of raw datas
 * @author Nicola
 */
public class SensorGraphView extends View {

    /** Default point circle size */
    private static final int CIRCLE_SIZE_DEFAULT = 3;
    /** Now drawing point circle size */
    private static final int CIRCLE_SIZE_ACTUAL = 20;

    /** Graph size */
    private static final int MAX_DATA_SIZE = 150;

    /** Paint brush for drawing samples */
    private Paint[] rectPaints = new Paint[8];
    /** Paint brush for drawing info datas */
    private Paint infoPaint;
    /** Paint brush for drawing background */
    private Paint backPaint;

    /** Matrix of points */
    private float[][] normalizedPoints;
    /** Current index in matrix */
    private int currentIndex = 0;
    /** Number of different sample channels */
    private int channels = 0;

    /** Position of zeroline */
    private float zeroline = 0;

    /** String for max value label */
    private String maxValueLabel = "";
    /** String for max value label */
    private String minValueValue = "";
    /** Boolean flag for running or not */
    private boolean running = false;

    /**
     * Generic constructor called when view is inflated
     * @param context Application context
     * @param attrs   Attribute set
     */
    public SensorGraphView(Context context, AttributeSet attrs) {
        super(context, attrs);

        Resources res = context.getResources();

        // Set Paint brushes
        rectPaints[0] = new Paint();
        rectPaints[0].setColor(res.getColor(R.color.graph_line1));

        rectPaints[1] = new Paint();
        rectPaints[1].setColor(res.getColor(R.color.graph_line2));

        rectPaints[2] = new Paint();
        rectPaints[2].setColor(res.getColor(R.color.graph_line3));

        rectPaints[3] = new Paint();
        rectPaints[3].setColor(res.getColor(R.color.graph_line4));

        rectPaints[4] = new Paint();
        rectPaints[4].setColor(res.getColor(R.color.graph_line5));

        rectPaints[5] = new Paint();
        rectPaints[5].setColor(res.getColor(R.color.graph_line6));

        rectPaints[6] = new Paint();
        rectPaints[6].setColor(res.getColor(R.color.graph_line7));

        rectPaints[7] = new Paint();
        rectPaints[7].setColor(res.getColor(R.color.graph_line8));

        infoPaint = new Paint();
        infoPaint.setColor(res.getColor(R.color.graph_info));
        infoPaint.setTextSize(48f);
        infoPaint.setAntiAlias(true);

        backPaint = new Paint();
        backPaint.setColor(res.getColor(R.color.ColorBackground));
    }

    /**
     * Method for adding normalized data points
     * @param normalisedDataPoints Array of normalized data points
     * @param sensor               Sensor who generated data points
     */
    public void setNormalisedDataPoints(LinkedList<Float>[] normalisedDataPoints, Sensor sensor) {

        int beginIndex = currentIndex;
        int loopIndex;
        channels = sensor.getChannels();
        normalizedPoints = new float[channels][MAX_DATA_SIZE];


        for (int chan = 0; chan < normalisedDataPoints.length; chan++) {

            int start_point = 0;
            if (normalisedDataPoints[chan].size() > MAX_DATA_SIZE) {
                start_point = normalisedDataPoints[chan].size() - MAX_DATA_SIZE - 1;
            }

            loopIndex = beginIndex;

            int toBeExtracted = Math.min(normalisedDataPoints[chan].size(), MAX_DATA_SIZE);
            if (toBeExtracted != MAX_DATA_SIZE) {
                loopIndex -= toBeExtracted;
                if (loopIndex < 0) loopIndex += MAX_DATA_SIZE;
            }

            for (int smp = 0; smp < toBeExtracted; smp++) {

                normalizedPoints[chan][loopIndex] = normalisedDataPoints[chan].get(smp + start_point);
                loopIndex = (loopIndex + 1) % MAX_DATA_SIZE;
            }
        }

    }

    /**
     * Set string for max value label
     * @param maxValue Max value label string
     */
    public void setMaxValueLabel(String maxValue) {
        this.maxValueLabel = maxValue;
    }

    /**
     * Set string for min value label
     * @param minValue Min value label string
     */
    public void setMinValueLabel(String minValue) {
        this.minValueValue = minValue;
    }

    /**
     * Set value for zeroline
     * @param zeroline Float value of zeroline
     */
    public void setZeroLine(float zeroline) {
        this.zeroline = zeroline;
    }

    /**
     * Method for adding a new datapoint to the graph
     * @param points Array of points to be added
     */
    public void addNewDataPoint(float[] points) {
        for (int i = 0; i < channels; i++) {
            this.normalizedPoints[i][currentIndex] = points[i];
        }
        currentIndex = (currentIndex + 1) % MAX_DATA_SIZE;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int height = canvas.getHeight();
        int width = canvas.getWidth();

        canvas.drawRect(0, 0, width, height, backPaint);
        float zeroLine = height - (height * zeroline);

        canvas.drawLine(0, zeroLine, width, zeroLine, infoPaint);

        if (zeroline < 0.8f && zeroline > 0.2f) {
            canvas.drawText("0", width - 70, zeroLine - 5, infoPaint);
        }
        canvas.drawText(maxValueLabel, width - 80, 60, infoPaint);
        canvas.drawText(minValueValue, width - 80, height - 40, infoPaint);

        if (normalizedPoints == null || normalizedPoints.length <= 0) {
            return;
        }
        if (!running)
            return;

        // From here only in running mode

        int maxValues = MAX_DATA_SIZE;
        int pointSpan = width / maxValues;

        float previousX = -1;
        float previousY = -1;

        for (int i = 0; i < channels; i++) {
            int currentX = 0;//width - pointSpan;

            for (int j = 0; j < MAX_DATA_SIZE; j++) {
                float y = height - (height * normalizedPoints[i][j]);

                if (previousX != -1 && previousY != -1) {
                    canvas.drawLine(previousX, previousY, currentX, y, rectPaints[i]);
                }
                if (j == ((currentIndex - 1) % MAX_DATA_SIZE)) {
                    canvas.drawCircle(currentX, y, CIRCLE_SIZE_ACTUAL, infoPaint);
                    previousX = -1;
                    previousY = -1;
                } else {
                    canvas.drawCircle(currentX, y, CIRCLE_SIZE_DEFAULT, rectPaints[i]);
                    previousX = currentX;
                    previousY = y;
                }
                currentX += pointSpan;
            }
            previousX = -1;
            previousY = -1;
        }
    }

    /**
     * Method to start/stop running this view
     * @param setRunning Boolean value for start/stop
     */
    public void setRunning(boolean setRunning) {
        this.running = setRunning;
    }
}