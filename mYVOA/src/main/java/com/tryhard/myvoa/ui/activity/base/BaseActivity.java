package com.tryhard.myvoa.ui.activity.base;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.tryhard.myvoa.R;

public class BaseActivity extends FragmentActivity {


    private final String TAG = this.getClass().getSimpleName();
    private final Context mContext = this;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
    }


}
