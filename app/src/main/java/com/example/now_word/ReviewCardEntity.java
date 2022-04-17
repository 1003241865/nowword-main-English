package com.example.now_word;

/**
 * Created by marco.granatiero on 03/02/2015.
 */
public class ReviewCardEntity {
    public int imageResId;
    public int titleResId;
    public int num;

    public ReviewCardEntity(int imageResId, int titleResId,int num){
        this.imageResId = imageResId;
        this.titleResId = titleResId;
        this.num=num;
    }
}
