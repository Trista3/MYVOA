package com.tryhard.myvoa.ui.fragment;

import java.util.ArrayList;
import java.util.List;

import com.tryhard.myvoa.R;
import com.tryhard.myvoa.bean.Information;
import com.tryhard.myvoa.ui.activity.MainActivity;
import com.tryhard.myvoa.widget.DrawView;

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

public class SortOfCultureFragment extends ListFragment {

	//视图组件
	private ListView view;
	private MyInformationAdapter listFragmentadapter;

	//数据型变量
	private List<Information> mInformations; //装载列表条目

	private int[] color; //小圆心颜色
	//逻辑对象
	private Context mContext;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.sort_of_study_or_culture_fragment,container,false);
		initView(v);
		initLObj();

		return v;
	}

	private void initView(View v){
		view = (ListView)v.findViewById(android.R.id.list);
		initInformationList();//初始化列表

	}

	private void initLObj(){
		listFragmentadapter = new MyInformationAdapter((ArrayList<Information>)mInformations);
		setListAdapter(listFragmentadapter);
		mContext = getActivity();
	}
	
	private void initInformationList(){
		Resources res = getResources();
		String[] eTitle = res.getStringArray(R.array.culture_array_english);
		String[] cTitle = res.getStringArray(R.array.culture_array_chinese);
		String[] website = res.getStringArray(R.array.culture_website);
		color = res.getIntArray(R.array.color);
		mInformations = new ArrayList<Information>();
		
		for(int i=0;i < eTitle.length;i++){
			Information information = new Information();
			information.setCtitle(cTitle[i]);
			information.setEtitle(eTitle[i]);
			information.setWebsite( website[i]);
			mInformations.add(information);
		}
		
	}
	
	private class MyInformationAdapter extends ArrayAdapter<Information>{
		public MyInformationAdapter(ArrayList<Information> Informations){
			super(getActivity(),0,Informations);
		}

		@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			if(convertView == null){
				convertView = getActivity().getLayoutInflater().inflate(R.layout.sorts_item,null);
			}
			Information info = getItem(position);


			TextView Etitle = (TextView)convertView.findViewById(R.id.Etitle);
			Etitle.setText(info.getEtitle());
			TextView Ctitle = (TextView)convertView.findViewById(R.id.Ctitle);
			Ctitle.setText(info.getCtitle());

			DrawView drawView = (DrawView)convertView.findViewById(R.id.drawView);
			drawView.setColor(color[position % 14]);

			return convertView;
		}
		
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		Information info = mInformations.get(position);
		MainActivity.setTabCurrentPosition(1);
		startActivity(MainActivity.makeIntent(mContext, info));
	}
}
