package com.example.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.model.Word;
import com.example.model.WordRecord;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

public class WordRecordDao {
    private DBOpenHelper helper;
    private SQLiteDatabase db;
    private Context context;
    //构造方法
    public WordRecordDao(Context context){
        this.context=context;
    }

    //添加方法
    public void add(WordRecord wordRecord) {
        helper=new DBOpenHelper(context);
        db=helper.getReadableDatabase();

        ContentValues values=new ContentValues();
        values.put("wordId", wordRecord.getWordId());
        values.put("isFalse", wordRecord.getIsFalse());
        values.put("isFlag", wordRecord.getIsFlag());
        values.put("reperaNum", wordRecord.getReperaNum());
        values.put("timeFirst", wordRecord.getTimeFirst());
        values.put("timeFinish", wordRecord.getTimeFinish());
        db.insert("tb_wordrecord",null,values);
        //关闭数据库
        db.close();
        helper.close();
    }

    //更新方法
    public void update(WordRecord wordRecord) {
        helper=new DBOpenHelper(context);
        db=helper.getReadableDatabase();

        ContentValues values=new ContentValues();
        values.put("wordId", wordRecord.getWordId());
        values.put("isFalse", wordRecord.getIsFalse());
        values.put("isFlag", wordRecord.getIsFlag());
        values.put("reperaNum", wordRecord.getReperaNum());
        values.put("timeFirst", wordRecord.getTimeFirst());
        values.put("timeFinish", wordRecord.getTimeFinish());
        db.update("tb_wordrecord",values,"wordId=?",new String[]{String.valueOf(wordRecord.getWordId())});
        //关闭数据库
        db.close();
        helper.close();
    }

    //查找方法
    public WordRecord find(Word word) {
        helper=new DBOpenHelper(context);
        db=helper.getReadableDatabase();

        String sql="select * from tb_wordrecord where wordId=?";
        Cursor cursor=db.rawQuery(sql,new String[]{String.valueOf(word.get_id())});
        if (cursor.moveToNext()){
            int _id=cursor.getInt(cursor.getColumnIndex("_id"));
            int wordId=cursor.getInt(cursor.getColumnIndex("wordId"));
            int isFalse=cursor.getInt(cursor.getColumnIndex("isFalse"));
            int isFlag=cursor.getInt(cursor.getColumnIndex("isFlag"));
            int reperaNum=cursor.getInt(cursor.getColumnIndex("reperaNum"));
            long timeFirst=cursor.getLong(cursor.getColumnIndex("timeFirst"));
            long timeFinish=cursor.getLong(cursor.getColumnIndex("timeFinish"));
            WordRecord wordRecord =new WordRecord(_id,wordId,isFalse,isFlag,reperaNum,timeFirst,timeFinish);
            //关闭游标和数据库
            cursor.close();
            db.close();
            helper.close();
            return wordRecord;
        }
        //关闭游标和数据库
        cursor.close();
        db.close();
        helper.close();
        return null;
    }

    //返回本难度所有背过的单词数据
    public List<Word> getLearnedWords() {
        List<Word> words_list=new ArrayList<Word>();
        helper=new DBOpenHelper(context);
        db=helper.getReadableDatabase();
        SettingDao settingDao=new SettingDao(context);
        String type=settingDao.getDifficulty();
        String sql="select * from tb_word,tb_wordrecord where tb_word._id=tb_wordrecord.wordId and tb_word.wordType=? and reperaNum>0";
        Cursor cursor=db.rawQuery(sql,new String[]{type});
        while (cursor.moveToNext()){
            int _id=cursor.getInt(cursor.getColumnIndex("wordId"));
            int wordRank=cursor.getInt(cursor.getColumnIndex("wordRank"));
            String headWord=cursor.getString(cursor.getColumnIndex("headWord"));
            String sentences=cursor.getString(cursor.getColumnIndex("sentences"));
            String usphone=cursor.getString(cursor.getColumnIndex("usphone"));
            String ukphone=cursor.getString(cursor.getColumnIndex("ukphone"));
            String syno=cursor.getString(cursor.getColumnIndex("syno"));
            String phrases=cursor.getString(cursor.getColumnIndex("phrases"));
            String tranCN=cursor.getString(cursor.getColumnIndex("tranCN"));
            String tranEN=cursor.getString(cursor.getColumnIndex("tranEN"));
            String wordType=cursor.getString(cursor.getColumnIndex("wordType"));
            Word word=new Word(_id,wordRank,headWord,sentences,usphone,ukphone,syno,phrases,tranCN,tranEN,wordType);
            words_list.add(word);
        }
        cursor.close();
        db.close();
        helper.close();
        return words_list;
    }
    //返回本难度所有背过的单词数据
    public List<Word> getLearnedWords(String type) {
        List<Word> words_list=new ArrayList<Word>();
        helper=new DBOpenHelper(context);
        db=helper.getReadableDatabase();
        String sql="select * from tb_word,tb_wordrecord where tb_word._id=tb_wordrecord.wordId and tb_word.wordType=? and reperaNum>0";
        Cursor cursor=db.rawQuery(sql,new String[]{type});
        while (cursor.moveToNext()){
            int _id=cursor.getInt(cursor.getColumnIndex("wordId"));
            int wordRank=cursor.getInt(cursor.getColumnIndex("wordRank"));
            String headWord=cursor.getString(cursor.getColumnIndex("headWord"));
            String sentences=cursor.getString(cursor.getColumnIndex("sentences"));
            String usphone=cursor.getString(cursor.getColumnIndex("usphone"));
            String ukphone=cursor.getString(cursor.getColumnIndex("ukphone"));
            String syno=cursor.getString(cursor.getColumnIndex("syno"));
            String phrases=cursor.getString(cursor.getColumnIndex("phrases"));
            String tranCN=cursor.getString(cursor.getColumnIndex("tranCN"));
            String tranEN=cursor.getString(cursor.getColumnIndex("tranEN"));
            String wordType=cursor.getString(cursor.getColumnIndex("wordType"));
            Word word=new Word(_id,wordRank,headWord,sentences,usphone,ukphone,syno,phrases,tranCN,tranEN,wordType);
            words_list.add(word);
        }
        cursor.close();
        db.close();
        helper.close();
        return words_list;
    }


    //返回本难度需复习的单词数据
    public List<Word> getNeedReWords(int todayneedreview) {
        List<Word> words_list=new ArrayList<Word>();
        helper=new DBOpenHelper(context);
        db=helper.getReadableDatabase();
        SettingDao settingDao=new SettingDao(context);
        String type=settingDao.getDifficulty();
        String sql="select * from tb_word,tb_wordrecord where tb_word._id=tb_wordrecord.wordId and reperaNum<5 and reperaNum>=1  and  tb_word.wordType=? order by timeFirst ASC limit ?";
        Cursor cursor=db.rawQuery(sql,new String[]{type,String.valueOf(todayneedreview)});
        while (cursor.moveToNext()){
            int _id=cursor.getInt(cursor.getColumnIndex("wordId"));
            int wordRank=cursor.getInt(cursor.getColumnIndex("wordRank"));
            String headWord=cursor.getString(cursor.getColumnIndex("headWord"));
            String sentences=cursor.getString(cursor.getColumnIndex("sentences"));
            String usphone=cursor.getString(cursor.getColumnIndex("usphone"));
            String ukphone=cursor.getString(cursor.getColumnIndex("ukphone"));
            String syno=cursor.getString(cursor.getColumnIndex("syno"));
            String phrases=cursor.getString(cursor.getColumnIndex("phrases"));
            String tranCN=cursor.getString(cursor.getColumnIndex("tranCN"));
            String tranEN=cursor.getString(cursor.getColumnIndex("tranEN"));
            String wordType=cursor.getString(cursor.getColumnIndex("wordType"));
            Word word=new Word(_id,wordRank,headWord,sentences,usphone,ukphone,syno,phrases,tranCN,tranEN,wordType);
            words_list.add(word);
        }
        cursor.close();
        db.close();
        helper.close();
        return words_list;
    }

    //返回本难度被标记的单词数据
    public List<Word> getTypeFlagWords(int needCount) {
        List<Word> words_list0=new ArrayList<Word>();
        List<Word> words_list=new ArrayList<Word>();
        helper=new DBOpenHelper(context);
        db=helper.getReadableDatabase();
        SettingDao settingDao=new SettingDao(context);
        String type=settingDao.getDifficulty();
        String sql="select * from tb_word,tb_wordrecord where tb_word._id=tb_wordrecord.wordId and isFlag=1 and  tb_word.wordType=?";
        Cursor cursor=db.rawQuery(sql,new String[]{type});
        while (cursor.moveToNext()) {
            int _id = cursor.getInt(cursor.getColumnIndex("wordId"));
            int wordRank = cursor.getInt(cursor.getColumnIndex("wordRank"));
            String headWord = cursor.getString(cursor.getColumnIndex("headWord"));
            String sentences = cursor.getString(cursor.getColumnIndex("sentences"));
            String usphone = cursor.getString(cursor.getColumnIndex("usphone"));
            String ukphone = cursor.getString(cursor.getColumnIndex("ukphone"));
            String syno = cursor.getString(cursor.getColumnIndex("syno"));
            String phrases = cursor.getString(cursor.getColumnIndex("phrases"));
            String tranCN = cursor.getString(cursor.getColumnIndex("tranCN"));
            String tranEN = cursor.getString(cursor.getColumnIndex("tranEN"));
            String wordType = cursor.getString(cursor.getColumnIndex("wordType"));
            Word word = new Word(_id, wordRank, headWord, sentences, usphone, ukphone, syno, phrases, tranCN, tranEN, wordType);
            words_list0.add(word);
        }
        if (words_list0.size() <= needCount) {//若单词数不够需要的则全部返回
            cursor.close();
            db.close();
            helper.close();
            return words_list0;
        } else {//若单词数够需要的则随机选择部分返回
            Random random = new Random();
            for (int i = 0; i < needCount; i++) {
                words_list.add(words_list0.get(random.nextInt(words_list0.size())));
            }
            cursor.close();
            db.close();
            helper.close();
            return words_list;
        }
    }

    //返回本难度被标记的单词数据
    public List<Word> getTypeFlagWords(String type) {
        List<Word> words_list0=new ArrayList<Word>();
        helper=new DBOpenHelper(context);
        db=helper.getReadableDatabase();
        String sql="select * from tb_word,tb_wordrecord where tb_word._id=tb_wordrecord.wordId and isFlag=1 and  tb_word.wordType=?";
        Cursor cursor=db.rawQuery(sql,new String[]{type});
        while (cursor.moveToNext()) {
            int _id = cursor.getInt(cursor.getColumnIndex("wordId"));
            int wordRank = cursor.getInt(cursor.getColumnIndex("wordRank"));
            String headWord = cursor.getString(cursor.getColumnIndex("headWord"));
            String sentences = cursor.getString(cursor.getColumnIndex("sentences"));
            String usphone = cursor.getString(cursor.getColumnIndex("usphone"));
            String ukphone = cursor.getString(cursor.getColumnIndex("ukphone"));
            String syno = cursor.getString(cursor.getColumnIndex("syno"));
            String phrases = cursor.getString(cursor.getColumnIndex("phrases"));
            String tranCN = cursor.getString(cursor.getColumnIndex("tranCN"));
            String tranEN = cursor.getString(cursor.getColumnIndex("tranEN"));
            String wordType = cursor.getString(cursor.getColumnIndex("wordType"));
            Word word = new Word(_id, wordRank, headWord, sentences, usphone, ukphone, syno, phrases, tranCN, tranEN, wordType);
            words_list0.add(word);
        }
            cursor.close();
            db.close();
            helper.close();
            return words_list0;
    }

    //返回本难度错误过的单词数据，按错误次数从大到小排列
    public List<Word> getWrongWords(int needCount){
        List<Word> words_list0=new ArrayList<Word>();
        List<Word> words_list=new ArrayList<Word>();
        helper=new DBOpenHelper(context);
        db=helper.getReadableDatabase();
        SettingDao settingDao=new SettingDao(context);
        String type=settingDao.getDifficulty();
        String sql="select * from tb_word,tb_wordrecord where tb_word._id=tb_wordrecord.wordId and isFalse>0 and  tb_word.wordType=? order by isFalse DESC";
        Cursor cursor=db.rawQuery(sql,new String[]{type});
        while (cursor.moveToNext()){
            int _id=cursor.getInt(cursor.getColumnIndex("wordId"));
            int wordRank=cursor.getInt(cursor.getColumnIndex("wordRank"));
            String headWord=cursor.getString(cursor.getColumnIndex("headWord"));
            String sentences=cursor.getString(cursor.getColumnIndex("sentences"));
            String usphone=cursor.getString(cursor.getColumnIndex("usphone"));
            String ukphone=cursor.getString(cursor.getColumnIndex("ukphone"));
            String syno=cursor.getString(cursor.getColumnIndex("syno"));
            String phrases=cursor.getString(cursor.getColumnIndex("phrases"));
            String tranCN=cursor.getString(cursor.getColumnIndex("tranCN"));
            String tranEN=cursor.getString(cursor.getColumnIndex("tranEN"));
            String wordType=cursor.getString(cursor.getColumnIndex("wordType"));
            Word word=new Word(_id,wordRank,headWord,sentences,usphone,ukphone,syno,phrases,tranCN,tranEN,wordType);
            words_list0.add(word);
        }
        if (words_list0.size() <= needCount) {//若单词数不够需要的则全部返回
            cursor.close();
            db.close();
            helper.close();
            return words_list0;
        } else {//若单词数够需要的则随机选择部分返回
            Random random = new Random();
            for (int i = 0; i < needCount; i++) {
                words_list.add(words_list0.get(random.nextInt(words_list0.size())));
            }
            cursor.close();
            db.close();
            helper.close();
            return words_list;
        }
    }

    //返回目前类型已背诵和完成复习的单词数据
    public List<Word> getTypeFinishWords(int needCount){
        List<Word> words_list0=new ArrayList<Word>();
        List<Word> words_list=new ArrayList<Word>();
        helper=new DBOpenHelper(context);
        db=helper.getReadableDatabase();
        SettingDao settingDao=new SettingDao(context);
        String type=settingDao.getDifficulty();
        String sql="select * from tb_word,tb_wordrecord where tb_word._id=tb_wordrecord.wordId and reperaNum>=5 and tb_word.wordType=? order by timeFinish DESC";
        Cursor cursor=db.rawQuery(sql,new String[]{type});
        while (cursor.moveToNext()){
            int _id=cursor.getInt(cursor.getColumnIndex("wordId"));
            int wordRank=cursor.getInt(cursor.getColumnIndex("wordRank"));
            String headWord=cursor.getString(cursor.getColumnIndex("headWord"));
            String sentences=cursor.getString(cursor.getColumnIndex("sentences"));
            String usphone=cursor.getString(cursor.getColumnIndex("usphone"));
            String ukphone=cursor.getString(cursor.getColumnIndex("ukphone"));
            String syno=cursor.getString(cursor.getColumnIndex("syno"));
            String phrases=cursor.getString(cursor.getColumnIndex("phrases"));
            String tranCN=cursor.getString(cursor.getColumnIndex("tranCN"));
            String tranEN=cursor.getString(cursor.getColumnIndex("tranEN"));
            String wordType=cursor.getString(cursor.getColumnIndex("wordType"));
            Word word=new Word(_id,wordRank,headWord,sentences,usphone,ukphone,syno,phrases,tranCN,tranEN,wordType);
            words_list0.add(word);
        }
        if (words_list0.size() <= needCount) {//若单词数不够需要的则全部返回
            cursor.close();
            db.close();
            helper.close();
            return words_list0;
        } else {//若单词数够需要的则随机选择部分返回
            Random random = new Random();
            for (int i = 0; i < needCount; i++) {
                words_list.add(words_list0.get(random.nextInt(words_list0.size())));
            }
            cursor.close();
            db.close();
            helper.close();
            return words_list;
        }
    }
    //返回目前类型已背诵和完成复习的单词数据
    public List<Word> getTypeFinishWords(String type){
        List<Word> words_list0=new ArrayList<Word>();
        helper=new DBOpenHelper(context);
        db=helper.getReadableDatabase();
        String sql="select * from tb_word,tb_wordrecord where tb_word._id=tb_wordrecord.wordId and reperaNum>=5 and tb_word.wordType=? order by timeFinish DESC";
        Cursor cursor=db.rawQuery(sql,new String[]{type});
        while (cursor.moveToNext()){
            int _id=cursor.getInt(cursor.getColumnIndex("wordId"));
            int wordRank=cursor.getInt(cursor.getColumnIndex("wordRank"));
            String headWord=cursor.getString(cursor.getColumnIndex("headWord"));
            String sentences=cursor.getString(cursor.getColumnIndex("sentences"));
            String usphone=cursor.getString(cursor.getColumnIndex("usphone"));
            String ukphone=cursor.getString(cursor.getColumnIndex("ukphone"));
            String syno=cursor.getString(cursor.getColumnIndex("syno"));
            String phrases=cursor.getString(cursor.getColumnIndex("phrases"));
            String tranCN=cursor.getString(cursor.getColumnIndex("tranCN"));
            String tranEN=cursor.getString(cursor.getColumnIndex("tranEN"));
            String wordType=cursor.getString(cursor.getColumnIndex("wordType"));
            Word word=new Word(_id,wordRank,headWord,sentences,usphone,ukphone,syno,phrases,tranCN,tranEN,wordType);
            words_list0.add(word);
        }
            cursor.close();
            db.close();
            helper.close();
            return words_list0;
    }
    //返回某难度单词数据，按第一次背诵的时间排序
    public List<Word> getFirstTimeWords(){
        List<Word> words_list=new ArrayList<Word>();
        helper=new DBOpenHelper(context);
        db=helper.getReadableDatabase();
        SettingDao settingDao=new SettingDao(context);
        String type=settingDao.getDifficulty();
        String sql="select * from tb_word,tb_wordrecord where tb_word._id=tb_wordrecord.wordId and tb_word.wordType=? order by timeFirst DESC ";
        Cursor cursor=db.rawQuery(sql,new String[]{type});
        while (cursor.moveToNext()){
            int _id=cursor.getInt(cursor.getColumnIndex("wordId"));
            int wordRank=cursor.getInt(cursor.getColumnIndex("wordRank"));
            String headWord=cursor.getString(cursor.getColumnIndex("headWord"));
            String sentences=cursor.getString(cursor.getColumnIndex("sentences"));
            String usphone=cursor.getString(cursor.getColumnIndex("usphone"));
            String ukphone=cursor.getString(cursor.getColumnIndex("ukphone"));
            String syno=cursor.getString(cursor.getColumnIndex("syno"));
            String phrases=cursor.getString(cursor.getColumnIndex("phrases"));
            String tranCN=cursor.getString(cursor.getColumnIndex("tranCN"));
            String tranEN=cursor.getString(cursor.getColumnIndex("tranEN"));
            String wordType=cursor.getString(cursor.getColumnIndex("wordType"));
            Word word=new Word(_id,wordRank,headWord,sentences,usphone,ukphone,syno,phrases,tranCN,tranEN,wordType);
            words_list.add(word);
        }
        cursor.close();
        db.close();
        helper.close();
        return words_list;
    }

    //返回单词数据，按最终完成背诵和复习的时间排序
    public List<Word> getFinishTimeWords(){
        List<Word> words_list=new ArrayList<Word>();
        helper=new DBOpenHelper(context);
        db=helper.getReadableDatabase();
        SettingDao settingDao=new SettingDao(context);
        String type=settingDao.getDifficulty();
        String sql="select * from tb_word,tb_wordrecord where tb_word._id=tb_wordrecord.wordId and tb_word.wordType=? order by timeFinish DESC ";
        Cursor cursor=db.rawQuery(sql,new String[]{type});
        while (cursor.moveToNext()){
            int _id=cursor.getInt(cursor.getColumnIndex("wordId"));
            int wordRank=cursor.getInt(cursor.getColumnIndex("wordRank"));
            String headWord=cursor.getString(cursor.getColumnIndex("headWord"));
            String sentences=cursor.getString(cursor.getColumnIndex("sentences"));
            String usphone=cursor.getString(cursor.getColumnIndex("usphone"));
            String ukphone=cursor.getString(cursor.getColumnIndex("ukphone"));
            String syno=cursor.getString(cursor.getColumnIndex("syno"));
            String phrases=cursor.getString(cursor.getColumnIndex("phrases"));
            String tranCN=cursor.getString(cursor.getColumnIndex("tranCN"));
            String tranEN=cursor.getString(cursor.getColumnIndex("tranEN"));
            String wordType=cursor.getString(cursor.getColumnIndex("wordType"));
            Word word=new Word(_id,wordRank,headWord,sentences,usphone,ukphone,syno,phrases,tranCN,tranEN,wordType);
            words_list.add(word);
        }
        cursor.close();
        db.close();
        helper.close();
        return words_list;
    }

    //返回指定数量的错误高危单词，高危单词是错误次数大于等于2的单词
    public List<Word> getHighWrongWords(int needCount,int highWrongtime ) {//传入测试的单词数量和最小错误次数
        List<Word> words_list0 = new ArrayList<Word>();
        List<Word> words_list = new ArrayList<Word>();
        helper = new DBOpenHelper(context);
        db = helper.getReadableDatabase();
        SettingDao settingDao=new SettingDao(context);
        String type=settingDao.getDifficulty();
        String sql = "select * from tb_word,tb_wordrecord where tb_word._id=tb_wordrecord.wordId and isFalse>=? and tb_word.wordType=? order by isFalse DESC";
        Cursor cursor = db.rawQuery(sql, new String[]{String.valueOf(highWrongtime),type});
        while (cursor.moveToNext()) {
            int _id = cursor.getInt(cursor.getColumnIndex("wordId"));
            int wordRank = cursor.getInt(cursor.getColumnIndex("wordRank"));
            String headWord = cursor.getString(cursor.getColumnIndex("headWord"));
            String sentences = cursor.getString(cursor.getColumnIndex("sentences"));
            String usphone = cursor.getString(cursor.getColumnIndex("usphone"));
            String ukphone = cursor.getString(cursor.getColumnIndex("ukphone"));
            String syno = cursor.getString(cursor.getColumnIndex("syno"));
            String phrases = cursor.getString(cursor.getColumnIndex("phrases"));
            String tranCN = cursor.getString(cursor.getColumnIndex("tranCN"));
            String tranEN = cursor.getString(cursor.getColumnIndex("tranEN"));
            String wordType = cursor.getString(cursor.getColumnIndex("wordType"));
            Word word = new Word(_id, wordRank, headWord, sentences, usphone, ukphone, syno, phrases, tranCN, tranEN, wordType);
            words_list0.add(word);
        }
        if (words_list0.size() <= needCount) {//若单词数不够需要的则全部返回
            cursor.close();
            db.close();
            helper.close();
            return words_list0;
        } else {//若单词数够需要的则随机选择部分返回
            Random random = new Random();
            for (int i = 0; i < needCount; i++) {
                words_list.add(words_list0.get(random.nextInt(words_list0.size())));
            }
            cursor.close();
            db.close();
            helper.close();
            return words_list;
        }
    }
    //返回指定数量的错误高危单词，高危单词是错误次数大于等于2的单词
    public List<Word> getHighWrongWords(String type,int highWrongtime ) {//传入测试的单词数量和最小错误次数
        List<Word> words_list0 = new ArrayList<Word>();
        helper = new DBOpenHelper(context);
        db = helper.getReadableDatabase();
        String sql = "select * from tb_word,tb_wordrecord where tb_word._id=tb_wordrecord.wordId and isFalse>=? and tb_word.wordType=? order by isFalse DESC";
        Cursor cursor = db.rawQuery(sql, new String[]{String.valueOf(highWrongtime),type});
        while (cursor.moveToNext()) {
            int _id = cursor.getInt(cursor.getColumnIndex("wordId"));
            int wordRank = cursor.getInt(cursor.getColumnIndex("wordRank"));
            String headWord = cursor.getString(cursor.getColumnIndex("headWord"));
            String sentences = cursor.getString(cursor.getColumnIndex("sentences"));
            String usphone = cursor.getString(cursor.getColumnIndex("usphone"));
            String ukphone = cursor.getString(cursor.getColumnIndex("ukphone"));
            String syno = cursor.getString(cursor.getColumnIndex("syno"));
            String phrases = cursor.getString(cursor.getColumnIndex("phrases"));
            String tranCN = cursor.getString(cursor.getColumnIndex("tranCN"));
            String tranEN = cursor.getString(cursor.getColumnIndex("tranEN"));
            String wordType = cursor.getString(cursor.getColumnIndex("wordType"));
            Word word = new Word(_id, wordRank, headWord, sentences, usphone, ukphone, syno, phrases, tranCN, tranEN, wordType);
            words_list0.add(word);
        }
            cursor.close();
            db.close();
            helper.close();
            return words_list0;
    }

    //获取总共的背过单词数
    public int getAllStudyWordCount(){
        helper=new DBOpenHelper(context);
        db=helper.getReadableDatabase();

        String sql="select count(tb_word._id) from tb_wordrecord,tb_word where tb_word._id=tb_wordrecord.wordId order by timeFirst DESC";
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

    //获取某类型的背过单词数
    public int getTypeStudyWordCount(){
        helper=new DBOpenHelper(context);
        db=helper.getReadableDatabase();
        SettingDao settingDao=new SettingDao(context);
        String difficulty=settingDao.getDifficulty();
        String sql="select count(tb_word._id) from tb_wordrecord,tb_word where wordType=? and reperaNum>0 and tb_word._id=tb_wordrecord.wordId order by timeFirst DESC";
        Cursor cursor=db.rawQuery(sql,new String[]{difficulty});
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

    //获取总共的已完成背诵和复习的单词数
    public int getAllFinishWordCount(){
        helper=new DBOpenHelper(context);
        db=helper.getReadableDatabase();
        String sql="select count(tb_word._id) from tb_wordrecord,tb_word where tb_word._id=tb_wordrecord.wordId and reperaNum>=5 order by timeFinish DESC";
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

    //获取某类型的已完成背诵和复习的单词数
    public int getTypeFinishWordCount(){
        helper=new DBOpenHelper(context);
        db=helper.getReadableDatabase();
        SettingDao settingDao=new SettingDao(context);
        String difficulty=settingDao.getDifficulty();
        String sql="select count(tb_word._id) from tb_wordrecord,tb_word where wordType=? and tb_word._id=tb_wordrecord.wordId and reperaNum>=5 ";
        Cursor cursor=db.rawQuery(sql,new String[]{difficulty});
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
    //获取某类型的已完成背诵和复习的单词数
    public int getTypeFinishWordCount(String difficulty){
        helper=new DBOpenHelper(context);
        db=helper.getReadableDatabase();
        String sql="select count(tb_word._id) from tb_wordrecord,tb_word where wordType=? and tb_word._id=tb_wordrecord.wordId and reperaNum>=5 ";
        Cursor cursor=db.rawQuery(sql,new String[]{difficulty});
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
    //获取所有的标记的单词数
    public int getAllFlagWordCount(){
        helper=new DBOpenHelper(context);
        db=helper.getReadableDatabase();
        String sql="select count(tb_word._id) from tb_wordrecord,tb_word where  tb_word._id=tb_wordrecord.wordId and isFlag=1";
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

    //获取某类型的标记的单词数
    public int getTypeFlagWordCount(){
        helper=new DBOpenHelper(context);
        db=helper.getReadableDatabase();
        SettingDao settingDao=new SettingDao(context);
        String difficulty=settingDao.getDifficulty();
        String sql="select count(tb_word._id) from tb_wordrecord,tb_word where wordType=? and tb_word._id=tb_wordrecord.wordId and isFlag=1";
        Cursor cursor=db.rawQuery(sql,new String[]{difficulty});
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
    //获取某难度需要复习的单词数量
    public int getTypeReWordCount(){
        helper=new DBOpenHelper(context);
        db=helper.getReadableDatabase();
        SettingDao settingDao=new SettingDao(context);
        String difficulty=settingDao.getDifficulty();
        String sql="select count(tb_word._id) from tb_wordrecord,tb_word where tb_word._id=tb_wordrecord.wordId and reperaNum<5 and reperaNum>0 and wordType=?";
        Cursor cursor=db.rawQuery(sql,new String[]{difficulty});
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
    //获所有难度有错误的单词数
    public int getAllWrongWordCount(){
        helper=new DBOpenHelper(context);
        db=helper.getReadableDatabase();
        String sql="select count(tb_word._id) from tb_wordrecord,tb_word where tb_word._id=tb_wordrecord.wordId and isFalse>0";
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
    //获取某个难度错误的单词数
    public int getTypeWrongWordCount(){
        helper=new DBOpenHelper(context);
        db=helper.getReadableDatabase();
        SettingDao settingDao=new SettingDao(context);
        String difficulty=settingDao.getDifficulty();
        String sql="select count(tb_word._id) from tb_wordrecord,tb_word where tb_word._id=tb_wordrecord.wordId and isFalse>0 and wordType=?";
        Cursor cursor=db.rawQuery(sql,new String[]{difficulty});
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
    //获取某个难度错误的单词数
    public int getTypeWrongWordCount(String difficulty){
        helper=new DBOpenHelper(context);
        db=helper.getReadableDatabase();
        String sql="select count(tb_word._id) from tb_wordrecord,tb_word where tb_word._id=tb_wordrecord.wordId and isFalse>0 and wordType=?";
        Cursor cursor=db.rawQuery(sql,new String[]{difficulty});
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
    //获取所有的易错单词数量
    public int getAllHighWrongWordCount(int miniwrongcount){
        helper=new DBOpenHelper(context);
        db=helper.getReadableDatabase();
        String sql="select count(tb_word._id) from tb_wordrecord,tb_word where tb_word._id=tb_wordrecord.wordId and isFalse>? ";
        Cursor cursor=db.rawQuery(sql,new String[]{String.valueOf(miniwrongcount)});
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
    //获取某类型的易错单词数量
    public int getTypeHighWrongWordCount(int miniwrongcount){
        helper=new DBOpenHelper(context);
        db=helper.getReadableDatabase();
        SettingDao settingDao=new SettingDao(context);
        String difficulty=settingDao.getDifficulty();
        String sql="select count(tb_word._id) from tb_wordrecord,tb_word where tb_word._id=tb_wordrecord.wordId and isFalse>? and wordType=?";
        Cursor cursor=db.rawQuery(sql,new String[]{String.valueOf(miniwrongcount),difficulty});
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
    //更新复习的次数
    public void SaveWordRepeatDate(Word word){
        WordRecord wordRecord =find(word);
        wordRecord.setReperaNum(wordRecord.getReperaNum()+1);
        if (wordRecord.getReperaNum()==5){//若这是第5次复习
            wordRecord.setTimeFinish(System.currentTimeMillis());
        }
        update(wordRecord);
    }
    //保存单词数据
    public void SaveDate(Word word){
        WordRecord wordRecord =find(word);
        if(wordRecord !=null){//如果单词本中存在本单词数据

        }
        else {//如果单词本中未有此单词数据，就说明这是新背的单词
            WordRecord wordRecord1 =new WordRecord(word.get_id(),0,0,0,System.currentTimeMillis(),0);
            add(wordRecord1);
        }
    }
    //选择正确时的调用方法
    public void trueSaveDate(Word word){
            WordRecord wordRecord =find(word);
            if(wordRecord !=null){//如果单词本中存在本单词数据

            }
        else {//如果单词本中未有此单词数据，就说明这是新背的单词
            WordRecord wordRecord1 =new WordRecord(word.get_id(),0,0,1,System.currentTimeMillis(),0);
            add(wordRecord1);
        }
    }

    //选择错误时的调用方法
    public void wrongSaveDate(Word word){
        WordRecord wordRecord =find(word);
        if(wordRecord !=null){//如果单词本中存在本单词数据
            wordRecord.setIsFalse(wordRecord.getIsFalse()+1);
            update(wordRecord);
        }
        else {//如果单词本中未有此单词数据，就说明这是新背的单词
            WordRecord wordRecord1 =new WordRecord(word.get_id(),1,0,1,System.currentTimeMillis(),0);
            add(wordRecord1);
        }
    }

    //五五循环只对错误的次数进行更新
    public void fivewrongSaveDate(Word word){
        WordRecord wordRecord =find(word);
        if(wordRecord !=null){//如果单词本中存在本单词数据
            wordRecord.setIsFalse(wordRecord.getIsFalse()+1);
            update(wordRecord);
            }
        }

        //对某个单词进行标记操作
    public void setFlagWord(Word word){
        WordRecord wordRecord =find(word);
        if(wordRecord !=null){//如果可以在单词本中找到该单词，即背过该单词
            if(wordRecord.getIsFlag()==1){//如果该单词已被标记
                wordRecord.setIsFlag(0);
                update(wordRecord);
            }else {//该单词未被标记
                wordRecord.setIsFlag(1);
                update(wordRecord);
            }

        }else {//不能在单词本中找到该单词，即没有背过该单词,添加到单词本
            wordRecord =new WordRecord(word.get_id(),0,1,0,0,0);
            add(wordRecord);
        }
    }

    //对一个单词数组中的词进行标记
    public void setFlagWords(List<Word> words){
        Iterator<Word> iterator=words.iterator();
        WordRecord wordRecord;
        Word nowWord;
        while (iterator.hasNext()){
            nowWord=iterator.next();
            wordRecord =find(nowWord);
            if(wordRecord !=null){//如果可以在单词本中找到该单词，即背过该单词
                    wordRecord.setIsFlag(1);
                    update(wordRecord);
            }else {//不能在单词本中找到该单词，即没有背过该单词,添加到单词本
                wordRecord =new WordRecord(nowWord.get_id(),0,1,0,0,0);
                add(wordRecord);
            }
        }
    }

    //返回某个单词是否标记
    public boolean getWordIsFlag(Word word){
        WordRecord wordRecord =find(word);
        if(wordRecord !=null){
            if (wordRecord.getIsFlag()==1){
                return true;
            }else{
                return false;
            }
        }else {//单词本中没有该单词，即该单词肯定没有标记
            return false;
        }
    }

    //返回错误单词和所占的对应比例
    public Map<String,Float> getWrongWordRate(){
        Map<String,Float> map=new TreeMap<>();
        Cursor cursor;
        String sql;
        int count=0;
        float rate;
        float allWordCount=(float)getAllStudyWordCount();
        if(allWordCount==0){allWordCount=1.0f;}
        //错误0次的
        helper=new DBOpenHelper(context);
        db=helper.getReadableDatabase();
        sql="select count(tb_wordrecord._id) from tb_wordrecord where isFalse=0";
        cursor=db.rawQuery(sql,null);
        if (cursor.moveToNext()){
        count= cursor.getInt(0);}
        rate=count/allWordCount;
        map.put("错误0次",rate);
        //错误1-2次
        sql="select count(tb_wordrecord._id) from tb_wordrecord where isFalse<=2 and isFalse>=1";
        cursor=db.rawQuery(sql,null);
        if (cursor.moveToNext()){
            count= cursor.getInt(0);}
        rate=count/allWordCount;
        map.put("错误1-2次",rate);
        //错误3-4次
        sql="select count(tb_wordrecord._id) from tb_wordrecord where isFalse<=4 and isFalse>=3";
        cursor=db.rawQuery(sql,null);
        if (cursor.moveToNext()){
            count= cursor.getInt(0);}
        rate=count/allWordCount;
        map.put("错误3-4次",rate);
        //错误5次及以上
        sql="select count(tb_wordrecord._id) from tb_wordrecord where  isFalse>=5";
        cursor=db.rawQuery(sql,null);
        if (cursor.moveToNext()){
            count= cursor.getInt(0);}
        rate=count/allWordCount;
        map.put("错误5次及以上",rate);
        //关闭游标和数据库
        db.close();
        helper.close();
        return map;
    }

    //获取各类型的错误数量的完成数量，总数量
    public List<List<Float>> getTypeInfomation(){
        List<List<Float>> lists=new ArrayList<>();
        List<Float> typeWrong=new ArrayList<>();
        List<Float> typeFinish=new ArrayList<>();
        List<Float> typeAll=new ArrayList<>();
        WordDao wordDao=new  WordDao(context);
        List<String> types=wordDao.getAllType();
        String difficulty;
        Iterator<String> typeIterator=types.iterator();
        while (typeIterator.hasNext()){
            difficulty=typeIterator.next();
            typeAll.add((float)wordDao.getTypeCount(difficulty));
            typeFinish.add((float)getTypeFinishWordCount(difficulty));
            typeWrong.add((float)getTypeWrongWordCount(difficulty));
        }
        lists.add(typeAll);
        lists.add(typeFinish);
        lists.add(typeWrong);
        return lists;
    }
}
