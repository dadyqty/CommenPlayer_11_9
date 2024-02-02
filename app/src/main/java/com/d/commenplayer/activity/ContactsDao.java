package com.d.commenplayer.activity;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class ContactsDao {

    private DBHelper dbHelper;
    public ContactsDao(Context context){
        dbHelper=new DBHelper(context);
    }
    /**
     * 添加一条记录
     */
    public void add(ContactsInfo contactsInfo){
        //1.得到连接
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        //2.执行Insert  insert into black_number (number) values (xxx)
        ContentValues values = new ContentValues();
        values.put("name",contactsInfo.getName());
        values.put("number", contactsInfo.getNumber());
        values.put("pinyin",contactsInfo.getPinyin());
        long id = database.insert("contacts_info", null, values);
        Log.e("TAG", "id=" + id);
        //设置id
        contactsInfo.setId((int) id);
        //3.关闭
        database.close();
    }
    /**
     * 根据id删除一条记录
     */
    public void deleteById(int id) {
        //1.得到连接
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        //2.执行delete delete from black_number where _id=id
        int deleteCount = database.delete("contacts_info", "_id=?", new String[]{id+""});
        Log.e("TAG", "deleteCount=" + deleteCount);
        //3.关闭
        database.close();
    }
    /***
     * 更新一条记录
     *
     */
    public void update(ContactsInfo contactsInfo) {
        //1.得到连接
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        //2.执行update update black_number set number=xxx where _id=id
        ContentValues values = new ContentValues();
        values.put("name",contactsInfo.getName());
        values.put("number", contactsInfo.getNumber());
        values.put("pinyin",contactsInfo.getPinyin());
        int updateCount = database.update("contacts_info", values, "_id=" + contactsInfo.getId(), null);
        Log.e("TAG", "updateCount=" + updateCount);
        //3.关闭
        database.close();
    }
    /**
     * 查询所有记录封装成List<BlackNumber>
     */
    public List<ContactsInfo> getAll() {
        List<ContactsInfo> list= new ArrayList<ContactsInfo>();
        //1.得到连接
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        //2.执行query select * from black number
        //查询根据_id 实现倒序
        if(checkColumnExists(database,"contacts_info","pinyin")) {
            Cursor cursor = database.query("contacts_info", null, null, null, null, null, "pinyin asc");
            //3.从cursor中取出所有数据并封装到List中
            while (cursor.moveToNext()) {
                //id
                int id = cursor.getInt(0);
                //number
                String number = cursor.getString(cursor.getColumnIndex("number"));
                String name = cursor.getString(cursor.getColumnIndex("name"));
                String pinyin = cursor.getString(cursor.getColumnIndex("pinyin"));
                list.add(new ContactsInfo(id, name, number, pinyin));
            }

            //3.关闭
            cursor.close();
        }
        database.close();
        return list;
    }
    public String getContactsNumber(String Name){
        String Number = null;
        //1.得到连接
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        //2.执行query select * from black number
        //查询根据_id 实现倒序
        Cursor cursor = database.query("contacts_info", null, "name=?", new String[]{Name}, null, null, "_id desc");
        while (cursor.moveToNext()){
            Number=cursor.getString(cursor.getColumnIndex("number"));
        }
        //3.关闭
        cursor.close();
        database.close();
        return Number;
    }
    public String getContactsName(String Number){
        String Name = null;
        //1.得到连接
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        //2.执行query select * from black number
        //查询根据_id 实现倒序
        Cursor cursor = database.query("contacts_info", null, "number=?", new String[]{Number}, null, null, "_id desc");
        while (cursor.moveToNext()){
            Name=cursor.getString(cursor.getColumnIndex("name"));
        }
        //3.关闭
        cursor.close();
        database.close();
        return Name;
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

