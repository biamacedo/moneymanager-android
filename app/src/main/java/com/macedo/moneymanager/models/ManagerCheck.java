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

    private Double accountsTotalAmount;
    private Double expensesTotalAmount;
    private Double differenceAmount;

    public ManagerCheck(Context context){
        mContext = context;
        mAccountsDatasource = new AccountsDatasource(mContext);
        mExpensesDatasource = new ExpensesDatasource(mContext);
    }

    public boolean verifyMatchAccountExpenses(){
        accountsTotalAmount = mAccountsDatasource.sumAllAccounts();
        expensesTotalAmount = mExpensesDatasource.sumAllExpenses();

        if (accountsTotalAmount == expensesTotalAmount){
            return true;
        } else {
            differenceAmount = Math.abs(accountsTotalAmount - expensesTotalAmount);
            return false;
        }
    };

}
