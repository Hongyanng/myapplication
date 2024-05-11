package com.guit.edu.myapplication.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.guit.edu.myapplication.Code;
import com.guit.edu.myapplication.R;
import com.guit.edu.myapplication.Suit;
import com.guit.edu.myapplication.entity.User;

import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

public class FindPassword_Activity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = null;
    private EditText find_phone;//手机号码
    private EditText find_sms;//验证码
    private EditText find_password;//重设密码
    private ImageView find_image;//验证码图片
    private TextView findword;//完成
    private TextView login_go;//去登陆
    private String realCode;
    private String phone;
    private String sms;
    private String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_findpassword);
        initView();
    }

    //初始化控件
    private void initView() {
        //控件变量
        find_phone = findViewById(R.id.Find_Phone);
        find_sms = findViewById(R.id.Find_SMS);
        find_password = findViewById(R.id.Find_Password);
        find_image = findViewById(R.id.Find_Image);
        findword = findViewById(R.id.Findword);
        login_go = findViewById(R.id.Login_Go);

        //监听机制
        find_image.setImageBitmap(Code.getInstance().createBitmap());
        realCode = Code.getInstance().getCode().toLowerCase();
        find_image.setOnClickListener(this);
        findword.setOnClickListener(this);
        login_go.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent();
        switch (view.getId()){
            case R.id.Find_Image:
                find_image.setImageBitmap(Code.getInstance().createBitmap());
                realCode = Code.getInstance().getCode().toLowerCase();
                Log.v(TAG,"realCode"+realCode);
                break;
            case R.id.Findword:
                Getdata();
                //判断不能为空
                if (TextUtils.isEmpty(phone)){
                    Toast.makeText(getApplicationContext(),"请输入手机号码",Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(),"请输入密码",Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(sms)) {
                    Toast.makeText(getApplicationContext(),"请输入验证码",Toast.LENGTH_SHORT).show();
                } else if (Suit.PhoneSuit(phone.trim()) != true) {
                    Toast.makeText(getApplicationContext(),"请输入正确的手机号码",Toast.LENGTH_SHORT).show();
                } else if (Suit.PasswordSuit(password.trim()) != true) {
                    Toast.makeText(getApplicationContext(),"密码最少包含3个字母",Toast.LENGTH_SHORT).show();
                } else if (password.length() < 6) {
                    Toast.makeText(getApplicationContext(),"密码不得少于6位数",Toast.LENGTH_SHORT).show();
                } else if (password.length() > 16) {
                    Toast.makeText(getApplicationContext(), "密码不得多于16位数", Toast.LENGTH_SHORT).show();
                }else {
                    BmobQuery<User> bmobQuery = new BmobQuery<>();
                    bmobQuery.findObjects(new FindListener<User>() {
                        @Override
                        public void done(List<User> object, BmobException e) {
                            if (e == null){
                                int count = 0;//判断是否查询到尾--遍历
                                //查询判断用户是否已经存在
                                for (User user : object){
                                    if (user.getUsername().equals(phone)){
                                        Toast.makeText(getApplicationContext(),"该用户存在",Toast.LENGTH_SHORT).show();
                                        //判断验证码是否正确
                                        if (sms.equals(realCode)) {
                                            //云端数据更新
                                            User user1 = new User();
                                            user1.setPassword(password);
                                            user1.update(user.getObjectId(), new UpdateListener() {
                                                @Override
                                                public void done(BmobException e) {
                                                    if (e == null) {
                                                        Toast.makeText(getApplicationContext(),"密码修改成功",Toast.LENGTH_SHORT).show();
                                                    }else {
                                                        Toast.makeText(getApplicationContext(),"修改失败"+"\n"+"错误代码："+e.getErrorCode(),Toast.LENGTH_LONG).show();
                                                    }
                                                }
                                            });
                                            finish();
                                            break;
                                        }else {
                                            find_sms.setText("");
                                            Toast.makeText(getApplicationContext(), "验证码错误", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                    count++;
                                }
                                //查询到尾没有
                                if (count >= object.size()){
                                    Toast.makeText(getApplicationContext(),"该用户不存在",Toast.LENGTH_SHORT).show();
                                }
                            }else {
                                Toast.makeText(getApplicationContext(),"该用户不存在",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
                break;
            case R.id.Login_Go:
                finish();
                break;
        }
    }

    //获取输入的数据
    private void Getdata() {
        phone = find_phone.getText().toString().trim();
        sms = find_sms.getText().toString().toLowerCase();
        password = find_password.getText().toString().trim();
    }
}