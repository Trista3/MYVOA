package com.tryhard.myvoa.ui.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.tryhard.myvoa.util.PreferencesManager;
import com.tryhard.myvoa.R;
import com.tryhard.myvoa.bean.Information;
import com.tryhard.myvoa.db.DBopenHelper;
import com.tryhard.myvoa.ui.activity.ListOfArticleSimpleActivity;
import com.tryhard.myvoa.ui.activity.MainActivity;
import com.tryhard.myvoa.widget.DrawView;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import static android.widget.RelativeLayout.ALIGN_PARENT_RIGHT;

public class SortOfCultureFragment extends ListFragment {
	//常量
	private static final String TAG = "SortOfCultureFragment"; //用于日志输出中识别自己
	private final static String WEBSITE_HEAD = "http://www.51voa.com"; //网址的首部

	//标志型变量

	//视图组件
	private ListView view;
	private MyInformationAdapter listFragmentadapter;

	//数据型变量
	private List<Information> mInformations; //装载列表条目

	private int[] colorIds3 = new int[]{
			0xFFFF0000, 0xFF00FF00, 0xFF0000FF,
			0xFFAA0033, 0xFF3300AA, 0xFF30AA03,
			0xFF9900EE, 0xFFEE0099, 0xFF00FFFF,
			0xFFBB5522, 0xFF1122FF, 0xFFFFFF00,
			0xFFDD4411, 0xFF11DD44
	};
	//逻辑对象
	public DBopenHelper dbOpenHelper;
	private Context appContext;

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
		appContext = getActivity();
		dbOpenHelper = new DBopenHelper(appContext);
	}
	
	private void initInformationList(){
		Resources res = getResources();
		String[] eTitle = res.getStringArray(R.array.culture_array_english);
		String[] cTitle = res.getStringArray(R.array.culture_array_chinese);
		String[] website = res.getStringArray(R.array.culture_website);
		mInformations = new ArrayList<Information>();
		
		for(int i=0;i < eTitle.length;i++){
			Information information = new Information();
			information.setCtitle(cTitle[i]);
			information.setEtitle(eTitle[i]);
			information.setWebsite(WEBSITE_HEAD + website[i]);
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

			Resources res = getResources();

			DrawView drawView = (DrawView)convertView.findViewById(R.id.drawView);
			drawView.setColor(colorIds3[position % 14]);

			return convertView;
		}
		
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		Information info = mInformations.get(position);
		//取某列表的设置
		PreferencesManager sharedPrefer = new PreferencesManager(getActivity(),info.getEtitle());
		Map<String,String> params = sharedPrefer.getPreferences();
		info.isBuildTable = Boolean.getBoolean(params.get(info.getEtitle()));//检测是否建立数据库表
		String website = info.getWebsite();

		if(params.get(info.getEtitle()) == "false")
		{
			dbOpenHelper.onMyCreate(dbOpenHelper.getWritableDatabase(), website.substring(website.lastIndexOf("/")+1,website.lastIndexOf(".")));
			info.isBuildTable = true;
			sharedPrefer.save(info.isBuildTable);
		}
		HashMap<String, Object> passValue = new HashMap<String, Object>();
		passValue.put("websiteT", info.getWebsite());
		passValue.put("titleName", info.getCtitle());
		Intent intent = new Intent(getActivity(),ListOfArticleSimpleActivity.class);
		intent.putExtra(MainActivity.INNER_WEBSITE, passValue);  //传入外层条目website
		startActivity(intent);
	}
}
