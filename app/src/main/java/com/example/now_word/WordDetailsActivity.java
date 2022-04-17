package com.example.now_word;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.dao.WordRecordDao;
import com.example.dao.WordDao;
import com.example.model.Word;
import com.example.utils.AudioMediaPlayer;
import com.example.utils.GetStyleTheme;
import com.example.utils.SelectZhengImage;
import com.like.LikeButton;
import com.like.OnLikeListener;
import com.ms.square.android.expandabletextview.ExpandableTextView;

public class WordDetailsActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView headWord,usphone,ukphone,tranCn,tranEn;
    private ExpandableTextView sentences,phrase,synos;
    private ImageView play_voice_uk,play_voice_us;
    private int wordindex;
    private Word word;
    private WordDao wordDao;
    private AudioMediaPlayer audioMediaPlayer;
    private LikeButton likeButton;      //收藏按钮
    private WordRecordDao wordRecordDao;
    private ImageView today_repeatNum;//正字提示框
    private ImageView all_repeatNum;

    private TextView today_recycle_num,fiveday_recycle_num;//正字上面的注释

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(GetStyleTheme.getThemeResId(this));
        setContentView(R.layout.activity_word_details);
        init();
    }
    public void init(){
        //初始化数据库操作类
        wordDao=new WordDao(this);
        wordRecordDao =new WordRecordDao(this);
        //初始化UI组件，设置监听器
        headWord=findViewById(R.id.headWord);
        headWord.setOnClickListener(this);
        usphone=findViewById(R.id.usphone_text);
        usphone.setOnClickListener(this);
        play_voice_uk=findViewById(R.id.play_vioce_uk);
        play_voice_uk.setOnClickListener(this);
        ukphone=findViewById(R.id.ukphone_text);
        ukphone.setOnClickListener(this);
        play_voice_us=findViewById(R.id.play_vioce_us);
        play_voice_us.setOnClickListener(this);
        tranCn=findViewById(R.id.tranCN);
        tranCn.setOnClickListener(this);
        tranEn=findViewById(R.id.tranEn);
        tranEn.setOnClickListener(this);
        sentences=findViewById(R.id.sentences);
        sentences.setOnClickListener(this);
        phrase=findViewById(R.id.phrases);
        phrase.setOnClickListener(this);
        synos=findViewById(R.id.synos);
        synos.setOnClickListener(this);
        audioMediaPlayer =new AudioMediaPlayer(this);
        today_repeatNum=findViewById(R.id.today_repeatNum);
        all_repeatNum=findViewById(R.id.all_repeatNum);
        today_repeatNum.setVisibility(View.INVISIBLE);

        //正字上面的注释
        today_recycle_num=findViewById(R.id.today_recycle_num);
        fiveday_recycle_num=findViewById(R.id.fiveday_recycle_num);
        today_recycle_num.setVisibility(View.INVISIBLE);

        //获取单词数据
        Intent intent=getIntent();
        wordindex= intent.getIntExtra("word",1);
        //System.out.println(wordindex);
        //Toast.makeText(WordDetails.this,String.valueOf(wordindex),Toast.LENGTH_SHORT).show();
        word=wordDao.find(wordindex);

        likeButton=findViewById(R.id.flag_button);//收藏按钮
        likeButton.setLiked(wordRecordDao.getWordIsFlag(word));

        //设置组件上显示的单词信息
        headWord.setText(word.getHeadWord());
        ukphone.setText(word.getUkphone());
        usphone.setText(word.getUkphone());
        tranCn.setText(word.getTranCN());
        tranEn.setText(word.getTranEN());
        sentences.setText(word.getSentences());
        phrase.setText(word.getPhrases());
        synos.setText(word.getSyno());
        if(wordRecordDao.find(word)!=null){//如果有复习次数就显示
            SelectZhengImage.setImage(all_repeatNum, wordRecordDao.find(word).getReperaNum());
        }else{
            all_repeatNum.setVisibility(View.INVISIBLE);
            fiveday_recycle_num.setVisibility(View.INVISIBLE);
        }


        //给收藏按钮添加监听器
        likeButton.setOnLikeListener(new OnLikeListener(){

            @Override
            public void liked(LikeButton likeButton) {
                wordRecordDao.setFlagWord(word);
                likeButton.setLiked(wordRecordDao.getWordIsFlag(word));
            }

            @Override
            public void unLiked(LikeButton likeButton) {
                wordRecordDao.setFlagWord(word);
                likeButton.setLiked(wordRecordDao.getWordIsFlag(word));
            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.headWord:
            case R.id.play_vioce_uk:
            case R.id.ukphone_text:
                audioMediaPlayer.updateMP(word.getHeadWord(),0);
                break;
            case R.id.play_vioce_us:
            case R.id.usphone_text:
                audioMediaPlayer.updateMP(word.getHeadWord(),1);
                break;
            case R.id.tranCN:
                audioMediaPlayer.updateMP(word.getTranCN(),2);
                break;
            case R.id.tranEn:
                audioMediaPlayer.updateMP(word.getTranEN(),1);
                break;
            //case R.id.phrases:
                //audioService.updateMP(word.getPhrases(),0);
                //break;
            case R.id.sentences:
                audioMediaPlayer.updateMP(word.getSentences(),1);
                break;
            //case R.id.synos:
                //audioService.updateMP(word.getSyno(),1);
                //break;
        }

    }
}