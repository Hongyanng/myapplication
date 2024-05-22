package com.guit.edu.myapplication.fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.guit.edu.myapplication.Adapter.WaterRecordAdapter;
import com.guit.edu.myapplication.R;
import com.guit.edu.myapplication.SPUtils;
import com.guit.edu.myapplication.activity.First_interface_Activity;
import com.guit.edu.myapplication.entity.History;
import com.guit.edu.myapplication.entity.User;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

public class Home_Fragment extends Fragment implements View.OnClickListener {
    private TextView nickname;
    private TextView taskTextView;
    private TextView welcomeTextView;
    private LinearLayout drinkLayout;
    private int drinkValue = 0;
    private int assignmentValue = 0;
    private int cupCapacity;
    private RecyclerView recyclerView;
    private WaterRecordAdapter adapter;
    private List<History> historyList;
    private String currentInput = "";
    private TextView display;
    private TextView timeTextView ;
    private TextView typeTextView;
    private TextView encouragementTextView;
    ImageView cupImage;
    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home_fragment, container, false);

        // 初始化视图
        nickname = view.findViewById(R.id.nickname);
        taskTextView = view.findViewById(R.id.task);
        welcomeTextView = view.findViewById(R.id.welcome);
        drinkLayout = view.findViewById(R.id.drink);
        timeTextView = view.findViewById(R.id.time);
        encouragementTextView = view.findViewById(R.id.encouragement_text);

        //初始化 RecyclerView
        recyclerView = view.findViewById(R.id.conn);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

         //初始化历史记录列表和适配器
        historyList = new ArrayList<>();
        adapter = new WaterRecordAdapter(historyList);
        recyclerView.setAdapter(adapter);


        // 获取当前时间
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);

        // 根据时间设置欢迎语
        String welcomeMessage;
        if (hour >= 7 && hour < 12) {
            welcomeMessage = "早上好";
        } else if (hour >= 12 && hour < 14) {
            welcomeMessage = "中午好";
        } else if (hour >= 14 && hour < 18) {
            welcomeMessage = "下午好";
        } else if (hour >= 18 && hour < 22) {
            welcomeMessage = "晚上好";
        }else {
            welcomeMessage = "晚安";
        }

        welcomeTextView.setText(welcomeMessage);


        drinkLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 点击事件，弹出拨号盘
                showDialerDialog();
            }
        });

        nickname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showInputDialog();
            }
        });

        // 查询用户数据并显示在 UI 上
        queryUserData();
        queryDrinkValue();

        startCountDown(); // 启动倒计时



        return view;
    }

    // 查询用户数据并显示在 UI 上
    private void queryUserData() {
        String currentUsername = SPUtils.get(getContext(), "username", "").toString();
        if (currentUsername.isEmpty()) {
            Toast.makeText(getContext(), "未登录或获取用户信息失败", Toast.LENGTH_SHORT).show();
            return;
        }
        BmobQuery<User> bmobQuery = new BmobQuery<>();
        bmobQuery.addWhereEqualTo("Username", currentUsername);
        bmobQuery.findObjects(new FindListener<User>() {
            @Override
            public void done(List<User> list, BmobException e) {
                if (e == null) {
                    if (!list.isEmpty()) {
                        User user = list.get(0);
                        // 显示用户昵称
                        nickname.setText(user.getNickname());
                        // 获取用户设定的饮水目标值
                        assignmentValue = user.getAssignment();
                        //获取杯子容量
                        cupCapacity = user.getCupcapacity();
                        // 更新任务剩余饮水量
                        updateTaskTextView();
                    }
                } else {
                    // 查询失败时，显示错误信息
                    Toast.makeText(getContext(), "查询数据失败", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }




    // 查询用户今日剩余饮水量
    private void queryDrinkValue() {
        String currentUsername = SPUtils.get(getContext(), "username", "").toString();
        if (currentUsername.isEmpty()) {
            Toast.makeText(getContext(), "未登录或获取用户信息失败", Toast.LENGTH_SHORT).show();
            return;
        }

        BmobQuery<History> query = new BmobQuery<>();
        query.addWhereEqualTo("Username", currentUsername);
        query.addWhereGreaterThanOrEqualTo("createdAt", new BmobDate(getStartOfDay()));
        query.addWhereLessThanOrEqualTo("createdAt", new BmobDate(getEndOfDay()));
        query.findObjects(new FindListener<History>() {
            @Override
            public void done(List<History> object, BmobException e) {
                if (e == null) {
                    // 查询成功
                    if (!object.isEmpty()) {
                        // 计算今日总饮水量
                        int totalDrink = 0;
                        for (History history : object){
                            totalDrink += history.getDrink();
                        }
                        drinkValue = totalDrink;
                        updateTaskTextView();
                        // 查询成功，更新适配器数据集
                        historyList.clear();
                        historyList.addAll(object);
                        adapter.notifyDataSetChanged();
                    } else {
                        // 如果查询结果为空，显示默认值（这里假设默认值为 0）
                        drinkValue = 0;
                        updateTaskTextView();
                    }
                } else {
                    // 查询失败，处理异常
                    Log.e("BmobQuery", "查询失败：" + e.getMessage());
                }
            }
        });
    }

    // 获取日期
    private Date getStartOfDay() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }
    private Date getEndOfDay() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar.getTime();
    }


    // 计算并更新任务剩余饮水量
    private void updateTaskTextView() {
        // 计算剩余饮水量
        int remainingDrink = assignmentValue - drinkValue;

        if (remainingDrink <= 0){
            taskTextView.setText("今天的饮水量已达成！");
        }else {
            taskTextView.setText("剩余"+String.valueOf(remainingDrink)+"ml ");
        }

        int remainingCups = remainingDrink / cupCapacity;


        if (encouragementTextView != null) {
            if (remainingCups <= 0) {
                encouragementTextView.setText("今天的饮水目标已经达成！");
            } else {
                encouragementTextView.setText("加油！还要再喝" + remainingCups + "杯水！");
            }
        }

    }

    // 弹出拨号盘的方法
    private void showDialerDialog() {
        // 重置当前输入
        currentInput = "";

        Dialog dialog = new Dialog(getContext());

        // 设置对话框布局
        Window window = dialog.getWindow();
        if (window != null) {
            window.setContentView(R.layout.activity_dialer);
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            window.setGravity(Gravity.BOTTOM);
            window.setBackgroundDrawableResource(android.R.color.transparent);
        }

        // 获取 display 的 TextView 对象
        display = dialog.findViewById(R.id.display);

        ImageView close = dialog.findViewById(R.id.close_button);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss(); // 关闭拨号盘
            }
        });

        // 设置拨号按钮的点击事件
        setDialerButtonClickListeners(dialog);

        dialog.show();
    }



    // 设置拨号按钮的点击事件
    private void setDialerButtonClickListeners(Dialog dialog) {
        // 查找拨号盘布局中的按钮
        Button num1 = dialog.findViewById(R.id.num_1);
        Button num2 = dialog.findViewById(R.id.num_2);
        Button num3 = dialog.findViewById(R.id.num_3);
        Button num4 = dialog.findViewById(R.id.num_4);
        Button num5 = dialog.findViewById(R.id.num_5);
        Button num6 = dialog.findViewById(R.id.num_6);
        Button num7 = dialog.findViewById(R.id.num_7);
        Button num8 = dialog.findViewById(R.id.num_8);
        Button num9 = dialog.findViewById(R.id.num_9);
        Button num0 = dialog.findViewById(R.id.num_0);
        Button num00 = dialog.findViewById(R.id.num_00);
        Button delete = dialog.findViewById(R.id.delete);
        Button recordButton = dialog.findViewById(R.id.record_button);
        LinearLayout type_layout = dialog.findViewById(R.id.type_layout);
        typeTextView = dialog.findViewById(R.id.type);


        // 设置点击事件监听器
        num1.setOnClickListener(this);
        num2.setOnClickListener(this);
        num3.setOnClickListener(this);
        num4.setOnClickListener(this);
        num5.setOnClickListener(this);
        num6.setOnClickListener(this);
        num7.setOnClickListener(this);
        num8.setOnClickListener(this);
        num9.setOnClickListener(this);
        num0.setOnClickListener(this);
        num00.setOnClickListener(this);
        delete.setOnClickListener(this);
        type_layout.setOnClickListener(this);


        recordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 执行饮水记录插入操作
                insertWaterRecord();
                // 关闭拨号盘
                dialog.dismiss();
            }
        });

        type_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDrinkTypeDialog();
            }
        });
    }

    private void showDrinkTypeDialog() {
        Dialog drinkTypeDialog  = new Dialog(getContext());

        // 设置对话框布局
        Window window = drinkTypeDialog.getWindow();
        if (window != null) {
            window.setContentView(R.layout.activity_type);
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            window.setGravity(Gravity.BOTTOM);
            window.setBackgroundDrawableResource(android.R.color.transparent);
        }

        // 初始化对话框内的按钮，并设置点击事件
        setupDrinkTypeDialogButtons(drinkTypeDialog);
        drinkTypeDialog.show();
    }

    private void setupDrinkTypeDialogButtons(Dialog drinkTypeDialog){
        Button water = drinkTypeDialog.findViewById(R.id.water);
        Button tea = drinkTypeDialog.findViewById(R.id.tea);
        Button soup = drinkTypeDialog.findViewById(R.id.soup);
        Button milk = drinkTypeDialog.findViewById(R.id.milk);
        Button coffee = drinkTypeDialog.findViewById(R.id.coffee);
        Button juice = drinkTypeDialog.findViewById(R.id.juice);
        Button beer = drinkTypeDialog.findViewById(R.id.beer);
        Button yoghurt = drinkTypeDialog.findViewById(R.id.yoghurt);
        Button milktea = drinkTypeDialog.findViewById(R.id.milktea);
        Button Sportsdrinks = drinkTypeDialog.findViewById(R.id.Sportsdrinks);
        Button Carbonateddrinks = drinkTypeDialog.findViewById(R.id.Carbonateddrinks);
        Button other = drinkTypeDialog.findViewById(R.id.other);

        View.OnClickListener onClickListener = view ->  {
                String selectedType  = ((Button) view).getText().toString();;
                updateDrinkType(selectedType); // 更新饮品类型显示
                drinkTypeDialog.dismiss(); // 关闭对话框
        };
        water.setOnClickListener(onClickListener);
        tea.setOnClickListener(onClickListener);
        soup.setOnClickListener(onClickListener);
        milk.setOnClickListener(onClickListener);
        coffee.setOnClickListener(onClickListener);
        juice.setOnClickListener(onClickListener);
        beer.setOnClickListener(onClickListener);
        yoghurt.setOnClickListener(onClickListener);
        milktea.setOnClickListener(onClickListener);
        Sportsdrinks.setOnClickListener(onClickListener);
        Carbonateddrinks.setOnClickListener(onClickListener);
        other.setOnClickListener(onClickListener);

    }


    private void updateDrinkType(String drinkType) {
        typeTextView.setText(drinkType);
    }



    @Override
    public void onClick(View v) {
        Button button = (Button) v;
        String buttonText = button.getText().toString();

        switch (v.getId()) {
            case R.id.num_1:
            case R.id.num_2:
            case R.id.num_3:
            case R.id.num_4:
            case R.id.num_5:
            case R.id.num_6:
            case R.id.num_7:
            case R.id.num_8:
            case R.id.num_9:
            case R.id.num_0:
            case R.id.num_00:
                // 拨号按钮点击事件
                // 添加拨号按钮的数字到当前输入
                currentInput += buttonText;
                updateDisplay(currentInput); // 更新显示
                break;
            case R.id.delete:
                // 删除按钮点击事件
                // 删除最后一个数字
                if (!currentInput.isEmpty()) {
                    currentInput = currentInput.substring(0, currentInput.length() - 1);
                    updateDisplay(currentInput); // 更新显示
                }
                break;
        }
    }

    // 更新显示内容到 display TextView
    private void updateDisplay(String input) {
        if (display != null) {
            display.setText(input);
        }
    }



    // 插入饮水记录到数据库
    private void insertWaterRecord() {
        // 获取当前登录用户的用户名
        String currentUsername = (String) SPUtils.get(getContext(), "username", "");
        String drinkType = typeTextView.getText().toString(); // 获取饮品种类

        if (!currentUsername.isEmpty() && !currentInput.isEmpty()) {
            // 在这里执行将饮水记录插入到 Bmob 后端云的操作
            History history = new History();
            history.setUsername(currentUsername); // 设置当前用户的用户名
            history.setDrink(Integer.parseInt(currentInput)); // 设置饮水量，需转换为整数
            history.setType(drinkType);  // 设置饮品种类
            history.save(new SaveListener<String>() {
                @Override
                public void done(String objectId, BmobException e) {
                    if (e == null) {
                        showToast("饮水记录插入成功！");
                        queryDrinkValue();
                    } else {
                        showToast("饮水记录插入失败：" + e.getMessage());
                    }
                }
            });
        } else {
            showToast("无法获取当前登录用户信息或饮水量为空！");
        }
    }

    // 显示 Toast
    private void showToast(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }


    private void startCountDown() {
        Calendar calendar = Calendar.getInstance();
        long currentTimeMillis = calendar.getTimeInMillis();
        long timeToNextHour = 3600000 - (currentTimeMillis % 3600000);

        new CountDownTimer(timeToNextHour, 1000) {
            public void onTick(long millisUntilFinished) {
                long seconds = (millisUntilFinished / 1000) % 60;
                long minutes = (millisUntilFinished / (1000 * 60)) % 60;
                long hours = (millisUntilFinished / (1000 * 60 * 60)) % 24;

                // 格式化显示时间
                String timeFormat = String.format("%02d:%02d:%02d", hours, minutes, seconds);
                timeTextView.setText("下次提醒:"+timeFormat);
            }

            public void onFinish() {
                // 当倒计时结束时，重置倒计时
                startCountDown(); // 递归调用以重新启动计时器
            }
        }.start();
    }

    // 弹出对话框或者输入框让用户输入新的昵称
    private void showInputDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("请输入新的昵称");

        // 创建一个 EditText 用于用户输入
        final EditText input = new EditText(getContext());
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        // 确认按钮
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String newNickname = input.getText().toString();
                updateNickname(newNickname); // 更新昵称到数据库
            }
        });

        // 取消按钮
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    // 更新昵称到 Bmob 数据库中
    private void updateNickname(final String newNickname) {
        String currentUsername = SPUtils.get(getContext(), "username", "").toString();
        if (currentUsername.isEmpty()) {
            Toast.makeText(getContext(), "未登录或获取用户信息失败", Toast.LENGTH_SHORT).show();
            return;
        }
        BmobQuery<User> query = new BmobQuery<>();
        query.addWhereEqualTo("Username", currentUsername);
        query.findObjects(new FindListener<User>() {
            @Override
            public void done(List<User> userList, BmobException e) {
                if (e == null && userList != null && userList.size() > 0) {
                    // 获取当前用户对象
                    User currentUser = userList.get(0);
                    // 更新昵称字段
                    currentUser.setNickname(newNickname);
                    // 保存更新后的用户信息到数据库
                    currentUser.update(new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            if (e == null) {
                                // 更新成功，更新昵称 TextView 的文本显示
                                nickname.setText(newNickname);
                                Toast.makeText(getContext(), "昵称更新成功", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getContext(), "昵称更新失败：" + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    Toast.makeText(getContext(), "查询当前用户信息失败：" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


}