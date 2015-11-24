package com.tryhard.myvoa.ui.activity;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import android.annotation.TargetApi;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.NavUtils;

import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.tryhard.myvoa.bean.InformationItem;
import com.tryhard.myvoa.ui.activity.base.BaseActivity;
import com.tryhard.myvoa.ui.fragment.ListOfArticleFragment;
import com.tryhard.myvoa.R;
import com.tryhard.myvoa.service.ArticleVideoPlayService;
import com.tryhard.myvoa.util.MvoaRetrofit;
import com.tryhard.myvoa.util.NetworkUtil;
import com.tryhard.myvoa.util.StreamTool;

import retrofit.client.Response;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class ArticleContentActivity extends BaseActivity {
    // 常量
    public static String content_extra_data="ArticleContentActivity.extra_data";
    public final static String NO_MP3 = "nomp3";

    // 数据型变量
    private String mTextWebsite;
    private String mp3UriString = NO_MP3;
    private InformationItem informationItem;
    String articalText;

    // 视图组件
    private LinearLayout mLinearLayout;
    private WebView articalWV; // 显示文章内容的网页
    public static SeekBar audioSeekBar; // 播放进度条
    private TextView playAudioBtn; // 播放按钮

    // 逻辑对象
    boolean isPlay = false;
    ArticleVideoPlayService.MyBinder binder;
    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            binder = (ArticleVideoPlayService.MyBinder)service;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    //***********************************************
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.article_content_activity);
        if(NavUtils.getParentActivityName(this) != null) {
           getActionBar().setDisplayHomeAsUpEnabled(true);
        }

        initTitleView();
        initViews();
        if(NetworkUtil.getInstance(mContext).checkNetworkState())
            uriDataRequest();
    }

    //初始化标题栏
    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void initTitleView(){
        informationItem = (InformationItem)getIntent().getSerializableExtra(ArticleContentActivity.content_extra_data);
        mTextWebsite = informationItem.getWebsite();
        getActionBar().setTitle(informationItem.getTitle());
    }

    //取得Mp3的播放网址
    private void uriDataRequest(){
        MvoaRetrofit.getHtmlObservable(mTextWebsite)
                .subscribeOn(Schedulers.io())
                .doOnNext(new Action1<Response>() {
                    @Override
                    public void call(Response response) {
                        try {
                            OutputStream os = StreamTool.getOutputStream(response.getBody().in());
                            Document doc = Jsoup.parse(os.toString());
                            articalText = doc.getElementById("content").toString();

                            if (doc.getElementById("mp3") != null)
                                mp3UriString = doc.getElementById("mp3").attr("href");

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Response>() {
                    @Override
                    public void call(Response response) {
                        articalWV.loadDataWithBaseURL(ListOfArticleFragment.WEBSITE_HEAD, articalText, "text/html", "utf-8", null);
                        if (!mp3UriString.equals(NO_MP3)) {
                            mLinearLayout.setVisibility(View.VISIBLE);
                            playAudioBtn.setEnabled(true);

                            HashMap<String,String> extra = new HashMap<>();
                            extra.put(ArticleVideoPlayService.EXTRA_STRING,mp3UriString);
                            bindService(makeIntent(mContext,ArticleVideoPlayService.class,extra), conn, Service.BIND_AUTO_CREATE);
                        } else {
                            mLinearLayout.setVisibility(View.GONE);
                        }
                    }
                });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 初始化视图组件
     */
    private void initViews() {
        articalWV = (WebView) findViewById(R.id.textShow);
        mLinearLayout = (LinearLayout) findViewById(R.id.play_linear);
        audioSeekBar = (SeekBar) findViewById(R.id.seekbar);
        playAudioBtn = (TextView) findViewById(R.id.play);

        playAudioBtn.setOnClickListener(clickListener);
    }


    @Override
    protected void onDestroy() {
        if(!mp3UriString.equals(NO_MP3))
            unbindService(conn);
        super.onDestroy();
    }

    /**
     * 单击监听器
     */
    private OnClickListener clickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.play: {
                    if (!isPlay) {
                        playAudioBtn.setBackgroundResource(R.drawable.bofang); // 更新按钮图标
                        binder.restartAudio();
                        isPlay = true;
                    } else {

                        playAudioBtn.setBackgroundResource(R.drawable.right); // 更新按钮图标
                        binder.pauseAudio();
                        isPlay = false;
                    }
                    break;
                }
            }
       }
    };

    public static Intent makeIntent(Context mFromContext,Class toStartClass,HashMap extra ){
        return new Intent(mFromContext,toStartClass).putExtra(ArticleVideoPlayService.SERVICE_EXTRA, extra);
    }
}