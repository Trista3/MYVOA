package com.tryhard.myvoa.ui.activity;

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

import com.tryhard.myvoa.ui.activity.base.BaseActivity;
import com.tryhard.myvoa.ui.fragment.InnerListFragment;
import com.tryhard.myvoa.R;

public class TextContentActivity extends BaseActivity {


    public static final String CONTENT_WEBSITE = "contentWebsite";


    private String mTextWebsite;
    private String mp3UriString = "noMP3";


    private LinearLayout mLinearLayout;
    private WebView articalWV; // ��ʾ�������ݵ���ҳ
    private SeekBar playProgressSB; // ���Ž����
    private TextView playAudioBtn; // ���Ű�ť


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
     * ��ʼ����ͼ���
     */
    private void initViews() {
        articalWV = (WebView) findViewById(R.id.textShow);
        mLinearLayout = (LinearLayout) findViewById(R.id.play_linear);
        playProgressSB = (SeekBar) findViewById(R.id.seekbar);
        playAudioBtn = (TextView) findViewById(R.id.play);

        playAudioBtn.setOnClickListener(clickListener);
    }

    /**
     * ��ʼ���߼�����
     */
    private void initLgObjs() {
        appContext = this;
        refreshPlayProgressTimer = new Timer();

        playProgressSB.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

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
        if (null != audioPlayer) {
            refreshPlayProgressTimer.cancel(); // ȡ�����ڸ��²��Ž�ȵĶ�ʱ����
            audioPlayer.stop(); // ֹͣ��������
            audioPlayer.release();
            audioPlayer = null; // ���˶�����Ϊnull

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
                audioPlayer = MediaPlayer.create(getApplicationContext(), Uri.parse(mp3UriString)); // ʹ�����ص���Ƶʵ���������
                playProgressSB.setMax(audioPlayer.getDuration()); // ���ò��ŵ��ܳ���
                playAudioBtn.setEnabled(true);
            } else {
                // mLinearLayout.setVisibility(View.VISIBLE);
                mLinearLayout.setVisibility(View.GONE);
            }
            super.onPostExecute(result);
        }
    }

    /**
     * ����������
     */
    private OnClickListener clickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {

            switch (v.getId()) {
                case R.id.play: {
                    if (!audioPlayer.isPlaying()) {
                        playAudioBtn.setBackgroundResource(R.drawable.bofang); // ���°�ťͼ��
                        audioPlayer.start(); // ����������
                        refreshPlayProgressTimer = new Timer();
                        refreshPlayProgressTimer.schedule(new refreshPlayProgressTimertask(), 0, 500); // �������ڸ��²��Ž�ȵĶ�ʱ����
                    } else {
                        playAudioBtn.setBackgroundResource(R.drawable.right); // ���°�ťͼ��
                        audioPlayer.pause(); // ��ͣ����
                        refreshPlayProgressTimer.cancel(); // ȡ�����ڸ��²��Ž�ȵĶ�ʱ����
                    }
                    break;
                }
            }
        }
    };

    /**
     * �������²��Ž�ȵĶ�ʱ������
     */
    private class refreshPlayProgressTimertask extends TimerTask {
        @Override
        public void run() {

            audioPlayer.getDuration();
            playProgressSB.setProgress(audioPlayer.getCurrentPosition());
        }
    }

    ;
}