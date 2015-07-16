package it.ncorti.emgvisualizer.model;


/**
 * Abstract class representing a generic event related to a sensor range change.
 * This event can be used to trigger visual redrawing of graph or similar.
 *
 * @author Nicola
 */
public abstract class SensorRangeEvent extends SensorEvent {

    /**
     * TAG for debugging purpose
     */
    private static final String TAG = "SmartwatchRangeEvent";

    /**
     * New min sensor value
     */
    private float minValue;
    /**
     * New max sensor value
     */
    private float maxValue;

    /**
     * Public constructor with setting of new sensor ranges
     *
     * @param sensor   Sensor name
     * @param minValue New sensor min value
     * @param maxValue New sensor max value
     */
    public SensorRangeEvent(Sensor sensor, float minValue, float maxValue) {
        super(sensor);
        this.minValue = minValue;
        this.maxValue = maxValue;
    }

    /**
     * Public constructor with setting of new sensor ranges and specific timeStamp
     *
     * @param sensor    Sensor name
     * @param timeStamp Event generation timestamp
     * @param minValue  New sensor min value
     * @param maxValue  New sensor max value
     */
    public SensorRangeEvent(Sensor sensor, long timeStamp, float minValue, float maxValue) {
        super(sensor, timeStamp);
        this.minValue = minValue;
        this.maxValue = maxValue;
    }
}
