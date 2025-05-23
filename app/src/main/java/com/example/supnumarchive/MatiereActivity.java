package com.example.supnumarchive;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MatiereActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.matiere_activity); // Assure-toi que ce fichier XML s'appelle bien ainsi

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Accès à la section CM
        LinearLayout cmlayout = findViewById(R.id.layout_cm);
        cmlayout.setOnClickListener(v -> {
            Intent intent = new Intent(MatiereActivity.this, CMactivity.class);
            startActivity(intent);
        });

        // Accès à la section TD/TP
        LinearLayout tdtplayout = findViewById(R.id.layout_tdtp);
        tdtplayout.setOnClickListener(v -> {
            Intent intent = new Intent(MatiereActivity.this, TDandTPActivity.class);
            startActivity(intent);
        });

        // Accès à la section Sujets
        LinearLayout sujetlayout = findViewById(R.id.layout_sujet);
        sujetlayout.setOnClickListener(v -> {
            Intent intent = new Intent(MatiereActivity.this, SujetsActivity.class);
            startActivity(intent);
        });

        // Accès à la section Challenge (corrigé)
        LinearLayout challangelayout = findViewById(R.id.layout_chalange);
        challangelayout.setOnClickListener(v -> {
            Intent intent = new Intent(MatiereActivity.this, HomeActivity.class); // Assure-toi du bon nom de classe
            startActivity(intent);
        });

        // Redirection vers le profil (ImageView utilisée)
        ImageView profilePhoto = findViewById(R.id.footer_profile_photo);
        profilePhoto.setOnClickListener(v -> {
            Intent intent = new Intent(MatiereActivity.this, ProfileActivity.class);
            startActivity(intent);
        });

        // (Optionnel) Gestion du téléchargement de fichiers PDF
//        ImageView downloadCour1 = findViewById(R.id.download_cour1);
//        downloadCour1.setOnClickListener(v -> {
//            // TODO: Implémenter le téléchargement ou l'ouverture du PDF cour1.pdf
//        });
//
//        ImageView downloadCour2 = findViewById(R.id.download_cour2);
//        downloadCour2.setOnClickListener(v -> {
//            // TODO: Implémenter le téléchargement ou l'ouverture du PDF cour2.pdf
//        });
    }
}
