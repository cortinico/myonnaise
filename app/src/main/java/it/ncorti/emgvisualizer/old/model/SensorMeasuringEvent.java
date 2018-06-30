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
