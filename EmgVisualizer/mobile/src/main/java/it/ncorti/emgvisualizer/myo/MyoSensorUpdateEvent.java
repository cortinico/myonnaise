package it.ncorti.emgvisualizer.myo;


import android.util.Log;

import it.ncorti.emgvisualizer.model.SensorUpdateEvent;
import it.ncorti.emgvisualizer.model.RawDataPoint;
import it.ncorti.emgvisualizer.model.Sensor;

/**
 * Abstract class representing event for receiving of new Myo emg RAW data
 *
 * @author Nicola
 */
public class MyoSensorUpdateEvent extends SensorUpdateEvent {

    /**
     * TAG for debugging purpose
     */
    private static final String TAG = "MyoSensorUpdateEvent";

    /**
     * Public constructor with setting of new received point
     *
     * @param sensor Sensor name
     * @param point  New received point
     */
    public MyoSensorUpdateEvent(Sensor sensor, RawDataPoint point) {
        super(sensor, point);
    }

    @Override
    public void fireEvent() {
        Log.d(TAG, "Event fired! Sensor: " + this.getSensor().getName() + " Time: " + this.getTimeStamp());
    }
}
