package com.example.utils;

import android.content.Context;
import android.net.Uri;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.example.utils.IsIntenet;

public class AudioMediaPlayer {

    private android.media.MediaPlayer mp;
    private Context context;
    private String word0;
    private int type0;
    private int last_type=-1;//记录上一次的播放类型
    private String last_word="";//记录上一次的单词

    public AudioMediaPlayer(Context context){
        this.context=context;
        System.out.println("音频初始化");
        }

        //更新音频和播放
    public void updateMP(String word, int type){
        this.word0=word;
        this.type0=type;
        //Log.i("hahaha","调用该UpdateMP方法");
        new Thread(new Runnable() {
            @Override
            public void run() {
                    if (type0 != last_type || word0 != last_word) {//如果播放类型和单词和上一次类型相同，就只播放不重新加载音频,否则重新加载
                        if(IsIntenet.isNetworkAvailable(context)) {//判断是否联网,如果联网
                            //播放不同类型或单词，重新加载音频
                            Uri location;
                            switch (type0) {
                                case 0://播放单词英音
                                    location = Uri.parse("http://dict.youdao.com/dictvoice?type=0&audio=" + word0);
                                    break;
                                case 1://播放单词美音
                                    location = Uri.parse("http://dict.youdao.com/dictvoice?type=1&audio=" + word0);
                                    break;
                                case 2://播放单词中文意思
                                    location = Uri.parse("http://qqlykm.cn/api/txt/apiz.php?text=" + word0 + "&spd=4");
                                    break;
                                default:
                                    location = Uri.parse("http://dict.youdao.com/dictvoice?type=0&audio=" + word0);
                                    break;
                            }
                            mp = android.media.MediaPlayer.create(context, location);
                            System.out.println("更换单词音频成功");
                            mp.start();
                    }
                }else {//不更换音频
                     if(IsIntenet.isNetworkAvailable(context)) {//判断是否联网,如果联网
                         if (!mp.isPlaying()) {//判断是否在播放
                             mp.start();
                         }
                     }else {
                         Looper.prepare();
                         Toast.makeText(context,"你还没有联网哦,不可播放音频 ！",Toast.LENGTH_SHORT).show();
                         Looper.loop();
                     }
            }
                last_type=type0;
                last_word=word0;
        }}).start();

    }
}

