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
package it.ncorti.emgvisualizer.old.model;

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
