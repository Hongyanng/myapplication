package com.guit.edu.myapplication.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.guit.edu.myapplication.R;
import com.guit.edu.myapplication.entity.History;

import java.util.Date;
import java.util.List;
import java.util.Map;

public abstract class HistoryDataFragment extends Fragment {
    protected BarChart chart;
    protected PieChart pieChart;
    protected LineChart lineChart;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history_data, container, false);
        chart = view.findViewById(R.id.chart);
        pieChart = view.findViewById(R.id.pieChart);
        lineChart = view.findViewById(R.id.lineChart);
        loadDrinkData();
        return view;
    }

    protected abstract void loadDrinkData();


    protected void setupBarChart(List<History> histories) {

    }

    protected void setupLineChart(List<History> histories) {

    }

    protected void setupPieChart(List<History> histories) {

    }


}
