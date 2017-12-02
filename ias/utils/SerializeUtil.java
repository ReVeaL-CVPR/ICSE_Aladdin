package com.ias.utils;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.robotium.solo.Solo;
import com.socks.library.KLog;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.URISyntaxException;
import java.util.List;

/**
 * Created by vector on 16/6/14.
 */
public class SerializeUtil {
    /**
     * 根据intent列表进行跳转
     * @param intents
     * @param now
     * Intent格式
    {
    "sourceClass":"com.douban.movie.app.HomeActivity", //起始的Activity类名
    "intentUri":"#Intent;component=com.douban.movie/.app.SubjectActivity;S.mid=25662329;end", //Intent.parseUri()
    "datas" : [
    {
    "key":"mid",
    "value":"25662329",
    "class":"java.lang.String"
    },
    {
    "key":"movie",
    "value":"{\"id\":\"25662329\",\"collectCount\":234515,\"title\":\"疯狂动物城\",\"pubdates\":[\"2016-03-04(中国大陆/美国)\"],\"alt\":\"http://movie.douban.com/subject/25662329/\",\"subtype\":\"movie\",\"originalTitle\":\"Zootopia\",\"images\":{\"small\":\"http://img3.doubanio.com/view/movie_poster_cover/ipst/public/p2315672647.jpg\",\"medium\":\"http://img3.doubanio.com/view/movie_poster_cover/spst/public/p2315672647.jpg\",\"large\":\"http://img3.doubanio.com/view/movie_poster_cover/lpst/public/p2315672647.jpg\"},\"year\":\"2016\",\"rating\":{\"min\":0,\"stars\":\"50\",\"average\":9.3,\"max\":10}}",
    "class":"com.douban.model.movie.Subject"
    }
    ]
    }
     */
    public static void transfer(Solo solo, JSONArray intents, int now) throws URISyntaxException {
        //跳转结束
        KLog.a("liuyi", intents);
        if(now == intents.size())
            return;
        Activity currentActivity = solo.getCurrentActivity();
        Intent targetIntent;
        JSONObject intentInfo = intents.getJSONObject(now);
        String target = intentInfo.getString("intentUri");
        JSONArray datas = intentInfo.getJSONArray("datas");
        targetIntent = Intent.parseUri(target,0);

//        (solo.getCurrentActivity()).startService(targetIntent);

        //TODO 保存的时候我们不保存基本类型的数据？
        if(datas != null && datas.size() > 0){
            Bundle bundle = new Bundle();
            for(int i = 0; i < datas.size(); i++) {
                JSONObject data = datas.getJSONObject(i);
                Class subjectClass;
                try {
                    subjectClass = Class.forName(data.getString("class"));
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
                String key = data.getString("key");
                String value = data.getString("value");
                //这里需要判断对应的Class实现的是哪一个接口，对应的使用bundle的putParcelable和putSerializable
                boolean isFromParcelable = Parcelable.class.isAssignableFrom(subjectClass);
                boolean isFromSerializable = Serializable.class.isAssignableFrom(subjectClass);
                if(isFromParcelable)
                    bundle.putParcelable(key, (Parcelable) JSONObject.parseObject(value, subjectClass));
                else if(isFromSerializable)
                    bundle.putSerializable(key, (Serializable) JSONObject.parseObject(value, subjectClass));
                targetIntent.putExtras(bundle);
            }
        }
        //跳转到对应的Activity
        currentActivity.startActivity(targetIntent);
        //TODO 跳转后直接等一段时间再跳转还是？
        while(true){
            Activity nowActivity = solo.getCurrentActivity();
            //TODO 已经跳转了（或者是不是应该判断nowActivity和intent中定义的targetActivity的class一样）
            if(!currentActivity.getClass().getName().equals(nowActivity.getClass().getName())){
                break;
            }
            //TODO
            solo.sleep(1000);
        }
        transfer(solo, intents, now+1);
    }
    public static String serialize(Solo solo){
        Activity currentActivity = solo.getCurrentActivity();
        return serialize(currentActivity.getIntent());
    }

    //反序列化intent
    public static Intent getIntent(String ser){
        JSONObject intentInfo = JSONObject.parseObject(ser);
        String target = intentInfo.getString("intentUri");
        JSONArray datas = intentInfo.getJSONArray("datas");
        Intent targetIntent = null;
        try {
            targetIntent = Intent.parseUri(target,0);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        //TODO 保存的时候我们不保存基本类型的数据？
        if(datas != null && datas.size() > 0){
            Bundle bundle = new Bundle();
            for(int i = 0; i < datas.size(); i++) {
                JSONObject data = datas.getJSONObject(i);
                Class subjectClass;
                try {
                    subjectClass = Class.forName(data.getString("class"));
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
                String key = data.getString("key");
                String value = data.getString("value");
                //这里需要判断对应的Class实现的是哪一个接口，对应的使用bundle的putParcelable和putSerializable
                boolean isFromParcelable = Parcelable.class.isAssignableFrom(subjectClass);
                boolean isFromSerializable = Serializable.class.isAssignableFrom(subjectClass);
                if(isFromParcelable)
                    bundle.putParcelable(key, (Parcelable) JSONObject.parseObject(value, subjectClass));
                else if(isFromSerializable)
                    bundle.putSerializable(key, (Serializable) JSONObject.parseObject(value, subjectClass));
                targetIntent.putExtras(bundle);
            }
        }
        return targetIntent;
    }

    //将Intent序列化成String存储
    public static String serialize(Intent intent){
        Bundle b = intent.getExtras();
        JSONObject result = new JSONObject();
        //TODO 查看一下toUri的不同参数的作用
        result.put("intentUri", intent.toUri(0));
        JSONArray datas = new JSONArray();
        JSONObject ob;
        if(b!= null && b.keySet() != null) {
            for (String key : b.keySet()) {
                Log.i("liuyi", key);
                if (b.get(key) == null)
                    continue;
                String className = b.get(key).getClass().getName();
                //如果不是基本数据类型，就是Parcelable或者Serializable对象，需要序列化成JSON格式的保存
                if (!className.startsWith("java")) {
                    JSONObject data = new JSONObject();
                    //判断是Parcelable还是Serializable的对象
                    boolean isFromParcelable = b.get(key) instanceof Parcelable;
                    boolean isFromSerializable = b.get(key) instanceof Serializable;
                    if (isFromParcelable)
                        ob = (JSONObject) JSONObject.toJSON(b.getParcelable(key));
                    else if (isFromSerializable)
                        ob = (JSONObject) JSONObject.toJSON(b.getSerializable(key));
                    else
                        ob = (JSONObject) JSONObject.toJSON(b.get(key));
                    //Log.v("liuyi",ob.toJSONString());
                    /*
                    {
                      "key":"mid",
                      "value":"25662329",
                      "class":"java.lang.String"
                    }
                     */
                    data.put("key", key);
                    data.put("value", ob.toJSONString());
                    data.put("class", className);
                    datas.add(data);
                }
            }
        }
        result.put("datas", datas);
        //TODO sourceClass，暂时没保存，如果需要保存则应该需要用一个全局变量来保存一下上一个Activity
        result.put("sourceClass", "");
        return result.toJSONString();
    }





    public static String toBase64(Object obj){
        return JSON.toJSONString(obj);
//        byte[] bytes = toByteArray(obj);
//        String str = Base64.encodeToString(bytes, 0, bytes.length,Base64.DEFAULT);
//        return str;
    }


    public static Object toObject(String base64, Class target){
//        byte[] bytes = android.util.Base64.decode(base64, android.util.Base64.DEFAULT);
//        return ViewUtil.toObject(bytes);
        return JSON.parseObject(base64, target);
    }

    public static List toObjects(String base64, Class target){
//        byte[] bytes = android.util.Base64.decode(base64, android.util.Base64.DEFAULT);
//        return ViewUtil.toObject(bytes);
        return JSON.parseArray(base64, target);
    }


    public static byte[] toByteArray (Object obj) {
        byte[] bytes = null;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(obj);
            oos.flush();
            bytes = bos.toByteArray ();
            oos.close();
            bos.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return bytes;
    }
    public static Object toObject (byte[] bytes) {
        Object obj = null;
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream (bytes);
            ObjectInputStream ois = new ObjectInputStream (bis);
            obj = ois.readObject();
            ois.close();
            bis.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return obj;
    }



    //获取ViewTree的树形结构，可以通过python的可视化程序展示
    public static String getTreeStr(View root, int rank){
        String tag = getAbbr(root.getClass().getName());
        String result = "";

        if(root instanceof ViewGroup){
            int childCount = ((ViewGroup)root).getChildCount();
            if(rank == -1){
                result = "{\""+tag+"\":{";
            }else{
                result = "\""+rank+"\":{\""+tag+"\":{";
            }
            for(int i = 0; i < childCount; i++){
                View item = ((ViewGroup) root).getChildAt(i);
                result += getTreeStr(item, i);
                if(i < childCount - 1){
                    result += ",";
                }
            }
            result += "}}";
        }else{
            return "\""+rank+"\":\""+tag+"\"";
        }
        return result;
    }

    public static String getAbbr(String name){
        String[] words = name.split("\\.");
        if(words.length == 0)
            return name;
        String result = "";
        for(int i = 0; i < words.length; i++){
            result += (""+words[i].charAt(0));
        }
        return result;
    }
}
