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
