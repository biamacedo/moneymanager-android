package com.macedo.moneymanager.ui.fragments;

import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import com.macedo.moneymanager.R;
import com.macedo.moneymanager.adapters.DailyExpenseItemListAdapter;
import com.macedo.moneymanager.database.ExpensesDatasource;
import com.macedo.moneymanager.models.Expense;
import com.macedo.moneymanager.ui.EditExpenseActivity;

import java.util.ArrayList;

import se.emilsjolander.stickylistheaders.ExpandableStickyListHeadersListView;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

public class DailyExpensesFragment extends Fragment {

    public static final String TAG = DailyExpensesFragment.class.getSimpleName();

    public static final String EXPENSE_EXTRA = "EXPENSE";

    public ExpandableStickyListHeadersListView mExpandableStickyList;
    public TextView mTotalAmountTextView;

    public ArrayList<Integer> mSelectedItems = new ArrayList<Integer>();
    ArrayList<Expense> mExpenses;

    public static DailyExpensesFragment newInstance() {
        return new DailyExpensesFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_daily_expenses, container, false);

        mExpandableStickyList = (ExpandableStickyListHeadersListView) rootView.findViewById(R.id.accountListView);
        mTotalAmountTextView = (TextView) rootView.findViewById(R.id.totalAmount);

        mExpandableStickyList.setOnHeaderClickListener(new StickyListHeadersListView.OnHeaderClickListener() {
            @Override
            public void onHeaderClick(StickyListHeadersListView l, View header, int itemPosition, long headerId, boolean currentlySticky) {
                if (mExpandableStickyList.isHeaderCollapsed(headerId)) {
                    mExpandableStickyList.expand(headerId);
                } else {
                    mExpandableStickyList.collapse(headerId);
                }
            }
        });

        mExpandableStickyList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), EditExpenseActivity.class);
                Expense clickedExpense = mExpenses.get(position);
                intent.putExtra(EXPENSE_EXTRA, clickedExpense);
                startActivity(intent);
                return true;
            }
        });

        mExpandableStickyList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long arg3) {
                if (!view.isSelected()) {
                    view.setSelected(true);
                    mSelectedItems.add(position);
                } else {
                    view.setSelected(false);
                    mSelectedItems.remove(Integer.valueOf(position));
                }
            }
        });
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        refreshList();
    }

    public void refreshList(){
        ExpensesDatasource datasource = new ExpensesDatasource(getActivity());
        mExpenses = datasource.read();
        mExpandableStickyList.setAdapter(new DailyExpenseItemListAdapter(getActivity(), mExpenses));
        mTotalAmountTextView.setText("$" + String.format("%.2f", datasource.sumAllExpenses()));
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_daily_expense, menu);
        super.onCreateOptionsMenu(menu, menuInflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_new_expense) {
            Intent intent = new Intent(getActivity(), EditExpenseActivity.class);
            startActivity(intent);
            return true;
        } /*else if (id == R.id.action_edit_expense) {
            if (mSelectedItems.size() > 0) {
                Intent intent = new Intent(getActivity(), EditExpenseActivity.class);
                Expense clickedExpense = mExpenses.get(mSelectedItems.get(0));
                intent.putExtra(EXPENSE_EXTRA, clickedExpense);
                startActivity(intent);
            } else {
                Toast.makeText(getActivity(), "No item selected.", Toast.LENGTH_LONG).show();
            }
        } */else if (id == R.id.action_delete_expense) {
            if (mSelectedItems.size() > 0){
                AlertDialog dialog = new AlertDialog.Builder(getActivity())
                        .setTitle("Delete Item")
                        .setMessage("Are you sure you want to delete " + mSelectedItems.size() + " item(s)?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // continue with delete
                                ExpensesDatasource datasource = new ExpensesDatasource(getActivity());
                                for(Integer i : mSelectedItems){
                                    datasource.delete(mExpenses.get(i).getId());
                                    refreshList();
                                }
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .setIcon(R.drawable.ic_exclamation_triangle)
                        .show();
            } else {
                Toast.makeText(getActivity(), "No item selected.", Toast.LENGTH_LONG).show();
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
