package com.ias.bean.Runtime_data;

import android.util.Log;

import com.ias.bean.View_data.ViewTree;
import com.ias.utils.CommonUtil;
import com.ias.utils.ViewUtil;
import com.robotium.solo.Solo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by apple on 16/6/27.
 */
public class RuntimeActivityNode implements Serializable {

    public List<RuntimeFragmentNode> stack;
    private String activity;
    private String intent_ser;

    boolean Recover;
    public List<String> filter;
    public Map<String, RuntimePath> path_map;
    //filter是记录该activity中点击的所有控件的textview，从而避免重复

    public RuntimeActivityNode(){
        filter = new ArrayList<>();
        stack = new LinkedList<>();
        path_map = new HashMap<>();
        Recover = false;
    }

    public RuntimeActivityNode(String activity, String intent_ser){
        this();
        this.activity = activity;
        this.intent_ser = intent_ser;
    }

    public String getIntent_ser() {
        return intent_ser;
    }

    public void setIntent_ser(String intent_ser) {
        this.intent_ser = intent_ser;
    }

    public String getActivity() {
        return activity;
    }

    public boolean isRecover() {
        return Recover;
    }

    public void setRecover(boolean recover) {
        Recover = recover;
    }

    public void setActivity(String activity) {
        this.activity = activity;
    }

    public List<RuntimeFragmentNode> getStack() {
        return stack;
    }

    public void setStack(List<RuntimeFragmentNode> stack) {
        this.stack = stack;
    }

    public void recover(Solo solo, int start) {
        Log.i("liuyi", "view stack recover begin");
        int tot = stack.size();
        ViewTree vt = new ViewTree(ViewUtil.getDecorView(solo));
        for (int ser = start+1; ser < tot; ++ser) {
            RuntimeFragmentNode rvn = stack.get(ser);
            if (rvn.getAction() != null) {
                rvn.getAction().execute_action(vt, solo);
                solo.sleep(3 * CommonUtil.SLEEPTIME);
                vt = new ViewTree(ViewUtil.getDecorView(solo));
                if (rvn.getStructure_hash() != (vt.getTreeStructureHash())) {
                    Log.i("liuyi", "runtimestack error");
                    System.exit(0);
                }
            }
            else
                Log.i("liuyi", "no action?? joking me");
        }
        Log.i("liuyi", "view stack recover finish");
        setRecover(false);
    }

    public RuntimeFragmentNode top(){
        if (!stack.isEmpty())
            return stack.get(stack.size() - 1);
        Log.i("liuyi", "top stack empty");
        return null;
    }
    public RuntimeFragmentNode pop(){
        if (!stack.isEmpty())
            return stack.remove(stack.size()-1);
        Log.i("liuyi", "pop stack empty");
        return null;
    }

}
