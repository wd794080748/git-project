package com.wangdong.weathernow.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wangdong.weathernow.R;
import com.wangdong.weathernow.util.DebugLog;
import com.wangdong.weathernow.util.HttpCallbackListener;
import com.wangdong.weathernow.util.HttpUtil;
import com.wangdong.weathernow.util.Utility;

public class WeatherActivity extends Activity implements View.OnClickListener {

    private TextView mCityName;
    private TextView mPublishText;
    private TextView mCurrentDate;
    private TextView mWeatherDesp;
    private TextView mTemp1;
    private TextView mTemp2;
    private LinearLayout mWeatherInfoLayout;
    public static final String TAG = "WeatherActivity";
    private Button mSwitchCity;
    private Button mRefreshWeather;
    private String weatherCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_weather);
        initView();
        String countryCode = getIntent().getStringExtra("county_code");
        if (!TextUtils.isEmpty(countryCode)) {
            //有县级代号是就去查询天气
            mPublishText.setText("同步中····");
            mWeatherInfoLayout.setVisibility(View.INVISIBLE);
            mCityName.setVisibility(View.INVISIBLE);
            queryWeatherCode(countryCode);
            DebugLog.debugLog(TAG, "countryCode="+countryCode);
        } else {
            DebugLog.debugLog(TAG, "show weather");
            //没有县级代号就直接显示天气
            showWeather();
        }
    }

    private void initView() {
        mCityName = (TextView) findViewById(R.id.city_name);
        mPublishText = (TextView) findViewById(R.id.publish_text);
        mCurrentDate = (TextView) findViewById(R.id.current_date);
        mWeatherDesp = (TextView) findViewById(R.id.weather_desp);
        mTemp1 = (TextView) findViewById(R.id.temp1);
        mTemp2 = (TextView) findViewById(R.id.temp2);
        mWeatherInfoLayout = (LinearLayout) findViewById(R.id.weather_info_layout);
        mRefreshWeather = (Button) findViewById(R.id.refresh_weather);
        mSwitchCity = (Button) findViewById(R.id.switch_city);
        mRefreshWeather.setOnClickListener(this);
        mSwitchCity.setOnClickListener(this);
    }

    /*
     * 查询县级代号所对应的天气代号
     * */
    private void queryWeatherCode(String countryCode) {
        String address = "http://www.weather.com.cn./data/list3/city" + countryCode + ".xml";
        queryFromServer(address, "countyCode");
    }

    /*
     * 根据传入的地址和类型去向服务器查询天气代号或者天气信息
     * */
    private void queryFromServer(String address, final String type) {
        HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                DebugLog.debugLog(TAG, response);
                if ("countyCode".equals(type)) {
                    if (!TextUtils.isEmpty(response)) {
                        //从服务器返回数据中解析出天气代号
                        String[] array = response.split("\\|");
                        if (array != null && array.length == 2) {
                             weatherCode = array[1];
                            queryWeatherInfo(weatherCode);
                        }
                    }
                } else if ("weatherCode".equals(type)) {
                    //处理服务器返回的信息
                    Utility.hnadleWeatherResponse(WeatherActivity.this, response);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showWeather();
                        }
                    });
                }
            }

            @Override
            public void onError(Exception e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mPublishText.setText("同步失败");
                    }
                });

            }
        });
    }

    /*
     * 查询天气代号所对应的天气
     * */
    private void queryWeatherInfo(String weatherCode) {
        String address = "http://www.weather.com.cn/data/cityinfo/" + weatherCode + ".html";
        DebugLog.debugLog(TAG, address);
        queryFromServer(address, "weatherCode");
    }

    private void showWeather() {
        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mCityName.setText(defaultSharedPreferences.getString("city_name", ""));
        mTemp1.setText(defaultSharedPreferences.getString("temp1", ""));
        mTemp2.setText(defaultSharedPreferences.getString("temp2", ""));
        mWeatherDesp.setText(defaultSharedPreferences.getString("weather_desp", ""));
        mPublishText.setText((defaultSharedPreferences.getString("publish_time", "")) + "发布");
        mCurrentDate.setText(defaultSharedPreferences.getString("current_time", ""));
        DebugLog.debugLog(TAG, mPublishText.getText().toString() + mCurrentDate.getText().toString());
        mWeatherInfoLayout.setVisibility(View.VISIBLE);
        mCityName.setVisibility(View.VISIBLE);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                break;
            case R.id.switch_city:
                Intent intent = new Intent(this,ChooseAreaActivity.class);
                intent.putExtra("from_weather_activity",true);
                startActivity(intent);
                finish();
                break;
            case R.id.refresh_weather:
                mPublishText.setText("同步中····");
                SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
                String weather_code = defaultSharedPreferences.getString("weather_code", null);
                DebugLog.debugLog(TAG,weather_code);
                if(!TextUtils.isEmpty(weather_code)){
                queryWeatherInfo(weather_code);
            }
                break;
        }
    }
}
