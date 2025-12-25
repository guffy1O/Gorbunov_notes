package com.example.gorbunov_notes;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private NotesAdapter adapter;
    private List<Note> noteList;

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
                        saveNotes();
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
                    saveNotes();
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SplashScreen.installSplashScreen(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loadNotes();

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

    private void saveNotes() {
        SharedPreferences sharedPreferences = getSharedPreferences("GorbunovNotes", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(noteList);
        editor.putString("notes_list", json);
        editor.apply();
    }

    private void loadNotes() {
        SharedPreferences sharedPreferences = getSharedPreferences("GorbunovNotes", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("notes_list", null);
        Type type = new TypeToken<ArrayList<Note>>() {}.getType();
        noteList = gson.fromJson(json, type);

        if (noteList == null) {
            noteList = new ArrayList<>();
        }
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
            saveNotes();
            return true;
        }
        return super.onContextItemSelected(item);
    }
}