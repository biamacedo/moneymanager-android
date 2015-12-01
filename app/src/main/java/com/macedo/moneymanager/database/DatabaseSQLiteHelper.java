package com.macedo.moneymanager.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

/**
 * Created by Beatriz on 06/09/2015.
 */
public class DatabaseSQLiteHelper extends SQLiteOpenHelper{

    private static final String DB_NAME = "moneyManager.db";
    private static final int DB_VERSION = 1;

    /***************** Relation to Category Table, used by Account Table and Operation Table *****************/
    public static final String COLUMN_FOREIGN_KEY_CATEGORY = "CATEGORY_ID";

    /***************** Account Table Information *****************/
    public static final String ACCOUNTS_TABLE = "ACCOUNTS";
    public static final String COLUMN_ACCOUNT_NAME = "NAME";
    public static final String COLUMN_ACCOUNT_AMOUNT = "AMOUNT";
    public static final String COLUMN_ACCOUNT_CREATE_DATE = "CREATE_DATE";
    public final String CREATE_ACCOUNTS =
            "CREATE TABLE " + ACCOUNTS_TABLE + " (" +
                    BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_ACCOUNT_NAME + " TEXT, " +
                    COLUMN_ACCOUNT_CREATE_DATE + " INTEGER, " +
                    COLUMN_ACCOUNT_AMOUNT + " REAL, " +
                    COLUMN_FOREIGN_KEY_CATEGORY + " INTEGER, " +
                    "FOREIGN KEY(" + COLUMN_FOREIGN_KEY_CATEGORY + ") " +
                    "REFERENCES CATEGORIES(_ID))";

    /***************** Expenses Table Information *****************/
    public static final String OPERATIONS_TABLE = "EXPENSES";
    public static final String COLUMN_OPERATION_TITLE = "TITLE";
    public static final String COLUMN_OPERATION_DESC = "DESCRIPTION";
    public static final String COLUMN_OPERATION_DATE = "DATE";
    public static final String COLUMN_OPERATION_CREATE_DATE = "CREATE_DATE";
    public static final String COLUMN_OPERATION_AMOUNT = "AMOUNT";
    public final String CREATE_OPERATIONS =
            "CREATE TABLE " + OPERATIONS_TABLE + " (" +
                    BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_OPERATION_TITLE + " TEXT, " +
                    COLUMN_OPERATION_DESC + " TEXT, " +
                    COLUMN_OPERATION_DATE + " INTEGER, " +
                    COLUMN_OPERATION_CREATE_DATE + " INTEGER, " +
                    COLUMN_OPERATION_AMOUNT + " REAL, " +
                    COLUMN_FOREIGN_KEY_CATEGORY + " INTEGER, " +
                    "FOREIGN KEY(" + COLUMN_FOREIGN_KEY_CATEGORY + ") " +
                    "REFERENCES CATEGORIES(_ID))";

    /***************** Categories Table Information *****************/
    public static final String CATEGORIES_TABLE = "CATEGORIES";
    public static final String COLUMN_CATEGORY_NAME = "NAME";
    public static final String COLUMN_CATEGORY_TYPE = "TYPE";
    public static final String COLUMN_CATEGORY_ICON_NAME = "ICON_NAME";
    public final String CREATE_CATEGORIES =
            "CREATE TABLE " + CATEGORIES_TABLE + " (" +
                    BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    COLUMN_CATEGORY_NAME + " TEXT, " +
                    COLUMN_CATEGORY_TYPE + " TEXT, " +
                    COLUMN_CATEGORY_ICON_NAME + " TEXT)";
    public final String INSERT_CATEGORY =
            "INSERT INTO " + CATEGORIES_TABLE + "("
                    + COLUMN_CATEGORY_NAME + ","
                    + COLUMN_CATEGORY_TYPE + ","
                    + COLUMN_CATEGORY_ICON_NAME + ") values ";

    /***************** Reminders Table Information *****************/
    public static final String REMINDERS_TABLE = "REMINDERS";
    public static final String COLUMN_REMINDER_NAME = "NAME";
    public static final String COLUMN_REMINDER_START_DATE = "START_DATE";
    public static final String COLUMN_REMINDER_END_DATE = "END_DATE";
    public static final String COLUMN_REMINDER_NEXT_ALERT_DATE = "NEXT_ALERT_DATE";
    public final String CREATE_REMINDERS =
            "CREATE TABLE " + REMINDERS_TABLE + " (" +
                    BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    COLUMN_REMINDER_NAME + " TEXT, " +
                    COLUMN_REMINDER_START_DATE + " INTEGER, " +
                    COLUMN_REMINDER_END_DATE + " INTEGER, " +
                    COLUMN_REMINDER_NEXT_ALERT_DATE + " INTEGER)";

    public DatabaseSQLiteHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_CATEGORIES);
        // Next tables are dependant on the Categories Table
        db.execSQL(CREATE_ACCOUNTS);
        db.execSQL(CREATE_OPERATIONS);
        db.execSQL(CREATE_REMINDERS);

        // Creating Default Categories: Account
        db.execSQL(getInsertCategoryString("Bank Account", "Account", "ic_bank"));
        db.execSQL(getInsertCategoryString("Wallet", "Account", "ic_folder_open"));
        db.execSQL(getInsertCategoryString("Savings", "Account", "ic_money"));
        db.execSQL(getInsertCategoryString("Loan", "Account", "ic_group"));
        db.execSQL(getInsertCategoryString("Home", "Account", "ic_home"));
        db.execSQL(getInsertCategoryString("Hidden", "Account", "ic_key"));
        db.execSQL(getInsertCategoryString("Others", "Account", "ic_question"));

        // Creating Default Categories: Operation
        db.execSQL(getInsertCategoryString("Salary", "Operation", "ic_briefcase"));
        db.execSQL(getInsertCategoryString("Savings", "Operation", "ic_money"));
        db.execSQL(getInsertCategoryString("Extra Earnings", "Operation", "ic_dollar"));
        db.execSQL(getInsertCategoryString("Profit", "Operation", "ic_line_chart"));
        db.execSQL(getInsertCategoryString("Loan", "Operation", "ic_group"));
        db.execSQL(getInsertCategoryString("Credit Card", "Operation", "ic_credit_card"));
        db.execSQL(getInsertCategoryString("Super Market", "Operation", "ic_shopping_cart"));
        db.execSQL(getInsertCategoryString("Food", "Operation", "ic_cutlery"));
        db.execSQL(getInsertCategoryString("Transportation", "Operation", "ic_car"));
        db.execSQL(getInsertCategoryString("Books", "Operation", "ic_book"));
        db.execSQL(getInsertCategoryString("Magazines", "Operation", "ic_bookmark"));
        db.execSQL(getInsertCategoryString("Comics", "Operation", "ic_comments"));
        db.execSQL(getInsertCategoryString("School", "Operation", "ic_graduation_cap"));
        db.execSQL(getInsertCategoryString("Games", "Operation", "ic_gamepad"));
        db.execSQL(getInsertCategoryString("Trips", "Operation", "ic_paper_plane"));
        db.execSQL(getInsertCategoryString("Shopping", "Operation", "ic_tags"));
        db.execSQL(getInsertCategoryString("Health", "Operation", "ic_medkit"));
        db.execSQL(getInsertCategoryString("Computer", "Operation", "ic_laptop"));
        db.execSQL(getInsertCategoryString("Product", "Operation", "ic_gift"));
        db.execSQL(getInsertCategoryString("Cinema", "Operation", "ic_film"));
        db.execSQL(getInsertCategoryString("House", "Operation", "ic_home"));
        db.execSQL(getInsertCategoryString("Cellphone", "Operation", "ic_mobile"));
        db.execSQL(getInsertCategoryString("Light", "Operation", "ic_bolt"));
        db.execSQL(getInsertCategoryString("Others", "Operation", "ic_question"));

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // To be used when updating the database
        switch (oldVersion){
            case 1:
                // SQL to Execute
                //sqLiteDatabase.execSQL(ALTER_ADD_CREATE_DATE);
        }
    }

    public String getInsertCategoryString(String name, String type, String iconName){
        return INSERT_CATEGORY + " ( " +
                "'" + name + "'," +
                "'" + type + "'," +
                "'" + iconName + "')";
    }
}
