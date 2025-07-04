package com.example.supnumarchive;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {
    private EditText passwordEditText;
    private Button loginButton;
    private EditText loginEmail;
    private ImageButton togglePasswordButton;
    private TextView signupText, forgotPasswordText;
    private boolean isPasswordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialisation des vues
        loginButton = findViewById(R.id.loginButton);
        loginEmail = findViewById(R.id.LoginEmail);
        passwordEditText = findViewById(R.id.passwordEditText);
        togglePasswordButton = findViewById(R.id.togglePasswordButton);
        signupText = findViewById(R.id.signupText);
        forgotPasswordText = findViewById(R.id.forgotPasswordText);

        // Gestion des clics sur le bouton de connexion
        loginButton.setOnClickListener(view -> {
            if (validateEmail() && validatePassword()) {
                checkUser();
            }
        });

        // Redirection vers l'activité d'inscription
        signupText.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, SignupActivity.class));
        });

        // Gestion de la visibilité du mot de passe
        togglePasswordButton.setOnClickListener(v -> {
            if (isPasswordVisible) {
                passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                togglePasswordButton.setImageResource(R.drawable.baseline_visibility_off_24);
            } else {
                passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                togglePasswordButton.setImageResource(R.drawable.baseline_visibility_on_24); // Correction ici pour l'icône visible
            }
            isPasswordVisible = !isPasswordVisible;
            passwordEditText.setSelection(passwordEditText.getText().length());
        });
    }

    private boolean validateEmail() {
        String val = loginEmail.getText().toString().trim();
        if (val.isEmpty()) {
            loginEmail.setError("Email ne peut pas être vide");
            return false;
        } else {
            loginEmail.setError(null);
            return true;
        }
    }

    private boolean validatePassword() {
        String val = passwordEditText.getText().toString().trim();
        if (val.isEmpty()) {
            passwordEditText.setError("Le mot de passe ne peut pas être vide");
            return false;
        } else {
            passwordEditText.setError(null);
            return true;
        }
    }

    private void checkUser() {
        String userEmail = loginEmail.getText().toString().trim();
        String userPassword = passwordEditText.getText().toString().trim();

        Log.d("LoginActivity", "Email saisi: " + userEmail);
        Log.d("LoginActivity", "Mot de passe saisi: " + userPassword);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");
        Query checkUserDatabase = reference.orderByChild("email").equalTo(userEmail);

        checkUserDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d("LoginActivity", "onDataChange called");
                if (snapshot.exists()) {
                    for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                        String passwordFromDB = userSnapshot.child("password").getValue(String.class);
                        String usernameFromDB = userSnapshot.child("username").getValue(String.class);

                        if (passwordFromDB != null) {
                            passwordFromDB = passwordFromDB.trim();
                        }

                        Log.d("LoginActivity", "Mot de passe de la base de données: " + passwordFromDB);

                        if (Objects.equals(passwordFromDB, userPassword)) {
                            // Mot de passe correct
                            SessionManager session = new SessionManager(LoginActivity.this);
                            session.createSession(userEmail, usernameFromDB);

                            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                            startActivity(intent);
                            finish();
                            return; 
                        } else {
                            Log.d("LoginActivity", "Mot de passe incorrect.");
                            passwordEditText.setError("Mot de passe incorrect");
                            passwordEditText.requestFocus();
                        }
                    }
                } else {
                    Log.d("LoginActivity", "L'utilisateur n'existe pas !");
                    loginEmail.setError("L'utilisateur n'existe pas !");
                    loginEmail.requestFocus();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("LoginActivity", "Erreur de base de données: " + error.getMessage());
                Toast.makeText(LoginActivity.this, "Erreur de base de données: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
