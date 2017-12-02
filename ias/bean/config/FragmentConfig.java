package com.ias.bean.config;

import com.ias.bean.View_data.Action;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vector on 16/6/20.
 */
public class FragmentConfig {
    int structure_hash;
    List<ViewIndetifier> identifiers;

    boolean traverse;
    boolean snapshot;

    Integer title;
    String discription;

    List<Action> intrapaths;
    List<Action> interpaths;

    public List<String> unclick_list;


    public Integer getTitle() {
        return title;
    }

    public void setTitle(Integer title) {
        this.title = title;
    }


    public FragmentConfig(){
        intrapaths = new ArrayList<>();
        interpaths = new ArrayList<>();
        unclick_list = new ArrayList<>();
    }

    public int getStructure_hash() {
        return structure_hash;
    }

    public void setStructure_hash(int structure_hash) {
        this.structure_hash = structure_hash;
    }
    public boolean isTraverse() {
        return traverse;
    }

    public void setTraverse(boolean traverse) {
        this.traverse = traverse;
    }

    public boolean isSnapshot() {
        return snapshot;
    }

    public void setSnapshot(boolean snapshot) {
        this.snapshot = snapshot;
    }

    public List<ViewIndetifier> getIdentifiers() {
        return identifiers;
    }

    public void setIdentifiers(List<ViewIndetifier> identifiers) {
        this.identifiers = identifiers;
    }

    public List<Action> getInterpaths() {
        return interpaths;
    }

    public void setInterpaths(List<Action> interpaths) {
        this.interpaths = interpaths;
    }

    public List<Action> getIntrapaths() {
        return intrapaths;
    }

    public void addIntrapath(Action ea) {
        for (Action ee : intrapaths){
            if (ee.path.equals(ea.path))
                return;
        }
        intrapaths.add(ea);
    }

    public void addInterpath(Action ea) {
        for (Action ee : interpaths){
            if (ee.path.equals(ea.path))
                return;
        }
        interpaths.add(ea);
    }

    public void setIntrapaths(List<Action> intrapaths) {
        this.intrapaths = intrapaths;
    }


    public FragmentConfig(int hash){
        this();
        structure_hash = hash;
    }

    public String getDiscription() {
        return discription;
    }

    public void setDiscription(String discription) {
        this.discription = discription;
    }

    public List<String> getUnclick_list() {
        return unclick_list;
    }

    public void setUnclick_list(List<String> unclick_list) {
        this.unclick_list = unclick_list;
    }

}
