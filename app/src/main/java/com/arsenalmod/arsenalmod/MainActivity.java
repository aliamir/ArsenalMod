package com.arsenalmod.arsenalmod;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private ListView mListOfDevicesView;
    List<BtleDevice> deviceList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Create 2 item list
        showListofDevices();
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
        mListOfDevicesView = (ListView) findViewById(R.id.list_bt_devices);

        //Create a list
        BtleDevice mBtleDevice = new BtleDevice("Amir", "Naqui");

        deviceList.add(mBtleDevice);
        deviceList.add(mBtleDevice);
        deviceList.add(mBtleDevice);

        // Populate ListView
        ArrayAdapter<BtleDevice> adapter = new MyListAdapter();
        ListView list = (ListView)findViewById(R.id.list_bt_devices);
        list.setAdapter(adapter);
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

