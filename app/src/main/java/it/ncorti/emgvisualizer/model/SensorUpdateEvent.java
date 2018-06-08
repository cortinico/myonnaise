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
