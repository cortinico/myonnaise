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
