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
package it.ncorti.emgvisualizer.ui.fragments;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Pair;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Adapter for displaying a list of found Myos
 *
 * @author Nicola
 */
public class MyoArrayAdapter extends ArrayAdapter<Pair<String, String>> {

    /**
     * Public constructor to create a new MyoArrayAdapter
     * @param context Application context
     * @param resource Resource id
     * @param textViewResourceId Textview Resource IF
     * @param objects List of objects Name-Address
     */
    public MyoArrayAdapter(Context context, int resource, int textViewResourceId, List<Pair<String, String>> objects) {
        super(context, resource, textViewResourceId, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = super.getView(position, convertView, parent);
        TextView text1 = (TextView) view.findViewById(android.R.id.text1);
        TextView text2 = (TextView) view.findViewById(android.R.id.text2);

        Pair<String, String> pair = getItem(position);

        // Populate the view
        text1.setText(pair.first);
        text1.setTypeface(null, Typeface.BOLD);
        text2.setText(pair.second);

        return view;
    }
}
