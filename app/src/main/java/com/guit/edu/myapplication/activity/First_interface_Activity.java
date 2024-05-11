package com.guit.edu.myapplication.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.guit.edu.myapplication.R;
import com.guit.edu.myapplication.SPUtils;
import com.guit.edu.myapplication.entity.History;
import com.guit.edu.myapplication.fragment.History_Fragment;
import com.guit.edu.myapplication.fragment.Home_Fragment;
import com.guit.edu.myapplication.fragment.Wo_Fragment;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class First_interface_Activity extends AppCompatActivity implements View.OnClickListener {
    LinearLayout history_layout,home_layout,wo_layout;

    History_Fragment history_fragment = new History_Fragment();
    Home_Fragment home_fragment = new Home_Fragment();
    Wo_Fragment wo_fragment = new Wo_Fragment();

    List<Fragment> fragments = new ArrayList<>();

    int currPosition = 1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_interface);
        fragments.add(history_fragment);
        fragments.add(home_fragment);
        fragments.add(wo_fragment);

        history_layout = findViewById(R.id.history_layout);
        home_layout = findViewById(R.id.home_layout);
        wo_layout = findViewById(R.id.wo_layout);

        history_layout.setOnClickListener(this);
        home_layout.setOnClickListener(this);
        wo_layout.setOnClickListener(this);

        changeFragment(currPosition);

    }


    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.history_layout){
            currPosition = 0;
        } else if (id == R.id.home_layout) {
            currPosition = 1;
        }else if (id == R.id.wo_layout){
            currPosition = 2;
        }
        changeFragment(currPosition);
    }

    public void changeFragment(int Position){
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.conner,fragments.get(Position));
        ft.commit();
    }
}
