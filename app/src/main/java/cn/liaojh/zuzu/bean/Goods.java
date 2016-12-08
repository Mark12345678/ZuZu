package cn.liaojh.zuzu.bean;

import java.io.Serializable;

/**
 * Created by Liaojh on 2016/10/27.
 */

public class Goods implements Serializable,Comparable {

    private Integer id;

    private String goodsName;

    private Integer goodsNum;

    private String goodsDescribe;

    private float goodsPrice;

    private String goodsResponsibility;

    private double goodsLongitude;

    private double goodsLatitude;

    private Integer goodsState;

    private String standPic;

    User user;

    public String getStandPic() {
        return standPic;
    }

    public void setStandPic(String standPic) {
        this.standPic = standPic;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public Integer getGoodsNum() {
        return goodsNum;
    }

    public void setGoodsNum(Integer goodsNum) {
        this.goodsNum = goodsNum;
    }

    public String getGoodsDescribe() {
        return goodsDescribe;
    }

    public void setGoodsDescribe(String goodsDescribe) {
        this.goodsDescribe = goodsDescribe;
    }

    public float getGoodsPrice() {
        return goodsPrice;
    }

    public void setGoodsPrice(float goodsPrice) {
        this.goodsPrice = goodsPrice;
    }

    public String getGoodsResponsibility() {
        return goodsResponsibility;
    }

    public void setGoodsResponsibility(String goodsResponsibility) {
        this.goodsResponsibility = goodsResponsibility;
    }

    public double getGoodsLongitude() {
        return goodsLongitude;
    }

    public void setGoodsLongitude(double goodsLongitude) {
        this.goodsLongitude = goodsLongitude;
    }

    public double getGoodsLatitude() {
        return goodsLatitude;
    }

    public void setGoodsLatitude(double goodsLatitude) {
        this.goodsLatitude = goodsLatitude;
    }

    public Integer getGoodsState() {
        return goodsState;
    }

    public void setGoodsState(Integer goodsState) {
        this.goodsState = goodsState;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public int compareTo(Object o) {

        Goods other = (Goods) o;


        return this.getId().compareTo(other.getId());
    }
}
