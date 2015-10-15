package com.tryhard.myvoa.ui.activity.base;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;

import com.tryhard.myvoa.R;

public class BaseActivity extends FragmentActivity {


    private final String TAG = this.getClass().getSimpleName();
    private final Context mContext = this;
    private MenuItem mBackUpMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //设置自定义标题栏
   //     requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.activity_base);
   //     getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title_bar);
        getActionBar().show();
        super.onCreate(savedInstanceState);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

}
