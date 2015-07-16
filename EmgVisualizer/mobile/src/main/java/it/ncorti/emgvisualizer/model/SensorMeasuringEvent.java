package it.ncorti.emgvisualizer.model;

/**
 * Abstract class representing sensor measuring start/stop event
 *
 * @author Nicola
 */
public class SensorMeasuringEvent extends SensorConnectEvent {

    /**
     * Public contstructor to create a new SensorMeasuringEvent
     * @param sensor Sensor involved
     * @param flagMeasuring True if device is start measuring, false otherwise
     */
    public SensorMeasuringEvent(Sensor sensor, boolean flagMeasuring) {
        super(sensor, flagMeasuring);
    }
}
