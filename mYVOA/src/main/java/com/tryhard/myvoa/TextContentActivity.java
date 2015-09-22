package com.tryhard.myvoa;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.app.Activity;
import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class TextContentActivity extends Activity {

	// 常量
	private static final String TAG = "myvoa-->CultureTextContentActivity";
	public static final String CONTENT_WEBSITE = "contentWebsite";

	// 标志型变量

	// 数据型变量
	private String mTextWebsite;
	private String mp3UriString = "noMP3";

	// 视图组件
	private LinearLayout mLinearLayout;
	private WebView articalWV; // 显示文章内容的网页
	private SeekBar playProgressSB; // 播放进度条
	private TextView playAudioBtn; // 播放按钮

	// 逻辑对象
	private Context appContext;
	private MediaPlayer audioPlayer;
	private Timer refreshPlayProgressTimer;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.text_show);

		// mWebView.getSettings().setJavaScriptEnabled(true);

		mTextWebsite = InnerListFragment.WEBSITE_HEAD + (String) getIntent().getSerializableExtra(CONTENT_WEBSITE);
		new GetDataTask().execute();

		initViews();
		initLgObjs();
		

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
				// TODO Auto-generated method stub
			
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
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

		StringBuilder textString = new StringBuilder();
		String text = new String();
		String temp;

		@Override
		protected String[] doInBackground(Void... params) {
			// Simulates a background job.
			try {
				Document doc = Jsoup.connect(mTextWebsite).get();
				Element content = doc.getElementById("content");
				Elements list = content.getAllElements();
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
			articalWV.loadDataWithBaseURL(InnerListFragment.WEBSITE_HEAD, text, "text/html", "utf-8", null);
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
	};
}