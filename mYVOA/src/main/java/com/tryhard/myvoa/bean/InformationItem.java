package com.tryhard.myvoa.bean;


import android.graphics.Bitmap;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Serializable;

@DatabaseTable(tableName = "InformationItem")
public class InformationItem implements Serializable {

	public static final String WEBSITE_FIELD_NAME = "website_column";
	public static final String BITMAPOS_FIELD_NAME = "bitmapos_column";
	public static final String ISSCANED_FIELD_NAME = "isscaned_column";
	public static final String SORT_FIELD_NAME = "from_sort_of_information";

	@DatabaseField(generatedId = true)
	private int id;
	@DatabaseField
	private String mTitle;
	@DatabaseField
	private String mDate;
	@DatabaseField(unique = true, columnName = WEBSITE_FIELD_NAME)
	private String mWebsite;
	@DatabaseField(canBeNull = true, columnName = BITMAPOS_FIELD_NAME,dataType= DataType.BYTE_ARRAY)
	private byte[] mBitmapOs = null;
	@DatabaseField(columnName = SORT_FIELD_NAME)
	private String mFromSortOfInformation;
	@DatabaseField(defaultValue = "false", columnName = ISSCANED_FIELD_NAME)
	Boolean isScaned = false;

	//private Integer mId;

	public Boolean getIsScaned() {
		return isScaned;
	}

	public void setIsScaned(Boolean isScaned) {
		this.isScaned = isScaned;
	}

	public InformationItem(){
		super();
	}
	
	public String getTitle() {
		return mTitle;
	}
	public void setTitle(String title) {
		mTitle = title;
	}
	public String getDate() {
		return mDate;
	}
	public void setDate(String date) {
		mDate = date;
	}
	public String getWebsite() {
		return mWebsite;
	}
	public void setWebsite(String website) {
		mWebsite = website;
	}

	public String getmFromSortOfInformation() {
		return mFromSortOfInformation;
	}

	public void setmFromSortOfInformation(String mFromSortOfInformation) {
		this.mFromSortOfInformation = mFromSortOfInformation;
	}

	public  byte[] getBitmapOs() {
		return mBitmapOs;
	}
	public void setBitmapOs( byte[] bitmapOs) {
		this.mBitmapOs = bitmapOs;
	}
	
}
