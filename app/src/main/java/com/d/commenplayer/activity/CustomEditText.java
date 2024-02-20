package com.d.commenplayer.activity;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.widget.EditText;

public class CustomEditText extends android.support.v7.widget.AppCompatEditText  {


    public SendMessageActivity activity;
    public AlertDialog dialog;

    public static boolean no_ignore = true;//避免模拟输入法触发
    public boolean hujiao_flag = false;

    public  boolean is_inside = false;

    public CustomEditText(Context context) {
        super(context);
    }

    public CustomEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        return super.dispatchKeyEvent(event);
    }

    @Override
    public boolean dispatchKeyEventPreIme(KeyEvent event) {
        if(hujiao_flag)
            return true;
        if(event.getKeyCode()==45&&event.getAction() == KeyEvent.ACTION_UP&&no_ignore)
        {
            if(is_inside)
                dialog.dispatchKeyEvent(event);
            else
                activity.dispatchKeyEvent(event);
            return true;
        }
        else if(event.getKeyCode()==51&&event.getAction() == KeyEvent.ACTION_UP&&no_ignore)
        {
            if(is_inside)
                dialog.dispatchKeyEvent(event);
            else
                activity.dispatchKeyEvent(event);
            return true;
        }
        else if(event.getKeyCode()==46&&event.getAction() == KeyEvent.ACTION_UP&&no_ignore)
        {
            if(is_inside)
                dialog.dispatchKeyEvent(event);
            else
                activity.dispatchKeyEvent(event);
            return true;
        }
        else if(event.getKeyCode()==48&&event.getAction() == KeyEvent.ACTION_UP&&no_ignore)
        {
            if(is_inside)
                dialog.dispatchKeyEvent(event);
            else
                activity.dispatchKeyEvent(event);
            return true;
        }
        else if(event.getKeyCode()==159&&event.getAction() == KeyEvent.ACTION_UP&&no_ignore)
        {
            if(is_inside)
                dialog.dispatchKeyEvent(event);
            else
                activity.dispatchKeyEvent(event);
            return true;
        }
        else if(event.getKeyCode()==213&&event.getAction() == KeyEvent.ACTION_UP&&no_ignore)
        {
            if(is_inside)
                dialog.dispatchKeyEvent(event);
            else
                activity.dispatchKeyEvent(event);
            return true;
        }
        else if(event.getKeyCode()==218&&event.getAction() == KeyEvent.ACTION_UP&&no_ignore)
        {
            if(is_inside)
                dialog.dispatchKeyEvent(event);
            else
                activity.dispatchKeyEvent(event);
            return true;
        }
        else if(event.getKeyCode()==212&&event.getAction() == KeyEvent.ACTION_UP&&no_ignore)
        {
            if(is_inside)
                dialog.dispatchKeyEvent(event);
            else
                activity.dispatchKeyEvent(event);
            return true;
        }
        else
        {
            if(event.getAction() == KeyEvent.ACTION_UP)
                CustomEditText.no_ignore = true;
            return false;
        }
    }
}
