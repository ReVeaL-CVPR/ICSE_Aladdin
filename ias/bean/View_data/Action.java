package com.ias.bean.View_data;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.TextView;

import com.ias.utils.CommonUtil;
import com.ias.utils.ViewUtil;
import com.robotium.solo.Solo;

import java.io.Serializable;
import java.util.List;

/**
 * Created by vector on 16/5/15.
 */
public class Action implements Serializable{

    public String path;
    //Action的path统一为xpath和序号
    public String target;
    boolean list;
    int index;
    int scroll;
    //TODO action is enum type
    int action;

    public static String[] filterwords = {"视频","登陆","注册","login","刷新", "登录","搜索","设置","setting"};


    public int getAction() {
        return action;
    }

    public void setAction(int action) {
        this.action = action;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
    public Action() {
        list = false;
        scroll = 0;
    }
    public interface action_list {
        int CLICK   = 0;
        int ROWDOWN = 1;
        int ROWUP   = -1;
        int BACK    = 2;
        int ROWRIGHT = 3;
        int ROWLEFT = -3;
        int MENU = 4;
        int INPUT = -4;
    }

    public Action(String path, int action) {
        this.path = path;
        this.action = action;
        list = false;
        scroll = 0;
    }

    public static float[] getClickCoordinates(View view){

        int[] xyLocation = new int[2];
        float[] xyToClick = new float[3];

        view.getLocationOnScreen(xyLocation);

        xyToClick[0] = xyLocation[0] +  view.getWidth() / 2.0f;
        xyToClick[1] = xyLocation[1];
        xyToClick[2] = xyLocation[1] +  view.getHeight();

        return xyToClick;
    }

    public void execute_action(ViewTree viewtree, Solo solo) {
        if (action == action_list.MENU){
            solo.sendKey(solo.MENU);
            Log.i("liuyi", "menu");
            solo.sleep(CommonUtil.SLEEPTIME);
        }
        else{
            ViewNode target;
            if (path.contains("#")){
                Log.i("liuyi", path);
                String[] sep = path.split("#");
                int ser = Integer.parseInt(sep[1]);
                String xpath = sep[0];
                List<ViewNode> vl = ViewUtil.getViewByXpath(viewtree.root, xpath);
                if (ser < vl.size())
                    target = vl.get(ser);
                else
                    target = vl.get(CommonUtil.rand.nextInt(vl.size()));
            }
            else{
                List<ViewNode> vl = ViewUtil.getViewByXpath(viewtree.root, path);
                target = vl.get(CommonUtil.rand.nextInt(vl.size()));
            }
            execute_action(target, this.getAction(), solo, null);
        }
    }



    public static boolean sensitive(String content, List<String> list){
        for(int i = 0; i < filterwords.length; i++){
            if(content.contains(filterwords[i]))
                return true;
            if (list != null && list.equals(content));
        }
        return false;
    }

    public static boolean isSensitive(View view, List<String> list){
        return false;
//        if(view instanceof TextView){
//            String content = ((TextView) view).getText().toString();
//            if(content != null && sensitive(content, list)){
//                Log.i("liuyi", "敏感词是:" + content);
//                return true;
//            }
//        }
//        boolean result = false;
//        if(view instanceof ViewGroup) {
//            for(int i = 0; i < ((ViewGroup) view).getChildCount(); i++){
//                result = result || isSensitive(((ViewGroup) view).getChildAt(i), list);
//                if(result)
//                    return result;
//            }
//        }
//        return result;
    }



    public static String getText(View view){
        if(view instanceof TextView){
            String content = ((TextView) view).getText().toString();
            return content;
        }
        if(view instanceof Button){
            return ((Button) view).getText().toString();
        }
        String result = "";
        if(view instanceof ViewGroup) {
            for(int i = 0; i < ((ViewGroup) view).getChildCount(); i++){
                result = result + " " + getText(((ViewGroup) view).getChildAt(i));
            }
        }
        return result;
    }

    public static boolean execute_action(ViewNode vn, int action, Solo solo, List<String> filter){
        View view = vn.getView();

        try {

            float []pxy = new float[2];
//            Log.i("liuyi", vn.getX()+ " " + vn.getWidth() + " " + vn.getY() + " " + vn.getHeight());
            pxy[0] = vn.getX() + vn.getWidth()/2;
            pxy[1] = vn.getY() + vn.getHeight()/2;
            if (pxy[0] > CommonUtil.screen_x) {
                Log.i("liuyi", "right" + vn.getX() + " " + vn.getWidth());
                pxy[0] = (vn.getX() + CommonUtil.screen_x) / (float)2.0;
            }

            if (pxy[0] < 0) {
                pxy[0] = (vn.getX() + vn.getWidth()) / (float)2.0;
                Log.i("liuyi", "left " + vn.getX() + " " + vn.getWidth());
            }
            switch (action) {
                case action_list.CLICK:
                    if(isSensitive(view, filter)){
                        Log.v("liuyi","有敏感词，跳过");
                        return false;
                    }
                    Log.v("liuyi", "click on " + vn.getViewTag() + " x= " + pxy[0] + " y= " +pxy[1] + " ,content is: " + vn.getViewText());

                    solo.clickOnScreen(pxy[0], pxy[1]);
                    //检查是否会进入新的状态
                    break;
                case action_list.ROWDOWN:
                    if (view == null)
                        break;
                    Log.v("liuyi", "!!!!!!!!!!!!!!!");
                    Log.v("liuyi", "rowdown on " + view.toString());
                    if (AbsListView.class.isAssignableFrom(view.getClass())) {
                        solo.scrollDownList((AbsListView) view);
                    } else if (ViewUtil.fromRecyclerView(view.getClass())) {
                        solo.scrollDownRecyclerView(view);
                    }
                    break;
                case action_list.ROWUP:
                    if (view == null)
                        break;
                    Log.i("liuyi", "!!!!!!!!!!!!!!!");
                    Log.i("liuyi", "rowup on " + view.toString());
                    if (AbsListView.class.isAssignableFrom(view.getClass())) {
                        solo.scrollUpList((AbsListView) view);
                    } else if (ViewUtil.fromRecyclerView(view.getClass())) {
                        solo.scrollUpRecyclerView(view);
                    }
                    break;
                case action_list.BACK:
                    Log.i("liuyi", "back");
                    //这个就是点击返回键
                    solo.goBack();
                    break;
                case action_list.ROWRIGHT:
                    if (view == null)
                        break;
                    Log.i("liuyi", "!!!!!!!!!!!!!!!");
                    Log.i("liuyi", "rowup right " + view.toString());
                    solo.scrollViewToSide(view, solo.RIGHT);
                    break;
                case action_list.ROWLEFT:
                    if (view == null)
                        break;
                    Log.i("liuyi", "!!!!!!!!!!!!!!!");
                    Log.i("liuyi", "rowup left " + view.toString());
                    solo.scrollViewToSide(view, solo.LEFT);
                    break;
                default:
                    break;
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        if (view == null)
            solo.sleep(CommonUtil.SLEEPTIME);
        solo.sleep(CommonUtil.SLEEPTIME);
        if (vn.getViewText() != null && vn.getViewText().length() != 0 && filter != null) {
            Log.i("liuyi", vn.getViewText());
            filter.add(vn.getViewText());
        }
        return true;
    }

    public boolean isList() {
        return list;
    }

    public void setList(boolean list) {
        this.list = list;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getScroll() {
        return scroll;
    }

    public void setScroll(int scroll) {
        this.scroll = scroll;
    }
}
