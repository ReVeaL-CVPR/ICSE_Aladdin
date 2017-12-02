package com.ias.bean.View_data;

import java.io.Serializable;

/**
 * Created by apple on 16/5/16.
 */
public class RuntimeNode implements Serializable{


    //TODO 待验证， 表示是否由前一个状态滚动获得的，这个时候应该把lastNodeID设置为前一个RuntimeNode的lastNodeID+1
    boolean scrollDerived;

    public ViewTree viewtree;
    public Action action;

    public ViewTree getViewtree() {
        return viewtree;
    }

    public void setViewtree(ViewTree viewtree) {
        this.viewtree = viewtree;
    }

    public Action getAction() {
        return action;
    }

    public void setAction(Action action) {
        this.action = action;
    }

    public boolean isScrollDerived() {
        return scrollDerived;
    }

    public void setScrollDerived(boolean scrollDerived) {
        this.scrollDerived = scrollDerived;
    }

    public RuntimeNode() {
    }

    public RuntimeNode(ViewTree viewtree, Action action) {
        this.viewtree = viewtree;
        this.action = action;
        this.scrollDerived = false;
    }

    public RuntimeNode(boolean scrollDerived, ViewTree viewtree, Action action) {
        this.scrollDerived = scrollDerived;
        this.viewtree = viewtree;
        this.action = action;
    }

    public boolean equals(RuntimeNode n){
        return this.viewtree.getTreeStructureHash() == (n.viewtree.getTreeStructureHash());
    }
}
