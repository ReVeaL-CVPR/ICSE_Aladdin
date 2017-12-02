package com.ias.bean.Runtime_data;

import com.ias.bean.View_data.Action;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by apple on 16/5/16.
 */
public class RuntimeFragmentNode implements Serializable{

    private int structure_hash;
    private Action action;
    public int xpath_index;
    public int path_index;
    public boolean if_menu;
    public List<String> filter;


    public Action getAction() {
        return action;
    }

    public void setAction(Action action) {
        this.action = action;
    }

    public int getStructure_hash() {
        return structure_hash;
    }

    public void setStructure_hash(int structure_hash) {
        this.structure_hash = structure_hash;
    }

    public RuntimeFragmentNode(){
        filter = new ArrayList<>();
        xpath_index = 0;
        path_index = 0;
        if_menu = false;
    }
    public RuntimeFragmentNode(int hash){
        this();
        structure_hash = hash;
    }
}
