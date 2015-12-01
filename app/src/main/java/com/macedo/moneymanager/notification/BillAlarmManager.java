package com.macedo.moneymanager.notification;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.macedo.moneymanager.database.RemindersDatasource;
import com.macedo.moneymanager.models.Reminder;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Beatriz on 18/09/2015.
 */
public class BillAlarmManager {

    private static final String TAG = BillAlarmManager.class.getSimpleName();

    private ArrayList<Reminder> mReminders;
    private Context mContext;
    private RemindersDatasource mRemindersDatasource;

    public BillAlarmManager(Context context) {
        mReminders = new ArrayList<>();
        mContext = context;
        mRemindersDatasource = new RemindersDatasource(context);
    }

    public void retrieveAlarmList(){
        mReminders = mRemindersDatasource.readReminders();
    }

    public void startAlarm(Reminder reminder){
        AlarmManager alarmService = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(mContext, AlarmReceiver.class);
        intent.putExtra(Reminder.REMINDER_EXTRA, reminder);

        PendingIntent pIntent = PendingIntent.getBroadcast(mContext, reminder.getId(), intent, PendingIntent.FLAG_CANCEL_CURRENT);

        Date today = new Date();
        Calendar calToday = Calendar.getInstance();
        calToday.setTime(today);
        calToday.set(Calendar.HOUR_OF_DAY, 23);
        calToday.set(Calendar.MINUTE, 59);
        calToday.set(Calendar.SECOND, 59);

        Calendar cal = Calendar.getInstance();
        cal.setTime(reminder.getNextAlertDate());
        if (reminder.getNextAlertDate().getTime() < calToday.getTimeInMillis()) {
            // Start 30 seconds after boot completed
            calToday.setTime(today);
            cal.set(Calendar.MINUTE, calToday.get(Calendar.MINUTE)+20);
            cal.set(Calendar.SECOND, 0);
        } else {
            cal.set(Calendar.HOUR_OF_DAY, 10);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
        }

        Log.d(TAG, "Starting alarm for: " + cal.getTime().toString());

        // Fetch every 30 seconds
        // InexactRepeating allows Android to optimize the energy consumption
        alarmService.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pIntent);

        Log.d(TAG, reminder.getId() + " - " + reminder.getName() +  " Alarm Started!");
    }

    public void removeAlarm(Reminder reminder){
        AlarmManager alarmService = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(mContext, AlarmReceiver.class);
        PendingIntent pIntent = PendingIntent.getBroadcast(mContext, reminder.getId(), intent, PendingIntent.FLAG_CANCEL_CURRENT);

        alarmService.cancel(pIntent);

        Log.d(TAG, reminder.getId() + " - " + reminder.getName() + " Alarm Removed!");
    }

    public void restartAllAlarms(){
        Log.d(TAG, "Restarting all alarms!");
        retrieveAlarmList();

        Date today = new Date();
        for (Reminder reminder : mReminders) {
            // Only set alarms for reminders that did not pass their end date and end date is not 0/null
            if (reminder.getEndDate().getTime() != 0 && reminder.getEndDate().getTime() < today.getTime()) {
                Log.d(TAG, reminder.getId() + " - " + reminder.getName() + " Alarm Removed!");
                startAlarm(reminder);
            }
        }
    }

    public void setNextAlarmDate(Reminder reminder){

        Calendar cal = Calendar.getInstance();

        cal.setTime(reminder.getNextAlertDate());
        // Setting Alarm to next month
        cal.add(Calendar.MONTH, 1);

        reminder.setNextAlertDate(cal.getTime());

        mRemindersDatasource.update(reminder);

        startAlarm(reminder);
    }
}
