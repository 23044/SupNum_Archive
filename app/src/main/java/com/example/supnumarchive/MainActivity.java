package com.example.supnumarchive;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private Button btnLogin, btnSignup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // Assurez-vous que le nom de votre layout est correct

        // Initialisation des boutons
        btnLogin = findViewById(R.id.btn_login);
        btnSignup = findViewById(R.id.btn_signup);

        // Vérifiez si l'utilisateur est déjà connecté
        SessionManager session = new SessionManager(this);
        if (session.isLoggedIn()) {
            // Redirigez vers HomeActivity si l'utilisateur est connecté
            startActivity(new Intent(this, HomeActivity.class));
            finish(); // Fermez MainActivity
        }

        // Gestion des clics sur les boutons
        btnLogin.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
        });

        btnSignup.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, SignupActivity.class));
        });
    }
}
