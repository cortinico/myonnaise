package it.ncorti.emgvisualizer.ui.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.otto.Subscribe;

import it.ncorti.emgvisualizer.R;
import it.ncorti.emgvisualizer.model.EventBusProvider;
import it.ncorti.emgvisualizer.model.Sensor;
import it.ncorti.emgvisualizer.model.SensorConnectEvent;
import it.ncorti.emgvisualizer.model.SensorMeasuringEvent;
import it.ncorti.emgvisualizer.ui.MySensorManager;

/**
 * Fragment for controlling sensors, allowing to connect and start raw data receiving
 * @author Nicola
 */
public class ControlFragment extends Fragment implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    /** TAG for debugging purpose */
    private static final String TAG = "ControlFragment";

    /** Reference to controlled sensor */
    private Sensor controlledSensor;

    /** Reference to Textview for Sensor name */
    private TextView txtSensorName;
    /** Reference to Textview for Sensor status */
    private TextView txtSensorStatus;
    /** Reference to Button to trigger connection */
    private Button btnConnection;
    /** Reference to Switch to trigger measuring */
    private Switch swcStream;
    /** Reference to Seekbar for streaming speed */
    private SeekBar skbRatio;

    /**
     * Public constructor to create a new ControlFragment
     */
    public ControlFragment() {
        this.controlledSensor = MySensorManager.getInstance().getMyo();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_control, container, false);

        btnConnection = (Button) view.findViewById(R.id.control_btn_connect);
        txtSensorName = (TextView) view.findViewById(R.id.control_sensor_name);
        txtSensorStatus = (TextView) view.findViewById(R.id.control_sensor_status);
        swcStream = (Switch) view.findViewById(R.id.control_swc_stream);
        swcStream.setOnCheckedChangeListener(this);

        txtSensorName.setText(controlledSensor.getName());
        updateSensorStatusView();

        btnConnection.setOnClickListener(this);
        setButtonConnect(controlledSensor.isConnected());
        return view;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.control_btn_connect) {
            if (!controlledSensor.isConnected()) {
                controlledSensor.startConnection();
                Toast.makeText(getActivity(), this.getString(R.string.connection_started), Toast.LENGTH_SHORT).show();
            } else {
                controlledSensor.stopConnection();
                Toast.makeText(getActivity(), this.getString(R.string.connection_stopped), Toast.LENGTH_SHORT).show();
            }
            setButtonConnect(controlledSensor.isConnected());
        }
        updateSensorStatusView();
    }

    /**
     * Private method for updating button connect state upon sensor status
     * @param connect True if sensor is connected, false otherwise
     */
    private void setButtonConnect(boolean connect) {

        Resources res = this.getResources();

        if (connect) {
            // Display disconnect layout
            btnConnection.setText(this.getString(R.string.disconnect));
            btnConnection.setCompoundDrawablesWithIntrinsicBounds(res.getDrawable(R.drawable.ic_bluetooth_disabled_white_36dp), null, null, null);
        } else {
            btnConnection.setText(this.getString(R.string.connect));
            btnConnection.setCompoundDrawablesWithIntrinsicBounds(res.getDrawable(R.drawable.ic_bluetooth_searching_white_36dp), null, null, null);
        }
    }

    /**
     * Method for updating sensor status textview
     */
    private void updateSensorStatusView() {
        txtSensorStatus.setText(Html.fromHtml(controlledSensor.getStatusString()));
        if (controlledSensor.isConnected())
            swcStream.setEnabled(true);
        else
            swcStream.setEnabled(false);

        if (controlledSensor.isMeasuring()) {
            swcStream.setChecked(true);
        } else {
            swcStream.setChecked(false);
        }
    }

    /**
     * Callback for Sensor connect event
     * @param event Event just received
     */
    @Subscribe
    public void onSensorConnectEvent(SensorConnectEvent event) {
        if (event.getSensor().getName().contentEquals(controlledSensor.getName())) {
            setButtonConnect(event.getSensor().isConnected());
            updateSensorStatusView();
            Log.d(TAG, "Event connected received " + event.getState());
        }
    }

    /**
     * Callback for Sensor start measuring event
     * @param event Event just received
     */
    @Subscribe
    public void onSensorMeasuringEvent(SensorMeasuringEvent event) {
        if (event.getSensor().getName().contentEquals(controlledSensor.getName())) {
            updateSensorStatusView();
            Log.d(TAG, "Event measuring received " + event.getState());
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        EventBusProvider.register(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        EventBusProvider.unregister(this);
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        if (compoundButton.getId() == R.id.control_swc_stream) {
            if (b)
                controlledSensor.startMeasurement();
            else
                controlledSensor.stopMeasurement();
        }
    }
}