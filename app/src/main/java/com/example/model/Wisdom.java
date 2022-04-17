package com.example.model;

public class Wisdom {
    int _id; //id
    String chinese_mean;//中文含义
    String english_mean;//英文含义

    public Wisdom(int _id, String chinese_mean, String english_mean) {
        this._id = _id;
        this.chinese_mean = chinese_mean;
        this.english_mean = english_mean;
    }

    public Wisdom(String chinese_mean, String english_mean) {
        this.chinese_mean = chinese_mean;
        this.english_mean = english_mean;
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String getChinese_mean() {
        return chinese_mean;
    }

    public void setChinese_mean(String chinese_mean) {
        this.chinese_mean = chinese_mean;
    }

    public String getEnglish_mean() {
        return english_mean;
    }

    public void setEnglish_mean(String english_mean) {
        this.english_mean = english_mean;
    }
}
