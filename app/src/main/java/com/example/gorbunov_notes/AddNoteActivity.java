package com.example.gorbunov_notes;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;

public class AddNoteActivity extends AppCompatActivity {
    private EditText editTitle, editDescription;
    private int position = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);

        editTitle = findViewById(R.id.editTitle);
        editDescription = findViewById(R.id.editDescription);
        Button btnSave = findViewById(R.id.btnSave);

        Intent intent = getIntent();
        if (intent.hasExtra("position")) {
            position = intent.getIntExtra("position", -1);
            editTitle.setText(intent.getStringExtra("title"));
            editDescription.setText(intent.getStringExtra("description"));
        }

        btnSave.setOnClickListener(v -> {
            String title = editTitle.getText().toString();
            String desc = editDescription.getText().toString();

            if (!title.isEmpty()) {
                Intent resultIntent = new Intent();
                resultIntent.putExtra("title", title);
                resultIntent.putExtra("description", desc);
                resultIntent.putExtra("position", position);
                setResult(RESULT_OK, resultIntent);
                finish();
            }
        });
    }
}