package com.macedo.moneymanager.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.macedo.moneymanager.R;
import com.macedo.moneymanager.adapters.CategorySpinnerListAdapter;
import com.macedo.moneymanager.database.AccountsDatasource;
import com.macedo.moneymanager.database.CategoriesDatasource;
import com.macedo.moneymanager.models.Account;
import com.macedo.moneymanager.models.Category;
import com.macedo.moneymanager.ui.fragments.AccountsFragment;

import java.util.ArrayList;

public class EditAccountActivity extends AppCompatActivity {

    public static final String TAG = EditAccountActivity.class.getSimpleName();

    private EditText nameEditText;
    private EditText amountEditText;
    private Spinner categorySpinner;
    private Button editAccountButton;

    private Account mCurrentAccount;

    public CategoriesDatasource categoriesDatasource = new CategoriesDatasource(this);
    private ArrayList<Category> mCategoryItems = new ArrayList<Category>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_account);

        nameEditText = (EditText) findViewById(R.id.nameEditText);
        amountEditText = (EditText) findViewById(R.id.amountEditText);
        categorySpinner = (Spinner) findViewById(R.id.categorySpinner);
        editAccountButton = (Button) findViewById(R.id.editAccountButton);

        mCategoryItems = categoriesDatasource.read(CategoriesDatasource.CATEGORY_TYPE_ACCOUNT);

        CategorySpinnerListAdapter adapter = new CategorySpinnerListAdapter(this, android.R.layout.simple_spinner_dropdown_item, R.id.categoryLabel,  mCategoryItems);
        categorySpinner.setAdapter(adapter);

        Intent intent = getIntent();
        if (intent != null){
            mCurrentAccount = intent.getParcelableExtra(AccountsFragment.ACCOUNT_EXTRA);
            if (mCurrentAccount != null) {
                nameEditText.setText(mCurrentAccount.getName());
                amountEditText.setText(String.valueOf(mCurrentAccount.getAmount()));
                categorySpinner.setSelection(adapter.getPosition(mCurrentAccount.getCategory()));
            }
        }

        editAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = nameEditText.getText().toString();
                Float amount = Float.parseFloat(amountEditText.getText().toString());
                Category category = mCategoryItems.get(categorySpinner.getSelectedItemPosition());

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
            }
        });
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
        AccountsDatasource datasource = new AccountsDatasource(this);
        if(mCurrentAccount.getId() != -1){
            datasource.update(mCurrentAccount);
        } else {
            datasource.create(mCurrentAccount);
        }
    }

}
