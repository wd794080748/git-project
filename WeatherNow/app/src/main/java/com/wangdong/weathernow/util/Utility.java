package com.wangdong.weathernow.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

import com.wangdong.weathernow.db.WeatherNowDB;
import com.wangdong.weathernow.model.City;
import com.wangdong.weathernow.model.County;
import com.wangdong.weathernow.model.Province;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

public class Utility {
    public static final String TAG = "Utility";
    /*
    * 解析和处理服务器返回的省级数据
    * */
    public synchronized static boolean handleProvincesResponse(WeatherNowDB weatherNowDB,String response){
        if(!TextUtils.isEmpty(response)){
            String[] allProvinces = response.split(",");
            if(allProvinces != null&& allProvinces.length>0){
                for(String provinceStr: allProvinces){
                    String[] array = provinceStr.split("\\|");
                    Province province = new Province();
                    province.setProvinceCode(array[0]);
                    province.setProvinceName(array[1]);
                    DebugLog.debugLog(TAG,array[1]+"--"+array[0]);
                    //将解析出来的数据存储到Province表中
                    Log.i(TAG, "handleProvincesResponse: "+province.toString());
                    weatherNowDB.saveProvince(province);
                }
            }
            return true;
        }
        return false;
    }

    /*
     * 解析和处理服务器返回的市级数据
     * */
    public synchronized static boolean handleCityResponse(WeatherNowDB weatherNowDB,String response,int provinceId){
        if(!TextUtils.isEmpty(response)){
            String[] allCities = response.split(",");
            if(allCities != null&& allCities.length>0){
                for(String cityStr: allCities){
                    String[] array = cityStr.split("\\|");
                    City city = new City();
                    Log.i(TAG, "handleCityResponse: "+ Arrays.toString(array));
                    city.setCityCode(array[0]);
                    city.setCityName(array[1]);
                    city.setProvinceId(provinceId);
                    //将解析出来的数据存储到City表中
                    Log.i(TAG, "handleCityResponse: "+city.toString());
                    weatherNowDB.saveCity(city);
                }
            }
            return true;
        }
        return false;
    }

    /*
     * 解析和处理服务器返回的县级数据
     * */
    public synchronized static boolean handleCountyResponse(WeatherNowDB weatherNowDB,String response,int cityId){
        if(!TextUtils.isEmpty(response)){
            String[] allCounties = response.split(",");
            if(allCounties != null&& allCounties.length>0){
                for(String countyStr: allCounties){
                    String[] array = countyStr.split("\\|");
                    County county = new County();
                    county.setCountyCode(array[0]);
                    county.setCountyName(array[1]);
                    county.setCityId(cityId);
                    DebugLog.debugLog(TAG,Arrays.toString(array));
                    DebugLog.debugLog(TAG,county.toString());
                    //将解析出来的数据存储到City表中
                    weatherNowDB.saveCounty(county);
                }
            }
            return true;
        }
        return false;
    }
    /*
    * 解析服务器返回的Json数据
    * 并将解析出的数据存储到本地
    * */
    public static void hnadleWeatherResponse (Context context,String response){
        try {
            JSONObject jsonObject = new JSONObject(response);
            DebugLog.debugLog(TAG,response);
            JSONObject weatherinfo = jsonObject.getJSONObject("weatherinfo");
            String cityName = weatherinfo.optString("city");
            DebugLog.debugLog(TAG,cityName);
            String weatherCode = weatherinfo.optString("cityid");
            String temp1 = weatherinfo.optString("temp1");
            String temp2 = weatherinfo.optString("temp2");
            String weatherDesp = weatherinfo.optString("weather");
            String publishTime = weatherinfo.optString("ptime");
            saveWeatherInfo(context,cityName,weatherCode,temp1,temp2,weatherDesp,publishTime);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /*
    * 将服务器返回的所有天气信息存储到sharePreference文件夹中
    * */
    public static void saveWeatherInfo(Context context ,String cityName ,String weatherCode ,String temp1 ,String temp2 ,String weatherDesp ,String publishTime){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年M月d日");
        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor edit = defaultSharedPreferences.edit();
        edit.putString("city_name",cityName);
        edit.putBoolean("city_selected",true);
        edit.putString("weather_code",weatherCode);
        edit.putString("temp1",temp1);
        edit.putString("temp2",temp2);
        edit.putString("weather_desp",weatherDesp);
        edit.putString("publish_time",publishTime);
        edit.putString("current_time",sdf.format(new Date()));
        edit.commit();
    }

}
