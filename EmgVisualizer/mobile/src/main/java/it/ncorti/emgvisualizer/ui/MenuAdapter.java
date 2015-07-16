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


import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import it.ncorti.emgvisualizer.R;

/**
 * Adapter for handling menu visualization using a Recyclerview
 *
 * @author Nicola
 */
public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.ViewHolder> {

    /** Type ID for Header View */
    private static final int TYPE_HEADER = 0;
    /** Type ID for Item View */
    private static final int TYPE_ITEM = 1;
    /** Type ID for Separator View */
    private static final int TYPE_SEPARATOR = 2;

    /** Array of menu entries */
    private String menuEntries[];
    /** Array of menu icons IDs */
    private int menuIcons[];
    /** ID of selected position */
    private int selectedPosition = 1;

    /**
     * Generic public constructor
     * @param titles Array of titles
     * @param icons  Array of Icons
     */
    public MenuAdapter(String titles[], int icons[]) {
        menuEntries = titles;
        menuIcons = icons;
    }

    @Override
    public MenuAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        // Inflate correct view depending on viewType
        if (viewType == TYPE_ITEM) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.menu_row, parent, false);
            return new ViewHolder(v, viewType);
        } else if (viewType == TYPE_HEADER) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.menu_header, parent, false);
            return new ViewHolder(v, viewType);
        } else if (viewType == TYPE_SEPARATOR) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.menu_row_title, parent, false);
            return new ViewHolder(v, viewType);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(MenuAdapter.ViewHolder holder, int position) {

        // Binding data from model to holder
        if (holder.Holderid == TYPE_ITEM) {
            holder.txtEntry.setText(menuEntries[position - 1]);
            holder.imgMenuIcon.setImageResource(menuIcons[position - 1]);
            if (selectedPosition == position) {
                holder.globalView.setBackgroundColor(Color.parseColor("#FFD5D5D5"));
            } else {
                holder.globalView.setBackgroundColor(Color.WHITE);
            }
        } else if (holder.Holderid == TYPE_SEPARATOR) {
            holder.txtEntry.setText(menuEntries[position - 1]);
        }
    }

    @Override
    public int getItemCount() {
        return menuEntries.length + 1; // +1 is for the header
    }

    @Override
    public int getItemViewType(int position) {
        // Return viewType upon position
        if (position == 0)
            return TYPE_HEADER;
        else if (menuIcons[position - 1] == -1)
            return TYPE_SEPARATOR;
        return TYPE_ITEM;
    }

    /**
     * Public method for updating selected item
     * @param newSelectedItem Position of new selected item
     */
    public void updateSelectedItem(int newSelectedItem) {
        if (selectedPosition == newSelectedItem || selectedPosition > getItemCount())
            return;
        int oldPosition = selectedPosition;
        selectedPosition = newSelectedItem;
        notifyItemChanged(oldPosition);
        notifyItemChanged(selectedPosition);
    }

    /**
     * View holder for handling view inflating an retrieving
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {

        /** Holder ID Type */
        int Holderid;
        /** Reference to Textview for description */
        TextView txtEntry;
        /** Reference to Icon */
        ImageView imgMenuIcon;
        /** Reference to global view */
        View globalView;

        /**
         * Public constructor for creating the holder
         * @param itemView View of current item
         * @param ViewType View type ID
         */
        public ViewHolder(View itemView, int ViewType) {
            super(itemView);
            if (ViewType == TYPE_ITEM) {
                txtEntry = (TextView) itemView.findViewById(R.id.menu_txt_entry);
                imgMenuIcon = (ImageView) itemView.findViewById(R.id.menu_img_icon);
                this.globalView = itemView;
            } else if (ViewType == TYPE_SEPARATOR) {
                txtEntry = (TextView) itemView.findViewById(R.id.menu_txt_separator);
            }
            Holderid = ViewType;
        }
    }
}