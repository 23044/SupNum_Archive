package com.example.supnumarchive;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class CMactivity extends AppCompatActivity {

    ImageView downloadCour1, downloadCour2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cm); // Assure-toi que ton fichier XML s'appelle bien activity_cm.xml

        // Liaison des éléments
        downloadCour1 = findViewById(R.id.download_cour1);
        downloadCour2 = findViewById(R.id.download_cour2);
        ImageView profilephoto = findViewById(R.id.footer_profile_photo);
        profilephoto.setOnClickListener(v -> {
            Intent intent = new Intent(CMactivity.this, ProfileActivity.class);
            startActivity(intent);
        });
        // Action bouton téléchargement cour1
        downloadCour1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(CMactivity.this, "Téléchargement de cour1.pdf...", Toast.LENGTH_SHORT).show();
                // TODO: Implémente ici le téléchargement réel du PDF
            }
        });

        // Action bouton téléchargement cour2
        downloadCour2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(CMactivity.this, "Téléchargement de cour2.pdf...", Toast.LENGTH_SHORT).show();
                // TODO: Implémente ici le téléchargement réel du PDF
            }
        });

        findViewById(R.id.icon_home).setOnClickListener(v -> startActivity(new Intent(this, HomeActivity.class)));
        findViewById(R.id.icon_chat).setOnClickListener(v -> startActivity(new Intent(this, ChatActivity.class)));
        findViewById(R.id.icon_notification).setOnClickListener(v -> startActivity(new Intent(this, NotificationActivity.class)));
        findViewById(R.id.icon_telechrge).setOnClickListener(v -> startActivity(new Intent(this, DownloadActivity.class)));
        findViewById(R.id.icon_back).setOnClickListener(v -> finish());
    }
}
