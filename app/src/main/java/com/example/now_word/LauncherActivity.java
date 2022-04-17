package com.example.now_word;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.example.utils.FirstInto;

/*
 *  描述： 启动页
 */
public class LauncherActivity extends AppCompatActivity implements View.OnClickListener{

    /**
     * 1.延时2000ms
     * 2.判断程序是否第一次运行
     * 3.Activity全屏主题
     */

    //闪屏业延时
    private static final int HANDLER_SPLASH = 1001;
    //判断程序是否是第一次运行
    private static final String SHARE_IS_FIRST = "isFirst";

    private Button jump_buttom;//跳过按钮
    private boolean isJump=false;

    private Handler handler ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);// 隐藏标题
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);// 设置全屏

        setContentView(R.layout.activity_launcher);
        initView();


    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    //初始化View
    private void initView() {
        //跳过按钮设置监听
        jump_buttom=findViewById(R.id.btn_jump);
        jump_buttom.setOnClickListener(this);
        //延时2000ms
        handler= new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                switch (msg.what) {
                    case HANDLER_SPLASH:
                        if(!isJump){//判断跳过状态，若未点击跳过按钮才进行下面操作
                            //判断程序是否是第一次运行
                            if (isFirst()) {
                                startActivity(new Intent(LauncherActivity.this, MyWelcomeActivity.class));
                            } else {
                                startActivity(new Intent(LauncherActivity.this, MainHomeActivity.class));
                            }
                            finish();
                        }
                        break;
                    default:
                        break;
                }
                return false;
            }

        });

        handler.sendEmptyMessageDelayed(HANDLER_SPLASH, 2000);
    }

    //判断程序是否第一次运行
    private boolean isFirst() {
        boolean isFirst = FirstInto.getBoolean(this, SHARE_IS_FIRST, true);
        if (isFirst) {
            FirstInto.putBoolean(this, SHARE_IS_FIRST, false);
            //是第一次运行
            return true;
        } else {
            return false;
        }

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_jump:
                //Log.i("hahaha","跳过按钮被点击！");
                isJump=true;//跳过状态为True，否则会连续两次跳到主页面
                if (isFirst()) {
                    startActivity(new Intent(LauncherActivity.this, MyWelcomeActivity.class));
                } else {
                    startActivity(new Intent(LauncherActivity.this, MainHomeActivity.class));
                }
                finish();
                break;
        }
    }

}