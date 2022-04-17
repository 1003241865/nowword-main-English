package com.example.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.strictmode.WebViewMethodCalledOnWrongThreadViolation;

import com.example.model.Word;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WordDao {
    private DBOpenHelper helper;
    private SQLiteDatabase db;
    private Context context;
    //构造方法
    public WordDao(Context context){
        this.context=context;
    }

    //添加方法,向数据库中添加单词
    public void add(Word word) {
        helper=new DBOpenHelper(context);
        db=helper.getReadableDatabase();
        if(word.getHeadWord()==null||word.getHeadWord().trim()==""||word.getHeadWord().isEmpty()){
            return;
        }
        if(word.getTranCN()==null||word.getTranCN().trim()==""||word.getTranCN().isEmpty()){
            word.setTranCN("未知");
        }
        if(word.getSentences()==null||word.getSentences()==""||word.getSentences().isEmpty()){
            word.setSentences("未知");
        }
        if(word.getSyno()==null||word.getSyno()==""||word.getSyno().isEmpty()){
            word.setSyno("未知");
        }
        if(word.getPhrases()==null||word.getPhrases()==""||word.getPhrases().isEmpty()){
            word.setPhrases("未知");
        }
        if(word.getTranEN()==null||word.getTranEN()==""||word.getTranEN().isEmpty()){
            word.setTranEN("未知");
        }

        ContentValues values=new ContentValues();
        values.put("headWord",word.getHeadWord());
        values.put("sentences",word.getSentences());
        values.put("usphone",word.getUsphone());
        values.put("ukphone",word.getUkphone());
        values.put("syno",word.getSyno());
        values.put("phrases",word.getPhrases());
        values.put("tranCN",word.getTranCN());
        values.put("tranEN",word.getTranEN());
        values.put("wordType",word.getWordType());
        db.insert("tb_word",null,values);
        //关闭数据库
        db.close();
        helper.close();
    }

    //更新方法
    public void update(Word word) {
        helper=new DBOpenHelper(context);
        db=helper.getReadableDatabase();
        ContentValues values=new ContentValues();
        values.put("headWord",word.getHeadWord());
        values.put("sentences",word.getSentences());
        values.put("usphone",word.getUsphone());
        values.put("ukphone",word.getUkphone());
        values.put("syno",word.getSyno());
        values.put(" phrases",word.getPhrases());
        values.put("tranCN",word.getTranCN());
        values.put("tranEN",word.getTranEN());
        values.put("wordType",word.getWordType());
        db.update("tb_word",values,"_id=?",new String[]{String.valueOf(word.get_id())});
        //关闭数据库
        db.close();
        helper.close();
    }
    //更新方法
    public void delete(Word word) {
        helper=new DBOpenHelper(context);
        db=helper.getReadableDatabase();
        db.delete("tb_word","_id=?",new String[]{String.valueOf(word.get_id())});
        db.close();
        helper.close();
    }
    //查找方法
    public Word find(int wordId){
        helper=new DBOpenHelper(context);
        db=helper.getReadableDatabase();

        String sql="select * from tb_word where _id=?";
        Cursor cursor=db.rawQuery(sql,new String[]{String.valueOf(wordId)});
        if (cursor.moveToNext()){
            int _id=cursor.getInt(cursor.getColumnIndex("_id"));
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
            //关闭游标和数据库
            cursor.close();
            db.close();
            helper.close();
            return word;
        }
        //关闭游标和数据库
        cursor.close();
        db.close();
        helper.close();
        return null;
    }

    //查找方法
    public Word find(Word word0){
        helper=new DBOpenHelper(context);
        db=helper.getReadableDatabase();

        String sql="select * from tb_word where headWord=?";
        Cursor cursor=db.rawQuery(sql,new String[]{String.valueOf(word0.getHeadWord())});
        if (cursor.moveToNext()){
            int _id=cursor.getInt(cursor.getColumnIndex("_id"));
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
            //关闭游标和数据库
            cursor.close();
            db.close();
            helper.close();
            return word;
        }
        //关闭游标和数据库
        cursor.close();
        db.close();
        helper.close();
        return null;
    }

    //返回索引为start开始的count条数据
    public List<Word> getWords(int start,int neednewwordCount) {
        List<Word> words_list=new ArrayList<Word>();
        helper=new DBOpenHelper(context);
        db=helper.getReadableDatabase();
        SettingDao settingDao=new SettingDao(context);
        String type=settingDao.getDifficulty();
        System.out.println(type);
        String sql="select * from tb_word where wordType=? limit ?,? ";
        Cursor cursor=db.rawQuery(sql,new String[]{type,String.valueOf(start),String.valueOf(neednewwordCount)});
        while (cursor.moveToNext()){
            int _id=cursor.getInt(cursor.getColumnIndex("_id"));
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
    //返回某一类的单词
    public List<Word> getTypeWords() {
        List<Word> words_list=new ArrayList<Word>();
        helper=new DBOpenHelper(context);
        db=helper.getReadableDatabase();
        SettingDao settingDao=new SettingDao(context);
        String type=settingDao.getDifficulty();
        System.out.println(type);
        String sql="select * from tb_word where wordType=?  ";
        Cursor cursor=db.rawQuery(sql,new String[]{type});
        while (cursor.moveToNext()){
            int _id=cursor.getInt(cursor.getColumnIndex("_id"));
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

    //返回某一类的单词
    public List<Word> getTypeWords(String type) {
        List<Word> words_list=new ArrayList<Word>();
        helper=new DBOpenHelper(context);
        db=helper.getReadableDatabase();
        String sql="select * from tb_word where wordType=?  ";
        Cursor cursor=db.rawQuery(sql,new String[]{type});
        while (cursor.moveToNext()){
            int _id=cursor.getInt(cursor.getColumnIndex("_id"));
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
    public static boolean isContainChinese( String str) {
        String regex = "[\\u4e00-\\u9fa5]";   //汉字的Unicode取值范围
        Pattern pattern = Pattern.compile(regex);
        Matcher match = pattern.matcher(str);
        return match.find();
    }


    //返回搜索的单词
    public List<Word> getSerchWords(String str,String type) {//0表示英文，1表示中文
        String sql;
        List<Word> words_list=new ArrayList<Word>();
        helper=new DBOpenHelper(context);
        db=helper.getReadableDatabase();
        Cursor cursor;
        if (str==null){return words_list;}
        if(type.equals("all")){
            //判断传入的字符串是否包含中文
            if (!isContainChinese(str)){
                str=str+"%";
                sql="select * from tb_word where headWord like ?";
            }else {
                str="%"+str+"%";
                sql="select * from tb_word where tranCN like ?";
            }
            cursor=db.rawQuery(sql,new String[]{str});

        }else {
            //判断传入的字符串是否包含中文
            if (!isContainChinese(str)){
                str=str+"%";
                sql="select * from tb_word where headWord like ? and wordType=?";
            }else {
                str="%"+str+"%";
                sql="select * from tb_word where tranCN like ? and wordType=?";
            }
            cursor=db.rawQuery(sql,new String[]{str,type});

        }

        while (cursor.moveToNext()){
            int _id=cursor.getInt(cursor.getColumnIndex("_id"));
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

    //获取总单词数
    public int getAllWordCount()  {
        helper=new DBOpenHelper(context);
        db=helper.getReadableDatabase();
        String sql="select count(_id) from tb_word";
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
    //获取某一类型的总单词数
    public int getTypeCount()  {
        helper=new DBOpenHelper(context);
        db=helper.getReadableDatabase();
        SettingDao settingDao=new SettingDao(context);
        String type=settingDao.getDifficulty();
        String sql="select count(_id) from tb_word where wordType=?";
        Cursor cursor=db.rawQuery(sql,new String[]{type});
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
    //获取某一类型的总单词数
    public int getTypeCount(String type)  {
        helper=new DBOpenHelper(context);
        db=helper.getReadableDatabase();
        String sql="select count(_id) from tb_word where wordType=?";
        Cursor cursor=db.rawQuery(sql,new String[]{type});
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

    //随机随机的单词
    public  List<Word> getwrongwords(int needCount){
        List<Word> wronglist=new ArrayList<Word>();
        Random random = new Random();
        for (int n = 0; n < needCount; n++) {
            int j = random.nextInt(getTypeCount());
            wronglist.add(find(j));
        }
        return wronglist;
    }

    //获取所有的难度
    public List<String> getAllType(){
        List<String> types=new ArrayList<>();
        helper=new DBOpenHelper(context);
        db=helper.getReadableDatabase();

        String sql="select Distinct wordType from tb_word";
        Cursor cursor=db.rawQuery(sql,null);
        while (cursor.moveToNext()){
            String wordType=cursor.getString(cursor.getColumnIndex("wordType"));
            types.add(wordType);
        }
        //关闭游标和数据库
        cursor.close();
        db.close();
        helper.close();
        return types;
    }



    //同步已有数据库，拍照取词进行更新
    public List<Word> UpdateHaveWord(List<Word> words){
        List<Word> newwords=new ArrayList<>();
        Iterator<Word> iterator=words.iterator();
        Word nowword;
        while (iterator.hasNext()){
            nowword=iterator.next();
            if(find(nowword)!=null){//若可以查到该单词就替换该单词信息
                nowword=find(nowword);
            }
            newwords.add(nowword);
        }
        return newwords;
    }

    //拍照取词存储到数据库,多个单词一个单词本
    public void SetNewWordBook(List<Word> words,String type){
        Iterator<Word> iterator=words.iterator();
        Word nowword;
        while (iterator.hasNext()){
            nowword=iterator.next();
            nowword.setWordType(type);
            add(nowword);
        }
    }
    //拍照取词存储到数据库，多个单词多个单词本
    public void SetNewWordBook(List<Word> words,List<String> types){
        Iterator<String> iteratortype=types.iterator();
        String type;
        Word nowword;
        while (iteratortype.hasNext()){
            type=iteratortype.next();
            Iterator<Word> iteratorword=words.iterator();
            while (iteratorword.hasNext()){
                nowword=iteratorword.next();
                nowword.setWordType(type);
                add(nowword);
            }
        }
    }
    //拍照取词存储到数据库,一个单词多个单词本
    public void SetNewWordBook(Word word,List<String> types){
        Iterator<String> iterator=types.iterator();
        String type;
        while (iterator.hasNext()){
            type=iterator.next();
            word.setWordType(type);
            add(word);
        }
    }

}
