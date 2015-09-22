package com.tryhard.myvoa;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class PreferencesManager{
	//Âß¼­¶ÔÏó
	private Context context;
	private String mInfoName;
	
	public PreferencesManager(Context context,String infoName){
		this.context = context;
		this.mInfoName = infoName;
	}

	public void save(boolean isBuildTable){
		SharedPreferences preferences = context.getSharedPreferences("tableBooleans",Context.MODE_PRIVATE);
		Editor editor = preferences .edit();
		editor.putBoolean(mInfoName,isBuildTable);
		editor.commit();
	}

	
	public Map<String,String> getPreferences(){
		Map<String,String> params = new HashMap<String,String>();
		SharedPreferences preferences = context.getSharedPreferences("tableBooleans",Context.MODE_PRIVATE);
		params.put(mInfoName,String.valueOf(preferences.getBoolean(mInfoName, false)));
		return params;
		
	}
}
