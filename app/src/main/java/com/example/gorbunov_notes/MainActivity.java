package com.example.gorbunov_notes;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private NotesAdapter adapter;
    private List<Note> noteList = new ArrayList<>();

    private final ActivityResultLauncher<Intent> editNoteLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    int position = result.getData().getIntExtra("position", -1);
                    String title = result.getData().getStringExtra("title");
                    String desc = result.getData().getStringExtra("description");

                    if (position != -1) {
                        Note oldNote = noteList.get(position);
                        Note updatedNote = new Note(oldNote.getId(), title, desc, oldNote.getDate());
                        noteList.set(position, updatedNote);
                        adapter.notifyItemChanged(position);
                    }
                }
            }
    );

    private final ActivityResultLauncher<Intent> addNoteLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    String title = result.getData().getStringExtra("title");
                    String desc = result.getData().getStringExtra("description");
                    String date = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(new Date());

                    Note newNote = new Note(noteList.size(), title, desc, date);
                    noteList.add(newNote);
                    adapter.notifyItemInserted(noteList.size() - 1);
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new NotesAdapter(noteList);
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new SpacingItemDecoration(16));

        FloatingActionButton fabAdd = findViewById(R.id.fabAdd);
        fabAdd.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddNoteActivity.class);
            addNoteLauncher.launch(intent);
        });
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        int position = adapter.getContextMenuPosition();
        if (item.getItemId() == 1) {
            Note note = noteList.get(position);
            Intent intent = new Intent(this, AddNoteActivity.class);
            intent.putExtra("position", position);
            intent.putExtra("title", note.getTitle());
            intent.putExtra("description", note.getDescription());
            editNoteLauncher.launch(intent);
            return true;
        } else if (item.getItemId() == 2) {
            noteList.remove(position);
            adapter.notifyItemRemoved(position);
            return true;
        }
        return super.onContextItemSelected(item);
    }
}