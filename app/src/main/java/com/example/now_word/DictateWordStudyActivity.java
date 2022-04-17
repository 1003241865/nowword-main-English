package com.example.now_word;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dao.StudyRecordDao;
import com.example.dao.SettingDao;
import com.example.dao.WordRecordDao;
import com.example.dao.WordDao;
import com.example.model.Word;
import com.example.utils.AudioMediaPlayer;
import com.example.utils.GetStyleTheme;
import com.example.utils.LocalTrueFalseMediaPlayer;
import com.example.utils.SelectZhengImage;
import com.example.utils.Sentence_split;
import com.example.utils.Tran_CN_split;
import com.like.LikeButton;
import com.like.OnLikeListener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DictateWordStudyActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView word_text,ukphone_text,usphone_text,tranEN_text,tranCN_text;//用来显示单词和音标的
    private TextView needfinishCountText;//提示框
    private ImageView play_uk_voiceButton,play_us_voiceButton;//播放声音按钮
    private AudioMediaPlayer audioMediaPlayer;//播放音频类
    private LocalTrueFalseMediaPlayer localTrueFalseMediaPlayer;//提示音类
    private EditText input; //输入框
    private Button commit;  //提交按钮
    private LikeButton likeButton;      //收藏按钮
    private ImageView all_repeatNum;
    //数据库操作类
    private WordDao wordDao;
    private WordRecordDao wordRecordDao;
    private SettingDao settingDao;
    private StudyRecordDao studyRecordDao;
    int needCount=30;  //要检测单词的数量
    int highWrongtime=2; //高危词为错误次数大于等于highWrongtime的词
    List<Word> needstudy;//需要学习的所有单词
    Iterator<Word> iterator;
    Word nowword;   //现在正在背的单词
    int size;//还要学习的单词总数量
    int size0;
    int wrongCount=0;//错误的单词次数
    int rightCount=0;//正确的单词次数

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(GetStyleTheme.getThemeResId(this));
        setContentView(R.layout.activity_dictate_word_study);
        init();//初始化控件
    }

    /**
     * 初始化控件
     */
    public void init() {

        //初始化按钮
        needfinishCountText =findViewById(R.id.needfinishCountText);
        word_text=findViewById(R.id.word_text);
        ukphone_text=findViewById(R.id.ukphone_text);
        usphone_text=findViewById(R.id.usphone_text);
        tranEN_text=findViewById(R.id.tranEN_text);
        tranCN_text=findViewById(R.id.tranCN_text);
        input=findViewById(R.id.input_text);
        commit=findViewById(R.id.commit);
        likeButton=findViewById(R.id.flag_button);
        all_repeatNum=findViewById(R.id.all_repeatNum);
        //初始化播放声音
        play_uk_voiceButton =findViewById(R.id.play_vioce_uk);
        play_us_voiceButton=findViewById(R.id.play_vioce_us);
        audioMediaPlayer =new AudioMediaPlayer(this);
        localTrueFalseMediaPlayer=new LocalTrueFalseMediaPlayer(this);
        //初始化数据库操作
        wordDao=new WordDao(this);
        wordRecordDao =new WordRecordDao(this);
        settingDao=new SettingDao(this);
        studyRecordDao =new StudyRecordDao(this);

        //添加监听
        //给按钮添加监听
        play_uk_voiceButton.setOnClickListener(this);
        play_us_voiceButton.setOnClickListener(this);
        commit.setOnClickListener(this);
        tranEN_text.setOnClickListener(this);
        tranCN_text.setOnClickListener(this);

        //给收藏按钮添加监听器
        likeButton.setOnLikeListener(new OnLikeListener(){

            @Override
            public void liked(LikeButton likeButton) {
                wordRecordDao.setFlagWord(nowword);
                likeButton.setLiked(wordRecordDao.getWordIsFlag(nowword));
            }

            @Override
            public void unLiked(LikeButton likeButton) {
                wordRecordDao.setFlagWord(nowword);
                likeButton.setLiked(wordRecordDao.getWordIsFlag(nowword));
            }
        });

        needstudy=new ArrayList<>();
        //将需要背的单词加入到学习列表中
        Intent intent=getIntent();//获得Intent
        String studyWords=intent.getStringExtra("studyWords");
        if(studyWords.equals("finish")){
            needstudy= wordRecordDao.getTypeFinishWords(needCount);}
        else if(studyWords.equals("flag")){
            needstudy= wordRecordDao.getTypeFlagWords(needCount);
        }else if(studyWords.equals("danger")){
            needstudy= wordRecordDao.getHighWrongWords(needCount,highWrongtime);
        }
        iterator=needstudy.iterator();
        size0=needstudy.size();//学习单词的总数量
        size=size0;


        if (size0<=5){Toast.makeText(this, "你还没有完成学习的单词，快去背单词吧！", Toast.LENGTH_LONG).show();
            finish();}
        else {
            getNextData();
        }
    }


    /**
     * 设置选项
     */
    private void setword(Word word) {
        word_text.setText(word.getHeadWord());
        ukphone_text.setText(word.getUkphone());
        usphone_text.setText(word.getUsphone());
        tranEN_text.setText(word.getTranEN());

        //设置单词收藏状态
        likeButton.setLiked(wordRecordDao.getWordIsFlag(word));
        SelectZhengImage.setImage(all_repeatNum, wordRecordDao.find(word).getReperaNum());

        tranCN_text.setText(Tran_CN_split.getspit(word.getTranCN()));
        //重置颜色
        word_text.setBackgroundColor(Color.WHITE);
        input.setBackgroundColor(Color.WHITE);
        //设置单词某些信息不可见
        word_text.setVisibility(View.INVISIBLE);
        ukphone_text.setVisibility(View.INVISIBLE);
        usphone_text.setVisibility(View.INVISIBLE);
        //清空输入框
        input.setText("");
        audioMediaPlayer.updateMP(nowword.getHeadWord(),0);
        // audioService.updateMP(nowword.getTranEN());
    }


    //提交按钮点击事件
    @Override
    public void onClick(View v) {
        //播放单词语音
        switch (v.getId()) {
            case R.id.play_vioce_uk://播放单词声音
                audioMediaPlayer.updateMP(nowword.getHeadWord(),0);
                break;
            case R.id.play_vioce_us:
                audioMediaPlayer.updateMP(nowword.getHeadWord(),1);
                break;
            case R.id.sentence_text:
                audioMediaPlayer.updateMP(Sentence_split.getspit(nowword.getSentences()),0);
                break;
            case R.id.tranEN_text:
                audioMediaPlayer.updateMP(nowword.getTranEN(),0);
                break;
            case R.id.tranCN_text:
                audioMediaPlayer.updateMP(Tran_CN_split.getCNvoice(nowword.getTranCN()),2);
                break;

            case R.id.commit:
                //获取用户输入的单词
                String inputword;
                inputword=input.getText().toString().trim();
                if(inputword.equals(nowword.getHeadWord())){//选择正确，设置正确时的颜色,并更新到数据库
                    localTrueFalseMediaPlayer.play(true);//提示音播放
                    word_text.setBackgroundColor(getResources().getColor(R.color.TIANYUANLV));
                    input.setBackgroundColor(getResources().getColor(R.color.TIANYUANLV));
                    wordRecordDao.trueSaveDate(nowword);
                    rightCount++;
                } else{//设置错误时的颜色，并更新数据库
                    localTrueFalseMediaPlayer.play(false);//提示音播放
                    word_text.setBackgroundColor(getResources().getColor(R.color.YANHONG));
                    input.setBackgroundColor(getResources().getColor(R.color.YANHONG));
                    wordRecordDao.wrongSaveDate(nowword);
                    wrongCount++;
                }
                //设置单词信息可见
                word_text.setVisibility(View.VISIBLE);
                ukphone_text.setVisibility(View.VISIBLE);
                usphone_text.setVisibility(View.VISIBLE);

                Handler handler=new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getNextData();
                    }
                },2000);
                break;
        }
    }

    //更新还需要的背单词数量
    private void update(){
        size=size-1;
        needfinishCountText.setText(String.valueOf(size));
    }


    private void getNextData() {

        if (iterator.hasNext()) {
            nowword=iterator.next();
            setword(nowword);
            update();
        } else {
            String msg="听写测验完成,"+"总共："+size0+'\n'+"正确："+rightCount+"\t"+"错误:"+wrongCount;
            Toast.makeText(this,msg, Toast.LENGTH_LONG).show();
            finish();
        }
    }
}
