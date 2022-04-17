package com.example.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.model.Setting;
import com.example.model.Word;
import com.example.model.WordType;

import java.util.ArrayList;
import java.util.List;

public class WordTypeDao {
    private DBOpenHelper helper;
    private SQLiteDatabase db;
    private Context context;
    //构造方法
    public WordTypeDao(Context context){
        this.context=context;
    }

    //添加方法，添加新的单词本
    public void add(WordType wordType){
        helper=new DBOpenHelper(context);
        db=helper.getReadableDatabase();
        ContentValues values=new ContentValues();
        values.put("wordType",wordType.getWordType());
        values.put("context",wordType.getContext());
        values.put("isSystem",wordType.getIsSystem());
        db.insert("tb_wordType",null,values);
        //关闭数据库
        db.close();
        helper.close();
    }

    //删除单词本方法
    public void delete(WordType wordType){
        helper=new DBOpenHelper(context);
        db=helper.getReadableDatabase();
        db.delete("tb_wordType","wordType=?",new String[]{wordType.getWordType()});
        db.close();
        helper.close();
    }
    //更新方法
    public void update(WordType wordType)  {
        helper=new DBOpenHelper(context);
        db=helper.getReadableDatabase();
        ContentValues values=new ContentValues();
        values.put("wordType",wordType.getWordType());
        values.put("context",wordType.getContext());
        values.put("isSystem",wordType.getIsSystem());
        db.update("tb_wordType",values,"wordType=?",new String[]{wordType.getWordType()});
        //关闭数据库
        db.close();
        helper.close();
    }

    //查找方法
    public WordType find(WordType wordType){
        helper=new DBOpenHelper(context);
        db=helper.getReadableDatabase();

        String sql="select * from tb_wordType where wordType=?";
        Cursor cursor=db.rawQuery(sql,new String[]{wordType.getWordType()});
        if (cursor.moveToNext()){
            String wordType0=cursor.getString(cursor.getColumnIndex("wordType"));
            String context0=cursor.getString(cursor.getColumnIndex("context"));
            int isSystem0=cursor.getInt(cursor.getColumnIndex("isSystem"));
            WordType find_wordType=new WordType(wordType0,context0,isSystem0);
            //关闭游标和数据库
            cursor.close();
            db.close();
            helper.close();
            return find_wordType;
        }//查不到返回空
        //关闭游标和数据库
        cursor.close();
        db.close();
        helper.close();
        return null;
    }
    //查找方法
    public WordType find(String wordType){
        helper=new DBOpenHelper(context);
        db=helper.getReadableDatabase();

        String sql="select * from tb_wordType where wordType=?";
        Cursor cursor=db.rawQuery(sql,new String[]{wordType});
        if (cursor.moveToNext()){
            String wordType0=cursor.getString(cursor.getColumnIndex("wordType"));
            String context0=cursor.getString(cursor.getColumnIndex("context"));
            int isSystem0=cursor.getInt(cursor.getColumnIndex("isSystem"));
            WordType find_wordType=new WordType(wordType0,context0,isSystem0);
            //关闭游标和数据库
            cursor.close();
            db.close();
            helper.close();
            return find_wordType;
        }//查不到返回空
        //关闭游标和数据库
        cursor.close();
        db.close();
        helper.close();
        return null;
    }

    //获取所有的难度单词本
    public List<WordType> getAllType(){
        List<WordType> types=new ArrayList<>();
        helper=new DBOpenHelper(context);
        db=helper.getReadableDatabase();

        String sql="select *  from tb_wordType";

        Cursor cursor=db.rawQuery(sql,null);
        while (cursor.moveToNext()){
            String wordType0=cursor.getString(cursor.getColumnIndex("wordType"));
            String context0=cursor.getString(cursor.getColumnIndex("context"));
            int isSystem0=cursor.getInt(cursor.getColumnIndex("isSystem"));
            WordType wordType=new WordType(wordType0,context0,isSystem0);
            types.add(wordType);
        }
        //关闭游标和数据库
        cursor.close();
        db.close();
        helper.close();
        return types;
    }


}
