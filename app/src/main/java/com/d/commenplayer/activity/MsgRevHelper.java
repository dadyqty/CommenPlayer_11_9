package com.d.commenplayer.activity;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;
import android.util.Log;

public class MsgRevHelper extends SQLiteOpenHelper {

    public MsgRevHelper(@Nullable Context context) {
        super(context, "msg_info.db", null, 1);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.e("TAG","MsgRevHelper onCreate()");
        //创建表
        db.execSQL("create table IF NOT EXISTS msg_info(_id integer primary key autoincrement,number varchar,msg varchar,gpsjd varchar,gpswd varchar,time varchar,rili varchar,type integer,img integer)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}