package com.macedo.moneymanager.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

import com.macedo.moneymanager.models.Account;
import com.macedo.moneymanager.models.Category;
import com.macedo.moneymanager.utils.DatabaseUtils;

import java.util.ArrayList;

/**
 * Created by Beatriz on 06/09/2015.
 */
public class AccountsDatasource {

    private Context mContext;
    private DatabaseSQLiteHelper mDatabaseSqlLiteHelper;

    public AccountsDatasource(Context context) {
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

    public ArrayList<Account> read(){
        ArrayList<Account> accounts = readAccounts();
        addCategoryInformation(accounts);
        return accounts;
    }

    public ArrayList<Account> readAccounts(){
        SQLiteDatabase database = open();
        ArrayList<Account> accounts = new ArrayList<>();

        Cursor cursor = database.query(
                DatabaseSQLiteHelper.ACCOUNTS_TABLE,
                new String[]{   BaseColumns._ID,
                        DatabaseSQLiteHelper.COLUMN_ACCOUNT_NAME,
                        DatabaseSQLiteHelper.COLUMN_FOREIGN_KEY_CATEGORY,
                        DatabaseSQLiteHelper.COLUMN_ACCOUNT_AMOUNT},
                null, // Selection
                null, // Selection args
                null, // Group By
                null, // Having
                DatabaseSQLiteHelper.COLUMN_ACCOUNT_CREATE_DATE + " DESC"); // Order

        if(cursor.moveToFirst()){
            do {
                Account account = new Account(DatabaseUtils.getIntFromColumnName(cursor, BaseColumns._ID),
                        DatabaseUtils.getStringFromColumnName(cursor, DatabaseSQLiteHelper.COLUMN_ACCOUNT_NAME),
                        new Category(DatabaseUtils.getIntFromColumnName(cursor, DatabaseSQLiteHelper.COLUMN_FOREIGN_KEY_CATEGORY)),
                        DatabaseUtils.getFloatFromColumnName(cursor, DatabaseSQLiteHelper.COLUMN_ACCOUNT_AMOUNT));

                accounts.add(account);
            } while(cursor.moveToNext());
        }
        cursor.close();
        close(database);
        return accounts;
    }

    public void addCategoryInformation(ArrayList<Account> accounts){
        SQLiteDatabase database = open();
        for (Account account : accounts) {
            Cursor cursor = database.rawQuery(
                    "SELECT * FROM " + DatabaseSQLiteHelper.CATEGORIES_TABLE +
                            " WHERE " + BaseColumns._ID +" = " + account.getCategory().getId(), null);
            if(cursor.moveToFirst()){
                do{
                    account.getCategory().setId(DatabaseUtils.getIntFromColumnName(cursor, BaseColumns._ID));
                    account.getCategory().setName(DatabaseUtils.getStringFromColumnName(cursor, DatabaseSQLiteHelper.COLUMN_CATEGORY_NAME));
                    account.getCategory().setType(DatabaseUtils.getStringFromColumnName(cursor, DatabaseSQLiteHelper.COLUMN_CATEGORY_TYPE));
                    account.getCategory().setIconName(DatabaseUtils.getStringFromColumnName(cursor, DatabaseSQLiteHelper.COLUMN_CATEGORY_ICON_NAME));
                }while (cursor.moveToNext());
            }
            cursor.close();
        }
        database.close();
    }

    public void update(Account account){
        SQLiteDatabase database = open();
        database.beginTransaction();

        ContentValues updateAccountValues = new ContentValues();
        updateAccountValues.put(DatabaseSQLiteHelper.COLUMN_ACCOUNT_NAME, account.getName());
        updateAccountValues.put(DatabaseSQLiteHelper.COLUMN_FOREIGN_KEY_CATEGORY, account.getCategory().getId());
        updateAccountValues.put(DatabaseSQLiteHelper.COLUMN_ACCOUNT_AMOUNT, account.getAmount());

        database.update(DatabaseSQLiteHelper.ACCOUNTS_TABLE,
                updateAccountValues,
                String.format("%s=%d", BaseColumns._ID, account.getId(), null), null);

        database.setTransactionSuccessful();
        database.endTransaction();
        close(database);

    }

    public void delete(int accountId){
        SQLiteDatabase database = open();
        database.beginTransaction();

        database.delete(DatabaseSQLiteHelper.ACCOUNTS_TABLE,
                String.format("%s=%s", BaseColumns._ID, String.valueOf(accountId)), null);

        database.setTransactionSuccessful();
        database.endTransaction();
        close(database);
    }

    public void create(Account account){
        SQLiteDatabase database = open();
        database.beginTransaction();

        // Implementation details
        ContentValues accountValues = new ContentValues();
        accountValues.put(DatabaseSQLiteHelper.COLUMN_ACCOUNT_NAME, account.getName());
        accountValues.put(DatabaseSQLiteHelper.COLUMN_FOREIGN_KEY_CATEGORY, account.getCategory().getId());
        accountValues.put(DatabaseSQLiteHelper.COLUMN_ACCOUNT_AMOUNT, account.getAmount());

        long accountID = database.insert(DatabaseSQLiteHelper.ACCOUNTS_TABLE, null, accountValues);

        database.setTransactionSuccessful();
        database.endTransaction();
        close(database);

    }

    public Float sumAllAccounts(){
        SQLiteDatabase database = open();
        ArrayList<Account> accounts = new ArrayList<Account>();
        Float amount = 0.00f;

        Cursor cursor = database.rawQuery(
                "SELECT SUM(" + DatabaseSQLiteHelper.COLUMN_ACCOUNT_AMOUNT + ") FROM " + DatabaseSQLiteHelper.ACCOUNTS_TABLE, null);

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
