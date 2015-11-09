package com.tryhard.myvoa.util;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.widget.SeekBar;

import com.tryhard.myvoa.ui.activity.ArticleContentActivity;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

public class ArticleVideoPlayService extends Service implements
        MediaPlayer.OnCompletionListener {
    /* 定于一个多媒体对象 */
    public static MediaPlayer mMediaPlayer = null;
    public static final String SERVICE_EXTRA = "ArticleVideoPlayService";
    public static final String PLAY = "play";
    public static final String STOP = "stop";
    private Timer refreshPlayProgressTimer;
    private MyBinder binder = new MyBinder();

    public class MyBinder extends Binder
    {
        public void restartAudio(){
            playMusic();
        }
        public void pauseAudio(){
            mMediaPlayer.pause();
        }
    }
    @Override
    public IBinder onBind(Intent intent) {
        refreshPlayProgressTimer = new Timer();
        HashMap extra = (HashMap)intent.getSerializableExtra(SERVICE_EXTRA);
        String mp3UriString = (String)extra.get("mp3UriString");
        if (!mp3UriString.equals("noMP3")) {
            mMediaPlayer = MediaPlayer.create(getApplicationContext(), Uri.parse(mp3UriString)); // 使用下载的音频实例化播放器对象


        /* 监听播放是否完成 */
            mMediaPlayer.setOnCompletionListener(this);
            ArticleContentActivity.audioSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    mMediaPlayer.seekTo(seekBar.getProgress());
                }

            });
        }
        return binder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        mMediaPlayer.pause();
        refreshPlayProgressTimer.cancel();
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
        return super.onUnbind(intent);
    }


    @Override
    public void onCreate() {
        super.onCreate();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        refreshPlayProgressTimer.cancel();
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    public void playMusic() {
        mMediaPlayer.start(); // 启动播放器
        // 设置进度条最大值
        ArticleContentActivity.audioSeekBar.setMax(ArticleVideoPlayService.mMediaPlayer
                .getDuration());
        refreshPlayProgressTimer.schedule(new refreshPlayProgressTimertask(), 0, 500); // 启动用于更新播放进度的定时任务

    }

    private class refreshPlayProgressTimertask extends TimerTask {
        @Override
        public void run() {
            mMediaPlayer.getDuration();
            ArticleContentActivity.audioSeekBar.setProgress(mMediaPlayer.getCurrentPosition());
        }
    }
    @Override
    public void onCompletion(MediaPlayer mp) {

    }
}
