package com.guit.edu.myapplication.Adapter;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.guit.edu.myapplication.R;
import com.guit.edu.myapplication.SPUtils;
import com.guit.edu.myapplication.entity.History;
import com.guit.edu.myapplication.entity.User;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;

public class WaterRecordAdapter extends RecyclerView.Adapter<WaterRecordAdapter.ViewHolder> {
    private List<History> historyList;

    public WaterRecordAdapter(List<History> historyList) {
        this.historyList = historyList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_water_record, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        History history = historyList.get(position);
        holder.bind(history);
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                // 弹出对话框确认删除
                AlertDialog.Builder builder = new AlertDialog.Builder(holder.itemView.getContext());
                builder.setMessage("是否删除这条记录？")
                        .setPositiveButton("是", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // 用户确认删除
                                deleteHistoryFromDatabase(history, holder.getAdapterPosition(), holder.itemView.getContext());
                            }
                        })
                        .setNegativeButton("否", null)
                        .show();
                return true;
            }
        });
    }


    private void deleteHistoryFromDatabase(History history, int position, Context context) {
        history.delete(new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    // 从本地列表中删除数据，并通知适配器更新
                    historyList.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, historyList.size());
                    Toast.makeText(context, "记录删除成功", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "记录删除失败: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    @Override
    public int getItemCount() {
        return historyList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView typeTextView;
        private TextView drinkTextView;
        private TextView timeTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            typeTextView = itemView.findViewById(R.id.type_info);
            drinkTextView = itemView.findViewById(R.id.drink_info);
            timeTextView = itemView.findViewById(R.id.time_info);
        }

        public void bind(History history) {
            typeTextView.setText(history.getType());
            drinkTextView.setText(String.valueOf(history.getDrink()) + "ml");
            timeTextView.setText(formatTime(history.getCreatedAt()));
        }
        private String formatTime(String time) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                Date date = sdf.parse(time);
                SimpleDateFormat sdfResult = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
                return sdfResult.format(date);
            } catch (Exception e) {
                e.printStackTrace();
                return time;
            }
        }
    }
}
