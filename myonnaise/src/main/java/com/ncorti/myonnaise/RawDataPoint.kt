package com.ncorti.myonnaise

import android.util.Log

/**
 * Class representing a single Raw data point, providing timestamp, accuracy and an array of float values.
 */
class RawDataPoint
/**
 * Generic constructor for new received point
 *
 * @param timestamp Timestamp of acquired point
 * @param accuracy Accuracy of received point
 * @param values Array of received values
 */
(
        /** Timestamp of received point  */
        /**
         * Return timestamp of received value
         * @return Timestamp of received value
         */
        val timestamp: Long,
        /** Accuracy of reading  */
        /**
         * Return accuracy of raw data
         * @return Accuracy of raw data
         */
        val accuracy: Int,
        /** Array of float values  */
        /**
         * Return array of values
         * @return Array of raw values
         */
        val values: FloatArray) : Cloneable {

    /**
     * Public method for setting new array of raw values
     * @param values New float array
     */
    fun setValue(values: FloatArray) {
        for (i in this.values.indices)
            this.values[i] = values[i]
    }

    override fun toString(): String {
        var print = timestamp.toString() + ";"
        for (i in values.indices) {
            print += values[i].toString() + ";"
        }
        return print
    }

    public override fun clone(): RawDataPoint {
        val newPoint = RawDataPoint(timestamp, accuracy, FloatArray(values.size))
        newPoint.setValue(values)
        return newPoint
    }

    companion object {

        /** TAG for debugging purpose  */
        private val TAG = "RawDataPoint"

        /**
         * Static method for printing a list of Raw Data Points in a CSV format
         * @param points List of Raw Data Points
         */
        fun printList(points: List<RawDataPoint>) {
            var print = "timestamp;"
            for (i in 0 until points[0].values.size) {
                print += "sample$i;"
            }
            Log.d(TAG, print)
            for (point in points) {
                Log.d(TAG, point.toString())
            }
        }
    }
}
