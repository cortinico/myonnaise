package it.ncorti.emgvisualizer.myo;

/**
 * This class is List of Myo Commands, allowing to
 * [https://github.com/thalmiclabs/myo-bluetooth].
 *
 * @author Naoki
 */
public class MyoCommandList {

    private byte[] send_bytes_data;

    public byte[] sendUnsetData() {
        byte command_data = (byte) 0x01;
        byte payload_data = (byte) 3;
        byte emg_mode = (byte) 0x00;
        byte imu_mode = (byte) 0x00;
        byte class_mode = (byte) 0x00;
        send_bytes_data =
                new byte[]{command_data, payload_data, emg_mode, imu_mode, class_mode};

        return send_bytes_data;
    }

    public byte[] sendVibration3() {
        byte command_vibrate = (byte) 0x03;
        byte payload_vibrate = (byte) 1;
        byte vibrate_type = (byte) 0x03;
        send_bytes_data = new byte[]{command_vibrate, payload_vibrate, vibrate_type};

        return send_bytes_data;
    }

    public byte[] sendEmgOnly() {
        byte command_data = (byte) 0x01;
        byte payload_data = (byte) 3;
        byte emg_mode = (byte) 0x02;
        byte imu_mode = (byte) 0x00;
        byte class_mode = (byte) 0x00;
        send_bytes_data =
                new byte[]{command_data, payload_data, emg_mode, imu_mode, class_mode};

        return send_bytes_data;
    }

    public byte[] sendUnSleep() {
        byte command_sleep_mode = (byte) 0x09;
        byte payload_unlock = (byte) 1;
        byte never_sleep = (byte) 1;
        send_bytes_data = new byte[]{command_sleep_mode, payload_unlock, never_sleep};

        return send_bytes_data;
    }

    public byte[] sendNormalSleep() {
        byte command_sleep_mode = (byte) 0x09;
        byte payload_unlock = (byte) 1;
        byte normal_sleep = (byte) 0;
        send_bytes_data = new byte[]{command_sleep_mode, payload_unlock, normal_sleep};

        return send_bytes_data;
    }
}
