package com.macedo.moneymanager.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.macedo.moneymanager.R;
import com.macedo.moneymanager.models.Category;

import java.util.ArrayList;

/**
 * Created by Beatriz on 06/09/2015.
 */
public class CategorySpinnerListAdapter extends ArrayAdapter<Category> {

    ArrayList<Category> mCategories;
    Context mContext;

    public CategorySpinnerListAdapter(Context context, int resource, int textViewResourceId, ArrayList<Category> categories) {
        super(context, resource, textViewResourceId, categories);
        mContext = context;
        mCategories = categories;
    }

    @Override
    public View getDropDownView(int position, View convertView,ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    public View getCustomView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(R.layout.category_spinner_list_item, parent, false);

        ImageView categoryIcon=(ImageView) convertView.findViewById(R.id.categoryIcon);
        int iconDrawable = mContext.getResources().getIdentifier(mCategories.get(position).getIconName(), "drawable", mContext.getPackageName());
        categoryIcon.setImageResource(iconDrawable);

        TextView categoryLabel = (TextView) convertView.findViewById(R.id.categoryLabel);
        categoryLabel.setText(mCategories.get(position).getName());

        return convertView;
    }
}
