package com.example.now_word;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Looper;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.example.dao.SettingDao;
import com.example.dao.StudyRecordDao;
import com.example.dao.WordRecordDao;
import com.example.dao.WordDao;
import com.example.model.StudyRecord;
import com.example.utils.FTPManager;
import com.example.utils.GetStyleTheme;
import com.example.utils.MPAndroidBarChart;
import com.example.utils.MPAndroidLineChart;
import com.example.utils.MPAndroidPieChart;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class StatisticsActivity extends AppCompatActivity  implements View.OnClickListener {
    private TextView come_days_Text,all_study_wordCount_Text,all_finish_wordCount_Text,
            all_wrong_wordCount_Text,all_highwrong_wordCount_Text,all_flag_wordCount_Text;//文本框显示组件

    private StudyRecordDao studyRecordDao;//数据库处理类
    private WordRecordDao wordRecordDao;
    private WordDao wordDao;
    private SettingDao settingDao;

    private Toolbar toolbar;//返回工具栏

    private LineChart everyday_record;//每天的背诵记录图形组件
    private PieChart wrong_word_rate;//错误统计饼图形
    private BarChart type_finish_and_wrong_count; //各类型的单词统计柱状图

    private Button uploadButton,downloadButton;//上传下载按钮

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(GetStyleTheme.getThemeResId(this));
        setContentView(R.layout.activity_statistics);

        //返回工具栏
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.icon_home_back);
        }

        //初始化数据库处理类
        studyRecordDao =new StudyRecordDao(this);
        wordRecordDao =new WordRecordDao(this);
        wordDao=new WordDao(this);
        settingDao=new SettingDao(this);

        //初始化文本框组件
        come_days_Text=findViewById(R.id.come_days);
        all_study_wordCount_Text=findViewById(R.id.all_study_wordCount);
        all_finish_wordCount_Text=findViewById(R.id.all_finish_wordCount);
        all_wrong_wordCount_Text=findViewById(R.id.all_wrong_wordCount);
        all_highwrong_wordCount_Text=findViewById(R.id.all_highwrong_wordCount);
        all_flag_wordCount_Text=findViewById(R.id.all_flag_wordCount);

        //图形组件初始化
        everyday_record=findViewById(R.id.everydayrecord);
        wrong_word_rate=findViewById(R.id.wrong_word_rate);
        type_finish_and_wrong_count=findViewById(R.id.type_finish_and_wrong_count);

        //上传下载按钮初始化
        uploadButton=findViewById(R.id.upload);
        downloadButton=findViewById(R.id.download);

        init();

    }
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void  init() {
        //为文本框设置实时更新信息
        come_days_Text.setText(studyRecordDao.getAllStudyDayCount() + "天");
        all_study_wordCount_Text.setText(wordRecordDao.getAllStudyWordCount() + "个");
        all_finish_wordCount_Text.setText(wordRecordDao.getAllFinishWordCount() + "个");
        all_wrong_wordCount_Text.setText(wordRecordDao.getAllWrongWordCount() + "个");
        all_highwrong_wordCount_Text.setText(wordRecordDao.getAllHighWrongWordCount(1) + "个");
        all_flag_wordCount_Text.setText(wordRecordDao.getAllFlagWordCount() + "个");

        //更新折现图
        List<StudyRecord> studyRecords = studyRecordDao.getWeekRecord();
        Iterator<StudyRecord> iterator= studyRecords.iterator();
        List<String> xValues=new ArrayList<>();
        List<List<Float>> yValues=new ArrayList<>();
        List<Float> yValues1=new ArrayList<>();
        List<Float> yValues2=new ArrayList<>();
        List<String> titles=new ArrayList<>();
        while (iterator.hasNext()){
            StudyRecord studyRecord =iterator.next();
            xValues.add(studyRecord.getDate());
            yValues1.add((float) studyRecord.getNewNum());
            yValues2.add((float) studyRecord.getNeedNewNum());
        }
        //如果记录只有一条，就往其中添加一条数据，否则会报错
        if(yValues1.size()<=1){
            xValues.add("0000-00-00");
            yValues1.add(0f);
            yValues2.add(0f);
        }
        yValues.add(yValues1);
        yValues.add(yValues2);
        titles.add("当天新背所选难度单词数");
        titles.add("当天需背所选难度单词数");
        MPAndroidLineChart.setLinesChart(everyday_record,xValues,yValues,titles,true,null);

        //更新饼图
        MPAndroidPieChart.setPieChart(wrong_word_rate, wordRecordDao.getWrongWordRate(),"错误次数比例",true);

        //更新柱状图
        List<List<Float>> lists= wordRecordDao.getTypeInfomation();
        MPAndroidBarChart.setThreeBarChart(type_finish_and_wrong_count,wordDao.getAllType(),lists.get(0),lists.get(1),lists.get(2),"本类型单词数","已背单词数","错误单词数");

        //为上传和下载按钮添加监听
        uploadButton.setOnClickListener(this);

        downloadButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.upload://上传进度
                ftpUpload();
                break;
            case R.id.download://下载进度
                ftpDownload();
                break;
        }
    }

    // 上传
    private void ftpUpload() {
        new Thread() {
            public void run() {
                try {
                    FTPManager ftpManager = new FTPManager(StatisticsActivity.this);
                    System.out.println("正在连接ftp服务器....");
                    if (ftpManager.connect()) {
                        System.out.println("连接成功就上传文件");

                        if (ftpManager.uploadFile( FTPManager.localPath, FTPManager.ServerDataBasePath+settingDao.getUserID())) {
                            ftpManager.closeFTP();
                            Looper.prepare();//线程中Toast需要先准备
                            Toast.makeText(StatisticsActivity.this,"进度上传成功",Toast.LENGTH_SHORT).show();
                            Looper.loop();
                        }
                    }
                } catch (Exception e) {
                    // TODO: handle exception
                    System.out.println(e.getMessage());
                }
            }
        }.start();
    }

    // 下载
    private void ftpDownload() {
        new Thread() {
            public void run() {
                try {
                    FTPManager ftpManager = new FTPManager(StatisticsActivity.this);
                    System.out.println("正在连接ftp服务器....");
                    if (ftpManager.connect()) {
                        System.out.println("连接成功就下载文件");

                        if (ftpManager.downloadFile( FTPManager.localPath, FTPManager.ServerDataBasePath)) {
                            ftpManager.closeFTP();
                            Looper.prepare();
                            Toast.makeText(StatisticsActivity.this,"进度下载成功",Toast.LENGTH_SHORT).show();
                            Looper.loop();
                        }
                    }
                } catch (Exception e) {
                    // TODO: handle exception
                    System.out.println(e.getMessage());
                }
            }
        }.start();
    }
}