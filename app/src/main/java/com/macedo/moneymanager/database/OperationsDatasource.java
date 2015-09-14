package com.macedo.moneymanager.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

import com.macedo.moneymanager.models.Category;
import com.macedo.moneymanager.models.Operation;
import com.macedo.moneymanager.utils.DatabaseUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Beatriz on 06/09/2015.
 */
public class OperationsDatasource {

    public static final String TAG = OperationsDatasource.class.getSimpleName();

    public static final String DB_DATE_FORMAT = "yyyy-MM-dd";

    private Context mContext;
    private DatabaseSQLiteHelper mDatabaseSqlLiteHelper;

    public SimpleDateFormat mDateFormatter = new SimpleDateFormat(DB_DATE_FORMAT);

    public OperationsDatasource(Context context) {
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

    public ArrayList<Operation> read(){
        ArrayList<Operation> operations = readExpenses();
        addCategoryInformation(operations);
        return operations;
    }

    public ArrayList<Operation> readExpenses(){
        SQLiteDatabase database = open();
        ArrayList<Operation> operations = new ArrayList<Operation>();

        Cursor cursor = database.query(
                DatabaseSQLiteHelper.OPERATIONS_TABLE,
                new String[]{   BaseColumns._ID,
                        DatabaseSQLiteHelper.COLUMN_OPERATION_TITLE,
                        DatabaseSQLiteHelper.COLUMN_OPERATION_DESC,
                        DatabaseSQLiteHelper.COLUMN_FOREIGN_KEY_CATEGORY,
                        DatabaseSQLiteHelper.COLUMN_OPERATION_AMOUNT,
                        DatabaseSQLiteHelper.COLUMN_OPERATION_DATE},
                null, // Selection
                null, // Selection args
                null, // Group By
                null, // Having
                DatabaseSQLiteHelper.COLUMN_OPERATION_DATE + " DESC"); // Order

        if(cursor.moveToFirst()){
            do {
                Operation operation = new Operation(DatabaseUtils.getIntFromColumnName(cursor, BaseColumns._ID),
                        DatabaseUtils.getStringFromColumnName(cursor, DatabaseSQLiteHelper.COLUMN_OPERATION_TITLE),
                        DatabaseUtils.getStringFromColumnName(cursor, DatabaseSQLiteHelper.COLUMN_OPERATION_DESC),
                        new Category(DatabaseUtils.getIntFromColumnName(cursor, DatabaseSQLiteHelper.COLUMN_FOREIGN_KEY_CATEGORY)),
                        DatabaseUtils.getFloatFromColumnName(cursor, DatabaseSQLiteHelper.COLUMN_OPERATION_AMOUNT),
                        DatabaseUtils.getDateFromColumnNameInUnixTime(cursor, DatabaseSQLiteHelper.COLUMN_OPERATION_DATE));

                operations.add(operation);
            } while(cursor.moveToNext());
        }
        cursor.close();
        close(database);
        return operations;
    }

    public void addCategoryInformation(ArrayList<Operation> operations){
        SQLiteDatabase database = open();
        for (Operation operation : operations) {
            Cursor cursor = database.rawQuery(
                    "SELECT * FROM " + DatabaseSQLiteHelper.CATEGORIES_TABLE +
                            " WHERE " + BaseColumns._ID +" = " + operation.getCategory().getId(), null);
            if(cursor.moveToFirst()){
                do{
                    operation.getCategory().setId(DatabaseUtils.getIntFromColumnName(cursor, BaseColumns._ID));
                    operation.getCategory().setName(DatabaseUtils.getStringFromColumnName(cursor, DatabaseSQLiteHelper.COLUMN_CATEGORY_NAME));
                    operation.getCategory().setType(DatabaseUtils.getStringFromColumnName(cursor, DatabaseSQLiteHelper.COLUMN_CATEGORY_TYPE));
                    operation.getCategory().setIconName(DatabaseUtils.getStringFromColumnName(cursor, DatabaseSQLiteHelper.COLUMN_CATEGORY_ICON_NAME));
                }while (cursor.moveToNext());
            }
            cursor.close();
        }
        database.close();
    }

    public void update(Operation operation){
        SQLiteDatabase database = open();
        database.beginTransaction();

        ContentValues updateExpenseValues = new ContentValues();
        updateExpenseValues.put(DatabaseSQLiteHelper.COLUMN_OPERATION_TITLE, operation.getTitle());
        updateExpenseValues.put(DatabaseSQLiteHelper.COLUMN_OPERATION_DESC, operation.getDescription());
        updateExpenseValues.put(DatabaseSQLiteHelper.COLUMN_FOREIGN_KEY_CATEGORY, operation.getCategory().getId());
        updateExpenseValues.put(DatabaseSQLiteHelper.COLUMN_OPERATION_AMOUNT, operation.getAmount());
        updateExpenseValues.put(DatabaseSQLiteHelper.COLUMN_OPERATION_DATE, operation.getDate().getTime()/1000);

        database.update(DatabaseSQLiteHelper.OPERATIONS_TABLE,
                updateExpenseValues,
                String.format("%s=%d", BaseColumns._ID, operation.getId(), null), null);

        database.setTransactionSuccessful();
        database.endTransaction();
        close(database);

    }

    public void delete(int expenseId){
        SQLiteDatabase database = open();
        database.beginTransaction();

        database.delete(DatabaseSQLiteHelper.OPERATIONS_TABLE,
                String.format("%s=%s", BaseColumns._ID, String.valueOf(expenseId)), null);

        database.setTransactionSuccessful();
        database.endTransaction();
        close(database);
    }

    public void create(Operation operation){
        SQLiteDatabase database = open();
        database.beginTransaction();

        // Implementation details
        ContentValues expenseValues = new ContentValues();
        expenseValues.put(DatabaseSQLiteHelper.COLUMN_OPERATION_TITLE, operation.getTitle());
        expenseValues.put(DatabaseSQLiteHelper.COLUMN_OPERATION_DESC, operation.getDescription());
        expenseValues.put(DatabaseSQLiteHelper.COLUMN_FOREIGN_KEY_CATEGORY, operation.getCategory().getId());
        expenseValues.put(DatabaseSQLiteHelper.COLUMN_OPERATION_AMOUNT, operation.getAmount());
        // Date input on database as Unix Time
        expenseValues.put(DatabaseSQLiteHelper.COLUMN_OPERATION_DATE, operation.getDate().getTime() / 1000);
        expenseValues.put(DatabaseSQLiteHelper.COLUMN_OPERATION_CREATE_DATE, new Date().getTime() / 1000);

        long expenseID = database.insert(DatabaseSQLiteHelper.OPERATIONS_TABLE, null, expenseValues);

        database.setTransactionSuccessful();
        database.endTransaction();
        close(database);

    }

    public Float sumAllExpenses(){
        SQLiteDatabase database = open();
        ArrayList<Operation> operations = new ArrayList<Operation>();
        Float amount = 0.00f;

        Cursor cursor = database.rawQuery(
                "SELECT SUM(" + DatabaseSQLiteHelper.COLUMN_OPERATION_AMOUNT + ") FROM " + DatabaseSQLiteHelper.OPERATIONS_TABLE, null);

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

        String fromDateString = mDateFormatter.format(fromDate);
        String toDateString = mDateFormatter.format(toDate);

        Cursor cursor = database.rawQuery(
                "SELECT SUM(" + DatabaseSQLiteHelper.COLUMN_OPERATION_AMOUNT + ") FROM " + DatabaseSQLiteHelper.OPERATIONS_TABLE +
                "WHERE " + DatabaseSQLiteHelper.COLUMN_OPERATION_DATE + " BETWEEN '" + fromDateString + "' AND '" + toDateString + "' ", null);

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

        String fromDateString = mDateFormatter.format(fromDate);
        String toDateString = mDateFormatter.format(toDate);

        Cursor cursor = database.rawQuery(
                "SELECT SUM(" + DatabaseSQLiteHelper.COLUMN_OPERATION_AMOUNT + ") FROM " + DatabaseSQLiteHelper.OPERATIONS_TABLE +
                        " WHERE " + DatabaseSQLiteHelper.COLUMN_OPERATION_AMOUNT + " > 0 " +
                "AND " + DatabaseSQLiteHelper.COLUMN_OPERATION_DATE + " BETWEEN '" + fromDateString + "' AND '" + toDateString + "' ", null);

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

        String fromDateString = mDateFormatter.format(fromDate);
        String toDateString = mDateFormatter.format(toDate);

        Cursor cursor = database.rawQuery(
                "SELECT SUM(" + DatabaseSQLiteHelper.COLUMN_OPERATION_AMOUNT + ") FROM " + DatabaseSQLiteHelper.OPERATIONS_TABLE +
                        " WHERE " + DatabaseSQLiteHelper.COLUMN_OPERATION_AMOUNT + " < 0 " +
                        "AND " + DatabaseSQLiteHelper.COLUMN_OPERATION_DATE + " BETWEEN '" + fromDateString + "' AND '" + toDateString + "' ", null);

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
