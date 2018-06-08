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
