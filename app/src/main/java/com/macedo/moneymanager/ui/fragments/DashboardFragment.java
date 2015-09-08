package com.macedo.moneymanager.ui.fragments;

/**
 * Created by Beatriz on 03/09/2015.
 */

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.macedo.moneymanager.R;
import com.macedo.moneymanager.database.ExpensesDatasource;

public class DashboardFragment extends Fragment {

    public static final String TAG = DashboardFragment.class.getSimpleName();

    TextView mBalanceTextView;
    TextView mMonthsToTargetTextView;

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

        mBalanceTextView = (TextView) rootView.findViewById(R.id.balanceTextView);
        mMonthsToTargetTextView = (TextView) rootView.findViewById(R.id.targetTextView);

        ExpensesDatasource expenseDatasource = new ExpensesDatasource(getActivity());

        // TODO: Months to Target Code

        mBalanceTextView.setText("$" + String.format("%.2f", expenseDatasource.sumAllExpenses()));

        return rootView;
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


}