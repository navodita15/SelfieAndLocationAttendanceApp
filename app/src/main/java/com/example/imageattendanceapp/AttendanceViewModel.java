package com.example.imageattendanceapp;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class AttendanceViewModel extends AndroidViewModel {

    private final AttendanceRepo attendanceRepo;
    private final LiveData<List<Attendance>> attendanceList;

    public AttendanceViewModel(@NonNull Application application) {
        super(application);
        attendanceRepo = new AttendanceRepo(application);
        attendanceList = attendanceRepo.getAttendanceData();
    }

    public LiveData<List<Attendance>> getAttendanceList() {
        return attendanceList;
    }

    public void insert(Attendance attendance){
        attendanceRepo.insert(attendance);
    }
}
