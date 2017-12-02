package com.ias.utils;

import android.content.Context;

import org.apache.http.util.EncodingUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by vector on 16/5/9.
 */
public class FileHelper {


    public static String getFilePath(){
        String filePath = "config";
        File tmp = new File(filePath);
        if(!tmp.exists()){
            tmp.mkdir();
        }
        return filePath;
    }

    //写数据
    public static void writeFile(String fileName,String writestr, Context context) throws IOException {
        try{

            FileOutputStream fout = context.openFileOutput(fileName, Context.MODE_PRIVATE);

            byte [] bytes = writestr.getBytes();

            fout.write(bytes);

            fout.close();
        }

        catch(Exception e){
            e.printStackTrace();
        }
    }

    //写数据到SD中的文件
    public static void writeFileSdcardFile(String fileName,String write_str) throws IOException{


        try{

            FileOutputStream fout = new FileOutputStream(fileName);
            byte [] bytes = write_str.getBytes();

            fout.write(bytes);
            fout.close();
        }

        catch(Exception e){
            e.printStackTrace();
        }
    }


    //读数据
    public static String readFile(String fileName, Context context) throws IOException{
        String res="";
        try{
            FileInputStream fin = context.openFileInput(fileName);
            int length = fin.available();
            byte [] buffer = new byte[length];
            fin.read(buffer);
            res = EncodingUtils.getString(buffer, "UTF-8");
            fin.close();
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return res;

    }


    public static void deleteAllFiles(File root) {
        File files[] = root.listFiles();
        if (files != null)
            for (File f : files) {
                if (f.isDirectory()) { // 判断是否为文件夹
                    deleteAllFiles(f);
                    try {
                        f.delete();
                    } catch (Exception e) {
                    }
                } else {
                    if (f.exists()) { // 判断是否存在
                        deleteAllFiles(f);
                        try {
                            f.delete();
                        } catch (Exception e) {
                        }
                    }
                }
            }
    }
}
