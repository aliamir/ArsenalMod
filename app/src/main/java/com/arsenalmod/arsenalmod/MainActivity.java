package com.arsenalmod.arsenalmod;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanRecord;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.bluetooth.BluetoothAdapter.*;

public class MainActivity extends AppCompatActivity {
    // Views, Lists and constant data
    private static final int REQUEST_ENABLE_BT = 1;
    private static final int  SCAN_PERIOD = 5000;
    private static final String TAG = "MainActivity";
    private ListView mListOfDevicesView;
    List<BtleDevice> deviceList = new ArrayList<>();
    private Button scanButton;
    private ProgressBar scanProgressBar;
    private boolean scanClicked = true;

    // Bluetooth Objects
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothManager mBluetoothManager;
    private BluetoothLeScanner mLEScanner;
    private ScanRecord mScanRecord;

    // Thread for scanning
    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // TODO: Check if BTLE is supported
        HandleBtleScan();

        // Create 2 item list
        showListofDevices();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = mBluetoothManager.getAdapter();
        mLEScanner = mBluetoothAdapter.getBluetoothLeScanner();
    }

    // Initialize ScanCallback
    ScanCallback mScanCallback = new ScanCallback() {
        boolean saveFlag = true;

        // This is where the device information comes in
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            Log.i("callbackType", String.valueOf(callbackType));
            Log.i("result", result.toString());

            // Create device to store the device data
            BluetoothDevice btDevice = result.getDevice();

            // Create the class that is used to display the data via ListView
            BtleDevice dAdd = new BtleDevice(btDevice.getName(), btDevice.getAddress());

            /* We want to avoid adding duplicate devices since a scan will constantly
             * show the devices on the list. We will only store unique devices by address. If
             * the List is empty, we will add the first device found and not go through an
             * empty List.
             */
            if (deviceList.size() > 0) {
                for (int i = 0; i < deviceList.size(); i++) {
                    if (btDevice.getAddress().equals(deviceList.get(i).getAddress())) {
                       saveFlag = false;
                       break;
                    }
                    else {
                        // Add device
                        saveFlag = true;
                   }
                }
            }
            else {
                saveFlag = true;
            }

            if (saveFlag) {
                deviceList.add(dAdd);
            }

            // Refresh the list of devices
            mListOfDevicesView.invalidateViews();
//                connectToDevice(btDevice);
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            for (ScanResult sr : results) {
                Log.i("ScanResult - Results", sr.toString());
            }
        }

        @Override
        public void onScanFailed(int errorCode) {
            Log.e("Scan Failed", "Error Code: " + errorCode);
        }
    };

    private void HandleBtleScan() {
        // Setup BT manager and adapter to use BT
        mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = mBluetoothManager.getAdapter();
        // initialize mLeScanner
        mLEScanner = mBluetoothAdapter.getBluetoothLeScanner();

        // Create thread to handle scanning
        mHandler = new Handler();

        scanProgressBar = (ProgressBar)findViewById(R.id.scan_progress_bar);

        // Scan Button Pressed
        scanButton = (Button)findViewById(R.id.scan_button);
        scanButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                // If BT is off, request to turn it on
                if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
                    Intent enableBtIntent = new Intent(ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                    scanProgressBar.setVisibility(View.INVISIBLE);
                } else {
                    // Ask for location permission before scanning for BT devices. Google is weird.
                    int MY_PERMISSIONS_REQUEST_BTLE = 0;
                    int permissionCheck = ContextCompat.checkSelfPermission(MainActivity.this,
                            ACCESS_FINE_LOCATION);
                    if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                        //Request access
                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                                        Manifest.permission.ACCESS_FINE_LOCATION},
                                MY_PERMISSIONS_REQUEST_BTLE);
                    }

                    if (scanClicked) {

                        // Start Progress Bar Spinning
                        scanProgressBar.setVisibility(View.VISIBLE);
                        scanButton.setText(R.string.stop_scan_button_text);

                        // Clear old list of devices
                        deviceList.clear();

                        // Start a scan
                        scanForDevices(true);
                    } else {
                        // Stop Progress Bar Spinning
                        scanProgressBar.setVisibility(View.GONE);
                        scanButton.setText(R.string.start_scan_button_text);

                        // Stop a scan
                        scanForDevices(false);
                    }
                    scanClicked ^= true;
                }
            }
        });
    }

    private void scanForDevices(final boolean enable) {
        if (enable) {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                        mLEScanner.stopScan(mScanCallback);
                        scanProgressBar.setVisibility(View.GONE);
                        scanButton.setText(R.string.start_scan_button_text);
                        scanClicked = true;
                }
            }, SCAN_PERIOD);
            mLEScanner.startScan(mScanCallback);
        }
        else {
            mLEScanner.stopScan(mScanCallback);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showListofDevices() {
        // Populate ListView
        ArrayAdapter<BtleDevice> adapter = new MyListAdapter();
        mListOfDevicesView = (ListView)findViewById(R.id.list_bt_devices);
        mListOfDevicesView.setAdapter(adapter);
    }

    private class MyListAdapter extends ArrayAdapter<BtleDevice> {
        public MyListAdapter() {
            super(MainActivity.this, R.layout.list_devices, deviceList);
        }

        @Override
        public View getView(int position, View convertedView, ViewGroup parent) {
            // Make sure we have a view to use
            View itemView = convertedView;
            if (itemView == null) {
                itemView = getLayoutInflater().inflate(R.layout.list_devices, parent, false);
            }

            // Find the device
            BtleDevice currentDevice = deviceList.get(position);

            // Fill the View (show the data from the BtleDevice class
            TextView dNameText = (TextView)(itemView.findViewById(R.id.name));
            dNameText.setText(currentDevice.getName());

            TextView dAddressText = (TextView)itemView.findViewById(R.id.address);
            dAddressText.setText(currentDevice.getAddress());

            // Return the View
            return itemView;
        }
    }
}

