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
package it.ncorti.emgvisualizer.old.ui.fragments;

import android.annotation.TargetApi;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.graphics.Outline;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import androidx.fragment.app.Fragment;
import it.ncorti.emgvisualizer.R;
import it.ncorti.emgvisualizer.old.ui.OldMainActivity;
import it.ncorti.emgvisualizer.old.ui.MySensorManager;

/**
 * Fragment for displaying scanned myos
 * @author Nicola
 */
public class MyoListFragment extends Fragment {

    /** TAG for debugging purpose */
    private static final String TAG = "MyoListFragment";

    /** Reference to imagebutton for triggering scan */
    private ImageButton btnScan;
    /** Reference to listview to display found myos */
    private ListView lstMyo;

    /** ArrayList of found Myos */
    private ArrayList<Pair<String, String>> deviceList = new ArrayList<>();
    /** Reference to ArrayAdapter for Myo list */
    private ArrayAdapter adapter;

    /** Scan period in milliseconds */
    private static final long SCAN_PERIOD = 5000;

    /** Reference to Handler for thread execution */
    private Handler mHandler;

    /** Reference to bluetooth LE scanner for scanning */
    private BluetoothLeScanner mBluetoothLeScanner;
    /** Reference to BT scan callback */
    private ScanMyoListCallback mCallback;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_myolist, container, false);

        mHandler = new Handler();

        BluetoothManager mBluetoothManager = (BluetoothManager) getActivity().getSystemService(Activity.BLUETOOTH_SERVICE);
        BluetoothAdapter mBluetoothAdapter = mBluetoothManager.getAdapter();
//        mBluetoothLeScanner = mBluetoothAdapter.getBluetoothLeScanner();

        mCallback = new ScanMyoListCallback();

        lstMyo = (ListView) view.findViewById(R.id.myolist_found_list);
        btnScan = (ImageButton) view.findViewById(R.id.myolist_scan_button);

        btnScan.setOutlineProvider(new ViewOutlineProvider() {
            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override

            // Set oval button
            public void getOutline(View view, Outline outline) {
                int diameter = getResources().getDimensionPixelSize(R.dimen.diameter);
                outline.setOval(0, 0, diameter, diameter);
            }
        });
        btnScan.setClipToOutline(true);

        // Set button click callback
        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (lstMyo.getCheckedItemPosition() != -1) {
                    Pair selected = (Pair) adapter.getItem(lstMyo.getCheckedItemPosition());
                    MySensorManager.getInstance().setMyo((String) selected.first, (String) selected.second, getActivity());
                    ((OldMainActivity) getActivity()).changeFragmentMyoControl();
                } else {
                    scanDevice();
                }
            }
        });

        adapter = new MyoArrayAdapter(getActivity(), android.R.layout.simple_list_item_2, android.R.id.text1, deviceList);
        lstMyo.setAdapter(adapter);
        lstMyo.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);

        // Set list click callback
        lstMyo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                lstMyo.setItemChecked(i, true);
                btnScan.setImageResource(R.drawable.ic_checkbox_marked_circle_outline_white_24dp);
                Log.d(TAG, "Selected " + i + " check " + lstMyo.getCheckedItemPosition() + " select " + lstMyo.getSelectedItemPosition());
            }
        });
        return view;
    }

    /**
     * Method for starting scan for Myos in nearby
     */
    public void scanDevice() {
        resetList();
        // Scanning Time out by Handler.
        // The device scanning needs high energy.
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
//                mBluetoothLeScanner.stopScan(mCallback);
                Pair<String, String> foundDevice = new Pair<>("AAA", "ABCDE");
                for (int i = 0; i < 20; i++){
                    deviceList.add(foundDevice);
                }
                deviceList.add(foundDevice);
                adapter.notifyDataSetChanged();
                try {
                    Toast.makeText(getActivity(), MyoListFragment.this.getString(R.string.scan_over), Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, SCAN_PERIOD);
//        mBluetoothLeScanner.startScan(mCallback);
    }

    protected class ScanMyoListCallback extends ScanCallback {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            BluetoothDevice device = result.getDevice();
            String deviceAddress = device.getAddress();
            String deviceName = device.getName();

            String msg = "name=" + device.getName() + ", bondStatus="
                    + device.getBondState() + ", address="
                    + device.getAddress() + ", type" + device.getType();
            Log.d(TAG, msg);

            Pair<String, String> foundDevice = new Pair<>(deviceName, deviceAddress);
            if (!deviceList.contains(foundDevice)) {
                deviceList.add(foundDevice);
            }
        }
    }

    /**
     * Method for resetting Myo found list
     */
    private void resetList() {
        deviceList.clear();
        btnScan.setImageResource(R.drawable.ic_magnify_white_24dp);
        adapter.notifyDataSetChanged();
    }
}