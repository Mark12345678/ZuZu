package cn.liaojh.zuzu.bean;

import java.util.List;

/**
 * Created by Liaojh on 2016/10/27.
 */

public class Page<T>  {

    private int curryPage;

    private int pageSize;

    private int totalRecord;

    private int totalPage;

    private List<T> list;

    public int getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
    }

    public int getCurryPage() {
        return curryPage;
    }

    public void setCurryPage(int curryPage) {
        this.curryPage = curryPage;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getTotalRecord() {
        return totalRecord;
    }

    public void setTotalRecord(int totalRecord) {
        this.totalRecord = totalRecord;
    }

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }
}
