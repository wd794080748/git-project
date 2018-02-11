package com.wangdong.weathernow.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.wangdong.weathernow.R;
import com.wangdong.weathernow.db.WeatherNowDB;
import com.wangdong.weathernow.model.City;
import com.wangdong.weathernow.model.County;
import com.wangdong.weathernow.model.Province;
import com.wangdong.weathernow.util.DebugLog;
import com.wangdong.weathernow.util.HttpCallbackListener;
import com.wangdong.weathernow.util.HttpUtil;
import com.wangdong.weathernow.util.Utility;

import java.sql.Array;
import java.util.ArrayList;
import java.util.List;

public class ChooseAreaActivity extends Activity {
    public static final int LEVEL_PROVINCE = 0;
    public static final int LEVEL_CITY = 1;
    public static final int LEVEL_COUNTY = 2;
    private int currentLevel;
    private ListView listView;
    private TextView textView;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> dataList = new ArrayList<String>();
    private WeatherNowDB weatherNowDB;
    private List<Province> provinceList;
    private List<City> cityList;
    private Province selectProvince;
    protected City selectCity;
    private List<County> countyList;
    public static final String TAG = "ChooseAreaActivity";
    private  boolean from_weather_activity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
      from_weather_activity = getIntent().getBooleanExtra("from_weather_activity", false);
        if(defaultSharedPreferences.getBoolean("city_selected",false)&&!from_weather_activity){
            Intent intent = new Intent(this, WeatherActivity.class);
            startActivity(intent);
            finish();
        }
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_choose_area);
        listView = (ListView) findViewById(R.id.list_view);
        textView = (TextView) findViewById(R.id.title_text);
        weatherNowDB = WeatherNowDB.getInstance(this);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, dataList);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                DebugLog.debugLog(TAG,"`````````````````"+currentLevel+"");
                if (currentLevel == LEVEL_PROVINCE) {
                    selectProvince = provinceList.get(position);
                    Log.i(TAG, "onItemClick: "+selectProvince.getProvinceName());
                    queryCities(selectProvince);
                } else if (currentLevel == LEVEL_CITY) {
                    selectCity = cityList.get(position);
                    queryCounties(selectCity);
                }else if(currentLevel == LEVEL_COUNTY){
                    String countyCode = countyList.get(position).getCounyCode();
                    DebugLog.debugLog(TAG,"countyCode="+countyCode);
                    Intent intent = new Intent(ChooseAreaActivity.this, WeatherActivity.class);
                    intent.putExtra("county_code",countyCode);
                    startActivity(intent);
                    finish();
                }
                DebugLog.debugLog(TAG,"`````````````````"+currentLevel+"");
            }
        });
        queryProvinces();

    }

    /*
     * 查询全国所有的省，优先从数据库查询，如果没有查询到，再去服务器上查询
     * */
    public void queryProvinces() {
        provinceList = weatherNowDB.loadProvinces();
        if (provinceList.size() > 0) {
            dataList.clear();
            for (Province province : provinceList) {
                dataList.add(province.getProvinceName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            textView.setText("中国");
            currentLevel = LEVEL_PROVINCE;
        } else {
            queryFromServer(null, "province");
        }
    }

    /*
     * 查询全国所有的市，优先从数据库查询，如果没有查询到，再去服务器上查询
     * */
    public void queryCities(Province selectProvince) {
        cityList = weatherNowDB.loadCity(selectProvince.getId());
        Log.i(TAG, "queryCities: "+cityList.size());
        if (cityList.size() > 0) {
            dataList.clear();
            for (City city : cityList) {
                dataList.add(city.getCityName());
                Log.i(TAG, "queryCities: "+city.toString());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            textView.setText(selectProvince.getProvinceName());
            currentLevel = LEVEL_CITY;

        } else {
            queryFromServer(selectProvince.getProvinceCode(), "city");
        }
    }

    /*
     * 查询全国所有的区，优先从数据库查询，如果没有查询到，再去服务器上查询
     * */
    public void queryCounties(City selectCity) {
        DebugLog.debugLog(TAG,selectCity.toString());
        countyList = weatherNowDB.loadCounty(selectCity.getId());
        DebugLog.debugLog(TAG,"11111111111111111");
        DebugLog.debugLog(TAG,countyList.size()+"");
        if (countyList.size() > 0) {
            dataList.clear();
            for (County county : countyList) {
                dataList.add(county.getCountyName());
                DebugLog.debugLog(TAG,county.toString());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            textView.setText(selectCity.getCityName());
            currentLevel = LEVEL_COUNTY;
        } else {
            queryFromServer(selectCity.getCityCode(), "county");
        }
    }

    /*
     * 根据传入的代号和类型从服务器上查询省市县数据
     * */
    public void queryFromServer(final String code, final String type) {
        String address = null;
        if (!TextUtils.isEmpty(code)) {
            address = "http://www.weather.com.cn/data/list3/city" + code + ".xml"; //查询省下包含的市
        } else {
            address = "http://www.weather.com.cn/data/list3/city.xml"; //查询所有的省
        }
        Log.i(TAG, "queryFromServer: "+address);
        HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {

            private boolean aBoolean;

            @Override
            public void onFinish(String response) {
                if ("province".equals(type)) {
                    aBoolean = Utility.handleProvincesResponse(weatherNowDB, response);
                } else if ("city".equals(type)) {
                    aBoolean = Utility.handleCityResponse(weatherNowDB, response, selectProvince.getId());
                    Log.i(TAG, "onFinish: "+aBoolean);
                } else if ("county".equals(type)) {
                    DebugLog.debugLog(TAG,"county");
                    aBoolean = Utility.handleCountyResponse(weatherNowDB, response, selectCity.getId());
                }
                if(aBoolean){
                    //通过runOnUiThread方法回到主线程
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if("province".equals(type)){
                                queryProvinces();
                            }else if("city".equals(type)){
                                queryCities(selectProvince);
                            }else  if("county".equals(type)){
                                queryCounties(selectCity);
                            }
                        }
                    });
                }


            }

            @Override
            public void onError(Exception e) {
runOnUiThread(new Runnable() {
    @Override
    public void run() {
        Toast.makeText(ChooseAreaActivity.this, "加载失败", Toast.LENGTH_SHORT).show();
    }
});
            }
        });
    }

    @Override
    public void onBackPressed() {
        DebugLog.debugLog(TAG,from_weather_activity+"``````"+currentLevel);
        if(currentLevel == LEVEL_COUNTY){
            queryCities(selectProvince);
        }else if(currentLevel == LEVEL_CITY){
            queryProvinces();
        }else {
            DebugLog.debugLog(TAG,from_weather_activity+"");
            if(from_weather_activity){
                Intent intent =new Intent(this,WeatherActivity.class);
                startActivity(intent);
            }
            finish();
        }
    }
}
