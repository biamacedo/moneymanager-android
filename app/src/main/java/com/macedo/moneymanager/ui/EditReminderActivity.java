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
import android.widget.Toast;

import com.macedo.moneymanager.R;
import com.macedo.moneymanager.database.RemindersDatasource;
import com.macedo.moneymanager.models.Reminder;
import com.macedo.moneymanager.notification.BillAlarmManager;
import com.macedo.moneymanager.ui.fragments.DatePickerFragment;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class EditReminderActivity extends AppCompatActivity {

    public static final String TAG = EditReminderActivity.class.getSimpleName();

    private EditText mNameEditText;
    private EditText mStartDateEditText;
    private EditText mEndDateEditText;

    private Reminder mCurrentReminder;

    private BillAlarmManager mBillAlarmManager;

    public SimpleDateFormat mDateFormatter = new SimpleDateFormat("MM/dd/yyyy");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_reminder);

        mNameEditText = (EditText) findViewById(R.id.nameEditText);
        mStartDateEditText = (EditText) findViewById(R.id.startDateEditText);
        mEndDateEditText = (EditText) findViewById(R.id.endDateEditText);
        ImageView startDateCalendarIcon = (ImageView) findViewById(R.id.startDateCalendarIcon);
        ImageView endDateCalendarIcon = (ImageView) findViewById(R.id.endDateCalendarIcon);

        mBillAlarmManager = new BillAlarmManager(this);

        Date now = new Date();
        mStartDateEditText.setText(mDateFormatter.format(now));

        Intent intent = getIntent();
        if (intent != null){
            mCurrentReminder = intent.getParcelableExtra(Reminder.REMINDER_EXTRA);
            if (mCurrentReminder != null) {
                mNameEditText.setText(mCurrentReminder.getName());
                mStartDateEditText.setText(mDateFormatter.format(mCurrentReminder.getStartDate()));
                if (mCurrentReminder.getEndDate().getTime() == 0) {
                    mEndDateEditText.setText("");
                } else {
                    mEndDateEditText.setText(mDateFormatter.format(mCurrentReminder.getEndDate()));
                }
            }
        }

        startDateCalendarIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showStartDatePickerDialog(v);
            }
        });
        endDateCalendarIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEndDatePickerDialog(v);
            }
        });
    }

    public void showStartDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                Calendar cal = Calendar.getInstance();
                cal.set(Calendar.YEAR, year);
                cal.set(Calendar.MONTH, month);
                cal.set(Calendar.DATE, day);
                Date dateSelected= cal.getTime();
                mStartDateEditText.setText(mDateFormatter.format(dateSelected));
            }
        };
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    public void showEndDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                Calendar cal = Calendar.getInstance();
                cal.set(Calendar.YEAR, year);
                cal.set(Calendar.MONTH, month);
                cal.set(Calendar.DATE, day);
                Date dateSelected= cal.getTime();
                mEndDateEditText.setText(mDateFormatter.format(dateSelected));
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
                String name = mNameEditText.getText().toString();
                Date startDate =  new Date();
                Date endDate =  new Date();
                try {
                    startDate = mDateFormatter.parse(mStartDateEditText.getText().toString());
                } catch (ParseException e) {
                    Log.e(TAG, e.getMessage());
                    startDate = new Date();
                }
                try {
                    String endDateStr = mEndDateEditText.getText().toString();
                    if (endDateStr.equals("")) {
                        endDate.setTime(0);
                    } else {
                        endDate = mDateFormatter.parse(endDateStr);
                    }
                } catch (ParseException e) {
                    Log.e(TAG, e.getMessage());
                }

                if (mCurrentReminder == null) {
                    mCurrentReminder = new Reminder(name, startDate, endDate);
                } else {
                    mCurrentReminder.setName(name);
                    mCurrentReminder.setStartDate(startDate);
                    mCurrentReminder.setEndDate(endDate);
                }

                mCurrentReminder.setId(saveReminder());

                mBillAlarmManager.startAlarm(mCurrentReminder);

                finish();

                Toast.makeText(EditReminderActivity.this, "Reminder Saved!", Toast.LENGTH_LONG).show();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    public boolean validateFields(){
        if (mNameEditText.getText().toString().equals("")){
            Toast.makeText(EditReminderActivity.this, "Please fill all mandatory fields!", Toast.LENGTH_LONG).show();
            return false;
        }

        String inputDate = mStartDateEditText.getText().toString();
        try {
            mDateFormatter.setLenient(false);
            Date startDate = mDateFormatter.parse(inputDate);
        } catch (ParseException e) {
            Toast.makeText(EditReminderActivity.this, "Please input date in 'mm/dd/yyyy' format!", Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }

    public int saveReminder(){
        RemindersDatasource datasource = new RemindersDatasource(this);
        int id = mCurrentReminder.getId();
        if(mCurrentReminder.getId() != -1){
            datasource.update(mCurrentReminder);
        } else {
            id = (int) datasource.create(mCurrentReminder);
        }
        return id;
    }


}
