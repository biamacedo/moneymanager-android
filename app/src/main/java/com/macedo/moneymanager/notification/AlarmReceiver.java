package com.macedo.moneymanager.notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.macedo.moneymanager.models.Reminder;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by Beatriz on 18/09/2015.
 */
public class AlarmReceiver extends BroadcastReceiver {

    private static final String TAG = AlarmReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        Reminder reminder = intent.getParcelableExtra(Reminder.REMINDER_EXTRA);

        Log.d(TAG, reminder.getId() + " - " + reminder.getName() + " Alarm Received!");
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        Log.d(TAG, "Received alarm for: " + cal.getTime().toString());

        NotificationCreator creator = new NotificationCreator(context);
        creator.createReminderNotification(reminder);

        // Setting next alarm date on database and start new alarm
        BillAlarmManager alarmManager = new BillAlarmManager(context);
        alarmManager.setNextAlarmDate(reminder);

    }
}
