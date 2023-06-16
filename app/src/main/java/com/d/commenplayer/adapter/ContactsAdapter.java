package com.d.commenplayer.adapter;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Instrumentation;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.d.commenplayer.ContactsDatabaseHelper;
import com.d.commenplayer.R;
import com.d.commenplayer.activity.ContactsActivity;

import java.util.List;

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.ViewHolder> {

    private List<Contacts> mContactsList;
    private ContactsDatabaseHelper mdatabaseHelper;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        View contactView;
        Button contactName;
        TextView contactNumber;

        public ViewHolder(View itemView) {
            super(itemView);
            contactView = itemView;
            contactName = (Button) itemView.findViewById(R.id.contact_name);
            contactNumber = (TextView) itemView.findViewById(R.id.contact_number);
        }
    }

    public ContactsAdapter(List<Contacts> contactsList, ContactsDatabaseHelper databaseHelper) {
        mContactsList = contactsList;
        mdatabaseHelper = databaseHelper;
    }

    @NonNull
    @Override
    public ContactsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_contact, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        holder.contactView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int position = holder.getAdapterPosition();
                Contacts contacts = mContactsList.get(position);
                SQLiteDatabase dbw = mdatabaseHelper.getWritableDatabase();

               // Cursor cursor = dbw.query("Contacts", new String[]{"name"}, "name=?", new String[]{"张三"},null, null, null,null);
             //   Toast.makeText(view.getContext(), "you click view"+ cursor.getString(cursor.getColumnIndex("number")),Toast.LENGTH_LONG).show();
            }
        });

        holder.contactName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int  fourflag = 100;
                SQLiteDatabase dbw = mdatabaseHelper.getWritableDatabase();
                Cursor cursor = dbw.query("Contacts", new String[]{"name", "number"}, "name = ?", new String[]{"update"},null, null, null);
                while (cursor.moveToNext()){
                    Log.d("database", "原始数据 "+cursor.getString(cursor.getColumnIndex("number")));
                    fourflag = Integer.parseInt(cursor.getString(cursor.getColumnIndex("number")));
                    Log.d("database", "转化后数据 "+fourflag);
                }
                int position = holder.getAdapterPosition();
                Log.d("database", "转化后数据 "+position);
                getListener.onClick(position);
                if(position == getmPosition()){
                    holder.itemView.setBackgroundColor(Color.GRAY);
                }else {
                    holder.itemView.setBackgroundColor(Color.WHITE);
                }
                Contacts contacts = mContactsList.get(position);
                switch (fourflag){
                    case 1:  // 修改联系人
                        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext()); //弹窗dialog设置, 选呼按键识别，并弹窗
                        builder.setTitle("修改联系人");
                        View viewmain = LayoutInflater.from(view.getContext()).inflate(R.layout.addcontact_dialog, null);
                        builder.setView(viewmain);
                        EditText nameid = (EditText)viewmain.findViewById(R.id.name);
                        EditText numberid = (EditText)viewmain.findViewById(R.id.number);
                        String namestring = contacts.getName();
                        String numberstring = contacts.getNumber();
                        nameid.setText(contacts.getName());
                        numberid.setText(contacts.getNumber());

                        builder.setPositiveButton("确定", new DialogInterface.OnClickListener()
                        {

                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {

                                String names = nameid.getText().toString().trim();
                                String numbers = numberid.getText().toString().trim();

                                ContentValues values = new ContentValues();
                                values.put("name", names);
                                values.put("number", numbers);
                                dbw.update("Contacts", values, "name=?", new String[]{namestring});
                                values.clear();

                                mContactsList.remove(position);
                                mContactsList.add(new Contacts(names, numbers));
                                notifyDataSetChanged();

                            }

                        });
                        builder.setNegativeButton("取消", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {

                            }
                        });
                        builder.setOnKeyListener(new DialogInterface.OnKeyListener() {

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
                                if (keyCode == 216 && event.getAction() == KeyEvent.ACTION_UP) { //取消按键,模拟点击屏幕取消按键的地点
                                    sendTouchEvent(590, 430);
                                    Log.i("按键输入", "dispatchKeyEvent: 输入取消");

                                }
                                if (keyCode == 215 && event.getAction() == KeyEvent.ACTION_UP) { //确认按键
                                    sendTouchEvent(650,430);
                                    Log.i("按键输入", "dispatchKeyEvent: 输入确认");
                                }
                                Log.i("按键输入", "onKey: "+keyCode);
                                return false;
                            }
                        } );


                        Dialog dialog1 =  builder.create();
                        dialog1.show();
                        WindowManager.LayoutParams params = dialog1.getWindow().getAttributes();
                        params.width = 400;
                        params.height = 280;
                        dialog1.getWindow().setAttributes(params);
                        Toast.makeText(view.getContext(), "you click view",Toast.LENGTH_LONG).show();
                        break;
                    case 0: //删除联系人
                        mContactsList.remove(position);
                        notifyDataSetChanged();
                        dbw.delete("Contacts", "name = ?", new String[] {contacts.getName()} );
                        break;
                    case 2: //拨打电话
                        ContentValues values = new ContentValues();
                        values.put("name", "update");
                        values.put("number", contacts.getName());
                        dbw.update("Contacts", values, "name=?", new String[]{"update"});
                        values.clear();
                        Log.d("database", "拨打电话");
                        break;
                    case 3: //拨打电话
                        ContentValues values2 = new ContentValues();
                        values2.put("name", "update");
                        values2.put("number", contacts.getName());
                        dbw.update("Contacts", values2, "name=?", new String[]{"update"});
                        values2.clear();
                        Log.d("database", "发送短信");
                        break;
                    case 5: break;
                    default:   Log.d("database", "无");break;
                }
            }
        });

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ContactsAdapter.ViewHolder holder, int position) {
        Contacts contacts = mContactsList.get(position);
        holder.contactName.setText(contacts.getName());
        holder.contactNumber.setText(contacts.getNumber());
        holder.itemView.setBackgroundColor(Color.GRAY);


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getListener.onClick(position);
            }
        });

        if(position == getmPosition()){
            holder.itemView.setBackgroundColor(Color.GRAY);
        }else {
            holder.itemView.setBackgroundColor(Color.WHITE);
        }
//        holder.contactName.setBackgroundColor(Color.parseColor("#f34649"));
    }

    @Override
    public int getItemCount() {
        return mContactsList.size();
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

    public interface GetListener {

        void onClick(int position);
    }

    private GetListener getListener;

    public void setGetListener(GetListener getListener) {
        this.getListener = getListener;
    }
    private  int mPosition;

    public int getmPosition() {
        return mPosition;
    }

    public void setmPosition(int mPosition) {
        this.mPosition = mPosition;
    }
}
