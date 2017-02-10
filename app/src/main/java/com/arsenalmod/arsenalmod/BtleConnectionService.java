package com.arsenalmod.arsenalmod;

import android.app.IntentService;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.Intent;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import java.util.List;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class BtleConnectionService extends IntentService {
    private BluetoothDevice mBluetoothDevice;
    private BluetoothGatt mGatt;

    // TODO: Rename actions, choose action names that describe tasks that this
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    private static final String ACTION_FOO = "com.arsenalmod.arsenalmod.action.FOO";
    private static final String ACTION_BAZ = "com.arsenalmod.arsenalmod.action.BAZ";

    // TODO: Rename parameters
    private static final String EXTRA_PARAM1 = "com.arsenalmod.arsenalmod.extra.PARAM1";
    private static final String EXTRA_PARAM2 = "com.arsenalmod.arsenalmod.extra.PARAM2";

    /******************* LEFT THIS FROM ORIGINAL FILE GENERATION ******************
     * This service starts new services based on the request that has come in from
     * the activity. */

    /*
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService

    // TODO: Customize helper method
    public static void startActionFoo(Context context, String param1, String param2) {
        Intent intent = new Intent(context, BtleConnectionService.class);
        intent.setAction(ACTION_FOO);
        intent.putExtra(EXTRA_PARAM1, param1);
        intent.putExtra(EXTRA_PARAM2, param2);
        context.startService(intent);
    }
    */

    /******************* LEFT THIS FROM ORIGINAL FILE GENERATION ******************/


    public BtleConnectionService() {
        super("BtleConnectionService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.e("BtleConnectionService", "Service Started!");

        /* ActivityMain will connect to the device and this service must maintain the connection.
         * Service must:
         * - Detect when the BLE connection is lost
         * - Notify activity that connection has been lost to restart a connection
         * - ACK to activity for each send/receive of commands
         * - Notify activity that service has been killed */
        //getIntent().getExtras().getParcelable("btdevice");
        BtleDevice cmds = new BtleDevice(null, null);
        Bundle b = intent.getExtras();
        if (b != null) {
            mBluetoothDevice = b.getParcelable(cmds.serviceStrings[0]);
        }

        Log.e("BtleConnectionService", "Connected to Device: " + mBluetoothDevice.getName());
        connectToDevice(mBluetoothDevice);
        // Initialize BLE objects

        // Check connection to device

        // Create events for when BLE transactions occur to fill in those callbacks

        /* To broadcast back to activity:
         * Intent i = new Intent(ACK_XXX_CMD);
          * BleConnectionService.this.sendBroadcast(i);
          * ACK_XXX_CMD = the ACK of the command sent over the BLE connection
          * */
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
