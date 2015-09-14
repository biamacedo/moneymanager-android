package com.macedo.moneymanager.ui.fragments;

/**
 * Created by Beatriz on 03/09/2015.
 */

import android.app.Activity;
import android.app.Fragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.macedo.moneymanager.R;
import com.macedo.moneymanager.database.OperationsDatasource;
import com.macedo.moneymanager.database.MonthOperationsDatasource;
import com.macedo.moneymanager.models.CheckManager;
import com.macedo.moneymanager.models.MonthOperation;
import com.macedo.moneymanager.utils.AmountFormatter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

public class DashboardFragment extends Fragment {

    public static final String TAG = DashboardFragment.class.getSimpleName();

    public static final String MONTHS_TO_TARGET_LABEL = "Months\nto target:\n";

    TextView mBalanceTextView;
    TextView mMonthsToTargetTextView;
    TextView mTargetLabel;
    TextView mCheckTextView;

    LineChart mLineChart;

    OperationsDatasource mOperationsDatasource;
    CheckManager mCheckManager;

    public static DashboardFragment newInstance() {
        return new DashboardFragment();
    }

    public DashboardFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_dashboard, container, false);
        mCheckManager = new CheckManager(getActivity());

        mBalanceTextView = (TextView) rootView.findViewById(R.id.balanceTextView);
        mMonthsToTargetTextView = (TextView) rootView.findViewById(R.id.targetTextView);
        mTargetLabel = (TextView) rootView.findViewById(R.id.targetLabel);
        mCheckTextView  = (TextView) rootView.findViewById(R.id.checkTextView);
        mLineChart = (LineChart) rootView.findViewById(R.id.lineChart);

        mOperationsDatasource = new OperationsDatasource(getActivity());

        updateDashboard();
        verifyAmounts();

        mCheckTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifyAmounts();
            }
        });

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        updateDashboard();
        verifyAmounts();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_dashboard_fragment, menu);
    }

    public void setupLineChart(){
        MonthOperationsDatasource monthOperationsDatasource = new MonthOperationsDatasource(getActivity());
        ArrayList<MonthOperation> monthOperations = monthOperationsDatasource.readMonthExpense(getCurrentYear());

        String[] monthValues = getActivity().getResources().getStringArray(R.array.months_char);
        ArrayList<String> chartXValues = new ArrayList<String>( Arrays.asList(monthValues));

        ArrayList<Entry> monthTotalValues = new ArrayList<Entry>();
        for (int i = 0; i < monthOperations.size(); i++){
            Entry newEntry = new Entry(monthOperations.get(i).getAmount(), i);
            monthTotalValues.add(newEntry);
        }

        LineDataSet monthTotalDataSet = new LineDataSet(monthTotalValues, "Month Totals");
        monthTotalDataSet.setColor(getActivity().getResources().getColor(R.color.graph_line));
        monthTotalDataSet.setLineWidth(3f);
        monthTotalDataSet.setCircleSize(0f);
        //monthTotalDataSet.setFillColor(getActivity().getResources().getColor(R.color.graph_total_value));
        monthTotalDataSet.setDrawCubic(false);
        monthTotalDataSet.setDrawValues(true);
        monthTotalDataSet.setValueTextSize(10f);
        monthTotalDataSet.setValueTextColor(getActivity().getResources().getColor(R.color.graph_font));

        monthTotalDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);

        ArrayList<LineDataSet> dataSets = new ArrayList<LineDataSet>();
        dataSets.add(monthTotalDataSet);

        LineData data = new LineData(chartXValues, dataSets);
        mLineChart.setData(data);

        mLineChart.setDrawGridBackground(false);
        mLineChart.setBackgroundColor(getActivity().getResources().getColor(R.color.graph_bkg));
        mLineChart.setDrawBorders(false);
        // enable value highlighting
        mLineChart.setHighlightEnabled(true);
        // enable touch gestures
        mLineChart.setTouchEnabled(true);
        // if disabled, scaling can be done on x- and y-axis separately
        mLineChart.setPinchZoom(false);

        XAxis xAxis = mLineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGridColor(getActivity().getResources().getColor(R.color.graph_grid));
        xAxis.setTextColor(getActivity().getResources().getColor(R.color.graph_font));
        xAxis.setLabelsToSkip(0);

        AmountFormatter custom = new AmountFormatter();

        YAxis leftAxis = mLineChart.getAxisLeft();
        leftAxis.setLabelCount(8, false);
        leftAxis.setValueFormatter(custom);
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        leftAxis.setGridColor(getActivity().getResources().getColor(R.color.graph_grid));
        leftAxis.setTextColor(getActivity().getResources().getColor(R.color.graph_font));
        leftAxis.setSpaceTop(15f);

        YAxis rightAxis = mLineChart.getAxisRight();
        rightAxis.setEnabled(false);

        Legend l = mLineChart.getLegend();
        l.setPosition(Legend.LegendPosition.BELOW_CHART_CENTER);

        mLineChart.setDescription("");

        mLineChart.invalidate(); // refresh
    }

    public void updateDashboard(){
        Float balance = mOperationsDatasource.sumAllExpenses();
        mBalanceTextView.setText("$" + String.format("%.2f", balance));

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String target = preferences.getString(getActivity().getResources().getString(R.string.settings_target_key), "100.00");
        String income = preferences.getString(getActivity().getResources().getString(R.string.settings_income_key), "10.00");
        Float targetAmount = Float.parseFloat(target);
        Float incomeAmount = Float.parseFloat(income);

        mTargetLabel.setText(MONTHS_TO_TARGET_LABEL + "$" + String.format("%.2f", targetAmount));

        int monthsToTarget = (int) Math.ceil((double) (targetAmount - balance) / incomeAmount);
        if (monthsToTarget < 0) { monthsToTarget = 0; }

        mMonthsToTargetTextView.setText(String.valueOf(monthsToTarget));

        setupLineChart();
    }

    public void verifyAmounts(){
        if(!mCheckManager.verifyMatchAccountExpenses()){
            String differenceAmount = "$" + String.format("%.2f", mCheckManager.getDifferenceAmount());
            mCheckTextView.setText(CheckManager.AMOUNT_MISMATCH + differenceAmount);
            mCheckTextView.setBackgroundColor(getResources().getColor(R.color.checkError));
        } else {
            mCheckTextView.setText(CheckManager.AMOUNT_CORRECT);
            mCheckTextView.setBackgroundColor(getResources().getColor(R.color.checkOk));
        }
    }

    public int getCurrentYear(){
        Date now =  new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(now);
        return cal.get(Calendar.YEAR);
    }
}