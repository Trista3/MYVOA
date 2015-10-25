package com.tryhard.myvoa.ui.fragment;


import java.util.List;

import com.tryhard.myvoa.R;
import com.tryhard.myvoa.bean.BrowsingItem;
import com.tryhard.myvoa.bean.InformationItem;
import com.tryhard.myvoa.db.BrowsingItemDao;
import com.tryhard.myvoa.ui.activity.ListOfArticleSimpleActivity;

import android.annotation.SuppressLint;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.content.Context;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class BrowsingHistoryFragment extends ListFragment {
	//数据型变量
	List<BrowsingItem> mBrowsingItems;

	//逻辑对象
	public static MyInformationAdapter adapter;
	private Context mContext;

	//常量
	public static BrowsingItemDao mBrowsingItemDao;

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
		mBrowsingItems = mBrowsingItemDao.getAllItems();
		adapter = new MyInformationAdapter();
	}

	@Override
	public void onResume() {
		super.onResume();
		mBrowsingItems = mBrowsingItemDao.getAllItems();
		adapter.notifyDataSetChanged();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		BrowsingItem bItem = mBrowsingItems.get(position);
		InformationItem item = new InformationItem();
		item.setTitle(bItem.getmTitle());
		item.setDate(bItem.getmDate());
		item.setWebsite(bItem.getmWebsite());
		item.setBitmapOs(bItem.getmBitmapOs());

		startActivity(ListOfArticleSimpleActivity.makeIntent(mContext, item));
	}


	class MyInformationAdapter extends ArrayAdapter<InformationItem> {

		public MyInformationAdapter() {
			super(getActivity(), 0);
		}

		@Override
		public int getCount() {
			if(mBrowsingItems == null)
				return 0;
			return mBrowsingItems.size();
		}

		@SuppressLint("InflateParams")
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			if (convertView == null) {
				convertView = getActivity().getLayoutInflater().inflate(R.layout.browsing_history_item, null);
			}

			BrowsingItem browseItem = mBrowsingItems.get(position);

			ImageView imageView = (ImageView) convertView.findViewById(R.id.photo);

			if(null == browseItem.getmBitmapOs()){
				imageView.setImageBitmap(null);
			}else{
				imageView.setImageBitmap(BitmapFactory.decodeByteArray(browseItem.getmBitmapOs(), 0, browseItem.getmBitmapOs().length, null));
			}
			TextView title = (TextView) convertView.findViewById(R.id.culture_titleView);
			title.setText(browseItem.getmTitle());
			TextView date = (TextView) convertView.findViewById(R.id.culture_dateView);
			date.setText(browseItem.getmDate());

			return convertView;
		}
	}
}
