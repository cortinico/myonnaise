package it.ncorti.emgvisualizer.myo;


import android.util.Log;

import it.ncorti.emgvisualizer.model.Sensor;
import it.ncorti.emgvisualizer.model.SensorRangeEvent;

/**
 * Abstract class representing event for new Myo emg raw data limits
 *
 * @author Nicola
 */
public class MyoSensorRangeEvent extends SensorRangeEvent {

    /**
     * TAG for debugging purpose
     */
    private static final String TAG = "MyoRangeEvent";

    /**
     * Public constructor with setting of new sensor ranges
     *
     * @param sensor   Sensor name
     * @param minValue New sensor min value
     * @param maxValue New sensor max value
     */
    public MyoSensorRangeEvent(Sensor sensor, float minValue, float maxValue) {
        super(sensor, minValue, maxValue);
    }

    @Override
    public void fireEvent() {
        Log.d(TAG, "Event fired! Sensor: " + this.getSensor().getName() + " Time: " + this.getTimeStamp());
    }
}
