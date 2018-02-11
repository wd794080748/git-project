package com.wangdong.weathernow.model;

/**
 * Author:WangDong
 * Time:19:09
 * Description:this is Province
 */
public class Province {
    private int id;
    private String provinceName;
    private String provinceCode;

    public void setId(int id) {

        this.id = id;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }

    public void setProvinceCode(String provinceCode) {
        this.provinceCode = provinceCode;
    }

    public int getId() {

        return id;
    }

    public String getProvinceName() {
        return provinceName;
    }

    @Override
    public String toString() {
        return "Province{" +
                "id=" + id +
                ", provinceName='" + provinceName + '\'' +
                ", provinceCode='" + provinceCode + '\'' +
                '}';
    }

    public String getProvinceCode() {
        return provinceCode;
    }
}
