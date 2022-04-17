package com.example.now_word;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.dao.SettingDao;
import com.example.model.Setting;

public class LockScreenReceiver extends BroadcastReceiver {
    private SettingDao settingDao;
    @Override
    public void onReceive(Context context, Intent intent) {
        settingDao=new SettingDao(context);
        String action=intent.getAction();
        if(settingDao.getSock()==1){//判断锁屏功能是否开启
        if(Intent.ACTION_SCREEN_OFF.equals(action)){
            //锁屏
            Intent lockScreen=new Intent(context,SockScreenActivity.class);
            lockScreen.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
            context.startActivity(lockScreen);
        }}
    }
}
