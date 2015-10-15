package com.tryhard.myvoa.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBopenHelper extends SQLiteOpenHelper {
	

	public DBopenHelper(Context context){

		super(context,"MyDB.db",null,1);
	
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {

	}

	public void onMyCreate(SQLiteDatabase db,String tableName) {
		db.execSQL("create table " + tableName +"(_id integer primary key,title varchar(500),date varchar(20),website varchar(500),scan varchar(20),bitmap blob,column int)");
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}
	
}
