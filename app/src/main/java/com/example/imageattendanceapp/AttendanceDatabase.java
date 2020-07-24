package com.example.imageattendanceapp;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {Attendance.class}, version = 1, exportSchema = false)
public abstract class AttendanceDatabase extends RoomDatabase {

    public abstract AttendanceDao attendanceDao();

    private static volatile AttendanceDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;

    public static final ExecutorService dbExecutorService = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public static AttendanceDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AttendanceDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            AttendanceDatabase.class,
                            "attendance_database").build();

                }
            }
        }
        return INSTANCE;
    }

}
