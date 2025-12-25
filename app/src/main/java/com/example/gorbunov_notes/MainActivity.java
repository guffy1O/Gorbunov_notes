package com.example.gorbunov_notes;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private NotesAdapter adapter;
    private NoteViewModel viewModel;

    private final ActivityResultLauncher<Intent> editNoteLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    int position = result.getData().getIntExtra("position", -1);
                    String title = result.getData().getStringExtra("title");
                    String desc = result.getData().getStringExtra("description");
                    viewModel.updateNote(position, title, desc);
                    sendNotification("Обновлено", "Заметка '" + title + "' изменена");
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

                    int newId = viewModel.getNotes().getValue() != null ? viewModel.getNotes().getValue().size() : 0;
                    viewModel.addNote(new Note(newId, title, desc, date));
                    sendNotification("Создано", "Заметка '" + title + "' добавлена");
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SplashScreen.installSplashScreen(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkNotificationPermission();

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new SpacingItemDecoration(16));

        viewModel = new ViewModelProvider(this).get(NoteViewModel.class);
        viewModel.getNotes().observe(this, notes -> {
            if (adapter == null) {
                adapter = new NotesAdapter(notes);
                recyclerView.setAdapter(adapter);
            } else {
                adapter.setNotes(notes);
                adapter.notifyDataSetChanged();
            }
        });

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
            Note note = viewModel.getNotes().getValue().get(position);
            Intent intent = new Intent(this, AddNoteActivity.class);
            intent.putExtra("position", position);
            intent.putExtra("title", note.getTitle());
            intent.putExtra("description", note.getDescription());
            editNoteLauncher.launch(intent);
            return true;
        } else if (item.getItemId() == 2) {
            Note note = viewModel.getNotes().getValue().get(position);
            viewModel.deleteNote(position);
            sendNotification("Удаление", "Заметка '" + note.getTitle() + "' удалена");
            return true;
        }
        return super.onContextItemSelected(item);
    }

    private void checkNotificationPermission() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 101);
            }
        }
    }

    private void sendNotification(String title, String message) {
        String channelId = "notes_channel";
        android.app.NotificationManager notificationManager = (android.app.NotificationManager) getSystemService(android.content.Context.NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            android.app.NotificationChannel channel = new android.app.NotificationChannel(
                    channelId, "Notes Notifications", android.app.NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }
        androidx.core.app.NotificationCompat.Builder builder = new androidx.core.app.NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(androidx.core.app.NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);
        notificationManager.notify((int) System.currentTimeMillis(), builder.build());
    }
}