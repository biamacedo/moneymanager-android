package com.macedo.moneymanager.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.macedo.moneymanager.R;
import com.macedo.moneymanager.models.MonthOperation;

import java.util.List;

/**
 * Created by Beatriz on 06/09/2015.
 */
public class MonthlyOperationItemListAdapter extends BaseAdapter {

    private Context mContext;
    private List<MonthOperation> mMonthOperationItems;

    public MonthlyOperationItemListAdapter(Context context, List<MonthOperation> monthOperationItems) {
        this.mContext = context;
        this.mMonthOperationItems = monthOperationItems;
    }

    @Override
    public int getCount() {
        return mMonthOperationItems.size();
    }

    @Override
    public Object getItem(int position) {
        return mMonthOperationItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return mMonthOperationItems.indexOf(getItem(position));
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.list_item_monthly_operations, null);

            holder = new ViewHolder();
            holder.monthLabel = (TextView) convertView.findViewById(R.id.monthLabel);
            holder.totalAmount = (TextView) convertView.findViewById(R.id.totalAmount);
            holder.expensePercentage = (TextView) convertView.findViewById(R.id.expensePercentage);
            holder.winAmount = (TextView) convertView.findViewById(R.id.winAmount);
            holder.lossAmount = (TextView) convertView.findViewById(R.id.lossAmount);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        MonthOperation monthOperation = mMonthOperationItems.get(position);

        holder.monthLabel.setText(monthOperation.getMonth());
        holder.totalAmount.setText("$" + String.format("%.2f", monthOperation.getAmount()));
        holder.expensePercentage.setText("(" + String.format("%.2f", monthOperation.getPercentage()) + "%)");
        holder.winAmount.setText("$" + String.format("%.2f", monthOperation.getWinAmount()));
        holder.lossAmount.setText("$" + String.format("%.2f", monthOperation.getLossAmount()));

        return convertView;
    }

    private static class ViewHolder {
        TextView monthLabel;
        TextView totalAmount;
        TextView expensePercentage;
        TextView winAmount;
        TextView lossAmount;
    }

}
