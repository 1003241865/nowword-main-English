package com.example.now_word;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.utils.GetStyleTheme;
import com.example.utils.NoScrollViewPager;
import com.google.android.material.tabs.TabLayout;
import com.nikhilpanju.recyclerviewenhanced.RecyclerTouchListener;

import java.util.ArrayList;
import java.util.List;

public class WordListActivity extends AppCompatActivity {
    private NoScrollViewPager pager;
    private Toolbar toolbar;
    private FragmentAdapter fragmentAdapter;
    private List<TabFragment> fragmentList;
    private TabLayout tabLayout;
    private List<String> mTitles;
    private String [] title={"全部单词","标记单词","已学单词","完成单词","易错单词"};
    public String type;  //单词本类型


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(GetStyleTheme.getThemeResId(this));
        setContentView(R.layout.activity_word_list);
        Intent intent = getIntent();
        type=intent.getExtras().getString("title");
        init();
    }
    public void init()
    {   toolbar=findViewById(R.id.toolbar);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar=getSupportActionBar();
        actionBar.setTitle(type);
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.icon_home_back);
        }
        pager=findViewById(R.id.page);
        tabLayout=findViewById(R.id.tab_layout);
        fragmentList=new ArrayList<>();
        mTitles=new ArrayList<>();
        for(int i=0;i<title.length;i++){
            mTitles.add(title[i]);
            fragmentList.add(new TabFragment(title[i],type));
        }

        fragmentAdapter=new FragmentAdapter(this.getSupportFragmentManager(),fragmentList,mTitles);
        pager.setAdapter(fragmentAdapter);

        tabLayout.setupWithViewPager(pager);//与ViewPage建立关系
    }
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
                //跳转到搜索界面
            case R.id.search_main:
                Intent intent=new Intent(this,SearchWordActivity.class);
                intent.putExtra("type",type);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override//重载顶部栏按钮
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar,menu);
        return true;
    }
}