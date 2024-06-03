package com.guit.edu.myapplication.activity;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.guit.edu.myapplication.R;

public class About_Activity extends AppCompatActivity {
    private TextView versionTextView;
    private ImageView back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about_activity);

        initView();

        // 获取版本号
        String versionName = getVersionName();
        if (versionName != null) {
            versionTextView.setText("多喝水 " + versionName);
        }



        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }


    private void initView(){
        versionTextView = findViewById(R.id.version);
        back = findViewById(R.id.back);
    }

    // 获取应用程序的版本号
    private String getVersionName() {
        String versionName = "";
        try {
            PackageInfo packageInfo = this.getPackageManager().getPackageInfo(this.getPackageName(), 0);
            versionName = packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionName;
    }

}
