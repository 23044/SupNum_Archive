package com.example.supnumarchive;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class SemestreActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private String currentSemester = "S1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_semestre);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        db = FirebaseFirestore.getInstance();

        if (getIntent().hasExtra("semesteres")) {
            currentSemester = getIntent().getStringExtra("semesteres");
        }

        TextView semestreTitle = findViewById(R.id.semestre_title);
        semestreTitle.setText("Matières - " + currentSemester);

        loadMatieres();

        ImageView profilePhoto = findViewById(R.id.footer_profile_photo);
        profilePhoto.setOnClickListener(v -> {
            Intent intent = new Intent(SemestreActivity.this, ProfileActivity.class);
            startActivity(intent);
        });

        findViewById(R.id.icon_home).setOnClickListener(v -> startActivity(new Intent(this, HomeActivity.class)));
        findViewById(R.id.icon_chat).setOnClickListener(v -> startActivity(new Intent(this, ChatActivity.class)));
        findViewById(R.id.icon_notification).setOnClickListener(v -> startActivity(new Intent(this, NotificationActivity.class)));
        findViewById(R.id.icon_telechrge).setOnClickListener(v -> startActivity(new Intent(this, DownloadActivity.class)));
        findViewById(R.id.icon_back).setOnClickListener(v -> finish());

    }

    private void loadMatieres() {
        Log.d("Firebase", "Début du chargement des matières pour le semestre: " + currentSemester);

        db.collection("semesters")
                .document(currentSemester)
                .collection("Matiers")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        LinearLayout container = findViewById(R.id.semestre_container);
                        container.removeAllViews();

                        int matiereCount = 0;

                        for (QueryDocumentSnapshot doc : task.getResult()) {
                            String docId = doc.getId();
                            String matiereName = doc.getString("titre");

                            if (matiereName != null && !matiereName.trim().isEmpty()) {
                                LinearLayout matiereLayout = createMatiereLayout(matiereName, docId);
                                container.addView(matiereLayout);
                                matiereCount++;
                            }
                        }

                        if (matiereCount == 0) {
                            showNoMatieresMessage(container);
                        }
                    } else {
                        Log.e("Firestore", "Erreur lors de la récupération", task.getException());
                        showErrorMessage(findViewById(R.id.semestre_container));
                    }
                });
    }

    private LinearLayout createMatiereLayout(String name, String code) {
        LinearLayout layout = new LinearLayout(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(32, 16, 32, 16);
        layout.setLayoutParams(params);

        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setBackgroundResource(R.drawable.rounded_background_blue);
        layout.setPadding(32, 32, 32, 32);
        layout.setGravity(Gravity.CENTER);
        layout.setClickable(true);
        layout.setFocusable(true);
        layout.setElevation(8);

        TextView textView = new TextView(this);
        textView.setText(name);
        textView.setTextSize(18);
        textView.setTextColor(Color.WHITE);
        textView.setTypeface(null, Typeface.BOLD);
        textView.setGravity(Gravity.CENTER);
        layout.addView(textView);

        TextView codeView = new TextView(this);
        codeView.setText("(" + code + ")");
        codeView.setTextSize(12);
        codeView.setTextColor(Color.WHITE);
        codeView.setGravity(Gravity.CENTER);
        codeView.setAlpha(0.8f);
        layout.addView(codeView);

        layout.setOnClickListener(v -> {
            Intent intent = new Intent(this, MatiereActivity.class);
            intent.putExtra("semesteres", currentSemester);
            intent.putExtra("matiere_code", code);
            intent.putExtra("matiere_name", name);
            startActivity(intent);
        });

        return layout;
    }

    private void showNoMatieresMessage(LinearLayout container) {
        TextView messageText = new TextView(this);
        messageText.setText("Aucune matière trouvée pour " + currentSemester);
        messageText.setTextSize(16);
        messageText.setTextColor(getResources().getColor(android.R.color.darker_gray));
        messageText.setGravity(Gravity.CENTER);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(32, 32, 32, 32);
        messageText.setLayoutParams(params);

        container.addView(messageText);
    }

    private void showErrorMessage(LinearLayout container) {
        TextView errorText = new TextView(this);
        errorText.setText("Erreur lors du chargement des matières");
        errorText.setTextSize(16);
        errorText.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
        errorText.setGravity(Gravity.CENTER);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(32, 32, 32, 32);
        errorText.setLayoutParams(params);

        container.addView(errorText);
    }

    public void onProfileClick(View view) {
        startActivity(new Intent(this, ProfileActivity.class));
    }
}