package com.d.commenplayer.util.constant;

import android.content.Context;
import android.os.Handler;
import android.widget.Toast;

import com.d.commenplayer.util.ToastUtil;

/**
 * 功能描述:自定义toast显示时长
 */
public class CustomToast {
    public String message;
    private Context mContext;
    private Handler mHandler = new Handler();
    private boolean canceled = false;

    public CustomToast(Context context, String msg) {
        message = msg;
        mContext = context;
    }

    /**
     * 隐藏toast
     */
    public void hide() {
        canceled = true;
    }

    public void show() {
        canceled = false;
    }

    public void showUntilCancel() {
        if (canceled) { //如果已经取消显示，就直接return
            return;
        }
        ToastUtil.show(mContext, message);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                showUntilCancel();
            }
        }, 2000);
    }


}