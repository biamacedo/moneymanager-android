package com.macedo.moneymanager.ui.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.macedo.moneymanager.R;
import com.macedo.moneymanager.adapters.MonthlyOperationItemListAdapter;
import com.macedo.moneymanager.database.MonthOperationsDatasource;
import com.macedo.moneymanager.models.MonthOperation;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class MonthlyOperationsFragment extends Fragment {

    public static final String TAG = MonthlyOperationsFragment.class.getSimpleName();

    public static final String EXPENSE_EXTRA = "EXPENSE";
    public static final String YEAR_LABEL = "Year Totals";

    public ListView mListView;
    public ViewGroup mYearItem;
    TextView mMonthLabel;
    TextView mTotalAmount;
    TextView mExpensePercentage;
    TextView mWinAmount;
    TextView mLossAmount;

    public ArrayList<Integer> mSelectedItems = new ArrayList<Integer>();
    ArrayList<MonthOperation> mMonthOperations;

    public static MonthlyOperationsFragment newInstance() {
        return new MonthlyOperationsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_monthly_operations, container, false);

        mListView = (ListView) rootView.findViewById(R.id.listView);
        mYearItem = (ViewGroup) rootView.findViewById(R.id.yearItem);
        mMonthLabel = (TextView) mYearItem.findViewById(R.id.monthLabel);
        mTotalAmount = (TextView) mYearItem.findViewById(R.id.totalAmount);
        mExpensePercentage = (TextView) mYearItem.findViewById(R.id.expensePercentage);
        mWinAmount = (TextView) mYearItem.findViewById(R.id.winAmount);
        mLossAmount = (TextView) mYearItem.findViewById(R.id.lossAmount);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long arg3) {

                // TODO: On click show expense chart for the month
                /*Intent intent = new Intent(getActivity(), EditOperationActivity.class);
                Operation clickedExpense = mExpenses.get(position);
                intent.putExtra(EXPENSE_EXTRA, clickedExpense);
                startActivity(intent);*/
            }
        });

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        Date now =  new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(now);
        int year = cal.get(Calendar.YEAR);

        refreshList(year);
    }

    public void refreshList(int year){
        MonthOperationsDatasource datasource = new MonthOperationsDatasource(getActivity());
        mMonthOperations = datasource.readMonthExpense(year);
        mListView.setAdapter(new MonthlyOperationItemListAdapter(getActivity(), mMonthOperations));

        MonthOperation yearTotals = datasource.readYearExpense(year);

        mMonthLabel.setText(YEAR_LABEL);
        mTotalAmount.setText("$" + String.format("%.2f", yearTotals.getAmount()));
        mExpensePercentage.setText("(" + String.format("%.2f", yearTotals.getPercentage()) + "%)");
        mWinAmount.setText("$" + String.format("%.2f", yearTotals.getWinAmount()));
        mLossAmount.setText("$" + String.format("%.2f", yearTotals.getLossAmount()));
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_monthly_operation, menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_new_expense) {
        }

        return super.onOptionsItemSelected(item);
    }


}
