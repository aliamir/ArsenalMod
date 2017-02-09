package com.arsenalmod.arsenalmod;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.util.Log;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class BtleConnectionService extends IntentService {
    // TODO: Rename actions, choose action names that describe tasks that this
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    private static final String ACTION_FOO = "com.arsenalmod.arsenalmod.action.FOO";
    private static final String ACTION_BAZ = "com.arsenalmod.arsenalmod.action.BAZ";

    // TODO: Rename parameters
    private static final String EXTRA_PARAM1 = "com.arsenalmod.arsenalmod.extra.PARAM1";
    private static final String EXTRA_PARAM2 = "com.arsenalmod.arsenalmod.extra.PARAM2";

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionFoo(Context context, String param1, String param2) {
        Intent intent = new Intent(context, BtleConnectionService.class);
        intent.setAction(ACTION_FOO);
        intent.putExtra(EXTRA_PARAM1, param1);
        intent.putExtra(EXTRA_PARAM2, param2);
        context.startService(intent);
    }

    /**
     * Starts this service to perform action Baz with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionBaz(Context context, String param1, String param2) {
        Intent intent = new Intent(context, BtleConnectionService.class);
        intent.setAction(ACTION_BAZ);
        intent.putExtra(EXTRA_PARAM1, param1);
        intent.putExtra(EXTRA_PARAM2, param2);
        context.startService(intent);
    }

    public BtleConnectionService() {
        super("BtleConnectionService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.e("BtleConnectionService", "Service Started!");
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_FOO.equals(action)) {
                final String param1 = intent.getStringExtra(EXTRA_PARAM1);
                final String param2 = intent.getStringExtra(EXTRA_PARAM2);
                handleActionFoo(param1, param2);
            } else if (ACTION_BAZ.equals(action)) {
                final String param1 = intent.getStringExtra(EXTRA_PARAM1);
                final String param2 = intent.getStringExtra(EXTRA_PARAM2);
                handleActionBaz(param1, param2);
            }
        }

        /* ActivityMain will connect to the device and this service must maintain the connection.
         * Service must:
         * - Detect when the BLE connection is lost
         * - Notify activity that connection has been lost to restart a connection
         * - ACK to activity for each send/receive of commands
         * - Notify activity that service has been killed */
        String passedConnectionCmd = intent.getStringExtra("start_connection");
        Log.e("BtleConnectionService", "From ActivityMain: " + passedConnectionCmd);

        // Initialize BLE objects

        // Check connection to device

        // Create events for when BLE transactions occur to fill in those callbacks

        /* To broadcast back to activity:
         * Intent i = new Intent(ACK_XXX_CMD);
          * BtleConnectionService.this.sendBroadcast(i);
          * ACK_XXX_CMD = the ACK of the command sent over the BLE connection
          * */
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleActionFoo(String param1, String param2) {
        // TODO: Handle action Foo
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * Handle action Baz in the provided background thread with the provided
     * parameters.
     */
    private void handleActionBaz(String param1, String param2) {
        // TODO: Handle action Baz
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
