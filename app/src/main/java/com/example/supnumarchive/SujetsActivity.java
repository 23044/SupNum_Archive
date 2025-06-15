
package com.example.supnumarchive;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
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

public class SujetsActivity extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_CODE = 1002;
    private static final String PREFS_NAME = "DownloadPrefs";
    private static final String ASSETS_FOLDER = "sujets";

    private SharedPreferences sharedPreferences;
    private LinearLayout fileContainer;

    // Liste des fichiers dans assets/sujets/
    private String[] fileList = {
            "Archive _algo&c++_UGEM.pdf",
            "Devoir_2022_2023.pdf"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sujets);

        sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        fileContainer = findViewById(R.id.pdf_list_container);

        // Vérifier les permissions
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !checkPermission()) {
            requestPermission();
        }

        // Générer la liste des fichiers
        generateFileList();

        // Configuration de la navigation
        setupNavigation();
    }

    private void generateFileList() {
        fileContainer.removeAllViews();

        for (String fileName : fileList) {
            addFileRow(fileName);
        }
    }

    private void addFileRow(String fileName) {
        // Créer une ligne pour chaque fichier
        LinearLayout fileRow = new LinearLayout(this);
        fileRow.setOrientation(LinearLayout.HORIZONTAL);
        fileRow.setPadding(0, 16, 0, 16);

        LinearLayout.LayoutParams rowParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        fileRow.setLayoutParams(rowParams);

        // Icône PDF
        ImageView pdfIcon = new ImageView(this);
        pdfIcon.setImageResource(R.drawable.icon_pdf);
        LinearLayout.LayoutParams iconParams = new LinearLayout.LayoutParams(60, 60);
        iconParams.setMarginEnd(20);
        pdfIcon.setLayoutParams(iconParams);

        // Nom du fichier
        TextView fileNameText = new TextView(this);
        fileNameText.setText(fileName);
        fileNameText.setTextSize(16);
        fileNameText.setTextColor(getColor(R.color.black));
        LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1
        );
        fileNameText.setLayoutParams(textParams);

        // Bouton de téléchargement
        ImageView downloadButton = new ImageView(this);
        updateDownloadIcon(downloadButton, fileName);
        LinearLayout.LayoutParams downloadParams = new LinearLayout.LayoutParams(48, 48);
        downloadButton.setLayoutParams(downloadParams);
        downloadButton.setClickable(true);
        downloadButton.setFocusable(true);

        // Action de téléchargement
        downloadButton.setOnClickListener(v -> downloadFile(fileName, downloadButton));
        fileNameText.setOnClickListener(v -> downloadFile(fileName, downloadButton));

        // Ajouter les éléments à la ligne
        fileRow.addView(pdfIcon);
        fileRow.addView(fileNameText);
        fileRow.addView(downloadButton);

        // Ajouter la ligne au conteneur
        fileContainer.addView(fileRow);
    }

    private void downloadFile(String fileName, ImageView downloadButton) {
        // Vérifier si déjà téléchargé
        if (isFileDownloaded(fileName)) {
            Toast.makeText(this, fileName + " est déjà téléchargé", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            // Copier le fichier depuis assets vers Downloads
            copyAssetToDownloads(fileName);

            // Marquer comme téléchargé
            markFileAsDownloaded(fileName);

            // Mettre à jour l'icône
            updateDownloadIcon(downloadButton, fileName);

            Toast.makeText(this, fileName + " téléchargé avec succès!", Toast.LENGTH_SHORT).show();

        } catch (IOException e) {
            Toast.makeText(this, "Erreur lors du téléchargement: " + e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    private void copyAssetToDownloads(String fileName) throws IOException {
        // Chemin source dans assets
        String assetPath = ASSETS_FOLDER + "/" + fileName;

        // Chemin destination dans Downloads
        File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File destinationFile = new File(downloadsDir, fileName);

        // Ouvrir le fichier depuis assets
        InputStream inputStream = getAssets().open(assetPath);

        // Créer le fichier de destination
        FileOutputStream outputStream = new FileOutputStream(destinationFile);

        // Copier le contenu
        byte[] buffer = new byte[1024];
        int length;
        while ((length = inputStream.read(buffer)) > 0) {
            outputStream.write(buffer, 0, length);
        }

        // Fermer les flux
        inputStream.close();
        outputStream.close();
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
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("downloaded_" + fileName, true);
        editor.apply();
    }

    private boolean isFileDownloaded(String fileName) {
        return sharedPreferences.getBoolean("downloaded_" + fileName, false);
    }

    private void setupNavigation() {
        ImageView profilePhoto = findViewById(R.id.footer_profile_photo);
        profilePhoto.setOnClickListener(v -> {
            Intent intent = new Intent(SujetsActivity.this, ProfileActivity.class);
            startActivity(intent);
        });

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
            int write = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            int read = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
            return write == PackageManager.PERMISSION_GRANTED && read == PackageManager.PERMISSION_GRANTED;
        }
    }

    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // Pour Android 11+
            Toast.makeText(this, "Veuillez autoriser l'accès aux fichiers dans les paramètres", Toast.LENGTH_LONG).show();
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