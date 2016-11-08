package com.alonebums.project.base;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;

import com.alonebums.project.db.DBTable;
import com.alonebums.project.utils.LUtils;
import com.library.base.db.ISQLiteCallBackListener;
import com.library.base.db.SQLiteHelper;
import com.library.utils.sp.SPUtils;

/**
 * Created by linqs on 2016/8/7.
 */
public class BaseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        initSP();
        initLog();
        initDB();
    }

    private void initDB() {
        SQLiteHelper.getInstances().init(getApplicationContext(), "myDB.db", 1, new ISQLiteCallBackListener() {
            @Override
            public void onCreate(SQLiteDatabase db) {
                db.execSQL(DBTable.CREATE_TABLE_SQL);
                db.execSQL(DBTable.CREATE_INDEX_NAME_SQL);
            }

            @Override
            public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

            }
        });
    }

    private void initLog() {
        //初始化Log工具类
        LUtils.init();
    }

    private void initSP() {
        SPUtils.getInstance().init(getApplicationContext());
        //可以自定义文件名字
        //SPUtils.getInstance().init(getApplicationContext(),"sp_file_name");
    }
}
