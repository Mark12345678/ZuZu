package cn.liaojh.zuzu.utils;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Liaojh on 2016/12/6.
 */

public class DBManagerHistory {

    private DBHelper helper;
    private SQLiteDatabase db;

    public DBManagerHistory(Context context) {
        helper = new DBHelper(context);
        db = helper.getWritableDatabase();
    }


    public void add(String userphone, String goodsname) {
        db.beginTransaction();  //开始事务
        try {

            db.execSQL("INSERT INTO history VALUES(null, ?, ?)", new Object[]{userphone, goodsname});

            db.setTransactionSuccessful();  //设置事务成功完成
        } finally {
            db.endTransaction();    //结束事务
        }
    }

    public List<String> query(String userphone) {
        ArrayList<String> goods = new ArrayList<String>();
        Cursor c = queryTheCursor(userphone);
        while (c.moveToNext()) {
            goods.add(c.getString(c.getColumnIndex("goodsname")));
        }
        c.close();
        return goods;
    }


    public void clearAll(String userphone){
        db.delete("history", "userphone = ?", new String[]{userphone});
    }

    public Cursor queryTheCursor(String userphone) {
        Cursor c = db.rawQuery("SELECT * FROM history where userphone = " + userphone, null);
        return c;
    }

    public void closeDB() {
        db.close();
    }

}
