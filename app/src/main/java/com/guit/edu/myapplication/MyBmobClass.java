package com.guit.edu.myapplication;

import android.app.Application;

import cn.bmob.v3.Bmob;


public class MyBmobClass extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // 初始化BmobSDK
        Bmob.initialize(this,"570ca2d5d1892d30b171b1550e2aeb2c");
//
//        // 初始化 JPush SDK
//        JPushInterface.setDebugMode(true);
//        JPushInterface.init(this);

//        // 使用推送服务时的初始化操作
//        BmobInstallation.getCurrentInstallation().save();
//        // 启动推送服务
//        BmobPush.startWork(this);
    }
}
