package cn.liaojh.zuzu.bean;

import java.io.Serializable;

/**
 * Created by Liaojh on 2017/1/1.
 */

public class HistoryRent implements Serializable {

    private Integer id;

    private Goods goods;

    /*private User masteruser;

    private User mUser;*/

    private String userPhone;

    private String masterPhone;

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public String getMasterPhone() {
        return masterPhone;
    }

    public void setMasterPhone(String masterPhone) {
        this.masterPhone = masterPhone;
    }

   public Goods getGoods() {
        return goods;
    }

    public void setGoods(Goods goods) {
        this.goods = goods;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
