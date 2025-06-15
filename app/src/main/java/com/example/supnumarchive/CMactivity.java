
package com.example.supnumarchive;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class CMactivity extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_CODE = 1003;
    private static final String PREFS_NAME = "DownloadPrefs";
    private static final String ASSETS_FOLDER = "cm";

    private SharedPreferences sharedPreferences;
    private LinearLayout fileContainer;

    private final String[] fileList = {
            "Partie_1__environnement informatique.pdf",
            "Partie_2__environnement informatique.pdf",
            "Fiche_EM_DEV11-22-23.pdf",
            "Chap2_ProgrammesSimples.pdf",
            "3_DEV11_StructuresProgrmmation.pdf"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cm);

        sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        fileContainer = findViewById(R.id.pdf_list_container);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !checkPermission()) {
            requestPermission();
        }

        generateFileList();
        setupNavigation();
    }

    private void generateFileList() {
        fileContainer.removeAllViews();
        for (String fileName : fileList) {
            addFileRow(fileName);
        }
    }

    private void addFileRow(String fileName) {
        LinearLayout fileRow = new LinearLayout(this);
        fileRow.setOrientation(LinearLayout.HORIZONTAL);
        fileRow.setPadding(0, 16, 0, 16);
        fileRow.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));

        ImageView pdfIcon = new ImageView(this);
        pdfIcon.setImageResource(R.drawable.icon_pdf);
        LinearLayout.LayoutParams iconParams = new LinearLayout.LayoutParams(90, 90);
        iconParams.setMarginEnd(30);
        pdfIcon.setLayoutParams(iconParams);

        TextView fileNameText = new TextView(this);
        fileNameText.setText(fileName);
        fileNameText.setTextSize(16);
        fileNameText.setTextColor(getColor(R.color.black));
        fileNameText.setLayoutParams(new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1
        ));

        ImageView downloadButton = new ImageView(this);
        updateDownloadIcon(downloadButton, fileName);
        LinearLayout.LayoutParams downloadParams = new LinearLayout.LayoutParams(72, 72);
        downloadButton.setLayoutParams(downloadParams);
        downloadButton.setClickable(true);

        downloadButton.setOnClickListener(v -> downloadFile(fileName, downloadButton));
        fileNameText.setOnClickListener(v -> downloadFile(fileName, downloadButton));

        fileRow.addView(pdfIcon);
        fileRow.addView(fileNameText);
        fileRow.addView(downloadButton);
        fileContainer.addView(fileRow);
    }

    private void downloadFile(String fileName, ImageView downloadButton) {
        if (isFileDownloaded(fileName)) {
            Toast.makeText(this, fileName + " est déjà téléchargé", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            copyAssetToDownloads(fileName);
            markFileAsDownloaded(fileName);
            updateDownloadIcon(downloadButton, fileName);
            Toast.makeText(this, fileName + " téléchargé avec succès!", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(this, "Erreur lors du téléchargement: " + e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    private void copyAssetToDownloads(String fileName) throws IOException {
        String assetPath = ASSETS_FOLDER + "/" + fileName;
        File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File destinationFile = new File(downloadsDir, fileName);

        try (InputStream inputStream = getAssets().open(assetPath);
             FileOutputStream outputStream = new FileOutputStream(destinationFile)) {

            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }
        }
    }

    private void updateDownloadIcon(ImageView downloadButton, String fileName) {
        if (isFileDownloaded(fileName)) {
            downloadButton.setImageResource(R.drawable.icon_done);
            downloadButton.setContentDescription("Téléchargé: " + fileName);
        } else {
            downloadButton.setImageResource(R.drawable.telecharg);
            downloadButton.setContentDescription("Télécharger: " + fileName);
        }
    }

    private void markFileAsDownloaded(String fileName) {
        sharedPreferences.edit()
                .putBoolean("downloaded_" + fileName, true)
                .apply();
    }

    private boolean isFileDownloaded(String fileName) {
        return sharedPreferences.getBoolean("downloaded_" + fileName, false);
    }

    private void setupNavigation() {
        findViewById(R.id.footer_profile_photo).setOnClickListener(v -> startActivity(new Intent(this, ProfileActivity.class)));
        findViewById(R.id.icon_home).setOnClickListener(v -> startActivity(new Intent(this, HomeActivity.class)));
        findViewById(R.id.icon_chat).setOnClickListener(v -> startActivity(new Intent(this, ChatActivity.class)));
        findViewById(R.id.icon_notification).setOnClickListener(v -> startActivity(new Intent(this, NotificationActivity.class)));
        findViewById(R.id.icon_telechrge).setOnClickListener(v -> startActivity(new Intent(this, DownloadActivity.class)));
        findViewById(R.id.icon_back).setOnClickListener(v -> finish());
    }

    private boolean checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            return Environment.isExternalStorageManager();
        } else {
            return ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        }
    }

    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            try {
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                intent.setData(Uri.parse("package:" + getPackageName()));
                startActivity(intent);
            } catch (Exception e) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                startActivity(intent);
            }
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE
                    },
                    PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission accordée!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Permission refusée!", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
