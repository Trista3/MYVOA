package com.tryhard.myvoa;


import java.util.ArrayList;

import com.tryhard.myvoa.datatype.InformationItem;
import com.tryhard.myvoa.db.DBopenHelper;
import com.tryhard.myvoa.db.InformationItemManager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class RecordFragment extends ListFragment {
	//数据型变量
	public DBopenHelper dbOpenHelper;
	ArrayList<InformationItem> mInformations;
	public static MyInformationAdapter adapter;
	
	//常量
	public static InformationItemManager mItemDBmanager,recordFragmentDBmanager;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.record_list, container, false);
		 ListView view = (ListView)v.findViewById(android.R.id.list);
	      view.setEmptyView(v.findViewById(android.R.id.empty));
	      
	      dbOpenHelper = new DBopenHelper(getActivity());
	      dbOpenHelper.getWritableDatabase().execSQL("create table if not exists recordTable(_id integer primary key,title varchar(500),date varchar(20),website varchar(500),bitmap varbinary)");
	    
	      mInformations = recordFragmentDBmanager.findAll();
	      adapter = new MyInformationAdapter();
		  setListAdapter(adapter);
	      
	   return v;
	}

	@Override
	public void onResume() {
		super.onResume();
		mInformations = recordFragmentDBmanager.findAll();
		adapter.notifyDataSetChanged();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		InformationItem item = mInformations.get(position);
		
		Intent intent = new Intent(getActivity(), TextContentActivity.class);
		intent.putExtra(TextContentActivity.CONTENT_WEBSITE, item.getWebsite());
		startActivity(intent);
	}

	
	class MyInformationAdapter extends ArrayAdapter<InformationItem> {

		public MyInformationAdapter() {
			super(getActivity(), 0);
		}

		@Override
		public int getCount() {
			return mInformations.size();
		}

		@SuppressLint("InflateParams")
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			
			if (convertView == null) {
				convertView = getActivity().getLayoutInflater().inflate(R.layout.culture_inner_item, null);
			}

			InformationItem infoItem = mInformations.get(position);
			
			ImageView imageView = (ImageView) convertView.findViewById(R.id.photo);
			 
			if(null == infoItem.getBitmap()){
				imageView.setImageBitmap(null);
			}else{
				imageView.setImageBitmap(infoItem.getBitmap());
			}
			TextView title = (TextView) convertView.findViewById(R.id.culture_titleView);
			title.setText(infoItem.getTitle());
			TextView date = (TextView) convertView.findViewById(R.id.culture_dateView);
			date.setText(infoItem.getDate());
			Bitmap bitmap = null;
	
			return convertView;
		}
	}
}
