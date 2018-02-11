package com.wangdong.weathernow.util;

import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Author:WangDong
 * Time:2018/1/23 17:57
 * Description:this is HttpUtil
 */
public class HttpUtil {
    public static final String TAG = "HttpUtil";
    public static void sendHttpRequest(final String address,final HttpCallbackListener listener){
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                InputStream inputStream = null;
                BufferedReader bufferedReader = null;
                try {
                    URL url = new URL(address);
                    connection= (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(8000);
                    connection.setReadTimeout(8000);
                    inputStream = connection.getInputStream();
                    bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                    String line = null;
                    StringBuilder stringBuilder = new StringBuilder();
                    while((line = bufferedReader.readLine())!=null){
                        stringBuilder.append(line);
                    }
                    if(listener!=null){
                        listener.onFinish(stringBuilder.toString());
                    }
                } catch (Exception e) {
                    if(listener!=null){
                        listener.onError(e);
                    }
                    e.printStackTrace();
                }finally {
                        try {
                            if(inputStream != null){
                                inputStream.close();
                            }
                            if(bufferedReader != null){
                                bufferedReader.close();
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                            DebugLog.debugLog(TAG,"close is fail");
                        }
                    connection.disconnect();
                }
            }
        }).start();
    }
}
