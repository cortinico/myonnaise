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
 * Abstract class representing a generic event
 *
 * @author Nicola
 */
public abstract class AbstractEvent {

    /**
     * Time stamp of event generatior
     */
    private long timeStamp;

    /**
     * Generic public constructor. Timestamp is recorded at method invocation
     *
     */
    public AbstractEvent() {
        this.timeStamp = System.currentTimeMillis();
    }

    /**
     * Generic public constructor with a specific timestamp
     *
     * @param timeStamp Event generation timestamp
     */
    public AbstractEvent(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    /**
     * Getter for timestamp
     *
     * @return Timestamp reference
     */
    public long getTimeStamp() {
        return timeStamp;
    }

    /**
     * Abstract method for event firing
     */
    public abstract void fireEvent();
}
