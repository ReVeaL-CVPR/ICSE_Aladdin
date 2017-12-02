package com.ias.bean.Runtime_data;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.ias.utils.SerializeUtil;
import com.robotium.solo.Solo;
import com.socks.library.KLog;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by apple on 16/6/27.
 */
public class RuntimeStack implements Serializable {
    public List<RuntimeActivityNode> stack;

    public RuntimeStack(){
        stack = new LinkedList<>();
    }


    public String cal_deep_link()
    {
        JSONArray ja = new JSONArray();
        for (RuntimeActivityNode ran : stack){
            if (ran.getIntent_ser() != null && !ran.getIntent_ser().equals(""))
                ja.add(JSON.parse(ran.getIntent_ser()));
        }
        KLog.a("liuyi", ja);
        return SerializeUtil.toBase64(ja);
    }

    public static List<String> parse_deep_link(String link){
        return (ArrayList<String>) SerializeUtil.toObjects(link, String.class);
    }

    public void recover(Solo solo, int start){
        Log.i("liuyi", "activity recover begin");
        if (stack != null) {
            int tot = stack.size();
            Activity activity = solo.getCurrentActivity();
            for (int ser = start+1; ser < tot; ++ser) {
                RuntimeActivityNode ran = stack.get(ser);
                Intent intent = SerializeUtil.getIntent(ran.getIntent_ser());
                activity.startActivity(intent);
                solo.sleep(3000);
                activity = solo.getCurrentActivity();
                if (!activity.getClass().getName().equals(ran.getActivity())) {
                    Log.i("liuyi", "runtimestack error");
                    System.exit(0);
                }
            }
        }
        Log.i("liuyi", "activity recover finish");
    }

    public RuntimeActivityNode top()
    {
        if (!stack.isEmpty())
            return stack.get(stack.size()-1);
        Log.i("liuyi", "top stack empty");
        return null;
    }
    public RuntimeActivityNode pop()
    {
        if (!stack.isEmpty())
            return stack.remove(stack.size()-1);
        Log.i("liuyi", "pop stack empty");
        return null;
    }
}
