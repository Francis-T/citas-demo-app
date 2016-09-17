package org.citas.basicdemo;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.citas.bluelinklib.interfaces.BluetoothEventHandler;
import org.citas.bluelinklib.interfaces.ILinkBridge;
import org.citas.bluelinklib.util.DemoLogger;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements BluetoothEventHandler {
    protected static final DemoLogger OLog = DemoLogger.getInstance();
    private ArrayAdapter<Device> _deviceListAdapter = null;
    private List<Device> _devices = new ArrayList<>();
    private String _dataBuf = "";

    private boolean _bIsReceiverRegistered = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        _deviceListAdapter = new ArrayAdapter<Device>(this, android.R.layout.simple_list_item_1, _devices);

        ListView deviceList = (ListView) findViewById(R.id.list_devices);
        deviceList.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        String name = _devices.get(position).getName();
                        String addr = _devices.get(position).getAddress();
                        new BluetoothDownloadTask().execute(name, addr);
                    }
                }
        );
        deviceList.setAdapter(_deviceListAdapter);

        /* Start the bluetooth bridge */
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        registerReceiver(_receiver, filter);

        _bIsReceiverRegistered = true;

        /* Obtain application reference */
        DemoApp app = (DemoApp) this.getApplication();

        if (app.getBluetoothAdapter().isEnabled() == false) {
            OLog.err("Bluetooth is not enabled");
            finish();
        }

        if (app.getBluetoothAdapter().cancelDiscovery() == false) {
            OLog.err("Failed to cancel old c discovery task");
        }

        if (app.getBluetoothAdapter().startDiscovery() == false) {
            OLog.err("Failed to start service discovery!");
        }

        if (app.getBluetoothBridge().initialize(this) != ILinkBridge.STATUS_FAILED) {
            OLog.err("Failed to initialize Bluetooth bridge");
        }

        if (app.getBluetoothBridge().setEventHandler(this) != ILinkBridge.STATUS_FAILED) {
            OLog.err("Failed to set the event handler");
        }

        OLog.info("onCreate() finished");

        return;
    }

    @Override
    protected void onDestroy() {
        if (_bIsReceiverRegistered) {
            unregisterReceiver(_receiver);
        }

        super.onDestroy();
        return;
    }

    @Override
    public void onDataReceived(byte[] data) {
        //new DisplayDataTask().execute(new String(data));
        _dataBuf += new String(data);
        return;
    }

    /***************************/
    /** Private Inner Classes **/
    /***************************/
    private class Device {
        private String _name = "";
        private String _addr = "";

        public Device(String name, String address) {
            this._name = name;
            this._addr = address;
            return;
        }

        public String getName() {
            return this._name;
        }

        public String getAddress() {
            return this._addr;
        }

        public String toString() {
            return (this._name + "\n" + this._addr);
        }

    }

    private class DisplayDataTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            return params[0];
        }

        @Override
        protected void onPostExecute(String data) {
            Intent intent = new Intent(MainActivity.this, DisplayActivity.class);
            intent.putExtra("CONTENT", data);
            startActivity(intent);

            _dataBuf = "";

            super.onPostExecute(data);
            return;
        }
    }

    private class BluetoothDownloadTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {
            if (params == null) {
                return null;
            }

            if (params.length != 2) {
                return null;
            }

            String name = params[0];
            String addr = params[1];

            /* Obtain application reference */
            DemoApp app = (DemoApp) MainActivity.this.getApplication();
            ILinkBridge btBridge = app.getBluetoothBridge();

            int ret = btBridge.connectByAddress(addr);
            if (ret != ILinkBridge.STATUS_OK)  {
                OLog.err("Failed to connect: " + name + "(" + addr + ")");
            }

            ret = btBridge.broadcast("DOWNLOAD>".getBytes());
            if (ret != ILinkBridge.STATUS_OK)  {
                OLog.err("Failed to broadcast: " + name + "(" + addr + ")");
            }

            try {
                Thread.sleep(8000);
            } catch (InterruptedException e) {
                // Ok
            }

            ret = btBridge.destroy();
            if (ret != ILinkBridge.STATUS_OK)  {
                OLog.err("Failed to close connection: " + name + "(" + addr + ")");
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            new DisplayDataTask().execute(_dataBuf);
            return;
        }
    }

    /**********/
    /** Misc **/
    /**********/
    // Create a BroadcastReceiver for ACTION_FOUND
    private final BroadcastReceiver _receiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Get the BluetoothDevice object from the Intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // Add the name and address to an array adapter to show in a ListView

                Device d = new Device(device.getName(), device.getAddress());
                _devices.add(d);
                _deviceListAdapter.notifyDataSetChanged();

                OLog.info("Device Found: " + d.toString());
            }

            return;
        }
    };
}
