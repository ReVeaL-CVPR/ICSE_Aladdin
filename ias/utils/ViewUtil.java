package com.ias.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.ias.bean.View_data.ViewNode;
import com.robotium.solo.Solo;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Field;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;

//import android.support.v4.view.ViewPager;

/**
 * Created by vector on 16/5/10.
 */
public class ViewUtil {
    public static final int VIEW_TEXTVIEW = 0;
    public static final int VIEW_BUTTON = 1;
    public static final int VIEW_IMAGE = 2;
    public static final int VIEW_ABSLISTVIEW = 3;
    public static final int VIEW_VIEWPAGER = 4;
    public static final int VIEW_LINEARLAYOUT = 5;
    public static final int VIEW_RELATIVELAYOUT = 6;
    public static final int VIEW_FRAMELAYOUT = 7;
    public static final int VIEW_SCROLLVIEW = 8;
    public static final int DECORVIEW = 9;
    public static final int VIEW_RECYCLERVIEW = 10;


    public static Class<? extends View>[] VIEW_CLASSES = new Class[]{TextView.class, Button.class, ImageView.class, AbsListView.class, LinearLayout.class, RelativeLayout.class, FrameLayout.class, ScrollView.class};
    static{
        try {
            String windowManagerClassName;
            if (android.os.Build.VERSION.SDK_INT >= 17) {
                windowManagerClassName = "android.view.WindowManagerGlobal";
            } else {
                windowManagerClassName = "android.view.WindowManagerImpl";
            }
            windowManager = Class.forName(windowManagerClassName);

        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * @param view 表示要判定的view对象
     * @param includeSubclasses 表示是否可以继承自目标View
     * @return
     */
    public static int getViewType(View view, boolean includeSubclasses){
        Class<? extends View> classOfView = view.getClass();
        for(int i = 0; i < VIEW_CLASSES.length; i++){
            if (includeSubclasses && VIEW_CLASSES[i].isAssignableFrom(classOfView) || !includeSubclasses && VIEW_CLASSES[i] == classOfView) {
                return i;
            }
        }
        //RecyclerView单独考虑
        Class r = getRecyclerView();
        if(r != null) {
            if (includeSubclasses && r.isAssignableFrom(classOfView) || !includeSubclasses && r == classOfView) {
                return VIEW_RECYCLERVIEW;
            }
        }

        //不在预设的构件列表中
        return -1;
    }


    public static int getViewType(String viewTag, boolean includeSubclasses){
        if(viewTag.contains("DecorView")){
            return DECORVIEW;
        }
        Class classOfView = null;
        try {
            classOfView = Class.forName(viewTag);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        for(int i = 0; i < VIEW_CLASSES.length; i++){
            if (includeSubclasses && VIEW_CLASSES[i].isAssignableFrom(classOfView) || !includeSubclasses && VIEW_CLASSES[i] == classOfView) {
                return i;
            }
        }
        //不在预设的构件列表中
        return -1;
    }

    public static boolean isView(View view, Class<? extends View> target, boolean includeSubclasses){
        Class<? extends View> classOfView = view.getClass();
        return includeSubclasses && target.isAssignableFrom(classOfView) || !includeSubclasses && target == classOfView;
    }

    //new decorview
    public static View getDecorView(Solo solo){
        View decorView = null;
        int cnt = 0;
        while (decorView == null) {
            try{
                solo.sleep(1000);
                //Log.i("liuyi", "pid："+android.os.Process.myPid());
                //decorView = solo.getCurrentActivity().findViewById(android.R.id.content);
                decorView = getDecorView();
            } catch (Exception e) {
                e.printStackTrace();
            }
            cnt ++;
            if (cnt == 8)
                break;
        }
        if (decorView == null) {
            Log.i("liuyi", "decor faile");
        }
        return decorView;
    }


    //由于有些应用中没有用到RecyclerView，如果直接用RecyclerView会报找不到类的错误，所以需要用反射的方式
    public static Class getRecyclerView(){
        Class r = null;
        try {
            r = Class.forName("android.support.v7.widget.RecyclerView");
        } catch (ClassNotFoundException e) {
            return null;
        }
        return r;
    }


    public static boolean fromRecyclerView(Class target){
        Class r = getRecyclerView();
        if(r == null)
            return false;
        return r.isAssignableFrom(target);
    }



    //判断一个view有没有设置OnClickListener
    public static boolean hasClickListener(View view){
        /*
        try {
            Class<?> viewClass = Class.forName("android.view.View");
            Class<?> listenerClass = Class.forName("android.view." + "View$ListenerInfo");
            Field onClickListenerField = listenerClass.getField("mOnClickListener");
            onClickListenerField.setAccessible(true);

            Field listenerfield = viewClass.getDeclaredField("mListenerInfo");
            listenerfield.setAccessible(true);

            Object mlistenerInfo = listenerfield.get(view);
            if(mlistenerInfo == null){
                //Log.v("liuyi", "mlistenerInfo is null");
            }else{
                Object listener = onClickListenerField.get(mlistenerInfo);
                if(listener != null)
                    return true;
            }

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return false;*/
        return true;

    }

    //AbListView和GridView都是继承AdapterView，可以设置OnItemClickListener
    public static boolean hasOnItemClickListener(View view){
        if(view instanceof AdapterView)
            return false;
        try {
            Class<?> adapterViewClass = Class.forName("android.widget.AdapterView");
            Field mOnItemClickListenerField = adapterViewClass.getDeclaredField("mOnItemClickListener");
            mOnItemClickListenerField.setAccessible(true);
            Object mOnItemClickListener = mOnItemClickListenerField.get(view);
            if(mOnItemClickListener != null)
                return true;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }


        return false;
    }


    public static String generate_path(ViewNode node){
        ArrayList<ViewNode> list = new ArrayList<>();
        do {
            list.add(node);
            node = node.getParent();
        }while(node != null);
        int size = list.size();
        Log.i("liuyi", " " + size);
        String xpath = "";
        for (int i = size-2; i >= 0; --i) {
            String[] spl = list.get(i).getViewTag().split("\\.");
            xpath += ('/' + spl[spl.length-1]);

        }
        return xpath.substring(1);
    }

    public static String generate_xpath(ViewNode node){
        ArrayList<ViewNode> list = new ArrayList<>();
        do {
            list.add(node);
            node = node.getParent();
        }while(node != null);
        int size = list.size();
        String xpath = "";
        for (int i = 0; i < size; ++i){
            Log.i("liuyi", list.get(i).getViewTag());
        }
        for (int i = size-2; i >= 0; --i) {
            String[] spl = list.get(i).getViewTag().split("\\.");
            xpath += ('/' + spl[spl.length-1]);

        }
        return xpath.substring(1);
    }



    public static List<ViewNode> getViewByXpath(ViewNode root, String xpath){
        class tmp{
            public ViewNode v;
            public int d;
            tmp(ViewNode v, int d){
                this.v = v; this.d = d;
            }
        }
        if (xpath.contains("@")){
            ArrayList<ViewNode> list  = new ArrayList<>();
            ArrayList<ViewNode> stack = new ArrayList<>();
            stack.add(root);
            while(!stack.isEmpty()){
                ViewNode node = stack.remove(0);
                if (node.xpath != null && node.xpath.equals(xpath)){
                    list.add(node);
                }
                stack.addAll(node.getChildren());
            }
            return list;
        }
        List<ViewNode> list = new ArrayList<>();
        ArrayDeque<tmp> queue=new ArrayDeque<>();
        String[] pathNodes = xpath.split("/");
        queue.push(new tmp(root, 1));
        while(!queue.isEmpty()){
            tmp t = queue.pop();
            ViewNode v = t.v;
            if (t.d == pathNodes.length)
                list.add(v);
            else{
                for (ViewNode child : v.getChildren()){
                    if (child.getViewTag().contains(pathNodes[t.d]))
                        queue.add(new tmp(child, t.d+1));
                }
            }
        }
        return list;
    }

    public static String getLast(String name){
        if (!name.contains("."))
            return name;
        String[] words = name.split("\\.");
        return  words[words.length-1];
    }
    public static synchronized Drawable stringToDrawable(String icon) {

        byte[] img= Base64.decode(icon.getBytes(), Base64.DEFAULT);
        Bitmap bitmap;
        if (img != null) {


            bitmap = BitmapFactory.decodeByteArray(img, 0, img.length);
            @SuppressWarnings("deprecation")
            Drawable drawable = new BitmapDrawable(bitmap);

            return drawable;
        }
        return null;

    }
    public  static synchronized  String drawableToString(Drawable drawable) {

        if (drawable != null) {
            Bitmap bitmap = Bitmap
                    .createBitmap(
                            drawable.getIntrinsicWidth(),
                            drawable.getIntrinsicHeight(),
                            drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                                    : Bitmap.Config.RGB_565);
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
                    drawable.getIntrinsicHeight());
            drawable.draw(canvas);
            int size = bitmap.getWidth() * bitmap.getHeight() * 4;

            // 创建一个字节数组输出流,流的大小为size
            ByteArrayOutputStream baos = new ByteArrayOutputStream(size);
            // 设置位图的压缩格式，质量为100%，并放入字节数组输出流中
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
            // 将字节数组输出流转化为字节数组byte[]
            byte[] imagedata = baos.toByteArray();

            String icon= Base64.encodeToString(imagedata, Base64.DEFAULT);
            return icon;
        }
        return null;
    }



    private static Class<?> windowManager;


    public static View[] getWindowDecorViews()
    {
        String windowManagerString = "";
        if (android.os.Build.VERSION.SDK_INT >= 17) {
            windowManagerString = "sDefaultWindowManager";

        } else if(android.os.Build.VERSION.SDK_INT >= 13) {
            windowManagerString = "sWindowManager";

        } else {
            windowManagerString = "mWindowManager";
        }

        Field viewsField;
        Field instanceField;
        try {
            //      Log.i("liuyi", windowManager.getName());
            viewsField = windowManager.getDeclaredField("mViews");
            instanceField = windowManager.getDeclaredField(windowManagerString);
            viewsField.setAccessible(true);
            instanceField.setAccessible(true);
            Object instance = instanceField.get(null);
            View[] result;
            if (android.os.Build.VERSION.SDK_INT >= 19) {
                ArrayList<View> list = (ArrayList<View>) viewsField.get(instance);
                result = new View[list.size()];
                for (int i=0;i<list.size();i++) {
                    result[i] = list.get(i);
                }
            } else {
                result = (View[]) viewsField.get(instance);
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }



    private static final View getRecentContainer(View[] views) {
        View container = null;
        long drawingTime = 0;
        View view;

        for(int i = 0; i < views.length; i++){
            view = views[i];
//            if (view != null){
//                Log.i("liuyi", "shown: " + view.isShown() + " focus: " + view.hasWindowFocus() + " drawn " + view.getDrawingTime() + " " + view.hashCode());
//            }
//            else
//                Log.i("liuyi", "null");
            if (view != null && view.isShown() && view.hasWindowFocus() && view.getDrawingTime() > drawingTime) {
                container = view;
                drawingTime = view.getDrawingTime();
            }
        }
        return container;
    }


    private static View getDecorView() {
        final View[] views = getWindowDecorViews();
        return getRecentContainer(views);

    }

}
