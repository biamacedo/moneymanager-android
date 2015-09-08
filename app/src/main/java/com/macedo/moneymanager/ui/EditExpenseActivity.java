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

    private EditText titleEditText;
    private EditText descriptionEditText;
    private EditText dateEditText;
    private EditText amountEditText;
    private Spinner categorySpinner;
    private Button editExpenseButton;
    private ImageView calendarIcon;

    private Expense mCurrentExpense;

    public CategoriesDatasource categoriesDatasource = new CategoriesDatasource(this);
    private ArrayList<Category> mCategoryItems = new ArrayList<Category>();

    public SimpleDateFormat mDateFormatter = new SimpleDateFormat("MM/dd/yyyy");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_expense);

        titleEditText  = (EditText) findViewById(R.id.nameEditText);
        descriptionEditText = (EditText) findViewById(R.id.descriptionEditText);
        amountEditText = (EditText) findViewById(R.id.amountEditText);
        dateEditText = (EditText) findViewById(R.id.dateEditText);
        categorySpinner = (Spinner) findViewById(R.id.categorySpinner);
        editExpenseButton = (Button) findViewById(R.id.editExpenseButton);
        calendarIcon = (ImageView) findViewById(R.id.calendarIcon);

        mCategoryItems = categoriesDatasource.read(CategoriesDatasource.CATEGORY_TYPE_EXPENSE);

        CategorySpinnerListAdapter adapter = new CategorySpinnerListAdapter(this, android.R.layout.simple_spinner_dropdown_item, R.id.categoryLabel,  mCategoryItems);
        categorySpinner.setAdapter(adapter);

        Intent intent = getIntent();
        if (intent != null){
            mCurrentExpense = intent.getParcelableExtra(DailyExpensesFragment.EXPENSE_EXTRA);
            if (mCurrentExpense != null) {
                titleEditText.setText(mCurrentExpense.getTitle());
                descriptionEditText.setText(mCurrentExpense.getDescription());
                amountEditText.setText(String.valueOf(mCurrentExpense.getAmount()));
                dateEditText.setText(mDateFormatter.format(mCurrentExpense.getDate()));
                categorySpinner.setSelection(adapter.getPosition(mCurrentExpense.getCategory()));
            }
        }

        editExpenseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = titleEditText.getText().toString();
                String description = descriptionEditText.getText().toString();
                Float amount = Float.parseFloat(amountEditText.getText().toString());
                Date date = new Date();
                try {
                    date = mDateFormatter.parse(dateEditText.getText().toString());
                } catch (ParseException e) {
                    Log.e(TAG, e.getMessage());
                }
                Category category = mCategoryItems.get(categorySpinner.getSelectedItemPosition());

                if (mCurrentExpense == null){
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

        calendarIcon.setOnClickListener(new View.OnClickListener() {
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
                dateEditText.setText((month+1) + "/" + day + "/" + year);
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
