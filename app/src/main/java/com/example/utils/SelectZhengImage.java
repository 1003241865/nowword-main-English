package com.example.utils;

import android.widget.ImageView;

import com.example.now_word.R;

public class SelectZhengImage {
    public static void setImage(ImageView imageView,int num){
        switch (num){
            case 0:
                imageView.setImageResource(R.drawable.ic_zengxin);
                break;
            case 1:
                imageView.setImageResource(R.drawable.ic_zengone);
                break;
            case 2:
                imageView.setImageResource(R.drawable.ic_zengtwo);
                break;
            case 3:
                imageView.setImageResource(R.drawable.ic_zengthree);
                break;
            case 4:
                imageView.setImageResource(R.drawable.ic_zengfour);
                break;
            default:
            case 5:
                imageView.setImageResource(R.drawable.ic_zengfive);
                break;
        }

    }
}
