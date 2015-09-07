package com.macedo.moneymanager.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.macedo.moneymanager.R;
import com.macedo.moneymanager.models.Account;

import java.util.List;

/**
 * Created by Beatriz on 06/09/2015.
 */
public class AccountItemListAdapter extends BaseAdapter {

    private Context mContext;
    private List<Account> mAccountItems;

    public AccountItemListAdapter(Context context, List<Account> accountItems) {
        this.mContext = context;
        this.mAccountItems = accountItems;
    }

    @Override
    public int getCount() {
        return mAccountItems.size();
    }

    @Override
    public Object getItem(int position) {
        return mAccountItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return mAccountItems.indexOf(getItem(position));
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.accounts_list_item, null);

            holder = new ViewHolder();
            holder.categoryIcon = (ImageView) convertView.findViewById(R.id.categoryIcon);
            holder.accountNameLabel = (TextView) convertView.findViewById(R.id.accountNameLabel);
            holder.accountAmountLabel = (TextView) convertView.findViewById(R.id.accountAmountLabel);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Account account = mAccountItems.get(position);

        if (account.getCategory().getIconName() != null){
            try {
                int iconDrawable = mContext.getResources().getIdentifier(account.getCategory().getIconName(), "drawable", mContext.getPackageName());
                holder.categoryIcon.setImageResource(iconDrawable);
            } catch (Exception e){
                holder.categoryIcon.setImageResource(R.drawable.ic_question);
            }
        } else {
            holder.categoryIcon.setImageResource(R.drawable.ic_question);
        }

        holder.accountNameLabel.setText(account.getName());
        holder.accountAmountLabel.setText("$" + String.format("%.2f", account.getAmount()));

        return convertView;
    }

    private static class ViewHolder {
        ImageView categoryIcon;
        TextView accountNameLabel;
        TextView accountAmountLabel;
    }
}
