package com.example.imageattendanceapp;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface AttendanceDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(Attendance attendance);

    @Query("SELECT * FROM attendance_table ORDER BY id ASC")
    LiveData<List<Attendance>> getAttendance();
}
