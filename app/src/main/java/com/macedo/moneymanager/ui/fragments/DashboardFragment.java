package com.macedo.moneymanager.ui.fragments;

/**
 * Created by Beatriz on 03/09/2015.
 */

import android.app.Activity;
import android.app.Fragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.macedo.moneymanager.R;
import com.macedo.moneymanager.database.ExpensesDatasource;
import com.macedo.moneymanager.models.ManagerCheck;

public class DashboardFragment extends Fragment {

    public static final String TAG = DashboardFragment.class.getSimpleName();

    public static final String MONTHS_TO_TARGET_LABEL = "Months\nto target:\n";

    TextView mBalanceTextView;
    TextView mMonthsToTargetTextView;
    TextView mTargetLabel;
    TextView mCheckTextView;

    ExpensesDatasource mExpensesDatasource;
    ManagerCheck mManagerCheck;

    public static DashboardFragment newInstance() {
        return new DashboardFragment();
    }

    public DashboardFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_dashboard, container, false);
        mManagerCheck = new ManagerCheck(getActivity());

        mBalanceTextView = (TextView) rootView.findViewById(R.id.balanceTextView);
        mMonthsToTargetTextView = (TextView) rootView.findViewById(R.id.targetTextView);
        mTargetLabel = (TextView) rootView.findViewById(R.id.targetLabel);
        mCheckTextView  = (TextView) rootView.findViewById(R.id.checkTextView);

        mExpensesDatasource = new ExpensesDatasource(getActivity());

        updateDashboard();

        mCheckTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifyAmounts();
            }
        });

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        updateDashboard();
        verifyAmounts();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_dashboard_fragment, menu);
    }

    public void updateDashboard(){
        Float balance = mExpensesDatasource.sumAllExpenses();
        mBalanceTextView.setText("$" + String.format("%.2f", balance));

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String target = preferences.getString(getActivity().getResources().getString(R.string.settings_target_key), "100.00");
        String income = preferences.getString(getActivity().getResources().getString(R.string.settings_income_key), "10.00");
        Float targetAmount = Float.parseFloat(target);
        Float incomeAmount = Float.parseFloat(income);

        mTargetLabel.setText(MONTHS_TO_TARGET_LABEL + "$" + String.format("%.2f", targetAmount));

        int monthsToTarget = new Float((targetAmount - balance) / incomeAmount).intValue();
        if (monthsToTarget < 0) { monthsToTarget = 0; }

        mMonthsToTargetTextView.setText(String.valueOf(monthsToTarget));
    }

    public void verifyAmounts(){
        if(!mManagerCheck.verifyMatchAccountExpenses()){
            String differenceAmount = "$" + String.format("%.2f", mManagerCheck.getDifferenceAmount());
            mCheckTextView.setText(ManagerCheck.AMOUNT_MISMATCH + differenceAmount);
            mCheckTextView.setBackgroundColor(getResources().getColor(R.color.checkError));
        } else {
            mCheckTextView.setText(ManagerCheck.AMOUNT_CORRECT);
            mCheckTextView.setBackgroundColor(getResources().getColor(R.color.checkOk));
        }
    }
}