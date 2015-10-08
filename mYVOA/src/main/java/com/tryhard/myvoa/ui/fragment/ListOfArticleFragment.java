package com.tryhard.myvoa.ui.fragment;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

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
import com.tryhard.myvoa.ui.activity.ArticleContentActivity;
import com.tryhard.myvoa.ui.activity.MainActivity;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.NavUtils;
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

public class ListOfArticleFragment extends Fragment {
	//常量
	private static final String TAG = "ListOfArticleFragment";
	public static final String INNER_WEBSITE = "website";
	public static final String WEBSITE_HEAD = "http://www.51voa.com";

	//数据型变量
	private ArrayList<InformationItem> mInformationItems = null;
	private String innerWebsite; // 网址
	private String mTableName;

	//视图组件
	private PullToRefreshListView mPullToRefreshListView;
	private ListView actualListView;

	//逻辑对象
	private MyInformationAdapter adapter;
	private Context mContext;
	private InformationItemManager mItemDBmanager1,recordFragmentDBmanager;//数据库的管理
	private HashMap<String, Object> getPassedValue;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initTitleBar(); //初始化标题栏
		initLgObj();
		mInformationItems = mItemDBmanager1.findAll();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.article_list_fragment, container, false);
		initView(v);

		if(mInformationItems.isEmpty()){
			new GetDataTask().execute();
		}
		actualListView.setAdapter(adapter);
		viewSetListener();
		return v;
	}

	//初始化标题栏
	private void initTitleBar(){
		if(NavUtils.getParentActivityName(getActivity()) != null){
			ImageView imageView = (ImageView)getActivity().getWindow().findViewById(R.id.titleBackUpImage);
			imageView.setBackgroundResource(R.drawable.fanhui);
			View backup = getActivity().getWindow().findViewById(R.id.titlebarBackup);
			backup.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					NavUtils.navigateUpFromSameTask(getActivity());
				}
			});
		}

		getPassedValue = (HashMap<String, Object>) getActivity().getIntent().getSerializableExtra(MainActivity.INNER_WEBSITE);
		innerWebsite = (String) getPassedValue.get("websiteT");
		mTableName = innerWebsite.substring(innerWebsite.lastIndexOf("/") + 1, innerWebsite.lastIndexOf("."));

		TextView textView = (TextView)getActivity().getWindow().findViewById(R.id.titleTextView);
		textView.setText((String) getPassedValue.get("titleName"));

	}

	private void initView(View v){
		//下拉列表初始化
		mPullToRefreshListView = (PullToRefreshListView) v.findViewById(R.id.pull_refresh_list);
		actualListView = mPullToRefreshListView.getRefreshableView();
	}

	private void initLgObj(){
		mContext = getActivity();
		mItemDBmanager1 = new InformationItemManager(mContext, mTableName);
		recordFragmentDBmanager = BrowsingHistoryFragment.recordFragmentDBmanager;
		adapter = new MyInformationAdapter();
	}

	private void viewSetListener(){
		mPullToRefreshListView.setMode(PullToRefreshListView.Mode.BOTH);
		//下拉监听
		mPullToRefreshListView.setOnRefreshListener(new OnRefreshListener<ListView>() {
			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				String label = DateUtils.formatDateTime(mContext.getApplicationContext(),
						System.currentTimeMillis(), DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE
								| DateUtils.FORMAT_ABBREV_ALL);

				//更新最新刷新时间
				refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);

				//启动异步任务链接网址，解析网页内容
				new GetDataTask().execute();
			}
		});
		//设置列表条目点击监听事件：点击后，启动TextContentActivity，获取解析的新闻内容
		mPullToRefreshListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				InformationItem item = mInformationItems.get(position - 1);

				new GetDataTask2(item).execute();
				HashMap<String, Object> passValue = new HashMap<String, Object>();
				passValue.put("websiteT", item.getWebsite());
				passValue.put("titleName", item.getTitle());
				Intent intent = new Intent(mContext,ArticleContentActivity.class);
				intent.putExtra(ArticleContentActivity.CONTENT_WEBSITE, passValue);  //传入外层条目website
				startActivity(intent);
			}
		});
	}

	private class GetDataTask extends AsyncTask<Void, Void, String> {


		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();

		}

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

	//解析网页内容
	private boolean getContent() {
		try {
			//连接网页
			Document doc = Jsoup.connect(innerWebsite).get();
			Element content = doc.getElementById("list");
			Elements list = content.getElementsByTag("li");
			//开始解析
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

	//检测点击的该条目网页里是否有图片，有则缓存进数据库里
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
			infoItem.setIsScaned(true);
			if(bitmap!=null){
				infoItem.setBitmap(bitmap);
			}
			mItemDBmanager1.update(infoItem);
			recordFragmentDBmanager.save(infoItem);//将浏览记录加到“离线”里面
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
			super(mContext, 0);
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
				convertView = getActivity().getLayoutInflater().inflate(R.layout.article_list_item, null);
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
			if(infoItem.getIsScaned()){
				title.setTextColor(0xFF898787);
				date.setTextColor(0xFF898787);
			}

			return convertView;
		}
	}
}



