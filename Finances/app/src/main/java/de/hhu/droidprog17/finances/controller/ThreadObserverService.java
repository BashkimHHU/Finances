package de.hhu.droidprog17.finances.controller;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

/**
 * This Started Service is responsible for logging all newly created Threads.
 * All newly started Threads locally broadcast a message containing their thread name.
 * This Broadcast is fetched by a dynamical local BroadcastReceiver and finally logged via Logcat
 *
 * @author Bashkim Berzati
 * @version 1.0
 */

public class ThreadObserverService extends Service {

    public static final String SERVICE_BROADCAST_RECEIVER_ACTION = "CURRENT_THREAD";
    public static final String THREAD_NAME_EXTRA_KEY = "THREAD_NAME";
    private static final String TAG = "ThreadService";

    @Override
    public void onCreate() {
        super.onCreate();
        IntentFilter threadObserverServiceIntentFilter =
                new IntentFilter(SERVICE_BROADCAST_RECEIVER_ACTION);
        LocalBroadcastManager.getInstance(this)
                .registerReceiver(mThreadMessageReceiver, threadObserverServiceIntentFilter);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String threadName = intent.getStringExtra(THREAD_NAME_EXTRA_KEY);
        logThreadName(threadName);
        return START_NOT_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        LocalBroadcastManager.getInstance(this)
                .unregisterReceiver(mThreadMessageReceiver);
        super.onDestroy();
    }

    private BroadcastReceiver mThreadMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(SERVICE_BROADCAST_RECEIVER_ACTION)) {
                String threadName = intent.getStringExtra(THREAD_NAME_EXTRA_KEY);
                logThreadName(threadName);
            }
        }
    };

    private void logThreadName(String threadName) {
        Log.i(TAG + "(T)", "THREAD: " + threadName);
    }
}
