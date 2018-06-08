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
