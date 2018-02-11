package com.wangdong.weathernow.model;

/**
 * Author:WangDong
 * Time:19:14
 * Description:this is County
 */
public class County {
    private int id;
    private String countyName;
    private String countyCode;
    private int cityId;

    public void setCityId(int cityId) {

        this.cityId = cityId;
    }

    public int getCityId() {

        return cityId;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setCountyName(String countyName) {
        this.countyName = countyName;
    }

    public void setCountyCode(String countyCode) {
        this.countyCode = countyCode;
    }

    public int getId() {

        return id;
    }

    public String getCountyName() {
        return countyName;
    }

    public String getCounyCode() {
        return countyCode;
    }

    @Override
    public String toString() {
        return "County{" +
                "id=" + id +
                ", countyName='" + countyName + '\'' +
                ", countyCode='" + countyCode + '\'' +
                ", cityId=" + cityId +
                '}';
    }
}
