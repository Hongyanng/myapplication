package com.guit.edu.myapplication;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Suit {
    //手机号码检测
    public static boolean PhoneSuit(String phone){
        String ChineseMainland = "^((13[0-9])|(14[5,7,9])|(15[0-3,5-9])|(166)|(17[3,5,6,7,8])" + "|(18[0-9])|(19[8,9]))\\d{8}$";
        String HongKong = "^(5|6|8|9)\\d{7}$";

        Matcher C = Pattern.compile(ChineseMainland).matcher(phone);
        Matcher H = Pattern.compile(HongKong).matcher(phone);
        return C.matches() || H.matches();
    }

    //密码检测（最少3个字母）
    public static boolean PasswordSuit(String password){
        char[] s = password.toCharArray();
        int count = 0 ;
        //检测所有的密码中有多少个字母
        for (int i = 0;i < s.length;i++){
            if ((s[i] >= 'a' && s[i] <= 'z') || (s[i] >= 'A' && s[i] <= 'Z')){
                count++;
            }
        }
        if (count >= 3){
            return true;
        }else {
            return false;
        }
    }
}