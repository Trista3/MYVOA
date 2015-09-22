package com.tryhard.myvoa;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

public abstract class InnerListActivity extends FragmentActivity {
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
