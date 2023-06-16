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
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
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
import com.d.commenplayer.util.ToastUtil;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

public class ContactsActivity extends Activity {

    private ListView lv_main;
    private Button add;
    private TextView tv_empty;
    private List<ContactsInfo> data;
    private int position;
    private ContactsDao dao;
    private ContactsInfoAdapter adapter;

    private MyHandle myHandle = new MyHandle(this);
    private int count_changlanguage;
    private boolean[] isdelay = new boolean[14];
    private boolean issend = false;
    private int last_keydown = 0;
    private int last_messagecode = 0;
    private int[] countarray = new int[14];
    private long predowmTime =0;
    private int count_downtime=0;
    private boolean flag_number = false;
    private TextView languagee;


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
        if (event.getKeyCode() == 216 && event.getAction() == KeyEvent.ACTION_UP)       //取消键 KeyCode() == 216, 表示返回
        {
            sendKeyCode(62);        //表示空格 KEYCODE_SPACE = 62
            //保存一个结果码
            int resultCode=3;
            //准备一个带额外数据的intent对象
            Intent data=new Intent();
            String result=String.valueOf(count_changlanguage);
            data.putExtra("RESULT",result);
            //设置结果
            setResult(resultCode,data);
            finish();
            ToastUtile.showText(ContactsActivity.this, "返回主界面");
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
                        if (zhongduanhao.getText().toString().length() < 9) {
                            ToastUtil.show(ContactsActivity.this, "输入错误");
                        } else {
                            String a = zhongduanhao.getText().toString();
                            String b = xindaohao.getText().toString();
                            ToastUtil.show(ContactsActivity.this, "电台号: " + a + ", 信道号: " + b);
                            int resultCode = 33;
                            //准备一个带额外数据的intent对象
                            Intent data = new Intent();
                            b = Integer.toHexString(Integer.parseInt(b));
                            while (b.length() < 4) {
                              b = "0".concat(b);
                            }
                            String CheckData = "01B3000A16" + "0" + a + b;
                            String CheckSum = GetCheckSum(CheckData);
                            String sendData = GetSendData(CheckData, CheckSum);
                            String result = String.valueOf(count_changlanguage);
                            data.putExtra("RESULT",result);
                            data.putExtra("xuanhu",sendData);

                            //设置结果
                            setResult(resultCode,data);
                            finish();
                        }
                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ToastUtil.show(ContactsActivity.this, "您点击了取消！");
                    }
                });
                Dialog dialog1 = builder.create();
                dialog1.show();
                WindowManager.LayoutParams params = dialog1.getWindow().getAttributes();
                params.width = 400;
                params.height = 280;
                dialog1.getWindow().setAttributes(params);
                break;
            case 4:
                int resultCode=34;
                //准备一个带额外数据的intent对象
                Intent data=new Intent();
                String result=String.valueOf(count_changlanguage);
                data.putExtra("RESULT",result);
                data.putExtra("ZHONGDUANHAO",contactsInfo.getNumber());
                //设置结果
                setResult(resultCode,data);
                finish();
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
                languagee.setText("数字");
                et_number.setSelection(et_number.getText().length());
            }
        });

        et_name.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                et_name.setSelected(hasFocus);
                if(count_changlanguage==1){
                    languagee.setText("英文");
                }else{
                    languagee.setText("中文");
                    count_changlanguage=2;
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
                //1).更新List对应的数据
                String name=et_name.getText().toString();
                String number=et_number.getText().toString();
                contactsInfo.setName(name);
                contactsInfo.setNumber(number);
                //2).更新数据表对应的数据
                dao.update(contactsInfo);
                //3).通知更新列表
                adapter.notifyDataSetChanged();
            }
        });
        builder.setNegativeButton("取消",null);
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
                languagee.setText("数字");
                et_number.setSelection(et_number.getText().length());
            }
        });

        et_name.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                et_name.setSelected(hasFocus);
                if(count_changlanguage==1){
                    languagee.setText("英文");
                }else{
                    languagee.setText("中文");
                    count_changlanguage=2;
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

        builder.setPositiveButton("添加", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //1).保存数据到数据库中
                String name=et_name.getText().toString();
                String number=et_number.getText().toString();
                ContactsInfo contactsInfo=new ContactsInfo(-1,name,number);
                dao.add(contactsInfo);
                //2).保存数据到List
                data.add(0, contactsInfo);  //让新添加的在第一行
                //3).通知更新列表
                adapter.notifyDataSetChanged();
            }
        });
        builder.setNegativeButton("取消",null);
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

                                // languagee.setText("英文");
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

                                //languagee.setText("中文");
                                break;
                            case 3:
                                flag_number=true;
                                count_changlanguage = 0;
                                //languagee.setText("数字");
                                break;
                        }

                }
            }
        }
    }


}