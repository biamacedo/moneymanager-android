package com.macedo.moneymanager.notification;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.NotificationCompat;

import com.macedo.moneymanager.R;
import com.macedo.moneymanager.models.Reminder;
import com.macedo.moneymanager.ui.MainActivity;

/**
 * Created by Beatriz on 18/09/2015.
 */
public class NotificationCreator {

    private Context mContext;

    public NotificationCreator(Context context) {
        mContext = context;
    }


    public void createReminderNotification(Reminder reminder) {
        // Prepare intent which is triggered if the
        // notification is selected
        Intent intent = new Intent(mContext, MainActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(mContext, (int) System.currentTimeMillis(), intent, 0);

        // Build notification
        // Actions are just fake
        Notification notification = new NotificationCompat.Builder(mContext)
                .setContentTitle("Bill Payment Today!")
                .setContentText(reminder.getName()).setSmallIcon(R.drawable.ic_stat_exclamation_circle)
                .setContentIntent(pIntent)
                .addAction(R.drawable.ic_pencil_square, "Edit Reminder", pIntent)
                //.addAction(R.drawable.ic_times_circle, "Dismiss", pIntent)
                //.addAction(R.drawable.ic_bars, "More", pIntent)
                //.addAction(R.drawable.ic_angle_down, "And more", pIntent)
                .build();
        NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Activity.NOTIFICATION_SERVICE);
        // hide the notification after its selected
        notification.flags |= Notification.FLAG_AUTO_CANCEL;

        notificationManager.notify(0, notification);

    }

}
