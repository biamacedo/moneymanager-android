package com.macedo.moneymanager.notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Beatriz on 18/09/2015.
 */
public class AlarmRestarter extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        BillAlarmManager alarmManager = new BillAlarmManager(context);

        alarmManager.restartAllAlarms();
    }
}
