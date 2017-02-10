package com.arsenalmod.arsenalmod;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import java.util.List;

public class ConnectedDeviceActivity extends AppCompatActivity {
    private BluetoothDevice mBluetoothDevice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connected_device);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Log.e("ConnectedDeviceActivity", "Now in ConnectedDeviceActivity.");

        Intent intent = getIntent();

        Bundle b = intent.getExtras();
        if (b != null) {
            BtleDevice cmds = new BtleDevice(null, null);
            mBluetoothDevice = b.getParcelable(cmds.serviceStrings[0]);
        }
        Log.e("ConnectedDeviceActivity", "Passed BlueToothDevice: " + mBluetoothDevice.getName());
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }
}
