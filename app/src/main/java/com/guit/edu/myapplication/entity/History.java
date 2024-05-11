package com.guit.edu.myapplication.entity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import cn.bmob.v3.BmobObject;

public class History extends BmobObject {
    String Username;  //账号
    int Drink;  //饮用量
    String Type; // 饮品种类

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }

    public String getUsername() {
        return Username;
    }

    public void setUsername(String username) {
        Username = username;
    }

    public int getDrink() {
        return Drink;
    }

    public void setDrink(int drink) {
        Drink = drink;
    }
}
