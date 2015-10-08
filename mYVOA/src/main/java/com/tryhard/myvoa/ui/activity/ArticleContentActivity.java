package com.tryhard.myvoa.ui.activity;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import android.annotation.TargetApi;
import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;


import android.support.v7.internal.view.menu.MenuPopupHelper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.tryhard.myvoa.ui.activity.base.BaseActivity;
import com.tryhard.myvoa.ui.fragment.ListOfArticleFragment;
import com.tryhard.myvoa.R;

public class ArticleContentActivity extends BaseActivity {
    // 常量
    private static final String TAG = "myvoa-->CultureTextContentActivity";
    public static final String CONTENT_WEBSITE = "contentWebsite";

    // 数据型变量
    private String mTextWebsite;
    private String mp3UriString = "noMP3";

    // 视图组件
    private LinearLayout mLinearLayout;
    private WebView articalWV; // 显示文章内容的网页
    private SeekBar playProgressSB; // 播放进度条
    private TextView playAudioBtn; // 播放按钮
    private ImageView mMoreMenu; //菜单图标

    // 逻辑对象
    private Context appContext;
    private MediaPlayer audioPlayer;
    private Timer refreshPlayProgressTimer;
    private  HashMap<String, Object> getPassedValue;
    private PopupMenu popupMenu; // 菜单对象

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.article_content_activity);

       initTitleView();

        new GetDataTask().execute();
        initViews();
        initLgObjs();
    }
    //初始化标题栏
    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void initTitleView(){
        getPassedValue = (HashMap<String, Object>)getIntent().getSerializableExtra(CONTENT_WEBSITE);
        mTextWebsite =  ListOfArticleFragment.WEBSITE_HEAD + (String) getPassedValue.get("websiteT");

        TextView textView = (TextView)getWindow().findViewById(R.id.titleTextView);
        textView.setText((String) getPassedValue.get("titleName"));
        // mWebView.getSettings().setJavaScriptEnabled(true);
        if(NavUtils.getParentActivityName(this) != null){
            ImageView imageView = (ImageView)getWindow().findViewById(R.id.titleBackUpImage);
            imageView.setBackgroundResource(R.drawable.fanhui);
            View backup = getWindow().findViewById(R.id.titlebarBackup);
            backup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ArticleContentActivity.this.finish();
                }
            });
        }
        //更多菜单
        mMoreMenu = (ImageView)findViewById(R.id.moreMenuImage);
        mMoreMenu.setBackgroundResource(R.drawable.gengduo);
        mMoreMenu.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                popupMenu.show();
            }
        });
        popupMenu = new PopupMenu(this, mMoreMenu);
        popupMenu.getMenuInflater().inflate(R.menu.menu_base, popupMenu.getMenu()); // 绑定菜单的视图
        popupMenu.getMenu().findItem(R.id.share).setIcon(R.drawable.share);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() { // 设置监听器
            public boolean onMenuItemClick(MenuItem item) {

                return true;
            }
        });
        try {
            Field field = popupMenu.getClass().getDeclaredField("android.widget.PopupMenu.mPopup");
            field.setAccessible(true);
            MenuPopupHelper mHelper = (MenuPopupHelper) field.get(popupMenu);
            mHelper.setForceShowIcon(true);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    //enable为true时，菜单添加图标有效，enable为false时无效。4.0系统默认无效
    private void setIconEnable(PopupMenu menu, boolean enable)
    {
        try
        {
            Class<?> clazz = Class.forName("com.android.internal.view.menu.MenuBuilder");
            Method m = clazz.getDeclaredMethod("setOptionalIconsVisible", boolean.class);
            m.setAccessible(true);

            //MenuBuilder实现Menu接口，创建菜单时，传进来的menu其实就是MenuBuilder对象(java的多态特征)
            m.invoke(menu, enable);

        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * 初始化视图组件
     */
    private void initViews() {
        articalWV = (WebView) findViewById(R.id.textShow);
        mLinearLayout = (LinearLayout) findViewById(R.id.play_linear);
        playProgressSB = (SeekBar) findViewById(R.id.seekbar);
        playAudioBtn = (TextView) findViewById(R.id.play);

        playAudioBtn.setOnClickListener(clickListener);
    }

    /**
     * 初始化逻辑对象
     */
    private void initLgObjs() {
        appContext = this;
        refreshPlayProgressTimer = new Timer();

        playProgressSB.setOnSeekBarChangeListener(new OnSeekBarChangeListener(){

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                audioPlayer.seekTo(seekBar.getProgress());
            }

        });
    }

    @Override
    protected void onDestroy() {
        if(null != audioPlayer){
            refreshPlayProgressTimer.cancel(); // 取消用于更新播放进度的定时任务
            audioPlayer.stop(); // 停止播放音乐
            audioPlayer.release();
            audioPlayer = null; // 将此对象置为null

        }
        super.onDestroy();
    }

    private class GetDataTask extends AsyncTask<Void, Void, String[]> {

        String text = new String();
        Document doc;

        @Override
        protected String[] doInBackground(Void... params) {
            // Simulates a background job.
            try {
                doc = Jsoup.connect(mTextWebsite).get();
                Element content = doc.getElementById("content");
                text = content.toString();

                Element video = doc.getElementById("mp3");
                if (video == null) {

                } else {
                    mp3UriString = video.attr("href");
                }
                ;

            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String[] result) {
            articalWV.loadDataWithBaseURL(ListOfArticleFragment.WEBSITE_HEAD, text, "text/html", "utf-8", null);
            if (!mp3UriString.equals("noMP3")) {
                mLinearLayout.setVisibility(View.VISIBLE);
                audioPlayer = MediaPlayer.create(getApplicationContext(), Uri.parse(mp3UriString)); // 使用下载的音频实例化播放器对象
                playProgressSB.setMax(audioPlayer.getDuration()); // 设置播放的总长度
                playAudioBtn.setEnabled(true);
            } else {
                // mLinearLayout.setVisibility(View.VISIBLE);
                mLinearLayout.setVisibility(View.GONE);
            }
            super.onPostExecute(result);
        }
    }

    /**
     * 单击监听器
     */
    private OnClickListener clickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {

            switch (v.getId()) {
                case R.id.play: {
                    if (!audioPlayer.isPlaying()) {
                        playAudioBtn.setBackgroundResource(R.drawable.bofang); // 更新按钮图标
                        audioPlayer.start(); // 启动播放器
                        refreshPlayProgressTimer = new Timer();
                        refreshPlayProgressTimer.schedule(new refreshPlayProgressTimertask(), 0, 500); // 启动用于更新播放进度的定时任务
                    } else {
                        playAudioBtn.setBackgroundResource(R.drawable.right); // 更新按钮图标
                        audioPlayer.pause(); // 暂停播放
                        refreshPlayProgressTimer.cancel(); // 取消用于更新播放进度的定时任务
                    }
                    break;
                }
            }
        }
    };

    /**
     * 用来更新播放进度的定时器任务
     */
    private class refreshPlayProgressTimertask extends TimerTask {
        @Override
        public void run() {
            audioPlayer.getDuration();
            playProgressSB.setProgress(audioPlayer.getCurrentPosition());
        }
    }
}