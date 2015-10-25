package com.tryhard.myvoa.bean;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

/**
 * Created by Chen on 2015/10/22.
 */
@DatabaseTable(tableName = "BrowsingItem")
public class BrowsingItem {
    public static final String WEBSITE_B_FIELD_NAME = "browse_website_column";
    public static final String BITMAPOS_B_FIELD_NAME = "browse_bitmapos_column";

    @DatabaseField(generatedId = true)
    private int id;
    @DatabaseField
    private String mTitle;
    @DatabaseField
    private String mDate;
    @DatabaseField(unique = true, columnName = WEBSITE_B_FIELD_NAME)
    private String mWebsite;
    @DatabaseField(canBeNull = true, columnName = BITMAPOS_B_FIELD_NAME,dataType= DataType.BYTE_ARRAY)
    private byte[] mBitmapOs = null;

    public BrowsingItem(String title, String date, String website, byte[] bitOs){
        super();
        mTitle = title;
        mDate = date;
        mWebsite = website;
        mBitmapOs = bitOs;
    }

    public BrowsingItem(){
        super();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public byte[] getmBitmapOs() {
        return mBitmapOs;
    }

    public void setmBitmapOs(byte[] mBitmapOs) {
        this.mBitmapOs = mBitmapOs;
    }

    public String getmDate() {
        return mDate;
    }

    public void setmDate(String mDate) {
        this.mDate = mDate;
    }

    public String getmTitle() {
        return mTitle;
    }

    public void setmTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public String getmWebsite() {
        return mWebsite;
    }

    public void setmWebsite(String mWebsite) {
        this.mWebsite = mWebsite;
    }
}

