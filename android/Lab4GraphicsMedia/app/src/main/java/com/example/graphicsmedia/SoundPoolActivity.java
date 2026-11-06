package com.example.graphicsmedia;

import android.app.Activity;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 程序5：SoundPool 播放音效。
 * SoundPool 适合播放短促的音效，支持多个音频同时播放、低延迟。
 * 音频文件放在 res/raw 下：
 *   windbell.wav（风铃声）、cuckoo.wav（布谷鸟叫声）、
 *   doorbell.wav（门铃声）、phonebell.mp3（电话声）、ring.wav（按键音）。
 */
public class SoundPoolActivity extends Activity implements View.OnClickListener {

    /** 最多同时播放 5 路音频 */
    private static final int MAX_STREAMS = 5;

    private final String[] soundNames = {
            "风铃声 windbell.mp3", "布谷鸟叫声 cuckoo.wav",
            "门铃声 doorbell.wav", "电话声 phonebell.mp3"
    };
    private final int[] rawIds = {R.raw.windbell, R.raw.cuckoo, R.raw.doorbell, R.raw.phonebell};
    private final int[] soundIds = new int[4];

    private SoundPool soundPool;
    private TextView tvLog;
    /** 按键音 ring.wav 对应的音频 id */
    private int ringSoundId;
    private int loadedCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_soundpool);

        tvLog = (TextView) findViewById(R.id.tvSoundLog);

        // 1) 初始化 SoundPool：最大同时播放数与音频属性
        AudioAttributes attributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();
        soundPool = new SoundPool.Builder()
                .setMaxStreams(MAX_STREAMS)
                .setAudioAttributes(attributes)
                .build();

        // 2) 加载音频：load() 返回音频 id；加载完成回调 onLoadComplete
        soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                loadedCount++;
                log("音频加载完成 sampleId=" + sampleId + " status=" + status);
                if (loadedCount == soundIds.length + 1) {
                    log("5 个音效已全部加载，点击按钮即可播放");
                }
            }
        });
        for (int i = 0; i < rawIds.length; i++) {
            soundIds[i] = soundPool.load(this, rawIds[i], 1);
            log("load " + soundNames[i] + " → soundId=" + soundIds[i]);
        }
        // 按键音
        ringSoundId = soundPool.load(this, R.raw.ring, 1);
        log("load 按键音 ring.wav → soundId=" + ringSoundId);

        int[] buttonIds = {R.id.btnSound1, R.id.btnSound2, R.id.btnSound3, R.id.btnSound4};
        for (int id : buttonIds) {
            findViewById(id).setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View v) {
        int index = -1;
        if (v.getId() == R.id.btnSound1) index = 0;
        else if (v.getId() == R.id.btnSound2) index = 1;
        else if (v.getId() == R.id.btnSound3) index = 2;
        else if (v.getId() == R.id.btnSound4) index = 3;
        if (index >= 0) {
            // 3) 播放：play(左声道, 右声道, 优先级, 循环次数, 播放速率)
            int streamId = soundPool.play(soundIds[index], 1, 1, 0, 0, 1);
            log("play " + soundNames[index] + " → streamID=" + streamId);
        }
    }

    /** 按下任意按键时播放按键音 ring.wav */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (soundPool != null) {
            int streamId = soundPool.play(ringSoundId, 1, 1, 0, 0, 1);
            log("onKeyDown(keyCode=" + keyCode + ") 播放按键音 ring.wav → streamID=" + streamId);
        }
        return super.onKeyDown(keyCode, event);
    }

    private void log(String text) {
        String time = new SimpleDateFormat("HH:mm:ss", Locale.CHINA).format(new Date());
        tvLog.append(time + "  " + text + "\n");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 4) 释放 SoundPool 资源
        if (soundPool != null) {
            soundPool.release();
            soundPool = null;
        }
    }
}
