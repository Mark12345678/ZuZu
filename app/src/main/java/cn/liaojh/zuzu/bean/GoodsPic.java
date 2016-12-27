package cn.liaojh.zuzu.bean;

import java.io.Serializable;

/**
 * Created by Liaojh on 2016/12/26.
 *
 * [{"picUrl":"w_15521036529_5_jiaoxinliaotian0.png","picType":2,"id":125}]
 */


public class GoodsPic implements Serializable {

    int id;

    String picUrl;

    int picType;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public int getPicType() {
        return picType;
    }

    public void setPicType(int picType) {
        this.picType = picType;
    }
}
