package it.ncorti.emgvisualizer.model;


/**
 * Abstract class representing receiving of new RAW data
 *
 * @author Nicola
 */
public abstract class SensorUpdateEvent extends SensorEvent {

    /**
     * Raw data point
     */
    private RawDataPoint point;

    /**
     * Public constructor with setting of new received point
     *
     * @param sensor Sensor name
     * @param point  New received point
     */
    public SensorUpdateEvent(Sensor sensor, RawDataPoint point) {
        super(sensor);
        this.point = point;
    }

    /**
     * Getter for new received point
     *
     * @return The new received point
     */
    public RawDataPoint getDataPoint() {
        return point;
    }
}
