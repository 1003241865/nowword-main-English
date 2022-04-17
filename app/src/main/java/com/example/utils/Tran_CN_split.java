package com.example.utils;

import android.util.Log;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Tran_CN_split {

    static public String getspit(String wordCN){
        StringBuffer stringBuffer=new StringBuffer();
        String[] allTypeCn=wordCN.split("\\s");//将不同的类型的中文意思进行切割
        for(int i=0;i<allTypeCn.length;i++){//注意i=0的是“”,所以从1开始
            stringBuffer.append(allTypeCn[i].split("；")[0]+";") ;//将每个类型只提取第一个意思
        }
        return stringBuffer.toString().trim();
    }
    static public String getCNvoice(String wordCN){
        String regex="[a-zA-Z\\p{Punct}]";
        wordCN=wordCN.replaceAll(regex,"");
        //System.out.println(wordCN);
        return wordCN;
    }

    //获取单词英文
    static public String getWordHead(String string){
        String regex="[a-zA-Z]+(\\s[a-zA-Z]+(?<![(\\sadv)(\\sadj)(\\sn)(\\svt)(\\svi)]))*";//匹配英文单词或词组
        Pattern pattern=Pattern.compile(regex);
        Matcher matcher=pattern.matcher(string);
        String wordhead=null;
        if(matcher.find()){
            wordhead=matcher.group();
        }
        //Log.i("hahaha",wordhead);
        return wordhead;
    }

    public static boolean isContainChinese( String str) {
        String regex = "[\\u4e00-\\u9fa5]";   //汉字的Unicode取值范围
        Pattern pattern = Pattern.compile(regex);
        Matcher match = pattern.matcher(str);
        return match.find();
    }
}

