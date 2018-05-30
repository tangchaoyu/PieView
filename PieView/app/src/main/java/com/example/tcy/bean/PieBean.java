package com.example.tcy.bean;

import android.graphics.Region;

/**
 * Created by tcy on 2018/5/18.
 */
public class PieBean {

    private String name;        // 名字
    private float value;        // 数值
    private String percentage;   // 百分比

    private int color = 0;      // 颜色
    private float angle = 0;    // 角度
    private float cutAngle=0;  //当前角度
    private Region region; //点击范围
    private boolean carve = false; //当前弧度是否被切割

    public String getName() {
        return name;
    }

    public float getValue() {
        return value;
    }

    public String getPercentage() {
        return percentage;
    }

    public int getColor() {
        return color;
    }

    public float getAngle() {
        return angle;
    }


    public void setName(String name) {
        this.name = name;
    }

    public void setValue(float value) {
        this.value = value;
    }

    public void setPercentage(String percentage) {
        this.percentage = percentage;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public void setAngle(float angle) {
        this.angle = angle;
    }


    public float getCutAngle() {
        return cutAngle;
    }

    public void setCutAngle(float cutAngle) {
        this.cutAngle = cutAngle;
    }

    public Region getRegion() {
        return region;
    }

    public void setRegion(Region region) {
        this.region = region;
    }

    public boolean isCarve() {
        return carve;
    }

    public void setCarve(boolean carve) {
        this.carve = carve;
    }
}
