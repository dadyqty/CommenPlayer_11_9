package com.d.commenplayer.activity;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Instrumentation;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.inputmethodservice.InputMethodService;
import android.media.audiofx.EnvironmentalReverb;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.text.method.ReplacementTransformationMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputMethodInfo;
import android.view.inputmethod.InputMethodManager;
import android.view.inputmethod.InputMethodSubtype;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.d.commenplayer.R;
import com.d.commenplayer.comn.Device;
import com.d.commenplayer.comn.message.IMessage;
import com.d.commenplayer.comn.message.SerialPortManager;
import com.d.commenplayer.util.NavigationBarUtil;
import com.d.commenplayer.util.ToastUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import java.lang.ref.WeakReference;
import java.nio.CharBuffer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;


public class SendMessageActivity extends AppCompatActivity {
    private static final int WHAT_SEND_ERROR = 10;
    private long predowmTime =0;
    private int count_downtime=0;
    private int count_changlanguage;

    private int send_state = 0;
    private boolean flag_number = false;

    private boolean lianxiren = false;

    private boolean caplook = false;

    private boolean suiyiflag = false;
    private MyHandle myHandle = new MyHandle(this);
    private TextView languagee;
    private boolean[] isdelay = new boolean[14];
    private boolean issend = false;
    private int last_keydown = 0;
    private int last_messagecode = 0;

    private int start_temp ;
    private int[] countarray = new int[14];
    private Timer timer;
    private MsgRevDao dao;

    private ContactsDao dao2;
    private boolean isRunning;
    private boolean mOpened = false;
    private Device mDevice;

    private boolean fuhao_flag = false;

    private EditText address;
    private CustomEditText shortmessage;

    private EditText shoujianren;

    private Editable editable;
    private CustomEditText xinxineirong;

    private Button danfa;

    private Button qunfa;

    private Button duofa;

    private Button shoujianxiang;

    private Button fajianxiang;

    private String name = null;
    private String number = null;

    int i = 0;
    private String SendData;

    private String address_string;

    private String shortmessage_string;

    private String lianxirenzhongduan;

    private CharSequence charSequence;

    private List<MsgInfo> datalist;

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if((address.isSelected()&&!xinxineirong.isSelected())){
            if (event.getKeyCode() == KeyEvent.KEYCODE_F11&& event.getAction() == KeyEvent.ACTION_UP)       //数字0键 KeyCode() == 141, KEYCODE_F11 = 141
            {
                sendKeyCode(7);//对应数字0
            }
            if (event.getKeyCode() == KeyEvent.KEYCODE_F12&& event.getAction() == KeyEvent.ACTION_UP)       //数字1键 KeyCode() == 142, KEYCODE_F12 = 142
            {

                sendKeyCode(8);//对应数字1

            }
            if (event.getKeyCode() == KeyEvent.KEYCODE_F2 && event.getAction() == KeyEvent.ACTION_UP)       //数字2键 KeyCode() == 132, KEYCODE_F2 = 132
            {
                sendKeyCode(9);//对应数字2

            }
            if (event.getKeyCode() == KeyEvent.KEYCODE_F3 && event.getAction() == KeyEvent.ACTION_UP)     //数字3键 KeyCode() == 133, KEYCODE_F4 = 133
            {
                sendKeyCode(10);//对应数字3

            }
            if (event.getKeyCode() == KeyEvent.KEYCODE_F4 && event.getAction() == KeyEvent.ACTION_UP)     //数字4键 KeyCode() == 134, KEYCODE_F4 = 134
            {
                sendKeyCode(11);//对应数字4
            }
            if (event.getKeyCode() == KeyEvent.KEYCODE_F5 && event.getAction() == KeyEvent.ACTION_UP)     //数字5键 KeyCode() == 135, KEYCODE_F5 = 135
            {
                sendKeyCode(12);//对应数字5
            }
            if (event.getKeyCode() == KeyEvent.KEYCODE_F6 && event.getAction() == KeyEvent.ACTION_UP)     //数字6键 KeyCode() == 136, KEYCODE_F6 = 136
            {
                sendKeyCode(13);//对应数字6
            }
            if (event.getKeyCode() == KeyEvent.KEYCODE_F7 && event.getAction() == KeyEvent.ACTION_UP)     //数字7键 KeyCode() == 137, KEYCODE_F7 = 137
            {
                sendKeyCode(14);//对应数字7
            }
            if (event.getKeyCode() == KeyEvent.KEYCODE_F8 && event.getAction() == KeyEvent.ACTION_UP)     //数字8键 KeyCode() == 138, KEYCODE_F8 = 138
            {
                sendKeyCode(15);//对应数字8
            }
            if (event.getKeyCode() == KeyEvent.KEYCODE_F9 && event.getAction() == KeyEvent.ACTION_UP)      //数字9键 KeyCode() == 139, KEYCODE_F9 = 139
            {
                sendKeyCode(16);//对应数字9
            }
        }
        if(shortmessage.isSelected()||xinxineirong.isSelected()) {
            if (event.getKeyCode() == 278 && event.getAction() == KeyEvent.ACTION_UP)  //输入法键 KeyCode() == 278, 切换输入法
            {
                if(count_changlanguage==1)
                {
                    languagee_temp = "中文";
                }
                else if(count_changlanguage==0)
                {
                    languagee_temp = "英文";
                }
                Log.i("TAG","dispatchKeyEvent"+event.getKeyCode());
                Message message = new Message();
                message.what = 13;
                myHandle.sendMessage(message);
                count_changlanguage++;
                Log.i("获取按键键值", "dispatchKeyEvent: "+count_changlanguage);
            }
            if (event.getKeyCode() == KeyEvent.KEYCODE_F12&& event.getAction() == KeyEvent.ACTION_UP)       //数字1键 KeyCode() == 142, KEYCODE_F12 = 142
            {
                Message message = new Message();
                message.what = 12;
                myHandle.sendMessage(message);
//                Log.i("TAG","dispatchKeyEvent"+event.getKeyCode());
//                if (last_keydown != event.getKeyCode() && last_keydown != 0) {
//                    isdelay[last_messagecode] = false;
//                    issend = true;
//                    isdelay[12] = true;
//                    predowmTime = event.getDownTime();
//                }
//                if (event.getDownTime() - predowmTime <= 400 || predowmTime==0){
//                    predowmTime = event.getDownTime();
//                    if(last_keydown == event.getKeyCode() || last_keydown==0)
//                        countarray[12]++;
//                    Log.i("识别", "dispatchKeyEvent: "+(event.getEventTime()-predowmTime)+"计数"
//                            +countarray[12]);
//                } else if(!issend){
//                    predowmTime = event.getDownTime();
//                    Message message = new Message();
//                    message.what = 12;
//                    myHandle.sendMessage(message);
//                    isdelay[12] = true;
//                }
//                last_keydown = event.getKeyCode();
//                last_messagecode = 12;
            }
            if (event.getKeyCode() == KeyEvent.KEYCODE_F2 && event.getAction() == KeyEvent.ACTION_UP)       //数字2键 KeyCode() == 132, KEYCODE_F2 = 132
            {
                Log.i("TAG","dispatchKeyEvent"+event.getKeyCode());
                if (last_keydown != event.getKeyCode() && last_keydown != 0) {
                    isdelay[last_messagecode] = false;
                    issend = true;
                    isdelay[2] = true;
                    predowmTime = event.getDownTime();
                    Message message = new Message();
                    message.what = 2;
                    myHandle.sendMessage(message);
                }
                if (event.getDownTime() - predowmTime <= 400 || predowmTime==0){
                    predowmTime = event.getDownTime();
                    if(last_keydown == event.getKeyCode() || last_keydown==0)
                        countarray[2]++;
                    Log.i("识别", "dispatchKeyEvent: "+(event.getEventTime()-predowmTime)+"计数"
                            +countarray[2]);
                } else if(!issend){
                    predowmTime = event.getDownTime();
                    Message message = new Message();
                    message.what = 2;
                    myHandle.sendMessage(message);
                    isdelay[2] = true;
                }
                last_keydown = event.getKeyCode();
                last_messagecode = 2;
            }
            if (event.getKeyCode() == KeyEvent.KEYCODE_F3 && event.getAction() == KeyEvent.ACTION_UP)     //数字3键 KeyCode() == 133, KEYCODE_F4 = 133
            {
                Log.i("TAG","dispatchKeyEvent"+event.getKeyCode());
                if (last_keydown != event.getKeyCode() && last_keydown != 0) {
                    isdelay[last_messagecode] = false;
                    issend = true;
                    isdelay[3] = true;
                    predowmTime = event.getDownTime();
                    Message message = new Message();
                    message.what = 3;
                    myHandle.sendMessage(message);
                }

                if (event.getDownTime() - predowmTime <= 400 || predowmTime==0){
                    predowmTime = event.getDownTime();
                    if(last_keydown == event.getKeyCode() || last_keydown==0)
                        countarray[3]++;
                    Log.i("识别", "dispatchKeyEvent: "+(event.getEventTime()-predowmTime)+"计数"
                            +countarray[3]);
                } else if(!issend){
                    predowmTime = event.getDownTime();
                    Message message = new Message();
                    message.what = 3;
                    myHandle.sendMessage(message);
                    isdelay[3] = true;
                }
                last_keydown = event.getKeyCode();
                last_messagecode = 3;
            }
            if (event.getKeyCode() == KeyEvent.KEYCODE_F4 && event.getAction() == KeyEvent.ACTION_UP)     //数字4键 KeyCode() == 134, KEYCODE_F4 = 134
            {
                Log.i("TAG","dispatchKeyEvent"+event.getKeyCode());
                if (last_keydown != event.getKeyCode() && last_keydown != 0) {
                    isdelay[last_messagecode] = false;
                    issend = true;
                    isdelay[4] = true;
                    predowmTime = event.getDownTime();
                    Message message = new Message();
                    message.what = 4;
                    myHandle.sendMessage(message);
                }
                if (event.getDownTime() - predowmTime <= 400 || predowmTime==0){
                    predowmTime = event.getDownTime();
                    if(last_keydown == event.getKeyCode() || last_keydown==0)
                        countarray[4]++;
                    Log.i("识别", "dispatchKeyEvent: "+(event.getEventTime()-predowmTime)+"计数"
                            +countarray[4]);
                } else if(!issend){
                    predowmTime = event.getDownTime();
                    Message message = new Message();
                    message.what = 4;
                    myHandle.sendMessage(message);
                    isdelay[4] = true;
                }
                last_keydown = event.getKeyCode();
                last_messagecode = 4;
            }
            if (event.getKeyCode() == KeyEvent.KEYCODE_F5 && event.getAction() == KeyEvent.ACTION_UP)     //数字5键 KeyCode() == 135, KEYCODE_F5 = 135
            {
                Log.i("TAG","dispatchKeyEvent"+event.getKeyCode());
                if (last_keydown != event.getKeyCode() && last_keydown != 0) {
                    isdelay[last_messagecode] = false;
                    issend = true;
                    isdelay[5] = true;
                    predowmTime = event.getDownTime();
                    Message message = new Message();
                    message.what = 5;
                    myHandle.sendMessage(message);
                }
                if (event.getDownTime() - predowmTime <= 400 || predowmTime==0){
                    predowmTime = event.getDownTime();
                    if(last_keydown == event.getKeyCode() || last_keydown==0)
                        countarray[5]++;
                    Log.i("识别", "dispatchKeyEvent: "+(event.getEventTime()-predowmTime)+"计数"
                            +countarray[5]);
                } else if(!issend){
                    predowmTime = event.getDownTime();
                    Message message = new Message();
                    message.what = 5;
                    myHandle.sendMessage(message);
                    isdelay[5] = true;
                }
                last_keydown = event.getKeyCode();
                last_messagecode = 5;
            }
            if (event.getKeyCode() == KeyEvent.KEYCODE_F6 && event.getAction() == KeyEvent.ACTION_UP)     //数字6键 KeyCode() == 136, KEYCODE_F6 = 136
            {
                Log.i("TAG","dispatchKeyEvent"+event.getKeyCode());
                if (last_keydown != event.getKeyCode() && last_keydown != 0) {
                    isdelay[last_messagecode] = false;
                    issend = true;
                    isdelay[6] = true;
                    predowmTime = event.getDownTime();
                    Message message = new Message();
                    message.what = 6;
                    myHandle.sendMessage(message);
                }
                if (event.getDownTime() - predowmTime <= 400 || predowmTime==0){
                    predowmTime = event.getDownTime();
                    if(last_keydown == event.getKeyCode() || last_keydown==0)
                        countarray[6]++;
                    Log.i("识别", "dispatchKeyEvent: "+(event.getEventTime()-predowmTime)+"计数"
                            +countarray[6]);
                } else if(!issend){
                    predowmTime = event.getDownTime();
                    Message message = new Message();
                    message.what = 6;
                    myHandle.sendMessage(message);
                    isdelay[6] = true;
                }
                last_keydown = event.getKeyCode();
                last_messagecode = 6;
            }
            if (event.getKeyCode() == KeyEvent.KEYCODE_F7 && event.getAction() == KeyEvent.ACTION_UP)     //数字7键 KeyCode() == 137, KEYCODE_F7 = 137
            {
                Log.i("TAG","dispatchKeyEvent"+event.getKeyCode());
                if (last_keydown != event.getKeyCode() && last_keydown != 0) {
                    isdelay[last_messagecode] = false;
                    issend = true;
                    isdelay[7] = true;
                    predowmTime = event.getDownTime();
                    Message message = new Message();
                    message.what = 7;
                    myHandle.sendMessage(message);
                }
                if (event.getDownTime() - predowmTime <= 400 || predowmTime==0){
                    predowmTime = event.getDownTime();
                    if(last_keydown == event.getKeyCode() || last_keydown==0)
                        countarray[7]++;
                    Log.i("识别", "dispatchKeyEvent: "+(event.getEventTime()-predowmTime)+"计数"
                            +countarray[7]);
                } else if(!issend){
                    predowmTime = event.getDownTime();
                    Message message = new Message();
                    message.what = 7;
                    myHandle.sendMessage(message);
                    isdelay[7] = true;
                }
                last_keydown = event.getKeyCode();
                last_messagecode = 7;
            }
            if (event.getKeyCode() == KeyEvent.KEYCODE_F8 && event.getAction() == KeyEvent.ACTION_UP)     //数字8键 KeyCode() == 138, KEYCODE_F8 = 138
            {
                Log.i("TAG","dispatchKeyEvent"+event.getKeyCode());
                if (last_keydown != event.getKeyCode() && last_keydown != 0) {
                    isdelay[last_messagecode] = false;
                    issend = true;
                    isdelay[8] = true;
                    predowmTime = event.getDownTime();
                    Message message = new Message();
                    message.what = 8;
                    myHandle.sendMessage(message);
                }
                if (event.getDownTime() - predowmTime <= 400 || predowmTime==0){
                    predowmTime = event.getDownTime();
                    if(last_keydown == event.getKeyCode() || last_keydown==0)
                        countarray[8]++;
                    Log.i("识别", "dispatchKeyEvent: "+(event.getEventTime()-predowmTime)+"计数"
                            +countarray[8]);
                } else if(!issend){
                    predowmTime = event.getDownTime();
                    Message message = new Message();
                    message.what = 8;
                    myHandle.sendMessage(message);
                    isdelay[8] = true;
                }
                last_keydown = event.getKeyCode();
                last_messagecode = 8;
            }
            if (event.getKeyCode() == KeyEvent.KEYCODE_F9 && event.getAction() == KeyEvent.ACTION_UP)      //数字9键 KeyCode() == 139, KEYCODE_F9 = 139
            {
                Log.i("TAG","dispatchKeyEvent"+event.getKeyCode());
                if (last_keydown != event.getKeyCode() && last_keydown != 0) {
                    isdelay[last_messagecode] = false;
                    issend = true;
                    isdelay[9] = true;
                    predowmTime = event.getDownTime();
                    Message message = new Message();
                    message.what = 9;
                    myHandle.sendMessage(message);
                }
                if (event.getDownTime() - predowmTime <= 400 || predowmTime==0){
                    predowmTime = event.getDownTime();
                    if(last_keydown == event.getKeyCode() || last_keydown==0)
                        countarray[9]++;
                    Log.i("识别", "dispatchKeyEvent: "+(event.getEventTime()-predowmTime)+"计数"
                            +countarray[9]);
                } else if(!issend){
                    predowmTime = event.getDownTime();
                    Message message = new Message();
                    message.what = 9;
                    myHandle.sendMessage(message);
                    isdelay[9] = true;
                }
                last_keydown = event.getKeyCode();
                last_messagecode = 9;
            }
            if (event.getKeyCode() == KeyEvent.KEYCODE_F10&& event.getAction() == KeyEvent.ACTION_UP)      //*星号键 KeyCode() == 140, KEYCODE_F10 = 140
            {
                Log.i("TAG","dispatchKeyEvent"+event.getKeyCode());
                if (event.getDownTime() - predowmTime <= 400 || predowmTime==0){
                    predowmTime = event.getDownTime();
                    count_downtime++;
                    Log.i("识别", "dispatchKeyEvent: "+(event.getEventTime()-predowmTime)+"计数"
                            +count_downtime);
                } else {
                    predowmTime = event.getDownTime();
                    Message message = new Message();
                    message.what = 10;
                    myHandle.sendMessage(message);
                }
            }
            if (event.getKeyCode() == KeyEvent.KEYCODE_F11&& event.getAction() == KeyEvent.ACTION_UP)      //0键 KeyCode() == 141, KEYCODE_F11 = 141
            {
                Log.i("TAG","dispatchKeyEvent"+event.getKeyCode());
                predowmTime = event.getDownTime();
                Message message = new Message();
                message.what = 11;
                myHandle.sendMessage(message);
            }
        }

        if (event.getKeyCode() == KeyEvent.KEYCODE_F1 && event.getAction() == KeyEvent.ACTION_UP)      //#号键
        {
            Log.i("TAG","dispatchKeyEvent"+event.getKeyCode());
            if (last_keydown != event.getKeyCode() && last_keydown != 0) {
                isdelay[last_messagecode] = false;
                issend = true;
                isdelay[1] = true;
                predowmTime = event.getDownTime();
                Message message = new Message();
                message.what = 1;
                myHandle.sendMessage(message);
            }
            if (event.getDownTime() - predowmTime <= 400 || predowmTime==0){
                predowmTime = event.getDownTime();
                if(last_keydown == event.getKeyCode() || last_keydown==0)
                    countarray[1]++;
                Log.i("识别", "dispatchKeyEvent: "+(event.getEventTime()-predowmTime)+"计数"
                        +countarray[1]);
            } else if(!issend){
                predowmTime = event.getDownTime();
                Message message = new Message();
                message.what = 1;
                myHandle.sendMessage(message);
                isdelay[1] = true;
            }
            last_keydown = event.getKeyCode();
            last_messagecode = 1;
        }

        if (event.getKeyCode() == 217 && event.getAction() == KeyEvent.ACTION_UP)   //翻页键 KeyCode() == 217, 表示删除 KEYCODE_DEL = 67;
        {
            Log.i("TAG","dispatchKeyEvent"+event.getKeyCode());
            sendKeyCode(67);
        }
        if (event.getKeyCode() == 216 && event.getAction() == KeyEvent.ACTION_UP)       //取消键 KeyCode() == 216, 表示返回
        {
            Log.i("TAG","dispatchKeyEvent"+event.getKeyCode());
            sendKeyCode(62);        //表示空格 KEYCODE_SPACE = 62
            //保存一个结果码
            int resultCode=3;
            //准备一个带额外数据的intent对象
            Intent data=new Intent();
            if(languagee_temp.equals("英文"))
                count_changlanguage = 1;
            if(languagee_temp.equals("中文"))
                count_changlanguage = 2;
            String result=String.valueOf(count_changlanguage);
            data.putExtra("RESULT",result);
            //设置结果
            setResult(resultCode,data);
            finish();
//            ToastUtile.showText(SendMessageActivity.this, "返回主界面");
           // Toast.makeText(SendMessageActivity.this, "返回主界面", Toast.LENGTH_SHORT).show();
        }
        if (event.getKeyCode() == 215&& event.getAction() == KeyEvent.ACTION_UP)       //确定键  KeyCode() == 215, 表示发送短信
        {
//            shortmessage.clearFocus();
        }
        if (event.getKeyCode() == 45&& event.getAction() == KeyEvent.ACTION_UP)       //确定键  KeyCode() == 215, 表示发送短信
        {
            Button f1 = findViewById(R.id.duanxindanfa);
            Button f2 = findViewById(R.id.duanxinqunfa);
            Button f3 = findViewById(R.id.duanxinduofa);
            Button f4 = findViewById(R.id.duanxinshoujianxiang);
            Button f5 = findViewById(R.id.duanxinfajianxiang);
            Button f6 = findViewById(R.id.duanxinf6);
            Button f7 = findViewById(R.id.duanxinf7);
            Button f8 = findViewById(R.id.duanxinf8);
            f6.setVisibility(View.VISIBLE);
            f7.setVisibility(View.VISIBLE);
            f8.setVisibility(View.VISIBLE);
            f1.setText("发送");
            f2.setText("退出");
            f3.setText("");
            f4.setText("中文");
            f5.setText("数字");
            f6.setText("大写");
            f7.setText("小写");
            f8.setText("符号");
            AlertDialog.Builder builder = new AlertDialog.Builder(SendMessageActivity.this);
            View view = LayoutInflater.from(SendMessageActivity.this).inflate(R.layout.dialog_danfaduanxin,null);
            builder.setView(view);
            shoujianren = view.findViewById(R.id.shoujianren);
            xinxineirong = view.findViewById(R.id.duanxinneirong);

            xinxineirong.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if(caplook)
                    {
                        if(suiyiflag) {
                            suiyiflag = false;
                            return;
                        }
                        if(count!=0)
                        {
                                char[] chars= {Character.toUpperCase(s.charAt(start + i))};
                                charSequence = new String(chars);
                                start_temp = start;
                                Message msg = new Message();
                                msg.what = 1;
                                send_handler.sendMessage(msg);
                        }
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
            shoujianren.setText(address.getText());
            xinxineirong.setText(shortmessage.getText());
            Log.i("TAG","dispatchKeyEvent"+event.getKeyCode());
            xinxineirong.is_inside = true;
            shoujianren.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    shoujianren.setSelected(hasFocus);
                    if (count_changlanguage == 1) {
                        languagee_temp = "英文";
                    } else if (count_changlanguage == 2) {
                        languagee_temp = "中文";
                    }
                    languagee.setText("数字");
                    flag_number=true;
                    count_changlanguage = 0;
                }
            });//为EditText添加获得焦点时的事件

            xinxineirong.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    xinxineirong.setSelected(hasFocus);
                    if(count_changlanguage==1){
                        languagee.setText("英文");
                        languagee_temp="英文";
                    }else if(count_changlanguage==2){
                        languagee.setText("中文");
                        languagee_temp="中文";
                    }
                    else {
                        languagee.setText(languagee_temp);
                        if(languagee_temp.equals("英文"))
                            count_changlanguage = 1;
                        else if(languagee_temp.equals("中文"))
                            count_changlanguage = 2;

                        flag_number=false;
                    }
                }
            });//为EditText添加获得焦点时的事件

//            xinxineirong.setOnKeyListener(new View.OnKeyListener() {
//                @Override
//                public boolean onKey(View v, int keyCode, KeyEvent event) {
//                    if(keyCode == 45) {
//
//                    }
//                }
//            });

            builder.setPositiveButton("确认",new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            f6.setVisibility(View.INVISIBLE);
                            f7.setVisibility(View.INVISIBLE);
                            f8.setVisibility(View.INVISIBLE);
                            f1.setText("单发");
                            f2.setText("群发");
                            f3.setText("多发");
                            f4.setText("收件箱");
                            f5.setText("发件箱");
                            if(caplook)
                            {
//                                Message message = new Message();
//                                message.what = 14;
//                                myHandle.sendMessage(message);
                                caplook = false;
                            }
                            xinxineirong.clearFocus();
                            fuhao_flag = false;
                        }
                    }

                    );
            builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    f6.setVisibility(View.INVISIBLE);
                    f7.setVisibility(View.INVISIBLE);
                    f8.setVisibility(View.INVISIBLE);
                    f1.setText("单发");
                    f2.setText("群发");
                    f3.setText("多发");
                    f4.setText("收件箱");
                    f5.setText("发件箱");
                    if(caplook)
                    {
//                        Message message = new Message();
//                        message.what = 14;
//                        myHandle.sendMessage(message);
                        caplook = false;
                    }
                    xinxineirong.clearFocus();
                    fuhao_flag = false;
                }
            });
            builder.setOnKeyListener(new DialogInterface.OnKeyListener() {


                @SuppressLint("SuspiciousIndentation")
                @Override
                public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {

                    if (keyCode == 45 && event.getAction() == KeyEvent.ACTION_UP ){
                        String gbk;
                        String zhongduan= shoujianren.getText().toString();
                        if(zhongduan.length()!=9){
                            ToastUtile.showText(SendMessageActivity.this, "终端号输入错误！信息发送失败！");
                        }else {
                            send_state = 0;
                            gbk = stringToUnicode(xinxineirong.getText().toString());
                            int len=gbk.length()/2+1;
                            String msglen=Integer.toHexString(len).toUpperCase();
                            String Length=Integer.toHexString(len+10).toUpperCase();
                            while (Length.length()<4){
                                Length="0".concat(Length);
                            }
                            while (msglen.length()<4){
                                msglen="0".concat(msglen);
                            }
                            String CheckData="01B3"+Length+"2F"+"0"+zhongduan+msglen+kind+gbk;
                            Log.i("TAG", "CheckData:"+CheckData);
                            String CheckSum=GetCheckSum(CheckData);
                            SendData=GetSendData(CheckData,CheckSum);
                            Log.e("TAG", "SendData:"+SendData);
                            sendData(SendData);
                            address_string = shoujianren.getText().toString();
                            shortmessage_string = xinxineirong.getText().toString();
                        }
                        Button btn_pos = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                        btn_pos.performClick(); //点击确定
                    }
                    if (keyCode == 142 && event.getAction() == KeyEvent.ACTION_UP) {
                        if(fuhao_flag)
                        {
                            int cur_pos = xinxineirong.getSelectionStart();
                            Editable editable = xinxineirong.getText();
                            editable.insert(cur_pos,"””");
                        }
                        else
                            dispatchKeyEvent(event);
                        Log.i("按键输入", "dispatchKeyEvent: 输入1");
                    }
                    if (keyCode == 132 && event.getAction() == KeyEvent.ACTION_UP) {
                        if(fuhao_flag)
                        {
                            int cur_pos = xinxineirong.getSelectionStart();
                            Editable editable = xinxineirong.getText();
                            editable.insert(cur_pos,"()");
                        }
                        else
                        dispatchKeyEvent(event);
                        Log.i("按键输入", "dispatchKeyEvent: 输入2");
                    }
                    if (keyCode == 133 && event.getAction() == KeyEvent.ACTION_UP) {
                        if(fuhao_flag)
                        {
                            int cur_pos = xinxineirong.getSelectionStart();
                            Editable editable = xinxineirong.getText();
                            editable.insert(cur_pos,"-");
                        }
                        else
                        dispatchKeyEvent(event);
                        Log.i("按键输入", "dispatchKeyEvent: 输入3");
                    }
                    if (keyCode == 134 && event.getAction() == KeyEvent.ACTION_UP) {
                        if(fuhao_flag)
                        {
                            int cur_pos = xinxineirong.getSelectionStart();
                            Editable editable = xinxineirong.getText();
                            editable.insert(cur_pos,"/");
                        }
                        else
                        dispatchKeyEvent(event);
                        Log.i("按键输入", "dispatchKeyEvent: 输入=4");
                    }
                    if (keyCode == 135 && event.getAction() == KeyEvent.ACTION_UP) {
                        if(fuhao_flag)
                        {
                            int cur_pos = xinxineirong.getSelectionStart();
                            Editable editable = xinxineirong.getText();
                            editable.insert(cur_pos,";");
                        }
                        else
                        dispatchKeyEvent(event);
                        Log.i("按键输入", "dispatchKeyEvent: 输入5");
                    }
                    if (keyCode == 136 && event.getAction() == KeyEvent.ACTION_UP) {
                        if(fuhao_flag)
                        {
                            sendKeyCode(KeyEvent.KEYCODE_SPACE);
                        }
                        else
                        dispatchKeyEvent(event);
                        Log.i("按键输入", "dispatchKeyEvent: 输入6");
                    }
                    if (keyCode == 137 && event.getAction() == KeyEvent.ACTION_UP) {
                        if(fuhao_flag)
                        {
                            int cur_pos = xinxineirong.getSelectionStart();
                            Editable editable = xinxineirong.getText();
                            editable.insert(cur_pos,"？");
                        }
                        else
                        dispatchKeyEvent(event);
                    }
                    if (keyCode == 138 && event.getAction() == KeyEvent.ACTION_UP) {
                        if(fuhao_flag)
                        {
                            int cur_pos = xinxineirong.getSelectionStart();
                            Editable editable = xinxineirong.getText();
                            editable.insert(cur_pos,"!");
                        }
                        else
                        dispatchKeyEvent(event);
                        Log.i("按键输入", "dispatchKeyEvent: 输入8");
                    }
                    if (keyCode == 139 && event.getAction() == KeyEvent.ACTION_UP) {
                        if(fuhao_flag)
                        {
                            int cur_pos = xinxineirong.getSelectionStart();
                            Editable editable = xinxineirong.getText();
                            editable.insert(cur_pos,"。");
                        }
                        else
                        dispatchKeyEvent(event);
                        Log.i("按键输入", "dispatchKeyEvent: 输入9");
                    }
                    if (keyCode == 141 && event.getAction() == KeyEvent.ACTION_UP) {
                        if(fuhao_flag)
                        {
                            int cur_pos = xinxineirong.getSelectionStart();
                            Editable editable = xinxineirong.getText();
                            editable.insert(cur_pos,"%");
                        }
                        else
                        dispatchKeyEvent(event);
                        Log.i("按键输入", "dispatchKeyEvent: 输入0");
                    }
                    if (keyCode == 140 && event.getAction() == KeyEvent.ACTION_UP) {
                        if(fuhao_flag)
                        {
                            sendKeyCode(77);
                        }
                        else
                            dispatchKeyEvent(event);
                        Log.i("按键输入", "dispatchKeyEvent: 输入0");
                    }
                    if (keyCode == 131 && event.getAction() == KeyEvent.ACTION_UP) {
                        if(fuhao_flag)
                        {
                            int cur_pos = xinxineirong.getSelectionStart();
                            Editable editable = xinxineirong.getText();
                            editable.insert(cur_pos,"~");
                        }
                        else
                            dispatchKeyEvent(event);
                        Log.i("按键输入", "dispatchKeyEvent: 输入0");
                    }
                    if (keyCode == 51 && event.getAction() == KeyEvent.ACTION_UP) {
                        Button btn_neg = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_NEGATIVE);
                        btn_neg.performClick(); //点击取消
                    }
                    if (keyCode == 48 && event.getAction() == KeyEvent.ACTION_UP) {
                        fuhao_flag = false;
                        if(caplook)
                        {
                            count_changlanguage = 2;
                            Message message = new Message();
                            message.what = 13;
                            myHandle.sendMessage(message);
                            caplook = false;
                        }
                        if(count_changlanguage==1) {
                            Message message = new Message();
                            message.what = 13;
                            myHandle.sendMessage(message);
                            count_changlanguage++;
                        }
                        if(count_changlanguage==0) {
                            if(languagee_temp.equals("英文")) {
                                Message message = new Message();
                                message.what = 13;
                                myHandle.sendMessage(message);
                                count_changlanguage = 2;
                            }
                            else if(languagee_temp.equals("中文"))
                            {
                                flag_number=false;
                                count_changlanguage =2;
                            }
                        }
                        languagee.setText("中文");
                    }
                    if (keyCode == 159 && event.getAction() == KeyEvent.ACTION_UP) {
                        fuhao_flag = false;
                        if(caplook)
                        {
//                            Message message = new Message();
//                            message.what = 14;
//                            myHandle.sendMessage(message);
                            caplook = false;
                        }
                        if(count_changlanguage!=0) {
                            if(count_changlanguage==1) {
                                languagee_temp = "英文";
                            }
                            else if(count_changlanguage==2)
                            {
                                languagee_temp = "中文";
                            }
                        }
                        Message message = new Message();
                        message.what = 13;
                        myHandle.sendMessage(message);
                        count_changlanguage = 3;
                    }
                    if (keyCode == 213 && event.getAction() == KeyEvent.ACTION_UP) {
                        fuhao_flag =false;
                        if(!caplook)
                        {
                            if(count_changlanguage==2)
                            {
                                Message message = new Message();
                                message.what = 13;
                                myHandle.sendMessage(message);
                                count_changlanguage = 1;
                            }
                            if(count_changlanguage==0)
                            {
                                if(languagee_temp.equals("中文"))
                                {
                                    Message message = new Message();
                                    message.what = 13;
                                    myHandle.sendMessage(message);
                                    count_changlanguage = 1;
                                }
                            }
                            caplook = true;
                        }
                        flag_number = false;
                        fuhao_flag = false;
                    }
                    if (keyCode == 218 && event.getAction() == KeyEvent.ACTION_UP) {
                        fuhao_flag = false;
                        if(caplook)
                        {
//                            Message message = new Message();
//                            message.what = 14;
//                            myHandle.sendMessage(message);
                            caplook = false;
                        }
                        if(count_changlanguage==0) {
                            if(languagee_temp.equals("中文")) {
                                Message message = new Message();
                                message.what = 13;
                                myHandle.sendMessage(message);
                                count_changlanguage = 1;
                            }
                            else if(languagee_temp.equals("英文"))
                            {
                                flag_number=false;
                                count_changlanguage =1;
                            }
                        }
                        else if(count_changlanguage==2) {
                            Message message = new Message();
                            message.what = 13;
                            myHandle.sendMessage(message);
                            count_changlanguage = 1;
                        }
                        languagee.setText("英文");
                    }
                    if (keyCode == 212 && event.getAction() == KeyEvent.ACTION_UP) {
                        languagee.setText("符号");
                        fuhao_flag = true;
                    }
                    if (keyCode == 216 && event.getAction() == KeyEvent.ACTION_UP) {
                        sendKeyCode(67);
                    }
                    if (keyCode == 215 && event.getAction() == KeyEvent.ACTION_UP) {

                    }
                    Log.i("按键输入", "onKey: " + keyCode);
                    return false;
                }



        });
            xinxineirong.dialog = builder.show();
            NavigationBarUtil.hideNavigationBar(xinxineirong.dialog.getWindow());
        }
        if(event.getKeyCode() == 51&& event.getAction() == KeyEvent.ACTION_UP)
        {
            Button f1 = findViewById(R.id.duanxindanfa);
            Button f2 = findViewById(R.id.duanxinqunfa);
            Button f3 = findViewById(R.id.duanxinduofa);
            Button f4 = findViewById(R.id.duanxinshoujianxiang);
            Button f5 = findViewById(R.id.duanxinfajianxiang);
            Button f6 = findViewById(R.id.duanxinf6);
            Button f7 = findViewById(R.id.duanxinf7);
            Button f8 = findViewById(R.id.duanxinf8);
            f6.setVisibility(View.VISIBLE);
            f7.setVisibility(View.VISIBLE);
            f8.setVisibility(View.VISIBLE);
            f1.setText("发送");
            f2.setText("退出");
            f3.setText("");
            f4.setText("中文");
            f5.setText("数字");
            f6.setText("大写");
            f7.setText("小写");
            f8.setText("符号");
            AlertDialog.Builder builder = new AlertDialog.Builder(SendMessageActivity.this);
            View view = LayoutInflater.from(SendMessageActivity.this).inflate(R.layout.dialog_danfaduanxin,null);
            builder.setView(view);
            TextView textView = view.findViewById(R.id.duanxintitle);
            textView.setText("发送信息：群发");
            TextView textView1 = view.findViewById(R.id.shoujianrentitle);
            textView1.setText("群号：");
            shoujianren = view.findViewById(R.id.shoujianren);
            xinxineirong = view.findViewById(R.id.duanxinneirong);
            shoujianren.setText(address.getText());
            xinxineirong.setText(shortmessage.getText());
            Log.i("TAG","dispatchKeyEvent"+event.getKeyCode());
            xinxineirong.is_inside = true;
            shoujianren.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    shoujianren.setSelected(hasFocus);
                    if (count_changlanguage == 1) {
                        languagee_temp = "英文";
                    } else if (count_changlanguage == 2) {
                        languagee_temp = "中文";
                    }
                    languagee.setText("数字");
                    flag_number=true;
                    count_changlanguage = 0;
                }
            });//为EditText添加获得焦点时的事件

            xinxineirong.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    xinxineirong.setSelected(hasFocus);
                    if(count_changlanguage==1){
                        languagee.setText("英文");
                        languagee_temp="英文";
                    }else if(count_changlanguage==2){
                        languagee.setText("中文");
                        languagee_temp="中文";
                    }
                    else {
                        languagee.setText(languagee_temp);
                        if(languagee_temp.equals("英文"))
                            count_changlanguage = 1;
                        else if(languagee_temp.equals("中文"))
                            count_changlanguage = 2;

                        flag_number=false;
                    }
                }
            });//为EditText添加获得焦点时的事件

//            xinxineirong.setOnKeyListener(new View.OnKeyListener() {
//                @Override
//                public boolean onKey(View v, int keyCode, KeyEvent event) {
//                    if(keyCode == 45) {
//
//                    }
//                }
//            });

            builder.setPositiveButton("确认",new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            f6.setVisibility(View.INVISIBLE);
                            f7.setVisibility(View.INVISIBLE);
                            f8.setVisibility(View.INVISIBLE);
                            f1.setText("单发");
                            f2.setText("群发");
                            f3.setText("多发");
                            f4.setText("收件箱");
                            f5.setText("发件箱");
                            if(caplook)
                            {
//                                Message message = new Message();
//                                message.what = 14;
//                                myHandle.sendMessage(message);
                                caplook = false;
                            }
                            xinxineirong.clearFocus();
                        }
                    }

            );
            builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    f6.setVisibility(View.INVISIBLE);
                    f7.setVisibility(View.INVISIBLE);
                    f8.setVisibility(View.INVISIBLE);
                    f1.setText("单发");
                    f2.setText("群发");
                    f3.setText("多发");
                    f4.setText("收件箱");
                    f5.setText("发件箱");
                    if(caplook)
                    {
//                        Message message = new Message();
//                        message.what = 14;
//                        myHandle.sendMessage(message);
                        caplook = false;
                    }
                    xinxineirong.clearFocus();
                }
            });
            builder.setOnKeyListener(new DialogInterface.OnKeyListener() {


                @Override
                public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {

                    if (keyCode == 45 && event.getAction() == KeyEvent.ACTION_UP ){
                        String gbk;
                        String zhongduan= shoujianren.getText().toString();
                        if(zhongduan.length()!=6){
                            ToastUtile.showText(SendMessageActivity.this, "群号输入错误！信息发送失败！");
                        }else {
                            send_state = 0;

                            gbk = stringToUnicode(xinxineirong.getText().toString());
                            int len=gbk.length()/2+1;
                            String msglen=Integer.toHexString(len).toUpperCase();
                            String Length=Integer.toHexString(len+8).toUpperCase();
                            while (Length.length()<4){
                                Length="0".concat(Length);
                            }
                            while (msglen.length()<4){
                                msglen="0".concat(msglen);
                            }
                            String CheckData="01B3"+Length+"2E"+zhongduan+msglen+kind+gbk;
                            Log.i("TAG", "CheckData:"+CheckData);
                            String CheckSum=GetCheckSum(CheckData);
                            SendData=GetSendData(CheckData,CheckSum);
                            Log.e("TAG", "SendData:"+SendData);
                            sendData(SendData);
                            address_string = shoujianren.getText().toString();
                            shortmessage_string = xinxineirong.getText().toString();
                            AlertDialog.Builder builder = new AlertDialog.Builder(SendMessageActivity.this);
                            builder.setIcon(R.drawable.email);
                            builder.setTitle("正在发送...");
                            View view = LayoutInflater.from(SendMessageActivity.this).inflate(R.layout.dialog_tishi, null);
                            builder.setView(view);

                            builder.setNegativeButton("取消",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                        }
                                    });
                            builder.setOnKeyListener(new DialogInterface.OnKeyListener() {
                                @Override
                                public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                                    if (keyCode == 216 && event.getAction() == KeyEvent.ACTION_UP) {
                                        Button btn_neg = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_NEGATIVE);
                                        btn_neg.performClick(); //点击取消
                                    }
                                    return false;
                                }}
                            );

                            Dialog dialog1 = builder.create();
                            dialog1.show();
                            dialogtemp = dialog1;
                            WindowManager.LayoutParams params22 = dialog1.getWindow().getAttributes();
                            params22.width = 400;
                            params22.height = 200;
                            dialog1.getWindow().setAttributes(params22);
                            NavigationBarUtil.hideNavigationBar(dialog1.getWindow());
                            data="";
                        }
                        Button btn_pos = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                        btn_pos.performClick(); //点击确定
                    }
                    if (keyCode == 142 && event.getAction() == KeyEvent.ACTION_UP) {
                        if(fuhao_flag)
                        {
                            int cur_pos = xinxineirong.getSelectionStart();
                            Editable editable = xinxineirong.getText();
                            editable.insert(cur_pos,"””");
                        }
                        else
                            dispatchKeyEvent(event);
                        Log.i("按键输入", "dispatchKeyEvent: 输入1");
                    }
                    if (keyCode == 132 && event.getAction() == KeyEvent.ACTION_UP) {
                        if(fuhao_flag)
                        {
                            int cur_pos = xinxineirong.getSelectionStart();
                            Editable editable = xinxineirong.getText();
                            editable.insert(cur_pos,"()");
                        }
                        else
                            dispatchKeyEvent(event);
                        Log.i("按键输入", "dispatchKeyEvent: 输入2");
                    }
                    if (keyCode == 133 && event.getAction() == KeyEvent.ACTION_UP) {
                        if(fuhao_flag)
                        {
                            int cur_pos = xinxineirong.getSelectionStart();
                            Editable editable = xinxineirong.getText();
                            editable.insert(cur_pos,"-");
                        }
                        else
                            dispatchKeyEvent(event);
                        Log.i("按键输入", "dispatchKeyEvent: 输入3");
                    }
                    if (keyCode == 134 && event.getAction() == KeyEvent.ACTION_UP) {
                        if(fuhao_flag)
                        {
                            int cur_pos = xinxineirong.getSelectionStart();
                            Editable editable = xinxineirong.getText();
                            editable.insert(cur_pos,"/");
                        }
                        else
                            dispatchKeyEvent(event);
                        Log.i("按键输入", "dispatchKeyEvent: 输入=4");
                    }
                    if (keyCode == 135 && event.getAction() == KeyEvent.ACTION_UP) {
                        if(fuhao_flag)
                        {
                            int cur_pos = xinxineirong.getSelectionStart();
                            Editable editable = xinxineirong.getText();
                            editable.insert(cur_pos,";");
                        }
                        else
                            dispatchKeyEvent(event);
                        Log.i("按键输入", "dispatchKeyEvent: 输入5");
                    }
                    if (keyCode == 136 && event.getAction() == KeyEvent.ACTION_UP) {
                        if(fuhao_flag)
                        {
                            sendKeyCode(KeyEvent.KEYCODE_SPACE);
                        }
                        else
                            dispatchKeyEvent(event);
                        Log.i("按键输入", "dispatchKeyEvent: 输入6");
                    }
                    if (keyCode == 137 && event.getAction() == KeyEvent.ACTION_UP) {
                        if(fuhao_flag)
                        {
                            int cur_pos = xinxineirong.getSelectionStart();
                            Editable editable = xinxineirong.getText();
                            editable.insert(cur_pos,"？");
                        }
                        else
                            dispatchKeyEvent(event);
                    }
                    if (keyCode == 138 && event.getAction() == KeyEvent.ACTION_UP) {
                        if(fuhao_flag)
                        {
                            int cur_pos = xinxineirong.getSelectionStart();
                            Editable editable = xinxineirong.getText();
                            editable.insert(cur_pos,"!");
                        }
                        else
                            dispatchKeyEvent(event);
                        Log.i("按键输入", "dispatchKeyEvent: 输入8");
                    }
                    if (keyCode == 139 && event.getAction() == KeyEvent.ACTION_UP) {
                        if(fuhao_flag)
                        {
                            int cur_pos = xinxineirong.getSelectionStart();
                            Editable editable = xinxineirong.getText();
                            editable.insert(cur_pos,"。");
                        }
                        else
                            dispatchKeyEvent(event);
                        Log.i("按键输入", "dispatchKeyEvent: 输入9");
                    }
                    if (keyCode == 141 && event.getAction() == KeyEvent.ACTION_UP) {
                        if(fuhao_flag)
                        {
                            int cur_pos = xinxineirong.getSelectionStart();
                            Editable editable = xinxineirong.getText();
                            editable.insert(cur_pos,"%");
                        }
                        else
                            dispatchKeyEvent(event);
                        Log.i("按键输入", "dispatchKeyEvent: 输入0");
                    }
                    if (keyCode == 140 && event.getAction() == KeyEvent.ACTION_UP) {
                        if(fuhao_flag)
                        {
                            sendKeyCode(77);
                        }
                        else
                            dispatchKeyEvent(event);
                        Log.i("按键输入", "dispatchKeyEvent: 输入0");
                    }
                    if (keyCode == 131 && event.getAction() == KeyEvent.ACTION_UP) {
                        if(fuhao_flag)
                        {
                            int cur_pos = xinxineirong.getSelectionStart();
                            Editable editable = xinxineirong.getText();
                            editable.insert(cur_pos,"~");
                        }
                        else
                            dispatchKeyEvent(event);
                        Log.i("按键输入", "dispatchKeyEvent: 输入0");
                    }
                    if (keyCode == 51 && event.getAction() == KeyEvent.ACTION_UP) {
                        Button btn_neg = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_NEGATIVE);
                        btn_neg.performClick(); //点击取消
                    }
                    if (keyCode == 48 && event.getAction() == KeyEvent.ACTION_UP) {
                        fuhao_flag = false;
                        if(caplook)
                        {
//                            Message message = new Message();
//                            message.what = 14;
//                            myHandle.sendMessage(message);
                            caplook = false;
                        }
                        if(count_changlanguage==1) {
                            Message message = new Message();
                            message.what = 13;
                            myHandle.sendMessage(message);
                            count_changlanguage++;
                        }
                        if(count_changlanguage==0) {
                            if(languagee_temp.equals("英文")) {
                                Message message = new Message();
                                message.what = 13;
                                myHandle.sendMessage(message);
                                count_changlanguage = 2;
                            }
                            else if(languagee_temp.equals("中文"))
                            {
                                flag_number=false;
                                count_changlanguage =2;
                            }
                        }
                        languagee.setText("中文");
                    }
                    if (keyCode == 159 && event.getAction() == KeyEvent.ACTION_UP) {
                        fuhao_flag = false;
                        if(caplook)
                        {
//                            Message message = new Message();
//                            message.what = 14;
//                            myHandle.sendMessage(message);
                            caplook = false;
                        }
                        if(count_changlanguage!=0) {
                            if(count_changlanguage==1) {
                                languagee_temp = "英文";
                            }
                            else if(count_changlanguage==2)
                            {
                                languagee_temp = "中文";
                            }
                        }
                        Message message = new Message();
                        message.what = 13;
                        myHandle.sendMessage(message);
                        count_changlanguage = 3;
                    }
                    if (keyCode == 213 && event.getAction() == KeyEvent.ACTION_UP) {
                        fuhao_flag =false;
                        flag_number = false;
                        if(!caplook)
                        {
//                            Message message = new Message();
//                            message.what = 14;
//                            myHandle.sendMessage(message);
                            caplook = true;
                        }
                    }
                    if (keyCode == 218 && event.getAction() == KeyEvent.ACTION_UP) {
                        fuhao_flag = false;
                        if(caplook)
                        {
//                            Message message = new Message();
//                            message.what = 14;
//                            myHandle.sendMessage(message);
                            caplook = false;
                        }
                        if(count_changlanguage==0) {
                            if(languagee_temp.equals("中文")) {
                                Message message = new Message();
                                message.what = 13;
                                myHandle.sendMessage(message);
                                count_changlanguage = 1;
                            }
                            else if(languagee_temp.equals("英文"))
                            {
                                flag_number=false;
                                count_changlanguage =1;
                            }
                        }
                        else if(count_changlanguage==2) {
                            Message message = new Message();
                            message.what = 13;
                            myHandle.sendMessage(message);
                            count_changlanguage = 1;
                        }
                        languagee.setText("英文");
                    }
                    if (keyCode == 212 && event.getAction() == KeyEvent.ACTION_UP) {
                        languagee.setText("符号");
                        fuhao_flag = true;
                    }
                    if (keyCode == 216 && event.getAction() == KeyEvent.ACTION_UP) {
                        sendKeyCode(67);
                    }
                    if (keyCode == 215 && event.getAction() == KeyEvent.ACTION_UP) {

                    }
                    Log.i("按键输入", "onKey: " + keyCode);
                    return false;
                }



            });
            xinxineirong.dialog = builder.show();
            NavigationBarUtil.hideNavigationBar(xinxineirong.dialog.getWindow());
        }
        if(event.getKeyCode() == 48&& event.getAction() == KeyEvent.ACTION_UP)
        {
            Intent intent46 = new Intent(SendMessageActivity.this, MsgBox.class);
            intent46.putExtra("count_changlanguage",String.valueOf(count_changlanguage));
            intent46.putExtra("select_tablelayout",0);
            startActivityForResult(intent46, 9);
        }
        if(event.getKeyCode() == 159&& event.getAction() == KeyEvent.ACTION_UP)
        {
            Intent intent46 = new Intent(SendMessageActivity.this, MsgBox.class);
            intent46.putExtra("count_changlanguage",String.valueOf(count_changlanguage));
            intent46.putExtra("select_tablelayout",1);
            startActivityForResult(intent46, 9);
        }
        return super.dispatchKeyEvent(event);
    }


    /**
     * 计算两次按键事件的时间差
     *
     * @param
     * @return boolean
     */

    private class MyHandle extends Handler {
        private final WeakReference<SendMessageActivity> mActivity;
        public MyHandle(SendMessageActivity activity){
            mActivity = new WeakReference<>(activity);
        }
        @Override
        public void handleMessage(Message msg) {
            SendMessageActivity activity = mActivity.get();
            if (activity != null){
                switch (msg.what){
                    case 1:
                        new Thread(){
                            @Override
                            public void run() {
                                if (!flag_number){
                                    try {
                                        Thread.sleep(400);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                    switch (count_downtime){
                                        case 0:sendKeyCode(62);
                                            Log.i("转译按键键值", "KeyCode:"+62);
                                            break;
                                        case 1:sendKeyCode(55);
                                            Log.i("转译按键键值", "KeyCode:"+55);
                                            break;
                                        case 2:sendKeyCode(56);
                                            Log.i("转译按键键值", "KeyCode:"+56);
//                                        case 3:execByRuntime("input keyevent 72");
//                                            Log.i("转译按键键值", "KeyCode:"+72);
                                            break;
                                    }
                                    count_downtime=0;

                                }else {
                                    sendKeyCode(8);
                                    Log.i("转译按键键值", "KeyCode:"+8);
                                }
                            }
                        }.start();
                        break;
                    case 2:
                        new Thread(){
                            @Override
                            public void run() {
                                issend =false;
                                if(!flag_number){
                                    try {
                                        for (int i=0; i<60 &&isdelay[2];i++)Thread.sleep(10);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                    switch (countarray[2]) {
                                        case 0:
                                            sendKeyCode(29);
                                            Log.i("转译按键键值", "KeyCode:" + 29);
                                            break;
                                        case 1:
                                            sendKeyCode(30);
                                            Log.i("转译按键键值", "KeyCode:" + 30);
                                            break;
                                        case 2:
                                            sendKeyCode(31);
                                            Log.i("转译按键键值", "KeyCode:" + 31);
                                            break;
//                                        case 3:
//                                            sendKeyCode(77);
//                                            Log.i("转译按键键值", "KeyCode:" + 77);
//                                            break;
                                        default:   sendKeyCode(31);

                                            break;
                                    }
                                    countarray[2]=0;
                                }else {
                                    sendKeyCode(9);
                                    Log.i("转译按键键值", "KeyCode:"+9);
                                }
                            }
                        }.start();
                        break;

                    case 3:
                        new Thread(){
                            @Override
                            public void run() {
                                issend = false;
                                if (!flag_number){
                                    try {
                                        for (int i=0; i<60 &&isdelay[3];i++)Thread.sleep(10);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                    switch (countarray[3]){
                                        case 0:sendKeyCode(32);
                                            Log.i("转译按键键值", "KeyCode:"+32);
                                            break;
                                        case 1:sendKeyCode(33);
                                            Log.i("转译按键键值", "KeyCode:"+33);
                                            break;
                                        case 2:sendKeyCode(34);
                                            Log.i("转译按键键值", "KeyCode:"+34);
                                            break;
                                        default:sendKeyCode(34);
                                    }
                                    countarray[3]=0;
                                } else {
                                    sendKeyCode(10);
                                    Log.i("转译按键键值", "KeyCode:"+10);
                                }
                            }
                        }.start();
                        break;
                    case 4:
                        new Thread(){
                            @Override
                            public void run() {
                                issend = false;
                                if (!flag_number){
                                    try {
                                        for (int i=0; i<60 &&isdelay[4];i++)Thread.sleep(10);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                    switch (countarray[4]){
                                        case 0:sendKeyCode(35);
                                            Log.i("转译按键键值", "KeyCode:"+32);
                                            break;
                                        case 1:sendKeyCode(36);
                                            Log.i("转译按键键值", "KeyCode:"+33);
                                            break;
                                        case 2:sendKeyCode(37);
                                            Log.i("转译按键键值", "KeyCode:"+34);
                                            break;
                                        default:sendKeyCode(37);
                                    }
                                    countarray[4]=0;
                                } else {
                                    sendKeyCode(11);
                                    Log.i("转译按键键值", "KeyCode:"+10);
                                }
                            }
                        }.start();
                        break;
                    case 5:
                        new Thread(){
                            @Override
                            public void run() {
                                issend = false;
                                if (!flag_number){
                                    try {
                                        for (int i=0; i<60 &&isdelay[5];i++)Thread.sleep(10);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                    switch (countarray[5]){
                                        case 0:sendKeyCode(38);
                                            Log.i("转译按键键值", "KeyCode:"+32);
                                            break;
                                        case 1:sendKeyCode(39);
                                            Log.i("转译按键键值", "KeyCode:"+33);
                                            break;
                                        case 2:sendKeyCode(40);
                                            Log.i("转译按键键值", "KeyCode:"+34);
                                            break;
                                        default:sendKeyCode(40);
                                    }
                                    countarray[5]=0;
                                } else {
                                    sendKeyCode(12);
                                    Log.i("转译按键键值", "KeyCode:"+12);
                                }
                            }
                        }.start();
                        break;
                    case 6:
                        new Thread(){
                            @Override
                            public void run() {
                                issend = false;
                                if (!flag_number){
                                    try {
                                        for (int i=0; i<60 &&isdelay[6];i++)Thread.sleep(10);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                    switch (countarray[6]){
                                        case 0:sendKeyCode(41);
                                            Log.i("转译按键键值", "KeyCode:"+32);
                                            break;
                                        case 1:sendKeyCode(42);
                                            Log.i("转译按键键值", "KeyCode:"+33);
                                            break;
                                        case 2:sendKeyCode(43);
                                            Log.i("转译按键键值", "KeyCode:"+34);
                                            break;
                                        default:sendKeyCode(43);
                                    }
                                    countarray[6]=0;
                                } else {
                                    sendKeyCode(13);
                                    Log.i("转译按键键值", "KeyCode:"+12);
                                }
                            }
                        }.start();
                        break;
                    case 7:
                        new Thread(){
                            @Override
                            public void run() {
                                issend = false;
                                if (!flag_number){
                                    try {
                                        for (int i=0; i<70 &&isdelay[7];i++)Thread.sleep(10);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                    switch (countarray[7]){
                                        case 0:sendKeyCode(44);
                                            Log.i("转译按键键值", "KeyCode:"+32);
                                            break;
                                        case 1:sendKeyCode(45);
                                            CustomEditText.no_ignore = false;
                                            Log.i("转译按键键值", "KeyCode:"+33);
                                            break;
                                        case 2:sendKeyCode(46);
                                            CustomEditText.no_ignore = false;
                                            Log.i("转译按键键值", "KeyCode:"+34);
                                            break;
                                        case 3:sendKeyCode(47);
                                            Log.i("转译按键键值", "KeyCode:"+34);
                                            break;
                                        default:sendKeyCode(47);
                                    }
                                    countarray[7]=0;
                                } else {
                                    sendKeyCode(14);
                                    Log.i("转译按键键值", "KeyCode:"+12);
                                }
                            }
                        }.start();
                        break;
                    case 8:
                        new Thread(){
                            @Override
                            public void run() {
                                issend = false;
                                if (!flag_number){
                                    try {
                                        for (int i=0; i<60 &&isdelay[8];i++)Thread.sleep(10);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                    switch (countarray[8]){
                                        case 0:sendKeyCode(48);
                                            CustomEditText.no_ignore = false;
                                            Log.i("转译按键键值", "KeyCode:"+32);
                                            break;
                                        case 1:sendKeyCode(49);
                                            Log.i("转译按键键值", "KeyCode:"+33);
                                            break;
                                        case 2:sendKeyCode(50);
                                            Log.i("转译按键键值", "KeyCode:"+34);
                                            break;
                                        default:sendKeyCode(50);
                                    }
                                    countarray[8]=0;
                                } else {
                                    sendKeyCode(15);
                                    Log.i("转译按键键值", "KeyCode:"+15);
                                }
                            }
                        }.start();
                        break;
                    case 9:
                        new Thread(){
                            @Override
                            public void run() {
                                issend = false;
                                if (!flag_number){
                                    try {
                                        for (int i=0; i<70 &&isdelay[9];i++)Thread.sleep(10);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                    switch (countarray[9]){
                                        case 0:sendKeyCode(51);
                                            CustomEditText.no_ignore = false;
                                            Log.i("转译按键键值", "KeyCode:"+32);
                                            break;
                                        case 1:sendKeyCode(52);
                                            Log.i("转译按键键值", "KeyCode:"+33);
                                            break;
                                        case 2:sendKeyCode(53);
                                            Log.i("转译按键键值", "KeyCode:"+34);
                                            break;
                                        case 3:sendKeyCode(54);
                                            Log.i("转译按键键值", "KeyCode:"+34);
                                            break;
                                        default:sendKeyCode(54);
                                    }
                                    countarray[9]=0;
                                } else {
                                    sendKeyCode(16);
                                    Log.i("转译按键键值", "KeyCode:"+12);
                                }
                            }
                        }.start();
                        break;
                    case 10:
                        new Thread(){
                            @Override
                            public void run() {
                                try {
                                    Thread.sleep(400);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                if(count_downtime>4)
                                    count_downtime=count_downtime-4;
                                switch (count_downtime){
                                    case 0:sendKeyCode(17);
                                        Log.i("转译按键键值", "KeyCode:"+17);
                                        break;
                                    case 1:sendKeyCode(76);
                                        Log.i("转译按键键值", "KeyCode:"+76);
                                        break;
                                    case 2:sendKeyCode(17);
                                        Log.i("转译按键键值", "KeyCode:"+17);
                                    case 3:sendKeyCode(76);
                                        Log.i("转译按键键值", "KeyCode:"+76);
                                        break;
                                }
                                count_downtime=0;

                            }
                        }.start();
                        break;
                    case 11:
                        new Thread(){
                            @Override
                            public void run() {
                                if (flag_number){
                                    sendKeyCode(7);
                                    Log.i("转译按键键值", "KeyCode:"+7);
                                }
                            }
                        }.start();
                        break;
                    case 12:
                        new Thread(){
                            @Override
                            public void run() {
                                if (flag_number){
                                    sendKeyCode(8);

                                }
                            }
                        }.start();
                        break;
                    case 13:
                        switch (count_changlanguage){
                            case 1:
                                new Thread(){
                                    @Override
                                    public void run() {
                                        sendKeyCode(59);
                                        Log.i("转译按键键值", "KeyCode:"+59);
                                    }
                                }.start();
                                flag_number=false;
                                languagee.setText("英文");
                                break;
                            case 2:
                                new Thread(){
                                    @Override
                                    public void run() {
                                        sendKeyCode(59);
                                        Log.i("转译按键键值", "KeyCode:"+59);
                                    }
                                }.start();
                                flag_number=false;
                                languagee.setText("中文");
                                break;
                            case 3:
                                flag_number=true;
                                count_changlanguage =0;
                                languagee.setText("数字");
                        }
                        break;
                    case 14:
                        new Thread(){
                            @Override
                            public void run() {
                                sendKeyCode(KeyEvent.KEYCODE_CAPS_LOCK);
                            }
                        }.start();
                        break;
                }
            }
        }
    }

    private String stringToUnicode(String string) {
        StringBuffer unicode = new StringBuffer();
        for (int i = 0; i < string.length(); i++) {
            char c = string.charAt(i);
            String temp=Integer.toHexString(c);
            while (temp.length()<4) {
                temp="0".concat(temp);
            }
            unicode.append(temp);
        }
        String str = unicode.toString().toUpperCase();
        return str;
    }
    private String languagee_temp;
    private String kind;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_message);
        Window globle_window = getWindow();
        WindowManager.LayoutParams params = globle_window.getAttributes();
        params.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION|View.SYSTEM_UI_FLAG_IMMERSIVE;
        globle_window.setAttributes(params);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        languagee=findViewById(R.id.show_language);
        address = findViewById(R.id.number_address);
        shortmessage = findViewById(R.id.message_short);
        danfa = findViewById(R.id.duanxindanfa);
        qunfa = findViewById(R.id.duanxinqunfa);
        duofa = findViewById(R.id.duanxinduofa);
        shoujianxiang = findViewById(R.id.duanxinshoujianxiang);
        fajianxiang = findViewById(R.id.duanxinfajianxiang);
        dao=new MsgRevDao(this);
        xinxineirong = new CustomEditText(this);
        shoujianren = new EditText(this);


        Intent intent = getIntent();
        count_changlanguage= Integer.valueOf(intent.getStringExtra("count_changlanguage"));
        if(count_changlanguage==1){
            languagee_temp="英文";
        }else if(count_changlanguage==2){
            languagee_temp="中文";
        }
        kind=intent.getStringExtra("kind");
        lianxiren = intent.getBooleanExtra("lxr_enter",false);
        if(lianxiren)
        {
            lianxirenzhongduan = intent.getStringExtra("zhongduanhao");
            address.setText(lianxirenzhongduan);
            sendKeyCode(45);
        }
        languagee.setText("数字");
        address.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                address.setSelected(hasFocus);
                if (count_changlanguage == 1) {
                    languagee_temp = "英文";
                } else if (count_changlanguage == 2) {
                    languagee_temp = "中文";
                }
                languagee.setText("数字");
                flag_number=true;
                count_changlanguage = 0;
            }
        });//为EditText添加获得焦点时的事件

        shortmessage.activity = SendMessageActivity.this;

        shortmessage.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                shortmessage.setSelected(hasFocus);
                if(count_changlanguage==1){
                    languagee.setText("英文");
                    languagee_temp="英文";
                }else if(count_changlanguage==2){
                    languagee.setText("中文");
                    languagee_temp="中文";
                }
                else {
                    languagee.setText(languagee_temp);
                    if(languagee_temp.equals("英文"))
                        count_changlanguage = 1;
                    else if(languagee_temp.equals("中文"))
                        count_changlanguage = 2;

                    flag_number=false;
                }
            }
        });//为EditText添加获得焦点时的事件


        if(intent.getStringExtra("ZHONGDUANHAO")!=null){
            address.setText(intent.getStringExtra("ZHONGDUANHAO"));
        }

        for(int i=0;i<countarray.length;i++)
            countarray[i] = 0;
        for(int i=0;i<isdelay.length;i++)
            isdelay[i] = true;
        initDevice();


    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);

    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }  //无需修改，关闭事件总线

    private StringBuilder messageSum=new StringBuilder();
    private Handler mHandler;
    private StringBuilder temp = new StringBuilder(); //临时缓存的字符串
    private String data = "";  //接收的指令
    private boolean data_use = false;
    private boolean send_success=false;
    private int count=0;
    private final Handler send_handler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case WHAT_SEND_ERROR:
                    if(count<3){
                        count++;
                        ToastUtile.showText(SendMessageActivity.this, "消息正在重新发送中。。。");
                        sendData(SendData);
                        send_handler.sendEmptyMessageDelayed(WHAT_SEND_ERROR,3000);
                    }else {
                        send_handler.removeMessages(WHAT_SEND_ERROR);
                        count=0;
                    }
                    break;
                case 1:
                    editable = xinxineirong.getEditableText();
                    suiyiflag = true;
                    editable.replace(start_temp ,start_temp+1,charSequence);
                    break;
                default:
                    break;
            }
        }
    };

    private Dialog dialogtemp;
    @Subscribe(threadMode = ThreadMode.MAIN) //675 440  740 450
    public void onMessageEvent(IMessage message) {
        Map<String, Object> map = Serial_Manage(message.getMessage(), temp, data, data_use);
        data = (String) map.get("data");
        data_use = (boolean) map.get("data_use");
        if(data_use){
            if(send_state==0)//单发短信
            {
            String cmd = data.substring(16, 18);
            switch (cmd) {
                case "35":
                    //Toast.makeText(this, "短信发送中。。。", Toast.LENGTH_LONG).show();
                    AlertDialog.Builder builder = new AlertDialog.Builder(SendMessageActivity.this);
                    builder.setIcon(R.drawable.email);
                    builder.setTitle("呼叫成功");
                    View view = LayoutInflater.from(SendMessageActivity.this).inflate(R.layout.dialog_tishi, null);
                    builder.setView(view);

                    builder.setNegativeButton("取消",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });
                    builder.setOnKeyListener(new DialogInterface.OnKeyListener() {
                        @Override
                        public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                            if (keyCode == 216 && event.getAction() == KeyEvent.ACTION_UP) {
                                Button btn_neg = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_NEGATIVE);
                                btn_neg.performClick(); //点击取消
                            }
                            return false;
                        }}
                    );

                    Dialog dialog1 = builder.create();
                    dialog1.show();
                    dialogtemp = dialog1;
                    WindowManager.LayoutParams params22 = dialog1.getWindow().getAttributes();
                    params22.width = 400;
                    params22.height = 200;
                    dialog1.getWindow().setAttributes(params22);
                    data="";
                    break;
                case "29":
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(SendMessageActivity.this);
                    builder1.setIcon(R.drawable.email);
                    builder1.setTitle("呼叫失败");
                    View view1 = LayoutInflater.from(SendMessageActivity.this).inflate(R.layout.dialog_tishi, null);
                    TextView tv = view1.findViewById(R.id.hujiaochenggong);
                    tv.setText("网络忙，请稍后重试");
                    builder1.setView(view1);

                    builder1.setPositiveButton("确定",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if(dialogtemp!=null)
                                    {
                                        dialogtemp.dismiss();
                                    }
                                }
                            });
                    builder1.setOnKeyListener(new DialogInterface.OnKeyListener() {
                        @Override
                        public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                            if (keyCode == 215 && event.getAction() == KeyEvent.ACTION_UP) {
                                Button btn_pos = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                                btn_pos.performClick(); //点击确定
                            }
                            return false;
                        }}
                    );
                    Dialog dialog2 = builder1.create();
                    dialog2.show();
                    WindowManager.LayoutParams params23 = dialog2.getWindow().getAttributes();
                    params23.width = 600;
                    params23.height = 200;
                    dialog2.getWindow().setAttributes(params23);

                    data="";
                    //MsgInfo msginfopre29=new MsgInfo(-1,address.getText().toString(),shortmessage.getText().toString()," "," "," "," ",3);
                    //dao.add(msginfopre29);
                    data="";
                    break;
                case "2B":
                    AlertDialog.Builder builder2 = new AlertDialog.Builder(SendMessageActivity.this);
                    builder2.setIcon(R.drawable.email);
                    builder2.setTitle("呼叫失败");
                    View view2 = LayoutInflater.from(SendMessageActivity.this).inflate(R.layout.dialog_tishi, null);
                    TextView tv1 = view2.findViewById(R.id.hujiaochenggong);
                    tv1.setText("发送失败！");
                    builder2.setView(view2);

                    builder2.setPositiveButton("确定",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if(dialogtemp!=null)
                                    {
                                        dialogtemp.dismiss();
                                    }
                                }
                            });

                    builder2.setOnKeyListener(new DialogInterface.OnKeyListener() {
                      @Override
                      public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                          if (keyCode == 215 && event.getAction() == KeyEvent.ACTION_UP) {
                              Button btn_pos = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                              btn_pos.performClick(); //点击确定
                          }
                          return false;
                      }}
                    );


                    Dialog dialog3 = builder2.create();
                    dialog3.show();
                    WindowManager.LayoutParams params24 = dialog3.getWindow().getAttributes();
                    params24.width = 400;
                    params24.height = 200;
                    dialog3.getWindow().setAttributes(params24);
                    break;
                case "2C":
                    send_success=true;
                    send_handler.removeMessages(10);
                    MsgInfo msgInfo=new MsgInfo(-1,address.getText().toString(),shortmessage.getText().toString()," "," "," "," ",2,0);
                    dao.add(msgInfo);
                    ToastUtile.showText(this, "短信发送成功！");
                    //保存一个结果码
                    int resultCode=3;
                    //准备一个带额外数据的intent对象
                    Intent intentback=new Intent();
                    if(languagee_temp.equals("英文"))
                        count_changlanguage = 1;
                    if(languagee_temp.equals("中文"))
                        count_changlanguage = 2;
                    String result=String.valueOf(count_changlanguage);
                    intentback.putExtra("RESULT",result);
                    //设置结果
                    setResult(resultCode,intentback);
                    data="";
                    data_use=false;
                    finish();
                    break;
                default:
                    break;
            }
        }
            if(send_state==1)//群发短信
            {
                String cmd = data.substring(16, 18);
                switch (cmd) {
                    case "35":

                        break;
                    case "29":
                        if(dialogtemp!=null)
                        {
                            dialogtemp.dismiss();
                        }
                        ToastUtile.showText(this, "网络忙,请稍后重试。。。");
                        //MsgInfo msginfopre29=new MsgInfo(-1,address.getText().toString(),shortmessage.getText().toString()," "," "," "," ",3);
                        //dao.add(msginfopre29);
                        data="";
                        break;
                    case "2B":
                        ToastUtile.showText(this, "短信群发失败！");
                        if(dialogtemp!=null)
                        {
                            dialogtemp.dismiss();
                        }
                        MsgInfo msginfopre=new MsgInfo(-1,address_string,shortmessage_string," "," "," "," ",3,0);
                        dao.add(msginfopre);
                        data="";
                        break;
                    case "2C":

                        send_success=true;
                        send_handler.removeMessages(10);
                        MsgInfo msgInfo=new MsgInfo(-1,address_string,shortmessage_string," "," "," "," ",2,0);
                        dao.add(msgInfo);
                        ToastUtile.showText(this, "短信群发成功！");
                        //保存一个结果码
                        int resultCode=3;
                        //准备一个带额外数据的intent对象
                        Intent intentback=new Intent();
                        String result=String.valueOf(count_changlanguage);
                        intentback.putExtra("RESULT",result);
                        //设置结果
                        setResult(resultCode,intentback);
                        data="";
                        data_use=false;
                        finish();
                        break;
                    default:
                        break;
                }
            }
        }
    }
    private void sendagain(boolean send_success,String data){
        int count = 0;
        while (count < 10){
            if (!send_success){
                sendData(data);
            }
            count++;
            SystemClock.sleep(3000);
        }
    }
    private byte toByte(char c) {
        byte b = (byte) "0123456789ABCDEF".indexOf(c);
        return b;
    }
    private byte[] hexStringToByte(String hex) {
        int len = (hex.length() / 2);
        byte[] result = new byte[len];
        char[] achar = hex.toCharArray();
        for (int i = 0; i < len; i++) {
            int pos = i * 2;
            result[i] = (byte) (toByte(achar[pos]) << 4 | toByte(achar[pos + 1]));
        }
        return result;
    }
    private String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder();
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            //stringBuilder.append(i + ":");//序号 2个数字为1组
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }
    private String ToNot(String javaStr) {
        byte[] bytes = hexStringToByte(javaStr);
        byte temp;
        for (int i = 0; i < bytes.length; i++) {
            temp = bytes[i];
            bytes[i] = (byte) (~temp);
        }
        String bths = bytesToHexString(bytes);
        String b = bths.toUpperCase();
        return b;
    }
    private String Answer(String text, boolean IsSuccess) {
        StringBuilder sb = new StringBuilder();
        sb.append("0F1F3FEF");
        String cmd = text.substring(16, 18);
        sb.append(cmd);
        sb.append(ToNot(cmd));
        if (IsSuccess) {
            sb.append("01");
        } else {
            sb.append("00");
        }
        sb.append("FCFEF0F8");
        return sb.toString();
    }
    private Map<String, Object> Serial_Manage(String Serial_data, StringBuilder temp, String data, Boolean data_use) {
        Log.e("串口接收", "当前值GetMsg: " + Serial_data);
        temp.append(Serial_data);
        Log.e("串口接收", "缓冲区temp: " + temp);
        Map<String, Object> map = new HashMap<String, Object>();
        int start, end, i;
        String length, ComputeCheckSum, GetCheckSum, mcmd;
        if (temp.indexOf("FCFEF0F8") != -1) {  //判断缓冲区的帧尾
            if (temp.indexOf("FEFCF8F001B3") != -1) {  //判断缓冲区的帧头
                if (temp.indexOf("FEFCF8F001B3") > temp.indexOf("FCFEF0F8")) {  //如果帧头在帧尾后边
                    temp.delete(0, temp.indexOf("FEFCF8F001B3"));
                } else {
                    start = temp.indexOf("FEFCF8F001B3");  //帧头位置
                    end = temp.indexOf("FCFEF0F8");          //帧尾位置
                    length = temp.substring(start + 12, start + 16); //获取字符串长度
                    i = Integer.parseInt(length, 16);
                    ComputeCheckSum = GetCheckSum(temp.substring(start, end + 8).substring(8, 2 * i + 12)); //计算校验和
                    GetCheckSum = temp.substring(start, end + 8).substring(2 * i + 12, 2 * i + 16);        //获得传来的校验和
                    mcmd = temp.substring(start, end + 8).substring(16, 18);      //获取指令
                    if (ComputeCheckSum.equals(GetCheckSum)) {   //如果校验成功
                        data_use = true;
                        data = temp.substring(start, end + 8);
                        Log.e("串口接收", "--校验成功--" + data);
                    } else {
                        data_use = false;
                        data = "";
                        Log.e("串口接收", "--校验失败--" + data);
                    }
                    temp.delete(0, end + 8);  //无论校验成功与否，都要清空缓冲区的数据
                    if (mcmd.equals("26") || mcmd.equals("16")||mcmd.equals("17")||mcmd.equals("14")) {   //根据指令 决定是否 发送应答帧
                        sendData(Answer(data, data_use));  //校验完，发送应答帧(PPT按键除外)
                    }
                }
            } else {
                temp.setLength(0); //如果得到了帧尾没有得到帧头,则清空缓冲区
            }
        }
        map.put("data", data);
        map.put("data_use", data_use);
        return map;
    }
    private String GetSendData(String CheckData, String CheckSum) {
        StringBuilder Builder = new StringBuilder("FEFCF8F0");  //Header[4] - 8位
        Builder.append(CheckData);
        Builder.append(CheckSum);
        Builder.append("FCFEF0F8");
        return Builder.toString();
    }  //得到发送指令
    private String GetCheckSum(String CheckData) {
        StringBuilder Builder = new StringBuilder();
        int sum = 0;
        String temp = "";
        int tdata = 0;
        for (int i = 0; i < CheckData.length() / 2; i++) {
            temp = CheckData.substring(i * 2, i * 2 + 2);
            if(temp.equals("\\u")){  //  \\u记为  FF/45
                temp="45";
            }
            tdata = Integer.parseInt(temp, 16);
            sum += tdata;
        }
        for (int t = 0; t < 4 - Integer.toHexString(sum).length(); t++) {
            Builder.append("0");
        }
        return Builder.append(Integer.toHexString(sum).toUpperCase()).toString();
    }  //获取校验和

    /**
     * 初始化串口设备列表
     */
    private void initDevice() {
        mDevice = new Device("/dev/ttyS2", "115200"); //这里设置的
        switchSerialPort();
    }

    private void sendData(String text) {
        SerialPortManager.instance().sendCommand(text);
        Log.e("串口发送短信", "命令：" + text);
    }

    /**
     * 打开或关闭串口
     */
    private void switchSerialPort() {
        if (mOpened) {
            SerialPortManager.instance().close();
            mOpened = false;
        } else {
            mOpened = SerialPortManager.instance().open(mDevice) != null;
            if (mOpened) {
                //ToastUtil.showOne(this, "成功与底层通信");
            } else {
                ToastUtil.showOne(this, "打开串口失败");
            }
        }
    }
    /**
     * 模拟按键输入，转译输入按键的键值
     * @param keyCode 转移后的按键键值，参考Shell中按键值表
     */
    private void sendKeyCode(final int keyCode) {  //最快的一种方式 必须在前台才能使用
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // 创建一个Instrumentation对象
                    Instrumentation inst = new Instrumentation();
                    // 调用inst对象的按键模拟方法
                    inst.sendKeyDownUpSync(keyCode);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
        Log.i("TAG", "sendKeyCode:"+keyCode);
    }

}
