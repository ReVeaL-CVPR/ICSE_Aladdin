package com.ias.bean.config;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vector on 16/6/20.
 */
public class ActivityConf {
    String activity_name;
    List<FragmentConfig> fragments;

    public ActivityConf(){
        fragments = new ArrayList<>();
    }
    public ActivityConf(String act){this(); activity_name = act;}

    public FragmentConfig find_Fragment(int hash){
        Log.i("liuyi", "find " + activity_name + " " + hash);
        for (FragmentConfig vc : fragments)
        {
            if (vc.getStructure_hash() == hash)
                return vc;
        }
        return null;
    }

    public String getActivity_name() {
        return activity_name;
    }

    public void setActivity_name(String activity_name) {
        this.activity_name = activity_name;
    }

    public List<FragmentConfig> getFragments() {
        return fragments;
    }

    public void setFragments(List<FragmentConfig> fragments) {
        this.fragments = fragments;
    }

    public FragmentConfig getFragment(int hash){
        for (FragmentConfig vc: fragments)
            if (vc.structure_hash == hash)
                return vc;
        return null;
    }
}
