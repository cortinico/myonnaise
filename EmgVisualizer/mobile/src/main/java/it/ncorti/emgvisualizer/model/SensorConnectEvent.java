package it.ncorti.emgvisualizer.model;


import android.util.Log;

/**
 * Abstract class representing sensor connection/disconnection
 *
 * @author Nicola
 */
public class SensorConnectEvent extends SensorEvent {

    /**
     * TAG for debugging purpose
     */
    private static final String TAG = "SensorConnectEvent";

    /**
     * Internal flag for connected or not
     */
    private boolean connected;

    /**
     * Public constructor with setting of new state
     *
     * @param sensor Sensor name
     * @param connected True if connection succeed, false otherwise
     */
    public SensorConnectEvent(Sensor sensor, boolean connected) {
        super(sensor);
        this.connected = connected;
    }

    /**
     * Getter for new state
     *
     * @return The new connection state
     */
    public boolean getState(){ return this.connected; }

    @Override
    public void fireEvent() {
        Log.d(TAG, "Sensor event from: " + getSensor().getName() + " connected: " + connected);
    }
}
