package com.macedo.moneymanager.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

import com.macedo.moneymanager.models.Category;
import com.macedo.moneymanager.models.Expense;
import com.macedo.moneymanager.utils.DatabaseUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Beatriz on 06/09/2015.
 */
public class ExpensesDatasource {

    public static final String TAG = ExpensesDatasource.class.getSimpleName();

    public static final String DB_DATE_FORMAT = "yyyy-MM-dd";

    private Context mContext;
    private DatabaseSQLiteHelper mDatabaseSqlLiteHelper;

    public SimpleDateFormat mDateFormatter = new SimpleDateFormat(DB_DATE_FORMAT);

    public ExpensesDatasource(Context context) {
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

    public ArrayList<Expense> read(){
        ArrayList<Expense> expenses = readExpenses();
        addCategoryInformation(expenses);
        return expenses;
    }

    public ArrayList<Expense> readExpenses(){
        SQLiteDatabase database = open();
        ArrayList<Expense> expenses = new ArrayList<Expense>();

        Cursor cursor = database.query(
                DatabaseSQLiteHelper.EXPENSES_TABLE,
                new String[]{   BaseColumns._ID,
                        DatabaseSQLiteHelper.COLUMN_EXPENSE_TITLE,
                        DatabaseSQLiteHelper.COLUMN_EXPENSE_DESC,
                        DatabaseSQLiteHelper.COLUMN_FOREIGN_KEY_CATEGORY,
                        DatabaseSQLiteHelper.COLUMN_EXPENSE_AMOUNT,
                        DatabaseSQLiteHelper.COLUMN_EXPENSE_DATE},
                null, // Selection
                null, // Selection args
                null, // Group By
                null, // Having
                DatabaseSQLiteHelper.COLUMN_EXPENSE_DATE + " DESC"); // Order

        if(cursor.moveToFirst()){
            do {
                Expense expense = new Expense(DatabaseUtils.getIntFromColumnName(cursor, BaseColumns._ID),
                        DatabaseUtils.getStringFromColumnName(cursor, DatabaseSQLiteHelper.COLUMN_EXPENSE_TITLE),
                        DatabaseUtils.getStringFromColumnName(cursor, DatabaseSQLiteHelper.COLUMN_EXPENSE_DESC),
                        new Category(DatabaseUtils.getIntFromColumnName(cursor, DatabaseSQLiteHelper.COLUMN_FOREIGN_KEY_CATEGORY)),
                        DatabaseUtils.getDoubleFromColumnName(cursor, DatabaseSQLiteHelper.COLUMN_EXPENSE_AMOUNT),
                        DatabaseUtils.getDateFromColumnNameInUnixTime(cursor, DatabaseSQLiteHelper.COLUMN_EXPENSE_DATE));

                expenses.add(expense);
            } while(cursor.moveToNext());
        }
        cursor.close();
        close(database);
        return expenses;
    }

    public void addCategoryInformation(ArrayList<Expense> expenses){
        SQLiteDatabase database = open();
        for (Expense expense : expenses) {
            Cursor cursor = database.rawQuery(
                    "SELECT * FROM " + DatabaseSQLiteHelper.CATEGORIES_TABLE +
                            " WHERE " + BaseColumns._ID +" = " + expense.getCategory().getId(), null);
            if(cursor.moveToFirst()){
                do{
                    expense.getCategory().setId(DatabaseUtils.getIntFromColumnName(cursor, BaseColumns._ID));
                    expense.getCategory().setName(DatabaseUtils.getStringFromColumnName(cursor, DatabaseSQLiteHelper.COLUMN_CATEGORY_NAME));
                    expense.getCategory().setType(DatabaseUtils.getStringFromColumnName(cursor, DatabaseSQLiteHelper.COLUMN_CATEGORY_TYPE));
                    expense.getCategory().setIconName(DatabaseUtils.getStringFromColumnName(cursor, DatabaseSQLiteHelper.COLUMN_CATEGORY_ICON_NAME));
                }while (cursor.moveToNext());
            }
            cursor.close();
        }
        database.close();
    }

    public void update(Expense expense){
        SQLiteDatabase database = open();
        database.beginTransaction();

        ContentValues updateExpenseValues = new ContentValues();
        updateExpenseValues.put(DatabaseSQLiteHelper.COLUMN_EXPENSE_TITLE, expense.getTitle());
        updateExpenseValues.put(DatabaseSQLiteHelper.COLUMN_EXPENSE_DESC, expense.getDescription());
        updateExpenseValues.put(DatabaseSQLiteHelper.COLUMN_FOREIGN_KEY_CATEGORY, expense.getCategory().getId());
        updateExpenseValues.put(DatabaseSQLiteHelper.COLUMN_EXPENSE_AMOUNT, expense.getAmount());
        updateExpenseValues.put(DatabaseSQLiteHelper.COLUMN_EXPENSE_DATE, expense.getDate().getTime()/1000);

        database.update(DatabaseSQLiteHelper.EXPENSES_TABLE,
                updateExpenseValues,
                String.format("%s=%d", BaseColumns._ID, expense.getId(), null), null);

        database.setTransactionSuccessful();
        database.endTransaction();
        close(database);

    }

    public void delete(int expenseId){
        SQLiteDatabase database = open();
        database.beginTransaction();

        database.delete(DatabaseSQLiteHelper.EXPENSES_TABLE,
                String.format("%s=%s", BaseColumns._ID, String.valueOf(expenseId)), null);

        database.setTransactionSuccessful();
        database.endTransaction();
        close(database);
    }

    public void create(Expense expense){
        SQLiteDatabase database = open();
        database.beginTransaction();

        // Implementation details
        ContentValues expenseValues = new ContentValues();
        expenseValues.put(DatabaseSQLiteHelper.COLUMN_EXPENSE_TITLE, expense.getTitle());
        expenseValues.put(DatabaseSQLiteHelper.COLUMN_EXPENSE_DESC, expense.getDescription());
        expenseValues.put(DatabaseSQLiteHelper.COLUMN_FOREIGN_KEY_CATEGORY, expense.getCategory().getId());
        expenseValues.put(DatabaseSQLiteHelper.COLUMN_EXPENSE_AMOUNT, expense.getAmount());
        // Date input on database as Unix Time
        expenseValues.put(DatabaseSQLiteHelper.COLUMN_EXPENSE_DATE, expense.getDate().getTime() / 1000);
        expenseValues.put(DatabaseSQLiteHelper.COLUMN_EXPENSE_CREATE_DATE, new Date().getTime() / 1000);

        long expenseID = database.insert(DatabaseSQLiteHelper.EXPENSES_TABLE, null, expenseValues);

        database.setTransactionSuccessful();
        database.endTransaction();
        close(database);

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

        String fromDateString = mDateFormatter.format(fromDate);
        String toDateString = mDateFormatter.format(toDate);

        Cursor cursor = database.rawQuery(
                "SELECT SUM(" + DatabaseSQLiteHelper.COLUMN_EXPENSE_AMOUNT + ") FROM " + DatabaseSQLiteHelper.EXPENSES_TABLE +
                "WHERE " + DatabaseSQLiteHelper.COLUMN_EXPENSE_DATE + " BETWEEN '" + fromDateString + "' AND '" + toDateString + "' ", null);

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

        String fromDateString = mDateFormatter.format(fromDate);
        String toDateString = mDateFormatter.format(toDate);

        Cursor cursor = database.rawQuery(
                "SELECT SUM(" + DatabaseSQLiteHelper.COLUMN_EXPENSE_AMOUNT + ") FROM " + DatabaseSQLiteHelper.EXPENSES_TABLE +
                        " WHERE " + DatabaseSQLiteHelper.COLUMN_EXPENSE_AMOUNT + " > 0 " +
                "AND " + DatabaseSQLiteHelper.COLUMN_EXPENSE_DATE + " BETWEEN '" + fromDateString + "' AND '" + toDateString + "' ", null);

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

        String fromDateString = mDateFormatter.format(fromDate);
        String toDateString = mDateFormatter.format(toDate);

        Cursor cursor = database.rawQuery(
                "SELECT SUM(" + DatabaseSQLiteHelper.COLUMN_EXPENSE_AMOUNT + ") FROM " + DatabaseSQLiteHelper.EXPENSES_TABLE +
                        " WHERE " + DatabaseSQLiteHelper.COLUMN_EXPENSE_AMOUNT + " < 0 " +
                        "AND " + DatabaseSQLiteHelper.COLUMN_EXPENSE_DATE + " BETWEEN '" + fromDateString + "' AND '" + toDateString + "' ", null);

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
