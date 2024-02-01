package com.d.commenplayer.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Instrumentation;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.ValueCallback;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.d.commenplayer.ContactsDatabaseHelper;
import com.d.commenplayer.MainActivity;
import com.d.commenplayer.R;
import com.d.commenplayer.adapter.Contacts;
import com.d.commenplayer.adapter.ContactsAdapter;
import com.d.commenplayer.comn.Device;
import com.d.commenplayer.comn.message.IMessage;
import com.d.commenplayer.comn.message.SerialPortManager;
import com.d.commenplayer.util.ToastUtil;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

public class ContactsActivity extends Activity {

    private ListView lv_main;
    private Button add;
    private TextView tv_empty;
    private List<ContactsInfo> data;

    private boolean data_use = false;

    private boolean send_success=false;

    private boolean hujiaofault = false;
    private int position;
    private ContactsDao dao;
    private ContactsInfoAdapter adapter;

    private MyHandle myHandle = new MyHandle(this);
    private int count_changlanguage;
    private boolean[] isdelay = new boolean[14];
    private boolean issend = false;
    private int last_keydown = 0;

    private  int timer_cnt =0;
    private int last_messagecode = 0;
    private int[] countarray = new int[14];
    private long predowmTime =0;
    private int count_downtime=0;
    private boolean flag_number = false;
    private TextView languagee;

    private String xiangxian;

    private boolean mOpened = false;
    private Device mDevice;

    private String languagee_temp;

    private TextView xuanhuhujiao;

    private TextView xuanhuhujiaojd;

    private TextView xuanhuhujiaowd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);
        add=findViewById(R.id.add);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        lv_main = (ListView) findViewById(R.id.lv_main);
        tv_empty=(TextView)findViewById(R.id.tv_empty);

        adapter=new ContactsInfoAdapter();
        dao=new ContactsDao(this);
        data=dao.getAll();

        lv_main.setEmptyView(tv_empty);
        lv_main.setAdapter(adapter);

        //给listView设置创建ContextMenu的监听
        lv_main.setOnCreateContextMenuListener(this);

        Intent intent = getIntent();
        count_changlanguage= Integer.valueOf(intent.getStringExtra("count_changlanguage"));
        if(count_changlanguage==1){
            languagee_temp="英文";
        }else if(count_changlanguage==2){
            languagee_temp="中文";
        }
        initDevice();
        new Thread(new ContactsActivity.Mythread()).start();
        for(int i=0;i<countarray.length;i++){
            countarray[i] = 0;
        }

        for(int i=0;i<isdelay.length;i++){
            isdelay[i] = true;
        }
    }
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        Log.e("TAG","keycode="+event.getKeyCode());
        if (event.getKeyCode()== 45 && event.getAction() == KeyEvent.ACTION_UP){  //添加键
            add.performClick();
        }
        if(event.getKeyCode() == 51&& event.getAction() == KeyEvent.ACTION_UP)
        {
            final ContactsInfo contactsInfo=data.get(lv_main.getSelectedItemPosition());
            showUpdateDialog(contactsInfo);
        }
        if(event.getKeyCode() == 46&& event.getAction() == KeyEvent.ACTION_UP)
        {
            final ContactsInfo contactsInfo=data.get(lv_main.getSelectedItemPosition());
            dao.deleteById(contactsInfo.getId());
            //2).删除List对应的数据
            data.remove(position);
            //3).通知更新列表
            adapter.notifyDataSetChanged();
            //4).提示
            Toast.makeText(ContactsActivity.this, "删除成功！", Toast.LENGTH_SHORT).show();
        }
        if(event.getKeyCode() == 48&& event.getAction() == KeyEvent.ACTION_UP)
        {
            final ContactsInfo contactsInfo=data.get(lv_main.getSelectedItemPosition());
            xuanhu(contactsInfo);
        }
        if(event.getKeyCode() == 159&& event.getAction() == KeyEvent.ACTION_UP)
        {
            final ContactsInfo contactsInfo=data.get(lv_main.getSelectedItemPosition());
            Intent intent2 = new Intent(ContactsActivity.this, SendMessageActivity.class);
            intent2.putExtra("count_changlanguage",String.valueOf(count_changlanguage));
            intent2.putExtra("kind","01");
            intent2.putExtra("lxr_enter",true);
            intent2.putExtra("zhongduanhao",contactsInfo.getNumber());
            startActivityForResult(intent2, 9);
        }
        if (event.getKeyCode() == 216 && event.getAction() == KeyEvent.ACTION_UP)       //取消键 KeyCode() == 216, 表示返回
        {
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
        }
        if (event.getKeyCode()== 62 && event.getAction() == KeyEvent.ACTION_UP){  //归中键
            if(lv_main.hasFocus()){
                lv_main.getSelectedView().performLongClick();
            }
        }

        return super.dispatchKeyEvent(event);
    }
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
    }  //模拟触摸屏幕的某个位置

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        //添加3个item
        menu.add(0, 1, 0, "更新");
        menu.add(0, 2, 0, "删除");
        menu.add(0, 3, 0, "呼叫");
        menu.add(0, 4, 0, "短信");
        menu.add(0, 5, 0, "取消");

        //得到长按的position
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        position = info.position;
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        //得到对应的ContactsInfo对象
        final ContactsInfo contactsInfo=data.get(position);
        switch (item.getItemId()){
            case 1: //更新
                //1).更新数据表对应的数据
                showUpdateDialog(contactsInfo);
                //2).更新List对应的数据
                //3).通知更新列表
                break;
            case 2://删除
                new AlertDialog.Builder(this)
                        .setTitle("提示")
                        .setMessage("确定删除？")
                        .setOnKeyListener(new DialogInterface.OnKeyListener() {
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
                                return false;
                            }
                        })
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //1).删除数据表对应的数据
                                dao.deleteById(contactsInfo.getId());
                                //2).删除List对应的数据
                                data.remove(position);
                                //3).通知更新列表
                                adapter.notifyDataSetChanged();
                                //4).提示
                                Toast.makeText(ContactsActivity.this, "删除成功！", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("取消", null)
                        .show();
                break;
            case 3:
                xuanhu(contactsInfo);
                break;
            case 4:
                break;
            case 5:
                break;
            default:
                break;
        }
        return super.onContextItemSelected(item);
    }

    /**
     * 显示更新的Dialog
     *
     * @param contactsInfo
     */
    private void showUpdateDialog(final ContactsInfo contactsInfo){
        Log.e("TAG","showUpdateDialog()");
        AlertDialog.Builder builder=new AlertDialog.Builder(ContactsActivity.this);
        builder.setTitle("更新联系人");
        final View update_view= LayoutInflater.from(ContactsActivity.this).inflate(R.layout.add_view,null);
        builder.setView(update_view);
        final EditText et_name=update_view.findViewById(R.id.et_name);
        final EditText et_number=update_view.findViewById(R.id.et_number);
        final TextView languagee=update_view.findViewById(R.id.show_language);
        et_name.setHint(contactsInfo.getName());
        et_number.setHint(contactsInfo.getNumber());
        et_number.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                    et_number.setSelected(hasFocus);
                    if (count_changlanguage == 1) {
                        languagee_temp = "英文";
                    } else if (count_changlanguage == 2) {
                        languagee_temp = "中文";
                    }
                    languagee.setText("数字");
                    flag_number=true;
                    count_changlanguage = 0;
//                    count_changlanguage = 3;
//                    Message message = new Message();
//                    message.what = 13;
//                    myHandle.sendMessage(message);
//                    et_number.setSelection(et_number.getText().length());

            }
        });

        et_name.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                et_name.setSelected(hasFocus);
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
        });
        builder.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if(et_name.isSelected()) {
                    if (event.getKeyCode() == 278 && event.getAction() == KeyEvent.ACTION_UP)  //输入法键 KeyCode() == 278, 切换输入法
                    {
                        count_changlanguage++;
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
                                count_changlanguage = 0;
                                languagee.setText("数字");
                                break;
                        }
                    }
                    if (event.getKeyCode() == KeyEvent.KEYCODE_F12&& event.getAction() == KeyEvent.ACTION_UP)       //数字1键 KeyCode() == 142, KEYCODE_F12 = 142
                    {
                        Log.i("TAG","dispatchKeyEvent"+event.getKeyCode());
                        if (last_keydown != event.getKeyCode() && last_keydown != 0) {
                            isdelay[last_messagecode] = false;
                            issend = true;
                            isdelay[12] = true;
                            predowmTime = event.getDownTime();
                            Message message = new Message();
                            message.what = 12;
                            myHandle.sendMessage(message);
                        }
                        if (event.getDownTime() - predowmTime <= 400 || predowmTime==0){
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
                if(et_number.isSelected()){
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

                if (event.getKeyCode() == 215&& event.getAction() == KeyEvent.ACTION_UP)       //确定键  KeyCode() == 215, 表示发送短信
                {
                    Button btn_pos = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                    btn_pos.performClick(); //点击确定
                }
                if (keyCode == 216 && event.getAction() == KeyEvent.ACTION_UP) {
                    Button btn_neg = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_NEGATIVE);
                    btn_neg.performClick(); //点击取消
                }
                return false;
            }
        });
        builder.setPositiveButton("更新", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    Field field = dialog.getClass().getSuperclass().getDeclaredField("mShowing");
                    field.setAccessible(true);
                    field.set(dialog,false);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                //1).更新List对应的数据
                String name=et_name.getText().toString();
                String number=et_number.getText().toString();
                if(number.length()!=9)
                {
                    ToastUtile.showText(ContactsActivity.this, "终端号输入错误！请重新输入！");
                    et_number.setText("");
                }
                else {
                    try {
                        Field field = dialog.getClass().getSuperclass().getDeclaredField("mShowing");
                        field.setAccessible(true);
                        field.set(dialog,true);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    contactsInfo.setName(name);
                    contactsInfo.setNumber(number);
                    //2).更新数据表对应的数据
                    dao.update(contactsInfo);
                    //3).通知更新列表
                    adapter.notifyDataSetChanged();
                }
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    Field field = dialog.getClass().getSuperclass().getDeclaredField("mShowing");
                    field.setAccessible(true);
                    field.set(dialog,true);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });
        Dialog dialog_update=builder.create();
        dialog_update.show();
        WindowManager.LayoutParams params=dialog_update.getWindow().getAttributes();
        params.width=400;
        params.height=260;
        dialog_update.getWindow().setAttributes(params);
    }
    private String GetCheckSum(String CheckData) {
        StringBuilder Builder = new StringBuilder();
        int sum = 0;
        String temp = "";
        int tdata = 0;
        for (int i = 0; i < CheckData.length() / 2; i++) {
            temp = CheckData.substring(i * 2, i * 2 + 2);
            tdata = Integer.parseInt(temp, 16);
            sum += tdata;
        }
        for (int t = 0; t < 4 - Integer.toHexString(sum).length(); t++) {
            Builder.append("0");
        }
        return Builder.append(Integer.toHexString(sum).toUpperCase()).toString();
    }  //获取校验和
    private String GetSendData(String CheckData, String CheckSum) {
        StringBuilder Builder = new StringBuilder("FEFCF8F0");  //Header[4] - 8位
        Builder.append(CheckData);
        Builder.append(CheckSum);
        Builder.append("FCFEF0F8");
        return Builder.toString();
    }  //得到发送指令
    public void add(View v){
        Log.e("TAG","onClick add");
        AlertDialog.Builder builder=new AlertDialog.Builder(ContactsActivity.this);
        builder.setTitle("添加联系人");
        final View add_view= LayoutInflater.from(ContactsActivity.this).inflate(R.layout.add_view,null);
        final EditText et_name=add_view.findViewById(R.id.et_name);
        final EditText et_number=add_view.findViewById(R.id.et_number);
        final TextView languagee=add_view.findViewById(R.id.show_language);
        builder.setView(add_view);
        et_number.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                et_number.setSelected(hasFocus);
                if (count_changlanguage == 1) {
                    languagee_temp = "英文";
                } else if (count_changlanguage == 2) {
                    languagee_temp = "中文";
                }
                languagee.setText("数字");
                flag_number=true;
                count_changlanguage = 0;
            }
        });

        et_name.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                et_name.setSelected(hasFocus);
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
        });
        builder.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if(et_name.isSelected()) {
                    if (event.getKeyCode() == 278 && event.getAction() == KeyEvent.ACTION_UP)  //输入法键 KeyCode() == 278, 切换输入法
                    {
                        if(count_changlanguage==1)
                        {
                            languagee_temp = "中文";
                            languagee.setText("中文");
                        }
                        else if(count_changlanguage==0)
                        {
                            languagee_temp = "英文";
                            languagee.setText("英文");
                        }
                        else
                            languagee.setText("数字");
                        Log.i("TAG","dispatchKeyEvent"+event.getKeyCode());
                        Message message = new Message();
                        message.what = 13;
                        myHandle.sendMessage(message);
                        count_changlanguage++;
                        Log.i("获取按键键值", "dispatchKeyEvent: "+count_changlanguage);
                    }
                    if (event.getKeyCode() == KeyEvent.KEYCODE_F12&& event.getAction() == KeyEvent.ACTION_UP)       //数字1键 KeyCode() == 142, KEYCODE_F12 = 142
                    {
                        Log.i("TAG","dispatchKeyEvent"+event.getKeyCode());
                        if (last_keydown != event.getKeyCode() && last_keydown != 0) {
                            isdelay[last_messagecode] = false;
                            issend = true;
                            isdelay[12] = true;
                            predowmTime = event.getDownTime();
                            Message message = new Message();
                            message.what = 12;
                            myHandle.sendMessage(message);
                        }
                        if (event.getDownTime() - predowmTime <= 400 || predowmTime==0){
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
                if(et_number.isSelected()){
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

                if (event.getKeyCode() == 215&& event.getAction() == KeyEvent.ACTION_UP)       //确定键  KeyCode() == 215, 表示发送短信
                {
                    Button btn_pos = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                    btn_pos.performClick(); //点击确定
                }
                if (keyCode == 216 && event.getAction() == KeyEvent.ACTION_UP) {
                    Button btn_neg = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_NEGATIVE);
                    btn_neg.performClick(); //点击取消
                }
                return false;
            }
        });

        builder.setPositiveButton("添加", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    Field field = dialog.getClass().getSuperclass().getDeclaredField("mShowing");
                    field.setAccessible(true);
                    field.set(dialog,false);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                //1).更新List对应的数据
                //1).保存数据到数据库中
                String name=et_name.getText().toString();
                String number=et_number.getText().toString();
                ContactsInfo contactsInfo=new ContactsInfo(-1,name,number);
                if(number.length()!=9)
                {
                    ToastUtile.showText(ContactsActivity.this, "终端号输入错误！请重新输入！");
                    et_number.setText("");
                }
                else {
                    try {
                        Field field = dialog.getClass().getSuperclass().getDeclaredField("mShowing");
                        field.setAccessible(true);
                        field.set(dialog,true);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    dao.add(contactsInfo);
                    //2).保存数据到List
                    data.add(0, contactsInfo);  //让新添加的在第一行
                    //3).通知更新列表
                    adapter.notifyDataSetChanged();
                }

            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    Field field = dialog.getClass().getSuperclass().getDeclaredField("mShowing");
                    field.setAccessible(true);
                    field.set(dialog,true);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });
        Dialog dialog_add=builder.create();
        dialog_add.show();
        WindowManager.LayoutParams params=dialog_add.getWindow().getAttributes();
        params.width=400;
        params.height=260;
        dialog_add.getWindow().setAttributes(params);
    }



    class ContactsInfoAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public Object getItem(int position) {
            return data.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = View.inflate(ContactsActivity.this, R.layout.list_item, null);
                AbsListView.LayoutParams param = new AbsListView.LayoutParams(MATCH_PARENT,50);
                convertView.setLayoutParams(param);
            }
            ContactsInfo contactsInfo=data.get(position);
            TextView name=(TextView)convertView.findViewById(R.id.name);
            TextView number=(TextView)convertView.findViewById(R.id.number);
            name.setText(contactsInfo.getName());
            number.setText(contactsInfo.getNumber());
            return convertView;
        }
    }
    /**
     * 模拟按键输入，转译输入按键的键值
     *
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
    /**
     * 计算两次按键事件的时间差
     *
     * @param
     * @return boolean
     */

    private class MyHandle extends Handler {
        private final WeakReference<ContactsActivity> mActivity;
        public MyHandle(ContactsActivity activity){
            mActivity = new WeakReference<>(activity);
        }
        @Override
        public void handleMessage(Message msg) {
            ContactsActivity activity = mActivity.get();
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
                                new Thread(){
                                    @Override
                                    public void run() {
                                        sendKeyCode(59);
                                        Log.i("转译按键键值", "KeyCode:"+59);
                                    }
                                }.start();
                                flag_number=false;

//                                 languagee.setText("英文");
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

//                                languagee.setText("中文");
                                break;
                            case 3:
                                flag_number=true;
                                count_changlanguage = 0;
//                                languagee.setText("数字");
                                break;
                        }

                }
            }
        }
    }

    private void xuanhu(ContactsInfo contactsInfo)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(ContactsActivity.this);
        builder.setIcon(R.drawable.bohao);
        builder.setTitle("选呼");
        View view = LayoutInflater.from(ContactsActivity.this).inflate(R.layout.dialog_xuanhuan_send, null);
        builder.setView(view);
        EditText zhongduanhao = (EditText) view.findViewById(R.id.zhongduanhao);
        EditText xindaohao = (EditText) view.findViewById(R.id.xindaohao);
        zhongduanhao.setText(contactsInfo.getNumber());
        xindaohao.setText("100");
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
                try {
                    Field field = dialog.getClass().getSuperclass().getDeclaredField("mShowing");
                    field.setAccessible(true);
                    field.set(dialog,false);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }

                if (zhongduanhao.getText().toString().length() < 9) {
                    ToastUtil.show(ContactsActivity.this, "输入错误");
                } else {
                    String a = zhongduanhao.getText().toString();
                    String b = xindaohao.getText().toString();
                    b = Integer.toHexString(Integer.parseInt(b));
                    while (b.length() < 4) {
                        b = "0".concat(b);
                    }
                    String CheckData = "01B3000A16" + "0" + a + b;
                    String CheckSum = GetCheckSum(CheckData);
//                                ToastUtil.show(SimpleActivity.this, "电台号: " + a + ", 信道号: " + b);
                    xuanhuhujiao = (TextView) view.findViewById(R.id.xuanhuhujiao);
                    xuanhuhujiaojd = (TextView)view.findViewById(R.id.jindu);
                    xuanhuhujiaowd = (TextView)view.findViewById(R.id.weidu);
                    xuanhuhujiao.setText("正在呼叫......");

//                                kind_of_hujiao.setText("呼叫种类：选呼");
//                                hujiao_number.setVisibility(View.VISIBLE);
//                                hujiao_number.setText("对方船号："+a);
                    String data = GetSendData(CheckData, CheckSum);
                    sendData(data);
                }
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    Field field = dialog.getClass().getSuperclass().getDeclaredField("mShowing");
                    field.setAccessible(true);
                    field.set(dialog,true);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }

                String CheckData = "01B3000325";
                String CheckSum = GetCheckSum(CheckData);
                String data = GetSendData(CheckData, CheckSum);
                sendData(data);
//                            ToastUtil.show(SimpleActivity.this, "取消选呼");
            }
        });
        Dialog dialog1 = builder.create();
        dialog1.show();
        WindowManager.LayoutParams params = dialog1.getWindow().getAttributes();
        params.width = 400;
        params.height = 350;
        dialog1.getWindow().setAttributes(params);
        Button btn_pos = ((AlertDialog) dialog1).getButton(AlertDialog.BUTTON_POSITIVE);
        btn_pos.performClick(); //点击确定
    }
    private StringBuilder temp = new StringBuilder(); //临时缓存的字符串
    @Subscribe(threadMode = ThreadMode.MAIN) //675 440  740 450
    public void onMessageEvent(IMessage message) {
         String data = "";  //接收的指令
        Map<String, Object> map = Serial_Manage(message.getMessage(), temp, data, data_use);
        data = (String) map.get("data");
        data_use = (boolean) map.get("data_use");
        if(data_use){
                String cmd = data.substring(16, 18);
                switch (cmd) {
                    case "35":
                        xuanhuhujiao.setText("呼叫成功！");
                        data = "";// data使用完 清空
                        data_use = false;
                        break;
                    case "29":
                        xuanhuhujiao.setText("呼叫失败！");
                        hujiaofault = true;
                        data_use = false;
                        timer_cnt = 3;
                        data = "";
                        break;
                    case "34":
                        Map<String, String> map34 = GetGps(data.substring(18, 30),xiangxian,false);
                        String jd34 = map34.get("jd");
                        String wd34 = map34.get("wd");
                        xuanhuhujiaojd.setText(jd34);
                        xuanhuhujiaowd.setText(wd34);
                    break;
                    default:
                        break;
                }

        }
    }
    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
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
    private void initDevice() {
        mDevice = new Device("/dev/ttyS2", "115200"); //这里设置的
        switchSerialPort();
    }

    private void sendData(String text) {
        SerialPortManager.instance().sendCommand(text);
        Log.e("串口发送短信", "命令：" + text);
    }

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

    Handler handler_timer = new Handler()
    {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what)
            {
                case 0:
                    sendKeyCode(216);
                    String CheckData = "01B3000325";
                    String CheckSum = GetCheckSum(CheckData);
                    String data = GetSendData(CheckData, CheckSum);
                    sendData(data);
                    hujiaofault = false;
                    break;
                default:

                    break;
            }
        }
    };
    private Map<String, String> GetGps(String GPS, String xiangxian,boolean fresh) {
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
    }
    public class Mythread implements Runnable{
        @Override
        public void run() {
            while (true)
            {
                Message message = new Message();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                if(hujiaofault)
                {
                    if(timer_cnt==0)
                    {
                        message.what = 0;
                        handler_timer.sendMessage(message);
                    }
                    timer_cnt = timer_cnt -1;
                }
            }
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 9:
                if (data.getStringExtra("RESULT") != null) {
                    String result = data.getStringExtra("RESULT");
                    count_changlanguage = Integer.valueOf(result);
                }
                break;

            default:
                break;
        }
    }

}