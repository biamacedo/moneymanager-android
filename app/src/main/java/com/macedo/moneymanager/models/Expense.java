package com.macedo.moneymanager.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

/**
 * Created by Beatriz on 06/09/2015.
 */
public class Expense implements Parcelable {

    private int mId;
    private String mTitle;
    private String mDescription;
    private Category mCategory;
    private Float mAmount;
    private Date mDate;

    public Expense(int id, String title, String description, Category category, Float amount, Date date) {
        mId = id;
        mTitle = title;
        mDescription = description;
        mCategory = category;
        mAmount = amount;
        mDate = date;
    }

    public Expense(String title, String description, Category category, Float amount, Date date) {
        mId = -1;
        mTitle = title;
        mDescription = description;
        mCategory = category;
        mAmount = amount;
        mDate = date;
    }

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        mDescription = description;
    }

    public Category getCategory() {
        return mCategory;
    }

    public void setCategory(Category category) {
        mCategory = category;
    }

    public Float getAmount() {
        return mAmount;
    }

    public void setAmount(Float amount) {
        mAmount = amount;
    }

    public Date getDate() {
        return mDate;
    }

    public void setDate(Date date) {
        mDate = date;
    }

 /* everything below here is for implementing Parcelable */

    // 99.9% of the time you can just ignore this
    public int describeContents() {
        return 0;
    }

    // write your object's data to the passed-in Parcel
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(mId);
        out.writeString(mTitle);
        out.writeString(mDescription);
        out.writeInt(mCategory.getId());
        out.writeString(mCategory.getName());
        out.writeString(mCategory.getType());
        out.writeString(mCategory.getIconName());
        out.writeFloat(mAmount);
        out.writeLong((long) mDate.getTime() / 1000);
    }

    // this is used to regenerate your object. All Parcelables must have a CREATOR that implements these two methods
    public static final Parcelable.Creator<Expense> CREATOR = new Parcelable.Creator<Expense>() {
        public Expense createFromParcel(Parcel in) {
            return new Expense(in);
        }

        public Expense[] newArray(int size) {
            return new Expense[size];
        }
    };

    // example constructor that takes a Parcel and gives you an object populated with it's values
    private Expense(Parcel in) {
        mId = in.readInt();
        mTitle = in.readString();
        mDescription = in.readString();
        mCategory = new Category(   in.readInt(),
                                    in.readString(),
                                    in.readString(),
                                    in.readString());
        mAmount = in.readFloat();
        mDate = new Date((long) in.readLong()*1000);
    }
}
