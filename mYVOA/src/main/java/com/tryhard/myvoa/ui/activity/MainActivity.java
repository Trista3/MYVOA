package com.tryhard.myvoa.ui.activity;

import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;
import com.tryhard.myvoa.bean.Information;
import com.tryhard.myvoa.db.BrowsingItemDao;
import com.tryhard.myvoa.ui.activity.base.BaseActivity;
import com.tryhard.myvoa.ui.fragment.SortOfCultureFragment;
import com.tryhard.myvoa.R;
import com.tryhard.myvoa.ui.fragment.BrowsingHistoryFragment;
import com.tryhard.myvoa.ui.fragment.SortOfStudyFragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;


public class MainActivity extends BaseActivity {
    //视图组件
    private static ViewPager mViewPager;
    SmartTabLayout viewPagerTab;

    //控制下划线当前位置变量
    public static int currentPosition = 0;
   @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BrowsingHistoryFragment.mBrowsingItemDao = new BrowsingItemDao(getApplicationContext());

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
        mViewPager.setCurrentItem(currentPosition);
        viewPagerTab = (SmartTabLayout) findViewById(R.id.viewpagertab);

        viewPagerTab.setViewPager(mViewPager);
    }

    public static void setTabCurrentPosition(int position){
        currentPosition = position;
    }

    public static Intent makeIntent(Context mContext, Information info){
        return new Intent(mContext, ListOfArticleSimpleActivity.class).putExtra(ListOfArticleSimpleActivity.extra_data, info);
    }
}