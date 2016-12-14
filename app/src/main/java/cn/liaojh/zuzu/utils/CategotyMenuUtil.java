package cn.liaojh.zuzu.utils;

import com.bigkoo.pickerview.OptionsPickerView;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/12/13.
 * 这个专门用于初始化选择菜单
 */

public class CategotyMenuUtil {

    public static OptionsPickerView initCategory(OptionsPickerView pvOptions ){
        ArrayList<String> options1Items = new ArrayList<String>();
        ArrayList<ArrayList<String>> options2Items = new ArrayList<ArrayList<String>>();

        options1Items.add(0,"实物");
        options1Items.add(1,"时间");
        options1Items.add(2,"需求");
        ArrayList<String> list1 = new ArrayList<String>();
        list1.add("书籍文具");
        list1.add("运动器材");
        list1.add("衣服鞋子");
        list1.add("生活工具");
        list1.add("植物盆栽");
        list1.add("艺术器材");
        list1.add("其他");
        options2Items.add(0,list1);
        ArrayList<String> list2 = new ArrayList<String>();
        list2.add("交心聊天");
        list2.add("外出活动");
        list2.add("日常闲逛");
        list2.add("教学辅导");
        options2Items.add(1,list2);
        ArrayList<String> list3 = new ArrayList<String>();
        list3.add("物品需求");
        list3.add("时间需求");
        options2Items.add(2,list3);

        //二级联动效果
        pvOptions.setPicker(options1Items, options2Items, true);
        pvOptions.setTitle("选择类别");
        pvOptions.setCyclic(false);
        pvOptions.setSelectOptions(0, 0);

        return pvOptions;
    }

}
