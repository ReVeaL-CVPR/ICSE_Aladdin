package com.ias.utils;

import android.util.DisplayMetrics;
import android.util.Log;

import com.loopj.android.http.SyncHttpClient;
import com.robotium.solo.Solo;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by vector on 16/6/27.
 */
public class CommonUtil {

    public static String DEFAULT_COVERAGE_FILE_PATH = "/mnt/sdcard/coverage.ec";
    public static final String HOST = "http://127.0.0.1:5000";
    public static final int SLEEPTIME = 1000;
    public static Solo console_solo;
    public static Random rand;
    public static HtmlTranslator htmlTranslator;
    public static int screen_x;
    public static int screen_y;
    public static SyncHttpClient sClient;

    public static boolean isNumeric(String str) {
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str);
        if (!isNum.matches()) {
            return false;
        }
        return true;
    }
    public static void initiate(Solo solo){
        console_solo = solo;
        solo.sleep(5 * SLEEPTIME);
        rand  = new Random();
        sClient = new SyncHttpClient();
        sClient.setConnectTimeout(2 * 60 * 1000);
        DisplayMetrics dm = new DisplayMetrics();
        solo.getCurrentActivity().getWindowManager().getDefaultDisplay().getMetrics(dm) ;
        screen_x = (int)(dm.widthPixels * 2.4);
        screen_y = (int)(dm.heightPixels * 2.4);
        htmlTranslator = new HtmlTranslator(solo);

        File file = new File(DEFAULT_COVERAGE_FILE_PATH);

        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        solo.unlockScreen();
    }

    public static void generate_report(){
        OutputStream out = null;
        try {
            out = new FileOutputStream("/mnt/sdcard/coverage.ec", false);
            Object agent = Class.forName("org.jacoco.agent.rt.RT")
                    .getMethod("getAgent")
                    .invoke(null);

            out.write((byte[]) agent.getClass().getMethod("getExecutionData", boolean.class)
                    .invoke(agent, false));
        } catch (Exception e) {
            Log.d("liuyi", e.toString(), e);
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
