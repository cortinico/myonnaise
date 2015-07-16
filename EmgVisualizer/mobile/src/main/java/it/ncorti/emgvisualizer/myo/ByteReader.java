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

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * This class help you to read the byte line from Myo.
 * But be carefully to byte array size. There is no limitation of get() method,
 * so there is a possibilty of overloading the byte buffer.
 * <p>
 * ByteReader is useful for handling raw data taken from bluetooth connection with Myo
 *
 * @author Nicola
 */
public class ByteReader {

    /**
     * Raw byte array
     */
    private byte[] byteData;
    /**
     * ByteBuffer for reading purpose
     */
    private ByteBuffer bbf;

    /**
     * Method for setting byteData into reader
     *
     * @param data Raw byteData read
     */
    public void setByteData(byte[] data) {
        this.byteData = data;
        this.bbf = ByteBuffer.wrap(this.byteData);
        bbf.order(ByteOrder.LITTLE_ENDIAN);
    }

    /**
     * Return reference to byteData
     *
     * @return byteData reference
     */
    public byte[] getByteData() {
        return byteData;
    }

    /**
     * Return a short from byteReader
     * ATTENTION: don't call this method before setByteData
     * ATTENTION: pay attention to byte array size
     *
     * @return Next short read
     */
    public short getShort() {
        return this.bbf.getShort();
    }

    /**
     * Return a byte from byteReader
     * ATTENTION: don't call this method before setByteData
     * ATTENTION: pay attention to byte array size
     *
     * @return Next byte read
     */
    public byte getByte() {
        return this.bbf.get();
    }

    /**
     * Return a int from byteReader
     * ATTENTION: don't call this method before setByteData
     * ATTENTION: pay attention to byte array size
     *
     * @return Next int read
     */
    public int getInt() {
        return this.bbf.getInt();
    }

    /**
     * Rewind byte reader to begin, for restart reading
     */
    public void rewind() {
        this.bbf.rewind();
    }

    /**
     * Method for reading n consecutive floats, returned in a new array
     *
     * @param size Number of bytes to be read (usually 8 or 16)
     * @return A new array with read bytes
     */
    public float[] getBytes(int size) {
        float[] result = new float[size];
        for (int i = 0; i < size; i++)
            result[i] = bbf.get();
        return result;
    }
}
