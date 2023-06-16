package com.d.commenplayer.activity;

import android.app.Instrumentation;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.d.commenplayer.R;
import com.d.commenplayer.comn.Device;
import com.d.commenplayer.comn.message.SerialPortManager;
import com.d.commenplayer.util.ToastUtil;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;

public class MessageActivity extends AppCompatActivity {

    private long predowmTime =0;
    private int count_downtime=0;
    private int count_changlanguage=1;
    private boolean flag_number = false;
    private MyHandle myHandle = new MyHandle(this);
    private TextView languagee;
    private boolean[] isdelay = new boolean[14];
    private boolean issend = false;
    private int last_keydown = 0;
    private int last_messagecode = 0;
    private int[] countarray = new int[14];


    private boolean mOpened = false;
    private Device mDevice;

    private EditText address;
    private EditText shortmessage;

    private String name = null;
    private String number = null;

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_F1 && event.getAction() == KeyEvent.ACTION_UP)
        {
            if (last_keydown != event.getKeyCode() && last_keydown != 0) {
                isdelay[last_messagecode] = false;
                issend = true;
                isdelay[1] = true;
                predowmTime = event.getDownTime();
                Message message = new Message();
                message.what = 1;
                myHandle.sendMessage(message);
            }
            if (event.getDownTime() - predowmTime <= 500 || predowmTime==0){
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
        if (event.getKeyCode() == KeyEvent.KEYCODE_F2 && event.getAction() == KeyEvent.ACTION_UP)
        {
            if (last_keydown != event.getKeyCode() && last_keydown != 0) {
                isdelay[last_messagecode] = false;
                issend = true;
                isdelay[2] = true;
                predowmTime = event.getDownTime();
                Message message = new Message();
                message.what = 2;
                myHandle.sendMessage(message);
            }
            if (event.getDownTime() - predowmTime <= 500 || predowmTime==0){
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

        if (event.getKeyCode() == KeyEvent.KEYCODE_F12&& event.getAction() == KeyEvent.ACTION_UP) //2
        {
            if (last_keydown != event.getKeyCode() && last_keydown != 0) {
                isdelay[last_messagecode] = false;
                issend = true;
                isdelay[12] = true;
                predowmTime = event.getDownTime();
                Message message = new Message();
                message.what = 12;
                myHandle.sendMessage(message);
            }
            if (event.getDownTime() - predowmTime <= 500 || predowmTime==0){
                predowmTime = event.getDownTime();
                if(last_keydown == event.getKeyCode() || last_keydown==0)
                    countarray[12]++;
                Log.i("识别", "dispatchKeyEvent: "+(event.getEventTime()-predowmTime)+"计数"
                        +countarray[12]);
            } else if(!issend){
                predowmTime = event.getDownTime();
                Message message = new Message();
                message.what = 12;
                myHandle.sendMessage(message);
                isdelay[12] = true;
            }
            last_keydown = event.getKeyCode();
            last_messagecode = 12;
        }

        if (event.getKeyCode() == KeyEvent.KEYCODE_F3 && event.getAction() == KeyEvent.ACTION_UP) //3
        {
            if (last_keydown != event.getKeyCode() && last_keydown != 0) {
                isdelay[last_messagecode] = false;
                issend = true;
                isdelay[3] = true;
                predowmTime = event.getDownTime();
                Message message = new Message();
                message.what = 3;
                myHandle.sendMessage(message);
            }

            if (event.getDownTime() - predowmTime <= 500 || predowmTime==0){
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
        if (event.getKeyCode() == KeyEvent.KEYCODE_F4 && event.getAction() == KeyEvent.ACTION_UP) //4
        {
            if (last_keydown != event.getKeyCode() && last_keydown != 0) {
                isdelay[last_messagecode] = false;
                issend = true;
                isdelay[4] = true;
                predowmTime = event.getDownTime();
                Message message = new Message();
                message.what = 4;
                myHandle.sendMessage(message);
            }
            if (event.getDownTime() - predowmTime <= 500 || predowmTime==0){
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
        if (event.getKeyCode() == KeyEvent.KEYCODE_F5 && event.getAction() == KeyEvent.ACTION_UP)
        {
            if (last_keydown != event.getKeyCode() && last_keydown != 0) {
                isdelay[last_messagecode] = false;
                issend = true;
                isdelay[5] = true;
                predowmTime = event.getDownTime();
                Message message = new Message();
                message.what = 5;
                myHandle.sendMessage(message);
            }
            if (event.getDownTime() - predowmTime <= 500 || predowmTime==0){
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
        if (event.getKeyCode() == KeyEvent.KEYCODE_F6 && event.getAction() == KeyEvent.ACTION_UP)
        {
            if (last_keydown != event.getKeyCode() && last_keydown != 0) {
                isdelay[last_messagecode] = false;
                issend = true;
                isdelay[6] = true;
                predowmTime = event.getDownTime();
                Message message = new Message();
                message.what = 6;
                myHandle.sendMessage(message);
            }
            if (event.getDownTime() - predowmTime <= 500 || predowmTime==0){
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
        if (event.getKeyCode() == KeyEvent.KEYCODE_F7 && event.getAction() == KeyEvent.ACTION_UP)
        {
            if (last_keydown != event.getKeyCode() && last_keydown != 0) {
                isdelay[last_messagecode] = false;
                issend = true;
                isdelay[7] = true;
                predowmTime = event.getDownTime();
                Message message = new Message();
                message.what = 7;
                myHandle.sendMessage(message);
            }
            if (event.getDownTime() - predowmTime <= 500 || predowmTime==0){
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
        if (event.getKeyCode() == KeyEvent.KEYCODE_F8 && event.getAction() == KeyEvent.ACTION_UP)
        {
            if (last_keydown != event.getKeyCode() && last_keydown != 0) {
                isdelay[last_messagecode] = false;
                issend = true;
                isdelay[8] = true;
                predowmTime = event.getDownTime();
                Message message = new Message();
                message.what = 8;
                myHandle.sendMessage(message);
            }
            if (event.getDownTime() - predowmTime <= 500 || predowmTime==0){
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

        if (event.getKeyCode() == KeyEvent.KEYCODE_F9 && event.getAction() == KeyEvent.ACTION_UP)
        {
            if (last_keydown != event.getKeyCode() && last_keydown != 0) {
                isdelay[last_messagecode] = false;
                issend = true;
                isdelay[9] = true;
                predowmTime = event.getDownTime();
                Message message = new Message();
                message.what = 9;
                myHandle.sendMessage(message);
            }
            if (event.getDownTime() - predowmTime <= 500 || predowmTime==0){
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
        if (event.getKeyCode() == KeyEvent.KEYCODE_F10&& event.getAction() == KeyEvent.ACTION_UP)
        {
            if (event.getDownTime() - predowmTime <= 500 || predowmTime==0){
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
        if (event.getKeyCode() == KeyEvent.KEYCODE_F11&& event.getAction() == KeyEvent.ACTION_UP)
        {

            predowmTime = event.getDownTime();
            Message message = new Message();
            message.what = 11;
            myHandle.sendMessage(message);
        }


        if (event.getKeyCode() == 278 && event.getAction() == KeyEvent.ACTION_UP)
        {
            Message message = new Message();
            message.what = 13;
            myHandle.sendMessage(message);
            count_changlanguage++;
            Log.i("获取按键键值", "dispatchKeyEvent: "+count_changlanguage);
        }

        if (event.getKeyCode() == 217 && event.getAction() == KeyEvent.ACTION_UP)
        {
            sendKeyCode(67);
        }


        if (event.getKeyCode() == 216&& event.getAction() == KeyEvent.ACTION_UP)
        {
            Intent intent = new Intent(MessageActivity.this,SimpleActivity.class);
            startActivityForResult(intent,1);
            Toast.makeText(MessageActivity.this, "返回主界面", Toast.LENGTH_SHORT).show();
        }

        if (event.getKeyCode() == 215&& event.getAction() == KeyEvent.ACTION_UP){
            String gbk;
            try {
                String utf8 = new String(shortmessage.getText().toString().getBytes("UTF-8"));
                String unicode = new String(utf8.getBytes(), "UTF-8");
                gbk = new String(unicode.getBytes("GB2312"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                gbk = null;
            }

            StringBuilder sm = new StringBuilder();
            StringBuilder checksum = new StringBuilder();
            checksum.append("01B300").append(intToHex(gbk.length())).append("2F").append(StringToBCDS("123456"));

            sendData(gbk);
        }



        Log.i("获取按键键值", "getKeyCode:"+event.getKeyCode());
        return super.dispatchKeyEvent(event);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
//        address = findViewById(R.id.number_address);
//        shortmessage = findViewById(R.id.message_short);
        Intent intent = getIntent();
        name = intent.getStringExtra("contactname");
        number = intent.getStringExtra("contactnumber");
        if (name != null){
            address.setText(name+"("+number+")");
            //this.finish();
        }
        Log.i("yejian", "onCreate: ");
        for(int i=0;i<countarray.length;i++)
            countarray[i] = 0;
        for(int i=0;i<isdelay.length;i++)
            isdelay[i] = true;
//        languagee=findViewById(R.id.show_language);
//        languagee=findViewById(R.id.show_language);
    }

    /**
     * 初始化串口设备列表
     */
    private void initDevice() {

        mDevice = new Device("/dev/ttyS2", "115200"); //这里设置的
        switchSerialPort();
    }

    private void sendData(String text) {
        SerialPortManager.instance().sendCommand(text);
    }

    /**
     *带拨号码转化问BCD字符串
     * @param
     * @return
     */
    private static String StringToBCDS(String s) {
        StringBuilder ss = new StringBuilder();
        int leng = s.length();
        if(leng%2 != 0){
            ss.append('0');
            ss.append(s.charAt(leng-1));
            leng--;
        }

        for (int i = leng-1; i > 0; i= i-2) {
            ss.append(s.charAt(i-1));
            ss.append(s.charAt(i));
        }
        return ss.toString();
    }

    /**
     * 十进制转为为16进制字符串
     * @param n
     * @return
     */
    private static String intToHex(int n) {
        //StringBuffer s = new StringBuffer();
        StringBuilder sb = new StringBuilder(8);
        String a;
        char []b = {'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};
        if (n <= 15) {
            sb.append(0).append(b[n]);
            return  sb.toString();
        }
        while(n != 0){
            sb = sb.append(b[n%16]);
            n = n/16;
        }
        a = sb.reverse().toString();
        return a;
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
                ToastUtil.showOne(this, "成功与底层通信");
            } else {
                ToastUtil.showOne(this, "打开串口失败");
            }
        }
    }

    /**
     * 计算两次按键事件的时间差
     *
     * @param
     * @return boolean
     */

    private class MyHandle extends Handler {
        private final WeakReference<MessageActivity> mActivity;
        public MyHandle(MessageActivity activity){
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            MessageActivity activity = mActivity.get();
            if (activity != null){
                switch (msg.what){
                    case 1:
                        new Thread(){
                            @Override
                            public void run() {
                                if (!flag_number){
                                    try {
                                        Thread.sleep(500);
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
                                        for (int i=0; i<6 &&isdelay[2];i++)Thread.sleep(100);
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
                    case 12:
                        new Thread(){
                            @Override
                            public void run() {
                                if (flag_number){
                                    sendKeyCode(8);
                                    Log.i("转译按键键值", "KeyCode:"+7);
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
                                        for (int i=0; i<6 &&isdelay[3];i++)Thread.sleep(100);
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
                                        for (int i=0; i<6 &&isdelay[4];i++)Thread.sleep(100);
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
                                        for (int i=0; i<6 &&isdelay[5];i++)Thread.sleep(100);
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
                                        for (int i=0; i<6 &&isdelay[6];i++)Thread.sleep(100);
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
                                        for (int i=0; i<7 &&isdelay[7];i++)Thread.sleep(100);
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
                                        for (int i=0; i<6 &&isdelay[8];i++)Thread.sleep(100);
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
                                        for (int i=0; i<7 &&isdelay[9];i++)Thread.sleep(100);
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
                                    Thread.sleep(500);
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
                                    sendKeyCode(8);
                                    Log.i("转译按键键值", "KeyCode:"+7);
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

                }
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
    }

}
