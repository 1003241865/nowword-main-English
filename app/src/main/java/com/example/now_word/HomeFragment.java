package com.example.now_word;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.dao.StudyRecordDao;
import com.example.dao.SettingDao;
import com.example.dao.WisdomDao;
import com.example.dao.WordRecordDao;
import com.example.dao.WordDao;
import com.example.dao.WordTypeDao;
import com.example.model.StudyRecord;
import com.example.model.Wisdom;
import com.example.model.Word;
import com.example.utils.AudioMediaPlayer;
import com.example.utils.GetStyleTheme;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

//首页
public class HomeFragment extends Fragment implements View.OnClickListener {

    private Button startstudyword;//开始学习按钮
    private TextView needreviewwordText;//今天还需要复习的单词数
    private TextView neednewwordText;//今天还需新学的单词书

    private TextView wisdom_english, wisdom_chinese;//名人名言组件
    private AudioMediaPlayer audioMediaPlayer;//明人名句的读
    private TextView now_difficult_Text;//显示现在的难度
    private ProgressBar now_finish_progressbar;//目前难度的背诵的进度条
    private TextView now_all_word, now_have_finish_word;//总单词数和已背的单词数显示框
    private Wisdom wisdom;//名人名言

    private WisdomDao wisdomDao;//数据库操作类
    private StudyRecordDao studyRecordDao;
    private WordDao wordDao;
    private WordRecordDao wordRecordDao;
    private SettingDao settingDao;
    private WordTypeDao wordTypeDao;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getContext().setTheme(GetStyleTheme.getThemeResId(getContext()));
        View view = inflater.inflate(R.layout.home_fragment, null);
        //获得Ui控件
        startstudyword = view.findViewById(R.id.startstudyword);
        neednewwordText = view.findViewById(R.id.new_text);
        needreviewwordText = view.findViewById(R.id.review_mastered);
        wisdom_chinese = view.findViewById(R.id.wisdom_china);
        wisdom_english = view.findViewById(R.id.wisdom_english);
        now_difficult_Text = view.findViewById(R.id.now_difficult);
        now_finish_progressbar = view.findViewById(R.id.now_finish_progressbar);
        now_all_word = view.findViewById(R.id.now_all_word);
        now_have_finish_word = view.findViewById(R.id.now_have_finish_word);
        audioMediaPlayer = new AudioMediaPlayer(getContext());

        //初始化数据库操作类
        wisdomDao = new WisdomDao(getContext());
        studyRecordDao = new StudyRecordDao(getContext());
        wordDao = new WordDao(getContext());
        wordRecordDao = new WordRecordDao(getContext());
        settingDao = new SettingDao(getContext());
        wordTypeDao=new WordTypeDao(getContext());

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        init();
    }

    @Override
    public void onResume() {
        super.onResume();
        //从其他页面返回的时候，更新数据
        init();
    }

    private void init() {
        //设置页面上的名人名言
        wisdom = wisdomDao.getRandomWisdom();
        wisdom_chinese.setText(wisdom.getChinese_mean());
        wisdom_english.setText(wisdom.getEnglish_mean());
        wisdom_english.setOnClickListener(this);

        //更新页面上还需要背和复习的单词数量
        StudyRecord studyRecord = studyRecordDao.addOrGet();
        int needreviewwordCount = studyRecord.getNeedRepeatNum() - studyRecord.getRepeatNum();
        int neednewwordCount = studyRecord.getNeedNewNum() - studyRecord.getNewNum();
        if (neednewwordCount < 0) {
            neednewwordCount = 0;
        }
        neednewwordText.setText(String.valueOf(neednewwordCount));
        needreviewwordText.setText(String.valueOf(needreviewwordCount));

        //如果需复习和背的单词都为0，则修改按钮文本显示和按钮监听
        if(wordDao.getTypeCount()<=5){
            startstudyword.setText("本单词本单词数不够");
            startstudyword.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(wordTypeDao.find(settingDao.getDifficulty())==null){
                        Toast.makeText(getContext(), "此单词本已被删除！", Toast.LENGTH_SHORT).show();
                        settingDao.updateDifficulty("CET4");
                    }
                    Toast.makeText(getContext(), "本单词本单词数不够！", Toast.LENGTH_SHORT).show();
                }
            });
        }
        else if (neednewwordCount == 0 && needreviewwordCount == 0) {
            startstudyword.setText("你已完成今天的学习");
            startstudyword.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(wordTypeDao.find(settingDao.getDifficulty())==null){
                        Toast.makeText(getContext(), "此单词本已被删除！", Toast.LENGTH_SHORT).show();
                        settingDao.updateDifficulty("CET4");
                    }
                    Toast.makeText(getContext(), "你已经完成今日的学习，明天再来吧！", Toast.LENGTH_SHORT).show();
                }
            });
        } else {//设置页面上的按钮监听
            startstudyword.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(wordTypeDao.find(settingDao.getDifficulty())==null){
                        Toast.makeText(getContext(), "此单词本已被删除！", Toast.LENGTH_SHORT).show();
                        settingDao.updateDifficulty("CET4");
                    }
                    Intent intent = new Intent(getView().getContext(), MainStudyActivity.class);
                    startActivity(intent);
                }
            });
        }

        //设置页面上目前所选择的单词难度
        String now_difficult = settingDao.getDifficulty();
        now_difficult_Text.setText("目前难度：" + now_difficult);

        //设置进度条进度
        now_finish_progressbar.setMax(wordDao.getTypeCount());
        now_finish_progressbar.setProgress(wordRecordDao.getTypeFinishWordCount());

        //设置总共单词数和已背单词数
        now_all_word.setText("总共单词：" + wordDao.getTypeCount());
        now_have_finish_word.setText("已背：" + wordRecordDao.getTypeFinishWordCount());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.wisdom_english://点击名句播放音频
                audioMediaPlayer.updateMP(wisdom.getEnglish_mean(), 0);
                break;
            case R.id.now_difficult://点击目前难度选择难度

        }
    }
}