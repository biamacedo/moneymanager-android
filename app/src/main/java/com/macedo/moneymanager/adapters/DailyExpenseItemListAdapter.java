package com.macedo.moneymanager.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.macedo.moneymanager.R;
import com.macedo.moneymanager.models.Expense;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;

/**
 * Created by Beatriz on 06/09/2015.
 */
public class DailyExpenseItemListAdapter extends BaseAdapter implements StickyListHeadersAdapter {

    private Context mContext;
    private List<Expense> mExpenseItems;
    private List<Integer> mSelectedItems;
    private boolean mSelectMode;

    private final String HEADER_DATE_FORMAT = "MM/dd/yyyy";

    public DailyExpenseItemListAdapter(Context context, List<Expense> expenseItems, List<Integer> selectedItems) {
        this.mContext = context;
        this.mExpenseItems = expenseItems;
        this.mSelectedItems = selectedItems;
        mSelectMode = false;
    }

    public void setSelectMode(boolean selectMode) {
        mSelectMode = selectMode;
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.list_item_daily_expenses, null);

            holder = new ViewHolder();
            holder.expenseIcon = (ImageView) convertView.findViewById(R.id.categoryIcon);
            holder.expenseTitleLabel = (TextView) convertView.findViewById(R.id.expenseTitleLabel);
            holder.expenseAmountLabel = (TextView) convertView.findViewById(R.id.expenseAmountLabel);
            holder.backgroundLayout = (RelativeLayout) convertView.findViewById(R.id.backgroundLayout);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Expense expense = mExpenseItems.get(position);

        if (mSelectMode){
            if (mSelectedItems.contains(Integer.valueOf(position))){
                holder.expenseIcon.setImageResource(R.drawable.ic_check_square);
            } else {
                holder.expenseIcon.setImageResource(R.drawable.ic_square);
            }
        } else {
            if (expense.getCategory().getIconName() != null) {
                try {
                    int iconDrawable = mContext.getResources().getIdentifier(expense.getCategory().getIconName(), "drawable", mContext.getPackageName());
                    holder.expenseIcon.setImageResource(iconDrawable);
                } catch (Exception e) {
                    holder.expenseIcon.setImageResource(R.drawable.ic_question);
                }
            } else {
                holder.expenseIcon.setImageResource(R.drawable.ic_question);
            }
        }

        holder.expenseTitleLabel.setText(expense.getTitle());
        holder.expenseAmountLabel.setText("$" + String.format("%.2f", expense.getAmount()));

        if (isFooter(position)){
            holder.backgroundLayout.setBackgroundDrawable(ContextCompat.getDrawable(mContext, R.drawable.section_footer_background));
        } else {
            holder.backgroundLayout.setBackgroundDrawable(ContextCompat.getDrawable(mContext, R.color.content_bkg));
        }

        return convertView;
    }

    private boolean isFooter(int position){
        if (position == mExpenseItems.size()-1){
            return true;
        }
        Date currentExpenseDate = mExpenseItems.get(position).getDate();
        Date nextExpenseDate = mExpenseItems.get(position+1).getDate();

        Calendar currentCalendar = Calendar.getInstance();
        currentCalendar.setTime(currentExpenseDate);
        Calendar nextCalendar = Calendar.getInstance();
        nextCalendar.setTime(nextExpenseDate);

        if (currentCalendar.get(Calendar.YEAR) == nextCalendar.get(Calendar.YEAR) &&
            currentCalendar.get(Calendar.MONTH) == nextCalendar.get(Calendar.MONTH) &&
            currentCalendar.get(Calendar.DAY_OF_MONTH) == nextCalendar.get(Calendar.DAY_OF_MONTH)){
            // This item has the same date as the next item so its not a footer
            return false;
        } else {
            return true;
        }

    }

    @Override
    public View getHeaderView(int position, View convertView, ViewGroup parent) {
        HeaderViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.section_header_daily_expenses, null);

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
        RelativeLayout backgroundLayout;
    }

    private static class HeaderViewHolder {
        TextView headerLabel;
    }
}
