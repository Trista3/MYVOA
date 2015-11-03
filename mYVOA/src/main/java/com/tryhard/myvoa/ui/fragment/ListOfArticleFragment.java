package com.tryhard.myvoa.ui.fragment;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import com.tryhard.myvoa.R;
import com.tryhard.myvoa.bean.BrowsingItem;
import com.tryhard.myvoa.bean.Information;
import com.tryhard.myvoa.bean.InformationItem;
import com.tryhard.myvoa.db.InformationItemDao;
import com.tryhard.myvoa.ui.activity.ListOfArticleSimpleActivity;
import com.tryhard.myvoa.util.ParseHtmlString;
import com.tryhard.myvoa.widget.DividerItemDecoration;
import com.tryhard.myvoa.widget.ListOfArticleFragAdapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.NavUtils;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;



public class ListOfArticleFragment extends Fragment {
	//常量
	private static final String TAG = "ListOfArticleFragment";
	public static final String INNER_WEBSITE = "website";
	public static final String WEBSITE_HEAD = "http://www.51voa.com";
	public static final int getMoreWebsite = 2;
	public static final int initList = 1;
	public static final int getMore = 3;
	public static final int GET_IMAGE = 4;

	//数据型变量
	private ArrayList<InformationItem> mInformationItems = null;
	private List<InformationItem> mInformationItems2 = null;
	private String innerWebsite; // 网址
	private String mTableName;
	private Information information;
	private int lastVisibleItem;
	private List<String> websites;
	private String mSortOfInformation;
	private Bitmap clickItemBitmap = null;
	private int clickPosition = -1;

	//视图组件
	private SwipeRefreshLayout mSwipeRefreshWidget;
	private RecyclerView mRecyclerView;
	private LinearLayoutManager mLayoutManager;

	//逻辑对象
	private ListOfArticleFragAdapter adapter;
	private Context mContext;
	private InformationItemDao mInformationItemDao;//数据库的管理
	private Boolean isFirst = true;
	private Handler firstHandler;
	private int scanWebsiteNum = 0;
	private ParseHtmlString parseHtmlString;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initLgObj();
		mInformationItems = mInformationItemDao.getAllItemsByInfoSort(mSortOfInformation);

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
		if(mInformationItems.isEmpty()){
			adapter = new ListOfArticleFragAdapter(mInformationItems);
			getWhat(innerWebsite, initList);
		}

		//获取同一类型的网页的所有也输得网址
		getWhat(innerWebsite,getMoreWebsite);

		//初始化fragment的视图
		initView(v);
		return v;
	}


	private void initView(View v) {
		mSwipeRefreshWidget = (SwipeRefreshLayout) v.findViewById(R.id.swipe_refresh_widget);
		mRecyclerView = (RecyclerView) v.findViewById(android.R.id.list);

		//为RecyclerView设置Adapter
		mRecyclerView.setItemAnimator(new DefaultItemAnimator());

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

				startActivity(ListOfArticleSimpleActivity.makeIntent(mContext, item));

				clickPosition = position;
				getWhat(item.getWebsite(),GET_IMAGE);
			}
		});
		mRecyclerView.setAdapter(adapter);

		//给SwipeRefreshLayout设置进度条颜色和监听器
		mSwipeRefreshWidget.setColorSchemeResources(R.color.c5, R.color.c2,
				R.color.c3, R.color.c4);
		mSwipeRefreshWidget.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
			@Override
			public void onRefresh() {
				getWhat(innerWebsite,initList);
			}
		});

		//设置滑到页面底部时上拉监听事件
		mRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
			@Override
			public void onScrollStateChanged(int newState) {
				if (newState == RecyclerView.SCROLL_STATE_IDLE
						&& lastVisibleItem + 1 == adapter.getItemCount()) {
					mSwipeRefreshWidget.setRefreshing(true);
					if(scanWebsiteNum < websites.size()) {
						getWhat(websites.get(++scanWebsiteNum),getMore);
					}else{
						mSwipeRefreshWidget.setRefreshing(false);
					}
				}
			}
			@Override
			public void onScrolled(int dx, int dy) {
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
		mSortOfInformation = information.getEtitle();

		mTableName = innerWebsite.substring(innerWebsite.lastIndexOf("/") + 1, innerWebsite.lastIndexOf("."));
		mInformationItemDao = new InformationItemDao(getActivity());
		websites = new ArrayList<String>();
		mInformationItems2 = new ArrayList<InformationItem>();
		parseHtmlString = new ParseHtmlString(mSortOfInformation);
	}

	private void getWhat(String website, int whatType){
		if(whatType == initList){
			parseHtmlString.getNewInformationItems(website);
			mInformationItems = parseHtmlString.informationItems;
			for(InformationItem item : parseHtmlString.informationItems){
				if(item.getWebsite() == mInformationItems.get(0).getWebsite())
					break;
				mInformationItemDao.add(item);
			}
		}else if(whatType == getMore){
			parseHtmlString.getNewInformationItems(website);
			mInformationItems.addAll(parseHtmlString.informationItems);
		}else if(whatType == getMoreWebsite){
			websites = parseHtmlString.getMoreWebsite(website);
		}else if(whatType == GET_IMAGE){
			clickItemBitmap = parseHtmlString.getImage(website);
			InformationItem item = mInformationItems.get(clickPosition);
			item.setIsScaned(true);
			if (clickItemBitmap != null) {
				ByteArrayOutputStream os = new ByteArrayOutputStream();
				clickItemBitmap.compress(Bitmap.CompressFormat.PNG, 100, os);
				item.setBitmapOs(os.toByteArray());
			}else{
				item.setBitmapOs(null);
			}
			mInformationItemDao.updateItem(item);

			//将浏览记录加到“离线”里面
			BrowsingItem browsingItem = new BrowsingItem(item.getTitle(),item.getDate(),item.getWebsite(),item.getBitmapOs());
			BrowsingHistoryFragment.mBrowsingItemDao.add(browsingItem);
		}

		adapter.notifyDataSetChanged();
	}

}




