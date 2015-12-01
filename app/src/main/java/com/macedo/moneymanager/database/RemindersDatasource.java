package com.macedo.moneymanager.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

import com.macedo.moneymanager.models.Reminder;
import com.macedo.moneymanager.utils.DatabaseUtils;

import java.util.ArrayList;

/**
 * Created by Beatriz on 06/09/2015.
 */
public class RemindersDatasource {

    private Context mContext;
    private DatabaseSQLiteHelper mDatabaseSqlLiteHelper;

    public RemindersDatasource(Context context) {
        mContext = context;
        // Lazy Initialization
        mDatabaseSqlLiteHelper = new DatabaseSQLiteHelper(context);

        // Forces the database to create itself
        //SQLiteDatabase database = mDatabaseSqlLiteHelper.getReadableDatabase();
        //database.close();
    }

    private SQLiteDatabase open(){
        return mDatabaseSqlLiteHelper.getWritableDatabase();
    }

    private void close(SQLiteDatabase database){
        database.close();
    }

    public ArrayList<Reminder> read(){
        ArrayList<Reminder> reminders = readReminders();
        return reminders;
    }

    public ArrayList<Reminder> readReminders(){
        SQLiteDatabase database = open();
        ArrayList<Reminder> reminders = new ArrayList<>();

        Cursor cursor = database.query(
                DatabaseSQLiteHelper.REMINDERS_TABLE,
                new String[]{   BaseColumns._ID,
                        DatabaseSQLiteHelper.COLUMN_REMINDER_NAME,
                        DatabaseSQLiteHelper.COLUMN_REMINDER_START_DATE,
                        DatabaseSQLiteHelper.COLUMN_REMINDER_END_DATE,
                        DatabaseSQLiteHelper.COLUMN_REMINDER_NEXT_ALERT_DATE},
                null, // Selection
                null, // Selection args
                null, // Group By
                null, // Having
                DatabaseSQLiteHelper.COLUMN_REMINDER_NAME + " DESC"); // Order

        if(cursor.moveToFirst()){
            do {
                Reminder reminder = new Reminder(DatabaseUtils.getIntFromColumnName(cursor, BaseColumns._ID),
                        DatabaseUtils.getStringFromColumnName(cursor, DatabaseSQLiteHelper.COLUMN_REMINDER_NAME),
                        DatabaseUtils.getDateFromColumnNameInUnixTime(cursor, DatabaseSQLiteHelper.COLUMN_REMINDER_START_DATE),
                        DatabaseUtils.getDateFromColumnNameInUnixTime(cursor, DatabaseSQLiteHelper.COLUMN_REMINDER_END_DATE),
                        DatabaseUtils.getDateFromColumnNameInUnixTime(cursor, DatabaseSQLiteHelper.COLUMN_REMINDER_NEXT_ALERT_DATE));

                reminders.add(reminder);
            } while(cursor.moveToNext());
        }
        cursor.close();
        close(database);
        return reminders;
    }

    public void update(Reminder reminder){
        SQLiteDatabase database = open();
        database.beginTransaction();

        ContentValues updateReminderValues = new ContentValues();
        updateReminderValues.put(DatabaseSQLiteHelper.COLUMN_REMINDER_NAME, reminder.getName());
        updateReminderValues.put(DatabaseSQLiteHelper.COLUMN_REMINDER_START_DATE, reminder.getStartDate().getTime()/1000);
        updateReminderValues.put(DatabaseSQLiteHelper.COLUMN_REMINDER_END_DATE, reminder.getEndDate().getTime()/1000);
        updateReminderValues.put(DatabaseSQLiteHelper.COLUMN_REMINDER_NEXT_ALERT_DATE, reminder.getStartDate().getTime()/1000);

        database.update(DatabaseSQLiteHelper.REMINDERS_TABLE,
                updateReminderValues,
                String.format("%s=%d", BaseColumns._ID, reminder.getId(), null), null);

        database.setTransactionSuccessful();
        database.endTransaction();
        close(database);

    }

    public void delete(int reminderId){
        SQLiteDatabase database = open();
        database.beginTransaction();

        database.delete(DatabaseSQLiteHelper.REMINDERS_TABLE,
                String.format("%s=%s", BaseColumns._ID, String.valueOf(reminderId)), null);

        database.setTransactionSuccessful();
        database.endTransaction();
        close(database);
    }

    public long create(Reminder reminder){
        SQLiteDatabase database = open();
        database.beginTransaction();

        // Implementation details
        ContentValues reminderValues = new ContentValues();
        reminderValues.put(DatabaseSQLiteHelper.COLUMN_REMINDER_NAME, reminder.getName());
        reminderValues.put(DatabaseSQLiteHelper.COLUMN_REMINDER_START_DATE, reminder.getStartDate().getTime()/1000);
        reminderValues.put(DatabaseSQLiteHelper.COLUMN_REMINDER_END_DATE, reminder.getEndDate().getTime()/1000);
        reminderValues.put(DatabaseSQLiteHelper.COLUMN_REMINDER_NEXT_ALERT_DATE, reminder.getNextAlertDate().getTime() / 1000);

        long reminderID = database.insert(DatabaseSQLiteHelper.REMINDERS_TABLE, null, reminderValues);

        database.setTransactionSuccessful();
        database.endTransaction();
        close(database);

        return reminderID;
    }
}
