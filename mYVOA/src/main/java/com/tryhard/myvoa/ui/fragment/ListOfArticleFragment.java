package com.tryhard.myvoa.ui.fragment;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.tryhard.myvoa.R;
import com.tryhard.myvoa.bean.Information;
import com.tryhard.myvoa.bean.InformationItem;
import com.tryhard.myvoa.db.InformationItemManager;
import com.tryhard.myvoa.ui.activity.ListOfArticleSimpleActivity;
import com.tryhard.myvoa.widget.DividerItemDecoration;
import com.tryhard.myvoa.widget.ListOfArticleFragAdapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.NavUtils;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


public class ListOfArticleFragment extends Fragment {
	//常量
	private static final String TAG = "ListOfArticleFragment";
	public static final String INNER_WEBSITE = "website";
	public static final String WEBSITE_HEAD = "http://www.51voa.com";
	private static final int getMoreWebsite = 2;
	private static final int initList = 1;
	private static final int getMore = 3;

	//数据型变量
	private ArrayList<InformationItem> mInformationItems = null;
	private List<InformationItem> mInformationItems2 = null;
	private String innerWebsite; // 网址
	private String mTableName;
	private Information information;
	private int lastVisibleItem;
	private List<String> websites;

	//视图组件
	private SwipeRefreshLayout mSwipeRefreshWidget;
	private RecyclerView mRecyclerView;
	private LinearLayoutManager mLayoutManager;

	//逻辑对象
	private ListOfArticleFragAdapter adapter;
	private Context mContext;
	private InformationItemManager mItemDBmanager1, recordFragmentDBmanager;//数据库的管理
	private Boolean isFirst = true;
	private Handler firstHandler;
	private int scanWebsiteNum = 0;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initLgObj();
		mInformationItems = mItemDBmanager1.findAll();

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.article_list_fragment, container, false);

		//设置标题栏题目
		getActivity().getActionBar().setTitle(information.getCtitle());

		//启动返回上一级菜单
		if (NavUtils.getParentActivityName(getActivity()) != null) {
			getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
		}

		//首次启动页面，自动加载数据
		if(mInformationItems.isEmpty())
		     new GetDataTask(innerWebsite, initList).execute();

		//获取同一类型的网页的所有也输得网址
		new GetDataTask(innerWebsite, getMoreWebsite).execute();

		//初始化fragment的视图
		initView(v);
		return v;
	}


	private void initView(View v) {
		mSwipeRefreshWidget = (SwipeRefreshLayout) v.findViewById(R.id.swipe_refresh_widget);
		mRecyclerView = (RecyclerView) v.findViewById(android.R.id.list);

		//为RecyclerView设置Adapter
		mRecyclerView.setItemAnimator(new DefaultItemAnimator());
		adapter = new ListOfArticleFragAdapter(mInformationItems);
		//为RecyclerView中的项设置点击监听事件
		adapter.setOnItemClickListener(new ListOfArticleFragAdapter.OnItemClickLitener() {
			@Override
			public void onItemClick(View view, int position) {
				//点击过的条目字体变色
				InformationItem item = mInformationItems.get(position);
				TextView title = (TextView) view.findViewById(R.id.culture_titleView);
				TextView date = (TextView) view.findViewById(R.id.culture_dateView);
				title.setTextColor(getResources().getColor(R.color.c001));
				date.setTextColor(getResources().getColor(R.color.c001));

				recordFragmentDBmanager.save(mInformationItems.get(position));//将浏览记录加到“离线”里面

				startActivity(ListOfArticleSimpleActivity.makeIntent(mContext, item));
			}
		});
		mRecyclerView.setAdapter(adapter);

		//给SwipeRefreshLayout设置进度条颜色和监听器
		mSwipeRefreshWidget.setColorSchemeResources(R.color.c5, R.color.c2,
				R.color.c3, R.color.c4);
		mSwipeRefreshWidget.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
			@Override
			public void onRefresh() {
				new GetDataTask(innerWebsite,initList).execute();
			}
		});

		//设置滑到页面底部时上拉监听事件
		mRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
			@Override
			public void onScrollStateChanged(int newState) {
				//		super.onScrollStateChanged(newState);
				if (newState == RecyclerView.SCROLL_STATE_IDLE
						&& lastVisibleItem + 1 == adapter.getItemCount()) {
					mSwipeRefreshWidget.setRefreshing(true);
					if(scanWebsiteNum < websites.size()) {
						new GetDataTask(WEBSITE_HEAD + "/" + websites.get(++scanWebsiteNum), getMore).execute();
					}else{
						mSwipeRefreshWidget.setRefreshing(false);
					}
				}
			}
			@Override
			public void onScrolled(int dx, int dy) {
				//		super.onScrolled(dx, dy);
				lastVisibleItem = mLayoutManager.findLastVisibleItemPosition();
			}
		});

		mRecyclerView.setHasFixedSize(true);
		mLayoutManager = new LinearLayoutManager(mContext);
		mRecyclerView.setLayoutManager(mLayoutManager);
		if(!mInformationItems.isEmpty()) {
			mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(),
					DividerItemDecoration.VERTICAL_LIST));
		}
	}

	private void initLgObj() {
		mContext = getActivity();
		information = (Information) getActivity().getIntent().getSerializableExtra(ListOfArticleSimpleActivity.extra_data);
		innerWebsite = information.getWebsite();
		mTableName = innerWebsite.substring(innerWebsite.lastIndexOf("/") + 1, innerWebsite.lastIndexOf("."));
		mItemDBmanager1 = new InformationItemManager(mContext, mTableName);
		recordFragmentDBmanager = BrowsingHistoryFragment.recordFragmentDBmanager;
		websites = new ArrayList<String>();
		mInformationItems2 = new ArrayList<InformationItem>();
	}

	//获取网页数据的异步任务
	private class GetDataTask extends AsyncTask<Void, Void, String> {
		String website;
		int type;
		public GetDataTask(String website, int getContentType){
			this.website = website;
			type = getContentType;
		}
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();

		}

		@Override
		protected String doInBackground(Void... params) {
			getContent(website,type);
			return "OK";
		}

		@Override
		protected void onPostExecute(String result) {
			if(type == getMore){
				mInformationItems.addAll(mInformationItems2);
			}else{
				mInformationItems = mItemDBmanager1.findAll();
			}
			mSwipeRefreshWidget.setRefreshing(false);
			adapter.updateInfoItemList(mInformationItems);
			adapter.notifyDataSetChanged();
			mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(),
					DividerItemDecoration.VERTICAL_LIST));
			super.onPostExecute(result);
		}
	}

	//解析网页内容
	private boolean getContent(String websiteOut, int getContentType) {
		try {
			//连接网页
			Document doc = Jsoup.connect(websiteOut).get();

			switch(getContentType){
				case getMore:
				case initList: {
					Element content = doc.getElementById("list");
					Elements contentlist = content.getElementsByTag("li");

					//开始解析
					for (org.jsoup.nodes.Element j : contentlist) {
						InformationItem infoItem = new InformationItem();

						Elements i = j.getElementsByTag("a");
						String text = j.text();
						infoItem.setDate(text.substring(text.lastIndexOf("(") + 1, text.lastIndexOf(")")));
						infoItem.setTitle(text.substring(0, text.lastIndexOf("(")));
						infoItem.setWebsite(i.attr("href"));
						String website = infoItem.getWebsite();

						String key = website.substring(website.length() - 10, website.length() - 5);
						infoItem.setId(Integer.parseInt(key));
						if(getContentType == getMore){
							mInformationItems2.add(infoItem);
						}else {
							mItemDBmanager1.save(infoItem);
						}
					}
					break;
				}
				case getMoreWebsite: {
					Element contentWeb = doc.getElementById("pagelist");
					Elements websitesList = contentWeb.getElementsByTag("a");
					for (org.jsoup.nodes.Element j : websitesList) {
						websites.add(j.attr("href"));
					}
					break;
				}
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
		int position;

		GetDataTask2(int position) {
			this.website = WEBSITE_HEAD + mInformationItems.get(position).getWebsite();
			this.position = position;
		}

		@Override
		protected Bitmap doInBackground(String... params) {
			bitmap = getImage(website);
			return bitmap;
		}

		@Override
		protected void onPostExecute(Bitmap result) {
			mInformationItems.get(position).setIsScaned(true);
			if (bitmap != null) {
				mInformationItems.get(position).setBitmap(bitmap);
			}
			mItemDBmanager1.update(mInformationItems.get(position));
			recordFragmentDBmanager.save(mInformationItems.get(position));//将浏览记录加到“离线”里面
			adapter.updateInfoItemList(mInformationItems);
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

		} finally {
			return bitmap;
		}
	}

}




