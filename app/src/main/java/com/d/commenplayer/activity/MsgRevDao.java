package com.d.commenplayer.activity;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class MsgRevDao {
    private MsgRevHelper msgRevHelper;
    public MsgRevDao(Context context){
        msgRevHelper=new MsgRevHelper(context);
    }
    /**
     * 添加一条记录
     */
    public void add(MsgInfo msgInfo){
        //1.得到连接
        SQLiteDatabase database = msgRevHelper.getReadableDatabase();
        //2.执行Insert  insert into black_number (number) values (xxx)
        ContentValues values = new ContentValues();
        values.put("number",msgInfo.getNumber());
        values.put("msg", msgInfo.getMsg());
        values.put("gpsjd",msgInfo.getGpsjd());
        values.put("gpswd",msgInfo.getGpswd());
        values.put("time",msgInfo.getTime());
        values.put("rili",msgInfo.getRili());
        values.put("type",msgInfo.getType());
        values.put("img",msgInfo.getImg());
        long id = database.insert("msg_info", null, values);
        Log.e("TAG", "id=" + id);
        //设置id
        msgInfo.setId((int) id);
        //3.关闭
        database.close();
    }
    /**
     * 根据id删除一条记录
     */
    public void deleteById(int id) {
        //1.得到连接
        SQLiteDatabase database = msgRevHelper.getReadableDatabase();
        //2.执行delete delete from black_number where _id=id
        int deleteCount = database.delete("msg_info", "_id=?", new String[]{id+""});
        Log.e("TAG", "deleteCount=" + deleteCount);
        //3.关闭
        database.close();
    }
    /***
     * 更新一条记录
     *
     */
    public void update(MsgInfo msgInfo) {
        //1.得到连接
        SQLiteDatabase database = msgRevHelper.getReadableDatabase();
        //2.执行update update black_number set number=xxx where _id=id
        ContentValues values = new ContentValues();
        values.put("number",msgInfo.getNumber());
        values.put("msg", msgInfo.getMsg());
        values.put("gpsjd",msgInfo.getGpsjd());
        values.put("gpswd",msgInfo.getGpswd());
        values.put("time",msgInfo.getTime());
        values.put("rili",msgInfo.getRili());
        values.put("type",msgInfo.getType());
        int updateCount = database.update("msg_info", values, "_id=" + msgInfo.getId(), null);
        Log.e("TAG", "updateCount=" + updateCount);
        //3.关闭
        database.close();
    }
    /**
     * 查询所有记录封装成List<MsgInfo>
     */
    public List<MsgInfo> getRev() {
        List<MsgInfo> list= new ArrayList<MsgInfo>();
        //1.得到连接
        SQLiteDatabase database = msgRevHelper.getReadableDatabase();
        //2.执行query select * from black number
        //查询根据_id 实现倒序
        Cursor cursor = database.query("msg_info",null, "type=?", new String[]{"1"}, null, null, "_id desc");
        //3.从cursor中取出所有数据并封装到List中
        while (cursor.moveToNext()){
            //id
            int id=cursor.getInt(0);
            //number
            String number=cursor.getString(cursor.getColumnIndex("number"));
            String msg=cursor.getString(cursor.getColumnIndex("msg"));
            String gpsjd=cursor.getString(cursor.getColumnIndex("gpsjd"));
            String gpswd=cursor.getString(cursor.getColumnIndex("gpswd"));
            String time=cursor.getString(cursor.getColumnIndex("time"));
            String rili=cursor.getString(cursor.getColumnIndex("rili"));
            int type=cursor.getInt(cursor.getColumnIndex("type"));
            int img=cursor.getInt(cursor.getColumnIndex("img"));
            list.add(new MsgInfo(id,number,msg,gpsjd,gpswd,time,rili,type,img));
        }
        //3.关闭
        cursor.close();
        database.close();
        return list;
    }
    public List<MsgInfo> getSend() {
        List<MsgInfo> list= new ArrayList<MsgInfo>();
        //1.得到连接
        SQLiteDatabase database = msgRevHelper.getReadableDatabase();
        //2.执行query select * from black number
        //查询根据_id 实现倒序
        Cursor cursor = database.query("msg_info",null, "type=?", new String[]{"2"}, null, null, "_id desc");
        //3.从cursor中取出所有数据并封装到List中
        while (cursor.moveToNext()){
            //id
            int id=cursor.getInt(0);
            //number
            String number=cursor.getString(cursor.getColumnIndex("number"));
            String msg=cursor.getString(cursor.getColumnIndex("msg"));
            String gpsjd=cursor.getString(cursor.getColumnIndex("gpsjd"));
            String gpswd=cursor.getString(cursor.getColumnIndex("gpswd"));
            String time=cursor.getString(cursor.getColumnIndex("time"));
            String rili=cursor.getString(cursor.getColumnIndex("rili"));
            int type=cursor.getInt(cursor.getColumnIndex("type"));
            int img=cursor.getInt(cursor.getColumnIndex("img"));
            list.add(new MsgInfo(id,number,msg,gpsjd,gpswd,time,rili,type,img));
        }
        //3.关闭
        cursor.close();
        database.close();
        return list;
    }

    public List<MsgInfo> getPrepared() {
        List<MsgInfo> list= new ArrayList<MsgInfo>();
        //1.得到连接
        SQLiteDatabase database = msgRevHelper.getReadableDatabase();
        //2.执行query select * from black number
        //查询根据_id 实现倒序
        Cursor cursor = database.query("msg_info",null, "type=?", new String[]{"3"}, null, null, "_id desc");
        //3.从cursor中取出所有数据并封装到List中
        while (cursor.moveToNext()){
            //id
            int id=cursor.getInt(0);
            //number
            String number=cursor.getString(cursor.getColumnIndex("number"));
            String msg=cursor.getString(cursor.getColumnIndex("msg"));
            String gpsjd=cursor.getString(cursor.getColumnIndex("gpsjd"));
            String gpswd=cursor.getString(cursor.getColumnIndex("gpswd"));
            String time=cursor.getString(cursor.getColumnIndex("time"));
            String rili=cursor.getString(cursor.getColumnIndex("rili"));
            int type=cursor.getInt(cursor.getColumnIndex("type"));
            list.add(new MsgInfo(id,number,msg,gpsjd,gpswd,time,rili,type,0));
        }
        //3.关闭
        cursor.close();
        database.close();
        return list;
    }
}
