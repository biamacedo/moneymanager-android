package com.macedo.moneymanager.models;

import android.content.Context;

import com.macedo.moneymanager.database.AccountsDatasource;
import com.macedo.moneymanager.database.OperationsDatasource;

/**
 * Created by Beatriz on 08/09/2015.
 */
public class CheckManager {

    private AccountsDatasource mAccountsDatasource;
    private OperationsDatasource mOperationsDatasource;
    private Context mContext;

    private Float accountsTotalAmount;
    private Float expensesTotalAmount;
    private Float differenceAmount;

    public static final String AMOUNT_MISMATCH = "Accounts and Expenses Mismatch!\n Missing: ";
    public static final String AMOUNT_CORRECT = "Check Between Accounts and Expenses: OK ";

    public CheckManager(Context context){
        mContext = context;
        mAccountsDatasource = new AccountsDatasource(mContext);
        mOperationsDatasource = new OperationsDatasource(mContext);
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
        expensesTotalAmount = mOperationsDatasource.sumAllExpenses();
        differenceAmount = 0.00f;

        if (accountsTotalAmount.equals(expensesTotalAmount)){
            return true;
        } else {
            differenceAmount = Math.abs(accountsTotalAmount - expensesTotalAmount);
            return false;
        }
    };
}
