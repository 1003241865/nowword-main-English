package com.example.model;

public class WordType {
    private String wordType;//主键，单词类型
    private String context; //备注
    private int isSystem ;  //是否是系统的单词本类型

    public WordType(String wordType, String context, int isSystem) {
        this.wordType = wordType;
        this.context = context;
        this.isSystem = isSystem;
    }

    public String getWordType() {
        return wordType;
    }

    public void setWordType(String wordType) {
        this.wordType = wordType;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public int getIsSystem() {
        return isSystem;
    }

    public void setIsSystem(int isSystem) {
        this.isSystem = isSystem;
    }
}
