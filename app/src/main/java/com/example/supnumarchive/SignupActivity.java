package com.example.supnumarchive;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignupActivity extends AppCompatActivity {

    private EditText SignupUsername, SignupEmail, passwordEditText;
    private TextView goToLogin;
    private Button signupButton;
    private ImageButton togglePasswordButton;
    private boolean isPasswordVisible = false;

    private FirebaseDatabase database;
    private DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        initializeViews();

        goToLogin.setOnClickListener(v -> {
            startActivity(new Intent(SignupActivity.this, LoginActivity.class));
            finish();
        });

        togglePasswordButton.setOnClickListener(v -> togglePasswordVisibility());

        signupButton.setOnClickListener(v -> registerUser());
    }

    private void initializeViews() {
        SignupUsername = findViewById(R.id.SignupUsername);
        SignupEmail = findViewById(R.id.SignupEmail);
        passwordEditText = findViewById(R.id.passwordEditText);
        togglePasswordButton = findViewById(R.id.togglePasswordButton);
        goToLogin = findViewById(R.id.goToLogin);
        signupButton = findViewById(R.id.signupButton);
    }

    private void togglePasswordVisibility() {
        if (isPasswordVisible) {
            passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            togglePasswordButton.setImageResource(R.drawable.baseline_visibility_off_24);
        } else {
            passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            togglePasswordButton.setImageResource(R.drawable.baseline_visibility_off_24);
        }
        isPasswordVisible = !isPasswordVisible;
        passwordEditText.setSelection(passwordEditText.getText().length());
    }

    private void registerUser() {
        String username = SignupUsername.getText().toString().trim();
        String email = SignupEmail.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            SignupEmail.setError("Email invalide");
            return;
        }

        if (password.length() < 6) {
            passwordEditText.setError("Le mot de passe doit contenir au moins 6 caractères");
            return;
        }

        database = FirebaseDatabase.getInstance();
        reference = database.getReference("users");

        String userId = reference.push().getKey();
        HelperClass helperClass = new HelperClass(email, password, username);

        reference.child(userId).setValue(helperClass)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        SessionManager session = new SessionManager(SignupActivity.this);
                        session.createSession(email, username);
                        Toast.makeText(SignupActivity.this, "Compte créé avec succès", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(SignupActivity.this, LoginActivity.class));
                        finish();
                    } else {
                        Toast.makeText(SignupActivity.this, "Erreur: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
