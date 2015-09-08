package com.macedo.moneymanager.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.macedo.moneymanager.R;
import com.macedo.moneymanager.adapters.CategorySpinnerListAdapter;
import com.macedo.moneymanager.database.CategoriesDatasource;
import com.macedo.moneymanager.database.ExpensesDatasource;
import com.macedo.moneymanager.models.Category;
import com.macedo.moneymanager.models.Expense;
import com.macedo.moneymanager.ui.fragments.DailyExpensesFragment;
import com.macedo.moneymanager.ui.fragments.DatePickerFragment;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class EditExpenseActivity extends AppCompatActivity {

    public static final String TAG = EditExpenseActivity.class.getSimpleName();

    private EditText mTitleEditText;
    private EditText mDescriptionEditText;
    private EditText mDateEditText;
    private EditText mAmountEditText;
    private Spinner mCategorySpinner;
    private Button mEditExpenseButton;
    private ImageView mCalendarIcon;

    private Expense mCurrentExpense;

    public CategoriesDatasource categoriesDatasource = new CategoriesDatasource(this);
    private ArrayList<Category> mCategoryItems = new ArrayList<Category>();

    public SimpleDateFormat mDateFormatter = new SimpleDateFormat("MM/dd/yyyy");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_expense);

        mTitleEditText = (EditText) findViewById(R.id.nameEditText);
        mDescriptionEditText = (EditText) findViewById(R.id.descriptionEditText);
        mAmountEditText = (EditText) findViewById(R.id.amountEditText);
        mDateEditText = (EditText) findViewById(R.id.dateEditText);
        mCategorySpinner = (Spinner) findViewById(R.id.categorySpinner);
        mEditExpenseButton = (Button) findViewById(R.id.editExpenseButton);
        mCalendarIcon = (ImageView) findViewById(R.id.calendarIcon);

        Date now = new Date();
        mDateEditText.setText(mDateFormatter.format(now));

        mCategoryItems = categoriesDatasource.read(CategoriesDatasource.CATEGORY_TYPE_EXPENSE);

        CategorySpinnerListAdapter adapter = new CategorySpinnerListAdapter(this, android.R.layout.simple_spinner_dropdown_item, R.id.categoryLabel,  mCategoryItems);
        mCategorySpinner.setAdapter(adapter);

        Intent intent = getIntent();
        if (intent != null){
            mCurrentExpense = intent.getParcelableExtra(DailyExpensesFragment.EXPENSE_EXTRA);
            if (mCurrentExpense != null) {
                mTitleEditText.setText(mCurrentExpense.getTitle());
                mDescriptionEditText.setText(mCurrentExpense.getDescription());
                mAmountEditText.setText(String.valueOf(mCurrentExpense.getAmount()));
                mDateEditText.setText(mDateFormatter.format(mCurrentExpense.getDate()));
                mCategorySpinner.setSelection(adapter.getPosition(mCurrentExpense.getCategory()));
            }
        }

        mEditExpenseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = mTitleEditText.getText().toString();
                String description = mDescriptionEditText.getText().toString();
                Float amount = Float.parseFloat(mAmountEditText.getText().toString());
                Date date = new Date();
                try {
                    date = mDateFormatter.parse(mDateEditText.getText().toString());
                } catch (ParseException e) {
                    Log.e(TAG, e.getMessage());
                }
                Category category = mCategoryItems.get(mCategorySpinner.getSelectedItemPosition());

                if (mCurrentExpense == null) {
                    mCurrentExpense = new Expense(title, description, category, amount, date);
                } else {
                    mCurrentExpense.setTitle(title);
                    mCurrentExpense.setDescription(description);
                    mCurrentExpense.setCategory(category);
                    mCurrentExpense.setAmount(amount);
                    mCurrentExpense.setDate(date);
                }

                saveExpense();

                finish();

                Toast.makeText(EditExpenseActivity.this, "Expense Saved!", Toast.LENGTH_LONG).show();
            }
        });

        mCalendarIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(v);
            }
        });
    }

    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                mDateEditText.setText((month + 1) + "/" + day + "/" + year);
            }
        };
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()== android.R.id.home) {
            Intent intent = NavUtils.getParentActivityIntent(this);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            NavUtils.navigateUpTo(this, intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void saveExpense(){
        ExpensesDatasource datasource = new ExpensesDatasource(this);
        if(mCurrentExpense.getId() != -1){
            datasource.update(mCurrentExpense);
        } else {
            datasource.create(mCurrentExpense);
        }
    }


}
