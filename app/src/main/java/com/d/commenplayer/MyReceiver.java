package com.d.commenplayer;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.util.Log;
import android.widget.LinearLayout;

/**
 * 监听系统音量
 *
 * @author by
 */
public class MyReceiver extends BroadcastReceiver {

    public static final String ACTION_VOLUME_CHANGED = "android.media.VOLUME_CHANGED_ACTION";
    public static final String EXTRA_VOLUME_STREAM_TYPE = "android.media.EXTRA_VOLUME_STREAM_TYPE";
    public static final String RINGER_MODE_CHANGED_ACTION = "android.media.RINGER_MODE_CHANGED";
    private VolumeChangeListener mVolumeChangeListener;

    public void setVolumeChangeListener(VolumeChangeListener volumeChangeListener) {
        this.mVolumeChangeListener = volumeChangeListener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(AudioManager.RINGER_MODE_CHANGED_ACTION)) {
            AudioManager am = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
            final int ringerMode = am.getRingerMode();
            switch (ringerMode) {
                case AudioManager.RINGER_MODE_NORMAL:
                    //normal
                    break;
                case AudioManager.RINGER_MODE_VIBRATE:
                    //vibrate
                    break;
                case AudioManager.RINGER_MODE_SILENT:
                    //silent
                    break;
            }
        }
        synchronized (this) {
            if (!getProcessName(context).equals("com.test.cn")) {
                return;
            }
            if (ACTION_VOLUME_CHANGED.equals(intent.getAction())
                    && (intent.getIntExtra(EXTRA_VOLUME_STREAM_TYPE, -1) == AudioManager.STREAM_MUSIC)) {
                handleChangeListener(context, AudioManager.STREAM_MUSIC);
            } else if (ACTION_VOLUME_CHANGED.equals(intent.getAction())) {
                handleChangeListener(context, AudioManager.STREAM_SYSTEM);
            }
        }

    }

    public interface VolumeChangeListener {
        /**
         * 系统媒体音量变化
         *
         * @param volume 音量大小
         */
        void onVolumeChanged(int volume);
    }



    int musicVoice=0;
    int systenVoice=0;
    public void handleChangeListener(Context context, int type) {
        AudioManager mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        int streamVolume = 0;
        if (mAudioManager != null) {
            streamVolume = mAudioManager.getStreamVolume(type);
            if (type == AudioManager.STREAM_SYSTEM) {
                if (systenVoice==streamVolume){
                    return;
                }
                systenVoice=streamVolume;
                Log.e("TAG",   "系统音量" + streamVolume);
            } else if (type == AudioManager.STREAM_MUSIC) {
                if (musicVoice==streamVolume){
                    return;
                }
                musicVoice=streamVolume;
                Log.e("TAG",   "媒体音量" + streamVolume);
            }
            if (mVolumeChangeListener != null) {

                mVolumeChangeListener.onVolumeChanged(streamVolume);

            }
        }
    }

    /***获取当前进程名称*/
    private String getProcessName(Context context) {
        int pid = android.os.Process.myPid();
        ActivityManager mActivityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (mActivityManager != null) {
            for (ActivityManager.RunningAppProcessInfo appProcess : mActivityManager.getRunningAppProcesses()) {
                if (appProcess.pid == pid) {
                    return appProcess.processName;
                }
            }
        }
        return null;
    }

}