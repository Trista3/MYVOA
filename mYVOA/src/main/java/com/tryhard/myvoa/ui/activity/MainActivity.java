package com.tryhard.myvoa.ui.activity;

import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;
import com.tryhard.myvoa.bean.Information;
import com.tryhard.myvoa.db.DBopenHelper;
import com.tryhard.myvoa.ui.activity.base.BaseActivity;
import com.tryhard.myvoa.ui.fragment.SortOfCultureFragment;
import com.tryhard.myvoa.R;
import com.tryhard.myvoa.ui.fragment.BrowsingHistoryFragment;
import com.tryhard.myvoa.ui.fragment.SortOfStudyFragment;
import com.tryhard.myvoa.db.InformationItemManager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuInflater;

public class MainActivity extends BaseActivity {
    //常量
    public static final String INNER_WEBSITE = "website";

    //视图组件
    private ViewPager mViewPager;
    SmartTabLayout viewPagerTab;

    //逻辑对象
    public DBopenHelper dbOpenHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dbOpenHelper = new DBopenHelper(this);
        dbOpenHelper.getWritableDatabase().execSQL("create table if not exists recordTable(_id integer primary key,title varchar(500),date varchar(20),website varchar(500),scan varchar(20),bitmap blob,column int)");
        BrowsingHistoryFragment.recordFragmentDBmanager = new InformationItemManager(this, "recordTable");

        InitViewPager();//初始化界面
    }

    private void InitViewPager() {
        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        FragmentPagerItemAdapter adapter = new FragmentPagerItemAdapter(
                getSupportFragmentManager(), FragmentPagerItems.with(this)
                .add(R.string.study, SortOfStudyFragment.class)
                .add(R.string.culture, SortOfCultureFragment.class)
                .add(R.string.record, BrowsingHistoryFragment.class)
                .create());

        mViewPager.setAdapter(adapter);
        mViewPager.setCurrentItem(0);
        viewPagerTab = (SmartTabLayout) findViewById(R.id.viewpagertab);
        viewPagerTab.setViewPager(mViewPager);
    }

    public static Intent makeIntent(Context mContext, Information info){
        return new Intent(mContext, ListOfArticleSimpleActivity.class).putExtra(ListOfArticleSimpleActivity.extra_data, info);
    }
}