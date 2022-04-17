package com.example.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.model.StudyRecord;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

public class StudyRecordDao {
    private DBOpenHelper helper;
    private SQLiteDatabase db;
    private Context context;
    //构造方法
    public StudyRecordDao(Context context){ this.context=context; }

    //添加方法
    public void add(StudyRecord studyRecord){
        helper=new DBOpenHelper(context);
        db=helper.getReadableDatabase();

        ContentValues values=new ContentValues();
        values.put("date", studyRecord.getDate());
        values.put("newNum", studyRecord.getNewNum());
        values.put("repeatNum", studyRecord.getRepeatNum());
        values.put("needNewNum", studyRecord.getNeedNewNum());
        values.put("needRepeatNum", studyRecord.getNeedRepeatNum());
        values.put("difficulty", studyRecord.getDifficulty());
        db.insert("tb_studyrecord",null,values);
        //关闭数据库
        db.close();
        helper.close();
    }
    //更新方法
    public void update(StudyRecord studyRecord){
        helper=new DBOpenHelper(context);
        db=helper.getReadableDatabase();

        ContentValues values=new ContentValues();
        values.put("date", studyRecord.getDate());
        values.put("newNum", studyRecord.getNewNum());
        values.put("repeatNum", studyRecord.getRepeatNum());
        values.put("needNewNum", studyRecord.getNeedNewNum());
        values.put("needRepeatNum", studyRecord.getNeedRepeatNum());
        db.update("tb_studyrecord",values,"_id=?",new String[]{String.valueOf(studyRecord.get_id())});
        //关闭数据库
        db.close();
        helper.close();
    }
    //插入或者返回方法,若有今天数据就返回，没有就新建
    public StudyRecord addOrGet(){
        helper=new DBOpenHelper(context);
        db=helper.getReadableDatabase();
        SettingDao settingDao=new SettingDao(context);
        final Calendar c=Calendar.getInstance();//获取系统当前数据
        int year=c.get(Calendar.YEAR);
        int month=c.get(Calendar.MONTH)+1;
        int day=c.get(Calendar.DAY_OF_MONTH);
        String date0=year+"-"+month+"-"+day;
        String difficulty=settingDao.getDifficulty();
        String sql="select * from tb_studyrecord where date=? and difficulty=?";
        Cursor cursor=db.rawQuery(sql,new String[]{date0,difficulty});
        StudyRecord studyRecord;
        if (cursor.moveToNext()){
            int _id=cursor.getInt(cursor.getColumnIndex("_id"));
            String date=cursor.getString(cursor.getColumnIndex("date"));
            int firstNum=cursor.getInt(cursor.getColumnIndex("newNum"));
            int repeatNum=cursor.getInt(cursor.getColumnIndex("repeatNum"));
            //获取最新的每天需要背的单词数量
            int needNewNum=settingDao.getNewNum();
            int needRepeatNum=cursor.getInt(cursor.getColumnIndex("needRepeatNum"));
            studyRecord =new StudyRecord(_id,date,firstNum,repeatNum,needNewNum,needRepeatNum,difficulty);
            cursor.close();
            db.close();
            helper.close();
            update(studyRecord);
        }else {//没有就新建
            Log.i("今日首次进软件","新建了今日背单词的数据");
            int firstNum=0;
            int repeatNum=0;
            int needNewNum=settingDao.getNewNum();
            WordRecordDao wordRecordDao =new WordRecordDao(context);
            int needRepeatNum= wordRecordDao.getTypeReWordCount();
            studyRecord =new StudyRecord(date0,firstNum,repeatNum,needNewNum,needRepeatNum,difficulty);
            add(studyRecord);
        }
        return studyRecord;
    }

    //返回列表中的7条数据
    public ArrayList<StudyRecord> getWeekRecord(){
        ArrayList<StudyRecord> studyRecord_list =new ArrayList<StudyRecord>();
        helper=new DBOpenHelper(context);
        db=helper.getReadableDatabase();
        SettingDao settingDao=new SettingDao(context);
        String difficulty=settingDao.getDifficulty();
        String sql="select * from tb_studyrecord  where difficulty=? order by date DESC limit 7 ";
        Cursor cursor=db.rawQuery(sql,new String[]{difficulty});
        while (cursor.moveToNext()){
            int _id=cursor.getInt(cursor.getColumnIndex("_id"));
           String date=cursor.getString(cursor.getColumnIndex("date"));
            int newNum=cursor.getInt(cursor.getColumnIndex("newNum"));
            int repeatNum=cursor.getInt(cursor.getColumnIndex("repeatNum"));
            int needNewNum=cursor.getInt(cursor.getColumnIndex("needNewNum"));
            int needRepeatNum=cursor.getInt(cursor.getColumnIndex("needRepeatNum"));
            StudyRecord studyRecord =new StudyRecord(_id,date,newNum,repeatNum,needNewNum,needRepeatNum,difficulty);
            studyRecord_list.add(studyRecord);
        }
        cursor.close();
        db.close();
        helper.close();
        Collections.reverse(studyRecord_list);//对数据进行翻转，将今天放在最后
        //Log.i("hahaha","---------------------------->"+ studyRecord_list.size());
        return studyRecord_list;
    }

    //返回总共学习的天数
    public int getAllStudyDayCount(){
        helper=new DBOpenHelper(context);
        db=helper.getReadableDatabase();

        String sql="select count(Distinct date) from tb_studyrecord";
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

    //更新今天已经背的单词数
    public void updatefirstNum(){
        StudyRecord studyRecord =addOrGet();
        studyRecord.setNewNum(studyRecord.getNewNum()+1);
        update(studyRecord);
    }
    //更新今天已经复习的单词数
    public void updatereviewNum(){
        StudyRecord studyRecord =addOrGet();
        studyRecord.setRepeatNum(studyRecord.getRepeatNum()+1);
        update(studyRecord);
    }

}
