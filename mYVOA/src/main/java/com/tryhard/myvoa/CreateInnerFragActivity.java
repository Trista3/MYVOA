package com.tryhard.myvoa;

import android.content.Context;
import android.support.v4.app.Fragment;

public class CreateInnerFragActivity extends InnerListActivity {
	//����
	private static final String TAG = "CreateInnerFragActivity";
	
	//�߼�����
	private Context appContext;
	
	@Override
	protected Fragment createFragment() {
		return new InnerListFragment(); //��������Activity�йܵ�Fragment
	}
}