package com.ias.utils;

import android.util.Log;
import android.webkit.JavascriptInterface;

/**
 * Created by apple on 2017/3/24.
 */
public class JSInterface {
    @JavascriptInterface
    public void log(String content){
        Log.i("liuyi", content);
    }
}
