package com.arsenalmod.arsenalmod;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.bluetooth.BluetoothAdapter.*;
import static android.widget.Toast.*;

public class MainActivity extends AppCompatActivity {
    // Views, Lists and constant data
    private static final int REQUEST_ENABLE_BT = 1;
    private static final int  SCAN_PERIOD = 5000;
    private static final String TAG = "MainActivity";
    private ListView mListOfDevicesView;
    List<BluetoothDevice> deviceList = new ArrayList<>();
    private Button scanButton;
    private ProgressBar scanProgressBar;
    private boolean scanClicked = true;
    private BluetoothGatt mGatt;

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

        // Handle Clicked devices to connect to
        clickedDevicesFromList();
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

            if (deviceList.size() == 0) {
                saveFlag = true;
            }
            else {
                for (int i = 0; i < deviceList.size(); i++) {
                    if (btDevice.getAddress().equals(deviceList.get(i).getAddress())) {
                       saveFlag = false;
                       break;
                    }
                    else {
                        // Add device to List
                        saveFlag = true;
                   }
                }
            }

            if (saveFlag) {
                deviceList.add(btDevice);
            }

            // Refresh the list of devices
            mListOfDevicesView.invalidateViews();
//          connectToDevice(btDevice);
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
                mLEScanner.stopScan(mScanCallback);
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
        ArrayAdapter<BluetoothDevice> adapter = new MyListAdapter();
        mListOfDevicesView = (ListView)findViewById(R.id.list_bt_devices);
        mListOfDevicesView.setAdapter(adapter);
    }

    private class MyListAdapter extends ArrayAdapter<BluetoothDevice> {
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
            BluetoothDevice currentDevice = deviceList.get(position);

            // Fill the View (show the data from the BtleDevice class
            TextView dNameText = (TextView)(itemView.findViewById(R.id.name));
            dNameText.setText(currentDevice.getName());

            TextView dAddressText = (TextView)itemView.findViewById(R.id.address);
            dAddressText.setText(currentDevice.getAddress());

            // Return the View
            return itemView;
        }
    }

    private void clickedDevicesFromList() {
        mListOfDevicesView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                BluetoothDevice deviceClicked = deviceList.get(position);
                String message = "You selected " + deviceClicked.getName() + " with address: " + deviceClicked.getAddress();
                makeText(MainActivity.this, message, LENGTH_LONG).show();

                // Connect to the device before starting a new activity to interact with the device
                connectToDevice(deviceClicked);

                // Start new activity
            }
        });
    }

    private void connectToDevice(BluetoothDevice device) {
        if (mGatt == null) {
            mGatt = device.connectGatt(this, false, gattCallback);
        }
    }

    private final BluetoothGattCallback gattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            Log.i("onConnectionStateChange", "Status: " + status);
            switch (newState) {
                case BluetoothProfile.STATE_CONNECTED:
                    Log.i("gattCallback", "STATE_CONNECTED");
                    gatt.discoverServices();
                    break;
                case BluetoothProfile.STATE_DISCONNECTED:
                    Log.e("gattCallback", "STATE_DISCONNECTED");
                    break;
                default:
                    Log.e("gattCallback", "STATE_OTHER");
            }

        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            List<BluetoothGattService> services = gatt.getServices();
            Log.i("onServicesDiscovered", services.toString());
//            gatt.readCharacteristic(services.get(1).getCharacteristics().get
//                    (0));
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt,
                                         BluetoothGattCharacteristic
                                                 characteristic, int status) {
            Log.i("onCharacteristicRead", characteristic.toString());
            gatt.disconnect();
        }
    };
}

