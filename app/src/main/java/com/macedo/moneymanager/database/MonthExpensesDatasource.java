package com.macedo.moneymanager.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.macedo.moneymanager.models.Expense;
import com.macedo.moneymanager.models.MonthExpense;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Beatriz on 06/09/2015.
 */
public class MonthExpensesDatasource {

    public static final String TAG = MonthExpensesDatasource.class.getSimpleName();

    public static final String DB_DATE_FORMAT = "yyyy-MM-dd";
    public static final String MONTH_NAME_FORMAT = "MMMM";

    private Context mContext;
    private DatabaseSQLiteHelper mDatabaseSqlLiteHelper;

    public SimpleDateFormat mMonthNameFormatter = new SimpleDateFormat(MONTH_NAME_FORMAT);

    public MonthExpensesDatasource(Context context) {
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

    public ArrayList<MonthExpense> readMonthExpense(int year){
        ArrayList<MonthExpense> monthExpenses = new ArrayList<MonthExpense>();

        for(int month = 0; month < 12; month++) {
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.YEAR, year);
            cal.set(Calendar.MONTH, month);
            cal.set(Calendar.DATE, 1);
            Date firstDateOfMonth = cal.getTime();

            cal.set(Calendar.DATE, cal.getActualMaximum(Calendar.DATE));

            Date lastDateOfMonth = cal.getTime();

            String monthName = mMonthNameFormatter.format(cal.getTime());
            Double amount = sumAllExpensesInPeriod(firstDateOfMonth, lastDateOfMonth);
            Double winAmount = sumAllPositiveAmountInPeriod(firstDateOfMonth, lastDateOfMonth);
            Double lossAmount = sumAllNegativeAmountInPeriod(firstDateOfMonth, lastDateOfMonth);

            Double percentage = 0.00;
            if (winAmount > 0.00) {
                percentage = (-lossAmount / winAmount) * 100;
            }

            MonthExpense newMonth = new MonthExpense(monthName, amount, percentage, winAmount, lossAmount);

            monthExpenses.add(newMonth);
        }

        return monthExpenses;
    }

    public MonthExpense readYearExpense(int year){
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, 1);
        cal.set(Calendar.DATE, 1);
        Date firstDateOfYear= cal.getTime();

        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, 12);
        cal.set(Calendar.DATE, 31);

        Date lastDateOfYear = cal.getTime();

        String monthName = mMonthNameFormatter.format(cal.getTime());
        Double amount = sumAllExpensesInPeriod(firstDateOfYear, lastDateOfYear);
        Double winAmount = sumAllPositiveAmountInPeriod(firstDateOfYear, lastDateOfYear);
        Double lossAmount = sumAllNegativeAmountInPeriod(firstDateOfYear, lastDateOfYear);

        Double percentage = 0.00;
        if (winAmount > 0.00) {
            percentage = (-lossAmount / winAmount) * 100;
        }

        MonthExpense yearTotals = new MonthExpense(monthName, amount, percentage, winAmount, lossAmount);

        return yearTotals;
    }

    public Double sumAllExpenses(){
        SQLiteDatabase database = open();
        ArrayList<Expense> expenses = new ArrayList<Expense>();
        Double amount = 0.00;

        Cursor cursor = database.rawQuery(
                "SELECT SUM(" + DatabaseSQLiteHelper.COLUMN_EXPENSE_AMOUNT + ") FROM " + DatabaseSQLiteHelper.EXPENSES_TABLE, null);

        if(cursor.moveToFirst()){
            do {
                amount = cursor.getDouble(0);
            } while(cursor.moveToNext());
        }
        cursor.close();
        close(database);
        return amount;
    }

    public Double sumAllExpensesInPeriod(Date fromDate, Date toDate){
        SQLiteDatabase database = open();
        ArrayList<Expense> expenses = new ArrayList<Expense>();
        Double amount = 0.00;

        long fromUnixTime = fromDate.getTime()/1000;
        long toUnixTime = toDate.getTime()/1000;

        Cursor cursor = database.rawQuery(
                "SELECT SUM(" + DatabaseSQLiteHelper.COLUMN_EXPENSE_AMOUNT + ") FROM " + DatabaseSQLiteHelper.EXPENSES_TABLE +
                " WHERE " + DatabaseSQLiteHelper.COLUMN_EXPENSE_DATE + " BETWEEN " + fromUnixTime + " AND " + toUnixTime, null);

        if(cursor.moveToFirst()){
            do {
                amount = cursor.getDouble(0);
            } while(cursor.moveToNext());
        }
        cursor.close();
        close(database);
        return amount;
    }

    public Double sumAllPositiveAmountInPeriod(Date fromDate, Date toDate){
        SQLiteDatabase database = open();
        ArrayList<Expense> expenses = new ArrayList<Expense>();
        Double amount = 0.00;

        long fromUnixTime = fromDate.getTime()/1000;
        long toUnixTime = toDate.getTime()/1000;

        Cursor cursor = database.rawQuery(
                "SELECT SUM(" + DatabaseSQLiteHelper.COLUMN_EXPENSE_AMOUNT + ") FROM " + DatabaseSQLiteHelper.EXPENSES_TABLE +
                        " WHERE " + DatabaseSQLiteHelper.COLUMN_EXPENSE_AMOUNT + " > 0 " +
                " AND " + DatabaseSQLiteHelper.COLUMN_EXPENSE_DATE + " BETWEEN " + fromUnixTime + " AND " + toUnixTime, null);

        if(cursor.moveToFirst()){
            do {
                amount = cursor.getDouble(0);
            } while(cursor.moveToNext());
        }
        cursor.close();
        close(database);
        return amount;
    }

    public Double sumAllNegativeAmountInPeriod(Date fromDate, Date toDate){
        SQLiteDatabase database = open();
        ArrayList<Expense> expenses = new ArrayList<Expense>();
        Double amount = 0.00;

        long fromUnixTime = fromDate.getTime()/1000;
        long toUnixTime = toDate.getTime()/1000;

        Cursor cursor = database.rawQuery(
                "SELECT SUM(" + DatabaseSQLiteHelper.COLUMN_EXPENSE_AMOUNT + ") FROM " + DatabaseSQLiteHelper.EXPENSES_TABLE +
                        " WHERE " + DatabaseSQLiteHelper.COLUMN_EXPENSE_AMOUNT + " < 0 " +
                        " AND " + DatabaseSQLiteHelper.COLUMN_EXPENSE_DATE + " BETWEEN " + fromUnixTime + " AND " + toUnixTime, null);

        if(cursor.moveToFirst()){
            do {
                amount = cursor.getDouble(0);
            } while(cursor.moveToNext());
        }
        cursor.close();
        close(database);
        return amount;
    }
}
