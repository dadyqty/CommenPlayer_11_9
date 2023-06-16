package com.d.commenplayer;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ContactsDatabaseHelper extends SQLiteOpenHelper {

    public static final String CREATE_CONTACTS = "create table Contacts (" +
            "id integer primary key autoincrement, " +
            "name text,"+
            "number text)";

    private Context mContext;

    public  ContactsDatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory,
                                   int version) {
        super(context, name, factory, version);
        mContext = context;

    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_CONTACTS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
