package cn.liaojh.zuzu.bean;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Liaojh on 2016/10/17.
 */

public class User implements Serializable {

    private int id;

    private String realName;

    private String nikeName;

    private String phone;

    private boolean sex;

    private String address;

    private String birthday;

    private String headImageUrl;

    private long OpeanId;

    private String password;

    private String takenIM;

    public String getTakenIM() {
        return takenIM;
    }

    public void setTakenIM(String takenIM) {
        this.takenIM = takenIM;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getNikeName() {
        return nikeName;
    }

    public void setNikeName(String nikeName) {
        this.nikeName = nikeName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public boolean isSex() {
        return sex;
    }

    public void setSex(boolean sex) {
        this.sex = sex;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getHeadImageUrl() {
        return headImageUrl;
    }

    public void setHeadImageUrl(String headImageUrl) {
        this.headImageUrl = headImageUrl;
    }

    public long getOpeanId() {
        return OpeanId;
    }

    public void setOpeanId(long opeanId) {
        OpeanId = opeanId;
    }
}
