package com.wangdong.weathernow.model;

/**
 * Author:WangDong
 * Time:19:13
 * Description:this is City
 */
public class City {
    @Override
    public String toString() {
        return "City{" +
                "id=" + id +
                ", cityName='" + cityName + '\'' +
                ", cityCode='" + cityCode + '\'' +
                ", provinceId=" + provinceId +
                '}';
    }

    private int id;
    private String cityName;
    private String cityCode;
    private int provinceId;

    public void setId(int id) {
        this.id = id;
    }
    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public void setCityCode(String cityCode) {
        this.cityCode = cityCode;
    }

    public void setProvinceId(int provinceId) {
        this.provinceId = provinceId;
    }

    public int getId() {

        return id;
    }

    public String getCityName() {
        return cityName;
    }

    public String getCityCode() {
        return cityCode;
    }

    public int getProvinceId() {
        return provinceId;
    }

}
