package com.ias.bean.View_data;

import android.view.View;

import com.alibaba.fastjson.annotation.JSONField;
import com.ias.utils.SerializeUtil;
import com.ias.utils.ViewUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by vector on 16/5/11.
 */
public class ViewNode implements Serializable, Comparable{

    //TODO 其实node中的viewRelateSignature和viewSignature都可以通过其他的内容构件出来的，
    //TODO 所以不应该序列化，也可以减少传输


    //表示View的类型，例如TextView等（类名）
    private String viewTag;
    @JSONField(serialize=false)
    public int total_view;

    public String getViewText() {
        return viewText;
    }

    public void setViewText(String viewText) {
        this.viewText = viewText;
    }

    private String viewText;
    public String xpath;
    @JSONField(serialize=false)
    private transient View view;

    public boolean isList;

    @JSONField(serialize=false)
    private int nodeHash;
    public boolean clickable;

    private int nodeRelateHash;

    //在树种的层级
    @JSONField(serialize=false)
    private int depth;

    //view的子节点
    private List<ViewNode> children;

    //view的父节点
    @JSONField(serialize=false)
    private ViewNode parent;


    //viewNodeID表示node在树中的编号
    @JSONField(serialize=false)
    private int viewNodeID;
    private int viewNodeIDRelative;

    private int width;
    private int height;
    private int x;
    private int y;



    public int getNodeHash() {
        return nodeHash;
    }

    public void setNodeHash(int nodeHash) {
        this.nodeHash = nodeHash;
    }

    public int getNodeRelateHash() {
        return nodeRelateHash;
    }

    public void setNodeRelateHash(int nodeRelateHash) {
        this.nodeRelateHash = nodeRelateHash;
    }



    public int getViewNodeIDRelative() {
        return viewNodeIDRelative;
    }

    public void setViewNodeIDRelative(int viewNodeIDRelative) {
        this.viewNodeIDRelative = viewNodeIDRelative;
    }


    public View getView() {
        return view;
    }

    public void setView(View view) {
        this.view = view;
    }

    public String getViewTag() {
        return viewTag;
    }

    public void setViewTag(String viewTag) {
        this.viewTag = viewTag;
    }


    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }


    public List<ViewNode> getChildren() {
        return children;
    }

    public void setChildren(List<ViewNode> children) {
        this.children = children;
    }

    public ViewNode getParent() {
        return parent;
    }

    public void setParent(ViewNode parent) {
        this.parent = parent;
    }

    public int getViewNodeID() {
        return viewNodeID;
    }

    public void setViewNodeID(int viewNodeID) {
        this.viewNodeID = viewNodeID;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    //返回的是class+深度+位置
    public String calString(){
        return SerializeUtil.getAbbr(this.viewTag) + "-" + depth + "-" + viewText + "-" + this.x + "-" + this.y;
    }


    public String calStringWithoutPosition(){
        return  SerializeUtil.getAbbr(this.viewTag) + "-" + this.depth;
    }
    

    public ViewNode() {
        children = new LinkedList<ViewNode>();
    }
    public ViewNode findRootNode(){
        ViewNode root = parent;
        while(root != null){
           if(root.parent != null)
               root = root.parent;
            else
               break;
        }
        return root;
    }

    @Override
    public int compareTo(Object another) {
        int res = getNodeRelateHash() - ((ViewNode) another).getNodeRelateHash();
        if (res != 0)
            return res;
        res = getY() - ((ViewNode) another).getY();
        if (res != 0)
            return res;
        return getX() - ((ViewNode) another).getX();
    }

    public String getPath(){
        ViewNode vn = this;
        List<String> list = new ArrayList<>();
        while (vn != null){
            if (vn.viewNodeIDRelative == 0)
                list.add(ViewUtil.getLast(vn.getViewTag()));
            else
                break;
            vn = vn.getParent();
        }
        String res = "";
        int len = list.size();
        if (len > 0){
            res = list.get(len-1);
            if (len > 1){
                for (int i = len-2; i >= 0; --i) {
                    res += ("/" + list.get(i));
                }
            }
        }
        if (vn == null)
            return res;
        else if (res == "")
            return ""+vn.getViewNodeIDRelative();
        return vn.getViewNodeIDRelative() + "#" + res;
    }
    public static int merge(ViewNode source, ViewNode target, int depth){
        String res = "";
        for (int i = 0; i < depth; ++i)
            res += "  ";
        List<Integer> filter = new ArrayList<>();
        int children_tot = source.children.size();
        if (children_tot == 0){
            if (source.getNodeRelateHash() == target.getNodeRelateHash())
            {
               // Log.i("liuyi", res + "hits: 1 / 1 -- " + source.getViewNodeIDRelative() + source.getViewTag());
                source.setView(target.getView());
                source.setX(target.getX());
                source.setY(target.getY());
                source.setWidth(target.getWidth());
                source.setHeight(target.getHeight());
                source.setViewText(target.getViewText());
                return 1;
            }
            return 0;
        }
        int hits = 0;
        int size2 = target.children.size();
        for (int i = 0; i < children_tot; ++i){
            ViewNode s = source.children.get(i);
            int tmp = 0;
            boolean flag = true;
            for (int j = 0; j < size2; ++j){
                if (filter.contains(j) || !source.getViewTag().equals(target.getViewTag()))
                    continue;
                int cnt = merge(s, target.children.get(j), depth + 1);
                if (cnt > tmp)
                    tmp = cnt;
                if ((float) cnt / s.total_view >= 0.35 + 0.5 / s.getDepth()) {
                    hits += cnt;
                    flag = false;
                    filter.add(j);
                    break;
                }
            }
            if (flag)
                hits += tmp;
        }

        if ((float) hits / source.total_view >= 0.35 + 0.5 / source.getDepth()){
            source.setView(target.getView());
            source.setX(target.getX());
            source.setY(target.getY());
            source.setWidth(target.getWidth());
            source.setHeight(target.getHeight());
            source.setViewText(target.getViewText());
            hits += 1;
        }
      //  Log.i("liuyi", res + "hits:" + hits + " / " + source.total_view + " -- " + source.getViewNodeIDRelative() + " " + source.getViewTag());
        return hits;
    }
}
