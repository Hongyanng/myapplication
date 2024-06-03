package com.guit.edu.myapplication.fragment;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.guit.edu.myapplication.activity.About_Activity;
import com.guit.edu.myapplication.activity.FindPassword_Activity;
import com.guit.edu.myapplication.activity.Main_Activity;
import com.guit.edu.myapplication.R;
import com.guit.edu.myapplication.SPUtils;
import com.guit.edu.myapplication.activity.Water_Info_Activity;
import com.guit.edu.myapplication.entity.History;
import com.guit.edu.myapplication.entity.User;
import com.makeramen.roundedimageview.RoundedImageView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import ando.widget.pickerview.builder.OptionsPickerBuilder;
import ando.widget.pickerview.listener.OnOptionsSelectListener;
import ando.widget.pickerview.view.OptionsPickerView;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

public class Wo_Fragment extends Fragment implements View.OnClickListener{
    private TextView nickname,signature,gender,height,weight,cupcapacity,assignment,versionTextView,achievementTextView;
    private LinearLayout genderLayout,weightLayout,heightLayout,cupCapacityLayout;
    private RoundedImageView touxiangImageView;
    private RelativeLayout exit_layout,assignment_layout,lose_layout,water_info_layout,about_layout;
    private List<String> genderItems = new ArrayList<>();
    private List<String> heightItems = new ArrayList<>();
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri imageUri;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.wo_fragment, container, false);

        // 初始化视图
        nickname = view.findViewById(R.id.nickname);
        signature = view.findViewById(R.id.signature);
        gender = view.findViewById(R.id.gender);
        weight = view.findViewById(R.id.weight);
        cupcapacity = view.findViewById(R.id.cupcapacity);
        genderLayout = view.findViewById(R.id.gender_layout);
        weightLayout = view.findViewById(R.id.weight_layout);
        cupCapacityLayout = view.findViewById(R.id.cupcapacity_layout);
        touxiangImageView = view.findViewById(R.id.touxiang);
        exit_layout = view.findViewById(R.id.exit_layout);
        versionTextView = view.findViewById(R.id.version);
        lose_layout = view.findViewById(R.id.lose_layout);
        assignment = view.findViewById(R.id.assignment_txt);
        assignment_layout = view.findViewById(R.id.assignment_layout);
        water_info_layout = view.findViewById(R.id.water_info_layout);
        achievementTextView = view.findViewById(R.id.achievement);
        heightLayout = view.findViewById(R.id.height_layout);
        height = view.findViewById(R.id.height);
        about_layout = view.findViewById(R.id.about_layout);

        // 初始化性别列表
        genderItems.add("男");
        genderItems.add("女");

        // 刷新数据
        queryUserData();
        //查询连续打卡次数
        updateContinuousDays();

        // 获取版本号
        String versionName = getVersionName();
        if (versionName != null) {
            versionTextView.setText(versionName);
        }

        //启动点击事件监听
        lose_layout.setOnClickListener(this);
        nickname.setOnClickListener(this);
        signature.setOnClickListener(this);
        genderLayout.setOnClickListener(this);
        heightLayout.setOnClickListener(this);
        weightLayout.setOnClickListener(this);
        cupCapacityLayout.setOnClickListener(this);
        touxiangImageView.setOnClickListener(this);
        water_info_layout.setOnClickListener(this);
        about_layout.setOnClickListener(this);
        assignment_layout.setOnClickListener(this);
        exit_layout.setOnClickListener(this);


        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.lose_layout:
                findPassword();
                break;
            case R.id.nickname:
                showInputDialog();
                break;
            case R.id.signature:
                showSignatureDialog();
                break;
            case R.id.gender_layout:
                showGenderPicker();
                break;
            case R.id.height_layout:
                showHeightPicker();
                break;
            case R.id.weight_layout:
                showWeightPicker();
                break;
            case R.id.cupcapacity_layout:
                showCupCapacityPicker();
                break;
            case R.id.touxiang:
                openGallery();
                break;
            case R.id.water_info_layout:
                showWaterInfo();
                break;
            case R.id.about_layout:
                showAboutActivity();
                break;
            case R.id.assignment_layout:
                showAssignmentPicker();
                break;
            case R.id.exit_layout:
                ExitUser();
                break;
        }
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageUri);
                touxiangImageView.setImageBitmap(bitmap);
                uploadImage(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    private void showAboutActivity(){
        Intent intent = new Intent(getContext(), About_Activity.class);
        startActivity(intent);
    }

    private void uploadImage(Bitmap bitmap) {
        String currentUsername = SPUtils.get(getContext(), "username", "").toString();
        if (currentUsername.isEmpty()) {
            Toast.makeText(getContext(), "未登录或获取用户信息失败", Toast.LENGTH_SHORT).show();
            return;
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] imageData = baos.toByteArray();
        final User currentUser = new User();
        currentUser.setTouxiang(imageData); // 设置用户头像为字节数组

        // 更新用户头像到 Bmob 数据库
        currentUser.update(new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    Toast.makeText(getContext(), "头像上传成功", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "头像上传失败：" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void updateContinuousDays() {
        String currentUsername = SPUtils.get(getContext(), "username", "").toString();
        if (currentUsername.isEmpty()) {
            Toast.makeText(getContext(), "未登录或获取用户信息失败", Toast.LENGTH_SHORT).show();
            return;
        }

        BmobQuery<History> query = new BmobQuery<>();
        query.addWhereEqualTo("Username", currentUsername);
        query.order("-createdAt");  // 按创建时间降序排列
        query.findObjects(new FindListener<History>() {
            @Override
            public void done(List<History> histories, BmobException e) {
                if (e == null) {
                    int days = 0;
                    if (!histories.isEmpty()) {
                        days = countContinuousDays(histories);
                    }
                    TextView achievementTextView = getView().findViewById(R.id.achievement);
                    if (achievementTextView != null) {
                        if (days > 0) {
                            achievementTextView.setText("已连续打卡 " + days + " 天");
                        } else {
                            achievementTextView.setText("未打卡");
                        }
                    } else {
                        Log.e(TAG, "Achievement TextView is null");
                    }
                } else {
                    Toast.makeText(getContext(), "查询打卡记录失败：" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private int countContinuousDays(List<History> histories) {
        if (histories == null || histories.isEmpty()) {
            return 0;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();
        int count = 0;  // 初始化连续天数计数
        Date lastDate = null;

        for (History history : histories) {
            Date currentDate;
            try {
                currentDate = sdf.parse(history.getCreatedAt());
            } catch (ParseException e) {
                e.printStackTrace();
                System.out.println("字符转换为Date类型时失败");
                continue;  // 如果解析失败，跳过这条记录
            }

            if (lastDate != null) {
                cal.setTime(lastDate);
                cal.add(Calendar.DATE, -1);
                Date expectedDate = cal.getTime();
                // 检查当前日期是否是上一个日期的前一天
                if (!isSameDay(expectedDate, currentDate)) {
                    // 如果不是连续的，继续检查下一个日期
                    continue;
                }
            }

            count++;  // 增加连续天数
            lastDate = currentDate;  // 更新上一次的日期
        }

        // 如果没有连续记录，则重新计数
        if (count == 0) {
            count = 1;
        }

        return count;
    }

    private boolean isSameDay(Date date1, Date date2) {
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(date1);
        cal2.setTime(date2);
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
    }





    private void findPassword(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("即将跳转到修改密码界面");
        builder.setMessage("确定跳转吗？");

        // 添加确认按钮
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // 跳转到修改密码界面
                Intent intent = new Intent(getContext(), FindPassword_Activity.class);
                startActivity(intent);
                // 结束当前 Fragment
                getActivity().finish();
            }
        });

        // 添加取消按钮
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // 用户取消退出登录，不做任何操作
            }
        });

        // 创建并显示对话框
        AlertDialog dialog = builder.create();
        dialog.show();
    }


    // 获取应用程序的版本号
    private String getVersionName() {
        String versionName = "";
        try {
            PackageInfo packageInfo = getContext().getPackageManager().getPackageInfo(getContext().getPackageName(), 0);
            versionName = packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionName;
    }

    // 查询用户数据并显示在 UI 上
    private void queryUserData(){
        String currentUsername = SPUtils.get(getContext(), "username", "").toString();
        if (currentUsername.isEmpty()) {
            Toast.makeText(getContext(), "未登录或获取用户信息失败", Toast.LENGTH_SHORT).show();
            return;
        }
        BmobQuery<User> query = new BmobQuery<>();
        query.addWhereEqualTo("Username", currentUsername);
        query.findObjects(new FindListener<User>() {
            @Override
            public void done(List<User> list, BmobException e) {
                if (e == null) {
                    if (!list.isEmpty()) {
                        User user = list.get(0);
                        nickname.setText(user.getNickname());
                        signature.setText(user.getSignature());
                        assignment.setText(user.getAssignment() + "ml");
                        gender.setText(user.getGender());
                        height.setText(user.getHeight() + "cm");
                        weight.setText(user.getWeight() + "kg");
                        cupcapacity.setText(user.getCupcapacity() + "ml");
                    }
                } else {
                    Toast.makeText(getContext(), "查询数据失败", Toast.LENGTH_SHORT).show();
                }
            }
        });
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

    // 弹出对话框或者输入框让用户输入新的个性签名
    private void showSignatureDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("请输入新的个性签名");

        // 创建一个 EditText 用于用户输入
        final EditText input = new EditText(getContext());
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        // 确认按钮
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String newSignature = input.getText().toString();
                updateSignature(newSignature); // 更新个性签名到数据库
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

    // 更新个性签名到 Bmob 数据库中
    private void updateSignature(final String newSignature) {
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
                    // 更新个性签名字段
                    currentUser.setSignature(newSignature);
                    // 保存更新后的用户信息到数据库
                    currentUser.update(new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            if (e == null) {
                                // 更新成功，更新个性签名 TextView 的文本显示
                                signature.setText(newSignature);
                                Toast.makeText(getContext(), "个性签名更新成功", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getContext(), "个性签名更新失败：" + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    Toast.makeText(getContext(), "查询当前用户信息失败：" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // 弹出身高选择器
    private void showHeightPicker() {
        // 从 height TextView 中获取当前的身高值
        String heightStr = height.getText().toString();
        // 解析身高值为整数
        int currentHeight = Integer.parseInt(heightStr.replaceAll("[^0-9]", ""));

        // 创建身高选择器
        OptionsPickerView<Integer> pickerView = new OptionsPickerBuilder(getContext(), new OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {
                // 获取选中的身高
                int selectedHeight = options1 * 5 + 100; // 范围从100到300，每5厘米一个选项
                // 更新身高到数据库
                updateHeight(selectedHeight);
            }
        })
                .setTitleText("选择身高")
                .setContentTextSize(20)
                .setDividerColor(Color.BLACK)
                .setSelectOptions((currentHeight - 100) / 5) // 设置默认选中项为当前身高
                .setBgColor(Color.WHITE)
                .setTitleBgColor(Color.WHITE)
                .setTitleColor(Color.BLACK)
                .setCancelColor(Color.BLACK)
                .setSubmitColor(Color.BLACK)
                .setTextColorCenter(Color.BLACK)
                .build();

        // 设置身高数据，范围从100到300，每5厘米一个选项
        List<Integer> heightItems = new ArrayList<>();
        for (int i = 100; i <= 300; i += 5) {
            heightItems.add(i);
        }
        pickerView.setPicker(heightItems);

        // 显示身高选择器
        pickerView.show();
    }

    // 更新身高到 Bmob 数据库中
    private void updateHeight(final int newHeight) {
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
                    // 更新身高字段
                    currentUser.setHeight(newHeight);
                    // 保存更新后的用户信息到数据库
                    currentUser.update(new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            if (e == null) {
                                // 更新成功，更新身高 TextView 的文本显示
                                height.setText(newHeight + "cm");
                                Toast.makeText(getContext(), "身高更新成功", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getContext(), "身高更新失败：" + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    Toast.makeText(getContext(), "查询当前用户信息失败：" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }




    // 弹出性别选择器
    private void showGenderPicker() {
        // 创建性别选择器
        OptionsPickerView<String> pickerView = new OptionsPickerBuilder(getContext(), new OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {
                // 获取选中的性别
                String selectedGender = genderItems.get(options1);
                // 更新性别到数据库
                updateGender(selectedGender);
            }
        })
                .setTitleText("选择性别")
                .setContentTextSize(20)
                .setDividerColor(Color.BLACK)
                .setSelectOptions(0)
                .setBgColor(Color.WHITE)
                .setTitleBgColor(Color.WHITE)
                .setTitleColor(Color.BLACK)
                .setCancelColor(Color.BLACK)
                .setSubmitColor(Color.BLACK)
                .setTextColorCenter(Color.BLACK)
                .build();

        // 设置性别数据
        pickerView.setPicker(genderItems);

        // 显示性别选择器
        pickerView.show();
    }

    // 更新性别到 Bmob 数据库中
    private void updateGender(final String newGender) {
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
                    // 更新性别字段
                    currentUser.setGender(newGender);
                    // 保存更新后的用户信息到数据库
                    currentUser.update(new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            if (e == null) {
                                // 更新成功，更新性别 TextView 的文本显示
                                gender.setText(newGender);
                                Toast.makeText(getContext(), "性别更新成功", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getContext(), "性别更新失败：" + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    Toast.makeText(getContext(), "查询当前用户信息失败：" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // 弹出体重选择器
    private void showWeightPicker() {
        // 从 weight TextView 中获取当前的体重值
        String weightStr = weight.getText().toString();
        // 解析体重值为整数
        int currentWeight = Integer.parseInt(weightStr.replaceAll("[^0-9]", ""));

        // 创建体重选择器
        OptionsPickerView<Integer> pickerView = new OptionsPickerBuilder(getContext(), new OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {
                // 获取选中的体重
                int selectedWeight = options1 + 1; // 由于 options1 从0开始，所以需要加1
                // 更新体重到数据库
                updateWeight(selectedWeight);
            }
        })
                .setTitleText("选择体重")
                .setContentTextSize(20)
                .setDividerColor(Color.BLACK)
                .setSelectOptions(currentWeight - 1) // 设置默认选中项为当前体重
                .setBgColor(Color.WHITE)
                .setTitleBgColor(Color.WHITE)
                .setTitleColor(Color.BLACK)
                .setCancelColor(Color.BLACK)
                .setSubmitColor(Color.BLACK)
                .setTextColorCenter(Color.BLACK)
                .build();

        // 设置体重数据，从1kg到200kg
        List<Integer> weightItems = new ArrayList<>();
        for (int i = 1; i <= 200; i++) {
            weightItems.add(i);
        }
        pickerView.setPicker(weightItems);

        // 显示体重选择器
        pickerView.show();
    }

    // 更新体重到 Bmob 数据库中
    private void updateWeight(final int newWeight) {
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
                    // 更新体重字段
                    currentUser.setWeight(newWeight);
                    int recommendedWaterIntake = newWeight * 35;
                    currentUser.setAssignment(recommendedWaterIntake);
                    currentUser.update(new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            if (e == null) {
                                weight.setText(newWeight + "kg");
                                queryUserData();
                                Toast.makeText(getContext(), "体重更新成功", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getContext(), "体重更新失败：" + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    Toast.makeText(getContext(), "查询当前用户信息失败：" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // 弹出杯容量选择器
    private void showCupCapacityPicker() {
        // 从 cupcapacity TextView 中获取当前的杯容量值
        String capacityStr = cupcapacity.getText().toString();
        // 解析杯容量值为整数
        int currentCapacity = Integer.parseInt(capacityStr.replaceAll("[^0-9]", ""));

        // 创建杯容量选择器
        OptionsPickerView<Integer> pickerView = new OptionsPickerBuilder(getContext(), new OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {
                // 获取选中的杯容量
                int selectedCapacity = (options1 + 2) * 50; // 由于 options1 从0开始，所以需要加2，并乘以50
                // 更新杯容量到数据库
                updateCupCapacity(selectedCapacity);
            }
        })
                .setTitleText("选择杯容量")
                .setContentTextSize(20)
                .setDividerColor(Color.BLACK)
                .setSelectOptions(currentCapacity / 50 - 2) // 设置默认选中项为当前杯容量
                .setBgColor(Color.WHITE)
                .setTitleBgColor(Color.WHITE)
                .setTitleColor(Color.BLACK)
                .setCancelColor(Color.BLACK)
                .setSubmitColor(Color.BLACK)
                .setTextColorCenter(Color.BLACK)
                .build();

        // 设置杯容量数据，从100ml到5000ml，每隔50ml为一个选项
        List<Integer> capacityItems = new ArrayList<>();
        for (int i = 100; i <= 5000; i += 50) {
            capacityItems.add(i);
        }
        pickerView.setPicker(capacityItems);

        // 显示杯容量选择器
        pickerView.show();
    }

    // 更新杯容量到 Bmob 数据库中
    private void updateCupCapacity(final int newCapacity) {
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
                    // 更新杯容量字段
                    currentUser.setCupcapacity(newCapacity);
                    // 保存更新后的用户信息到数据库
                    currentUser.update(new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            if (e == null) {
                                // 更新成功，更新杯容量 TextView 的文本显示
                                cupcapacity.setText(newCapacity + "ml");
                                Toast.makeText(getContext(), "杯子容量更新成功", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getContext(), "杯子容量更新失败：" + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    Toast.makeText(getContext(), "查询当前用户信息失败：" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // 弹出每日目标选择器
    private void showAssignmentPicker() {
        // 从 assignment TextView 中获取当前的每日目标
        String assignmentStr = assignment.getText().toString();
        // 解析每日目标为整数
        int currentAssignment = Integer.parseInt(assignmentStr.replaceAll("[^0-9]", ""));

        // 创建每日目标选择器
        OptionsPickerView<Integer> pickerView = new OptionsPickerBuilder(getContext(), new OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {
                // 获取选中的每日目标
                int selectedAssignment = (options1 + 2) * 50; // 由于 options1 从0开始，所以需要加2，并乘以50
                // 更新每日目标到数据库
                updateAssignment(selectedAssignment);
            }
        })
                .setTitleText("设置每日目标")
                .setContentTextSize(20)
                .setDividerColor(Color.BLACK)
                .setSelectOptions(currentAssignment / 50 - 2) // 设置默认选中项为当前杯容量
                .setBgColor(Color.WHITE)
                .setTitleBgColor(Color.WHITE)
                .setTitleColor(Color.BLACK)
                .setCancelColor(Color.BLACK)
                .setSubmitColor(Color.BLACK)
                .setTextColorCenter(Color.BLACK)
                .build();

        // 设置数据，从100ml到5000ml，每隔50ml为一个选项
        List<Integer> capacityItems = new ArrayList<>();
        for (int i = 100; i <= 5000; i += 50) {
            capacityItems.add(i);
        }
        pickerView.setPicker(capacityItems);

        // 显示选择器
        pickerView.show();
    }

    // 更新每日目标到 Bmob 数据库中
    private void updateAssignment(final int newAssignment) {
        String currentUsername = SPUtils.get(getContext(), "username", "").toString();
        if (currentUsername.isEmpty()) {
            Toast.makeText(getContext(), "未登录或获取用户信息失败", Toast.LENGTH_SHORT).show();
            return;
        }
        // 查询当前用户的数据以确保数据最新
        BmobQuery<User> query = new BmobQuery<>();
        query.addWhereEqualTo("Username", currentUsername);
        query.findObjects(new FindListener<User>() {
            @Override
            public void done(List<User> userList, BmobException e) {
                if (e == null && userList != null && userList.size() > 0) {
                    // 获取当前用户对象
                    User currentUser = userList.get(0);
                    // 更新杯容量字段
                    currentUser.setAssignment(newAssignment);
                    // 保存更新后的用户信息到数据库
                    currentUser.update(new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            if (e == null) {
                                // 更新成功，更新杯容量 TextView 的文本显示
                                assignment.setText(newAssignment + "ml");
                                Toast.makeText(getContext(), "每日目标更新成功", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getContext(), "每日目标更新失败：" + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    Toast.makeText(getContext(), "查询当前用户信息失败：" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void showWaterInfo(){
        Intent intent = new Intent(getContext(), Water_Info_Activity.class);
        startActivity(intent);
    }






    // 退出登录功能
    private void ExitUser() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("确认退出登录");
        builder.setMessage("确定要退出登录吗？");

        // 添加确认按钮
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // 清除保存的用户信息
                SPUtils.clear(getContext());
                // 跳转到登录界面
                Intent intent = new Intent(getContext(), Main_Activity.class);
                startActivity(intent);
                // 结束当前 Fragment
                 getActivity().finish();
            }
        });

        // 添加取消按钮
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // 用户取消退出登录，不做任何操作
            }
        });

        // 创建并显示对话框
        AlertDialog dialog = builder.create();
        dialog.show();
    }

}
