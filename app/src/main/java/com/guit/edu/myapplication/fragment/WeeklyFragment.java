package com.guit.edu.myapplication.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.guit.edu.myapplication.R;
import com.guit.edu.myapplication.SPUtils;
import com.guit.edu.myapplication.entity.History;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class WeeklyFragment extends HistoryDataFragment {
    @Override
    protected void loadDrinkData() {
        String currentUsername = SPUtils.get(getContext(), "username", "").toString();
        if (currentUsername.isEmpty()) {
            Toast.makeText(getContext(), "未登录或获取用户信息失败", Toast.LENGTH_SHORT).show();
            return;
        }


        // 打印开始时间
        Log.d("WeeklyFragment", "开始时间：" + getStartOfWeek().toString());
        // 打印结束时间
        Log.d("WeeklyFragment", "结束时间：" + getEndOfWeek().toString());
        BmobQuery<History> query = new BmobQuery<>();
        query.addWhereEqualTo("Username", currentUsername);
        query.addWhereGreaterThanOrEqualTo("createdAt", new BmobDate(getStartOfWeek()));
        query.addWhereLessThanOrEqualTo("createdAt", new BmobDate(getEndOfWeek()));
        query.findObjects(new FindListener<History>() {
            @Override
            public void done(List<History> histories, BmobException e) {
                if (e == null) {
                    setupBarChart(histories);
                    setupPieChart(histories);
                    setupLineChart(histories);
                } else {
                    Toast.makeText(getContext(), "查询失败:" + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    protected Date getStartOfWeek() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        calendar.add(Calendar.WEEK_OF_YEAR, -1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        return calendar.getTime();
    }

    protected Date getEndOfWeek() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
//        calendar.add(Calendar.WEEK_OF_YEAR, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        return calendar.getTime();
    }

    protected void setupBarChart(List<History> histories) {
        // 处理数据并设置柱状图

        // 存储每种饮品类型（String）和相应的饮用量总和（Integer）
        Map<String, Integer> typeToVolume = new HashMap<>();
        // 用于表示图表中的一个条目（在这里是柱状图的某一根柱子）
        List<BarEntry> entries = new ArrayList<>();
        // 存储每个数据点的标签，即饮品的类型名称
        List<String> labels = new ArrayList<>();

        int index = 0;
        if (histories != null && !histories.isEmpty()) {
            for (History history : histories) {
                String type = history.getType();
                int currentVolume = typeToVolume.getOrDefault(type, 0);
                typeToVolume.put(type, currentVolume + history.getDrink());
            }

            for (Map.Entry<String, Integer> entry : typeToVolume.entrySet()) {
                entries.add(new BarEntry(index, entry.getValue()));
                labels.add(entry.getKey());
                index++;
            }

            // 绘制柱状图
            BarDataSet dataSet = new BarDataSet(entries, "");
            dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
            BarData data = new BarData(dataSet);
            chart.setData(data);
            chart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labels));
            chart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
            chart.getXAxis().setDrawGridLines(false);
            chart.getXAxis().setGranularity(1f);
            chart.getXAxis().setGranularityEnabled(true);
            chart.getDescription().setEnabled(false);
            // 获取右边的 Y 轴对象并隐藏
            YAxis rightYAxis = chart.getAxisRight();
            rightYAxis.setEnabled(false);
            chart.animateY(1000);
            chart.invalidate(); // 刷新图表

            // 获取图表的Legend对象
            Legend legend = chart.getLegend();

            // 设置图例的位置为右上角
            legend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
            legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
            legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);

            // 设置图例的形状、大小和颜色等
            legend.setForm(Legend.LegendForm.SQUARE); // 设置图例的形状为方形
            legend.setFormSize(10f); // 设置图例图形的大小
            legend.setTextSize(12f); // 设置图例文字的大小

            // 设置图例的样式和间距等
            legend.setXEntrySpace(5f); // 设置图例项之间的X轴间距
            legend.setYEntrySpace(5f); // 设置图例项之间的Y轴间距

            // 刷新图表
            chart.invalidate(); // 重新绘制图表以应用更改

        } else {
            Toast.makeText(getContext(), "暂无数据", Toast.LENGTH_LONG).show();
        }


    }



    protected void setupLineChart(List<History> histories) {
        if (histories != null && !histories.isEmpty()) {
            // 将历史记录按日期进行分组并计算每天的饮用量总和
            Map<String, Integer> dateToTotalDrink = new HashMap<>();
            for (History history : histories) {
                String date = history.getCreatedAt().split(" ")[0]; // 提取日期部分
                int drink = history.getDrink();
                int currentTotalDrink = dateToTotalDrink.getOrDefault(date, 0);
                dateToTotalDrink.put(date, currentTotalDrink + drink);
            }

            // 生成折线图的数据集
            List<Entry> entries = new ArrayList<>();
            int index = 0;
            for (Map.Entry<String, Integer> entry : dateToTotalDrink.entrySet()) {
                entries.add(new Entry(index, entry.getValue()));
                index++;
            }

            // 创建折线图的数据集
            LineDataSet dataSet = new LineDataSet(entries, "一周内的饮用量趋势");
            dataSet.setColor(Color.GREEN);
            dataSet.setCircleColor(Color.BLACK);
            dataSet.setLineWidth(2f);
            dataSet.setCircleRadius(4f);

            // 设置折线图的其他属性
            LineData lineData = new LineData(dataSet);
            lineChart.setData(lineData);
            lineChart.getDescription().setEnabled(false);

            // 配置 X 轴
            XAxis xAxis = lineChart.getXAxis();
            xAxis.setValueFormatter(new ValueFormatter() {
                @Override
                public String getFormattedValue(float value) {
                    int intValue = (int) value;
                    if (intValue >= 0 && intValue < dateToTotalDrink.size()) {
                        // 返回每天的日期
                        return formatDate(dateToTotalDrink.keySet().toArray(new String[0])[intValue]);
                    }
                    return "";
                }
            });
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            xAxis.setDrawGridLines(false);
            xAxis.setGranularity(1f);
            xAxis.setGranularityEnabled(true);

            // 配置 Y 轴
            YAxis yAxis = lineChart.getAxisLeft();
            yAxis.setValueFormatter(new ValueFormatter() {
                @Override
                public String getFormattedValue(float value) {
                    return String.valueOf((int) value);
                }
            });

            // 获取右边的 Y 轴对象并隐藏
            YAxis rightYAxis = lineChart.getAxisRight();
            rightYAxis.setEnabled(false);

            lineChart.animateX(2500);

            // 获取图例对象
            Legend legend = lineChart.getLegend();
            // 设置图例的位置为右上角
            legend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
            legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
            legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
            // 可以设置图例的形状、大小和颜色等
            legend.setForm(Legend.LegendForm.SQUARE); // 设置图例的形状为方形
            legend.setFormSize(10f); // 设置图例图形的大小
            legend.setTextSize(12f); // 设置图例文字的大小
            // 设置图例的样式和间距等
            legend.setXEntrySpace(5f); // 设置图例项之间的X轴间距
            legend.setYEntrySpace(5f); // 设置图例项之间的Y轴间距

            // 刷新图表
            lineChart.invalidate();
        } else {
            Toast.makeText(getContext(), "暂无数据", Toast.LENGTH_SHORT).show();
        }
    }

    // 辅助方法：格式化日期
    private String formatDate(String dateString) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date date = sdf.parse(dateString);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            // 格式化为 mm 月 dd 日
            return String.format("%d月%d日", calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH));
        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }
    }




    protected void setupPieChart(List<History> histories) {
        // 存储每种饮品类型（String）和相应的饮用量总和（Integer）
        Map<String, Integer> typeToVolume = new HashMap<>();

        if (histories != null && !histories.isEmpty()) {
            // 计算每种饮品的总饮用量
            for (History history : histories) {
                String type = history.getType();
                int currentVolume = typeToVolume.getOrDefault(type, 0);
                typeToVolume.put(type, currentVolume + history.getDrink());
            }

            // 创建饼图的数据项列表
            List<PieEntry> entries = new ArrayList<>();
            for (Map.Entry<String, Integer> entry : typeToVolume.entrySet()) {
                entries.add(new PieEntry(entry.getValue(), entry.getKey()));
            }

            PieDataSet dataSet = new PieDataSet(entries, " ");
            dataSet.setColors(ColorTemplate.MATERIAL_COLORS);  // 设置扇形块的颜色
            dataSet.setSliceSpace(3f);                         // 设置扇形块之间的间隙
            dataSet.setSelectionShift(5f);                     // 设置选中扇形块时的偏移量

            PieData data = new PieData(dataSet);
            data.setValueFormatter(new PercentFormatter(pieChart));  // 使用百分比显示
            data.setValueTextSize(11f);                       // 设置数据文本大小
            data.setValueTextColor(Color.WHITE);              // 设置数据文本颜色

            pieChart.setData(data);
            pieChart.setUsePercentValues(true);               // 设置为显示百分比
            pieChart.setEntryLabelTextSize(12f);              // 设置标签文本大小
            pieChart.setEntryLabelColor(Color.BLACK);         // 设置标签文本颜色
            pieChart.setCenterText("饮品占比");               // 设置中心文本
            pieChart.setCenterTextSize(16f);                  // 设置中心文本大小
            pieChart.getDescription().setEnabled(false);      // 不显示描述文本
            pieChart.animateY(1400, Easing.EaseInOutQuad);    // 设置动画

            // 配置图例
            Legend legend = pieChart.getLegend();
            legend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);   // 设置垂直对齐为顶部
            legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT); // 设置水平对齐为右侧
//        legend.setOrientation(Legend.LegendOrientation.VERTICAL);          // 设置图例的方向为垂直
            legend.setDrawInside(false);                                       // 设置绘制在外部
            legend.setEnabled(true);                                           // 启用图例

            // 刷新图表
            pieChart.invalidate();
        } else {
            Toast.makeText(getContext(), "暂无数据", Toast.LENGTH_SHORT).show();
        }
    }




}