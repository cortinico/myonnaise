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

import java.util.LinkedList;

/**
 * Model class for handling an hardware concrete sensor
 * @author Nicola Corti
 */
public abstract class Sensor {

    /**
     * TAG for debugging purpose
     */
    private static final String TAG = "Sensor";

    /**
     * Data point list size
     */
    private static final int MAX_DATA_POINTS = 5000;
    /**
     * Data point list
     */
    private LinkedList<RawDataPoint> dataPoints = new LinkedList<>();
    /**
     * Max sensor value
     */
    private float minValue = Integer.MAX_VALUE;
    /**
     * Min sensor value
     */
    private float maxValue = Integer.MIN_VALUE;

    /**
     * Sensor name
     */
    private String name;

    /**
     * Generic public constructor
     * @param name Sensor name
     */
    public Sensor(String name) {
        this.name = name;
    }

    /**
     * Public constructor with setting of specific sensor ranges
     * @param name     Sensor name
     * @param minValue Sensor min value
     * @param maxValue Sensor max value
     */
    public Sensor(String name, float minValue, float maxValue) {
        this.name = name;
        this.minValue = minValue;
        this.maxValue = maxValue;
    }

    /**
     * Getter for sensor name
     * @return Sensor name
     */
    public String getName() {
        return name;
    }

    /**
     * Method for getting short sensor name, used for Arff file (it's short name without spaces)
     * @return Sensor short name
     */
    public String getShortName() {
        String shortName = name.substring(0, 5).toLowerCase().replace(" ", "_");
        for(int i = shortName.length(); i < 5; i++)
            shortName += "_";
        return shortName;
    }

    /**
     * Getter for sensor status string (for displaying purposes
     * @return Sensor status string
     */
    public String getStatusString() {
        String status = "<p>Device is currenty: ";
        if (isConnected()) {
            status += " <b>Connected.</b><br>";
        } else {
            status += " <b>Disconnected.</b><br>";
        }
        if (isMeasuring())
            status += " Raw data receiving is: <b>Active.</b><br>";
        else
            status += " Raw data receiving is: <b>Inactive.</b><br>";
        //status += "Received RAW points: <b>" + getDataPoints().size() + "</b></p>";
        status += "</p>";
        return status;
    }

    /**
     * Getter for sensor max value
     * @return Sensor max value
     */
    public float getMaxValue() {
        return maxValue;
    }

    /**
     * Getter for sensor min value
     * @return Sensor min value
     */
    public float getMinValue() {
        return minValue;
    }

    /**
     * Getter for data point list.
     * The list is cloned before returning.
     * @return Data point list
     */
    public synchronized LinkedList<RawDataPoint> getDataPoints() {
        return (LinkedList<RawDataPoint>) dataPoints.clone();
    }

    /**
     * Method for adding a point to the sensor data point list
     * @param dataPoint New point to be added
     */
    public synchronized void addDataPoint(RawDataPoint dataPoint) {
        dataPoints.addLast(dataPoint);

        if (dataPoints.size() > MAX_DATA_POINTS) {
            dataPoints.removeFirst();
        }

        // Check for new range event
        boolean newLimits = false;

        for (float value : dataPoint.getValues()) {
            if (value > maxValue) {
                maxValue = value;
                newLimits = true;
            }
            if (value < minValue) {
                minValue = value;
                newLimits = true;
            }
        }

        if (newLimits) {
            Log.d(TAG, "New range for sensor " + name + ": " + minValue + " - " + maxValue);
            EventBusProvider.postOnMainThread(getRangeEvent(minValue, maxValue));
        }
        SensorUpdateEvent updateEvent = getUpdateEvent(dataPoint);
        EventBusProvider.postOnMainThread(updateEvent);
    }

    /**
     * Method for clearing data point list
     */
    public synchronized void clearDataPoints() {
        dataPoints.clear();
    }

    /**
     * Method for starting connection with sensor
     */
    public abstract void startConnection();

    /**
     * Method for stopping connection with sensor
     */
    public abstract void stopConnection();

    /**
     * Method for check if connection with sensor is established
     * @return True if connection is established, false otherwise
     */
    public abstract boolean isConnected();

    /**
     * Method for starting raw data measurement
     */
    public abstract void startMeasurement();

    /**
     * Method for stopping raw data measurement
     */
    public abstract void stopMeasurement();

    /**
     * Method for check if sensor is streaming raw data
     * @return True if sensor is streaming, false otherwise
     */
    public abstract boolean isMeasuring();

    /**
     * Return concrete subclass instance of a SensorUpdateEvent
     * <p/>
     * This method is necessary due to event system design
     * @param point New point related to update event
     * @return The new SensorUpdateEvent
     */
    public abstract SensorUpdateEvent getUpdateEvent(RawDataPoint point);

    /**
     * Return concrete subclass instance of a SensorRangeEvent
     * <p/>
     * This method is necessary due to event system design
     * @param minValue New min value
     * @param maxValue New max value
     * @return The new SensorUpdateEvent
     */
    public abstract SensorRangeEvent getRangeEvent(float minValue, float maxValue);

    /**
     * Return size of RAW value array (e.g For accelerometer 3 as x,y,z axes)
     * @return Number of different axes
     */
    public abstract int getChannels();

    @Override
    public boolean equals(Object o) {
        if (o == null) return false;
        else if (o instanceof Sensor){
            if (((Sensor) o).getName().contentEquals(this.name))
                return true;
        }
        return false;
    }

    public void addMark(RawDataPoint point){
        dataPoints.add(point);
    }
}
