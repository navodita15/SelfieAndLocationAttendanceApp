package com.example.imageattendanceapp;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

public class AttendanceRepo {

    private AttendanceDao attendanceDao;
    private LiveData<List<Attendance>> listLiveData;

    public AttendanceRepo(Application application) {
        AttendanceDatabase database = AttendanceDatabase.getDatabase(application);
        attendanceDao = database.attendanceDao();
        listLiveData = attendanceDao.getAttendance();
    }

    public LiveData<List<Attendance>> getAttendanceData() {
        return listLiveData;
    }

    public void insert(final Attendance attendance){
        AttendanceDatabase.dbExecutorService.execute(new Runnable() {
            @Override
            public void run() {
                attendanceDao.insert(attendance);
            }
        });
    }


}

