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

import android.util.Log;

import java.util.List;

/**
 * Class representing a single Raw data point, providing timestamp, accuracy and an array of float values.
 *
 * @author Nicola
 */
public class RawDataPoint implements Cloneable {

    /** TAG for debugging purpose */
    private static final String TAG = "RawDataPoint";

    /** Timestamp of received point */
    private long timestamp;
    /** Array of float values */
    private float[] values;
    /** Accuracy of reading */
    private int accuracy;

    /**
     * Generic constructor for new received point
     *
     * @param timestamp Timestamp of acquired point
     * @param accuracy Accuracy of received point
     * @param values Array of received values
     */
    public RawDataPoint(long timestamp, int accuracy, float[] values) {
        this.timestamp = timestamp;
        this.accuracy = accuracy;
        this.values = values;
    }

    /**
     * Return array of values
     * @return Array of raw values
     */
    public float[] getValues() {
        return values;
    }

    /**
     * Public method for setting new array of raw values
     * @param values New float array
     */
    public void setValue(float[] values) {
        for (int i = 0; i < this.values.length; i++)
            this.values[i] = values[i];
    }

    /**
     * Return timestamp of received value
     * @return Timestamp of received value
     */
    public long getTimestamp() {
        return timestamp;
    }

    /**
     * Return accuracy of raw data
     * @return Accuracy of raw data
     */
    public int getAccuracy() {
        return accuracy;
    }

    @Override
    public String toString(){
        String print =  timestamp + ";";
        for(int i = 0; i < values.length; i++){
            print += values[i] + ";";
        }
        return print;
    }

    /**
     * Static method for printing a list of Raw Data Points in a CSV format
     * @param points List of Raw Data Points
     */
    public static void printList(List<RawDataPoint> points){
        String print = "timestamp;";
        for(int i = 0; i < points.get(0).getValues().length; i++){
            print += "sample" + i + ";";
        }
        Log.d(TAG, print);
        for(RawDataPoint point : points){
            Log.d(TAG, point.toString());
        }
    }

    @Override
    public RawDataPoint clone(){
        RawDataPoint newPoint = new RawDataPoint(timestamp, accuracy, new float[values.length]);
        newPoint.setValue(values);
        return newPoint;
    }
}
