package com.macedo.moneymanager.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

/**
 * Created by Beatriz on 13/09/2015.
 */
public class Reminder implements Parcelable {

    public static final String REMINDER_EXTRA = "REMINDER";

    private int mId;
    private String mName;
    private Date mStartDate;
    private Date mEndDate;
    private Date mNextAlertDate;

    public Reminder(int id, String name, Date startDate, Date endDate, Date nextAlertDate) {
        mId = id;
        mName = name;
        mStartDate = startDate;
        mEndDate = endDate;
        mNextAlertDate = nextAlertDate;
    }

    // Constructor for new reminder, the next alert date will be the start date
    public Reminder(String name, Date startDate, Date endDate) {
        mId = -1;
        mName = name;
        mStartDate = startDate;
        mEndDate = endDate;
        mNextAlertDate = startDate;
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

    public Date getStartDate() {
        return mStartDate;
    }

    public void setStartDate(Date startDate) {
        mStartDate = startDate;
    }

    public Date getEndDate() {
        return mEndDate;
    }

    public void setEndDate(Date endDate) {
        mEndDate = endDate;
    }

    public Date getNextAlertDate() {
        return mNextAlertDate;
    }

    public void setNextAlertDate(Date nextAlertDate) {
        mNextAlertDate = nextAlertDate;
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
        out.writeLong(mStartDate.getTime());
        out.writeLong(mEndDate.getTime());
        out.writeLong(mNextAlertDate.getTime());
    }

    // this is used to regenerate your object. All Parcelables must have a CREATOR that implements these two methods
    public static final Parcelable.Creator<Reminder> CREATOR = new Parcelable.Creator<Reminder>() {
        public Reminder createFromParcel(Parcel in) {
            return new Reminder(in);
        }

        public Reminder[] newArray(int size) {
            return new Reminder[size];
        }
    };

    // example constructor that takes a Parcel and gives you an object populated with it's values
    private Reminder(Parcel in) {
        mId = in.readInt();
        mName = in.readString();
        mStartDate = new Date((long) in.readLong()*1000);
        mEndDate = new Date((long) in.readLong()*1000);
        mNextAlertDate = new Date((long) in.readLong()*1000);
    }
}

