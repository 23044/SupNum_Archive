package com.example.supnumarchive;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.HashMap;
import java.util.Map;

public class ForumActivity extends AppCompatActivity {

    private LinearLayout forumContainer;
    private EditText questionInput;
    private Button postBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forum);

        forumContainer = findViewById(R.id.forum_container);
        questionInput = findViewById(R.id.question_input);
        postBtn = findViewById(R.id.post_question_btn);

        postBtn.setOnClickListener(v -> postQuestion());
        loadQuestions();
    }

    private void postQuestion() {
        String question = questionInput.getText().toString();
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        Map<String, Object> q = new HashMap<>();
        q.put("question", question);
        q.put("auteurId", uid);

        FirebaseFirestore.getInstance().collection("questions")
                .add(q)
                .addOnSuccessListener(docRef -> {
                    questionInput.setText("");
                    loadQuestions();
                });
    }

    private void loadQuestions() {
        forumContainer.removeAllViews();
        FirebaseFirestore.getInstance().collection("questions")
                .get()
                .addOnSuccessListener(query -> {
                    for (QueryDocumentSnapshot doc : query) {
                        String question = doc.getString("question");
                        TextView view = new TextView(this);
                        view.setText("Q: " + question);
                        view.setPadding(16, 16, 16, 16);
                        forumContainer.addView(view);
                    }
                });
    }
}