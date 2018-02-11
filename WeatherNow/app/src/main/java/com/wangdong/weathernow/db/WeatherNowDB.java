package com.wangdong.weathernow.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.wangdong.weathernow.model.City;
import com.wangdong.weathernow.model.County;
import com.wangdong.weathernow.model.Province;

import java.util.ArrayList;
import java.util.List;

/**
 * Author:WangDong
 * Time:19:39
 * Description:this is WeatherNowDB
 */
public class WeatherNowDB {
    /*
     * 数据库名
     * */
    public static final String DB_NAME = "weather_now";
    /*
     * 数据库版本
     * */
    public static final int VERSION = 1;

    private static WeatherNowDB weatherNowDB;
    private SQLiteDatabase db;

    /*
     * 将构造方法私有化
     * */
    private WeatherNowDB(Context context) {
        WeatherNowOpenHelper weatherNowOpenHelper = new WeatherNowOpenHelper(context, DB_NAME, null, VERSION);
        db = weatherNowOpenHelper.getWritableDatabase();
    }

    /*
     *获取weatherNow实例 */
    public synchronized static WeatherNowDB getInstance(Context context) {
        if (weatherNowDB == null) {
            weatherNowDB = new WeatherNowDB(context);
        }
        return weatherNowDB;
    }

    /*
     * 将province数据存储到数据库
     * */
    public void saveProvince(Province province) {
        if (province != null) {
            ContentValues values = new ContentValues();
            values.put("province_name", province.getProvinceName());
            values.put("province_code", province.getProvinceCode());
            db.insert("Province", null, values);
        }
    }

    /*
     * 从数据库读取全国所有的省份信息
     * */
    public List<Province> loadProvinces() {
        ArrayList<Province> provincesList = new ArrayList<>();
        Cursor cursor = db.query("Province", null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                Province province = new Province();
                province.setId(cursor.getInt(cursor.getColumnIndex("id")));
                province.setProvinceCode(cursor.getString(cursor.getColumnIndex("province_code")));
                province.setProvinceName(cursor.getString(cursor.getColumnIndex("province_name")));
                provincesList.add(province);
            } while (cursor.moveToNext());
        }
        if (cursor != null) {
            cursor.close();
        }
        return provincesList;
    }

    /*
     * 将city数据存储到数据库
     * */
    public void saveCity(City city) {
        if (city != null) {
            ContentValues values = new ContentValues();
            values.put("city_name", city.getCityName());
            values.put("city_code", city.getCityCode());
            values.put("province_id", city.getProvinceId());
            db.insert("City", null, values);
        }
    }

    /*
     * 从数据库读取某省下所有城市信息
     * */
    public List<City> loadCity(int provinceId) {
        ArrayList<City> cityList = new ArrayList<>();
        Cursor cursor = db.query("City", null, "province_id= ?", new String[]{String.valueOf(provinceId)}, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                City city = new City();
                city.setId(cursor.getInt(cursor.getColumnIndex("id")));
                city.setCityCode(cursor.getString(cursor.getColumnIndex("city_code")));
                city.setCityName(cursor.getString(cursor.getColumnIndex("city_name")));
                city.setProvinceId(provinceId);
                cityList.add(city);
            } while (cursor.moveToNext());
        }
        if (cursor != null) {
            cursor.close();
        }
        return cityList;
    }

    /*
     * 将county数据存储到数据库
     * */
    public void saveCounty(County county) {
        if (county != null) {
            ContentValues values = new ContentValues();
            values.put("county_name", county.getCountyName());
            values.put("county_code", county.getCounyCode());
            values.put("city_id", county.getCityId());
            db.insert("County", null, values);
        }
    }

    /*
     * 从数据库读取某城市下所有地区信息
     * */
    public List<County> loadCounty(int cityId) {
        ArrayList<County> countyList = new ArrayList<>();
        Cursor cursor = db.query("County", null, "city_id= ?", new String[]{String.valueOf(cityId)}, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                County county = new County();
                county.setId(cursor.getInt(cursor.getColumnIndex("id")));
                county.setCountyCode(cursor.getString(cursor.getColumnIndex("county_code")));
                county.setCountyName(cursor.getString(cursor.getColumnIndex("county_name")));
                county.setCityId(cityId);
                countyList.add(county);
            } while (cursor.moveToNext());
        }
        if (cursor != null) {
            cursor.close();
        }
        return countyList;
    }
}
