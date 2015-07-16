/* This file is part of EmgVisualizer.

    EmgVisualizer is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    EmgVisualizer is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with EmgVisualizer.  If not, see <http://www.gnu.org/licenses/>.
*/
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
