package com.example.supnumarchive;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

public class UploadActivity extends AppCompatActivity {

    private static final int PICK_FILE_REQUEST = 1;
    private Uri selectedFileUri;

    private EditText titreInput, matiereInput, typeInput;
    private Button chooseBtn, uploadBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        titreInput = findViewById(R.id.doc_title);
        matiereInput = findViewById(R.id.doc_matiere);
        typeInput = findViewById(R.id.doc_type);
        chooseBtn = findViewById(R.id.select_file_btn);
        uploadBtn = findViewById(R.id.upload_file_btn);

        chooseBtn.setOnClickListener(v -> chooseFile());
        uploadBtn.setOnClickListener(v -> uploadFile());
    }

    private void chooseFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("application/pdf");
        startActivityForResult(Intent.createChooser(intent, "Choisir un fichier PDF"), PICK_FILE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_FILE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            selectedFileUri = data.getData();
            Toast.makeText(this, "Fichier sélectionné", Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadFile() {
        if (selectedFileUri == null) {
            Toast.makeText(this, "Aucun fichier sélectionné", Toast.LENGTH_SHORT).show();
            return;
        }

        String titre = titreInput.getText().toString();
        String matiere = matiereInput.getText().toString();
        String type = typeInput.getText().toString();

        StorageReference storageRef = FirebaseStorage.getInstance().getReference("documents/" + titre + ".pdf");

        storageRef.putFile(selectedFileUri)
                .addOnSuccessListener(taskSnapshot -> storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    Map<String, Object> docData = new HashMap<>();
                    docData.put("titre", titre);
                    docData.put("matiere", matiere);
                    docData.put("type", type);
                    docData.put("url", uri.toString());
                    docData.put("ajoutePar", FirebaseAuth.getInstance().getCurrentUser().getUid());
                    docData.put("valide", false);

                    FirebaseFirestore.getInstance().collection("documents")
                            .add(docData)
                            .addOnSuccessListener(documentReference -> Toast.makeText(this, "Fichier envoyé avec succès", Toast.LENGTH_SHORT).show())
                            .addOnFailureListener(e -> Toast.makeText(this, "Erreur Firestore : " + e.getMessage(), Toast.LENGTH_SHORT).show());
                }))
                .addOnFailureListener(e -> Toast.makeText(this, "Échec d’upload : " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}
