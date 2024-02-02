package com.d.commenplayer.activity;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 2;
    public DBHelper(@Nullable Context context) {
        super(context, "contacts_info.db", null, 2);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.e("TAG","DBHelper onCreate()");
        //创建表
        db.execSQL("create table contacts_info(_id integer primary key autoincrement,name varchar,number varchar)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        switch (newVersion) {
            case DATABASE_VERSION:
                if(!checkColumnExists(db,"contacts_info","pinyin")) {
                    String sql = "ALTER TABLE contacts_info"
                            + " ADD COLUMN pinyin text";
                    db.execSQL(sql);  // 执行修改表，添加字段的逻辑。
                }
    }
    }

    /**
     * 方法：检查表中某列是否存在
     * @param db
     * @param tableName 表名
     * @param columnName 列名
     * @return
     */
    private boolean checkColumnExists(SQLiteDatabase db, String tableName, String columnName) {
        boolean result = false ;
        Cursor cursor = null ;

        try{
            cursor = db.rawQuery( "select * from sqlite_master where name = ? and sql like ?"
                    , new String[]{tableName , "%" + columnName + "%"} );
            result = null != cursor && cursor.moveToFirst() ;
        }catch (Exception e){
            Log.e("TAG","checkColumnExists..." + e.getMessage()) ;
        }finally{
            if(null != cursor && !cursor.isClosed()){
                cursor.close() ;
            }
        }

        return result ;
    }
}
