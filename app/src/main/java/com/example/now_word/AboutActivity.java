package com.example.now_word;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toolbar;

import com.example.utils.GetStyleTheme;

import mehdi.sakout.aboutpage.AboutPage;
import mehdi.sakout.aboutpage.Element;

public class AboutActivity extends AppCompatActivity {

    private Toolbar toolbar;//工具栏

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(GetStyleTheme.getThemeResId(this));
        mehdi.sakout.aboutpage.Element versionElement = new mehdi.sakout.aboutpage.Element();
        versionElement.setTitle("Now Version 1.0").setGravity(Gravity.CENTER);
        mehdi.sakout.aboutpage.Element back=new Element();
        back.setTitle("返回主界面")
                .setGravity(Gravity.CENTER);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        View aboutPage = new AboutPage(this)
                .isRTL(false)
                .enableDarkMode(false)
                .addItem(versionElement)
                .setImage(R.drawable.logo)
                .setDescription(getString(R.string.about))
                .addGroup("Connect with us")
                .addEmail("1003241865@qq.com")
                .addGitHub("1003241865")
                .addItem(back)
                .create();

        setContentView(aboutPage);
    }

}