package it.ncorti.emgvisualizer.model;

/**
 * Abstract class representing a generic event related to a specific sensor.
 *
 * @author Nicola
 */
public abstract class SensorEvent extends AbstractEvent {

    /**
     * Sensor reference
     */
    private Sensor sensor;

    /**
     * Generic public constructor. Timestamp is recorded at method invocation
     *
     * @param sensor Sensor reference
     */
    public SensorEvent(Sensor sensor) {
        super();
        this.sensor = sensor;
    }

    /**
     * Generic public constructor with a specific timestamp
     *
     * @param sensor    Sensor reference
     * @param timeStamp Event generation timestamp
     */
    public SensorEvent(Sensor sensor, long timeStamp) {
        super(timeStamp);
        this.sensor = sensor;
    }

    /**
     * Getter for sensor
     *
     * @return Sensor reference
     */
    public Sensor getSensor() {
        return sensor;
    }
}
