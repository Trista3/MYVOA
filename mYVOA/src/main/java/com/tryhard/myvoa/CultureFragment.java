package com.tryhard.myvoa;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.tryhard.myvoa.datatype.Information;
import com.tryhard.myvoa.db.DBopenHelper;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class CultureFragment extends ListFragment {
	//常量
	private static final String TAG = "CultureFragment"; //用于日志输出中识别自己
	private final static String WEBSITE_HEAD = "http://www.51voa.com"; //网址的首部
	
	//标志型变量
	
	//视图组件
	
	//数据型变量
	private List<Information> mInformations; //装载列表条目
	
	//逻辑对象
	public DBopenHelper dbOpenHelper;
	private Context appContext;
	private MyInformationAdapter listFragmentadapter;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		initInformationList();//初始化列表
		
	    listFragmentadapter = new MyInformationAdapter((ArrayList<Information>)mInformations);
		setListAdapter(listFragmentadapter);
	}
	
	private void initInformationList(){
		String[] eTitle = new String[]{"Technology Report","This is America","Agriculture Report","Science in the News","Health Report",
				"Explorations","Education Report","The Making of a Nation","Economics Report","American Mosaic","In the News","American Stories",
				"Words And Their Stories","People in America","AS IT IS"
		};
		
		String[] cTitle = new String[]{"科技报道","今日美国","农业报道","科学报道","健康报道","自然探索","教育报道","建国史话","经济报道","美国万花筒","时事新闻",
				"美国故事","词汇掌故","美国人物志","慢速新闻杂志"
		};
		String[] website = new String[]{"/Technology_Report_1.html","/This_is_America_1.html","/Agriculture_Report_1.html","/Science_in_the_News_1.html",
				"/Health_Report_1.html","/Explorations_1.html","/Education_Report_1.html","/The_Making_of_a_Nation_1.html","/Economics_Report_1.html",
				"/American_Mosaic_1.html","/In_the_News_1.html","/American_Stories_1.html","/Words_And_Their_Stories_1.html","/People_in_America_1.html","/as_it_is_1.html"
		};
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

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if(convertView == null){
				convertView = getActivity().getLayoutInflater().inflate(R.layout.study_item,null);
			}
			Information info = getItem(position);
			
			TextView Etitle = (TextView)convertView.findViewById(R.id.Etitle);
			Etitle.setText(info.getEtitle());
			TextView Ctitle = (TextView)convertView.findViewById(R.id.Ctitle);
			Ctitle.setText(info.getCtitle());
			
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
		
		dbOpenHelper = new DBopenHelper(getActivity());
		if(params.get(info.getEtitle()) == "false")
		{
			dbOpenHelper.onMyCreate(dbOpenHelper.getWritableDatabase(), website.substring(website.lastIndexOf("/")+1,website.lastIndexOf(".")));
			info.isBuildTable = true;
			sharedPrefer.save(info.isBuildTable);
		}	
		Intent intent = new Intent(getActivity(),CreateInnerFragActivity.class);
		intent.putExtra(MainActivity.INNER_WEBSITE, info.getWebsite());  //传入外层条目website
		startActivity(intent);
		
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.study_list,container,false);
		ListView view = (ListView)v.findViewById(android.R.id.list);
		return v;
	}
}
