package com.example.gorbunov_notes;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class NoteViewModel extends AndroidViewModel {
    private final MutableLiveData<List<Note>> notesData = new MutableLiveData<>();
    private final SharedPreferences sharedPreferences;
    private final Gson gson = new Gson();

    public NoteViewModel(@NonNull Application application) {
        super(application);
        sharedPreferences = application.getSharedPreferences("GorbunovNotes", Context.MODE_PRIVATE);
        loadNotes();
    }

    public LiveData<List<Note>> getNotes() {
        return notesData;
    }

    private void loadNotes() {
        String json = sharedPreferences.getString("notes_list", null);
        Type type = new TypeToken<ArrayList<Note>>() {}.getType();
        List<Note> list = gson.fromJson(json, type);
        if (list == null) list = new ArrayList<>();
        notesData.setValue(list);
    }

    private void saveNotes() {
        String json = gson.toJson(notesData.getValue());
        sharedPreferences.edit().putString("notes_list", json).apply();
    }

    public void addNote(Note note) {
        List<Note> current = notesData.getValue();
        if (current != null) {
            current.add(note);
            notesData.setValue(new ArrayList<>(current));
            saveNotes();
        }
    }

    public void updateNote(int position, String title, String desc) {
        List<Note> current = notesData.getValue();
        if (current != null && position != -1) {
            Note old = current.get(position);
            current.set(position, new Note(old.getId(), title, desc, old.getDate()));
            notesData.setValue(new ArrayList<>(current));
            saveNotes();
        }
    }

    public void deleteNote(int position) {
        List<Note> current = notesData.getValue();
        if (current != null && position < current.size()) {
            current.remove(position);
            notesData.setValue(new ArrayList<>(current));
            saveNotes();
        }
    }
}