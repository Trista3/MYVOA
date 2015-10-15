package com.tryhard.myvoa.ui.fragment;


import java.util.ArrayList;
import java.util.HashMap;

import com.tryhard.myvoa.R;
import com.tryhard.myvoa.bean.InformationItem;
import com.tryhard.myvoa.db.DBopenHelper;
import com.tryhard.myvoa.db.InformationItemManager;
import com.tryhard.myvoa.ui.activity.ArticleContentActivity;
import com.tryhard.myvoa.ui.activity.ListOfArticleSimpleActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.content.Context;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;

public class BrowsingHistoryFragment extends ListFragment {
	//数据型变量
	ArrayList<InformationItem> mInformations;

	//逻辑对象
	public DBopenHelper dbOpenHelper;
	public static MyInformationAdapter adapter;
	private Context mContext;
	private PopupMenu popupMenu; // 菜单对象


	//视图组件
	private ImageView mMoreMenu; //菜单图标

	//常量
	public static InformationItemManager mItemDBmanager,recordFragmentDBmanager;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.browsing_history_fragment, container, false);
		initLgObj();
		initView(v);

		setListAdapter(adapter);
		return v;
	}

	private void initView(View v){
		ListView view = (ListView)v.findViewById(android.R.id.list);
		view.setEmptyView(v.findViewById(android.R.id.empty));

	}

	private void initLgObj(){
		mContext = getActivity();
		dbOpenHelper = new DBopenHelper(mContext);
		mInformations = recordFragmentDBmanager.findAll();
		adapter = new MyInformationAdapter();
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

		startActivity(ListOfArticleSimpleActivity.makeIntent(mContext, item));
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
				convertView = getActivity().getLayoutInflater().inflate(R.layout.browsing_history_item, null);
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

			return convertView;
		}
	}
}
