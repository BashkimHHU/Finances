package de.hhu.droidprog17.finances.controller;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * This static BroadcastReceiver is build to catch system Intents informing about the current
 * battery level, especially about changes from a high to low battery level and vice versa.
 *
 * @author Bashkim Berzati
 * @version 1.0
 */
public class BatteryStatusReceiver extends BroadcastReceiver {

    private static boolean batteryLevelLow;
    private static final String TAG = "Battery_State_Receiver";

    private static void setBatteryLevel(boolean value) {
        batteryLevelLow = value;
    }

    /**
     * Return the current battery status of the device.
     *
     * @return true, if battery status is low and false otherwise.
     */
    public static boolean isBatteryLevelLow() {
        return batteryLevelLow;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String intentAction = intent.getAction();
        if (intentAction.equals("android.intent.action.BATTERY_LOW")) {
            Log.d(TAG, "BATTERY_LOW");
            setBatteryLevel(true);
        } else if (intentAction.equals("android.intent.action.BATTERY_OKAY")) {
            Log.d(TAG, "BATTERY_OKAY");
            setBatteryLevel(false);
        }
    }
}
