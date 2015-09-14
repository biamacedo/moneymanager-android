package com.macedo.moneymanager.models;

/**
 * Created by Beatriz on 06/09/2015.
 */
public class Category {

    private int mId;
    private String mName;
    private String mType; /*Account or Operation Category*/
    private String mIconName;

    public Category(int id) {
        mId = id;
    }

    public Category(int id, String name, String type, String iconName) {
        mId = id;
        mName = name;
        mType = type;
        mIconName = iconName;
    }

    public Category(String name, String type, String iconName) {
        mId = -1;
        mName = name;
        mType = type;
        mIconName = iconName;
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

    public String getType() {
        return mType;
    }

    public void setType(String type) {
        mType = type;
    }

    public String getIconName() {
        return mIconName;
    }

    public void setIconName(String iconName) {
        mIconName = iconName;
    }

    @Override
    public boolean equals(Object obj) {
        Category category = (Category) obj;
        if (this.getId() == category.getId())
            return true;
        return false;
    }
}
