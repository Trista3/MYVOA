package com.tryhard.myvoa.ui.fragment;

import java.util.ArrayList;
import java.util.List;

import com.tryhard.myvoa.R;
import com.tryhard.myvoa.bean.Information;
import com.tryhard.myvoa.ui.activity.MainActivity;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class SortOfStudyFragment extends ListFragment {
    //常量
    private final static String WEBSITE = "http://www.51voa.com";
    //数据型变量
    private List<Information> mInformations;
    private int[] colorIds = new int[]{
            R.color.c1, R.color.c2 , R.color.c3,
            R.color.c4, R.color.c5 , R.color.c6,
            R.color.c7, R.color.c8 , R.color.c9,
            R.color.c10, R.color.c11 , R.color.c12,
            R.color.c13, R.color.c14
    };
    //逻辑对象
    private Context mContext;
    private List<String> websites;
    //视图组件
    private MyInformationAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.sort_of_study_or_culture_fragment,container,false);
        initView(v);
        initLgObj();

        return v;
    }


    private void initView(View v){
        initInformationList();
    }

    private void initLgObj(){
        adapter = new MyInformationAdapter((ArrayList<Information>)mInformations);
        setListAdapter(adapter);
        mContext = getActivity();
    }
    private void initInformationList() {

        Resources res = getResources();
        String[] eTitle = res.getStringArray(R.array.study_array_english);
        String[] cTitle = res.getStringArray(R.array.study_array_chinese);
        String[] website = res.getStringArray(R.array.study_website);
        mInformations = new ArrayList<Information>();

        for (int i = 0; i < eTitle.length; i++) {
            Information information = new Information();
            information.setCtitle(cTitle[i]);
            information.setEtitle(eTitle[i]);
            information.setWebsite( website[i]);
            mInformations.add(information);
        }
    }

    private class MyInformationAdapter extends ArrayAdapter<Information> {
        public MyInformationAdapter(ArrayList<Information> Informations) {
            super(getActivity(), 0, Informations);
        }

        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getActivity().getLayoutInflater().inflate(R.layout.sorts_item, null);
            }

            Information info = getItem(position);

            TextView Etitle = (TextView) convertView.findViewById(R.id.Etitle);
            Etitle.setText(info.getEtitle());
            TextView Ctitle = (TextView) convertView.findViewById(R.id.Ctitle);
            Ctitle.setText(info.getCtitle());

            Resources res = getResources();
            View shapeImage = convertView.findViewById(R.id.drawView);
            shapeImage.setBackgroundColor(res.getColor(colorIds[position % 14]));
            return convertView;
        }

    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Information info = mInformations.get(position);
        MainActivity.setTabCurrentPosition(0);
        startActivity(MainActivity.makeIntent(mContext, info));
    }

}
