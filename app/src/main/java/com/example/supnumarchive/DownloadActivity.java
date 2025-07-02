package com.example.supnumarchive;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.File;

public class DownloadActivity extends AppCompatActivity {

    private LinearLayout downloadedFilesContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_download);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Navigation
        findViewById(R.id.icon_home).setOnClickListener(v -> startActivity(new Intent(this, HomeActivity.class)));
        findViewById(R.id.icon_chat).setOnClickListener(v -> startActivity(new Intent(this, ChatActivity.class)));
        findViewById(R.id.icon_notification).setOnClickListener(v -> startActivity(new Intent(this, NotificationActivity.class)));
        findViewById(R.id.icon_telechrge).setOnClickListener(v -> recreate()); // Refresh
        findViewById(R.id.icon_back).setOnClickListener(v -> finish());

        downloadedFilesContainer = findViewById(R.id.downloaded_files_container);
        listDownloadedFiles();
    }

    private void listDownloadedFiles() {
        File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File[] files = downloadsDir.listFiles();

        if (files == null || files.length == 0) {
            Toast.makeText(this, "Aucun fichier téléchargé trouvé.", Toast.LENGTH_SHORT).show();
            return;
        }

        for (File file : files) {
            if (file.isFile()) {
                addFileToList(file);
            }
        }
    }

    private void addFileToList(File file) {
        LinearLayout fileRow = new LinearLayout(this);
        fileRow.setOrientation(LinearLayout.HORIZONTAL);
        fileRow.setPadding(0, 20, 0, 20);
        fileRow.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));

        ImageView icon = new ImageView(this);
        icon.setImageResource(R.drawable.icon_pdf); // Change selon type si tu veux
        LinearLayout.LayoutParams iconParams = new LinearLayout.LayoutParams(90, 90);
        iconParams.setMarginEnd(30);
        icon.setLayoutParams(iconParams);

        TextView fileName = new TextView(this);
        fileName.setText(file.getName());
        fileName.setTextSize(16);
        fileName.setLayoutParams(new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1
        ));

        fileRow.addView(icon);
        fileRow.addView(fileName);

        fileRow.setOnClickListener(v -> openFile(file));
        downloadedFilesContainer.addView(fileRow);
    }

    private void openFile(File file) {
        Uri uri = Uri.fromFile(file);
        String extension = MimeTypeMap.getFileExtensionFromUrl(uri.toString());
        String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);

        if (mimeType == null) mimeType = "*/*";

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(uri, mimeType);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_ACTIVITY_NEW_TASK);

        try {
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(this, "Aucune application pour ouvrir ce fichier.", Toast.LENGTH_SHORT).show();
        }
    }
}
