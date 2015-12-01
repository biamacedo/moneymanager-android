package com.macedo.moneymanager.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.macedo.moneymanager.R;
import com.macedo.moneymanager.adapters.CategorySpinnerListAdapter;
import com.macedo.moneymanager.database.CategoriesDatasource;
import com.macedo.moneymanager.database.OperationsDatasource;
import com.macedo.moneymanager.models.Category;
import com.macedo.moneymanager.models.Operation;
import com.macedo.moneymanager.ui.fragments.DatePickerFragment;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class EditOperationActivity extends AppCompatActivity {

    public static final String TAG = EditOperationActivity.class.getSimpleName();

    private EditText mTitleEditText;
    private EditText mDescriptionEditText;
    private EditText mDateEditText;
    private EditText mAmountEditText;
    private Spinner mCategorySpinner;

    private Operation mCurrentOperation;

    public CategoriesDatasource categoriesDatasource = new CategoriesDatasource(this);
    private ArrayList<Category> mCategoryItems = new ArrayList<>();

    public SimpleDateFormat mDateFormatter = new SimpleDateFormat("MM/dd/yyyy");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_operation);

        mTitleEditText = (EditText) findViewById(R.id.nameEditText);
        mDescriptionEditText = (EditText) findViewById(R.id.descriptionEditText);
        mAmountEditText = (EditText) findViewById(R.id.amountEditText);
        mDateEditText = (EditText) findViewById(R.id.dateEditText);
        mCategorySpinner = (Spinner) findViewById(R.id.categorySpinner);
        ImageView calendarIcon = (ImageView) findViewById(R.id.calendarIcon);

        Date now = new Date();
        mDateEditText.setText(mDateFormatter.format(now));

        mCategoryItems = categoriesDatasource.read(CategoriesDatasource.CATEGORY_TYPE_EXPENSE);

        CategorySpinnerListAdapter adapter = new CategorySpinnerListAdapter(this, android.R.layout.simple_spinner_dropdown_item, R.id.categoryLabel,  mCategoryItems);
        mCategorySpinner.setAdapter(adapter);

        Intent intent = getIntent();
        if (intent != null){
            mCurrentOperation = intent.getParcelableExtra(Operation.OPERATION_EXTRA);
            if (mCurrentOperation != null) {
                mTitleEditText.setText(mCurrentOperation.getTitle());
                mDescriptionEditText.setText(mCurrentOperation.getDescription());
                mAmountEditText.setText(String.valueOf(mCurrentOperation.getAmount()));
                mDateEditText.setText(mDateFormatter.format(mCurrentOperation.getDate()));
                mCategorySpinner.setSelection(adapter.getPosition(mCurrentOperation.getCategory()));
            }
        }

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
                Calendar cal = Calendar.getInstance();
                cal.set(Calendar.YEAR, year);
                cal.set(Calendar.MONTH, month);
                cal.set(Calendar.DATE, day);
                Date dateSelected= cal.getTime();
                mDateEditText.setText(mDateFormatter.format(dateSelected));
            }
        };
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_edit_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == android.R.id.home) {
            Intent intent = NavUtils.getParentActivityIntent(this);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            NavUtils.navigateUpTo(this, intent);
            return true;
        } else if (itemId == R.id.action_save) {

            if (validateFields()) {
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

                if (mCurrentOperation == null) {
                    mCurrentOperation = new Operation(title, description, category, amount, date);
                } else {
                    mCurrentOperation.setTitle(title);
                    mCurrentOperation.setDescription(description);
                    mCurrentOperation.setCategory(category);
                    mCurrentOperation.setAmount(amount);
                    mCurrentOperation.setDate(date);
                }

                saveExpense();

                finish();

                Toast.makeText(EditOperationActivity.this, "Operation Saved!", Toast.LENGTH_LONG).show();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    public boolean validateFields(){
        if (mTitleEditText.getText().toString().equals("")||  mAmountEditText.getText().toString().equals("")){
            Toast.makeText(EditOperationActivity.this, "Please fill all mandatory fields!", Toast.LENGTH_LONG).show();
            return false;
        }

        String inputDate = mDateEditText.getText().toString();
        try {
            mDateFormatter.setLenient(false);
            Date date = mDateFormatter.parse(inputDate);
        } catch (ParseException e) {
            Toast.makeText(EditOperationActivity.this, "Please input date in 'mm/dd/yyyy' format!", Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }

    public void saveExpense(){
        OperationsDatasource datasource = new OperationsDatasource(this);
        if(mCurrentOperation.getId() != -1){
            datasource.update(mCurrentOperation);
        } else {
            datasource.create(mCurrentOperation);
        }
    }


}
