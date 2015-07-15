package com.magicare.smartnurse.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

public class FileUtils {
	
//	public static String SDPATH = Environment.getExternalStorageDirectory()
//			+ "/formats/";
	public static String SDPATH = Environment.getExternalStorageDirectory()
			+ "/smartnurse/";
	public static String SDPATH1 = Environment.getExternalStorageDirectory()
        + "/myimages/";
	
	public static void saveBitmap(Bitmap bm, String picName) {
		Log.e("", "保存图片");
		try {
			if (!isFileExist("")) {
				File tempf = createSDDir("");
			}
			File f = new File(SDPATH, picName + ".JPEG"); 
			if (f.exists()) {
				f.delete();
			}
			FileOutputStream out = new FileOutputStream(f);
			bm.compress(Bitmap.CompressFormat.JPEG, 95, out);
			out.flush();
			out.close();
			Log.e("", "已经保存");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static Bitmap getCacheBitmap(String picName) {
		File cacheFile = new File(SDPATH, picName + ".JPEG");
		if (cacheFile.exists()) {
			return BitmapFactory.decodeFile(cacheFile.getAbsolutePath());
		}
		return null;
	}

	public static File createSDDir(String dirName) throws IOException {
		File dir = new File(SDPATH + dirName);
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {

			System.out.println("createSDDir:" + dir.getAbsolutePath());
			System.out.println("createSDDir:" + dir.mkdir());
		}
		return dir;
	}

	public static boolean isFileExist(String fileName) {
		File file = new File(SDPATH + fileName);
		file.isFile();
		return file.exists();
	}
	
	public static void delFile(String fileName){
		File file = new File(SDPATH + fileName);
		if(file.isFile()){
			file.delete();
        }
		file.exists();
	}

	public static void deleteDir(String path) {
		File dir = new File(path);
		if (dir == null || !dir.exists() || !dir.isDirectory())
			return;
		
		for (File file : dir.listFiles()) {
			if (file.isFile())
				file.delete(); // 删除所有文件
			else if (file.isDirectory())
				deleteDir(path); // 递规的方式删除文件夹
		}
		dir.delete();// 删除目录本身
	}

	public static boolean fileIsExists(String path) {
		try {
			File f = new File(path);
			if (!f.exists()) {
				return false;
			}
		} catch (Exception e) {

			return false;
		}
		return true;
	}
	
//	public static List<Map<String, String>> getlistForJson(String jsonStr){  
//        List<Map<String, String>> list = null;  
//        try {  
//            JSONArray jsonArray = new JSONArray(jsonStr);  
//            JSONObject jsonObj ;  
//            list = new ArrayList<Map<String, String>>();  
//            for(int i = 0 ; i < jsonArray.length() ; i ++){  
//                jsonObj = (JSONObject)jsonArray.get(i);  
//                list.add(getMapForJson(jsonObj.toString()));  
//            }  
//        } catch (Exception e) {  
//            // TODO: handle exception  
//            e.printStackTrace();  
//        }  
//        return list;  
//    } 
	
    public static String getMapForJson(String jsonStr){  
        JSONObject jsonObject ;  
        try {  
            jsonObject = new JSONObject(jsonStr);  
              
            Iterator<String> keyIter= jsonObject.keys();  
            String key;  
            Object value = null ;  
            Map<String, String> valueMap = new HashMap<String, String>();  
            while (keyIter.hasNext()) {  
                key = keyIter.next();  
                value = jsonObject.get(key); 
                valueMap.put(key, value.toString()); 
            }  
            return value.toString();  
        } catch (Exception e) {  
            // TODO: handle exception  
            e.printStackTrace();  
        }  
        return null;  
    }
	

}