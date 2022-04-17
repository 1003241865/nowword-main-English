package com.example.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.model.Wisdom;

import java.util.Random;

public class WisdomDao {
    private DBOpenHelper helper;
    private SQLiteDatabase db;
    private Context context;
    //构造方法
    public WisdomDao(Context context){
        this.context=context;
    }
    //获取指定id的名人名句
    public Wisdom find(int id){
        helper=new DBOpenHelper(context);
        db=helper.getReadableDatabase();

        String sql="select * from tb_wisdom where _id=?";
        Cursor cursor=db.rawQuery(sql,new String[]{String.valueOf(String.valueOf(id))});
        if (cursor.moveToNext()){
            int _id=cursor.getInt(cursor.getColumnIndex("_id"));
            String chinese_mean=cursor.getString(cursor.getColumnIndex("chinese_mean"));
            String english_mean=cursor.getString(cursor.getColumnIndex("english_mean"));
            Wisdom wisdom=new Wisdom(_id,chinese_mean,english_mean);
            //关闭游标和数据库
            cursor.close();
            db.close();
            helper.close();
            return wisdom;
        }
        //关闭游标和数据库
        cursor.close();
        db.close();
        helper.close();
        return null;
    }

    //随机获取一条名人名句
    public Wisdom getRandomWisdom(){
        Random random=new Random();
        int i=random.nextInt(getWisdomCount());
        Wisdom wisdom=find(i);
        return wisdom;
    }

    //获取名人名句的总数量
    public int getWisdomCount(){
        helper=new DBOpenHelper(context);
        db=helper.getReadableDatabase();
        String sql="select count(_id) from tb_wisdom";
        Cursor cursor=db.rawQuery(sql,null);
        if (cursor.moveToNext()){
            int count= cursor.getInt(0);
            //关闭游标和数据库
            cursor.close();
            db.close();
            helper.close();
            return count;
        }
        return 0;
    }
}
