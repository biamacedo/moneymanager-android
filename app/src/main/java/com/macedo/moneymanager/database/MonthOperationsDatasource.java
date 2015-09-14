package com.macedo.moneymanager.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.macedo.moneymanager.models.Operation;
import com.macedo.moneymanager.models.MonthOperation;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by Beatriz on 06/09/2015.
 */
public class MonthOperationsDatasource {

    public static final String TAG = MonthOperationsDatasource.class.getSimpleName();

    public static final String DB_DATE_FORMAT = "yyyy-MM-dd";
    public static final String MONTH_NAME_FORMAT = "MMMM";

    private Context mContext;
    private DatabaseSQLiteHelper mDatabaseSqlLiteHelper;

    public SimpleDateFormat mMonthNameFormatter = new SimpleDateFormat(MONTH_NAME_FORMAT);

    public MonthOperationsDatasource(Context context) {
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

    public ArrayList<MonthOperation> readMonthExpense(int year){
        ArrayList<MonthOperation> monthOperations = new ArrayList<MonthOperation>();

        for(int month = 0; month < 12; month++) {
            Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
            cal.set(Calendar.YEAR, year);
            cal.set(Calendar.MONTH, month);
            cal.set(Calendar.DATE, 1);
            cal.set(Calendar.HOUR_OF_DAY, 0);
            Date firstDateOfMonth = cal.getTime();

            cal.set(Calendar.DATE, cal.getActualMaximum(Calendar.DATE));
            cal.set(Calendar.HOUR_OF_DAY, cal.getActualMaximum(Calendar.HOUR_OF_DAY));

            Date lastDateOfMonth = cal.getTime();

            String monthName = mMonthNameFormatter.format(cal.getTime());
            Float amount = sumAllExpensesInPeriod(firstDateOfMonth, lastDateOfMonth);
            Float winAmount = sumAllPositiveAmountInPeriod(firstDateOfMonth, lastDateOfMonth);
            Float lossAmount = sumAllNegativeAmountInPeriod(firstDateOfMonth, lastDateOfMonth);

            Float percentage = 0.00f;
            if (winAmount > 0.00) {
                percentage = (-lossAmount / winAmount) * 100;
            }

            MonthOperation newMonth = new MonthOperation(monthName, amount, percentage, winAmount, lossAmount);

            monthOperations.add(newMonth);
        }

        return monthOperations;
    }

    public MonthOperation readYearExpense(int year){
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, 1);
        cal.set(Calendar.DATE, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        Date firstDateOfYear= cal.getTime();

        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, 12);
        cal.set(Calendar.DATE, 31);
        cal.set(Calendar.HOUR_OF_DAY, cal.getActualMaximum(Calendar.HOUR_OF_DAY));

        Date lastDateOfYear = cal.getTime();

        String monthName = mMonthNameFormatter.format(cal.getTime());
        Float amount = sumAllExpensesInPeriod(firstDateOfYear, lastDateOfYear);
        Float winAmount = sumAllPositiveAmountInPeriod(firstDateOfYear, lastDateOfYear);
        Float lossAmount = sumAllNegativeAmountInPeriod(firstDateOfYear, lastDateOfYear);

        Float percentage = 0.00f;
        if (winAmount > 0.00) {
            percentage = (-lossAmount / winAmount) * 100;
        }

        MonthOperation yearTotals = new MonthOperation(monthName, amount, percentage, winAmount, lossAmount);

        return yearTotals;
    }

    public Float sumAllExpenses(){
        SQLiteDatabase database = open();
        ArrayList<Operation> operations = new ArrayList<Operation>();
        Float amount = 0.00f;

        Cursor cursor = database.rawQuery(
                "SELECT SUM(T." + DatabaseSQLiteHelper.COLUMN_OPERATION_AMOUNT + ") FROM " + DatabaseSQLiteHelper.OPERATIONS_TABLE +" T", null);

        if(cursor.moveToFirst()){
            do {
                amount = cursor.getFloat(0);
            } while(cursor.moveToNext());
        }
        cursor.close();
        close(database);
        return amount;
    }

    public Float sumAllExpensesInPeriod(Date fromDate, Date toDate){
        SQLiteDatabase database = open();
        ArrayList<Operation> operations = new ArrayList<Operation>();
        Float amount = 0.00f;

        long fromUnixTime = fromDate.getTime()/1000;
        long toUnixTime = toDate.getTime()/1000;

        Cursor cursor = database.rawQuery("SELECT SUM(T." + DatabaseSQLiteHelper.COLUMN_OPERATION_AMOUNT + ") FROM " + DatabaseSQLiteHelper.OPERATIONS_TABLE + " T " +
                " WHERE T." + DatabaseSQLiteHelper.COLUMN_OPERATION_DATE + " BETWEEN " + fromUnixTime + " AND " + toUnixTime, null);

        if(cursor.moveToFirst()){
            do {
                amount = cursor.getFloat(0);
            } while(cursor.moveToNext());
        }
        cursor.close();
        close(database);
        return amount;
    }

    public Float sumAllPositiveAmountInPeriod(Date fromDate, Date toDate){
        SQLiteDatabase database = open();
        ArrayList<Operation> operations = new ArrayList<Operation>();
        Float amount = 0.00f;

        long fromUnixTime = fromDate.getTime()/1000;
        long toUnixTime = toDate.getTime()/1000;

        Cursor cursor = database.rawQuery(
                "SELECT SUM(" + DatabaseSQLiteHelper.COLUMN_OPERATION_AMOUNT + ") FROM " + DatabaseSQLiteHelper.OPERATIONS_TABLE +
                        " WHERE " + DatabaseSQLiteHelper.COLUMN_OPERATION_AMOUNT + " > 0 " +
                " AND " + DatabaseSQLiteHelper.COLUMN_OPERATION_DATE + " BETWEEN " + fromUnixTime + " AND " + toUnixTime, null);

        if(cursor.moveToFirst()){
            do {
                amount = cursor.getFloat(0);
            } while(cursor.moveToNext());
        }
        cursor.close();
        close(database);
        return amount;
    }

    public Float sumAllNegativeAmountInPeriod(Date fromDate, Date toDate){
        SQLiteDatabase database = open();
        ArrayList<Operation> operations = new ArrayList<Operation>();
        Float amount = 0.00f;

        long fromUnixTime = fromDate.getTime()/1000;
        long toUnixTime = toDate.getTime()/1000;

        Cursor cursor = database.rawQuery(
                "SELECT SUM(" + DatabaseSQLiteHelper.COLUMN_OPERATION_AMOUNT + ") FROM " + DatabaseSQLiteHelper.OPERATIONS_TABLE +
                        " WHERE " + DatabaseSQLiteHelper.COLUMN_OPERATION_AMOUNT + " < 0 " +
                        " AND " + DatabaseSQLiteHelper.COLUMN_OPERATION_DATE + " BETWEEN " + fromUnixTime + " AND " + toUnixTime, null);

        if(cursor.moveToFirst()){
            do {
                amount = cursor.getFloat(0);
            } while(cursor.moveToNext());
        }
        cursor.close();
        close(database);
        return amount;
    }
}
