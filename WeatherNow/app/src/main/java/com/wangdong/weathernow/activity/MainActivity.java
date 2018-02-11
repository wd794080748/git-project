package com.wangdong.weathernow.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.wangdong.weathernow.R;
import com.wangdong.weathernow.util.DebugLog;
import com.wangdong.weathernow.util.HttpCallbackListener;
import com.wangdong.weathernow.util.HttpUtil;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = new Intent(this,ChooseAreaActivity.class);
        startActivity(intent);
        finish();
//        HttpUtil.sendHttpRequest("http://www.weather.com.cn/data/list3/city.xml", new HttpCallbackListener() {
//            @Override
//            public void onFinish(String response) {
//                DebugLog.debugLog(TAG,response);
//            }
//
//            @Override
//            public void onError(Exception e) {
//
//            }
//        });
    }
}
