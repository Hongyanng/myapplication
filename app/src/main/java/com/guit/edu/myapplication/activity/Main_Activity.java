package com.guit.edu.myapplication.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.DisplayCutout;
import android.view.View;
import android.view.WindowInsets;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.guit.edu.myapplication.R;
import com.guit.edu.myapplication.SPUtils;
import com.guit.edu.myapplication.entity.User;

import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class Main_Activity extends AppCompatActivity implements View.OnClickListener {

    private EditText login_phone;//输入收集号码
    private EditText login_password;//输入密码
    private TextView login;//登陆按钮
    private TextView login_lose;//忘记密码
    private TextView login_resgister;//注册账号
    private String phone;
    private String password;
    private CheckBox remember_password_checkbox;//记住密码

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        loadSavedCredentials();//加载保存的密码

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            handleDisplayCutout();
        }
    }

    //初始化控件
    private void initView() {
        login_phone = findViewById(R.id.Login_Phone);
        login_password = findViewById(R.id.Login_Password);
        login = findViewById(R.id.Login);
        login_lose = findViewById(R.id.Login_Lose);
        login_resgister = findViewById(R.id.Login_Resgister);
        remember_password_checkbox = findViewById(R.id.remember_password_checkbox); // 初始化复选框

        //启动监听事件
        login.setOnClickListener(this);
        login_lose.setOnClickListener(this);
        login_resgister.setOnClickListener(this);
    }

    //点击事件
    @Override
    public void onClick(View view) {
        //添加意图，切换画面
        Intent intent = new Intent();
        //选择监听按钮触发机制
        switch (view.getId()){
            case R.id.Login:
                Getdata();
                //没有输入号码、密码判断为空则弹窗提示
                if (TextUtils.isEmpty(phone)){
                    Toast.makeText(this,"请输入手机号码",Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(password)) {
                    Toast.makeText(this,"请输入密码",Toast.LENGTH_SHORT).show();
                } else {
                    BmobQuery<User> bmobQuery = new BmobQuery<>();
                    bmobQuery.findObjects(new FindListener<User>() {
                        @Override
                        public void done(List<User> object, BmobException e) {
                            //如果没有异常的话进行查询列表账号
                            if(e == null){
                                int count = 0;//计数
                                //遍历
                                for (User user : object) {
                                    //查询到与客户端输入的账号一致
                                    if (user.getUsername().equals(phone)){
                                        //查询到与客户端输入的密码一致
                                        if (user.getPassword().equals(password)){
                                            // 登录成功，保存用户信息
                                            saveUserInfo(getApplicationContext(), user);

                                            // 页面跳转
                                            intent.setClass(getApplicationContext(), First_interface_Activity.class);
                                            startActivity(intent);
                                            finish();
                                            Toast.makeText(getApplicationContext(),"登陆成功",Toast.LENGTH_SHORT).show();
                                            break;
                                        }else {
                                            Toast.makeText(getApplicationContext(),"密码错误",Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                    count++;
                                }
                                //遍历没有查询到，此时count++到object的数量
                                if (count >= object.size()){
                                    Toast.makeText(getApplicationContext(),"账号不存在",Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(getApplicationContext(),"账号不存在",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
                break;
            case R.id.Login_Lose:
                intent.setClass(getApplicationContext(), FindPassword_Activity.class);
                startActivity(intent);
                break;
            case R.id.Login_Resgister:
                intent.setClass(getApplicationContext(), Resgister_Activity.class);
                startActivity(intent);
                break;
        }
    }

    //获取输入的号码与密码
    public void Getdata(){
        phone = login_phone.getText().toString().trim();
        password = login_password.getText().toString().trim();
    }

    // 加载保存的账号密码
    private void loadSavedCredentials() {
        // 从SharedPreferences中获取保存的账号密码
        String savedPhone =  (String) SPUtils.get(this, "saved_phone", "");
        String savedPassword = (String) SPUtils.get(this, "saved_password", "");

        // 如果账号密码非空，则在界面上显示，并勾选记住密码复选框
        if (!TextUtils.isEmpty(savedPhone) && !TextUtils.isEmpty(savedPassword)) {
            login_phone.setText(savedPhone);
            login_password.setText(savedPassword);
            remember_password_checkbox.setChecked(true);
        }
    }

    // 保存用户信息到SharedPreferences
    private void saveUserInfo(Context context, User user) {
        SPUtils.put(context, "username", user.getUsername());
        SPUtils.put(context, "nickname", user.getNickname());
        SPUtils.put(context, "signature", user.getSignature());
        SPUtils.put(context, "gender", user.getGender());
        SPUtils.put(context, "height", user.getHeight());
        SPUtils.put(context, "weight", user.getWeight());
        SPUtils.put(context, "cupcapacity", user.getCupcapacity());

        if (remember_password_checkbox.isChecked()) {
            SPUtils.put(context, "saved_phone", phone);
            SPUtils.put(context, "saved_password", password);
        } else {
            SPUtils.remove(context, "saved_phone");
            SPUtils.remove(context, "saved_password");
        }
    }

    private void handleDisplayCutout() {
        final View rootLayout = findViewById(R.id.root_layout);
        rootLayout.setOnApplyWindowInsetsListener(new View.OnApplyWindowInsetsListener() {
            @Override
            public WindowInsets onApplyWindowInsets(View v, WindowInsets insets) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    DisplayCutout displayCutout = insets.getDisplayCutout();
                    if (displayCutout != null) {
                        int topPadding = displayCutout.getSafeInsetTop();
                        int bottomPadding = displayCutout.getSafeInsetBottom();
                        int leftPadding = displayCutout.getSafeInsetLeft();
                        int rightPadding = displayCutout.getSafeInsetRight();
                        rootLayout.setPadding(leftPadding, topPadding, rightPadding, bottomPadding);
                    }
                }
                return insets;
            }
        });
    }
}
