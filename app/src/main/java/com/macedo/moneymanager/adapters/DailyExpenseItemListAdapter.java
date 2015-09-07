package com.macedo.moneymanager.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.macedo.moneymanager.R;
import com.macedo.moneymanager.models.Expense;

import java.text.SimpleDateFormat;
import java.util.List;

import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;

/**
 * Created by Beatriz on 06/09/2015.
 */
public class DailyExpenseItemListAdapter extends BaseAdapter implements StickyListHeadersAdapter {

    private Context mContext;
    private List<Expense> mExpenseItems;

    private final String HEADER_DATE_FORMAT = "MM/dd/yyyy";

    public DailyExpenseItemListAdapter(Context context, List<Expense> expenseItems) {
        this.mContext = context;
        this.mExpenseItems = expenseItems;
    }

    @Override
    public int getCount() {
        return mExpenseItems.size();
    }

    @Override
    public Object getItem(int position) {
        return mExpenseItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return mExpenseItems.indexOf(getItem(position));
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.daily_expenses_list_item, null);

            holder = new ViewHolder();
            holder.expenseIcon = (ImageView) convertView.findViewById(R.id.categoryIcon);
            holder.expenseTitleLabel = (TextView) convertView.findViewById(R.id.expenseTitleLabel);
            holder.expenseAmountLabel = (TextView) convertView.findViewById(R.id.accountAmountLabel);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Expense expense = mExpenseItems.get(position);

        if (expense.getCategory().getIconName() != null){
            try {
                int iconDrawable = mContext.getResources().getIdentifier(expense.getCategory().getIconName(), "drawable", mContext.getPackageName());
                holder.expenseIcon.setImageResource(iconDrawable);
            } catch (Exception e){
                holder.expenseIcon.setImageResource(R.drawable.ic_question);
            }
        } else {
            holder.expenseIcon.setImageResource(R.drawable.ic_question);
        }

        holder.expenseTitleLabel.setText(expense.getTitle());
        holder.expenseAmountLabel.setText("$" + String.format("%.2f", expense.getAmount()));

        return convertView;
    }

    @Override
    public View getHeaderView(int position, View convertView, ViewGroup parent) {
        HeaderViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.daily_expenses_section_header, null);

            holder = new HeaderViewHolder();
            holder.headerLabel = (TextView) convertView.findViewById(R.id.headerLabel);
            convertView.setTag(holder);
        } else {
            holder = (HeaderViewHolder) convertView.getTag();
        }

        Expense expense = mExpenseItems.get(position);
        SimpleDateFormat headerFormatter = new SimpleDateFormat(HEADER_DATE_FORMAT);
        holder.headerLabel.setText(headerFormatter.format(expense.getDate()));

        return convertView;
    }

    @Override
    public long getHeaderId(int position) {
        return mExpenseItems.get(position).getDate().getTime()/1000;
    }

    private static class ViewHolder {
        ImageView expenseIcon;
        TextView expenseTitleLabel;
        TextView expenseAmountLabel;
    }

    private static class HeaderViewHolder {
        TextView headerLabel;
    }
}
