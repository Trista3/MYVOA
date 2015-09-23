package com.tryhard.myvoa.ui.fragment;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.tryhard.myvoa.R;
import com.tryhard.myvoa.bean.InformationItem;
import com.tryhard.myvoa.db.InformationItemManager;
import com.tryhard.myvoa.ui.activity.TextContentActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class InnerListFragment extends Fragment {
	//����
	private static final String TAG = "myvoa->CultureInnerListFragment";
	public static final String INNER_WEBSITE = "website";
	public static final String WEBSITE_HEAD = "http://www.51voa.com";
	
	//����ͱ���
	private ArrayList<InformationItem> mInformationItems = null;
	private String innerWebsite; // ��ַ
	private String mTableName;

	//��ͼ���
	private PullToRefreshListView mPullToRefreshListView;
	
	//�߼�����
	private MyInformationAdapter adapter;
	private Context mContext;
	private InformationItemManager mItemDBmanager1,recordFragmentDBmanager;//��ݿ�Ĺ���


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		innerWebsite = (String) getActivity().getIntent().getSerializableExtra(INNER_WEBSITE);
		mTableName = innerWebsite.substring(innerWebsite.lastIndexOf("/") + 1, innerWebsite.lastIndexOf("."));
		
		mItemDBmanager1 = new InformationItemManager(getActivity(), mTableName);
		recordFragmentDBmanager = RecordFragment.recordFragmentDBmanager;
		
		mInformationItems = mItemDBmanager1.findAll();
		adapter = new MyInformationAdapter();
		
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.study_list_inner, container, false);
		
		//�����б��ʼ��
		mPullToRefreshListView = (PullToRefreshListView) v.findViewById(R.id.pull_refresh_list);
		ListView actualListView = mPullToRefreshListView.getRefreshableView();
		if(mInformationItems.isEmpty()){
			new GetDataTask().execute();
		}
		actualListView.setAdapter(adapter);
		//��������
		mPullToRefreshListView.setOnRefreshListener(new OnRefreshListener<ListView>() {
			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				String label = DateUtils.formatDateTime(getActivity().getApplicationContext(),
						System.currentTimeMillis(), DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE
								| DateUtils.FORMAT_ABBREV_ALL);

				//��������ˢ��ʱ��
				refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);

				//�����첽����������ַ��������ҳ����
				new GetDataTask().execute();
			}
		});
		//�����б���Ŀ��������¼������������TextContentActivity����ȡ��������������
		mPullToRefreshListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				InformationItem item = mInformationItems.get(position - 1);
				
				
				
				Intent intent = new Intent(getActivity(), TextContentActivity.class);
       			new GetDataTask2(item).execute();
				intent.putExtra(TextContentActivity.CONTENT_WEBSITE, item.getWebsite());
				startActivity(intent);
			}
		});
		return v;
	}

	private class GetDataTask extends AsyncTask<Void, Void, String> {

		@Override
		protected String doInBackground(Void... params) {
			getContent();
			return "OK";
		}

		@Override
		protected void onPostExecute(String result) {
			mInformationItems = mItemDBmanager1.findAll();
			adapter.notifyDataSetChanged();
			mPullToRefreshListView.onRefreshComplete();
			super.onPostExecute(result);
		}
	}
	
//������ҳ����
	private boolean getContent() {
		try {
			//������ҳ
			Document doc = Jsoup.connect(innerWebsite).get();
			Element content = doc.getElementById("list");
			Elements list = content.getElementsByTag("li");
			//��ʼ����
			for (org.jsoup.nodes.Element j : list) {
				InformationItem infoItem = new InformationItem();

				Elements i = j.getElementsByTag("a");
				String text = j.text();
				infoItem.setDate(text.substring(text.lastIndexOf("(") + 1, text.lastIndexOf(")")));
				infoItem.setTitle(text.substring(0, text.lastIndexOf("(")));
				infoItem.setWebsite(i.attr("href"));
				String website = infoItem.getWebsite();

				String key = website.substring(website.length() - 10, website.length() - 5);
				infoItem.setId(Integer.parseInt(key));
				mItemDBmanager1.save(infoItem);
			}
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			Log.e(TAG, "doc error");
			return false;
		}
	}

	//������ĸ���Ŀ��ҳ���Ƿ���ͼƬ�����򻺴����ݿ���
	private class GetDataTask2 extends AsyncTask<String, Void, Bitmap> {

		private String website;
		Bitmap bitmap = null;
		InformationItem infoItem;
		
		GetDataTask2(InformationItem infoItem) {
			this.website = WEBSITE_HEAD + infoItem.getWebsite();
			this.infoItem = infoItem;
		}

		@Override
		protected Bitmap doInBackground(String... params) {
			bitmap = getImage(website);
			return bitmap;
		}

		@Override
		protected void onPostExecute(Bitmap result) {
		
			if(bitmap!=null){
				infoItem.setBitmap(bitmap);
				mItemDBmanager1.update(infoItem);
			}
			recordFragmentDBmanager.save(infoItem);//�������¼�ӵ������ߡ�����
			adapter.notifyDataSetChanged();
			super.onPostExecute(bitmap);
		}
	}

	private Bitmap getImage(String website) {
		Bitmap bitmap = null;
		try {
			Document doc = Jsoup.connect(website).get();
			Element content = doc.getElementById("content");
			Elements images = content.getElementsByTag("img");

			if (images.isEmpty())
				return bitmap;
			
			String photoWebsite = images.get(0).attr("src");
			
			String temp = WEBSITE_HEAD + photoWebsite;
			URL url = new URL(WEBSITE_HEAD + photoWebsite);
			InputStream is = url.openStream();
			bitmap = BitmapFactory.decodeStream(is);
			is.close();
			
			
		} catch (IOException e) {
			e.printStackTrace();
			Log.e(TAG, "doc error");
			
		}finally{
			return bitmap;
		}
	}

	private class MyInformationAdapter extends ArrayAdapter<InformationItem> {

		public MyInformationAdapter() {
			super(getActivity(), 0);
		}

		@Override
		public int getCount() {
			Log.i(TAG, "The item num is: " + mInformationItems.size());
			return mInformationItems.size();
		}

		@SuppressLint("InflateParams")
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			
			if (convertView == null) {
				convertView = getActivity().getLayoutInflater().inflate(R.layout.culture_inner_item, null);
			}

			InformationItem infoItem = mInformationItems.get(position);

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



