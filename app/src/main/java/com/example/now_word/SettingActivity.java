package com.example.now_word;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.dao.SettingDao;
import com.example.dao.WordDao;
import com.example.model.Word;
import com.example.utils.GetStyleTheme;
import com.suke.widget.SwitchButton;

import java.util.Iterator;
import java.util.List;

//我的页面
public class SettingActivity extends AppCompatActivity implements View.OnClickListener {
    private SettingDao settingDao;                          //数据库操作类
    private SwitchButton switchButton;                      //开关按钮
    private Spinner spinnerDifficulty;                      //定义选择难度的下拉框
    private Spinner spinnerSockNum;                          //定义解锁题目的下拉框
    private Spinner spinnerNewNum;//定义新题目的下拉框
    private Toolbar toolbar;//定义顶部导航栏
    private Button style_TANGCILAN,style_MIBAI,style_FENHONG,style_PUBULAN,style_JINGDIANLAN,style_YUANQIHONG,style_YUNHONG,style_CONGLV;


    public ArrayAdapter<String> adapterDifficulty, adapterSockNum, adapterNewNum;        //定义下拉框的适配器
    public final static String[] sockNum = new String[]{"3", "5", "7", "9"};                               //解锁题目下拉框的选项内容
    public final static String[] newNum = new String[]{"5","10", "20", "30", "50"};                                  //新题目下拉框的选项内容
    public final static String[] sockNum_db = new String[]{"3", "5", "7", "9"};                               //存入数据库的对应数据
    public final static String[] newNum_db = new String[]{"5","10", "20", "30", "50"};                        //存入数据库的对应数据
    /**
     * 设置下拉框默认选项的方法
     */
    public void setSpinnerItemSelectedByValue(Spinner spinner, String value) {
        SpinnerAdapter apsAdapter = spinner.getAdapter(); //得到SpinnerAdapter对象
        int k = apsAdapter.getCount();
        for (int i = 0; i < k; i++) {
            if (value.equals(apsAdapter.getItem(i).toString())) {
                spinner.setSelection(i, true);// 默认选中项
            }
        }
    }
    //将数据库中获取的转换为下拉框中对应的字符串
    private String getCorrectItem(String value,String[] a,String[] a_db){
        for(int i=0;i<a.length;i++){
            if (value.equals(a_db[i])){
                return a[i];
            }
        }
        return null;
    }
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(GetStyleTheme.getThemeResId(this));
        setContentView(R.layout.activity_setting);
        settingDao=new SettingDao(SettingActivity.this);
        //获得Ui控件

        switchButton= findViewById(R.id.switch_btn);                    //开关按钮绑定id
        spinnerDifficulty =findViewById(R.id.spinner_difficulty);       //选择难度下拉框绑定id
        spinnerSockNum = findViewById(R.id.spinner_all_number);        //解锁题目下拉框绑定id
        spinnerNewNum = findViewById(R.id.spinner_new_number);            //新题目下拉框绑定id
        style_TANGCILAN=findViewById(R.id.style_TANGCILAN);
        style_TANGCILAN.setOnClickListener(this);
        style_FENHONG=findViewById(R.id.style_FENHONG);
        style_FENHONG.setOnClickListener(this);
        style_PUBULAN=findViewById(R.id.style_PUBULAN);
        style_PUBULAN.setOnClickListener(this);
        style_MIBAI=findViewById(R.id.style_MIBAI);
        style_MIBAI.setOnClickListener(this);
        style_JINGDIANLAN=findViewById(R.id.style_JINGDIANLAN);
        style_JINGDIANLAN.setOnClickListener(this);
        style_YUANQIHONG=findViewById(R.id.style_YUANQIHONG);
        style_YUANQIHONG.setOnClickListener(this);
        style_YUNHONG=findViewById(R.id.style_YUNHONG);
        style_YUNHONG.setOnClickListener(this);
        style_CONGLV=findViewById(R.id.style_CONGLV);
        style_CONGLV.setOnClickListener(this);

        toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.icon_home_back);
        }

        WordDao wordDao=new WordDao(this);
        List<String> difficulty_db=wordDao.getAllType();
        final String difficulty[]=new String[difficulty_db.size()];
        Iterator<String> iterator=difficulty_db.iterator();
        int i=0;
        while (iterator.hasNext()){
            difficulty[i]=iterator.next();
            i++;
        }

        //设置难度监听器
        //NiceSpinner niceSpinner = (NiceSpinner) findViewById(R.id.spinner_difficulty);
        adapterDifficulty = new ArrayAdapter<String>(SettingActivity.this,
                android.R.layout.simple_selectable_list_item, difficulty);              //初始化选择难度下拉框的适配器
        spinnerDifficulty.setAdapter(adapterDifficulty);                            //给选择难度下拉框设置适配器

        String nowdifficulty;
        if(difficulty_db.indexOf(settingDao.getDifficulty())==-1){
            nowdifficulty="CET4";
        }else {
            nowdifficulty=settingDao.getDifficulty();
        }
        setSpinnerItemSelectedByValue(spinnerDifficulty, nowdifficulty);      //定义选择难度下拉框的默认选项
        this.spinnerDifficulty.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {             //设置选择难度的下拉框的监听事件
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String msg = difficulty[position];                         //获取到选择的内容
                settingDao.updateDifficulty(msg);                                                            //保存
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });


        //设置锁屏解锁题目监听
        adapterSockNum = new ArrayAdapter<String>(SettingActivity.this, android.R.layout.simple_selectable_list_item, sockNum);
        spinnerSockNum.setAdapter(adapterSockNum);
        String c=getCorrectItem(String.valueOf(settingDao.getSockNum()),sockNum,sockNum_db);
        setSpinnerItemSelectedByValue(spinnerSockNum, c);
        spinnerSockNum.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String msg = sockNum_db[position];
                int i = Integer.parseInt(msg);
                settingDao.updateSockNum(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });


        //每天新学单词设置监听器
        adapterNewNum = new ArrayAdapter<String>(SettingActivity.this, android.R.layout.simple_selectable_list_item, newNum);
        spinnerNewNum.setAdapter(adapterNewNum);
        String n=getCorrectItem(String.valueOf(settingDao.getNewNum()),newNum,newNum_db);
        setSpinnerItemSelectedByValue(spinnerNewNum, n);
        spinnerNewNum.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String msg =newNum_db[position];
                int i= Integer.parseInt(msg);
                settingDao.updateNewNum(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //为是否开启锁屏设置监听器
        if (settingDao.getSock()==1) {
            switchButton.setChecked(true);
        } else {
            switchButton.setChecked(false);
        }
        switchButton.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener(){

            @Override
            public void onCheckedChanged(SwitchButton view, boolean isChecked) {
                if (isChecked){
                    settingDao.updateSock(1);
                }

                else
                    settingDao.updateSock(0);
            }

        });

    }
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
        }
        return super.onOptionsItemSelected(item);
    }
    //设置主题


    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.style_PUBULAN:

                toolbar.setBackgroundColor(getResources().getColor(R.color.PUBULAN));
                settingDao.updateTheme(0);
                Toast.makeText(this,"部分页面重启后生效",Toast.LENGTH_LONG).show();

                break;
            case R.id.style_TANGCILAN:
                toolbar.setBackgroundColor(getResources().getColor(R.color.TANCCILAN));
                settingDao.updateTheme(1);
                Toast.makeText(this,"部分页面重启后生效",Toast.LENGTH_LONG).show();

                break;
            case R.id.style_MIBAI:
                toolbar.setBackgroundColor(getResources().getColor(R.color.MIBAI));
                settingDao.updateTheme(2);
                Toast.makeText(this,"部分页面重启后生效",Toast.LENGTH_LONG).show();

                break;
            case R.id.style_FENHONG:
                toolbar.setBackgroundColor(getResources().getColor(R.color.FENHONG));
                settingDao.updateTheme(3);
                Toast.makeText(this,"部分页面重启后生效",Toast.LENGTH_LONG).show();

                break;
            case R.id.style_JINGDIANLAN:
                toolbar.setBackgroundColor(getResources().getColor(R.color.JINGDIANLAN));
                settingDao.updateTheme(4);
                Toast.makeText(this,"部分页面重启后生效",Toast.LENGTH_LONG).show();

                break;
            case R.id.style_YUANQIHONG:
                toolbar.setBackgroundColor(getResources().getColor(R.color.YUANQIHONG));
                settingDao.updateTheme(5);
                Toast.makeText(this,"部分页面重启后生效",Toast.LENGTH_LONG).show();

                break;
            case R.id.style_YUNHONG:
                toolbar.setBackgroundColor(getResources().getColor(R.color.YUNHONG));
                settingDao.updateTheme(6);
                Toast.makeText(this,"部分页面重启后生效",Toast.LENGTH_LONG).show();

                break;
            case R.id.style_CONGLV:
                toolbar.setBackgroundColor(getResources().getColor(R.color.CONGLV));
                settingDao.updateTheme(7);
                Toast.makeText(this,"部分页面重启后生效",Toast.LENGTH_LONG).show();

                break;

        }
    }
}
