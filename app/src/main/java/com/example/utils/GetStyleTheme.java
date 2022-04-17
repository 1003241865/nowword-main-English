package com.example.utils;

import android.content.Context;

import com.example.dao.SettingDao;
import com.example.now_word.R;

public class GetStyleTheme {

    public static int getThemeResId(Context mContext) {
        SettingDao settingDao;

        settingDao = new SettingDao(mContext);
        int a = settingDao.getTheme();
        int themeresId = 0;
        switch (a) {
            case 0:
                themeresId = R.style.AppTheme;
                break;
            case 1:
                themeresId = R.style.TANGCILAN;
                break;
            case 2:
                themeresId = R.style.MIBAI;
                break;
            case 3:
                themeresId = R.style.FENHONG;
                break;
            case 4:
                themeresId = R.style.JINGDIANLAN;
                break;
            case 5:
                themeresId = R.style.YUANQIHONG;
                break;
            case 6:
                themeresId = R.style.YUNHONG;
                break;
            case 7:
                themeresId = R.style.CONGLV;
                break;

        }
        return themeresId;
    }
}
