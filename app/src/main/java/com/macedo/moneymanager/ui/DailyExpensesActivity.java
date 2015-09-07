package com.macedo.moneymanager.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import com.macedo.moneymanager.R;
import com.macedo.moneymanager.adapters.DailyExpenseItemListAdapter;
import com.macedo.moneymanager.database.ExpensesDatasource;
import com.macedo.moneymanager.models.Expense;

import java.util.ArrayList;

import se.emilsjolander.stickylistheaders.ExpandableStickyListHeadersListView;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

public class DailyExpensesActivity extends AppCompatActivity {

    public static final String TAG = DailyExpensesActivity.class.getSimpleName();

    public static final String EXPENSE_EXTRA = "EXPENSE";

    public ExpandableStickyListHeadersListView mExpandableStickyList;
    public TextView mTotalAmountTextView;

    public ArrayList<Integer> mSelectedItems = new ArrayList<Integer>();
    ArrayList<Expense> mExpenses;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_expenses);

        mExpandableStickyList = (ExpandableStickyListHeadersListView) findViewById(R.id.accountListView);
        mTotalAmountTextView = (TextView) findViewById(R.id.totalAmount);

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
        mExpandableStickyList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(DailyExpensesActivity.this, EditExpenseActivity.class);
                Expense clickedExpense = mExpenses.get(position);
                intent.putExtra(EXPENSE_EXTRA, clickedExpense);
                startActivity(intent);
                return true;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        refreshList();
    }

    public void refreshList(){
        ExpensesDatasource datasource = new ExpensesDatasource(this);
        mExpenses = datasource.read();
        mExpandableStickyList.setAdapter(new DailyExpenseItemListAdapter(this, mExpenses));
        mTotalAmountTextView.setText("$" + String.format("%.2f", datasource.sumAllExpenses()));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_daily_expense, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_new_expense) {
            Intent intent = new Intent(DailyExpensesActivity.this, EditExpenseActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_edit_expense) {
            if (mSelectedItems.size() > 0) {
                Intent intent = new Intent(DailyExpensesActivity.this, EditExpenseActivity.class);
                Expense clickedExpense = mExpenses.get(mSelectedItems.get(0));
                intent.putExtra(EXPENSE_EXTRA, clickedExpense);
                startActivity(intent);
            } else {
                Toast.makeText(this, "No item selected.", Toast.LENGTH_LONG).show();
            }
        } else if (id == R.id.action_delete_expense) {
            if (mSelectedItems.size() > 0){
                AlertDialog dialog = new AlertDialog.Builder(this)
                        .setTitle("Delete Item")
                        .setMessage("Are you sure you want to delete " + mSelectedItems.size() + " item(s)?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // continue with delete
                                ExpensesDatasource datasource = new ExpensesDatasource(DailyExpensesActivity.this);
                                for(Integer i : mSelectedItems){
                                    datasource.delete(mExpenses.get(i).getId());
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
                refreshList();
            } else {
                Toast.makeText(this, "No item selected.", Toast.LENGTH_LONG).show();
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
