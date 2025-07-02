package com.example.supnumarchive;

import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;
import java.util.Map;

public class ValidationActivity extends AppCompatActivity {

    private LinearLayout validationContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_validation);

        validationContainer = findViewById(R.id.validation_container);
        fetchPendingDocuments();
    }

    private void fetchPendingDocuments() {
        FirebaseFirestore.getInstance().collection("documents")
                .whereEqualTo("valide", false)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<DocumentSnapshot> docs = queryDocumentSnapshots.getDocuments();
                    for (DocumentSnapshot doc : docs) {
                        Map<String, Object> data = doc.getData();
                        if (data != null) {
                            String titre = (String) data.get("titre");
                            TextView docView = new TextView(this);
                            docView.setText(titre);
                            docView.setPadding(20, 20, 20, 20);
                            docView.setOnClickListener(v -> validateDocument(doc.getId()));
                            validationContainer.addView(docView);
                        }
                    }
                });
    }

    private void validateDocument(String docId) {
        FirebaseFirestore.getInstance().collection("documents")
                .document(docId)
                .update("valide", true)
                .addOnSuccessListener(unused ->
                        Toast.makeText(this, "Document validé", Toast.LENGTH_SHORT).show()
                );
    }
}