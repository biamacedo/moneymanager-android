package com.macedo.moneymanager.models;

import android.content.Context;

import com.macedo.moneymanager.database.AccountsDatasource;
import com.macedo.moneymanager.database.ExpensesDatasource;

/**
 * Created by Beatriz on 08/09/2015.
 */
public class ManagerCheck {

    private AccountsDatasource mAccountsDatasource;
    private ExpensesDatasource mExpensesDatasource;
    private Context mContext;

    private Float accountsTotalAmount;
    private Float expensesTotalAmount;
    private Float differenceAmount;

    public static final String AMOUNT_MISMATCH = "Accounts and Expenses Mismatch!\n Missing: ";
    public static final String AMOUNT_CORRECT = "Check Between Accounts and Expenses: OK ";

    public ManagerCheck(Context context){
        mContext = context;
        mAccountsDatasource = new AccountsDatasource(mContext);
        mExpensesDatasource = new ExpensesDatasource(mContext);
    }

    public Float getAccountsTotalAmount() {
        return accountsTotalAmount;
    }

    public Float getExpensesTotalAmount() {
        return expensesTotalAmount;
    }

    public Float getDifferenceAmount() {
        return differenceAmount;
    }

    public boolean verifyMatchAccountExpenses(){
        accountsTotalAmount = mAccountsDatasource.sumAllAccounts();
        expensesTotalAmount = mExpensesDatasource.sumAllExpenses();
        differenceAmount = 0.00f;

        if (accountsTotalAmount.equals(expensesTotalAmount)){
            return true;
        } else {
            differenceAmount = Math.abs(accountsTotalAmount - expensesTotalAmount);
            return false;
        }
    };
}
