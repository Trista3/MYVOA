package com.tryhard.myvoa.ui.activity;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.tryhard.myvoa.R;
import com.tryhard.myvoa.ui.activity.base.BaseActivity;

public abstract class ArticleListSimpleActivity extends BaseActivity {
		protected abstract Fragment createFragment();
		
		@Override
		    protected void onCreate(Bundle savedInstanceState) {
		        super.onCreate(savedInstanceState);

		        setContentView(R.layout.fragment_activity);
		        
		        FragmentManager fm = getSupportFragmentManager();
		        Fragment fragment = fm.findFragmentById(R.id.innerFragContainer);
		        
		        if(fragment == null){
		        	fragment =  createFragment();
		        	fm.beginTransaction().add(R.id.innerFragContainer, fragment).commit();
		        }
		 }
}
