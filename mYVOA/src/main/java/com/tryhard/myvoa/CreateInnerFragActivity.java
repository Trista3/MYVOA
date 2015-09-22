package com.tryhard.myvoa;

import android.content.Context;
import android.support.v4.app.Fragment;

public class CreateInnerFragActivity extends InnerListActivity {
	//常量
	private static final String TAG = "CreateInnerFragActivity";
	
	//逻辑变量
	private Context appContext;
	
	@Override
	protected Fragment createFragment() {
		return new InnerListFragment(); //创建被该Activity托管的Fragment
	}
}