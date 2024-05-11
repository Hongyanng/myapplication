package com.guit.edu.myapplication;

import android.app.Application;

import cn.bmob.v3.Bmob;

public class MyBmobClass extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Bmob.initialize(this,"570ca2d5d1892d30b171b1550e2aeb2c");
    }
}
