package com.example.supnumarchive;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

public class HomeActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        LinearLayout s1Layout = findViewById(R.id.s1_layout);
        s1Layout.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, SemestreActivity.class);
            startActivity(intent);
        });

        ImageView profilephoto = findViewById(R.id.footer_profile_photo);
        profilephoto.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, ProfileActivity.class);
            startActivity(intent);
        });
    }
}
