package com.tryhard.myvoa.ui.activity.base;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.Menu;
import com.tryhard.myvoa.R;

public class BaseActivity extends FragmentActivity {


    private final String TAG = this.getClass().getSimpleName();
    protected final Context mContext = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_base);
        getActionBar().show();
        super.onCreate(savedInstanceState);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

}
