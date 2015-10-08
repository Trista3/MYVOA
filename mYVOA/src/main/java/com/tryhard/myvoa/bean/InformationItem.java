package com.tryhard.myvoa.bean;


import android.graphics.Bitmap;

import java.io.Serializable;

public class InformationItem implements Serializable {

	private String mTitle;
	private String mDate;
	private String mWebsite;
	private Integer mId;
	private Bitmap mBitmap;
	Boolean isScaned = false;

	public Boolean getIsScaned() {
		return isScaned;
	}

	public void setIsScaned(Boolean isScaned) {
		this.isScaned = isScaned;
	}

	public InformationItem(int id,String title,String date,String website,Bitmap bitmap){
		mId = id;
		mTitle = title;
		mDate = date;
		mWebsite = website;
		mBitmap = bitmap;
	}
	public void setId(Integer id) {
		mId = id;
	}
	public InformationItem(){
		
	}
	
	public Integer getId() {
		return mId;
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
	public Bitmap getBitmap() {
		return mBitmap;
	}
	public void setBitmap(Bitmap bitmap) {
		this.mBitmap = bitmap;
	}
	
}
