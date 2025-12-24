package com.example.gorbunov_notes;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;

public class AddNoteActivity extends AppCompatActivity {
    private EditText editTitle, editDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);

        editTitle = findViewById(R.id.editTitle);
        editDescription = findViewById(R.id.editDescription);
        Button btnSave = findViewById(R.id.btnSave);

        btnSave.setOnClickListener(v -> {
            String title = editTitle.getText().toString();
            String desc = editDescription.getText().toString();

            if (!title.isEmpty()) {
                Intent intent = new Intent();
                intent.putExtra("title", title);
                intent.putExtra("description", desc);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }
}