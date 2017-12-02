package com.ias.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.util.Log;

import com.robotium.solo.Solo;

import java.util.List;

/**
 * Created by vector on 16/6/14.
 */
public class PageUtil {

    //获取当前Activity的深度
    public static int getActivityDepth(Solo solo) {
        ActivityManager manager = (ActivityManager)solo.getCurrentActivity().getSystemService(Context.ACTIVITY_SERVICE );
        List<ActivityManager.RunningTaskInfo> list_info = manager.getRunningTasks(1);
        if(list_info != null && !list_info.isEmpty()) {
            Log.v("Ruogu", "Current Depth: " + list_info.get(0).numRunning);
            return list_info.get(0).numRunning;
        }
        else
            return -1 ;
    }


    /**
     *通过RunningAppProcessInfo类判断（不需要额外权限）：
     * @param context
     * @return
     */
    public static boolean isBackground(Context context) {


        ActivityManager activityManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager
                .getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.processName.equals(context.getPackageName())) {
                if (appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_BACKGROUND) {
                    Log.i("liuyi", "后台：" + appProcess.processName);
                    return true;
                } else {
                    Log.i("liuyi", "前台：" + appProcess.processName);
                    return false;
                }
            }
        }
        return false;
    }

}
