package com.example.work.utils;

import android.app.Application;

import com.example.work.database.DaoMaster;
import com.example.work.database.DaoSession;
import com.facebook.stetho.Stetho;

public class SessionInitializer extends Application {

    private DaoSession daoSession;

    @Override
    public void onCreate() {
        super.onCreate();
        Stetho.initializeWithDefaults(this);
        daoSession=new DaoMaster(new DaoMaster.DevOpenHelper(this,"database").getWritableDb()).newSession();

    }

    public DaoSession getDaoSession() {
        return daoSession;
    }
}
