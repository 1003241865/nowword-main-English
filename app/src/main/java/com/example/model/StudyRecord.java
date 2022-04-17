package com.example.model;

//记录实体类

public class StudyRecord {
    private int _id;        //Id
    private String date;    //日期
    private int newNum;    //今日已经背诵新单词数量
    private int repeatNum;      //今日已经复习单词数量
    private int needNewNum;  //今日需要背诵新单词的数量
    private int needRepeatNum; //今日需要复习的单词数量
    private String difficulty ;       //背单词难度类型
    //默认构造方法
    public StudyRecord(){             //默认构造方法
        super();
    }
    //定义有参数构造方法，初始化信息实体中的字段
    public StudyRecord(String date, int newNum, int repeatNum, int needNewNum, int needRepeatNum, String difficulty) {
        this.date = date;
        this.newNum = newNum;
        this.repeatNum = repeatNum;
        this.needNewNum = needNewNum;
        this.needRepeatNum = needRepeatNum;
        this.difficulty=difficulty;
    }

    public StudyRecord(int _id, String date, int newNum, int repeatNum, int needNewNum, int needRepeatNum, String difficulty) {
        this._id = _id;
        this.date = date;
        this.newNum = newNum;
        this.repeatNum = repeatNum;
        this.needNewNum = needNewNum;
        this.needRepeatNum = needRepeatNum;
        this.difficulty=difficulty;
    }


    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getNewNum() {
        return newNum;
    }

    public void setNewNum(int newNum) {
        this.newNum = newNum;
    }

    public int getRepeatNum() {
        return repeatNum;
    }

    public void setRepeatNum(int repeatNum) {
        this.repeatNum = repeatNum;
    }

    public int getNeedNewNum() {
        return needNewNum;
    }

    public void setNeedNewNum(int needNewNum) {
        this.needNewNum = needNewNum;
    }

    public int getNeedRepeatNum() {
        return needRepeatNum;
    }

    public void setNeedRepeatNum(int needRepeatNum) {
        this.needRepeatNum = needRepeatNum;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }
}
