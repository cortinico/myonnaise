package it.ncorti.emgvisualizer.ui.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.squareup.otto.Subscribe;

import java.text.MessageFormat;
import java.util.LinkedList;

import it.ncorti.emgvisualizer.R;
import it.ncorti.emgvisualizer.model.EventBusProvider;
import it.ncorti.emgvisualizer.model.RawDataPoint;
import it.ncorti.emgvisualizer.model.Sensor;
import it.ncorti.emgvisualizer.model.SensorConnectEvent;
import it.ncorti.emgvisualizer.model.SensorMeasuringEvent;
import it.ncorti.emgvisualizer.model.SensorRangeEvent;
import it.ncorti.emgvisualizer.model.SensorUpdateEvent;
import it.ncorti.emgvisualizer.ui.MySensorManager;
import it.ncorti.emgvisualizer.ui.views.SensorGraphView;

/**
 * Fragment for displaying sensors raw data
 * @author Nicola
 */
public class GraphFragment extends Fragment {

    /** TAG for debugging purpose */
    private static final String TAG = "GraphFragment";

    /** Framerate ms gap */
    private static final int FRAMERATE_SKIP_MS = 20;

    /** Reference to sensor graph custom view */
    private SensorGraphView graph;
    /** Reference to sensor */
    private Sensor sensor;
    /** Reference to layout for error message */
    private RelativeLayout errorMessage;

    /** Point spread */
    private float spread = 0;
    /** Reference to thread handler */
    private Handler handler;
    /** Reference to runnable for graph timing */
    private Runnable runner;

    /** Array of normalized points */
    private float[] normalized;


    /**
     * Public constructor to create a new  GraphFragment
     */
    public GraphFragment() {
        this.sensor = MySensorManager.getInstance().getMyo();
        this.normalized = new float[sensor.getChannels()];
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        handler = new Handler(Looper.getMainLooper());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_graph, container, false);
        graph = (SensorGraphView) view.findViewById(R.id.graph_sensorgraphview);
        errorMessage = (RelativeLayout) view.findViewById(R.id.graph_error_view);

        checkForErrorMessage();

        return view;
    }

    /**
     * Private method to check if an error message must be displayed
     */
    private void checkForErrorMessage() {
        Log.d(TAG, "Measuring: " + sensor.isMeasuring() + " Status conn: " + sensor.isConnected());
        if (sensor.isMeasuring() && sensor.isConnected()) {
            errorMessage.setVisibility(View.GONE);
            graph.setVisibility(View.VISIBLE);
        } else {
            errorMessage.setVisibility(View.VISIBLE);
            graph.setVisibility(View.GONE);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        EventBusProvider.register(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        EventBusProvider.unregister(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        initialiseSensorData();
    }

    /**
     * Method to initialize sensor data to be displayed
     */
    protected void initialiseSensorData() {
        spread = sensor.getMaxValue() - sensor.getMinValue();
        LinkedList<RawDataPoint> dataPoints = sensor.getDataPoints();

        if (dataPoints == null || dataPoints.isEmpty()) {
            Log.w("sensor data", "no data found for sensor " + sensor.getName());
            return;
        }

        int channels = sensor.getChannels();

        LinkedList<Float>[] normalisedValues = new LinkedList[channels];
        for (int i = 0; i < channels; ++i) {
            normalisedValues[i] = new LinkedList<Float>();
        }


        for (RawDataPoint dataPoint : dataPoints) {
            for (int i = 0; i < channels; ++i) {
                float normalised = (dataPoint.getValues()[i] - sensor.getMinValue()) / spread;
                normalisedValues[i].add(normalised);
            }
        }

        this.graph.setNormalisedDataPoints(normalisedValues, sensor);

        this.graph.setZeroLine((0 - sensor.getMinValue()) / spread);

        this.graph.setMaxValueLabel(MessageFormat.format("{0,number,#}", sensor.getMaxValue()));
        this.graph.setMinValueLabel(MessageFormat.format("{0,number,#}", sensor.getMinValue()));
    }

    /**
     * Callback for sensor connect event
     * @param event Just received event
     */
    @Subscribe
    public void onSensorConnectEvent(SensorConnectEvent event) {
        if (event.getSensor().getName().contentEquals(sensor.getName())) {
            checkForErrorMessage();
            Log.d(TAG, "Event connected received " + event.getState());
        }
    }

    /**
     * Callback for sensor measuring event
     * @param event Just received event
     */
    @Subscribe
    public void onSensorMeasuringEvent(SensorMeasuringEvent event) {
        if (event.getSensor().getName().contentEquals(sensor.getName())) {
            checkForErrorMessage();
            Log.d(TAG, "Event measuring received " + event.getState());
        }
    }

    /**
     * Callback for sensor updated
     * @param event Just received event
     */
    @Subscribe
    public void onSensorUpdatedEvent(SensorUpdateEvent event) {
        if (!event.getSensor().getName().contentEquals(sensor.getName())) return;
        for (int i = 0; i < sensor.getChannels(); i++) {
            normalized[i] = (event.getDataPoint().getValues()[i] - sensor.getMinValue()) / spread;
        }
        this.graph.addNewDataPoint(normalized);
    }

    /**
     * Callback for sensor connect event
     * @param event Just received event
     */
    @Subscribe
    public void onSensorRangeEvent(SensorRangeEvent event) {
        if (event.getSensor().getName().contentEquals(sensor.getName()))

            initialiseSensorData();

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.graph_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle item selection
        switch (item.getItemId()) {
            case R.id.action_start_graph:
                runner = new Runnable() {
                    long last = System.currentTimeMillis();
                    long actual;

                    public void run() {
                        graph.invalidate();
                        actual = System.currentTimeMillis();
                        if (actual - last > FRAMERATE_SKIP_MS)
                            handler.postDelayed(this, actual - last);
                        else
                            handler.postDelayed(this, FRAMERATE_SKIP_MS);
                        last = actual;
                    }
                };
                graph.setRunning(true);
                handler.post(runner);
                return true;
            case R.id.action_pause_graph:
                graph.setRunning(false);
                handler.removeCallbacks(runner);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}