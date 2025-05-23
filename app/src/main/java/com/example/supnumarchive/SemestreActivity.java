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

public class SemestreActivity extends AppCompatActivity {

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
        LinearLayout matiere1Layout = findViewById(R.id.layout_matiere1);
        matiere1Layout.setOnClickListener(v -> {
            Intent intent = new Intent(SemestreActivity.this, MatiereActivity.class);
            startActivity(intent);
        });

        ImageView profilephoto = findViewById(R.id.footer_profile_photo);
        profilephoto.setOnClickListener(v -> {
            Intent intent = new Intent(SemestreActivity.this, ProfileActivity.class);
            startActivity(intent);
        });
    }
}