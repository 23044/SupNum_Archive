package com.example.supnumarchive;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PlanningActivity extends AppCompatActivity {

    private EditText matieresInput, joursInput;
    private Button genererBtn;
    private TextView planningResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_planning);

        matieresInput = findViewById(R.id.input_matieres);
        joursInput = findViewById(R.id.input_jours);
        genererBtn = findViewById(R.id.btn_generer);
        planningResult = findViewById(R.id.text_resultat);

        genererBtn.setOnClickListener(v -> genererPlanning());
    }

    private void genererPlanning() {
        String matieresStr = matieresInput.getText().toString().trim();
        String joursStr = joursInput.getText().toString().trim();

        if (matieresStr.isEmpty() || joursStr.isEmpty()) {
            planningResult.setText("Veuillez remplir les deux champs.");
            return;
        }

        List<String> matieres = Arrays.asList(matieresStr.split(","));
        int jours = Integer.parseInt(joursStr);

        List<List<String>> planning = new ArrayList<>();
        for (int i = 0; i < jours; i++) planning.add(new ArrayList<>());

        for (int i = 0; i < matieres.size(); i++) {
            planning.get(i % jours).add(matieres.get(i).trim());
        }

        StringBuilder result = new StringBuilder();
        for (int i = 0; i < planning.size(); i++) {
            result.append("Jour ").append(i + 1).append(" : ");
            result.append(String.join(", ", planning.get(i)));
            result.append("\n");
        }

        planningResult.setText(result.toString());
    }
}
