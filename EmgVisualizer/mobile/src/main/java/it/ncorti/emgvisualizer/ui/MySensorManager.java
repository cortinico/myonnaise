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
package it.ncorti.emgvisualizer.ui;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import it.ncorti.emgvisualizer.model.Sensor;
import it.ncorti.emgvisualizer.myo.MyoSensor;

/**
 * Global class for handling different kind of sensors
 * @author Nicola
 */
public class MySensorManager {

    /** Singleton: concrete instance */
    private static MySensorManager concreteInstance = null;

    /** Reference to Myo Sensor, null if not setted */
    private MyoSensor myoSensor = null;

    /** List of active sensor */
    private List<Sensor> sensorList = new ArrayList<>();

    /**
     * Private empty constructor
     */
    private MySensorManager() {
    }

    /**
     * Singleton: method for obtaining an instance of MySensorManager
     * @return A concrete instance of MySensorManager
     */
    public static MySensorManager getInstance() {
        if (concreteInstance == null)
            concreteInstance = new MySensorManager();
        return concreteInstance;
    }

    /**
     * Method for getting reference to Myo
     * @return Reference to Myo
     */
    public MyoSensor getMyo() {
        return myoSensor;
    }

    /**
     * Method to check if myo as already been scanned and setted
     * @return True if myo was found, false otherwise
     */
    public boolean isMyoFound() {
        return (myoSensor != null);
    }

    /**
     * Method to set reference to Myo Sensor
     * @param name    Myo Name
     * @param address Myo BT Hw address
     * @param c       Application context
     */
    public void setMyo(String name, String address, Context c) {
        // Specific custom section
        myoSensor = new MyoSensor(name, address, c);
        for (Sensor s : sensorList)
            if (s instanceof MyoSensor) sensorList.remove(s);
        sensorList.add(myoSensor);
    }
}