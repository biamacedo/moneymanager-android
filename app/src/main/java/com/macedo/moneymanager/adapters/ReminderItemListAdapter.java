package com.macedo.moneymanager.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.macedo.moneymanager.R;
import com.macedo.moneymanager.models.Reminder;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by Beatriz on 06/09/2015.
 */
public class ReminderItemListAdapter extends BaseAdapter {

    private final String HEADER_DATE_FORMAT = "MM/dd/yyyy";

    private Context mContext;
    private List<Reminder> mReminderItems;
    private List<Integer> mSelectedItems;
    private boolean mSelectMode;

    public ReminderItemListAdapter(Context context, List<Reminder> reminderItems, List<Integer> selectedItems) {
        this.mContext = context;
        this.mReminderItems = reminderItems;
        this.mSelectedItems = selectedItems;
        mSelectMode = false;
    }

    public void setSelectMode(boolean selectMode) {
        mSelectMode = selectMode;
    }

    @Override
    public int getCount() {
        return mReminderItems.size();
    }

    @Override
    public Object getItem(int position) {
        return mReminderItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return mReminderItems.indexOf(getItem(position));
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.list_item_reminder, null);

            holder = new ViewHolder();
            holder.reminderIcon = (ImageView) convertView.findViewById(R.id.reminderIcon);
            holder.reminderNameLabel = (TextView) convertView.findViewById(R.id.reminderNameLabel);
            holder.reminderDateLabel = (TextView) convertView.findViewById(R.id.reminderDateLabel);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Reminder reminder = mReminderItems.get(position);

        if (mSelectMode){
            if (mSelectedItems.contains(Integer.valueOf(position))){
                holder.reminderIcon.setImageResource(R.drawable.ic_check_square);
            } else {
                holder.reminderIcon.setImageResource(R.drawable.ic_square);
            }
        } else {
            holder.reminderIcon.setImageResource(R.drawable.ic_calendar);
        }

        holder.reminderNameLabel.setText(reminder.getName());
        SimpleDateFormat headerFormatter = new SimpleDateFormat(HEADER_DATE_FORMAT);
        holder.reminderDateLabel.setText(headerFormatter.format(reminder.getDate()));

        return convertView;
    }

    private static class ViewHolder {
        ImageView reminderIcon;
        TextView reminderNameLabel;
        TextView reminderDateLabel;
    }
}
