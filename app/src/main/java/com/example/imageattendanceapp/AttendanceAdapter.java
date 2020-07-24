package com.example.imageattendanceapp;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AttendanceAdapter extends RecyclerView.Adapter<AttendanceAdapter.AttendanceViewHolder> {

    private List<Attendance> attendances;

    public void setAttendanceData(List<Attendance> attendances) {
        this.attendances = attendances;
    }

    @NonNull
    @Override
    public AttendanceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout_attendance, parent, false);
        return new AttendanceViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull AttendanceViewHolder holder, int position) {
        Attendance attendance = attendances.get(position);
        holder.imagePathTextView.setText("Image Location : " + attendance.getImagePath());
        holder.addressTextView.setText("Address: " + attendance.getAttendanceLocation());

    }

    @Override
    public int getItemCount() {
        return attendances != null ? attendances.size() : 0;
    }

    public static class AttendanceViewHolder extends RecyclerView.ViewHolder {

        private final TextView imagePathTextView;
        private final TextView addressTextView;

        public AttendanceViewHolder(@NonNull View itemView) {
            super(itemView);
            imagePathTextView = itemView.findViewById(R.id.image_path_text_view);
            addressTextView = itemView.findViewById(R.id.address_text_view);
        }
    }
}
