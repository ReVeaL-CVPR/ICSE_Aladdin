package com.ias.bean.config;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vector on 16/6/20.
 */
public class AppConfig {
    String package_name;
    String home_activity;
    List<ActivityConf> activities;

    public AppConfig(){
        activities = new ArrayList<>();
    }

    public void display(){
        Log.i("liuyi", package_name + " " + home_activity + " " + activities.size());
        for (ActivityConf acf : activities){
            Log.i("liuyi", "   " + acf.activity_name + " " + acf.getFragments().size());
            for (FragmentConfig vcf : acf.getFragments()){
                Log.i("liuyi", "      " + vcf.getStructure_hash());
                if (vcf.getIntrapaths() == null)
                    Log.i("liuyi", "intra null");
                else
                    Log.i("liuyi", ""+vcf.getInterpaths().size());
                if (vcf.getInterpaths() == null)
                    Log.i("liuyi", "inter null");
                else
                    Log.i("liuyi", ""+vcf.getInterpaths().size());
            }
        }
    }

    public ActivityConf getAct(String act){
        for (ActivityConf ac: activities)
            if (ac.getActivity_name().equals(act))
                return ac;
        return null;
    }

    public ActivityConf find_Activity(String activity_name){
        for (ActivityConf ac : activities)
        {
            if (ac.getActivity_name().equals(activity_name)) {
                return ac;
            }
        }
        return null;
    }

    public String getHome_activity() {
        return home_activity;
    }


    public void setHome_activity(String home_activity) {
        this.home_activity = home_activity;
    }

    public String getPackage_name() {
        return package_name;
    }

    public void setPackage_name(String package_name) {
        this.package_name = package_name;
    }


    public List<ActivityConf> getActivities() {
        return activities;
    }

    public void setActivities(List<ActivityConf> activities) {
        this.activities = activities;
    }
}
