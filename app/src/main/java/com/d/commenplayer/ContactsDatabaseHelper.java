package com.d.commenplayer;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.d.commenplayer.activity.ContactsDao;

public class ContactsDatabaseHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 9;
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
//        switch (i1) {
//            case DATABASE_VERSION:
//                if(!checkColumnExists(sqLiteDatabase,"contacts_info","pinyin")) {
//                    String sql = "ALTER TABLE Contacts"
//                            + " ADD COLUMN pinyin text";
//                    sqLiteDatabase.execSQL(sql);  // 执行修改表，添加字段的逻辑。
//                }
//    }

    }


}
