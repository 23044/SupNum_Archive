package com.example.supnumarchive;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

public class HomeActivity extends AppCompatActivity {

    private static final int FILE_SELECT_CODE = 1001;

    private Uri selectedFileUri;
    private EditText titleEditText, descriptionEditText;
    private Button selectFileBtn, uploadBtn;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // navigation
        findViewById(R.id.icon_home).setOnClickListener(v -> startActivity(new Intent(this, HomeActivity.class)));
        findViewById(R.id.icon_chat).setOnClickListener(v -> startActivity(new Intent(this, ChatActivity.class)));
        findViewById(R.id.icon_notification).setOnClickListener(v -> startActivity(new Intent(this, NotificationActivity.class)));
        findViewById(R.id.icon_telechrge).setOnClickListener(v -> startActivity(new Intent(this, DownloadActivity.class)));
        findViewById(R.id.icon_back).setOnClickListener(v -> finish());

        findViewById(R.id.footer_profile_photo).setOnClickListener(v -> {
            startActivity(new Intent(this, ProfileActivity.class));
        });

        findViewById(R.id.s1_layout).setOnClickListener(v -> {
            startActivity(new Intent(this, SemestreActivity.class));
        });

        // --- INITIALISATION UPLOAD ---
        titleEditText = findViewById(R.id.upload_title);
        descriptionEditText = findViewById(R.id.upload_description);
        selectFileBtn = findViewById(R.id.select_file_button);
        uploadBtn = findViewById(R.id.upload_button);
        progressDialog = new ProgressDialog(this);

        selectFileBtn.setOnClickListener(v -> selectFile());
        uploadBtn.setOnClickListener(v -> uploadFile());
    }

    private void selectFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        startActivityForResult(intent, FILE_SELECT_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FILE_SELECT_CODE && resultCode == RESULT_OK && data != null) {
            selectedFileUri = data.getData();
            Toast.makeText(this, "Fichier sélectionné", Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadFile() {
        String title = titleEditText.getText().toString().trim();
        String desc = descriptionEditText.getText().toString().trim();

        if (selectedFileUri == null || title.isEmpty() || desc.isEmpty()) {
            Toast.makeText(this, "Remplis tous les champs et choisis un fichier", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.setMessage("Téléversement en cours...");
        progressDialog.show();

        String fileName = System.currentTimeMillis() + "_" + title.replace(" ", "_");
        StorageReference fileRef = FirebaseStorage.getInstance().getReference("uploads/" + fileName);

        fileRef.putFile(selectedFileUri).addOnSuccessListener(taskSnapshot -> {
            fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                progressDialog.dismiss();
                Toast.makeText(this, "Fichier téléversé", Toast.LENGTH_SHORT).show();

                // Sauvegarder les métadonnées dans Realtime Database
                String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                Map<String, Object> fileMeta = new HashMap<>();
                fileMeta.put("title", title);
                fileMeta.put("description", desc);
                fileMeta.put("fileUrl", uri.toString());
                fileMeta.put("userId", userId);

                FirebaseDatabase.getInstance()
                        .getReference("user_uploads")
                        .child(userId)
                        .push()
                        .setValue(fileMeta);
            });
        }).addOnFailureListener(e -> {
            progressDialog.dismiss();
            Toast.makeText(this, "Erreur : " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }
}
