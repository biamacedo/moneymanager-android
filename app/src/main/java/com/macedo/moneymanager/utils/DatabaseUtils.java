package com.macedo.moneymanager.utils;

import android.database.Cursor;

import java.util.Date;

/**
 * Created by Beatriz on 06/09/2015.
 */
public class DatabaseUtils {

    public static final String TAG = DatabaseUtils.class.getSimpleName();

    public static int getIntFromColumnName(Cursor cursor, String columnName){
        int columnIndex = cursor.getColumnIndex(columnName);
        return cursor.getInt(columnIndex);
    }

    public static String getStringFromColumnName(Cursor cursor, String columnName){
        int columnIndex = cursor.getColumnIndex(columnName);
        return cursor.getString(columnIndex);
    }

    public static double getDoubleFromColumnName(Cursor cursor, String columnName){
        int columnIndex = cursor.getColumnIndex(columnName);
        return cursor.getDouble(columnIndex);
    }

    public static Date getDateFromColumnNameInUnixTime(Cursor cursor, String columnName){
        int columnIndex = cursor.getColumnIndex(columnName);
        int unixTime = cursor.getInt(columnIndex);
        return new Date((long) unixTime*1000);
    }

}
