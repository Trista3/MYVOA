package com.tryhard.myvoa.ui.fragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.tryhard.myvoa.util.PreferencesManager;
import com.tryhard.myvoa.R;
import com.tryhard.myvoa.bean.Information;
import com.tryhard.myvoa.db.DBopenHelper;
import com.tryhard.myvoa.ui.activity.CreateInnerFragActivity;
import com.tryhard.myvoa.ui.activity.MainActivity;

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
	//����
	private static final String TAG = "CultureFragment"; //������־�����ʶ���Լ�
	private final static String WEBSITE_HEAD = "http://www.51voa.com"; //��ַ���ײ�
	
	//��־�ͱ���
	
	//��ͼ���
	
	//����ͱ���
	private List<Information> mInformations; //װ���б���Ŀ
	
	//�߼�����
	public DBopenHelper dbOpenHelper;
	private Context appContext;
	private MyInformationAdapter listFragmentadapter;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		initInformationList();//��ʼ���б�
		
	    listFragmentadapter = new MyInformationAdapter((ArrayList<Information>)mInformations);
		setListAdapter(listFragmentadapter);
	}
	
	private void initInformationList(){
		String[] eTitle = new String[]{"Technology Report","This is America","Agriculture Report","Science in the News","Health Report",
				"Explorations","Education Report","The Making of a Nation","Economics Report","American Mosaic","In the News","American Stories",
				"Words And Their Stories","People in America","AS IT IS"
		};
		
		String[] cTitle = new String[]{"�Ƽ�����","��������","ũҵ����","��ѧ����","��������","��Ȼ̽��","�����","����ʷ��","���ñ���","������Ͳ","ʱ������",
				"�������","�ʻ��ƹ�","��������־","����������־"
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
		//ȡĳ�б������
		PreferencesManager sharedPrefer = new PreferencesManager(getActivity(),info.getEtitle());
		Map<String,String> params = sharedPrefer.getPreferences();
		info.isBuildTable = Boolean.getBoolean(params.get(info.getEtitle()));//����Ƿ�����ݿ��
		String website = info.getWebsite();
		
		dbOpenHelper = new DBopenHelper(getActivity());
		if(params.get(info.getEtitle()) == "false")
		{
			dbOpenHelper.onMyCreate(dbOpenHelper.getWritableDatabase(), website.substring(website.lastIndexOf("/")+1,website.lastIndexOf(".")));
			info.isBuildTable = true;
			sharedPrefer.save(info.isBuildTable);
		}	
		Intent intent = new Intent(getActivity(),CreateInnerFragActivity.class);
		intent.putExtra(MainActivity.INNER_WEBSITE, info.getWebsite());  //���������Ŀwebsite
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
