package com.d.commenplayer.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.d.commenplayer.R;
import com.d.commenplayer.activity.ContactsDao;
import com.d.commenplayer.activity.MsgBox;
import com.d.commenplayer.activity.MsgInfo;
import com.d.commenplayer.activity.MsgRevDao;
import com.d.commenplayer.activity.ToastUtile;
import com.d.commenplayer.activity.message_show;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

public class Fragment1 extends Fragment implements AdapterView.OnItemClickListener{
    private MsgInfoAdapter adapter;
    private List<MsgInfo> data;
    private MsgRevDao dao;
    private ContactsDao dao2;
    private ListView lv_main;
    private TextView tv_empty;
    private int position;
    private String type = "短信";
    private boolean isShow;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Log.e("TAG","onCreateView()");
        View view = inflater.inflate(R.layout.fragment1, null);
        dao=new MsgRevDao(getActivity());
        dao2=new ContactsDao(getActivity());
        data= dao.getRev();
        lv_main = (ListView) view.findViewById(R.id.lv_main);
        registerForContextMenu(lv_main);//进行注册
        tv_empty=(TextView)view.findViewById(R.id.tv_empty);
        adapter=new MsgInfoAdapter();
        lv_main.setEmptyView(tv_empty);
        lv_main.setAdapter(adapter);
        lv_main.setOnCreateContextMenuListener(this);
        lv_main.setOnItemClickListener(this);

        return view;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        System.out.println("position1:"+position);
        this.position = position;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.e("TAG","onAttach()");
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("TAG","onCreate()");
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Log.e("TAG","onActivityCreated()");
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.e("TAG","onStart()");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e("TAG","onResume()");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.e("TAG","onPause()");
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        isShow=isVisibleToUser;
        Log.e("TAG","Fragment1 is VisibleToUser="+isVisibleToUser);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        if (isShow){
            //添加3个item
            menu.add(0, 1, 0, "查看");
            menu.add(0, 2, 0, "回复");
            menu.add(0, 3, 0, "删除");
            menu.add(0, 4, 0, "取消");
            //得到长按的position
            //AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
           // System.out.println("position: "+position);
        }
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (isShow){
            MsgInfo msgInfo=data.get(position);
            switch (item.getItemId()){
                case 1:
                    System.out.println(msgInfo.getType());
                    //准备一个带额外数据的intent对象
                    if(msgInfo.getImg()==0)
                        type = "短信";
                    else if (msgInfo.getImg()==1)
                        type = "信箱图片";
                    Intent intent=new Intent(getActivity(),message_show.class);
                    intent.putExtra("type",type);
                    intent.putExtra("tv_msg_zhongduanhao",msgInfo.getNumber());
                    intent.putExtra("tv_msg",msgInfo.getMsg());
                    intent.putExtra("tv_gpsjd_rev",msgInfo.getGpsjd());
                    intent.putExtra("tv_gpswd_rev",msgInfo.getGpswd());
                    intent.putExtra("tv_time_rev",msgInfo.getTime());
                    intent.putExtra("rili",msgInfo.getRili());
                    startActivity(intent);
                    Log.e("TAG", "跳转成功！");
                    break;
                case 2:
                    int resultCode=34;
                    //准备一个带额外数据的intent对象
                    Intent intent2=new Intent();
                    intent2.putExtra("ZHONGDUANHAO",msgInfo.getNumber());
                    //设置结果
                    getActivity().setResult(resultCode,intent2);
                    getActivity().finish();
                    break;
                case 3:
                    new AlertDialog.Builder(getActivity())
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
                                    Toast.makeText(getActivity(), "删除成功！", Toast.LENGTH_SHORT).show();
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
        }
        return super.onContextItemSelected(item);
    }


    class MsgInfoAdapter extends BaseAdapter {

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
                convertView = View.inflate(getActivity(), R.layout.msg_item, null);
                AbsListView.LayoutParams param = new AbsListView.LayoutParams(MATCH_PARENT,50);
                convertView.setLayoutParams(param);
            }
            MsgInfo msgInfo=data.get(position);
            System.out.println("position:"+position);
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.e("TAG","onDestroyView()");
    }
}
