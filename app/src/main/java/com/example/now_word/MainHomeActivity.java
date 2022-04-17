package com.example.now_word;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


import com.example.dao.SettingDao;
import com.example.dao.WordTypeDao;
import com.example.model.WordType;
import com.example.utils.GetStyleTheme;
import com.goodiebag.horizontalpicker.HorizontalPicker;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;

import static android.app.PendingIntent.getActivity;

public class MainHomeActivity extends AppCompatActivity implements View.OnClickListener {
    BottomNavigationView bottomNavigationView;//底部导航栏
    private FragmentManager fragmentManager; //管理fragment的类
    private DrawerLayout mDrawerLayout;//管理侧边栏的类
    private SettingDao settingDao;      //设置数据库处理类
    private HomeFragment homeFragment;
    private ReviewFragment reviewFragment;
    private WordBookFragment wordBookFragment;
    private Fragment nowfragment;
    private TextView wordType,select;
    private WordTypeDao wordTypeDao;
    private String[] itemstype;     //记录列表项要选择的单词本
    private ArrayList<WordBookEntity> bookList;
    AlertDialog.Builder builder;//添加单词本对话框
    View dialog,view;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        repaint();
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            //打开侧边栏
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);

                break;
            //跳转到搜索界面
            case R.id.search_main:
                Intent intent=new Intent(this,SearchWordActivity.class);
                intent.putExtra("type","all");
                startActivity(intent);
                break;
                default:
                    Toast.makeText(MainHomeActivity.this,"不要乱点",Toast.LENGTH_LONG);
        }
        return true;
    }

    @Override//重载顶部栏按钮
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar,menu);
        return true;
    }
    //切换界面的Fragment
    public void changeFragment(Fragment fragment){
        //开始事务
        //初始化FragmentManager类
        fragmentManager=getSupportFragmentManager();
        FragmentTransaction transaction=fragmentManager.beginTransaction();
        if(!fragment.isAdded()){//如果该碎片没有添加，就添加到栈中，隐藏原来的界面
            transaction.hide(nowfragment).add(R.id.main_content,fragment).commit();
            nowfragment=fragment;//记录目前的碎片
        }else{
            transaction.hide(nowfragment).show(fragment).commit();
            nowfragment=fragment;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

    }
    private void repaint(){
        //开启服务
        startService(new Intent(MainHomeActivity.this, LockScreenService.class));
        setTheme(GetStyleTheme.getThemeResId(this));
        settingDao=new SettingDao(this);
        setContentView(R.layout.activity_main_home);
        wordType=findViewById(R.id.wordType);
        wordType.setText(settingDao.getDifficulty());
        select=findViewById(R.id.select);
        wordType.setOnClickListener(this);
        select.setOnClickListener(this);
        wordTypeDao=new WordTypeDao(this);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        homeFragment=new HomeFragment();
        reviewFragment=new ReviewFragment();
        wordBookFragment=new WordBookFragment();
        nowfragment=new Fragment();

        mDrawerLayout=findViewById(R.id.drawer_layout);

        ActionBar actionBar=getSupportActionBar();
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            //actionBar.setHomeAsUpIndicator(R.drawable.menu_side);
            
        }
        //设置侧边栏监听
        NavigationView navigationView=findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                Intent intent;
                switch(menuItem.getItemId()){
                    case R.id.nav_settings:
                        intent = new Intent(MainHomeActivity.this,SettingActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.nav_statistics:
                        intent = new Intent(MainHomeActivity.this, StatisticsActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.about:
                        intent = new Intent(MainHomeActivity.this,AboutActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.login:
                        intent = new Intent(MainHomeActivity.this,LoginActivity.class);
                        startActivity(intent);
                        break;
                }
                return true;
            }
        });

        //获取组件对象
        bottomNavigationView=findViewById(R.id.bottom_nav);
        //设置页面切换的监听
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.navigation_home:
                        changeFragment(homeFragment);
                        break;
                    case R.id.navigation_review:
                        changeFragment(reviewFragment);
                        break;
                    case R.id.navigation_wordbook:
                        changeFragment(wordBookFragment);
                        break;
                }
                return true;
            }
        });
        changeFragment(homeFragment);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.wordType:
            case R.id.select:
                dialog = View.inflate(this, R.layout.select_wordbook_dialog, null);
                HorizontalPicker hpText = (HorizontalPicker)dialog.findViewById(R.id.hpicker);
                List<WordType> wordTypeList=wordTypeDao.getAllType();
                HorizontalPicker.OnSelectionChangeListener listener = new HorizontalPicker.OnSelectionChangeListener() {
                    @Override
                    public void onItemSelect(HorizontalPicker picker, int index) {
                        HorizontalPicker.PickerItem selected = picker.getSelectedItem();
                        settingDao.updateDifficulty(selected.getText());
                        repaint();
                        wordType.setText(selected.getText().toString());
                        //Toast.makeText(MainActivity.this, selected.hasDrawable() ? "Item at " + (picker.getSelectedIndex() + 1) + " is selected" : selected.getText() + " is selected", Toast.LENGTH_SHORT).show();
                    }
                };
                hpText.setChangeListener(listener);
                //获取当前难度
                String nowDifficulty=settingDao.getDifficulty();
                int nowDifficultyIndex=0;
                //获取难度列表
                itemstype=new String[wordTypeList.size()];
                for (int i=0;i<wordTypeList.size();i++){
                    itemstype[i]=wordTypeList.get(i).getWordType();
                    if(nowDifficulty.equals(itemstype[i])){
                        nowDifficultyIndex=i;
                    }
                }
                List<HorizontalPicker.PickerItem> textItems = new ArrayList<>();
                for(int i=0;i<itemstype.length;i++){
                    textItems.add(new HorizontalPicker.TextItem(itemstype[i]));
                }
                hpText.setItems(textItems,nowDifficultyIndex); //3 here signifies the default selected review_card_item. Use : hpText.setItems(textItems) if none of the items are selected by default.

//                //If your picker takes images as items :
//                HorizontalPicker hpImage = (HorizontalPicker) findViewById(R.id.hpImage);
//
//                List<HorizontalPicker.PickerItem> imageItems = new ArrayList<>();
//                imageItems.add(new HorizontalPicker.DrawableItem(R.drawable.));
//                imageItems.add(new HorizontalPicker.DrawableItem(R.drawable.example2));
//                hpImage.setItems(imageItems);
                builder = new AlertDialog.Builder(this);
                builder.setTitle("选择单词本");
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.setView(dialog);
                builder.setIcon(R.drawable.slide_about_icon);
                builder.show();
                break;
        }

    }

}