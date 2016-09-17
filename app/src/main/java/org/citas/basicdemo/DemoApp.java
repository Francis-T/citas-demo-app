package org.citas.basicdemo;

import android.app.Application;
import android.bluetooth.BluetoothAdapter;

import org.citas.bluelinklib.android.AndroidBluetoothBridge;

/**
 * Created by francis on 8/16/16.
 */
public class DemoApp extends Application {
    private BluetoothAdapter _btAdapter = BluetoothAdapter.getDefaultAdapter();
    private AndroidBluetoothBridge _btBridge = AndroidBluetoothBridge.getInstance();

    public AndroidBluetoothBridge getBluetoothBridge() {
        return _btBridge;
    }

    public BluetoothAdapter getBluetoothAdapter() {
        return _btAdapter;
    }
}
