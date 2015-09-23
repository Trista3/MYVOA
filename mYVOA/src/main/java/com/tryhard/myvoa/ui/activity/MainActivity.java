package com.tryhard.myvoa.ui.activity;

import java.util.ArrayList;
import java.util.List;

import com.tryhard.myvoa.ui.activity.base.BaseActivity;
import com.tryhard.myvoa.ui.fragment.CultureFragment;
import com.tryhard.myvoa.R;
import com.tryhard.myvoa.ui.fragment.RecordFragment;
import com.tryhard.myvoa.ui.fragment.StudyFragment;
import com.tryhard.myvoa.db.InformationItemManager;

import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.app.ActionBarActivity;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends BaseActivity {


    public static final String INNER_WEBSITE = "website";


    private ViewPager mViewPager;
    private TextView mStudyView, mCultureView, mRecordView;
    private ImageView mAnimateLineImage;


    private List<View> mListViews;
    private int offset = 0;
    private int currentIndex = 0;
    private int bmpW;


    private ArrayList<Fragment> fragmentsList;
    private StudyFragment studyFragment;
    private CultureFragment studyFragment2;
    private RecordFragment studyFragment3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        fragmentsList = new ArrayList<Fragment>();
        studyFragment = new StudyFragment();
        studyFragment2 = new CultureFragment();
        studyFragment3 = new RecordFragment();
        fragmentsList.add(studyFragment);
        fragmentsList.add(studyFragment2);
        fragmentsList.add(studyFragment3);

        RecordFragment.recordFragmentDBmanager = new InformationItemManager(this, "recordTable");

        InitTextView();

        InitImageView();//��ʼ����ɫС���ߵ���ͼ

        InitViewPager();


    }


    private void InitTextView() {
        mStudyView = (TextView) findViewById(R.id.study);
        mCultureView = (TextView) findViewById(R.id.culture);
        mRecordView = (TextView) findViewById(R.id.record);

        mStudyView.setOnClickListener(new MyOnClickListener(0));
        mCultureView.setOnClickListener(new MyOnClickListener(1));
        mRecordView.setOnClickListener(new MyOnClickListener(2));

    }

    public class MyOnClickListener implements View.OnClickListener {
        private int index = 0;

        public MyOnClickListener(int i) {
            index = i;
        }

        @Override
        public void onClick(View v) {
            mViewPager.setCurrentItem(index);
        }
    }

    public void InitImageView() {
        mAnimateLineImage = (ImageView) findViewById(R.id.cursor);
        bmpW = BitmapFactory.decodeResource(getResources(), R.drawable.line).getWidth();
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int screenW = dm.widthPixels;
        offset = (screenW / 3 - bmpW) / 2;
        Matrix matrix = new Matrix();
        matrix.postTranslate(offset, 0);
        mAnimateLineImage.setImageMatrix(matrix);
    }

    private void InitViewPager() {
        mViewPager = (ViewPager) findViewById(R.id.viewPager);
        mListViews = new ArrayList<View>();
        LayoutInflater inflater = getLayoutInflater();
        mListViews.add(inflater.inflate(R.layout.study_list, null));
        mListViews.add(inflater.inflate(R.layout.culture_list, null));
        mListViews.add(inflater.inflate(R.layout.record_list, null));
        mViewPager.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));
        mViewPager.setCurrentItem(0);
        mViewPager.setOnPageChangeListener(new MyOnPageChangeListener());
    }

    public class MyPagerAdapter extends FragmentStatePagerAdapter {

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int arg0) {
            Fragment sFrag = fragmentsList.get(arg0);
            return sFrag;
        }

        @Override
        public int getCount() {
            return fragmentsList.size();
        }

    }

    public class MyOnPageChangeListener implements OnPageChangeListener {
        int one = offset * 2 + bmpW;
        int two = one * 2;

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        @Override
        public void onPageSelected(int arg0) {
            Animation animation = null;
            switch (arg0) {
                case 0:
                    if (currentIndex == 1) {
                        animation = new TranslateAnimation(one, 0, 0, 0);
                    } else if (currentIndex == 2) {
                        animation = new TranslateAnimation(two, 0, 0, 0);
                    }
                    break;
                case 1:
                    if (currentIndex == 0) {
                        animation = new TranslateAnimation(offset, one, 0, 0);
                    } else if (currentIndex == 2) {
                        animation = new TranslateAnimation(two, one, 0, 0);
                    }
                    break;
                case 2:
                    if (currentIndex == 0) {
                        animation = new TranslateAnimation(offset, two, 0, 0);
                    } else if (currentIndex == 1) {
                        animation = new TranslateAnimation(one, two, 0, 0);
                    }
                    break;
            }
            currentIndex = arg0;
            animation.setFillAfter(true);//True��ͼƬֹͣ�ڶ�������״̬
            animation.setDuration(300);
            mAnimateLineImage.startAnimation(animation);
        }
    }

}