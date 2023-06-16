package com.d.commenplayer.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.d.commenplayer.R;
import java.util.List;
import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

public class messagerevActivity extends Activity {
    private ListView lv_main;
    private TextView tv_empty;
    private int position;
    private MsgInfoAdapter adapter;

    private List<MsgInfo> data;
    private MsgRevDao dao;
    private ContactsDao dao2;
    private int count_changlanguage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messagerev);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        lv_main = (ListView) findViewById(R.id.lv_main);
        tv_empty=(TextView)findViewById(R.id.tv_empty);
        adapter=new MsgInfoAdapter();

        Intent intent = getIntent();
        count_changlanguage= Integer.valueOf(intent.getStringExtra("count_changlanguage"));
        dao=new MsgRevDao(this);
        dao2=new ContactsDao(this);
        data=dao.getRev();

        lv_main.setEmptyView(tv_empty);
        lv_main.setAdapter(adapter);

//        //给listView设置创建ContextMenu的监听
        lv_main.setOnCreateContextMenuListener(this);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        //添加3个item
        menu.add(0, 1, 0, "查看");
        menu.add(0, 2, 0, "回复");
        menu.add(0, 3, 0, "删除");
        menu.add(0, 4, 0, "取消");
        //得到长按的position
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        position = info.position;
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        MsgInfo msgInfo=data.get(position);
        switch (item.getItemId()){
            case 1:
                //准备一个带额外数据的intent对象
                Intent intent=new Intent(messagerevActivity.this,message_show.class);
                intent.putExtra("tv_msg_zhongduanhao",msgInfo.getNumber());
                intent.putExtra("tv_msg",msgInfo.getMsg());
                intent.putExtra("tv_gpsjd_rev",msgInfo.getGpsjd());
                intent.putExtra("tv_gpswd_rev",msgInfo.getGpswd());
                intent.putExtra("tv_time_rev",msgInfo.getTime());
                intent.putExtra("rili",msgInfo.getRili());

                startActivity(intent);
                break;
            case 2:
                int resultCode=34;
                //准备一个带额外数据的intent对象
                Intent intent2=new Intent();
                String result=String.valueOf(count_changlanguage);
                intent2.putExtra("RESULT",result);
                intent2.putExtra("ZHONGDUANHAO",msgInfo.getNumber());
                //设置结果
                setResult(resultCode,intent2);
                finish();
                break;
            case 3:
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
                                dao.deleteById(msgInfo.getId());
                                //2).删除List对应的数据
                                data.remove(position);
                                //3).通知更新列表
                                adapter.notifyDataSetChanged();
                                //4).提示
                                Toast.makeText(messagerevActivity.this, "删除成功！", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("取消", null)
                        .show();
                break;
            case 4:
                break;
            default:
                break;
        }
        return super.onContextItemSelected(item);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == 216 && event.getAction() == KeyEvent.ACTION_UP){
            //保存一个结果码
            int resultCode=3;
            //准备一个带额外数据的intent对象
            Intent data=new Intent();
            String result=String.valueOf(count_changlanguage);
            data.putExtra("RESULT",result);
            //设置结果
            setResult(resultCode,data);
            finish();
            ToastUtile.showText(messagerevActivity.this, "返回主界面");
        }
        if (event.getKeyCode()== 62 && event.getAction() == KeyEvent.ACTION_UP){  //归中键
            if(lv_main.hasFocus()){
                lv_main.getSelectedView().performLongClick();
            }
        }

        return super.dispatchKeyEvent(event);
    }

  public  class MsgInfoAdapter extends BaseAdapter {

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
                convertView = View.inflate(messagerevActivity.this, R.layout.msg_item, null);
                AbsListView.LayoutParams param = new AbsListView.LayoutParams(MATCH_PARENT,50);
                convertView.setLayoutParams(param);
            }
            MsgInfo msgInfo=data.get(position);
            TextView name=(TextView)convertView.findViewById(R.id.tv_name_rev);
            TextView number=(TextView)convertView.findViewById(R.id.tv_number_rev);
            TextView msg=(TextView)convertView.findViewById(R.id.tv_msg_rev);
            String msg_name=dao2.getContactsName(msgInfo.getNumber());
            if(msg_name!=null){
                name.setText(msg_name);
            }else {
                name.setText("未知用户");
            }
            number.setText(msgInfo.getNumber());
            msg.setText(msgInfo.getMsg());
            return convertView;
        }
    }
}