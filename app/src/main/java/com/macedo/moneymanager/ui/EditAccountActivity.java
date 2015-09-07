package com.macedo.moneymanager.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.macedo.moneymanager.R;
import com.macedo.moneymanager.adapters.CategorySpinnerListAdapter;
import com.macedo.moneymanager.database.AccountsDatasource;
import com.macedo.moneymanager.database.CategoriesDatasource;
import com.macedo.moneymanager.models.Account;
import com.macedo.moneymanager.models.Category;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class EditAccountActivity extends AppCompatActivity {

    public static final String TAG = EditAccountActivity.class.getSimpleName();

    private  EditText titleEditText;
    private  EditText amountEditText;
    private Spinner categorySpinner;
    private Button createNewAccount;
    private ImageView calendarIcon;

    private Account mCurrentAccount;

    public CategoriesDatasource categoriesDatasource = new CategoriesDatasource(this);
    private ArrayList<Category> mCategoryItems = new ArrayList<Category>();

    public SimpleDateFormat mDateFormatter = new SimpleDateFormat("MM/dd/yyyy");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_accounts);

        titleEditText  = (EditText) findViewById(R.id.titleEditText);
        amountEditText = (EditText) findViewById(R.id.amountEditText);
        categorySpinner = (Spinner) findViewById(R.id.categorySpinner);
        createNewAccount = (Button) findViewById(R.id.newExpenseButton);
        calendarIcon = (ImageView) findViewById(R.id.calendarIcon);

        mCategoryItems = categoriesDatasource.read(CategoriesDatasource.CATEGORY_TYPE_ACCOUNT);

        CategorySpinnerListAdapter adapter = new CategorySpinnerListAdapter(this, android.R.layout.simple_spinner_dropdown_item, R.id.categoryLabel,  mCategoryItems);
        categorySpinner.setAdapter(adapter);

        Intent intent = getIntent();
        if (intent != null){
            mCurrentAccount = intent.getParcelableExtra(DailyExpensesActivity.EXPENSE_EXTRA);
            if (mCurrentAccount != null) {
                titleEditText.setText(mCurrentAccount.getName());
                amountEditText.setText(String.valueOf(mCurrentAccount.getAmount()));
                categorySpinner.setSelection(adapter.getPosition(mCurrentAccount.getCategory()));
            }
        }

        createNewAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = titleEditText.getText().toString();
                Double amount = Double.parseDouble(amountEditText.getText().toString());
                Category category = mCategoryItems.get(categorySpinner.getSelectedItemPosition());

                mCurrentAccount.setName(title);
                mCurrentAccount.setCategory(category);
                mCurrentAccount.setAmount(amount);

                saveExpense();

                finish();

                Toast.makeText(EditAccountActivity.this, "New Registry Created", Toast.LENGTH_LONG).show();
            }
        });
    }


    public void saveExpense(){
        AccountsDatasource datasource = new AccountsDatasource(this);
        if(mCurrentAccount.getId() != -1){
            datasource.update(mCurrentAccount);
        } else {
            datasource.create(mCurrentAccount);
        }
    }

}
