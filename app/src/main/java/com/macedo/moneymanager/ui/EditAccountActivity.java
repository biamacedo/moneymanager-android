package com.macedo.moneymanager.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.macedo.moneymanager.R;
import com.macedo.moneymanager.adapters.CategorySpinnerListAdapter;
import com.macedo.moneymanager.database.AccountsDatasource;
import com.macedo.moneymanager.database.CategoriesDatasource;
import com.macedo.moneymanager.models.Account;
import com.macedo.moneymanager.models.Category;

import java.util.ArrayList;

public class EditAccountActivity extends AppCompatActivity {

    public static final String TAG = EditAccountActivity.class.getSimpleName();

    private EditText mNameEditText;
    private EditText mAmountEditText;
    private Spinner mCategorySpinner;

    private Account mCurrentAccount;

    public CategoriesDatasource categoriesDatasource = new CategoriesDatasource(this);
    private ArrayList<Category> mCategoryItems = new ArrayList<Category>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_account);

        mNameEditText = (EditText) findViewById(R.id.nameEditText);
        mAmountEditText = (EditText) findViewById(R.id.amountEditText);
        mCategorySpinner = (Spinner) findViewById(R.id.categorySpinner);

        mCategoryItems = categoriesDatasource.read(CategoriesDatasource.CATEGORY_TYPE_ACCOUNT);

        CategorySpinnerListAdapter adapter = new CategorySpinnerListAdapter(this, android.R.layout.simple_spinner_dropdown_item, R.id.categoryLabel,  mCategoryItems);
        mCategorySpinner.setAdapter(adapter);

        Intent intent = getIntent();
        if (intent != null){
            mCurrentAccount = intent.getParcelableExtra(Account.ACCOUNT_EXTRA);
            if (mCurrentAccount != null) {
                mNameEditText.setText(mCurrentAccount.getName());
                mAmountEditText.setText(String.valueOf(mCurrentAccount.getAmount()));
                mCategorySpinner.setSelection(adapter.getPosition(mCurrentAccount.getCategory()));
            }
        }
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
                String name = mNameEditText.getText().toString();
                Float amount = Float.parseFloat(mAmountEditText.getText().toString());
                Category category = mCategoryItems.get(mCategorySpinner.getSelectedItemPosition());

                if (mCurrentAccount == null){
                    mCurrentAccount = new Account(name, category, amount);
                } else {
                    mCurrentAccount.setName(name);
                    mCurrentAccount.setCategory(category);
                    mCurrentAccount.setAmount(amount);
                }

                saveExpense();

                finish();

                Toast.makeText(EditAccountActivity.this, "Account Saved!", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(EditAccountActivity.this, "Please fill all mandatory fields!", Toast.LENGTH_LONG).show();
            }
        }

        return super.onOptionsItemSelected(item);
    }

    public boolean validateFields(){
        if (mNameEditText.getText().toString().equals("")||  mAmountEditText.getText().toString().equals("")){
            return false;
        }
        return true;
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
