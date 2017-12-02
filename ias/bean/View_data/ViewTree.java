package com.ias.bean.View_data;

import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.AbsListView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import com.ias.utils.CommonUtil;
import com.ias.utils.SerializeUtil;
import com.ias.utils.ViewUtil;

import java.io.Serializable;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by vector on 16/5/11.
 */
public class ViewTree implements Serializable{

    //表示树的结构的hash值这里只考虑了节点的class+深度，而且对于子节点性质相同的list，子节点不考虑
    public int treeStructureHash;
    //树的跟节点

    public ViewNode root;
    static String _url = "com";
    @JSONField(serialize=false)
    public int lastNodeID;
    public int totalViewCount;
    public int relativeCount;
    String html_nodes = "";

    public List<String> click_list;


    @JSONField(serialize=false)
    List<Position> loc = new ArrayList<>();

    @JSONField(serialize=false)
    public static Class<? extends View>[] filtsBys = new Class[]{AbsListView.class, GridView.class};

    public int getTreeStructureHash() {
        return treeStructureHash;
    }

    public void setTreeStructureHash(int treeStructureHash) {
        this.treeStructureHash = treeStructureHash;
    }

    public ViewNode getRoot() {
        return root;
    }

    public void setRoot(ViewNode root) {
        this.root = root;
    }

    public int getLastNodeID() {
        return lastNodeID;
    }

    public void setLastNodeID(int lastNodeID) {
        this.lastNodeID = lastNodeID;
    }

    public int getTotalViewCount() {
        return totalViewCount;
    }

    public void setTotalViewCount(int totalViewCount) {
        this.totalViewCount = totalViewCount;
    }

    public List<Position> getLoc() {
        return loc;
    }

    public void setLoc(List<Position> lo) {
        this.loc = lo;
    }

    public ViewTree() {
    }

    public ViewTree(View decorView){
        relativeCount = totalViewCount = 0;
        root = construct(decorView, 0, null);
        totalViewCount = root.total_view;
        treeStructureHash = root.getNodeRelateHash();
    }

    public String py_display(ViewNode vn, int depth){
        String res = "";
        for (int i = 0; i < depth; ++i)
            res += "   ";
        res += vn.isList + " :" + ViewUtil.getLast(vn.getViewTag()) + " " + vn.getViewText();
        if (vn.getChildren().size() == 0){
            res += "               path: " + vn.getPath();
        }
        int [] loc = new int[2];
        vn.getView().getLocationOnScreen(loc);
        if (vn.getView() == null)
            res +=  "  " +vn.xpath + "   ";
        res += "   x = " + loc[0] + " y = " + loc[1] + "\n";
        //Log.i("liuyi", res);
        for (ViewNode n : vn.getChildren())
            res += py_display(n, depth+1);
        return res;
    }

    public static void display(ViewNode vn, int depth){
        String res = "";
        for (int i = 0; i < depth; ++i)
            res += "   ";
        res += vn.isList + " :" + vn.clickable + " " + vn.getNodeRelateHash() + "--"+ ViewUtil.getLast(vn.getViewTag()) + " " + vn.getViewText();
        res += "   x=" + vn.getX() + " y=" + vn.getY() + "   w=" + vn.getWidth() + " h=" + vn.getHeight() ;
        if (vn.getView() == null)
            res +=  "  " +vn.xpath + "   ";
        Log.i("liuyi", res);
        for (ViewNode n : vn.getChildren())
            display(n, depth+1);
    }



    ViewNode construct(final View rootView, int depth, ViewNode par){

        if(rootView != null) {
            int[] position = new int[2];
            rootView.getLocationOnScreen(position);
            if (depth > 1) {
                if (rootView.getVisibility() == View.GONE)
                    return null;
                if ((position[0] + rootView.getWidth()) <= 0 || (position[0]) >= CommonUtil.screen_x) {
                    return null;
                }
            }

            //设置View点击的位置（中间）

            ViewNode now = new ViewNode();
            now.clickable = ViewUtil.hasClickListener(rootView);
            now.setView(rootView);
            now.setDepth(depth);
            now.setX((int)(position[0] * 2.4));
            now.setY((int)(position[1] * 2.4));
            now.setWidth((int)(rootView.getWidth() * 2.4));
            now.setHeight((int)(rootView.getHeight()* 2.4));
            now.setViewTag(rootView.getClass().getName());
            now.setParent(par);
            if (par != null)
                now.xpath = par.xpath + '/' + ViewUtil.getLast(now.getViewTag());
            else
                now.xpath = ViewUtil.getLast(now.getViewTag());

            now.isList = false;
            if (rootView instanceof WebView) {
                html_nodes = CommonUtil.console_solo.getCLICK();
                if (html_nodes == null || html_nodes.equals("")){
                    now.setNodeRelateHash(now.calStringWithoutPosition().hashCode());
                    return now;
                }
                List<JSONObject> ja = SerializeUtil.toObjects(html_nodes, JSONObject.class);
                List<ViewNode> children = now.getChildren();
                List<String> path_list = new ArrayList<>();
                for (JSONObject node : ja) {
                    if ((node.getInteger("x") + node.getInteger("w")) <= 0 || node.getInteger("x") >= CommonUtil.screen_x)
                        continue;
                    ViewNode vn = new ViewNode();
                    vn.clickable = true;
                    vn.setDepth(depth + 1);
                    vn.setX(node.getInteger("x") + now.getX());
                    vn.setY(node.getInteger("y") + now.getY());
                    vn.setWidth(node.getInteger("w"));
                    vn.setHeight(node.getInteger("h"));
                    vn.xpath = "@" + node.getString("path");
                    vn.setViewTag(node.getString("tag").replace(" ", "").replace("\n", ""));
                    if (node.getString("text") != null)
                        vn.setViewText(node.getString("text").replace(" ", "").replace("\n", ""));
                    vn.setNodeRelateHash((vn.xpath).hashCode());
                    vn.setParent(now);
                    vn.isList = false;
                    if (!path_list.contains(vn.xpath))
                        path_list.add(vn.xpath);
                    children.add(vn);
                }
                Collections.sort(path_list);
                String str = now.calStringWithoutPosition();
                for (String s : path_list)
                    str += s;
                now.setNodeRelateHash(str.hashCode());
                return now;
            }
            if (rootView instanceof TextView) {
                now.setViewText(((TextView) rootView).getText().toString());
            }
            else if (rootView instanceof ImageView) {
                try {
                    Drawable d = ((ImageView) rootView).getDrawable();
                    if (d != null && d.getIntrinsicWidth() > 0 && d.getIntrinsicHeight() > 0)
                        now.setViewText("" + ViewUtil.drawableToString(d).hashCode());
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            else
                now.setViewText("");

            now.total_view = 1;
            String relate_hash_string = now.calStringWithoutPosition();
            if (rootView instanceof ViewGroup) {
                int child_Num = ((ViewGroup)rootView).getChildCount();

                List<ViewNode> child_list = new ArrayList<>();
                for(int i = 0; i < child_Num; i++){
                    View child = ((ViewGroup)rootView).getChildAt(i);
                    ViewNode child_node = construct(child, depth + 1, now);
                    if (child_node == null)
                        continue;
                    child_list.add(child_node);
                    now.total_view += child_node.total_view;
                }
                if (child_list.size() > 0) {
                    Collections.sort(child_list);
                    child_Num = child_list.size();
                    boolean isList = isList(rootView);
                    ArrayList<Integer> cnt = new ArrayList<>();
                    int ccnt = 0;
                    for (int i = 0; i < child_Num; i++) {
                        int id = child_list.get(i).getNodeRelateHash();
                        if (cnt.contains(id)) {
                            ++ccnt;
                        } else {
                            cnt.add(id);
                            relate_hash_string += id;
                        }
                    }
                    if (!isList && ccnt > child_list.size() * 2 / 3)
                        isList = true;
                    now.isList = isList;
                    now.setChildren(child_list);
                }
            }
            now.setNodeRelateHash(relate_hash_string.hashCode());
            return now;
        }
        return null;
    }


    public ViewTree merge(ViewTree previousTree){
        Log.i("liuyi", "begin merge");
        if (treeStructureHash == previousTree.getTreeStructureHash())
            return this;
        //进行树匹配
        int tot = ViewNode.merge(previousTree.root, root, 0);
        float result = (float)tot / previousTree.totalViewCount;
        Log.i("liuyi", "merge:" + tot + " / " + previousTree.totalViewCount + " = " + result);
        if (result >= 0.7){
            Log.i("liuyi", "merge success");
            return previousTree;
        }

        return null;
    }



    //对于AbListView/RecyclerView/GridView等比较特殊的view，我们可能需要特殊考虑,例如计算treehash的时候可能就不需要计算到其子节点
    public static boolean isList(View root){

        boolean beFiltered = false;

        Class viewClass = root.getClass();
        for(int i = 0; i < filtsBys.length; i++) {
            beFiltered = (beFiltered || filtsBys[i].isAssignableFrom(viewClass));
            if (beFiltered) {
                return true;
            }
        }
        //RecyclerView特殊考虑，因为可能有些应用没有引入这个包，会报找不到这个类的错误
        return ViewUtil.fromRecyclerView(viewClass);
    }


    //根据View在ViewTree中的位置来确定点击View节点
    public View getViewByID(int id){
        return getNodebyID(id).getView();
    }

    public ViewNode getNodebyID(int id)
    {
        ArrayDeque<ViewNode> queue=new ArrayDeque<>();
        queue.add(root);
        while(!queue.isEmpty()){
            ViewNode node=queue.remove();
            if(node.getViewNodeIDRelative() == id)
                return node;
            queue.addAll(node.getChildren());
        }
        return null;
    }

    public ArrayList<String> get_click_list(){
        display(root, 0);
        ArrayList<String> list  = new ArrayList<>();
        ArrayList<ViewNode> stack = new ArrayList<>();
        stack.addAll(root.getChildren());
        while(!stack.isEmpty()){
            ViewNode node = stack.remove(0);
            if (node.clickable && node.getChildren().size() == 0){
                Log.i("liuyi", "click:" + node.xpath);
                if (!list.contains(node.xpath))
                    list.add(node.xpath);
            }
            stack.addAll(node.getChildren());
        }
        return list;
    }
}


//                CommonUtil.console_solo.getCurrentActivity().runOnUiThread(new Runnable()
//                {
//                    @Override
//                    public void run() {
//                        _url = ((WebView) rootView).getUrl();
//
//                        ((WebView) rootView).getSettings().setJavaScriptEnabled(true);
//                        ((WebView) rootView).addJavascriptInterface(new JSInterface(), "ias");
//                        String js = "var list = [];\n" +
//                                "var lx = " + rootView.getX() + ";\n" +
//                                "var ly = " + rootView.getY() + ";\n" +
//                                "function searchleaf(node, xpath){\n" +
//                                "  var children = node.children;\n" +
//                                "  if (children == undefined)\n" +
//                                "    return;\n" +
//                                "  path = xpath + '/' + node.tagName;\n" +
//                                "  w = node.offsetWidth;\n" +
//                                "  h = node.offsetHeight;\n" +
//                                "  if (w > 0 && h > 0 && (node.tagName == 'a' || node.tagName == 'A' || node.onclick  != null)) {\n" +
//                                "    text = node.text;\n" +
//                                "    list.push({'x':lx+getElementLeft(node), 'y':ly+getElementTop(node), 'w': w, 'h' : h, 'path': path, 'text': text, 'tag' : node.tagName});\n" +
//                                "  }\n" +
//                                "  for (var i = 0; i < children.length; i++) {\n" +
//                                "    searchleaf(children[i], path);\n" +
//                                " }\n" +
//                                "}\n" +
//                                "function getElementTop(element){\n" +
//                                "    var actualTop = element.offsetTop;\n" +
//                                "    var current = element.offsetParent;\n" +
//                                "    while (current !== null){\n" +
//                                "      actualTop += current.offsetTop;\n" +
//                                "      current = current.offsetParent;\n" +
//                                "    }\n" +
//                                "    return actualTop;\n" +
//                                "}\n" +
//                                "function getElementLeft(element){\n" +
//                                "    var actualLeft = element.offsetLeft;\n" +
//                                "    var current = element.offsetParent;\n" +
//                                "    while (current !== null){\n" +
//                                "      actualLeft += current.offsetLeft;\n" +
//                                "      current = current.offsetParent;\n" +
//                                "    }\n" +
//                                "    return actualLeft;\n" +
//                                "}\n" +
//                                "searchleaf(document.getElementsByTagName('body')[0], '');\n" +
//                                "console.log(JSON.stringify(list));\n"+
//                                "$.ajax({type: 'get', url : \"" + CommonUtil.HOST + "/store_html\", data : {nodes: JSON.stringify(list), _url:\"" + _url +"\" } , callback:  null, dataType : 'JSONP'});";
//                        ((WebView) rootView).loadUrl("javascript: " + js);
//                        ((WebView) rootView).removeJavascriptInterface("ias");
//                    }


//                    webElement.
//
//
//                });
//                try {
//                    int cnt = 0;
//                    while(html_nodes.equals("")) {
//                        CommonUtil.console_solo.sleep(500);
//                        CommonUtil.sClient.get(CommonUtil.console_solo.getCurrentActivity(), CommonUtil.HOST + "/get_html?url=" + _url, new RequestParams(), new TextHttpResponseHandler() {
//                            @Override
//                            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
//                            }
//
//                            @Override
//                            public void onSuccess(int statusCode, Header[] headers, String responseString) {
//                                html_nodes = responseString;
//                                if (html_nodes == null)
//                                    html_nodes = "";
//                            }
//                        });
//                        if (cnt ++ > 6)
//                            break;
//                    }
//                    if (!html_nodes.equals("")) {
//                        List<JSONObject> ja = SerializeUtil.toObjects(html_nodes, JSONObject.class);
//                        List<ViewNode> children = now.getChildren();
//                        List<String> path_list = new ArrayList<>();
//                        for (JSONObject node : ja) {
//                            ViewNode vn = new ViewNode();
//                            vn.clickable = true;
//                            vn.setDepth(depth + 1);
//                            vn.setX(node.getInteger("x"));
//                            vn.setY(node.getInteger("y"));
//                            vn.setWidth(node.getInteger("w"));
//                            vn.setHeight(node.getInteger("h"));
//                            vn.xpath = "@" + node.getString("path");
//                            vn.setViewTag(node.getString("tag"));
//                            vn.setViewText(node.getString("text"));
//                            vn.setNodeRelateHash((vn.xpath).hashCode());
//                            vn.setParent(now);
//                            vn.isList = false;
//                            if (!path_list.contains(vn.xpath))
//                                path_list.add(vn.xpath);
//                            children.add(vn);
//                        }
//                        Collections.sort(path_list);
//                        String str = now.calStringWithoutPosition();
//                        for (String s : path_list)
//                            str += s;
//                        now.setNodeRelateHash(str.hashCode());
//                        return now;
//                    }
//                }catch (Exception e){
//                    e.printStackTrace();
//                }