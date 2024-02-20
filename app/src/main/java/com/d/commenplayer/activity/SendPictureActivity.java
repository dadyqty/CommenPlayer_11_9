package com.d.commenplayer.activity;


import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Instrumentation;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.d.commenplayer.Activity_contorller;
import com.d.commenplayer.R;
import com.d.commenplayer.comn.Device;
import com.d.commenplayer.comn.message.IMessage;
import com.d.commenplayer.comn.message.SerialPortManager;
import com.d.commenplayer.util.ToastUtil;
import com.d.commenplayer.util.constant.CustomToast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;


public class SendPictureActivity extends AppCompatActivity {
    private static final int WHAT_SEND_ERROR = 10;
    private long predowmTime =0;
    private int count_downtime=0;
    private int count_changlanguage;
    private boolean flag_number = false;
    private MyHandle myHandle = new MyHandle(this);
    private boolean[] isdelay = new boolean[14];
    private boolean issend = false;
    private int last_keydown = 0;
    private int last_messagecode = 0;
    private int[] countarray = new int[14];
    private Timer timer;
    private boolean isRunning;
    private boolean mOpened = false;
    private Device mDevice;
    private CustomToast customToast;
    private int time;
    private long time1;
    private long sendstarttime;

    private EditText address;
    private TextView sendtime;
    private ImageView send_image;

    private int imgcount = 0;

    private int[] imgid = {R.drawable.test,R.drawable.test1,R.drawable.test2,R.drawable.test3,R.drawable.test4,R.drawable.test5,R.drawable.test6};

    private String name = null;
    private String number = null;

    public static Drawable image_drawable;

    int i = 0;
    private String SendData;
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if(address.isSelected()){
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
            if (event.getKeyCode() == 217 && event.getAction() == KeyEvent.ACTION_UP)   //翻页键 KeyCode() == 217, 表示删除 KEYCODE_DEL = 67;
            {
                Log.i("TAG","dispatchKeyEvent"+event.getKeyCode());
                sendKeyCode(67);
            }
        }
        else if (event.getKeyCode() == 217 && event.getAction() == KeyEvent.ACTION_UP)   //翻页键 KeyCode() == 217, 表示删除 KEYCODE_DEL = 67;
        {
            Log.i("TAG","dispatchKeyEvent"+event.getKeyCode());
            imgcount--;
            if(imgcount<0)
                imgcount = imgid.length-1;
            send_image.setImageResource(imgid[imgcount]);
        }
        if (event.getKeyCode() == 159 && event.getAction() == KeyEvent.ACTION_UP) {  //电话 气象发求救按键识别
            Log.i("TAG","dispatchKeyEvent"+event.getKeyCode());
            imgcount++;
            if(imgcount>imgid.length-1)
                imgcount = 0;
            send_image.setImageResource(imgid[imgcount]);
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
        if (event.getKeyCode() == 216 && event.getAction() == KeyEvent.ACTION_UP)       //取消键 KeyCode() == 216, 表示返回
        {
            Log.i("TAG","dispatchKeyEvent"+event.getKeyCode());
            sendKeyCode(62);        //表示空格 KEYCODE_SPACE = 62
            //准备一个带额外数据的intent对象
            finish();
            ToastUtile.showText(SendPictureActivity.this, "返回主界面");
           // Toast.makeText(SendMessageActivity.this, "返回主界面", Toast.LENGTH_SHORT).show();
        }
        if (event.getKeyCode() == 215&& event.getAction() == KeyEvent.ACTION_UP)       //确定键  KeyCode() == 215, 表示发送短信
        {
            Log.i("TAG","dispatchKeyEvent"+event.getKeyCode());
            String gbk;
            String zhongduan= address.getText().toString();
            if(zhongduan.length()<9){
                ToastUtile.showText(this, "终端号输入错误！信息发送失败！");
            }else {
                String image_string =  sendPhoto(send_image);
                Log.i("TAG", Integer.toString(image_string.length()/2));
                int image_len = image_string.length()/2;
                String msglen=Integer.toHexString(image_len).toUpperCase();
                String Length=Integer.toHexString(image_len+10).toUpperCase();

                while (Length.length()<4){
                    Length="0".concat(Length);
                }
                while (msglen.length()<4){
                    msglen="0".concat(msglen);
                }
                String CheckData="01B3"+Length+"04"+"0"+zhongduan+msglen+image_string;
                Log.i("TAG", "CheckData:"+CheckData);
                String CheckSum=GetCheckSum(CheckData);
                SendData=GetSendData(CheckData,CheckSum);
                Log.e("TAG", "SendData:"+SendData);
                sendData(SendData);

            }
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
        private final WeakReference<SendPictureActivity> mActivity;
        public MyHandle(SendPictureActivity activity){
            mActivity = new WeakReference<>(activity);
        }
        @Override
        public void handleMessage(Message msg) {
            SendPictureActivity activity = mActivity.get();
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
                                            Log.i("转译按键键值", "KeyCode:"+33);
                                            break;
                                        case 2:sendKeyCode(46);
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
                            case 2:
                                new Thread(){
                                    @Override
                                    public void run() {
                                        sendKeyCode(59);
                                        Log.i("转译按键键值", "KeyCode:"+59);
                                    }
                                }.start();
                                flag_number=false;
                                break;

                            case 3:
                                flag_number=true;
                                count_changlanguage =0;
                        }
                        break;
                    case 14:
                        sendtime.setText(Integer.toString((int)(sendstarttime+time-time1)/1000));
                        break;

                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_picture);
        Activity_contorller.add_activities(this);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        address = findViewById(R.id.address);
        send_image = findViewById(R.id.testpicture);
        sendtime = findViewById(R.id.send_times);
        customToast = new CustomToast(this,"呼叫成功,图片发送中。。。");
                address.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                address.setSelected(hasFocus);
                address.setSelection(address.getText().length());
            }
        });//为EditText添加获得焦点时的事件



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
                    if(count<9){
                        count++;
/*                        ToastUtile.showText(SendPictureActivity.this, "消息正在重新发送中。。。");
                        sendData(SendData);
                        send_handler.sendEmptyMessageDelayed(WHAT_SEND_ERROR,3000);*/
                    }else {
                        send_handler.removeMessages(WHAT_SEND_ERROR);
                        count=0;
                    }
                    break;
                default:
                    break;
            }
        }
    };

    @Subscribe(threadMode = ThreadMode.MAIN) //675 440  740 450
    public void onMessageEvent(IMessage message) {
        Map<String, Object> map = Serial_Manage(message.getMessage(), temp, data, data_use);
        data = (String) map.get("data");
        data_use = (boolean) map.get("data_use");
        if(data_use){
            String cmd = data.substring(16, 18);
            switch (cmd) {
                case "35":
                    //Toast.makeText(this, "短信发送中。。。", Toast.LENGTH_LONG).show();
                    ToastUtile.showText(this, "呼叫成功,图片发送中。。。");
                  //  customToast.show();
                   // customToast.showUntilCancel();
                    sendtime.setText("发送中");
                    data="";
                    break;
                case "29":
                    ToastUtile.showText(this, "网络忙,请稍后重试。。。");
                    //MsgInfo msginfopre29=new MsgInfo(-1,address.getText().toString(),shortmessage.getText().toString()," "," "," "," ",3);
                    //dao.add(msginfopre29);
                    data="";
                    break;
                case "2B":
                    ToastUtile.showText(this, "图片发送失败");
/*                    if(!send_handler.hasMessages(WHAT_SEND_ERROR)){
                        //send_handler.sendEmptyMessageDelayed(WHAT_SEND_ERROR,3000);
                    }*/
                    sendtime.setText("");
                   // customToast.hide();
                    //data="";
                    break;
                case "2C":
                    send_success=true;
                    send_handler.removeMessages(10);
                    //customToast.hide();
                    sendtime.setText("");
                    ToastUtile.showText(this, "图片发送成功！");
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
                case "05":
                    String times =  data.substring(18,22);
                    sendstarttime = System.currentTimeMillis();
                    time =  Integer.parseInt (times,16);
                    //sendtime();
                    break;
/*                case "05"://图片
                    Map<String, String> map01=GetGps(data.substring(28,40),data.substring(28,29));
                    AlertDialog.Builder builder01 = new AlertDialog.Builder(SendPictureActivity.this);
                    builder01.setTitle("收到图片 来自：");
                    View view18 = LayoutInflater.from(SendPictureActivity.this).inflate(R.layout.dialog_msg_rev, null);
                    TextView  tv_number_rev= (TextView) view18.findViewById(R.id.tv_number_rev);
                    TextView tv_gpsjd_rev = (TextView) view18.findViewById(R.id.tv_gpsjd_rev);
                    TextView tv_gpswd_rev = (TextView) view18.findViewById(R.id.tv_gpswd_rev);
                    tv_number_rev.setText("终端号:"+data.substring(19,28));
                    String tempzhongduan=data.substring(19,28);
                    tv_gpsjd_rev.setText("经度:"+map01.get("jd"));
                    tv_gpswd_rev.setText("纬度:"+map01.get("wd"));
                    image_drawable = recv_photo(data.substring(52,data.length()-12));
                    data_use = false;
                    data = "";
                    builder01.setIcon(image_drawable);
                    builder01.setView(view18);
                    builder01.setOnKeyListener(new DialogInterface.OnKeyListener() {
                        @Override
                        public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                            if (keyCode == 216 && event.getAction() == KeyEvent.ACTION_UP) { //取消按键
                                Button btn_neg = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_NEGATIVE);
                                btn_neg.performClick(); //点击取消
                            }
                            if (keyCode == 215 && event.getAction() == KeyEvent.ACTION_UP) {
                                Button btn_pos = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                                btn_pos.performClick(); //点击确定
                            }
                            return false;
                        }
                    });
                    builder01.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String CheckData = "01B3000325";
                            String CheckSum = GetCheckSum(CheckData);
                            String data = GetSendData(CheckData, CheckSum);
                            sendData(data);
                        }
                    });
                    builder01.setPositiveButton("查看", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            String CheckData = "01B3000325";
                            String CheckSum = GetCheckSum(CheckData);
                            String data1 = GetSendData(CheckData, CheckSum);
                            sendData(data1);
                            Intent intent01 = new Intent(SendPictureActivity.this, message_show.class);
                            intent01.putExtra("type","图片");
                            intent01.putExtra("tv_msg_zhongduanhao","终端号:"+tempzhongduan);
                            intent01.putExtra("tv_gpsjd_rev","经度:"+map01.get("jd"));
                            intent01.putExtra("tv_gpswd_rev","纬度:"+map01.get("wd"));
                            startActivity(intent01);
                        }
                    });
                    Dialog dialog01 = builder01.create();
                    dialog01.show();
                    break;*/
                default:
                    break;
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
                    System.out.println("算校验"+ComputeCheckSum);
                    System.out.println("校验"+GetCheckSum);
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
        String sum_hex = Integer.toHexString(sum);
        if(sum_hex.length()>4)
            sum_hex=sum_hex.substring(sum_hex.length()-4);
        else
        for (int t = 0; t < 4 - sum_hex.length(); t++) {
            Builder.append("0");
        }
        return Builder.append(sum_hex.toUpperCase()).toString();
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
        Log.e("串口发送图片", "命令：" + text);
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
    /**
     * 将图片转换成十六进制字符串
     * @param photo
     * @return
     */
    public static String sendPhoto(ImageView photo) {
        Drawable d = photo.getDrawable();
        Bitmap bitmap=((BitmapDrawable)d).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,80,stream);
        int options = 80;
        //如果压缩后的大小超出所要求的，继续压缩
        while (stream.toByteArray().length / 1024 > 3){
            if (options>=10){//避免出现options<=0
                options -=10;
            } else {
                break;
            }
            stream.reset();
            bitmap.compress(Bitmap.CompressFormat.JPEG,options,stream);
            //每次减少10%质量
        }
        byte[] bt = stream.toByteArray();
        String photoStr = byte2hex(bt);
        return photoStr;
    }


    /**
     * 二进制转字符串
     * @param b
     * @return
     */
    public static String byte2hex(byte[] b)
    {
        StringBuilder sb = new StringBuilder();
        String stmp = "";
        for (int n = 0; n < b.length; n++) {
            stmp = Integer.toHexString(b[n] & 0XFF);
            if (stmp.length() == 1) {
                sb.append("0" + stmp);
            } else {
                sb.append(stmp);
            }
        }
        return sb.toString();
    }

    /**
     * 质量压缩并存到SD卡中
     * @param bitmap
     * @param reqSize 需要的大小
     * @return
     */

    public static String qualityCompress1(Bitmap bitmap ,int reqSize){

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        //这里100表示不压缩，把压缩后的数据存放到baos中
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,baos);
        int options = 95;
        //如果压缩后的大小超出所要求的，继续压缩
        while (baos.toByteArray().length / 1024 > reqSize){
            baos.reset();
            bitmap.compress(Bitmap.CompressFormat.JPEG,options,baos);

            //每次减少5%质量
            if (options>5){//避免出现options<=0
                options -=5;
            } else {
                break;
            }

        }


        //存入SD卡中
        SimpleDateFormat formatYMD = new SimpleDateFormat("yyyy/MM/dd");

        String compressImgUri =   "000/"
                + formatYMD.format(new Date()) + "/" + System.currentTimeMillis() + "a.jpg";

        File outputFile = new File(compressImgUri);
        if (!outputFile.exists()) {
            outputFile.getParentFile().mkdirs();
        } else {
            outputFile.delete();
        }
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(outputFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        bitmap.compress(Bitmap.CompressFormat.JPEG, options, out);

        return outputFile.getPath();

    }

    private void sendtime(){
        new Thread(){
            @Override
            public void run() {
                do {
                    try {
                        Thread.sleep(1000);
                        Message message=new Message();
                        message.what=14;
                        myHandle.sendMessage(message);
                        time1 = System.currentTimeMillis();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }while ((time1-sendstarttime)>=time);
            }
        }.start();
    }


/*    private Map<String, String> GetGps(String GPS,String xiangxian) {
        Map<String, String> map = new HashMap<>();
        int jingdu = Integer.parseInt(GPS.substring(0, 7), 16);
        int weidu = Integer.parseInt(GPS.substring(7, 12), 16);
        String jd = Integer.toString(jingdu);
        String wd = Integer.toString(weidu);
        while (jd.length() < 8) {
            jd = "0".concat(jd);
        }
        while (wd.length() < 6) {
            wd = "0".concat(wd);
        }
        char fangwei = jd.charAt(0);
        switch (fangwei) {
            case '0':
                map.put("jd", jd.substring(1, 4) + "°" + jd.substring(4, 6) + "′" + jd.substring(6, 8) + "″E");
                map.put("wd", wd.substring(0, 2) + "°" + wd.substring(2, 4) + "′" + wd.substring(4, 6) + "″N");
                map.put(xiangxian,"0");
                break;
            case '1':
                map.put("jd", jd.substring(1, 4) + "°" + jd.substring(4, 6) + "′" + jd.substring(6, 8) + "″W");
                map.put("wd", wd.substring(0, 2) + "°" + wd.substring(2, 4) + "′" + wd.substring(4, 6) + "″N");
                map.put(xiangxian,"1");
                break;
            case '2':
                map.put("jd", jd.substring(1, 4) + "°" + jd.substring(4, 6) + "′" + jd.substring(6, 8) + "″W");
                map.put("wd", wd.substring(0, 2) + "°" + wd.substring(2, 4) + "′" + wd.substring(4, 6) + "″S");
                map.put(xiangxian,"2");
                break;
            case '3':
                map.put("jd", jd.substring(1, 4) + "°" + jd.substring(4, 6) + "′" + jd.substring(6, 8) + "″E");
                map.put("wd", wd.substring(0, 2) + "°" + wd.substring(2, 4) + "′" + wd.substring(4, 6) + "″S");
                map.put(xiangxian,"3");
                break;
            default:
                break;
        }
        return map;
    }*/

/*    public Drawable recv_photo(String sendPhoto) {

        byte[] recv_byte = hex2byte(sendPhoto);
        Bitmap bitmap = Bytes2Bitmap(recv_byte);
        Drawable drawable = new BitmapDrawable(this.getResources(),bitmap);
        return drawable;
    }*/

    /**
     * 字符转char数组
     * @param hex
     * @return
     */
/*    public static byte[] hex2byte(String hex) {
        int len = hex.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4)
                    + Character.digit(hex.charAt(i+1), 16));
        }
        return data;
    }
    public static Bitmap Bytes2Bitmap(byte[] b) {
        if (b.length != 0) {
            return BitmapFactory.decodeByteArray(b, 0, b.length);
        } else {
            return null;
        }
    }*/
}


