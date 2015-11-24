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
import com.tryhard.myvoa.util.NetworkUtil;
import com.tryhard.myvoa.util.ParseHtmlString;
import com.tryhard.myvoa.widget.DividerItemDecoration;
import com.tryhard.myvoa.ui.adapter.ListOfArticleFragAdapter;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.NavUtils;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ListOfArticleFragment extends Fragment {
	//常量
	public static final String WEBSITE_HEAD = "http://www.51voa.com";
	public static final int getMoreWebsite = 2;
	public static final int initList = 1;
	public static final int getMore = 3;
	public static final int GET_IMAGE = 4;

	//数据型变量
	private static ArrayList<InformationItem> mInformationItems = null;
	static List<InformationItem> mInformationItems2 = null;
	private String innerWebsite; // 网址
	private Information information;
	private int lastVisibleItem;
	static List<String> websites;
	private static String mSortOfInformation;
	private static Bitmap clickItemBitmap = null;
	private static int clickPosition = -1;

	//视图组件
	private SwipeRefreshLayout mSwipeRefreshWidget;
	private RecyclerView mRecyclerView;
	private LinearLayoutManager mLayoutManager;

	//逻辑对象
	public static ListOfArticleFragAdapter adapter;
	private Context mContext;
	private static InformationItemDao mInformationItemDao;//数据库的管理
	private int scanWebsiteNum = 0;
	private ParseHtmlString parseHtmlString;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initLgObj();
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
			getWhat(innerWebsite, initList);
		}

		//获取同一类型的网页的所有页数的网址
		getWhat(innerWebsite, getMoreWebsite);

		//初始化fragment的视图
		initView(v);
		return v;
	}


	private void initView(View v) {
		mSwipeRefreshWidget = (SwipeRefreshLayout) v.findViewById(R.id.swipe_refresh_widget);
		mRecyclerView = (RecyclerView) v.findViewById(android.R.id.list);

		//为RecyclerView设置Adapter
		mRecyclerView.setItemAnimator(new DefaultItemAnimator());
		mRecyclerView.setAdapter(adapter);
		mRecyclerView.addItemDecoration(new DividerItemDecoration(mContext,DividerItemDecoration.VERTICAL_LIST));

		//为RecyclerView中的项设置点击监听事件
		adapter.setOnItemClickListener(new ListOfArticleFragAdapter.OnItemClickLitener() {
			@Override
			public void onItemClick(View view, int position) {
				//将该Article的是否浏览状态设置为true，并保存起来
				InformationItem item = mInformationItems.get(position);
				item.setIsScaned(true);
				mInformationItemDao.updateItem(item);
				startActivity(ListOfArticleSimpleActivity.makeIntent(mContext, item));

				//获取该Article网页的图片
				clickPosition = position;
				getWhat(item.getWebsite(),GET_IMAGE);
			}
		});


		//给SwipeRefreshLayout设置进度条颜色和监听器
		mSwipeRefreshWidget.setColorSchemeResources(R.color.c5, R.color.c2,
				R.color.c3, R.color.c4);
		mSwipeRefreshWidget.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
			//设置下拉监听
			@Override
			public void onRefresh() {
				getWhat(innerWebsite,initList);
				mSwipeRefreshWidget.setRefreshing(false);
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
					}
					mSwipeRefreshWidget.setRefreshing(false);
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
		//设置分割线
		if(!mInformationItems.isEmpty()) {
			mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(),
					DividerItemDecoration.VERTICAL_LIST));
		}
	}

	//初始化逻辑对象
	private void initLgObj() {
		mContext = getActivity();
		information = (Information) getActivity().getIntent().getSerializableExtra(ListOfArticleSimpleActivity.extra_data);
		innerWebsite = information.getWebsite();
		mSortOfInformation = information.getEtitle();

		mInformationItemDao = new InformationItemDao(getActivity());
		websites = new ArrayList<>();
		mInformationItems2 = new ArrayList<>();
		mInformationItems = mInformationItemDao.getAllItemsByInfoSort(mSortOfInformation);
		adapter = new ListOfArticleFragAdapter(mInformationItems,mContext);
		parseHtmlString = new ParseHtmlString(mSortOfInformation);
	}

	//获取对应要求类型的内容
	private void getWhat(String website, int whatType){
		if(NetworkUtil.getInstance(mContext).checkNetworkState()){
			if(whatType == initList || whatType == getMore){
				parseHtmlString.getNewInformationItems(website, whatType);
			}else if(whatType == getMoreWebsite){
				parseHtmlString.getMoreWebsite(website, whatType);
			}else if(whatType == GET_IMAGE){
				parseHtmlString.getImage(website,whatType);
			}
		}else{
			NetworkUtil.getInstance(mContext).checkNetworkState();
		}
	}

	//将异步返回的获取内容储存起来
	public static void saveInDao(List<String> websites,ArrayList<InformationItem> items,Bitmap bitmap,int getWhatType){
		if(getWhatType == initList) {
			for (InformationItem item : items) {
				if (!mInformationItems.isEmpty() && item.getWebsite() == mInformationItems.get(0).getWebsite())
					break;
				mInformationItemDao.add(item);
			}
			mInformationItems = mInformationItemDao.getAllItemsByInfoSort(mSortOfInformation);
			adapter.updateInfoItemList(mInformationItems);
			adapter.notifyDataSetChanged();
		}else if(getWhatType == getMoreWebsite){
			ListOfArticleFragment.websites = websites;
		}else if(getWhatType == getMore){
			mInformationItems.addAll(items);
			adapter.updateInfoItemList(mInformationItems);
			adapter.notifyDataSetChanged();
		}else if(getWhatType == GET_IMAGE){
			clickItemBitmap = bitmap;
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
			adapter.notifyDataSetChanged();

			//将浏览记录加到“离线”里面
			BrowsingItem browsingItem = new BrowsingItem(item.getTitle(),item.getDate(),item.getWebsite(),item.getBitmapOs());
			BrowsingHistoryFragment.mBrowsingItemDao.add(browsingItem);
		}
	}
}




