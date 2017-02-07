package com.arsenalmod.arsenalmod;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
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
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.bluetooth.BluetoothAdapter.*;
import static android.bluetooth.BluetoothManager.*;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_ENABLE_BT = 1;
    private static final String TAG = "MainActivity";
    private ListView mListOfDevicesView;
    List<BtleDevice> deviceList = new ArrayList<>();
    private Button scanButton;

    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothManager mBluetoothManager;
    private BluetoothLeScanner mLEScanner;
//    private LeScanCallback mScanCallback;

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
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            Log.i("callbackType", String.valueOf(callbackType));
            Log.i("result", result.toString());
//                BluetoothDevice btDevice = result.getDevice();
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

        scanButton = (Button)findViewById(R.id.scan_button);
        scanButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                // If BT is off, request to turn it on
                if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
                    Intent enableBtIntent = new Intent(ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                }
                else{
                    // if not initialized, initialize it here
//                    if (mLEScanner == null) {
//                        mLEScanner = mBluetoothAdapter.getBluetoothLeScanner();
//                    }

                    // Ask for location permission before scanning for BT devices. Google is weird.
                    int MY_PERMISSIONS_REQUEST_BTLE = 0;
                    int permissionCheck = ContextCompat.checkSelfPermission(MainActivity.this,
                            ACCESS_FINE_LOCATION);
                    if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                        //Request access
                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                MY_PERMISSIONS_REQUEST_BTLE);
                    }
                    // Start a scan
                    mLEScanner.startScan(mScanCallback);
                }
            }
        });
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
        //Create a list
        BtleDevice mBtleDevice = new BtleDevice("Amir", "Naqui");

        deviceList.add(mBtleDevice);
        deviceList.add(mBtleDevice);
        deviceList.add(mBtleDevice);

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

