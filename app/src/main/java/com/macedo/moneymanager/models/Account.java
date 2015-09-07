package com.macedo.moneymanager.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Beatriz on 06/09/2015.
 */
public class Account implements Parcelable{

    private int mId;
    private String mName;
    private Category mCategory;
    private Double mAmount;

    public Account(int id, String name, Category category, Double amount) {
        mId = id;
        mName = name;
        mCategory = category;
        mAmount = amount;
    }

    public Account(String name, Category category, Double amount) {
        mId = -1;
        mName = name;
        mCategory = category;
        mAmount = amount;
    }

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public Double getAmount() {
        return mAmount;
    }

    public void setAmount(Double amount) {
        mAmount = amount;
    }

    public Category getCategory() {
        return mCategory;
    }

    public void setCategory(Category category) {
        mCategory = category;
    }

     /* everything below here is for implementing Parcelable */

    // 99.9% of the time you can just ignore this
    public int describeContents() {
        return 0;
    }

    // write your object's data to the passed-in Parcel
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(mId);
        out.writeString(mName);
        out.writeInt(mCategory.getId());
        out.writeString(mCategory.getName());
        out.writeString(mCategory.getType());
        out.writeString(mCategory.getIconName());
        out.writeDouble(mAmount);
    }

    // this is used to regenerate your object. All Parcelables must have a CREATOR that implements these two methods
    public static final Parcelable.Creator<Account> CREATOR = new Parcelable.Creator<Account>() {
        public Account createFromParcel(Parcel in) {
            return new Account(in);
        }

        public Account[] newArray(int size) {
            return new Account[size];
        }
    };

    // example constructor that takes a Parcel and gives you an object populated with it's values
    private Account(Parcel in) {
        mId = in.readInt();
        mName = in.readString();
        mCategory = new Category(   in.readInt(),
                in.readString(),
                in.readString(),
                in.readString());
        mAmount = in.readDouble();
    }
}
