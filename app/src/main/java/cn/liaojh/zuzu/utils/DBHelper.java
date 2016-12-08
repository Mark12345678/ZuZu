package cn.liaojh.zuzu.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Liaojh on 2016/12/6.
 */

public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "zuzu.db";
    private static final int DATABASE_VERSION = 1;

    public DBHelper(Context context) {
        //CursorFactory设置为null,使用默认值
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("CREATE TABLE IF NOT EXISTS history" +
                "(_id INTEGER PRIMARY KEY AUTOINCREMENT, userphone VARCHAR, goodsname VARCHAR )");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("ALTER TABLE person ADD COLUMN other STRING");
    }
}
