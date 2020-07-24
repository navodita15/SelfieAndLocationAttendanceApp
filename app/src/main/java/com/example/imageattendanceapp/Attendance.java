package com.example.imageattendanceapp;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import org.jetbrains.annotations.NotNull;

@Entity(tableName = "attendance_table")
public class Attendance {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @NonNull
    @ColumnInfo(name = "imagePath")
    private String imagePath;

    @NonNull
    @ColumnInfo(name = "location")
    private String attendanceLocation;

    public Attendance(@NonNull String imagePath, @NotNull String attendanceLocation) {
        this.imagePath = imagePath;
        this.attendanceLocation = attendanceLocation;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @NonNull
    public String getImagePath() {
        return imagePath;
    }


    @NonNull
    public String getAttendanceLocation() {
        return attendanceLocation;
    }

    @Override
    public String toString() {
        return "Attendance{" +
                "id=" + id +
                ", imagePath='" + imagePath + '\'' +
                ", attendanceLocation='" + attendanceLocation + '\'' +
                '}';
    }
}
