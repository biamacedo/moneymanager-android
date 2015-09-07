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

    /***************** Relation to Category Table, used by Account Table and Expense Table *****************/
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
    public static final String EXPENSES_TABLE = "EXPENSES";
    public static final String COLUMN_EXPENSE_TITLE = "TITLE";
    public static final String COLUMN_EXPENSE_DESC = "DESCRIPTION";
    public static final String COLUMN_EXPENSE_DATE = "DATE";
    public static final String COLUMN_EXPENSE_CREATE_DATE = "CREATE_DATE";
    public static final String COLUMN_EXPENSE_AMOUNT = "AMOUNT";
    public final String CREATE_EXPENSES =
            "CREATE TABLE " + EXPENSES_TABLE + " (" +
                    BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_EXPENSE_TITLE + " TEXT, " +
                    COLUMN_EXPENSE_DESC + " TEXT, " +
                    COLUMN_EXPENSE_DATE + " INTEGER, " +
                    COLUMN_EXPENSE_CREATE_DATE + " INTEGER, " +
                    COLUMN_EXPENSE_AMOUNT + " REAL, " +
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

    public DatabaseSQLiteHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_CATEGORIES);
        // Next tables are dependant on the Categories Table
        db.execSQL(CREATE_ACCOUNTS);
        db.execSQL(CREATE_EXPENSES);

        // Creating Default Categories: Account
        db.execSQL(getInsertCategoryString("Bank Account", "Account", "ic_bank"));
        db.execSQL(getInsertCategoryString("Wallet", "Account", "ic_folder_open"));
        db.execSQL(getInsertCategoryString("Savings", "Account", "ic_money"));
        db.execSQL(getInsertCategoryString("Loan", "Account", "ic_group"));
        db.execSQL(getInsertCategoryString("Home", "Account", "ic_home"));
        db.execSQL(getInsertCategoryString("Hidden", "Account", "ic_key"));
        db.execSQL(getInsertCategoryString("Others", "Account", "ic_question"));

        // Creating Default Categories: Expense
        db.execSQL(getInsertCategoryString("Salary", "Expense", "ic_briefcase"));
        db.execSQL(getInsertCategoryString("Savings", "Expense", "ic_money"));
        db.execSQL(getInsertCategoryString("Extra Earnings", "Expense", "ic_dollar"));
        db.execSQL(getInsertCategoryString("Profit", "Expense", "ic_line_chart"));
        db.execSQL(getInsertCategoryString("Loan", "Expense", "ic_group"));
        db.execSQL(getInsertCategoryString("Credit Card", "Expense", "ic_credit_card"));
        db.execSQL(getInsertCategoryString("Super Market", "Expense", "ic_shopping_cart"));
        db.execSQL(getInsertCategoryString("Food", "Expense", "ic_cutlery"));
        db.execSQL(getInsertCategoryString("Trasportation", "Expense", "ic_bus"));
        db.execSQL(getInsertCategoryString("Books", "Expense", "ic_book"));
        db.execSQL(getInsertCategoryString("Magazines", "Expense", "ic_bookmark"));
        db.execSQL(getInsertCategoryString("Comics", "Expense", "ic_comments"));
        db.execSQL(getInsertCategoryString("School", "Expense", "ic_graduation_cap"));
        db.execSQL(getInsertCategoryString("Games", "Expense", "ic_gamepad"));
        db.execSQL(getInsertCategoryString("Trips", "Expense", "ic_paper_plane"));
        db.execSQL(getInsertCategoryString("Shopping", "Expense", "ic_tags"));
        db.execSQL(getInsertCategoryString("Health", "Expense", "ic_medkit"));
        db.execSQL(getInsertCategoryString("Computer", "Expense", "ic_laptop"));
        db.execSQL(getInsertCategoryString("Cinema", "Expense", "ic_film"));
        db.execSQL(getInsertCategoryString("House", "Expense", "ic_home"));
        db.execSQL(getInsertCategoryString("Cellphone", "Expense", "ic_mobile_phone"));
        db.execSQL(getInsertCategoryString("Light", "Expense", "ic_bolt"));
        db.execSQL(getInsertCategoryString("Others", "Expense", "ic_question"));

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
