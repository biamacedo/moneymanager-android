package com.macedo.moneymanager.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

import com.macedo.moneymanager.models.Category;
import com.macedo.moneymanager.utils.DatabaseUtils;

import java.util.ArrayList;

/**
 * Created by Beatriz on 06/09/2015.
 */
public class CategoriesDatasource {

    private Context mContext;
    private DatabaseSQLiteHelper mDatabaseSqlLiteHelper;

    public static final String CATEGORY_TYPE_ACCOUNT = "Account";
    public static final String CATEGORY_TYPE_EXPENSE = "Expense";

    public CategoriesDatasource(Context context) {
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

    public ArrayList<Category> read(String type){
        ArrayList<Category> categories = readCategories(type);
        return categories;
    }

    public ArrayList<Category> readCategories(String type){
        SQLiteDatabase database = open();
        ArrayList<Category> categories = new ArrayList<Category>();

        String[] args = { type };

        Cursor cursor = database.query(
                DatabaseSQLiteHelper.CATEGORIES_TABLE,
                new String[]{   BaseColumns._ID,
                        DatabaseSQLiteHelper.COLUMN_CATEGORY_NAME,
                        DatabaseSQLiteHelper.COLUMN_CATEGORY_TYPE,
                        DatabaseSQLiteHelper.COLUMN_CATEGORY_ICON_NAME},
                DatabaseSQLiteHelper.COLUMN_CATEGORY_TYPE + "=?", // Selection
                args, // Selection args
                null, // Group By
                null, // Having
                DatabaseSQLiteHelper.COLUMN_CATEGORY_NAME + " ASC"); // Order

        if(cursor.moveToFirst()){
            do {
                Category category = new Category(DatabaseUtils.getIntFromColumnName(cursor, BaseColumns._ID),
                        DatabaseUtils.getStringFromColumnName(cursor, DatabaseSQLiteHelper.COLUMN_CATEGORY_NAME),
                        DatabaseUtils.getStringFromColumnName(cursor, DatabaseSQLiteHelper.COLUMN_CATEGORY_TYPE),
                        DatabaseUtils.getStringFromColumnName(cursor, DatabaseSQLiteHelper.COLUMN_CATEGORY_ICON_NAME));

                categories.add(category);
            } while(cursor.moveToNext());
        }
        cursor.close();
        close(database);
        return categories;
    }

    public void update(Category category){
        SQLiteDatabase database = open();
        database.beginTransaction();

        ContentValues updateCategoryValues = new ContentValues();
        updateCategoryValues.put(DatabaseSQLiteHelper.COLUMN_CATEGORY_NAME, category.getName());
        updateCategoryValues.put(DatabaseSQLiteHelper.COLUMN_CATEGORY_TYPE, category.getType());
        updateCategoryValues.put(DatabaseSQLiteHelper.COLUMN_CATEGORY_ICON_NAME, category.getIconName());

        database.update(DatabaseSQLiteHelper.CATEGORIES_TABLE,
                updateCategoryValues,
                String.format("%s=%d", BaseColumns._ID, category.getId(), null), null);

        database.setTransactionSuccessful();
        database.endTransaction();
        close(database);

    }

    public void delete(int categoryId){
        SQLiteDatabase database = open();
        database.beginTransaction();

        database.delete(DatabaseSQLiteHelper.CATEGORIES_TABLE,
                String.format("%s=%s", BaseColumns._ID, String.valueOf(categoryId)), null);

        database.setTransactionSuccessful();
        database.endTransaction();
        close(database);
    }

    public void create(Category category){
        SQLiteDatabase database = open();
        database.beginTransaction();

        // Implementation details
        ContentValues categoryValues = new ContentValues();
        categoryValues.put(DatabaseSQLiteHelper.COLUMN_CATEGORY_NAME, category.getName());
        categoryValues.put(DatabaseSQLiteHelper.COLUMN_CATEGORY_TYPE, category.getType());
        categoryValues.put(DatabaseSQLiteHelper.COLUMN_CATEGORY_ICON_NAME, category.getIconName());

        long categoryID = database.insert(DatabaseSQLiteHelper.CATEGORIES_TABLE, null, categoryValues);

        database.setTransactionSuccessful();
        database.endTransaction();
        close(database);

    }

}
