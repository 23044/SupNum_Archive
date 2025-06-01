package com.example.supnumarchive;

import android.Manifest;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class SujetsActivity extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_CODE = 1002;

    private StorageReference storageReference;

    private ImageView downloadExam1, downloadTest1;
    private TextView textExam1, textTest1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sujets);

        storageReference = FirebaseStorage.getInstance().getReference();

        downloadExam1 = findViewById(R.id.download_exam1);
        downloadTest1 = findViewById(R.id.download_test1);

        textExam1 = findViewById(R.id.text_exam1);
        textTest1 = findViewById(R.id.text_test1);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !checkPermission()) {
            requestPermission();
        }
        ImageView profilephoto = findViewById(R.id.footer_profile_photo);
        profilephoto.setOnClickListener(v -> {
            Intent intent = new Intent(SujetsActivity.this, ProfileActivity.class);
            startActivity(intent);
        });
        downloadExam1.setOnClickListener(v -> downloadFile("Exam1.pdf"));
        textExam1.setOnClickListener(v -> downloadFile("Exam1.pdf"));

        downloadTest1.setOnClickListener(v -> downloadFile("Test1.pdf"));
        textTest1.setOnClickListener(v -> downloadFile("Test1.pdf"));

        findViewById(R.id.icon_home).setOnClickListener(v -> startActivity(new Intent(this, HomeActivity.class)));
        findViewById(R.id.icon_chat).setOnClickListener(v -> startActivity(new Intent(this, ChatActivity.class)));
        findViewById(R.id.icon_notification).setOnClickListener(v -> startActivity(new Intent(this, NotificationActivity.class)));
        findViewById(R.id.icon_telechrge).setOnClickListener(v -> startActivity(new Intent(this, DownloadActivity.class)));
        findViewById(R.id.icon_back).setOnClickListener(v -> finish());
    }

    private void downloadFile(String fileName) {
        StorageReference fileRef = storageReference.child("pdfs/" + fileName);
        fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
            startDownload(this, fileName.replace(".pdf", ""), ".pdf", Environment.DIRECTORY_DOWNLOADS, uri.toString());
            Toast.makeText(this, "Downloading: " + fileName, Toast.LENGTH_SHORT).show();
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Download failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
        });
    }

    public static void startDownload(Context context, String fileName, String fileExtension, String destinationDirectory, String url) {
        DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        Uri uri = Uri.parse(url);

        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalPublicDir(destinationDirectory, fileName + fileExtension);

        if (downloadManager != null) {
            downloadManager.enqueue(request);
        }
    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission Granted!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Permission Denied!", Toast.LENGTH_SHORT).show();
            }
        }
    }
}

