package com.example.imageattendanceapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "MainActivity";

    private static final int CAMERA_REQUEST = 123;
    private static final int REQUEST_TAKE_CAPTURE = 101;
    private static final int LOCATION_PERMISSION = 200;
    private ImageView clickedImageView;
    private static final int CAMERA_PERMISSION_CODE = 100;
    private String imagePath;
    private double longitude = 0.0;
    private double latitude = 0.0;
    private TextView locationTextView;
    private NavigationService navigationService;
    private AttendanceViewModel attendanceViewModel;
    private RecyclerView attendanceRecyclerView;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        attendanceViewModel = new ViewModelProvider(this).get(AttendanceViewModel.class);
        requestPermission();

        navigationService = new NavigationService(this);
        locationTextView = findViewById(R.id.location_text_view);
        clickedImageView = findViewById(R.id.clicked_image_view);
        attendanceRecyclerView = findViewById(R.id.attendance_list);
        Button sendAttendanceButton = findViewById(R.id.send_attendance_button);
        sendAttendanceButton.setOnClickListener(this);
        Button getAttendanceBtn = findViewById(R.id.get_attendance_button);
        getAttendanceBtn.setOnClickListener(this);
    }

    private void requestPermission() {
        Dexter.withActivity(this)
                .withPermissions(Manifest.permission.CAMERA,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                ).withListener(new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport report) {
                if (report.areAllPermissionsGranted()) {
                    Log.d(TAG, "onPermissionsChecked: granted");
                }
                // check for permanent denial of any permission
                if (report.isAnyPermissionPermanentlyDenied()) {
                    // show alert dialog navigating to Settings
                    showSettingsDialog();
                }
            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                token.continuePermissionRequest();
            }
        }).onSameThread().check();
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "onRequestPermissionsResult: cam permission granted");
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            } else {
                Log.d(TAG, "onRequestPermissionsResult: cam permission denied");
            }
        } else if (requestCode == LOCATION_PERMISSION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "onRequestPermissionsResult: location granted");
            } else {
                Log.d(TAG, "onRequestPermissionsResult: location denied");
            }
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_TAKE_CAPTURE && resultCode == RESULT_OK) {
            File imgFile = new File(imagePath);
            if (imgFile.exists()) {
                Uri imageUri = Uri.fromFile(imgFile);
                Log.d(TAG, "onActivityResult: image uri == " + imageUri);
                Glide.with(this).load(imageUri).into(clickedImageView);
                locationTextView.setText("Address: " + navigationService.getAddress(latitude, longitude));
                attendanceViewModel.insert(new Attendance(imagePath, navigationService.getAddress(latitude, longitude)));
            }
        }
    }


    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.send_attendance_button) {
            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
            } else {
                getCurrentLocation();
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                if (cameraIntent.resolveActivity(getPackageManager()) != null) {

                    File pictureFile;
                    try {
                        pictureFile = createImageFile();
                    } catch (IOException ex) {
                        Toast.makeText(this,
                                "Photo file can't be created, please try again",
                                Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "onClick: exception ", ex);
                        return;
                    }
                    Uri photoURI = FileProvider.getUriForFile(this,
                            BuildConfig.APPLICATION_ID + ".provider",
                            pictureFile);
                    cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    startActivityForResult(cameraIntent, REQUEST_TAKE_CAPTURE);
                }

            }
        } else if (view.getId() == R.id.get_attendance_button) {
            final AttendanceAdapter attendanceAdapter = new AttendanceAdapter();
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
            attendanceRecyclerView.setLayoutManager(linearLayoutManager);
            DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this,
                    linearLayoutManager.getOrientation());
            attendanceRecyclerView.addItemDecoration(dividerItemDecoration);
            attendanceRecyclerView.setAdapter(attendanceAdapter);
            attendanceViewModel.getAttendanceList().observe(this, new Observer<List<Attendance>>() {
                @Override
                public void onChanged(List<Attendance> attendances) {
                    attendanceAdapter.setAttendanceData(attendances);
                }
            });
        }
    }

    public void getCurrentLocation() {

        if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_PERMISSION);
        } else {
            Location loc = navigationService.getLocationManager().getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (navigationService.canGetLocation()) {
                if (loc != null) {
                    latitude = loc.getLatitude();
                    longitude = loc.getLongitude();
                }

            } else {
                navigationService.showSettingsAlert();
            }
        }

    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd", Locale.UK).format(new Date());
        String mFileName = "IMG_" + timeStamp;
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(mFileName, ".jpg", storageDir);
        imagePath = image.getAbsolutePath();
        Log.d(TAG, "createImageFile: imagepath == " + imagePath);
        return image;
    }

    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Need Permissions");
        builder.setMessage(
                "This app needs permission to use this feature. You can grant them in app settings.");
        builder.setPositiveButton("GOTO SETTINGS", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                openSettings();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    // navigating user to app settings
    private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        navigationService.stopListener();
    }
}