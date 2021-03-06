package com.whu.iss.bookmanagesystem.util;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.whu.iss.bookmanagesystem.Book;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

/**
 * 
 */
public class Util {
	/**
	 * 下载图书的JSON数据
	 * @param urlstr
	 * @return
	 */
	public static String Download(String urlstr)
	{
	   String result="";
	   try{
	       URL url=new URL(urlstr);
	       URLConnection connection =url.openConnection();
	
	       String line;
	       BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(),"UTF-8"));
	           while ((line = in.readLine()) != null) {
	               result += line;
	           }
	       }catch (Exception e) {
	           e.printStackTrace();
	       }
	   return  result;
	}
   
	/**
	 * 解析图书JSON数据
	 * @param str
	 * @return
	 */
    public Book parseBookInfo(String str)
    {
        Book info=new Book();
        try{
            JSONObject mess=new JSONObject(str);
            info.setId(mess.getString("id"));
            info.setTitle(mess.getString("title"));
            info.setMapUrl(mess.getString("image"));
            info.setBitmap(DownloadBitmap(mess.getString("image")));
            info.setAuthor(parseAuthor(mess.getJSONArray("author")));
            info.setPublisher(mess.getString("publisher"));
            info.setPublishDate(mess.getString("pubdate"));
            info.setISBN(mess.getString("isbn13"));
            info.setSummary(mess.getString("summary"));
            info.setAuthorInfo(mess.getString("author_intro"));
            info.setPage(mess.getString("pages"));
            info.setPrice(mess.getString("price"));
            info.setContent(mess.getString("catalog"));
            info.setRate(mess.getJSONObject("rating").getString("average"));
            info.setTag(parseTags(mess.getJSONArray("tags")));
        }catch (Exception e) {
            e.printStackTrace();
        	return null;
        }
        return info;
    }
    public String parseTags (JSONArray obj)
    {
        StringBuffer str =new StringBuffer();
        for(int i=0;i<obj.length();i++)
        {
            try{
                str=str.append(obj.getJSONObject(i).getString("name")).append(" ");
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return str.toString();
    }
    public String parseAuthor (JSONArray arr)
    {
        StringBuffer str =new StringBuffer();
        for(int i=0;i<arr.length();i++)
        {
            try{
                str=str.append(arr.getString(i)).append(" ");
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return str.toString();
    }

    /**
     * 下载url的图片
     * @param bmurl
     * @return
     */
    public Bitmap DownloadBitmap(String bmurl)
     {
         Bitmap bm=null;
         InputStream is =null;
         BufferedInputStream bis=null;
         try{
             URL  url=new URL(bmurl);
             URLConnection connection=url.openConnection();
             bis=new BufferedInputStream(connection.getInputStream());
             bm= BitmapFactory.decodeStream(bis);
         }catch (Exception e){
             e.printStackTrace();
         }
         finally {
             try {
                 if(bis!=null)
                     bis.close();
                 if (is!=null)
                     is.close();
             }catch (Exception e){
                 e.printStackTrace();
             }
         }
         return bm;
     }
    
    /**
     * Get请求，返回JSON数据
     * @param url
     * @return
     */
 	public static String GetRequest(String url)
 	{
        String content = "";
		try {
			URL getUrl = new URL(url);
	        HttpURLConnection connection = (HttpURLConnection) getUrl.openConnection();
	        connection.connect();
	        // 取得输入流，并使用Reader读取
	        BufferedReader reader = new BufferedReader(new InputStreamReader(
	                connection.getInputStream()));
	        String lines="";
	        while ((lines = reader.readLine()) != null) {
	            //System.out.println(lines);
	        	content+=lines;
	        }
	        reader.close();
	        connection.disconnect();	        
		} catch (Exception e) {
			e.printStackTrace();
		}
		return content;
 	}
 	
 	
	/*
	 * 判断网络是否连接
	 */
	public static boolean isNetworkConnected(Context context) {  
	    if (context != null) {  
	        ConnectivityManager mConnectivityManager = (ConnectivityManager) context  
	                .getSystemService(Context.CONNECTIVITY_SERVICE);  
	        NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();  
	        if (mNetworkInfo != null) {  
	            return mNetworkInfo.isAvailable();  
	        }  
	    }  
	    return false;  
	}
}






