package cn.liaojh.zuzu.bean;

import java.io.Serializable;

/**
 * Created by Liaojh on 2016/12/28.
 */

public class isPayGoods extends Goods implements Serializable {

    int usreSure;

    int masterSure;



    public int getUsreSure() {
        return usreSure;
    }

    public void setUsreSure(int usreSure) {
        this.usreSure = usreSure;
    }

    public int getMasterSure() {
        return masterSure;
    }

    public void setMasterSure(int masterSure) {
        this.masterSure = masterSure;
    }


}
