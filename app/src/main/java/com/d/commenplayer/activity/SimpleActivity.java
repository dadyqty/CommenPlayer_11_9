package com.d.commenplayer.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Instrumentation;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.ValueCallback;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.d.commenplayer.R;
import com.d.commenplayer.comn.Device;
import com.d.commenplayer.comn.message.IMessage;
import com.d.commenplayer.comn.message.SerialPortManager;
//import com.d.commenplayer.fragment.LogFragment;
import com.d.commenplayer.netstate.NetBus;
import com.d.commenplayer.netstate.NetCompat;
import com.d.commenplayer.netstate.NetState;
import com.d.commenplayer.util.Lunar;
import com.d.commenplayer.util.ToastUtil;
import com.d.lib.commenplayer.CommenPlayer;
import com.d.lib.commenplayer.listener.IPlayerListener;
import com.d.lib.commenplayer.listener.OnNetListener;
import com.d.lib.commenplayer.ui.ControlLayout;
import com.d.lib.commenplayer.util.ULog;
import com.d.lib.commenplayer.util.Util;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import tv.danmaku.ijk.media.player.IMediaPlayer;

public class SimpleActivity extends Activity implements NetBus.OnNetListener {
    private CommenPlayer player; //播放器类
    private WebView webView;
    private ImageView aispower;
    private ImageView videopower;
    private TextView diantaihao;
    private TextView xingdaohao;
    private TextView jingdu_main;
    private TextView weidu_main;
    private boolean isVideo;
    private boolean ignoreNet;
    private boolean webViewisSelected = false;
    private boolean onReceivedError = true;
    private MsgRevDao dao;

    private TextSwitcher tv_switcher; //广告滚动显示窗口
    private TextSwitcher tv_switcher2;
    private TextSwitcher tv_switcher3;
    private TextSwitcher tv_switcher4;
    private TextSwitcher tv_switcher5;

    private Dialog dialogtemp;
    private Button button1; //更换按键显示文本实现翻页的效果
    private Button button2;
    private Button button3;
    private Button button4;
    private Button button5;
    private Button button6;
    private Button button7;
    private Button button8;
    private int count_changlanguage;
    private boolean running = false; //计时状态
    private boolean qiujiuflag = false;
    private boolean qixiang = false;
    private int seconds = 0; //计时时间
    private TextView textView_timer;
    private TextView textView_ComStatus;
    private TextView kind_of_hujiao;
    private TextView hujiao_number;
    private TextView pttanjian;
    private TextView hangsu;
    private TextView timeUpdate; //显示时间
    private String timeupdate;
    private TextView dataUpdate; //显示日历
    private String dataupdate;
    private TextView nongli; //显示日历
    private String DEFAULT_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss"; //hh 12 HH 24小时
    private Lunar lunar;
    private Device mDevice;
    private static int count = 1;
    private Long gps_time = 0L;
    private Long system_time;
    private Long temp_time;
    private Long diff = 0L;
    private String xiangxian="0";
    private boolean mOpened = false;
    private TextView qunhao;
    private int changlanguage=1;
    private ArrayList<String> alist;
    private ArrayList<String> alist2;
    private ArrayList<String> alist3;
    private ArrayList<String> alist4;
    private ArrayList<String> alist5;
    //    private LogFragment mLogFragment;
    private Map<String, String> contactsmap = new LinkedHashMap<>();   // name number
    public static Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        dao=new MsgRevDao(this);
        NetBus.getIns().addListener(this);
        initView();
        initPlayer();
        initView_advertise();

        //initFragment();
        initDevice();
        initButton();

        initCommucationView();
        initTimeView();
        runTime();

        webViewSet();


//        String checkString="01B30003F0";
//        String checksumString= GetCheckSum(checkString);
//        String getinfo= GetSendData(checkString,checksumString);
        sendData("FEFCF8F001B30003F001A7FCFEF0F8");  //向底层申请获取本船信息
//        new Thread(){
//            public void run(){
//                while (true){
//                    SystemClock.sleep(1000);
//                    sendData("0F1F3FEF26D91FCFEF0F8");
//                }
//            }
//        }.start();


    }

    private void webViewSet() {
        webView.loadUrl("http://127.0.0.1:8080/index.html");
        webView.getSettings().setDisplayZoomControls(false);//是否使用内置缩放机制
        webView.getSettings().setJavaScriptEnabled(true);  //设置与Js交互的权限
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true); // 设置允许JS弹窗
        webView.getSettings().setSupportZoom(true);// 是否支持变焦
        webView.getSettings().setBuiltInZoomControls(true);// 设置WebView是否应该使用其内置变焦机制,显示放大缩小
        webView.getSettings().setUseWideViewPort(true);//是否开启控件viewport。默认false，自适应；true时标签中指定宽度值生效
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setAllowContentAccess(true);
        webView.getSettings().setGeolocationEnabled(true);
        //使用缓存，否则localstorage等无法使用
        webView.getSettings().setDomStorageEnabled(true);

        // 当前页面打开网页
        webView.setWebViewClient(new WebViewClient() {
            //重定向URL请求，返回true表示拦截此url，返回false表示不拦截此url。
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                String url = request.getUrl().toString();
                //作用1：重定向url
                if (url.startsWith("content://")) {
                    url = url.replace("content://", "https://");
                    view.loadUrl(url);
                } else {
                    //作用2：在本页面的WebView打开，防止外部浏览器打开此链接
                    view.loadUrl(url);
                }
                return false;
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                onReceivedError = true;
            }

        });
    }

    private void initCommucationView() {
        textView_timer = findViewById(R.id.timeCount);
        textView_ComStatus = findViewById(R.id.conmiustatus);
        kind_of_hujiao=findViewById(R.id.kind_of_hujiao);
        hujiao_number=findViewById(R.id.hujiao_number);
        pttanjian = findViewById(R.id.PPTanjian);
        textView_ComStatus.setText("呼叫状态：无呼叫");
    }

    private void initTimeView() {
        hangsu = findViewById(R.id.hangsu);
        timeUpdate = findViewById(R.id.shijian);
        dataUpdate = findViewById(R.id.rili);
        nongli = findViewById(R.id.nongli);
        qunhao= findViewById(R.id.qunhao);
        lunar = new Lunar(Calendar.getInstance());
    }

    private void initButton() {
        button1 = (Button) findViewById(R.id.button1);
        button2 = (Button) findViewById(R.id.button2);
        button3 = (Button) findViewById(R.id.button3);
        button4 = (Button) findViewById(R.id.button4);
        button5 = (Button) findViewById(R.id.button5);
        button6 = (Button) findViewById(R.id.button6);
        button7 = (Button) findViewById(R.id.button7);
        button8 = (Button) findViewById(R.id.button8);
    }

    private void runTime() {
        final Handler handler = new Handler();
        handler.post(new Runnable() {
                         @Override
                         public void run() {
                             int hour = seconds / 3600 % 24;
                             int minute = seconds % 3600 / 60;
                             String time = String.format("通话时间：%02d:%02d:%02d", hour, minute, seconds % 60);
                             textView_timer.setText(time);
                             if (running) {
                                 textView_ComStatus.setText("呼叫状态：已接通");
                                 if(dialogtemp != null)
                                 {
                                     dialogtemp.dismiss();
                                     dialogtemp = null;
                                 }
                                 //sendTouchEvent(10, 750);
                                 seconds++;
                             }
                             handler.postDelayed(this, 1000);

                             SimpleDateFormat dateFormatter = new SimpleDateFormat("yyMMddHHmmss");
                             timeupdate = dateFormatter.format(Calendar.getInstance().getTime()); //获取当前时间
                             temp_time = Long.valueOf(timeupdate);
                             temp_time = temp_time + diff;

                             if (gps_time != 0L) {
                                 diff = gps_time - system_time;
                                 if (diff > 10 || diff < -10) {
                                     temp_time = temp_time + diff;
                                     system_time = temp_time;
                                 } else {
                                     system_time = temp_time;
                                 }
                             } else {
                                 system_time = temp_time;
                             }
                             gps_time = 0L;
                             timeupdate = String.valueOf(system_time);
                             dataUpdate.setText("20" + timeupdate.substring(0, 2) + "-" + timeupdate.substring(2, 4) + "-" + timeupdate.substring(4, 6) + lunar.getWeek());
                             timeUpdate.setText(timeupdate.substring(6, 8) + ":" + timeupdate.substring(8, 10) + ":" + timeupdate.substring(10, 12));
                             nongli.setText(lunar.cyclical() + lunar.animalsYear() + "年" + lunar.toString());
                         }
                     }
        );
    }  //时间相关的设置

    /**
     * 键值对应表
     * 航迹--45 航点--51 航线--46 标记--48 测距--159 旋转--213 列表--218 查船--212 求救--114 MOB--58
     * 翻页--217 菜单--159 取消--216 确定--215 归中--62 上方向--19 下方向--20 左方向--21 右方向--22  左上--113 右上--57 左下--59 右下--143
     * 缩小--277 输入法--278 放大--279
     * 数字1--142 数字2--132 数字3--133 数字4--134 数字5--135 数字6--136 数字7--137 数字8--138 数字9--139 星号*--140 数字0--141 #号--131
     * @param event
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {  //按键发送数据
        Log.e("TAG","dispatchKeyEvent:"+event.getKeyCode());
        if (event.getKeyCode() == 62 && event.getAction() == KeyEvent.ACTION_UP) { // 选中海图
            if(webViewisSelected)
            {
                webView.evaluateJavascript("javascript:restore()", new ValueCallback<String>() {
                    @Override
                    public void onReceiveValue(String value) {
                        //此处为 js 返回的结果
                    }
                });
            }
            if(!isVideo)
            {
                webViewisSelected = true;
            }
        }   //翻页功能
        if (event.getKeyCode() == 279 && event.getAction() == KeyEvent.ACTION_UP) { // 海图放大
            if(webViewisSelected)
            {
                webView.evaluateJavascript("javascript:zoomIn()", new ValueCallback<String>() {
                    @Override
                    public void onReceiveValue(String value) {
                        //此处为 js 返回的结果
                    }
                });
            }
        }
        if (event.getKeyCode() == 277 && event.getAction() == KeyEvent.ACTION_UP) { // 海图缩小
            if(webViewisSelected)
            {
                webView.evaluateJavascript("javascript:zoomOut()", new ValueCallback<String>() {
                    @Override
                    public void onReceiveValue(String value) {
                        //此处为 js 返回的结果
                    }
                });
            }
        }
        if (event.getKeyCode() == 19 && event.getAction() == KeyEvent.ACTION_UP) { // 海图上移
            if(webViewisSelected)
            {
                webView.evaluateJavascript("javascript:pantoup()", new ValueCallback<String>() {
                    @Override
                    public void onReceiveValue(String value) {
                        //此处为 js 返回的结果
                    }
                });
            }
        }
        if (event.getKeyCode() == 20 && event.getAction() == KeyEvent.ACTION_UP) { // 海图下移
            if(webViewisSelected)
            {
                webView.evaluateJavascript("javascript:pantodown()", new ValueCallback<String>() {
                    @Override
                    public void onReceiveValue(String value) {
                        //此处为 js 返回的结果
                    }
                });
            }
        }
        if (event.getKeyCode() == 21 && event.getAction() == KeyEvent.ACTION_UP) { //
            if(webViewisSelected)
            {
                webView.evaluateJavascript("javascript:pantoleft()", new ValueCallback<String>() {
                    @Override
                    public void onReceiveValue(String value) {
                        //此处为 js 返回的结果
                    }
                });
            }
        }
        if (event.getKeyCode() == 22 && event.getAction() == KeyEvent.ACTION_UP) { //
            if(webViewisSelected)
            {
                webView.evaluateJavascript("javascript:pantoright()", new ValueCallback<String>() {
                    @Override
                    public void onReceiveValue(String value) {
                        //此处为 js 返回的结果
                    }
                });
            }
        }
        if (event.getKeyCode() == 217 && event.getAction() == KeyEvent.ACTION_UP) { // 切换按键显示的文本，实现翻页的效果
            count++;
            if (count >= 4) count = 1;
            switch (count) {
                case 1:
                    button1.setText("选呼");
                    button2.setText("群呼");
                    button3.setText("信箱");
                    button4.setText("广告");
                    button5.setText("电话");
                    button6.setText("亮度");
                    button7.setText("音量");
                    button8.setText("通讯录"); //联系人
                    break;
                case 2:
                    button1.setText("定位呼");
                    button2.setText("船位发");
                    button3.setText("切换视频");
                    button4.setText("全呼");
                    button5.setText("发件箱");
                    button6.setText("对讲单发");
                    button7.setText("信道设置");
                    button8.setText("发短信");
                    break;
                case 3:
                    button1.setText("时间设置");
                    button2.setText("GPS设置");
                    button3.setText("视频源一");
                    button4.setText("视频源二");
                    button5.setText("发送图片");
                    button6.setText("海呼");
                    button7.setText("设置群号");
                    button8.setText("气象呼");
                    break;
                default:
                    break;
            }
        }   //翻页功能
        if (event.getKeyCode() == 140 && event.getAction() == KeyEvent.ACTION_UP) {                      // 按星(*)号键 设置信道号 显示于界面 并向底层发数据
            AlertDialog.Builder builder = new AlertDialog.Builder(SimpleActivity.this);
            if(running)
            {
                Toast.makeText(this,"无法设置信道号! 请先取消当前通话！",Toast.LENGTH_SHORT).show();
                return false;
            }
            builder.setTitle("设置信道号");
            View view = LayoutInflater.from(SimpleActivity.this).inflate(R.layout.setxindao, null);
            builder.setView(view);
            EditText et_set = (EditText) view.findViewById(R.id.Set_XinDao);
            builder.setOnKeyListener(new DialogInterface.OnKeyListener() {
                @Override
                public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                    if (keyCode == 142 && event.getAction() == KeyEvent.ACTION_UP) {
                        sendKeyCode(8);//对应数字1
                    }
                    if (keyCode == 132 && event.getAction() == KeyEvent.ACTION_UP) {
                        sendKeyCode(9);//对应数字2
                    }
                    if (keyCode == 133 && event.getAction() == KeyEvent.ACTION_UP) {
                        sendKeyCode(10);//对应数字3
                    }
                    if (keyCode == 134 && event.getAction() == KeyEvent.ACTION_UP) {
                        sendKeyCode(11);//对应数字4
                    }
                    if (keyCode == 135 && event.getAction() == KeyEvent.ACTION_UP) {
                        sendKeyCode(12);//对应数字5
                    }
                    if (keyCode == 136 && event.getAction() == KeyEvent.ACTION_UP) {
                        sendKeyCode(13);//对应数字6
                    }
                    if (keyCode == 137 && event.getAction() == KeyEvent.ACTION_UP) {
                        sendKeyCode(14);//对应数字7
                    }
                    if (keyCode == 138 && event.getAction() == KeyEvent.ACTION_UP) {
                        sendKeyCode(15);//对应数字8
                    }
                    if (keyCode == 139 && event.getAction() == KeyEvent.ACTION_UP) {
                        sendKeyCode(16);//对应数字9
                    }
                    if (keyCode == 141 && event.getAction() == KeyEvent.ACTION_UP) {
                        sendKeyCode(7);//对应数字10
                    }
                    if (keyCode == 217 && event.getAction() == KeyEvent.ACTION_UP) {
                        sendKeyCode(67); //退格按键，对应翻页
                    }
                    if (keyCode == 216 && event.getAction() == KeyEvent.ACTION_UP) {
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
            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String number = et_set.getText().toString();
                    int n = Integer.parseInt(number);
                    if ((n >= 1 && n <= 999)) {  //(n >= 1 && n <= 280) || (n >= 321 && n <= 440)
                        while (number.length() < 3) {
                            number = "0".concat(number);
                        }
                        xingdaohao.setText("信道号：" + number);
                        ToastUtil.show(SimpleActivity.this, "设置成功");
                        number = Integer.toHexString(n).toUpperCase();
                        while (number.length() < 4) {
                            number = "0".concat(number);
                        }
                        String CheckData = "01B300059A" + number;
                        String CheckSum = GetCheckSum(CheckData);
                        String data = GetSendData(CheckData, CheckSum);
                        sendData(data);
                    } else {
                        ToastUtil.show(SimpleActivity.this, "范围错误，设置失败");
                    }
                }
            });
            builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ToastUtil.show(SimpleActivity.this, "设置取消");
                }
            });
            Dialog dialog1 = builder.create();
            dialog1.show();
            WindowManager.LayoutParams params = dialog1.getWindow().getAttributes();
            params.width = 300;
            params.height = 180;
            dialog1.getWindow().setAttributes(params);
        }   //s按星(*)号键 设置信道号 显示于界面 并向底层发数据
        if (event.getKeyCode() == 45 && event.getAction() == KeyEvent.ACTION_UP) {
            switch (count) {
                case 1:  //弹窗dialog设置, 选呼按键识别，并弹窗
                    if(running)
                    {
                        Toast.makeText(this,"通话占用中！请先取消当前通话！",Toast.LENGTH_SHORT).show();
                        return false;
                    }
                    AlertDialog.Builder builder = new AlertDialog.Builder(SimpleActivity.this);
                    builder.setIcon(R.drawable.bohao);
                    builder.setTitle("选呼");
                    View view = LayoutInflater.from(SimpleActivity.this).inflate(R.layout.dialog_xuanhuan_send, null);
                    builder.setView(view);
                    builder.setOnKeyListener(new DialogInterface.OnKeyListener() {
                        @Override
                        public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                            if (keyCode == 142 && event.getAction() == KeyEvent.ACTION_UP) {
                                sendKeyCode(8);//对应数字1
                            }
                            if (keyCode == 132 && event.getAction() == KeyEvent.ACTION_UP) {
                                sendKeyCode(9);//对应数字2
                            }
                            if (keyCode == 133 && event.getAction() == KeyEvent.ACTION_UP) {
                                sendKeyCode(10);//对应数字3
                            }
                            if (keyCode == 134 && event.getAction() == KeyEvent.ACTION_UP) {
                                sendKeyCode(11);//对应数字4
                            }
                            if (keyCode == 135 && event.getAction() == KeyEvent.ACTION_UP) {
                                sendKeyCode(12);//对应数字5
                            }
                            if (keyCode == 136 && event.getAction() == KeyEvent.ACTION_UP) {
                                sendKeyCode(13);//对应数字6
                            }
                            if (keyCode == 137 && event.getAction() == KeyEvent.ACTION_UP) {
                                sendKeyCode(14);//对应数字7
                            }
                            if (keyCode == 138 && event.getAction() == KeyEvent.ACTION_UP) {
                                sendKeyCode(15);//对应数字8
                            }
                            if (keyCode == 139 && event.getAction() == KeyEvent.ACTION_UP) {
                                sendKeyCode(16);//对应数字9
                            }
                            if (keyCode == 141 && event.getAction() == KeyEvent.ACTION_UP) {
                                sendKeyCode(7);//对应数字10
                            }
                            if (keyCode == 217 && event.getAction() == KeyEvent.ACTION_UP) {
                                sendKeyCode(67); //退格按键，对应翻页
                            }
                            if (keyCode == 216 && event.getAction() == KeyEvent.ACTION_UP) {
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
                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        EditText zhongduanhao = (EditText) view.findViewById(R.id.zhongduanhao);
                        EditText xindaohao = (EditText) view.findViewById(R.id.xindaohao);

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (zhongduanhao.getText().toString().length() < 9) {
                                ToastUtil.show(SimpleActivity.this, "输入错误");
                            } else {
                                String a = zhongduanhao.getText().toString();
                                String b = xindaohao.getText().toString();
                                b = Integer.toHexString(Integer.parseInt(b));
                                while (b.length() < 4) {
                                    b = "0".concat(b);
                                }
                                String CheckData = "01B3000A16" + "0" + a + b;
                                String CheckSum = GetCheckSum(CheckData);
                                ToastUtil.show(SimpleActivity.this, "电台号: " + a + ", 信道号: " + b);
                                textView_ComStatus.setText("呼叫状态：正在呼叫。。。。");
                                kind_of_hujiao.setText("呼叫种类：选呼");
                                hujiao_number.setVisibility(View.VISIBLE);
                                hujiao_number.setText("对方船号："+a);
                                String data = GetSendData(CheckData, CheckSum);
                                sendData(data);
                            }
                        }
                    });
                    builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ToastUtil.show(SimpleActivity.this, "取消选呼");
                        }
                    });
                    Dialog dialog1 = builder.create();
                    dialog1.show();
                    WindowManager.LayoutParams params = dialog1.getWindow().getAttributes();
                    params.width = 400;
                    params.height = 280;
                    dialog1.getWindow().setAttributes(params);
                    break;
                case 2:
                    ToastUtil.show(this, "定位呼");
//                            sendData(sendMingling_nodata(3, "")); //气象发
                    break;
                case 3:
                    ToastUtil.show(this, "时间设置");
                    break;
                default:
                    break;

            }
        }    //选呼
        if (event.getKeyCode() == 114 && event.getAction() == KeyEvent.ACTION_UP) {  //求救呼发按键识别
            if(running)
            {
                Toast.makeText(this,"通话占用中！请先取消当前通话！",Toast.LENGTH_SHORT).show();
                return false;
            }
            Toast.makeText(SimpleActivity.this, "求救呼叫指令发送", Toast.LENGTH_LONG).show();
            String CheckData = "01B3000311";
            String CheckSum = GetCheckSum(CheckData);
            String data = GetSendData(CheckData, CheckSum);
            sendData(data);
            textView_ComStatus.setText("呼叫状态：呼叫中");
            kind_of_hujiao.setText("呼叫种类：求救");
            qiujiuflag = true;
        }   //求救呼
        if (event.getKeyCode() == 51 && event.getAction() == KeyEvent.ACTION_UP) {
            switch (count) {//群呼按键识别
                case 1:
                    if(running)
                    {
                        Toast.makeText(this,"通话占用中！请先取消当前通话！",Toast.LENGTH_SHORT).show();
                        return false;
                    }
                    AlertDialog.Builder builder = new AlertDialog.Builder(SimpleActivity.this);
                    builder.setIcon(R.drawable.bohao);
                    builder.setTitle("群呼");
                    View view = LayoutInflater.from(SimpleActivity.this).inflate(R.layout.dialog_send_qunhu, null);
                    builder.setView(view);
                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        EditText qunhuhao = (EditText) view.findViewById(R.id.qunhuhao);
                        EditText xindaohao = (EditText) view.findViewById(R.id.xindaohao);

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String qunhuString = qunhuhao.getText().toString();
                            String xindaoString = xindaohao.getText().toString();
                            xindaoString = Integer.toHexString(Integer.parseInt(xindaoString));

                            while (xindaoString.length() < 4) {
                                xindaoString = "0".concat(xindaoString);
                            }
                            if(qunhuString.length()!=6){
                                ToastUtil.show(SimpleActivity.this, "群号输入错误！");
                            }else if(xindaoString.length()!=4)
                            {
                                ToastUtil.show(SimpleActivity.this, "信道号输入错误！");
                            }
                            else {
                                String CheckData = "01B3000813"+qunhuString+xindaoString;
                                String CheckSum = GetCheckSum(CheckData);
                                String data = GetSendData(CheckData, CheckSum);
                                sendData(data);
                                ToastUtil.show(SimpleActivity.this, "正在发送中。。。");
                                kind_of_hujiao.setText("呼叫种类：群呼");
                                hujiao_number.setVisibility(View.VISIBLE);
                                hujiao_number.setText("群号："+qunhuString);
                            }
                        }
                    });
                    builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    builder.setOnKeyListener(new DialogInterface.OnKeyListener() {
                        @Override
                        public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                            if (keyCode == 142 && event.getAction() == KeyEvent.ACTION_UP) {
                                sendKeyCode(8);//对应数字1
                            }
                            if (keyCode == 132 && event.getAction() == KeyEvent.ACTION_UP) {
                                sendKeyCode(9);//对应数字2
                            }
                            if (keyCode == 133 && event.getAction() == KeyEvent.ACTION_UP) {
                                sendKeyCode(10);//对应数字3
                            }
                            if (keyCode == 134 && event.getAction() == KeyEvent.ACTION_UP) {
                                sendKeyCode(11);//对应数字4
                            }
                            if (keyCode == 135 && event.getAction() == KeyEvent.ACTION_UP) {
                                sendKeyCode(12);//对应数字5
                            }
                            if (keyCode == 136 && event.getAction() == KeyEvent.ACTION_UP) {
                                sendKeyCode(13);//对应数字6
                            }
                            if (keyCode == 137 && event.getAction() == KeyEvent.ACTION_UP) {
                                sendKeyCode(14);//对应数字7
                            }
                            if (keyCode == 138 && event.getAction() == KeyEvent.ACTION_UP) {
                                sendKeyCode(15);//对应数字8
                            }
                            if (keyCode == 139 && event.getAction() == KeyEvent.ACTION_UP) {
                                sendKeyCode(16);//对应数字9
                            }
                            if (keyCode == 141 && event.getAction() == KeyEvent.ACTION_UP) {
                                sendKeyCode(7);//对应数字10
                            }
                            if (keyCode == 217 && event.getAction() == KeyEvent.ACTION_UP) {
                                sendKeyCode(67); //退格按键，对应翻页
                            }
                            if (keyCode == 216 && event.getAction() == KeyEvent.ACTION_UP) {
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
                    Dialog dialog1 = builder.create();
                    dialog1.show();
                    WindowManager.LayoutParams params22 = dialog1.getWindow().getAttributes();
                    params22.width = 400;
                    params22.height = 300;
                    dialog1.getWindow().setAttributes(params22);
                    break;

                case 2:  //船位呼发
                    AlertDialog.Builder builder2 = new AlertDialog.Builder(SimpleActivity.this);
                    builder2.setIcon(R.drawable.ic_launcher_foreground);
                    builder2.setTitle("船位呼发");
                    View view2 = LayoutInflater.from(SimpleActivity.this).inflate(R.layout.dialog_send_qunhu, null);
                    builder2.setView(view2);


                    builder2.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        EditText chuanweihufa = (EditText) view2.findViewById(R.id.qunhuhao);

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String a = chuanweihufa.getText().toString().trim();
                            int Length = 3 + a.length();
                            String Comand = "1E";
                            sendData(sendMingling(Length, Comand, a));
                            Toast.makeText(SimpleActivity.this, "船位呼发号: " + a, Toast.LENGTH_SHORT).show();
                        }
                    });
                    builder2.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    builder2.setOnKeyListener(new DialogInterface.OnKeyListener() {
                        @Override
                        public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                            if (keyCode == 142 && event.getAction() == KeyEvent.ACTION_UP) {
                                sendKeyCode(8);
                                Log.i("按键输入", "dispatchKeyEvent: 输入1");
                            }
                            if (keyCode == 132 && event.getAction() == KeyEvent.ACTION_UP) {
                                sendKeyCode(9);
                                Log.i("按键输入", "dispatchKeyEvent: 输入2");
                            }
                            if (keyCode == 133 && event.getAction() == KeyEvent.ACTION_UP) {
                                sendKeyCode(10);
                                Log.i("按键输入", "dispatchKeyEvent: 输入3");
                            }
                            if (keyCode == 134 && event.getAction() == KeyEvent.ACTION_UP) {
                                sendKeyCode(11);
                                Log.i("按键输入", "dispatchKeyEvent: 输入=4");
                            }
                            if (keyCode == 135 && event.getAction() == KeyEvent.ACTION_UP) {
                                sendKeyCode(12);
                                Log.i("按键输入", "dispatchKeyEvent: 输入5");
                            }
                            if (keyCode == 136 && event.getAction() == KeyEvent.ACTION_UP) {
                                sendKeyCode(13);
                                Log.i("按键输入", "dispatchKeyEvent: 输入6");
                            }
                            if (keyCode == 137 && event.getAction() == KeyEvent.ACTION_UP) {
                                sendKeyCode(14);
                            }
                            if (keyCode == 138 && event.getAction() == KeyEvent.ACTION_UP) {
                                sendKeyCode(15);
                                Log.i("按键输入", "dispatchKeyEvent: 输入8");
                            }
                            if (keyCode == 139 && event.getAction() == KeyEvent.ACTION_UP) {
                                sendKeyCode(16);
                                Log.i("按键输入", "dispatchKeyEvent: 输入9");
                            }
                            if (keyCode == 141 && event.getAction() == KeyEvent.ACTION_UP) {
                                sendKeyCode(7);
                                Log.i("按键输入", "dispatchKeyEvent: 输入0");
                            }
                            if (keyCode == 217 && event.getAction() == KeyEvent.ACTION_UP) { //退格按键，对应翻页
                                sendKeyCode(67);
                                Log.i("按键输入", "dispatchKeyEvent: 输入退格"); //keycode 67
                            }
                            if (keyCode == 216 && event.getAction() == KeyEvent.ACTION_UP) {
                                Button btn_neg = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_NEGATIVE);
                                btn_neg.performClick(); //点击取消
                            }
                            if (keyCode == 215 && event.getAction() == KeyEvent.ACTION_UP) {
                                Button btn_pos = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                                btn_pos.performClick(); //点击确定
                            }
                            Log.i("按键输入", "onKey: " + keyCode);
                            return false;
                        }
                    });
                    builder2.show();
                    break;
                case 3:
                    AlertDialog.Builder builder513 = new AlertDialog.Builder(SimpleActivity.this);
                    builder513.setIcon(R.drawable.bohao);
                    builder513.setTitle("设置GPS");
                    View view513 = LayoutInflater.from(SimpleActivity.this).inflate(R.layout.dialog_set_gps, null);
                    builder513.setView(view513);

                    builder513.setOnKeyListener(new DialogInterface.OnKeyListener() {
                        @Override
                        public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                            if (keyCode == 142 && event.getAction() == KeyEvent.ACTION_UP) {
                                sendKeyCode(8);//对应数字1
                            }
                            if (keyCode == 132 && event.getAction() == KeyEvent.ACTION_UP) {
                                sendKeyCode(9);//对应数字2
                            }
                            if (keyCode == 133 && event.getAction() == KeyEvent.ACTION_UP) {
                                sendKeyCode(10);//对应数字3
                            }
                            if (keyCode == 134 && event.getAction() == KeyEvent.ACTION_UP) {
                                sendKeyCode(11);//对应数字4
                            }
                            if (keyCode == 135 && event.getAction() == KeyEvent.ACTION_UP) {
                                sendKeyCode(12);//对应数字5
                            }
                            if (keyCode == 136 && event.getAction() == KeyEvent.ACTION_UP) {
                                sendKeyCode(13);//对应数字6
                            }
                            if (keyCode == 137 && event.getAction() == KeyEvent.ACTION_UP) {
                                sendKeyCode(14);//对应数字7
                            }
                            if (keyCode == 138 && event.getAction() == KeyEvent.ACTION_UP) {
                                sendKeyCode(15);//对应数字8
                            }
                            if (keyCode == 139 && event.getAction() == KeyEvent.ACTION_UP) {
                                sendKeyCode(16);//对应数字9
                            }
                            if (keyCode == 141 && event.getAction() == KeyEvent.ACTION_UP) {
                                sendKeyCode(7);//对应数字10
                            }
                            if (keyCode == 217 && event.getAction() == KeyEvent.ACTION_UP) {
                                sendKeyCode(67); //退格按键，对应翻页
                            }
                            if (keyCode == 216 && event.getAction() == KeyEvent.ACTION_UP) {
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
                    builder513.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        EditText jingdu_set = (EditText) view513.findViewById(R.id.jingdu_set);
                        EditText weidu_set = (EditText) view513.findViewById(R.id.weidu_set);
                        EditText et_xiangxian = (EditText) view513.findViewById(R.id.xiangxian);

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String wd = "";
                            String jd = "";
                            if (weidu_set.getText().toString().length() < 6 || jingdu_set.getText().toString().length() < 7 || et_xiangxian.getText().toString().length() != 1) {
                                ToastUtil.show(SimpleActivity.this, "GPS输入错误");
                            } else {
                                switch (et_xiangxian.getText().toString()) {
                                    case "0":
                                        jd  = jingdu_set.getText().toString().substring(0, 3) + "°" + jingdu_set.getText().toString().substring(3, 5) + "′" +
                                                jingdu_set.getText().toString().substring(5, 7) + "″" + "E";
                                        wd = weidu_set.getText().toString().substring(0, 2) + "°" + weidu_set.getText().toString().substring(2, 4) + "′" +
                                                weidu_set.getText().toString().substring(4, 6) + "″" + "N";
                                        xiangxian="0";
                                        break;
                                    case "1":
                                        jd = jingdu_set.getText().toString().substring(0, 3) + "°" + jingdu_set.getText().toString().substring(3, 5) + "′" +
                                                jingdu_set.getText().toString().substring(5, 7) + "″" + "W";
                                        wd = weidu_set.getText().toString().substring(0, 2) + "°" + weidu_set.getText().toString().substring(2, 4) + "′" +
                                                weidu_set.getText().toString().substring(4, 6) + "″" + "N";
                                        xiangxian="1";
                                        break;
                                    case "2":
                                        jd = jingdu_set.getText().toString().substring(0, 3) + "°" + jingdu_set.getText().toString().substring(3, 5) + "′" +
                                                jingdu_set.getText().toString().substring(5, 7) + "″" + "W";
                                        wd = weidu_set.getText().toString().substring(0, 2) + "°" + weidu_set.getText().toString().substring(2, 4) + "′" +
                                                weidu_set.getText().toString().substring(4, 6) + "″" + "S";
                                        xiangxian="2";
                                        break;
                                    case "3":
                                        jd = jingdu_set.getText().toString().substring(0, 3) + "°" + jingdu_set.getText().toString().substring(3, 5) + "′" +
                                                jingdu_set.getText().toString().substring(5, 7) + "″" +"E";
                                        wd = weidu_set.getText().toString().substring(0, 2) + "°" + weidu_set.getText().toString().substring(2, 4) + "′" +
                                                weidu_set.getText().toString().substring(4, 6) + "″" +"S";
                                        xiangxian="3";
                                        break;
                                    default:
                                        break;
                                }
                            }
                            jingdu_main.setText("经度:" + jd);
                            weidu_main.setText("纬度:" + wd);
                        }
                    });
                    builder513.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ToastUtil.show(SimpleActivity.this, "取消设置");
                        }
                    });
                    Dialog dialog513 = builder513.create();
                    dialog513.show();
                    WindowManager.LayoutParams params = dialog513.getWindow().getAttributes();
                    params.width = 400;
                    params.height = 280;
                    dialog513.getWindow().setAttributes(params);
                    break;

                default:
                    break;

            }


        }
        if (event.getKeyCode() == 46 && event.getAction() == KeyEvent.ACTION_UP) {  //短信 本船信息按键识别
            switch (count) {
                case 1:
                    Intent intent46 = new Intent(SimpleActivity.this, MsgBox.class);
                    intent46.putExtra("count_changlanguage",String.valueOf(changlanguage));
                    startActivityForResult(intent46, 9);

//                    Intent intent = new Intent(SimpleActivity.this, ContactsActivity.class);
//                    intent.putExtra("count_changlanguage",String.valueOf(changlanguage));
//                    startActivityForResult(intent, 9);
                    break;
                case 2:
                    if (isVideo){
                        player.setVisibility(View.INVISIBLE);
                        webView.setVisibility(View.VISIBLE);
                        if(onReceivedError) {
                            webViewSet();
                            onReceivedError = false;
                        }
                        isVideo = false;
                    } else {
                        player.setVisibility(View.VISIBLE);
                        webView.setVisibility(View.INVISIBLE);
                        isVideo = true;
                        webViewisSelected = false;
                    }
                    Toast.makeText(SimpleActivity.this, "切换按键按下", Toast.LENGTH_SHORT).show();
                    break;
                case 3:

                    break;
                default:
                    break;
            }

        }
        if (event.getKeyCode() == 48 && event.getAction() == KeyEvent.ACTION_UP) {  //发件箱 气象收按键识别
            switch (count) {
                case 1:  //广告
                    if(running)
                    {
                        Toast.makeText(this,"通话占用中！请先取消当前通话！",Toast.LENGTH_SHORT).show();
                        return false;
                    }
                    Intent intent48 = new Intent(SimpleActivity.this, SendMessageActivity.class);
                    intent48.putExtra("count_changlanguage",String.valueOf(changlanguage));
                    intent48.putExtra("kind","02");
                    startActivityForResult(intent48, 9);
                    break;
                case 2:  //全呼
                    if(running)
                    {
                        Toast.makeText(this,"通话占用中！请先取消当前通话！",Toast.LENGTH_SHORT).show();
                        return false;
                    }
                    Toast.makeText(SimpleActivity.this, "全呼指令发送", Toast.LENGTH_LONG).show();
                    String CheckData = "01B300031A";
                    String CheckSum = GetCheckSum(CheckData);
                    String data = GetSendData(CheckData, CheckSum);
                    sendData(data);
                    textView_ComStatus.setText("呼叫状态：呼叫中");
                    kind_of_hujiao.setText("呼叫种类：全呼");
                    break;
                default:
                    break;
            }
        }
        if (event.getKeyCode() == 159 && event.getAction() == KeyEvent.ACTION_UP) {  //电话 气象发求救按键识别
            switch (count) {
                case 1:
                    Toast.makeText(SimpleActivity.this, "电话按键", Toast.LENGTH_SHORT).show();
                    pttanjian.setText("语音正在发射");
                    break;
                case 2:
//                    Intent intent159 = new Intent(SimpleActivity.this, messagerevActivity.class);
//                    startActivityForResult(intent159, 159);
//                    Toast.makeText(SimpleActivity.this, "短信按键按下", Toast.LENGTH_SHORT).show();
                    break;
                case 3:
                    if(running)
                    {
                        Toast.makeText(this,"通话占用中！请先取消当前通话！",Toast.LENGTH_SHORT).show();
                        return false;
                    }
                    Intent intent2 = new Intent(SimpleActivity.this, SendPictureActivity.class);
                    intent2.putExtra("count_changlanguage",String.valueOf(changlanguage));
                    intent2.putExtra("kind","01");
                    startActivityForResult(intent2, 9);
                    break;
                default:
                    break;
            }
        }
        if (event.getKeyCode() == 213 && event.getAction() == KeyEvent.ACTION_UP) {  //电话 对讲单发按键识别
            switch (count) {
                case 1:
                    Toast.makeText(SimpleActivity.this, "亮度按键", Toast.LENGTH_SHORT).show();
                    break;
                case 2:
                    AlertDialog.Builder builder2 = new AlertDialog.Builder(SimpleActivity.this);
                    builder2.setIcon(R.drawable.ic_launcher_foreground);
                    builder2.setTitle("对讲单发");
                    View view2 = LayoutInflater.from(SimpleActivity.this).inflate(R.layout.dialog_send_qunhu, null);
                    builder2.setView(view2);


                    builder2.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        EditText chuanweihufa = (EditText) view2.findViewById(R.id.qunhuhao);

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String a = chuanweihufa.getText().toString().trim();
                            int Length = 3 + a.length();
                            String Comand = "2F";
                            sendData(sendMingling(Length, Comand, a));
                            Toast.makeText(SimpleActivity.this, "对讲单发: " + a, Toast.LENGTH_SHORT).show();
                        }
                    });
                    builder2.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    builder2.setOnKeyListener(new DialogInterface.OnKeyListener() {
                        @Override
                        public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                            if (keyCode == 142 && event.getAction() == KeyEvent.ACTION_UP) {
                                sendKeyCode(8);
                                Log.i("按键输入", "dispatchKeyEvent: 输入1");
                            }
                            if (keyCode == 132 && event.getAction() == KeyEvent.ACTION_UP) {
                                sendKeyCode(9);
                                Log.i("按键输入", "dispatchKeyEvent: 输入2");
                            }
                            if (keyCode == 133 && event.getAction() == KeyEvent.ACTION_UP) {
                                sendKeyCode(10);
                                Log.i("按键输入", "dispatchKeyEvent: 输入3");
                            }
                            if (keyCode == 134 && event.getAction() == KeyEvent.ACTION_UP) {
                                sendKeyCode(11);
                                Log.i("按键输入", "dispatchKeyEvent: 输入=4");
                            }
                            if (keyCode == 135 && event.getAction() == KeyEvent.ACTION_UP) {
                                sendKeyCode(12);
                                Log.i("按键输入", "dispatchKeyEvent: 输入5");
                            }
                            if (keyCode == 136 && event.getAction() == KeyEvent.ACTION_UP) {
                                sendKeyCode(13);
                                Log.i("按键输入", "dispatchKeyEvent: 输入6");
                            }
                            if (keyCode == 137 && event.getAction() == KeyEvent.ACTION_UP) {
                                sendKeyCode(14);
                            }
                            if (keyCode == 138 && event.getAction() == KeyEvent.ACTION_UP) {
                                sendKeyCode(15);
                                Log.i("按键输入", "dispatchKeyEvent: 输入8");
                            }
                            if (keyCode == 139 && event.getAction() == KeyEvent.ACTION_UP) {
                                sendKeyCode(16);
                                Log.i("按键输入", "dispatchKeyEvent: 输入9");
                            }
                            if (keyCode == 141 && event.getAction() == KeyEvent.ACTION_UP) {
                                sendKeyCode(7);
                                Log.i("按键输入", "dispatchKeyEvent: 输入0");
                            }
                            if (keyCode == 217 && event.getAction() == KeyEvent.ACTION_UP) { //退格按键，对应翻页
                                sendKeyCode(67);
                                Log.i("按键输入", "dispatchKeyEvent: 输入退格"); //keycode 67
                            }
                            if (keyCode == 216 && event.getAction() == KeyEvent.ACTION_UP) {
                                Button btn_neg = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_NEGATIVE);
                                btn_neg.performClick(); //点击取消
                            }
                            if (keyCode == 215 && event.getAction() == KeyEvent.ACTION_UP) {
                                Button btn_pos = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                                btn_pos.performClick(); //点击确定
                            }
                            Log.i("按键输入", "onKey: " + keyCode);
                            return false;
                        }
                    });
                    builder2.show();

                    break;
                case 3:
                    if(running)
                    {
                        Toast.makeText(this,"通话占用中！请先取消当前通话！",Toast.LENGTH_SHORT).show();
                        return false;
                    }
                    AlertDialog.Builder builder = new AlertDialog.Builder(SimpleActivity.this);
                    builder.setIcon(R.drawable.bohao);
                    builder.setTitle("海呼");
                    View view = LayoutInflater.from(SimpleActivity.this).inflate(R.layout.dialog_haihu_send, null);
                    builder.setView(view);
                    builder.setOnKeyListener(new DialogInterface.OnKeyListener() {
                        @Override
                        public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                            if (keyCode == 142 && event.getAction() == KeyEvent.ACTION_UP) {
                                sendKeyCode(8);//对应数字1
                            }
                            if (keyCode == 132 && event.getAction() == KeyEvent.ACTION_UP) {
                                sendKeyCode(9);//对应数字2
                            }
                            if (keyCode == 133 && event.getAction() == KeyEvent.ACTION_UP) {
                                sendKeyCode(10);//对应数字3
                            }
                            if (keyCode == 134 && event.getAction() == KeyEvent.ACTION_UP) {
                                sendKeyCode(11);//对应数字4
                            }
                            if (keyCode == 135 && event.getAction() == KeyEvent.ACTION_UP) {
                                sendKeyCode(12);//对应数字5
                            }
                            if (keyCode == 136 && event.getAction() == KeyEvent.ACTION_UP) {
                                sendKeyCode(13);//对应数字6
                            }
                            if (keyCode == 137 && event.getAction() == KeyEvent.ACTION_UP) {
                                sendKeyCode(14);//对应数字7
                            }
                            if (keyCode == 138 && event.getAction() == KeyEvent.ACTION_UP) {
                                sendKeyCode(15);//对应数字8
                            }
                            if (keyCode == 139 && event.getAction() == KeyEvent.ACTION_UP) {
                                sendKeyCode(16);//对应数字9
                            }
                            if (keyCode == 141 && event.getAction() == KeyEvent.ACTION_UP) {
                                sendKeyCode(7);//对应数字10
                            }
                            if (keyCode == 131 && event.getAction() == KeyEvent.ACTION_UP) {
                                sendKeyCode(18);//对应#
                            }
                            if (keyCode == 217 && event.getAction() == KeyEvent.ACTION_UP) {
                                sendKeyCode(67); //退格按键，对应翻页
                            }
                            if (keyCode == 216 && event.getAction() == KeyEvent.ACTION_UP) {
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
                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        EditText jingduduzengliang = (EditText) view.findViewById(R.id.jingdudu);
                        EditText jingdufenzengliang = (EditText) view.findViewById(R.id.jingdufen);
                        EditText weiduduzengliang = (EditText) view.findViewById(R.id.weidudu);
                        EditText weidufenzengliang = (EditText) view.findViewById(R.id.weidufen);
                        EditText xiangxian =(EditText) view.findViewById(R.id.xiangxian) ;
                        EditText cankaodianjingdudu =(EditText) view.findViewById(R.id.cankaojingdudu) ;
                        EditText cankaodianjingdufen =(EditText) view.findViewById(R.id.cankaojingdufen) ;
                        EditText cankaodianweidudu = (EditText) view.findViewById(R.id.cankaoweidudu);
                        EditText cankaodianweidufen = (EditText) view.findViewById(R.id.cankaoweidufen);
                        EditText xindaohao = (EditText) view.findViewById(R.id.xindaohao7);
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (jingduduzengliang.getText().toString().length() > 5) {
                                ToastUtil.show(SimpleActivity.this, "输入错误");
                            } else {
                                String jingzeng1 = jingduduzengliang.getText().toString();
                                String jingzeng2 = jingdufenzengliang.getText().toString();
                                String weizeng1 = weiduduzengliang.getText().toString();
                                String weizeng2 = weidufenzengliang.getText().toString();
                                String xx = xiangxian.getText().toString();
                                String cankaojing1 = cankaodianjingdudu.getText().toString();
                                String cankaojing2 = cankaodianjingdufen.getText().toString();
                                String cankaowei1 = cankaodianweidudu.getText().toString();
                                String cankaowei2 = cankaodianweidufen.getText().toString();
                                String xd = xindaohao.getText().toString();

                                String a = Integer.toString(Integer.valueOf(jingzeng1,10)*100+Integer.valueOf(jingzeng2,10),16);
                                String b = Integer.toString(Integer.valueOf(weizeng1,10)*100+Integer.valueOf(weizeng2,10),16);
                                String d = Integer.toString(Integer.valueOf(xx,10)*100000+Integer.valueOf(cankaojing1,10)*100+Integer.valueOf(cankaojing2,10),16);
                                String e = Integer.toString(Integer.valueOf(cankaowei1,10)*100+Integer.valueOf(cankaowei2,10),16);
                                String f = Integer.toString(Integer.valueOf(xd,10),16);
                                    while (a.length() < 4) {
                                        a = "0".concat(a);
                                    }
                                    while (b.length() < 4) {
                                        b = "0".concat(b);
                                    }
                                    while (d.length() < 8) {
                                        d = "0".concat(d);
                                    }
                                    while (e.length() < 4) {
                                        e = "0".concat(e);
                                    }
                                    while (f.length() < 4) {
                                        f = "0".concat(f);
                                    }
                                    String CheckData = "01B3000F1C" + a + b + d + e + f;
                                    String CheckSum = GetCheckSum(CheckData);
                                    ToastUtil.show(SimpleActivity.this, "正在寻找目标区域");
                                    textView_ComStatus.setText("呼叫状态：正在呼叫。。。。");
                                    kind_of_hujiao.setText("呼叫种类：海呼");
                                    hujiao_number.setVisibility(View.VISIBLE);
                                    hujiao_number.setText("参考点："+ cankaojing1 + "°" + cankaojing2 + "'"+"  " + cankaowei1 + "°" + cankaowei2 + "'");
                                    String data = GetSendData(CheckData, CheckSum);
                                    sendData(data);

                            }
                        }
                    });
                    builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ToastUtil.show(SimpleActivity.this, "取消海呼");
                        }
                    });
                    Dialog dialog1 = builder.create();
                    dialog1.show();
                    WindowManager.LayoutParams params = dialog1.getWindow().getAttributes();
                    params.width = 400;
                    params.height = 500;
                    dialog1.getWindow().setAttributes(params);
                    break;
                default:
                    break;
            }
        }
        if (event.getKeyCode() == 218 && event.getAction() == KeyEvent.ACTION_UP) {  //音量 GPS发按键识别
            switch (count) {
                case 1:
                    AlertDialog.Builder builder218 = new AlertDialog.Builder(SimpleActivity.this);
                    builder218.setIcon(R.drawable.ic_launcher_foreground);
                    builder218.setTitle("音量");
                    View view218 = LayoutInflater.from(SimpleActivity.this).inflate(R.layout.dialog_send_qunhu, null);
                    EditText yinliang= view218.findViewById(R.id.qunhuhao);
                    yinliang.setHint("设置音量大小(0-9)");
                    builder218.setView(view218);

                    builder218.setPositiveButton("确定", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            String a = yinliang.getText().toString().trim();
                            int b=Integer.parseInt(a);
                            if(b>9||b<0){
                                ToastUtil.show(SimpleActivity.this,"音量设置错误,请重新设置！");
                            }else {
                                String checkString="01B300043E"+"0"+a;
                                String checksumString= GetCheckSum(checkString);
                                String send_yinliang= GetSendData(checkString,checksumString);
                                sendData(send_yinliang);
                                ToastUtil.show(SimpleActivity.this,"音量设置为:"+a);
                            }
                        }
                    });
                    builder218.setNegativeButton("取消", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {

                        }
                    });
                    builder218.setOnKeyListener(new DialogInterface.OnKeyListener() {
                        @Override
                        public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                            if (keyCode == 142 && event.getAction() == KeyEvent.ACTION_UP) {
                                sendKeyCode(8);
                                Log.i("按键输入", "dispatchKeyEvent: 输入1");
                            }
                            if (keyCode == 132 && event.getAction() == KeyEvent.ACTION_UP) {
                                sendKeyCode(9);
                                Log.i("按键输入", "dispatchKeyEvent: 输入2");
                            }
                            if (keyCode == 133 && event.getAction() == KeyEvent.ACTION_UP) {
                                sendKeyCode(10);
                                Log.i("按键输入", "dispatchKeyEvent: 输入3");
                            }
                            if (keyCode == 134 && event.getAction() == KeyEvent.ACTION_UP) {
                                sendKeyCode(11);
                                Log.i("按键输入", "dispatchKeyEvent: 输入=4");
                            }
                            if (keyCode == 135 && event.getAction() == KeyEvent.ACTION_UP) {
                                sendKeyCode(12);
                                Log.i("按键输入", "dispatchKeyEvent: 输入5");
                            }
                            if (keyCode == 136 && event.getAction() == KeyEvent.ACTION_UP) {
                                sendKeyCode(13);
                                Log.i("按键输入", "dispatchKeyEvent: 输入6");
                            }
                            if (keyCode == 137 && event.getAction() == KeyEvent.ACTION_UP) {
                                sendKeyCode(14);
                            }
                            if (keyCode == 138 && event.getAction() == KeyEvent.ACTION_UP) {
                                sendKeyCode(15);
                                Log.i("按键输入", "dispatchKeyEvent: 输入8");
                            }
                            if (keyCode == 139 && event.getAction() == KeyEvent.ACTION_UP) {
                                sendKeyCode(16);
                                Log.i("按键输入", "dispatchKeyEvent: 输入9");
                            }
                            if (keyCode == 141 && event.getAction() == KeyEvent.ACTION_UP) {
                                sendKeyCode(7);
                                Log.i("按键输入", "dispatchKeyEvent: 输入0");
                            }
                            if (keyCode == 217 && event.getAction() == KeyEvent.ACTION_UP) { //退格按键，对应翻页
                                sendKeyCode(67);
                                Log.i("按键输入", "dispatchKeyEvent: 输入退格"); //keycode 67
                            }
                            if (keyCode == 216 && event.getAction() == KeyEvent.ACTION_UP) {
                                Button btn_neg = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_NEGATIVE);
                                btn_neg.performClick(); //点击取消
                            }
                            if (keyCode == 215 && event.getAction() == KeyEvent.ACTION_UP) {
                                Button btn_pos = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                                btn_pos.performClick(); //点击确定
                            }
                            Log.i("按键输入", "onKey: " + keyCode);
                            return false;
                        }
                    });
                    builder218.show();
                    break;
//                    Toast.makeText(SimpleActivity.this, "放大按键", Toast.LENGTH_SHORT).show();
//                    AlertDialog.Builder builder9 = new AlertDialog.Builder(SimpleActivity.this) .setIcon(R.drawable.ring_icon); //求救呼收
//                    builder9.setTitle("收到求救通话请求");
//                    View view2 = LayoutInflater.from(SimpleActivity.this).inflate(R.layout.dialog_send_qiujiu, null);
//                    builder9.setView(view2);
//
//                    builder9.setPositiveButton("接听", new DialogInterface.OnClickListener()
//                    {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which)
//                        {
//                            sendData(sendMingling_nodata(3, "35"));
//                            textView_ComStatus.setText("选呼通话中，按取消挂断");
//                            running = true;
//                        }
//                    });
//                    builder9.setNegativeButton("拒绝", new DialogInterface.OnClickListener()
//                    {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which)
//                        {
//                            sendData(sendMingling_nodata(3, "25"));
//                        }
//                    });
//                    builder9.setOnKeyListener(new DialogInterface.OnKeyListener() {
//                        @Override
//                        public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
//                            if (keyCode == 216 && event.getAction() == KeyEvent.ACTION_UP) { //取消按键,模拟点击屏幕取消按键的地点
//                                sendTouchEvent(540, 500);
//                                Log.i("按键输入", "dispatchKeyEvent: 输入取消");
//
//                            }
//                            if (keyCode == 215 && event.getAction() == KeyEvent.ACTION_UP) { //确认按键
//                                sendTouchEvent(600,490);
//                                Log.i("按键输入", "dispatchKeyEvent: 输入确认");
//                            }
//                            Log.i("按键输入", "onKey: "+keyCode);
//                            return false;
//                        }
//                    } );
//                    Dialog  dialog = builder9.create();
//                    dialog.show();
//                    WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
//                    params.width = 300;
//                    params.height = 280;
//                    dialog.getWindow().setAttributes(params);
//                    break;
                case 2:
                    AlertDialog.Builder builder3 = new AlertDialog.Builder(SimpleActivity.this);
                    builder3.setIcon(R.drawable.ic_launcher_foreground);
                    builder3.setTitle("输入信道号");
                    View view3 = LayoutInflater.from(SimpleActivity.this).inflate(R.layout.dialog_send_qunhu, null);
                    builder3.setView(view3);
                    builder3.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        EditText chuanweihufa = (EditText) view3.findViewById(R.id.qunhuhao);
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String a = chuanweihufa.getText().toString().trim();
                            int Length = 3 + a.length();
                            String Comand = "31";
                            sendData(sendMingling(Length, Comand, a));
                            Toast.makeText(SimpleActivity.this, "信道号设置" + a, Toast.LENGTH_SHORT).show();
                        }
                    });
                    builder3.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    builder3.setOnKeyListener(new DialogInterface.OnKeyListener() {
                        @Override
                        public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                            if (keyCode == 142 && event.getAction() == KeyEvent.ACTION_UP) {
                                sendKeyCode(8);
                                Log.i("按键输入", "dispatchKeyEvent: 输入1");
                            }
                            if (keyCode == 132 && event.getAction() == KeyEvent.ACTION_UP) {
                                sendKeyCode(9);
                                Log.i("按键输入", "dispatchKeyEvent: 输入2");
                            }
                            if (keyCode == 133 && event.getAction() == KeyEvent.ACTION_UP) {
                                sendKeyCode(10);
                                Log.i("按键输入", "dispatchKeyEvent: 输入3");
                            }
                            if (keyCode == 134 && event.getAction() == KeyEvent.ACTION_UP) {
                                sendKeyCode(11);
                                Log.i("按键输入", "dispatchKeyEvent: 输入=4");
                            }
                            if (keyCode == 135 && event.getAction() == KeyEvent.ACTION_UP) {
                                sendKeyCode(12);
                                Log.i("按键输入", "dispatchKeyEvent: 输入5");
                            }
                            if (keyCode == 136 && event.getAction() == KeyEvent.ACTION_UP) {
                                sendKeyCode(13);
                                Log.i("按键输入", "dispatchKeyEvent: 输入6");
                            }
                            if (keyCode == 137 && event.getAction() == KeyEvent.ACTION_UP) {
                                sendKeyCode(14);
                            }
                            if (keyCode == 138 && event.getAction() == KeyEvent.ACTION_UP) {
                                sendKeyCode(15);
                                Log.i("按键输入", "dispatchKeyEvent: 输入8");
                            }
                            if (keyCode == 139 && event.getAction() == KeyEvent.ACTION_UP) {
                                sendKeyCode(16);
                                Log.i("按键输入", "dispatchKeyEvent: 输入9");
                            }
                            if (keyCode == 141 && event.getAction() == KeyEvent.ACTION_UP) {
                                sendKeyCode(7);
                                Log.i("按键输入", "dispatchKeyEvent: 输入0");
                            }
                            if (keyCode == 217 && event.getAction() == KeyEvent.ACTION_UP) { //退格按键，对应翻页
                                sendKeyCode(67);
                                Log.i("按键输入", "dispatchKeyEvent: 输入退格"); //keycode 67
                            }
                            if (keyCode == 216 && event.getAction() == KeyEvent.ACTION_UP) {
                                Button btn_neg = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_NEGATIVE);
                                btn_neg.performClick(); //点击取消
                            }
                            if (keyCode == 215 && event.getAction() == KeyEvent.ACTION_UP) {
                                Button btn_pos = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                                btn_pos.performClick(); //点击确定
                            }
                            Log.i("按键输入", "onKey: " + keyCode);
                            return false;
                        }
                    });
                    builder3.show();
                    break;
                case 3:
                    AlertDialog.Builder builder2183 = new AlertDialog.Builder(SimpleActivity.this);
                    builder2183.setTitle("设置群号");
                    View view = LayoutInflater.from(SimpleActivity.this).inflate(R.layout.dialog_set_qunhao, null);
                    builder2183.setView(view);
                    EditText et_set = (EditText) view.findViewById(R.id.et_setqunhao);
                    builder2183.setOnKeyListener(new DialogInterface.OnKeyListener() {
                        @Override
                        public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                            if (keyCode == 142 && event.getAction() == KeyEvent.ACTION_UP) {
                                sendKeyCode(8);//对应数字1
                            }
                            if (keyCode == 132 && event.getAction() == KeyEvent.ACTION_UP) {
                                sendKeyCode(9);//对应数字2
                            }
                            if (keyCode == 133 && event.getAction() == KeyEvent.ACTION_UP) {
                                sendKeyCode(10);//对应数字3
                            }
                            if (keyCode == 134 && event.getAction() == KeyEvent.ACTION_UP) {
                                sendKeyCode(11);//对应数字4
                            }
                            if (keyCode == 135 && event.getAction() == KeyEvent.ACTION_UP) {
                                sendKeyCode(12);//对应数字5
                            }
                            if (keyCode == 136 && event.getAction() == KeyEvent.ACTION_UP) {
                                sendKeyCode(13);//对应数字6
                            }
                            if (keyCode == 137 && event.getAction() == KeyEvent.ACTION_UP) {
                                sendKeyCode(14);//对应数字7
                            }
                            if (keyCode == 138 && event.getAction() == KeyEvent.ACTION_UP) {
                                sendKeyCode(15);//对应数字8
                            }
                            if (keyCode == 139 && event.getAction() == KeyEvent.ACTION_UP) {
                                sendKeyCode(16);//对应数字9
                            }
                            if (keyCode == 141 && event.getAction() == KeyEvent.ACTION_UP) {
                                sendKeyCode(7);//对应数字10
                            }
                            if (keyCode == 217 && event.getAction() == KeyEvent.ACTION_UP) {
                                sendKeyCode(67); //退格按键，对应翻页
                            }
                            if (keyCode == 216 && event.getAction() == KeyEvent.ACTION_UP) {
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
                    builder2183.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String number = et_set.getText().toString();
                            if(number.length()<6){
                                ToastUtil.show(SimpleActivity.this, "设置失败,群号为6位！");
                            }else {
                                qunhao.setText("群号：" + number);
                                ToastUtil.show(SimpleActivity.this, "设置成功");
                                String CheckData = "01B3000627" + number;
                                String CheckSum = GetCheckSum(CheckData);
                                String data = GetSendData(CheckData, CheckSum);
                                sendData(data);
                            }
                        }
                    });
                    builder2183.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ToastUtil.show(SimpleActivity.this, "设置取消");
                        }
                    });
                    Dialog dialog2183 = builder2183.create();
                    dialog2183.show();
                    WindowManager.LayoutParams params = dialog2183.getWindow().getAttributes();
                    params.width = 300;
                    params.height = 180;
                    dialog2183.getWindow().setAttributes(params);
//                    String CheckData = "FEFCF8F001B3000332";
//                    String CheckSum = GetCheckSum(CheckData);
//                    String data = GetSendData(CheckData, CheckSum);
//                    sendData(data);
                    break;
                default:
                    break;
            }
        }
        if (event.getKeyCode() == 212 && event.getAction() == KeyEvent.ACTION_UP) {  //联系人  存储状态发按键识别
            switch (count) {
                case 1:
                    Intent intent = new Intent(SimpleActivity.this, ContactsActivity.class);
                    intent.putExtra("count_changlanguage",String.valueOf(changlanguage));
                    startActivityForResult(intent, 9);
                    break;
                case 2:          //发送短信
                    //   sendData(sendMingling(4, "3E","00")); //输入
                    if(running)
                    {
                        Toast.makeText(this,"通话占用中！请先取消当前通话！",Toast.LENGTH_SHORT).show();
                        return false;
                    }
                    Intent intent2 = new Intent(SimpleActivity.this, SendMessageActivity.class);
                    intent2.putExtra("count_changlanguage",String.valueOf(changlanguage));
                    intent2.putExtra("kind","01");
                    startActivityForResult(intent2, 9);
                    // Toast.makeText(SimpleActivity.this, "联系人按键按下", Toast.LENGTH_SHORT).show();
                    break;
                case 3:          //气象呼
                    if(running)
                    {
                        Toast.makeText(this,"通话占用中！请先取消当前通话！",Toast.LENGTH_SHORT).show();
                        return false;
                    }
                    Toast.makeText(SimpleActivity.this, "气象呼叫指令发送", Toast.LENGTH_LONG).show();
                    String CheckData = "01B3000318";
                    String CheckSum = GetCheckSum(CheckData);
                    String data = GetSendData(CheckData, CheckSum);
                    sendData(data);
                    textView_ComStatus.setText("呼叫状态：呼叫中");
                    kind_of_hujiao.setText("呼叫种类：气象呼");
                    qixiang = true;
                    break;
                default:
                    break;
            }
        }
        if (event.getKeyCode() == 41 && event.getAction() == KeyEvent.ACTION_UP) {  //求救呼发按键识别
            AlertDialog.Builder builder2 = new AlertDialog.Builder(SimpleActivity.this);
            builder2.setIcon(R.drawable.ic_launcher_foreground);
            builder2.setTitle("求救呼发");
            View view2 = LayoutInflater.from(SimpleActivity.this).inflate(R.layout.dialog_send_qiujiu, null);
            builder2.setView(view2);

            builder2.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                EditText chuanweihufa = (EditText) view2.findViewById(R.id.qunhuhao);

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String help = "";
                    sendData(sendMingling_nodata(3, "11"));
                }
            });
            builder2.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });
            builder2.setOnKeyListener(new DialogInterface.OnKeyListener() {
                @Override
                public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                    if (keyCode == 216 && event.getAction() == KeyEvent.ACTION_UP) {
                        Button btn_neg = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_NEGATIVE);
                        btn_neg.performClick(); //点击取消
                    }
                    if (keyCode == 215 && event.getAction() == KeyEvent.ACTION_UP) {
                        Button btn_pos = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                        btn_pos.performClick(); //点击确定
                    }
                    Log.i("按键输入", "onKey: " + keyCode);
                    return false;
                }
            });
            builder2.show();

        }
        if (event.getKeyCode() == 216 && event.getAction() == KeyEvent.ACTION_UP) {  //挂断电话操作
            if (running) {
                running = false;
                seconds = 0;
                pttanjian.setText(" ");
                Toast.makeText(SimpleActivity.this, "通话挂断", Toast.LENGTH_LONG).show();
            }
            kind_of_hujiao.setText("呼叫种类：无");
            hujiao_number.setVisibility(View.GONE);
            textView_ComStatus.setText("呼叫状态：无呼叫");
            qiujiuflag = false;
            String CheckData = "01B3000325";
            String CheckSum = GetCheckSum(CheckData);
            String data = GetSendData(CheckData, CheckSum);
            sendData(data);
        }   //挂断电话操作
        return super.dispatchKeyEvent(event);
    }

    /**
     * 模拟按键输入，转译输入按键的键值
     *  https://blog.csdn.net/CJohn1994/article/details/123996744
     * @param keyCode 转移后的按键键值，参考Shell中按键值表
     */

    private void sendKeyCode(final int keyCode) {
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
    } //按键转义为 键盘的按键

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 212:
                if (resultCode == RESULT_OK) {
                    String returnedData = data.getStringExtra("contactname");
                    String a = null;
                    Log.d("database", "onActivityResult: " + returnedData);
                    if (contactsmap.containsKey(returnedData)) {
                        a = contactsmap.get(returnedData);
                        Log.d("database", "a: " + a);
                    } else {
                        a = "1111111111";
                    }

                    String b = "233";
                    StringBuilder sb = new StringBuilder(a);//构造一个StringBuilder对象
                    StringBuilder sb2 = new StringBuilder(b);//构造一个StringBuilder对象
                    sb.insert(8, '0');
                    sb2.insert(2, '0');
                    //    将输入的用户名和密码打印出来
                    String data2 = sb.toString() + sb2.toString();
                    int Length = 10;
                    String Comand = "16";
                    sendData(sendMingling(Length, Comand, data2));
                    Toast.makeText(SimpleActivity.this, "终端号: " + a + ", 信道号: " + b, Toast.LENGTH_SHORT).show();
                    textView_ComStatus.setText("正在呼叫。。。。");
                }
            case 9:
                if(data.getStringExtra("RESULT")!=null){
                    String result=data.getStringExtra("RESULT");
                    changlanguage=Integer.valueOf(result);
                }
                break;

            default:
                break;
        }
        switch (resultCode) {
            case 33:
                if(data.getStringExtra("xuanhu")!=null){
                    sendData(data.getStringExtra("xuanhu"));
                    textView_ComStatus.setText("正在呼叫。。。。");
                }
                break;
            case 34:
                Intent intent2 = new Intent(SimpleActivity.this, SendMessageActivity.class);
                intent2.putExtra("count_changlanguage",String.valueOf(changlanguage));
                intent2.putExtra("ZHONGDUANHAO",data.getStringExtra("ZHONGDUANHAO"));
                startActivityForResult(intent2, 9);
                break;
            default:
                break;
        }
    }


    private void initView() {
        player = (CommenPlayer) findViewById(R.id.player);
        webView = (WebView) findViewById(R.id.map);
        aispower = (ImageView) findViewById(R.id.ais_power);
        videopower = (ImageView) findViewById(R.id.video_power);
        diantaihao = (TextView) findViewById(R.id.diantaihao);
        xingdaohao = (TextView) findViewById(R.id.xindaohao);
        jingdu_main = (TextView) findViewById(R.id.chuanwei1);
        weidu_main = (TextView) findViewById(R.id.chuanwei2);
    }

    private void initPlayer() {
        player.setLive(false);
        player.setOnNetListener(new OnNetListener() {
            @Override
            public void onIgnoreMobileNet() {
                ignoreNet = true;
                isVideo = false;
            }
        }).setOnPlayerListener(new IPlayerListener() {
            @Override
            public void onLoading() {
                player.getControl().setState(ControlLayout.STATE_LOADING);
            }

            @Override
            public void onCompletion(IMediaPlayer mp) {
                player.getControl().setState(ControlLayout.STATE_COMPLETION);
            }

            @Override
            public void onPrepared(IMediaPlayer mp) {
                if (!ignoreNet && NetCompat.getStatus() == NetState.CONNECTED_MOBILE) {
                    player.pause();
                    player.getControl().setState(ControlLayout.STATE_MOBILE_NET);
                } else {
                    player.getControl().setState(ControlLayout.STATE_PREPARED);
                }
            }

            @Override
            public boolean onError(IMediaPlayer mp, int what, int extra) {
                player.getControl().setState(ControlLayout.STATE_ERROR);
                return false;
            }

            @Override
            public boolean onInfo(IMediaPlayer mp, int what, int extra) {
                return false;
            }

            @Override
            public void onVideoSizeChanged(IMediaPlayer mp, int width, int height, int sarNum, int sarDen) {

            }
        });
        player.play(getResources().getString(R.string.url1));
        Log.i("播放打点", "initPlayer:开始播放 ");
    }  //只需要修改播放的地址，即可

    @Override
    protected void onStart() {
        super.onStart();
        //initContactsMap();
        EventBus.getDefault().register(this);
    }  //开启事件总线


//    protected void initFragment() {
//        FragmentManager fragmentManager = getFragmentManager();
//        mLogFragment = (LogFragment) fragmentManager.findFragmentById(R.id.log_fragment);
//    }

//    @Override
//    public boolean onNavigateUp() {
//        finish();
//        return super.onNavigateUp();
//    }  //未使用


    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }  //无需修改，关闭事件总线

    /**
     * 带数据的命令发
     *
     * @param
     * @return
     */
    private static String sendMingling(int length, String m_comand, String s) {
        StringBuilder sm = new StringBuilder();
        StringBuilder checksum = new StringBuilder();
        checksum.append("01B300").append(intToHex(length)).append(m_comand).append(StringToBCDS(s));
        sm.append("FEFCF8F001B300").append(intToHex(length)).append(m_comand)
                .append(StringToBCDS(s)).append(getCheckSum(checksum.toString(), 4)).append("FCFEF0F8");
        return sm.toString();
    }

    /**
     * 不带数据的命令发
     *
     * @param
     * @return
     */
    private static String sendMingling_nodata(int length, String m_comand) {
        StringBuilder sm = new StringBuilder();
        StringBuilder checksum = new StringBuilder();
        checksum.append("01B300").append(intToHex(length)).append(m_comand);//b3改11
        sm.append("FEFCF8F001B300").append(intToHex(length)).append(m_comand)
                .append(getCheckSum(checksum.toString(), 4)).append("FCFEF0F8"); //b3改11
        return sm.toString();
    }

    /**
     * 十进制转为为16进制字符串
     *
     * @param n
     * @return
     */
    private static String intToHex(int n) {
        //StringBuffer s = new StringBuffer();
        StringBuilder sb = new StringBuilder(8);
        String a;
        char[] b = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        if (n <= 15) {
            sb.append(0).append(b[n]);
            return sb.toString();
        }
        while (n != 0) {
            sb = sb.append(b[n % 16]);
            n = n / 16;
        }
        a = sb.reverse().toString();
        return a;
    }

    /**
     * 带拨号码转化问BCD字符串
     *
     * @param
     * @return
     */
    private static String StringToBCDS(String s) {
        StringBuilder ss = new StringBuilder();
        int leng = s.length();
        if (leng % 2 != 0) {
            ss.append('0');
            ss.append(s.charAt(leng - 1));
            leng--;
        }

        for (int i = leng - 1; i > 0; i = i - 2) {
            ss.append(s.charAt(i - 1));
            ss.append(s.charAt(i));
        }
        return ss.toString();
    }

    /**
     * 和校验，取最后round位
     *
     * @param round 取后面多少位
     */
    private static String getCheckSum(String cmd, int round) {
        int lenth = cmd.length() / 2;
        int cmmSum = 0;
        for (int i = 0; i < lenth; i++) {
            //每两位转为16进制进行计算
            int c = Integer.valueOf(cmd.substring(0, 2), 0x10);
            cmd = cmd.substring(2);
            cmmSum = cmmSum + c;
        }
        String newString = Integer.toHexString(cmmSum);
        newString = "00000000000000000000000" + newString;//这里黑科技
        //这里获得我们需要返回的位数
        newString = newString.substring(newString.length() - round);
        return newString;
    }

    /**
     * 串口接收数据处理，使用EventBus总线
     *
     * @param message
     */
    private StringBuilder temp = new StringBuilder(); //临时缓存的字符串
    private String data = "";  //接收的指令
    private boolean data_use = false;
    private String xuliehao;
    private String tianxian;
    private String dianya;
    private int Adi=0;
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Subscribe(threadMode = ThreadMode.MAIN) //675 440  740 450
    public void onMessageEvent(IMessage message) {
        Map<String, Object> map = Serial_Manage(message.getMessage(), temp, data, data_use);
        data = (String) map.get("data");
        data_use = (boolean) map.get("data_use");

        if (data_use) {
            String cmd = data.substring(16, 18);
            switch (cmd) {
                case "26":  //本船信息
                    String dian_int = data.substring(19, 28);
                    xuliehao=data.substring(28,36);
                    tianxian=data.substring(46,48);
                    dianya = String.valueOf(Integer.parseInt(data.substring(48,50),16));
                    String xingdao = data.substring(43, 46);
                    String qunhao_get =data.substring(36,42);
                    diantaihao.setText("终端号:" + dian_int);
                    xingdaohao.setText("信道号:" + xingdao);
                    qunhao.setText("群号:"+qunhao_get);
                    data = "";// data使用完 清空
                    data_use = false;
                    break;
                case "99":  //信道号
                    String s99 = data.substring(19, 22);
                    int i = Integer.parseInt(s99, 16);
                    s99 = String.valueOf(i);
                    while (s99.length() < 3) {
                        s99 = "0".concat(s99);
                    }
                    xingdaohao.setText("信道号:" + s99);
                    data = "";// data使用完 清空
                    data_use = false;
                    break;
                case "3F":
                    String s3F = data.substring(18, 20);
                    if (s3F.equals("00")) {
                        pttanjian.setText("正在发射中。。。");
                        //sendTouchEvent(100, 100);
                    } else {
                        pttanjian.setText("正在接收中。。。");
                    }
                    data = "";// data使用完 清空
                    data_use = false;
                    break;
                case "A4":
                    ToastUtil.show(SimpleActivity.this, "信道忙，请稍后重试!");
                    sendKeyCode(216);
                    data = "";// data使用完 清空
                    data_use = false;
                    break;
                case "35":
                    running = true;
                    data = "";// data使用完 清空
                    data_use = false;
                    break;
                case "12":   //求救收
                    AlertDialog.Builder builder12 = new AlertDialog.Builder(SimpleActivity.this);
                    builder12.setIcon(R.drawable.ring_icon);
                    builder12.setTitle("收到求救呼叫");
                    View view2 = LayoutInflater.from(SimpleActivity.this).inflate(R.layout.dialog_send_qiujiu, null);
                    TextView tv_zhongduan = (TextView) view2.findViewById(R.id.zhongduanhao);
                    TextView tv_jingdu = (TextView) view2.findViewById(R.id.jingdu);
                    TextView tv_weidu = (TextView) view2.findViewById(R.id.weidu);
                    Map<String, String> map12 = GetGps(data.substring(28, 40),xiangxian,false);
                    String jd12 = map12.get("jd");
                    String wd12 = map12.get("wd");
                    tv_jingdu.setText(jd12);
                    tv_weidu.setText(wd12);
                    tv_zhongduan.setText("来自终端号：" + data.substring(19, 28) + " 的呼叫");
                    kind_of_hujiao.setText("呼叫种类：求救");
                    hujiao_number.setVisibility(View.VISIBLE);
                    hujiao_number.setText("对方船号："+data.substring(19, 28));
                    builder12.setView(view2);
                    builder12.setOnKeyListener(new DialogInterface.OnKeyListener() {
                        @Override
                        public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                            if (keyCode == 216 && event.getAction() == KeyEvent.ACTION_UP) { //取消按键
                                kind_of_hujiao.setText("呼叫种类：无");
                                hujiao_number.setVisibility(View.GONE);
                                Button btn_neg = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_NEGATIVE);
                                btn_neg.performClick(); //点击取消
                            }
                            return false;
                        }
                    });
                    builder12.setNegativeButton("拒绝", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            kind_of_hujiao.setText("呼叫种类：无");
                            hujiao_number.setVisibility(View.GONE);
                            String CheckData = "01B3000325";
                            String CheckSum = GetCheckSum(CheckData);
                            String data = GetSendData(CheckData, CheckSum);
                            sendData(data);
                        }
                    });
                    dialogtemp = builder12.create();
//                    if (!dialog12.isShowing()) {
//                        dialog12.show();
//                    }
                    dialogtemp.show();
                    //sendTouchEvent(100, 100);
                    data_use = false;
                    data = "";
                    break;
                case "19":   //气象收
                    AlertDialog.Builder builder19 = new AlertDialog.Builder(SimpleActivity.this);
                    builder19.setIcon(R.drawable.ring_icon);
                    builder19.setTitle("收到气象呼叫");
                    View view3 = LayoutInflater.from(SimpleActivity.this).inflate(R.layout.dialog_send_qiujiu, null);
                    TextView tv_zhongduan1 = (TextView) view3.findViewById(R.id.zhongduanhao);
                    TextView tv_jingdu1 = (TextView) view3.findViewById(R.id.jingdu);
                    TextView tv_weidu1 = (TextView) view3.findViewById(R.id.weidu);
                    Map<String, String> map19 = GetGps(data.substring(28, 40),xiangxian,false);
                    String jd19 = map19.get("jd");
                    String wd19 = map19.get("wd");
                    tv_jingdu1.setText(jd19);
                    tv_weidu1.setText(wd19);
                    tv_zhongduan1.setText("来自终端号：" + data.substring(19, 28) + " 的呼叫");
                    kind_of_hujiao.setText("呼叫种类：气象");
                    hujiao_number.setVisibility(View.VISIBLE);
                    hujiao_number.setText("对方船号："+data.substring(19, 28));
                    builder19.setView(view3);
                    builder19.setOnKeyListener(new DialogInterface.OnKeyListener() {
                        @Override
                        public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                            if (keyCode == 216 && event.getAction() == KeyEvent.ACTION_UP) { //取消按键
                                kind_of_hujiao.setText("呼叫种类：无");
                                hujiao_number.setVisibility(View.GONE);
                                Button btn_neg = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_NEGATIVE);
                                btn_neg.performClick(); //点击取消
                            }
                            return false;
                        }
                    });
                    builder19.setNegativeButton("拒绝", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            kind_of_hujiao.setText("呼叫种类：无");
                            hujiao_number.setVisibility(View.GONE);
                            String CheckData = "01B3000325";
                            String CheckSum = GetCheckSum(CheckData);
                            String data = GetSendData(CheckData, CheckSum);
                            sendData(data);
                        }
                    });
                    dialogtemp = builder19.create();
//                    if (!dialog12.isShowing()) {
//                        dialog12.show();
//                    }
                    dialogtemp.show();
                    //sendTouchEvent(100, 100);
                    data_use = false;
                    data = "";
                    break;
                case "33":
                    Map<String, String> map31 = GetGps(data.substring(18, 30),xiangxian,true);
                    gps_time = Long.parseLong(data.substring(36, 48));
                    String jd31 = map31.get("jd");
                    String wd31 = map31.get("wd");
                    jingdu_main.setText("经度:" + jd31);
                    weidu_main.setText("纬度:" + wd31);
                    hangsu.setText("航速：" + Integer.parseInt(data.substring(34, 36), 16) + "海里/时");
                    data_use = false;
                    data = "";
                    break;
                case "17":  //选呼收
                    AlertDialog.Builder builder17 = new AlertDialog.Builder(SimpleActivity.this);
                    builder17.setIcon(R.drawable.ring_icon);
                    builder17.setTitle("收到选呼");
                    View view17 = LayoutInflater.from(SimpleActivity.this).inflate(R.layout.dialog_rev_xuanhu, null);
                    TextView zhongduanhao17 = (TextView) view17.findViewById(R.id.zhongduanhao);
                    TextView xindaohao17 = (TextView) view17.findViewById(R.id.xindaohao);
                    zhongduanhao17.setText("终端号：" + data.substring(19, 28));
                    int xindaohao = Integer.parseInt(data.substring(28, 32), 16);
                    String xindao = String.valueOf(xindaohao);
                    if (xindao.length() < 3) {
                        xindao = "0".concat(xindao);
                    }
                    kind_of_hujiao.setText("呼叫种类：选呼");
                    hujiao_number.setVisibility(View.VISIBLE);
                    hujiao_number.setText("对方船号："+data.substring(19, 28));
                    xindaohao17.setText("信道号：" + xindao);
                    builder17.setView(view17);
                    builder17.setOnKeyListener(new DialogInterface.OnKeyListener() {
                        @Override
                        public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                            if (keyCode == 216 && event.getAction() == KeyEvent.ACTION_UP) { //取消按键
                                kind_of_hujiao.setText("呼叫种类：无");
                                hujiao_number.setVisibility(View.GONE);
                                Button btn_neg = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_NEGATIVE);
                                btn_neg.performClick(); //点击取消
                            }
                            return false;
                        }
                    });
                    builder17.setNegativeButton("拒绝", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            kind_of_hujiao.setText("呼叫种类：无");
                            hujiao_number.setVisibility(View.GONE);
                            String CheckData = "01B3000325";
                            String CheckSum = GetCheckSum(CheckData);
                            String data = GetSendData(CheckData, CheckSum);
                            sendData(data);
                        }
                    });
                    dialogtemp = builder17.create();
                    dialogtemp.show();
                    data_use = false;
                    data = "";
                    break;
                case "14":  //群呼收
                    AlertDialog.Builder builder14 = new AlertDialog.Builder(SimpleActivity.this);
                    builder14.setIcon(R.drawable.ic_launcher_foreground);
                    builder14.setTitle("收到群呼消息");
                    View view14 = LayoutInflater.from(SimpleActivity.this).inflate(R.layout.dialog_rev_qunhu, null);
                    builder14.setView(view14);
                    TextView zhongduan14 = (TextView) view14.findViewById(R.id.zhongduanhao);
                    TextView xindao14 = (TextView) view14.findViewById(R.id.xindaohao);
                    zhongduan14.setText("主叫终端号："+data.substring(19,28));
                    xindao14.setText("信道号："+Integer.parseInt(data.substring(28,32),16));
                    kind_of_hujiao.setText("呼叫种类：群呼");
                    hujiao_number.setVisibility(View.VISIBLE);
                    hujiao_number.setText("对方船号："+data.substring(19,28));
                    builder14.setNegativeButton("拒绝", new DialogInterface.OnClickListener() //不接串口设备不能在这里面写接收语句，否则会自发自收
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            kind_of_hujiao.setText("呼叫种类：无");
                            hujiao_number.setVisibility(View.GONE);
                            String CheckData = "01B3000325";
                            String CheckSum = GetCheckSum(CheckData);
                            String data = GetSendData(CheckData, CheckSum);
                            sendData(data);
                        }
                    });
                    builder14.setOnKeyListener(new DialogInterface.OnKeyListener() {
                        @Override
                        public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                            if (keyCode == 216 && event.getAction() == KeyEvent.ACTION_UP) { //取消按键,模拟点击屏幕取消按键的地点
                                kind_of_hujiao.setText("呼叫种类：无");
                                hujiao_number.setVisibility(View.GONE);
                                Button btn_neg = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_NEGATIVE);
                                btn_neg.performClick(); //点击取消
                            }
                            return false;
                        }
                    });
                    dialogtemp = builder14.create();
                    dialogtemp.show();
                    data="";// data使用完 清空
                    data_use=false;
                    break;
                case "32":  //经度、纬度
                    StringBuilder data1=new StringBuilder();
                    data1.append(xiangxian);
                    String jd= xiangxian+jingdu_main.getText().toString().substring(3,6)+jingdu_main.getText().toString().substring(7,9)+jingdu_main.getText().toString().substring(10,12);
                    data1.append(Integer.toHexString(Integer.valueOf(jd)).toUpperCase());
                    String wd=weidu_main.getText().toString().substring(3,5)+weidu_main.getText().toString().substring(6,8)+weidu_main.getText().toString().substring(9,11);
                    data1.append(Integer.toHexString(Integer.valueOf(wd)).toUpperCase());
                    data1.append(timeupdate.substring(0, 2)+timeupdate.substring(2, 4)+timeupdate.substring(4, 6));
                    data1.append(timeupdate.substring(6, 8)+timeupdate.substring(8, 10)+timeupdate.substring(10, 12));

                    String checkdata="01B3000F31"+data1.toString();
                    String checksum=GetCheckSum(checkdata);
                    String getsenddata=GetSendData(checkdata,checksum);
                    sendData(getsenddata);
                    data="";
                    data_use=false;
                    break;
                case "01":  //短信单收
                    String msg_kind = data.substring(52,54);
                    if(msg_kind.equals("01")){
                        Map<String, String> map01=GetGps(data.substring(28,40),data.substring(28,29),false);
                        MsgInfo msgInfo=new MsgInfo(-1,data.substring(19,28),unicodeToString(data.substring(54,data.length()-12)),
                                "经度:"+map01.get("jd"),"纬度:"+map01.get("wd"),"时间"+timeupdate.substring(6, 8) + ":" + timeupdate.substring(8, 10) + ":"
                                + timeupdate.substring(10, 12),dataUpdate.getText().toString(),1,0);
                        dao.add(msgInfo);
                        AlertDialog.Builder builder01 = new AlertDialog.Builder(SimpleActivity.this);
                        builder01.setIcon(R.drawable.ring_icon);
                        builder01.setTitle("收到短信 来自：");
                        View view18 = LayoutInflater.from(SimpleActivity.this).inflate(R.layout.dialog_msg_rev, null);
                        TextView  tv_number_rev= (TextView) view18.findViewById(R.id.tv_number_rev);
                        TextView tv_gpsjd_rev = (TextView) view18.findViewById(R.id.tv_gpsjd_rev);
                        TextView tv_gpswd_rev = (TextView) view18.findViewById(R.id.tv_gpswd_rev);
                        TextView tv_msg = (TextView) view18.findViewById(R.id.tv_msg_rev);
                        TextView tv_time_rev = (TextView) view18.findViewById(R.id.tv_time_rev);
                        tv_number_rev.setText("终端号:"+data.substring(19,28));
                        String tempzhongduan=data.substring(19,28);
                        tv_gpsjd_rev.setText("经度:"+map01.get("jd"));
                        tv_gpswd_rev.setText("纬度:"+map01.get("wd"));
                        tv_msg.setText("内容:"+unicodeToString(data.substring(54,data.length()-12)));
                        String tempcontent=unicodeToString(data.substring(54,data.length()-12));
                        tv_time_rev.setText("时间"+timeupdate.substring(6, 8) + ":" + timeupdate.substring(8, 10) + ":" + timeupdate.substring(10, 12));

                        data_use = false;
                        data = "";

                        builder01.setView(view18);
                        builder01.setOnKeyListener(new DialogInterface.OnKeyListener() {
                            @Override
                            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                                if (keyCode == 216 && event.getAction() == KeyEvent.ACTION_UP) { //取消按键
                                    Button btn_neg = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_NEGATIVE);
                                    btn_neg.performClick(); //点击取消
                                }
                                if (keyCode == 215 && event.getAction() == KeyEvent.ACTION_UP) {
                                    Button btn_neg = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_NEGATIVE);

                                    Button btn_pos = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                                    if(btn_neg.isSelected())
                                    {
                                        btn_neg.performClick(); //点击取消
                                    }
                                    if(btn_pos.isSelected())
                                    {
                                        btn_pos.performClick(); //点击确定
                                    }
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
                                Intent intent01 = new Intent(SimpleActivity.this, message_show.class);
                                intent01.putExtra("type","短信");
                                intent01.putExtra("tv_msg_zhongduanhao","终端号:"+tempzhongduan);
                                intent01.putExtra("tv_gpsjd_rev","经度:"+map01.get("jd"));
                                intent01.putExtra("tv_gpswd_rev","纬度:"+map01.get("wd"));
                                intent01.putExtra("tv_msg","内容:"+tempcontent);
                                intent01.putExtra("tv_time_rev","时间"+timeupdate.substring(6, 8) + ":" + timeupdate.substring(8, 10) + ":" + timeupdate.substring(10, 12));
                                intent01.putExtra("rili",dataUpdate.getText().toString());
                                startActivity(intent01);
                            }
                        });
                        Dialog dialog01 = builder01.create();
                        dialog01.show();
                    }else if(msg_kind.equals("02")){
                        ToastUtil.showOne(this, "收到一条广告！");
                        switch(Adi){
                            case 0:
                                alist.set(0,unicodeToString(data.substring(54,data.length()-12)));
                                alist2.set(4,unicodeToString(data.substring(54,data.length()-12)));
                                alist3.set(3,unicodeToString(data.substring(54,data.length()-12)));
                                alist4.set(2,unicodeToString(data.substring(54,data.length()-12)));
                                alist5.set(1,unicodeToString(data.substring(54,data.length()-12)));
                                Adi++;
                                break;
                            case 1:
                                alist.set(1,unicodeToString(data.substring(54,data.length()-12)));
                                alist2.set(0,unicodeToString(data.substring(54,data.length()-12)));
                                alist3.set(4,unicodeToString(data.substring(54,data.length()-12)));
                                alist4.set(3,unicodeToString(data.substring(54,data.length()-12)));
                                alist5.set(2,unicodeToString(data.substring(54,data.length()-12)));
                                Adi++;
                                break;
                            case 2:
                                alist.set(2,unicodeToString(data.substring(54,data.length()-12)));
                                alist2.set(1,unicodeToString(data.substring(54,data.length()-12)));
                                alist3.set(0,unicodeToString(data.substring(54,data.length()-12)));
                                alist4.set(4,unicodeToString(data.substring(54,data.length()-12)));
                                alist5.set(3,unicodeToString(data.substring(54,data.length()-12)));
                                Adi++;
                                break;
                            case 3:
                                alist.set(3,unicodeToString(data.substring(54,data.length()-12)));
                                alist2.set(2,unicodeToString(data.substring(54,data.length()-12)));
                                alist3.set(1,unicodeToString(data.substring(54,data.length()-12)));
                                alist4.set(0,unicodeToString(data.substring(54,data.length()-12)));
                                alist5.set(4,unicodeToString(data.substring(54,data.length()-12)));
                                Adi++;
                                break;
                            case 4:
                                alist.set(4,unicodeToString(data.substring(54,data.length()-12)));
                                alist2.set(3,unicodeToString(data.substring(54,data.length()-12)));
                                alist3.set(2,unicodeToString(data.substring(54,data.length()-12)));
                                alist4.set(1,unicodeToString(data.substring(54,data.length()-12)));
                                alist5.set(0,unicodeToString(data.substring(54,data.length()-12)));
                                Adi=0;
                                break;
                        }

                    }else {
                        Log.e("TAG","年检信息");
                    }


                    break;
                case "05"://图片
                    Map<String, String> map01=GetGps(data.substring(28,40),data.substring(28,29),false);
                    AlertDialog.Builder builder01 = new AlertDialog.Builder(SimpleActivity.this);
                    builder01.setTitle("收到图片 来自：");
                    View view18 = LayoutInflater.from(SimpleActivity.this).inflate(R.layout.dialog_msg_rev, null);
                    TextView  tv_number_rev= (TextView) view18.findViewById(R.id.tv_number_rev);
                    TextView tv_gpsjd_rev = (TextView) view18.findViewById(R.id.tv_gpsjd_rev);
                    TextView tv_gpswd_rev = (TextView) view18.findViewById(R.id.tv_gpswd_rev);
                    TextView tv_time_rev = (TextView) view18.findViewById(R.id.tv_time_rev);
                    tv_number_rev.setText("终端号:"+data.substring(19,28));
                    String tempzhongduan=data.substring(19,28);
                    tv_gpsjd_rev.setText("经度:"+map01.get("jd"));
                    tv_gpswd_rev.setText("纬度:"+map01.get("wd"));
                    tv_time_rev.setText("时间"+timeupdate.substring(6, 8) + ":" + timeupdate.substring(8, 10) + ":" + timeupdate.substring(10, 12));
                    bitmap = recv_photo(data.substring(52,data.length()-12));
                    data_use = false;
                    data = "";
                    builder01.setIcon(new BitmapDrawable(this.getResources(),bitmap));
                    builder01.setView(view18);
                    builder01.setOnKeyListener(new DialogInterface.OnKeyListener() {
                        @Override
                        public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                            if (keyCode == 216 && event.getAction() == KeyEvent.ACTION_UP) { //取消按键
                                Button btn_neg = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_NEGATIVE);
                                btn_neg.performClick(); //点击取消
                            }
                            if (keyCode == 215 && event.getAction() == KeyEvent.ACTION_UP) {
                                Button btn_neg = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_NEGATIVE);

                                Button btn_pos = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                                if(btn_neg.isSelected())
                                {
                                    btn_neg.performClick(); //点击取消
                                }
                                if(btn_pos.isSelected())
                                {
                                    btn_pos.performClick(); //点击确定
                                }
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
                            Intent intent01 = new Intent(SimpleActivity.this, message_show.class);
                            intent01.putExtra("type","图片");
                            intent01.putExtra("tv_msg_zhongduanhao","终端号:"+tempzhongduan);
                            intent01.putExtra("tv_gpsjd_rev","经度:"+map01.get("jd"));
                            intent01.putExtra("tv_gpswd_rev","纬度:"+map01.get("wd"));
                            intent01.putExtra("tv_time_rev",timeupdate.substring(6, 8) + ":" + timeupdate.substring(8, 10) + ":" + timeupdate.substring(10, 12));
                            intent01.putExtra("rili",dataUpdate.getText().toString());
                            intent01.putExtra("data.substring(19,28)",tempzhongduan);
                            intent01.putExtra("timeupdate.substring(6, 8)",timeupdate.substring(6, 8));
                            intent01.putExtra("timeupdate.substring(8, 10)",timeupdate.substring(8, 10));
                            intent01.putExtra("timeupdate.substring(10, 12)",timeupdate.substring(10, 12));
                            startActivity(intent01);
                        }
                    });
                    Dialog dialog01 = builder01.create();
                    dialog01.show();
                    break;
                case "30":
                    ToastUtil.showOne(this, "设备内部通信错误，请重试！");
                    sendKeyCode(216);
                    data_use = false;
                    data = "";
                    break;
                case "29":
                    ToastUtil.showOne(this, "连接失败，请重试！");
                    sendKeyCode(216);
                    data_use = false;
                    data = "";
                    break;
                case "1B"://全呼收
                    String zhongduanhao1B=data.substring(19,28);
                    String xindao1B=data.substring(28,32);
                    String zhujiaoxuliehao1B=data.substring(32,40);
                    break;
                case "1D"://海呼收
                    AlertDialog.Builder builder1D = new AlertDialog.Builder(SimpleActivity.this);
                    builder1D.setIcon(R.drawable.ring_icon);
                    builder1D.setTitle("收到海呼");
                    View view1D = LayoutInflater.from(SimpleActivity.this).inflate(R.layout.dialog_rev_xuanhu, null);
                    TextView zhongduanhao1D = (TextView) view1D.findViewById(R.id.zhongduanhao);
                    TextView xindaohao1D = (TextView) view1D.findViewById(R.id.xindaohao);
                    zhongduanhao1D.setText("终端号：" + data.substring(19, 28));
                    int xindaohao2 = Integer.parseInt(data.substring(28, 32), 16);
                    String xindao2 = String.valueOf(xindaohao2);
                    if (xindao2.length() < 3) {
                        xindao2 = "0".concat(xindao2);
                    }
                    kind_of_hujiao.setText("呼叫种类：海呼");
                    hujiao_number.setVisibility(View.VISIBLE);
                    hujiao_number.setText("对方船号："+data.substring(19, 28));
                    xindaohao1D.setText("信道号：" + xindao2);
                    builder1D.setView(view1D);
                    builder1D.setOnKeyListener(new DialogInterface.OnKeyListener() {
                        @Override
                        public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                            if (keyCode == 216 && event.getAction() == KeyEvent.ACTION_UP) { //取消按键
                                kind_of_hujiao.setText("呼叫种类：无");
                                hujiao_number.setVisibility(View.GONE);
                                Button btn_neg = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_NEGATIVE);
                                btn_neg.performClick(); //点击取消
                            }
                            return false;
                        }
                    });
                    builder1D.setNegativeButton("拒绝", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            kind_of_hujiao.setText("呼叫种类：无");
                            hujiao_number.setVisibility(View.GONE);
                            String CheckData = "01B3000325";
                            String CheckSum = GetCheckSum(CheckData);
                            String data = GetSendData(CheckData, CheckSum);
                            sendData(data);
                        }
                    });
                    dialogtemp = builder1D.create();
                    dialogtemp.show();
                    data_use = false;
                    data = "";
                    break;
                default:
                    break;
            }
        }

    }  //从getMessage()方法获取串口接收数据，后续进行处理

    private String unicodeToString(String unicode) {
        StringBuffer string = new StringBuffer();
        String hex;
        for (int i = 0; i < unicode.length()/4; i++) {
            hex =unicode.substring(i*4,i*4+4);
            int data = Integer.parseInt(hex, 16);
            string.append((char) data);
        }
        return string.toString();
    }

    /**
     * 对串口接收到的数据进行处理
     *
     * @param Serial_data 串口接收到的数据
     * @param temp        缓存串口接收的数据
     * @param data        结果数据
     * @param data_use    数据是否校验成功
     * @return map类型 key1 data key2 data_use
     */

    private Map<String, Object> Serial_Manage(String Serial_data, StringBuilder temp, String data, Boolean data_use) {
        Log.e("串口接收", "当前值GetMsg: " + Serial_data);
        temp.append(Serial_data);
        Log.e("串口接收", "缓冲区temp: " + temp);
        Map<String, Object> map = new HashMap<String, Object>();
        int start, end, i;
        String length, ComputeCheckSum, GetCheckSum, mcmd;

        if (temp.indexOf("FCFEF0F8") != -1) {  //判断缓冲区的帧尾
            if (temp.indexOf("FEFCF8F001B3") != -1) {  //判断缓冲区的帧头
                if (temp.indexOf("FEFCF8F001B3") > temp.indexOf("FCFEF0F8")){  //如果帧头在帧尾后边
                    temp.delete(0, temp.indexOf("FEFCF8F001B3"));
                } else {
                    start = temp.indexOf("FEFCF8F001B3");  //帧头位置
                    end = temp.indexOf("FCFEF0F8");          //帧尾位置
                    length = temp.substring(start + 12, start + 16); //获取字符串长度
                    i = Integer.parseInt(length, 16);
                    ComputeCheckSum = GetCheckSum(temp.substring(start, end + 8).substring(8, 2 * i + 12)); //计算校验和
                    GetCheckSum = temp.substring(start, end + 8).substring(2 * i + 12, 2 * i + 16);        //获得传来的校验和
                    Log.e("串口接收", "--算校验--" + ComputeCheckSum);
                    Log.e("串口接收", "--接收校验--" + GetCheckSum);
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
                    if (mcmd.equals("26") || mcmd.equals("16")||mcmd.equals("17")||mcmd.equals("14")||mcmd.equals("01")||mcmd.equals("12")||mcmd.equals("19")||mcmd.equals("1B")) {   //根据指令 决定是否 发送应答帧
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

    /**
     * 初始化串口设备列表，根据接线的串口，来连接设备
     */
    private void initDevice() {
        mDevice = new Device("/dev/ttyS2", "115200");
        switchSerialPort();
    }

    private void play_nocation() {
        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        final Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
        r.play();
    }  //播放声音，暂时功能无法使用

    /**
     * 向底层串口发送指令，字符串形式的，---------------------重要---------------------
     */
    private void sendData(String text) {
        SerialPortManager.instance().sendCommand(text);
        Log.e("串口发送", "命令：" + text);
    }

    /**
     * 向底层发数据、 接收到数据后的校验 需要使用
     *
     * @param CheckData 需要校验的数据
     * @return String 校验和
     */
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
     * 向底层发数据使用
     *
     * @param CheckSum  校验和 GetCheckSum的返回值
     * @param CheckData 需要校验的数据
     * @return String 完整的发送数据
     */
    private String GetSendData(String CheckData, String CheckSum) {
        StringBuilder Builder = new StringBuilder("FEFCF8F0");  //Header[4] - 8位
        Builder.append(CheckData);
        Builder.append(CheckSum);
        Builder.append("FCFEF0F8");
        return Builder.toString();
    }  //得到发送指令

    /**
     * 获取并解析gps数据
     *
     * @param GPS 包含GPS的6字节数据 字符串(12位)
     * @return Map<String, String> 类型的数据key1:jd , key2:wd
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private Map<String, String> GetGps(String GPS, String xiangxian,boolean fresh) {
        Map<String, String> map = new HashMap<>();
        int jingdu = Integer.parseInt(GPS.substring(0, 7), 16);
        int weidu = Integer.parseInt(GPS.substring(7, 12), 16);
        String jd = Integer.toString(jingdu);
        String wd = Integer.toString(weidu);
        if(fresh)
        {
            int a = Integer.parseInt(jd.substring(1, 4));
            double b = (double) Integer.parseInt(jd.substring(4, 6))/60;
            double c = (double)Integer.parseInt(jd.substring(6, 8))/3600;
            int d = Integer.parseInt(wd.substring(0, 2));
            double f = (double) Integer.parseInt(wd.substring(2, 4))/60;
            double g = (double)Integer.parseInt(wd.substring(4, 6))/3600;
            JSONObject para = new JSONObject();
            try {
                para.put("pos", 0);
                para.put("x", a+b+c);
                para.put("y", d+f+g);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            String p = para.toString();
            String js = "javascript:" + "change_position" + "(" + p + ")";
            webView.evaluateJavascript(js, new ValueCallback<String>() {
                @Override
                public void onReceiveValue(String value) {
                    //此处为 js 返回的结果
                }
            });
        }
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
    }

    /**
     * 将图片转换成十六进制字符串
     * @param sendPhoto
     * @return
     */
    public Bitmap recv_photo(String sendPhoto) {

        byte[] recv_byte = hex2byte(sendPhoto);
        Bitmap bitmap = Bytes2Bitmap(recv_byte);
        return bitmap;
    }

    /**
     * 字符转char数组
     * @param hex
     * @return
     */
    public static byte[] hex2byte(String hex) {
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

    private byte toByte(char c) {
        byte b = (byte) "0123456789ABCDEF".indexOf(c);
        return b;
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
    }   //与底层串口通信 无需修改


    @Override
    protected void onResume() {
        super.onResume();
        if (player != null) {
            player.onResume();
        }

    }  //无需修改

    @Override
    protected void onPause() {
        if (player != null) {
            player.onPause();
        }
        super.onPause();
    }   //无需修改

    @Override
    public void onNetChange(int state) {
        if (isFinishing()) {
            return;
        }
        ULog.d("dsiner: Network state--> " + state);
    }  //无需修改
    /**
     * 按键转译模拟屏幕触摸点击事件
     * @param x 屏幕坐标x
     * @param y 屏幕坐标y
     */
    private void sendTouchEvent(final int x, final int y) {  //最快的一种方式
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // 创建一个Instrumentation对象
                    Instrumentation inst = new Instrumentation();
                    // 调用inst对象的按键模拟方法
                    inst.sendPointerSync(MotionEvent.obtain(SystemClock.uptimeMillis(),
                            SystemClock.uptimeMillis(), MotionEvent.ACTION_DOWN, x, y, 0));
                    inst.sendPointerSync(MotionEvent.obtain(SystemClock.uptimeMillis(),
                            SystemClock.uptimeMillis(), MotionEvent.ACTION_UP, x, y, 0));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        ViewGroup.LayoutParams lp = player.getLayoutParams();
        lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            lp.height = ViewGroup.LayoutParams.MATCH_PARENT;
            player.setLayoutParams(lp);
        } else {
            lp.height = Util.dip2px(getApplicationContext(), 180);
            player.setLayoutParams(lp);
        }
        if (player != null) {
            player.onConfigurationChanged(newConfig);
        }
    }  //播放器的设置，无需修改

    @Override
    public void onBackPressed() {
        if (player != null && player.onBackPress()) {
            return;
        }
        super.onBackPressed();
    }    //无需修改

    @Override
    public void finish() {
        if (player != null) {
            player.onDestroy();
        }
        super.finish();
    }   //无需修改

    @Override
    protected void onDestroy() {
        SerialPortManager.instance().close();
        NetBus.getIns().removeListener(this);
        super.onDestroy();
    }  //无需修改

    private void initView_advertise() {
        tv_switcher = findViewById(R.id.tv_switcher);
        tv_switcher2 = findViewById(R.id.tv_switcher2);
        tv_switcher3 = findViewById(R.id.tv_switcher3);
        tv_switcher4 = findViewById(R.id.tv_switcher4);
        tv_switcher5 = findViewById(R.id.tv_switcher5);
        tv_switcher.setFactory(new ViewSwitcher.ViewFactory() {
            @Override
            public View makeView() {
                return new TextView(SimpleActivity.this);
            }
        });
        tv_switcher2.setFactory(new ViewSwitcher.ViewFactory() {
            @Override
            public View makeView() {
                return new TextView(SimpleActivity.this);
            }
        });
        tv_switcher3.setFactory(new ViewSwitcher.ViewFactory() {
            @Override
            public View makeView() {
                return new TextView(SimpleActivity.this);
            }
        });
        tv_switcher4.setFactory(new ViewSwitcher.ViewFactory() {
            @Override
            public View makeView() {
                return new TextView(SimpleActivity.this);
            }
        });
        tv_switcher5.setFactory(new ViewSwitcher.ViewFactory() {
            @Override
            public View makeView() {
                return new TextView(SimpleActivity.this);
            }
        });

        alist = new ArrayList<>();
        alist.clear();
//        for (int i = 0; i < 10; i++) {
//            alist.add("我是"+i);
        alist.add("大连昊洋科技发展有限公司1");
        alist.add("大连昊洋科技发展有限公司2");
        alist.add("大连昊洋科技发展有限公司3");
        alist.add("大连昊洋科技发展有限公司4");
        alist.add("大连昊洋科技发展有限公司5");


        alist2 = new ArrayList<>();
        alist2.clear();
        alist2.add("大连昊洋科技发展有限公司2");
        alist2.add("大连昊洋科技发展有限公司3");
        alist2.add("大连昊洋科技发展有限公司4");
        alist2.add("大连昊洋科技发展有限公司5");
        alist2.add("大连昊洋科技发展有限公司1");

        alist3 = new ArrayList<>();
        alist3.clear();
        alist3.add("大连昊洋科技发展有限公司3");
        alist3.add("大连昊洋科技发展有限公司4");
        alist3.add("大连昊洋科技发展有限公司5");
        alist3.add("大连昊洋科技发展有限公司1");
        alist3.add("大连昊洋科技发展有限公司2");

        alist4 = new ArrayList<>();
        alist4.clear();
        alist4.add("大连昊洋科技发展有限公司4");
        alist4.add("大连昊洋科技发展有限公司5");
        alist4.add("大连昊洋科技发展有限公司1");
        alist4.add("大连昊洋科技发展有限公司2");
        alist4.add("大连昊洋科技发展有限公司3");

        alist5 = new ArrayList<>();
        alist5.clear();
        alist5.add("大连昊洋科技发展有限公司5");
        alist5.add("大连昊洋科技发展有限公司1");
        alist5.add("大连昊洋科技发展有限公司2");
        alist5.add("大连昊洋科技发展有限公司3");
        alist5.add("大连昊洋科技发展有限公司4");

        new TextSwitcherAnimation(tv_switcher, alist).create();
        new TextSwitcherAnimation(tv_switcher2, alist2).create();
        new TextSwitcherAnimation(tv_switcher3, alist3).create();
        new TextSwitcherAnimation(tv_switcher4, alist4).create();
        new TextSwitcherAnimation(tv_switcher5, alist5).create();

    } //无需修改，滑动广告
}
