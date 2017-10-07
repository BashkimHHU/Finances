package de.hhu.droidprog17.finances.controller;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import de.hhu.droidprog17.finances.R;

/**
 * This Bound Service is responsible for creating user feedback.
 * Each Activity/Fragment that creates a user feedback will bind to this Service.
 *
 * @author Bashkim Berzati
 * @version 1.0
 */

public class UserFeedbackService extends Service {

    private final IBinder mBinder = new LocalBinder();

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    /**
     * Creates a Notification
     *
     * @param title title of the notification
     * @param text  text of the notification
     */
    public void showNotification(String title, String text) {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_money_off_black_24dp)
                .setContentTitle(title)
                .setContentText(text);

        int mNotificationID = 999;
        NotificationManager nManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        nManager.notify(mNotificationID, mBuilder.build());
    }

    /**
     * Closes the currently display notification, if available
     */
    public void cancelNotification() {
        String service = Context.NOTIFICATION_SERVICE;
        NotificationManager notificationManager = (NotificationManager) getSystemService(service);
        notificationManager.cancel(999);
    }

    /**
     * Displays a Toast
     *
     * @param message toast message
     */
    public void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    /**
     * Returns the binder object which is necessary for communication
     */
    public class LocalBinder extends Binder {
        public UserFeedbackService getService() {
            return UserFeedbackService.this;
        }
    }
}
