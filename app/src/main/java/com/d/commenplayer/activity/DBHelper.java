package com.d.commenplayer.activity;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper {

    public DBHelper(@Nullable Context context) {
        super(context, "contacts_info.db", null, 1);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.e("TAG","DBHelper onCreate()");
        //创建表
        db.execSQL("create table contacts_info(_id integer primary key autoincrement,name varchar,number varchar)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
