package com.guit.edu.myapplication.activity;

import androidx.appcompat.app.AppCompatActivity;

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
import cn.bmob.v3.listener.SaveListener;

public class Resgister_Activity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = null;
    private EditText register_phone;//获取注册的手机号码
    private EditText register_password;//获取注册的密码
    private EditText register_sms;//输入验证码
    private TextView register;//注册
    private TextView login_back;//返回登陆
    private ImageView register_paint;//获取验证码
    private String phone;//客户端输入的手机号码
    private String passeword;//客户端输入的密码
    private String sms;//客户端输入的验证码
    private String realCode;//图形验证码

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resgister);
        initView();
    }

    //初始化控件
    private void initView() {
        //获取控件变量
        register_phone = findViewById(R.id.Register_Phone);
        register_password = findViewById(R.id.Register_Password);
        register_sms = findViewById(R.id.Register_SMS);
        register = findViewById(R.id.Register);
        login_back = findViewById(R.id.Login_Back);
        register_paint = findViewById(R.id.Register_Paint);

        //启动监听机制
        //将验证码用图片的形式显示出来
        register_paint.setImageBitmap(Code.getInstance().createBitmap());
        realCode = Code.getInstance().getCode().toLowerCase();
        register_paint.setOnClickListener(this);
        register.setOnClickListener(this);
        login_back.setOnClickListener(this);
    }

    //获取数据
    public void Getdata(){
        phone = register_phone.getText().toString().trim();
        passeword = register_password.getText().toString().trim();
        sms = register_sms.getText().toString().toLowerCase();
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.Register_Paint:
                register_paint.setImageBitmap(Code.getInstance().createBitmap());
                realCode = Code.getInstance().getCode().toLowerCase();
                Log.v(TAG,"realCode"+realCode);
                break;
            case R.id.Login_Back:
                finish();
                break;
            case R.id.Register:
                Getdata();
                //输入限制条件：不能为空、号码以及密码符合格式、密码数量限制条件：6到16
                if (TextUtils.isEmpty(phone)){
                    Toast.makeText(getApplicationContext(),"请输入手机号码",Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(passeword)) {
                    Toast.makeText(getApplicationContext(),"请输入密码",Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(sms)) {
                    Toast.makeText(getApplicationContext(),"请输入验证码",Toast.LENGTH_SHORT).show();
                } else if (Suit.PhoneSuit(phone.trim()) != true) {
                    Toast.makeText(getApplicationContext(),"请输入正确的手机号码",Toast.LENGTH_SHORT).show();
                } else if (Suit.PasswordSuit(passeword.trim()) != true) {
                    Toast.makeText(getApplicationContext(),"密码最少包含3个字母",Toast.LENGTH_SHORT).show();
                } else if (passeword.length() < 6) {
                    Toast.makeText(getApplicationContext(),"密码不得少于6位数",Toast.LENGTH_SHORT).show();
                } else if (passeword.length() > 16) {
                    Toast.makeText(getApplicationContext(),"密码不得多于16位数",Toast.LENGTH_SHORT).show();
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
                                        register_phone.setText("");
                                        Toast.makeText(getApplicationContext(),"该用户已经注册过",Toast.LENGTH_SHORT).show();
                                        finish();
                                        break;
                                    }
                                    count++;
                                }
                                //查询到尾没有
                                if (count == object.size()){
                                    //判断验证码是否正确
                                    if (sms.equals(realCode)) {
                                        Toast.makeText(getApplicationContext(),  "验证码正确", Toast.LENGTH_SHORT).show();
                                        //将用户信息存入bmob云端
                                        final User user = new User();
                                        user.setUsername(phone);
                                        user.setPassword(passeword);
                                        user.setNickname("爱喝水的小熊宝");
                                        user.setAssignment(0);
                                        user.setSignature("爱喝水的小熊宝");
                                        user.setGender("男");
                                        user.save(new SaveListener<String>() {
                                            @Override
                                            public void done(String s, BmobException e) {
                                                if (e == null){
                                                    Toast.makeText(getApplicationContext(),"注册成功",Toast.LENGTH_SHORT).show();
                                                    finish();
                                                }else {
                                                    Toast.makeText(getApplicationContext(),"注册失败",Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                                    } else {
                                        register_sms.setText("");
                                        Toast.makeText(getApplicationContext(), "验证码错误", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }else {
                                Toast.makeText(Resgister_Activity.this, "该用户不存在", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
                break;
        }
    }
}