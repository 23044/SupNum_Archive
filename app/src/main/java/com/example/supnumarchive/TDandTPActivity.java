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

public class TDandTPActivity extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_CODE = 1001;

    private StorageReference storageReference;

    private ImageView downloadTd1, downloadTp1;
    private TextView textTd1, textTp1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tdtp); // تأكد من اسم ملف XML

        // Firebase Storage reference
        storageReference = FirebaseStorage.getInstance().getReference();

        // ربط الواجهات
        downloadTd1 = findViewById(R.id.download_cour1); // زر تحميل TD1
        downloadTp1 = findViewById(R.id.download_cour2); // زر تحميل TP1

        textTd1 = findViewById(R.id.text_cour1); // نص TD1
        textTp1 = findViewById(R.id.text_cour2); // نص TP1

        // صلاحية التخزين
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !checkPermission()) {
            requestPermission();
        }

        // تحميل عند الضغط على الأيقونة أو النص
        downloadTd1.setOnClickListener(v -> downloadFile("TD1.pdf"));
        textTd1.setOnClickListener(v -> downloadFile("TD1.pdf"));

        downloadTp1.setOnClickListener(v -> downloadFile("TP1.pdf"));
        textTp1.setOnClickListener(v -> downloadFile("TP1.pdf"));

        ImageView profilephoto = findViewById(R.id.footer_profile_photo);
        profilephoto.setOnClickListener(v -> {
            Intent intent = new Intent(TDandTPActivity.this, ProfileActivity.class);
            startActivity(intent);
        });
    }

    private void downloadFile(String fileName) {
        StorageReference fileRef = storageReference.child("pdfs/" + fileName);

        fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
            startDownload(this, fileName.replace(".pdf", ""), ".pdf", Environment.DIRECTORY_DOWNLOADS, uri.toString());
            Toast.makeText(this, "Downloading: " + fileName, Toast.LENGTH_SHORT).show();
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Failed to download: " + e.getMessage(), Toast.LENGTH_LONG).show();
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
