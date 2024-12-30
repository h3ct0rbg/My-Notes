package com.android.mynotes.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.ImageView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.android.mynotes.R;
import com.android.mynotes.adapters.NotesAdapter;
import com.android.mynotes.database.NotesDatabase;
import com.android.mynotes.entities.Note;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * MainActivity manages the display of notes in a RecyclerView and the addition of new notes.
 */
public class MainActivity extends AppCompatActivity {

    private RecyclerView notesRecyclerView;
    private final List<Note> noteList = new ArrayList<>();
    private NotesAdapter notesAdapter;

    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler handler = new Handler(Looper.getMainLooper());

    private ActivityResultLauncher<Intent> addNoteLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupUI();
        setupActivityResultLauncher();
        fetchAndDisplayNotes();
    }

    /**
     * Sets up the UI components like RecyclerView and the "Add Note" button.
     */
    private void setupUI() {
        notesRecyclerView = findViewById(R.id.notesRecyclerView);
        notesRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));

        notesAdapter = new NotesAdapter(noteList);
        notesRecyclerView.setAdapter(notesAdapter);

        ImageView imageAddNoteMain = findViewById(R.id.imageAddNoteMain);
        imageAddNoteMain.setOnClickListener(v -> openCreateNoteActivity());
    }

    /**
     * Sets up the ActivityResultLauncher to handle results from CreateNoteActivity.
     */
    private void setupActivityResultLauncher() {
        addNoteLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        fetchAndDisplayNotes();
                    }
                }
        );
    }

    /**
     * Opens the CreateNoteActivity using the ActivityResultLauncher.
     */
    private void openCreateNoteActivity() {
        Intent intent = new Intent(this, CreateNoteActivity.class);
        addNoteLauncher.launch(intent);
    }

    /**
     * Fetches notes from the database and updates the RecyclerView.
     */
    private void fetchAndDisplayNotes() {
        executor.execute(() -> {
            List<Note> notes = NotesDatabase.getDataBase(this).noteDao().getAllNotes();
            handler.post(() -> updateNotesList(notes));
        });
    }

    /**
     * Updates the notes list and refreshes the RecyclerView.
     * @param notes List of notes fetched from the database.
     */
    @SuppressLint("NotifyDataSetChanged")
    private void updateNotesList(List<Note> notes) {
        if (noteList.isEmpty()) {
            noteList.addAll(notes);
            notesAdapter.notifyDataSetChanged();
        } else {
            noteList.add(0, notes.get(0));
            notesAdapter.notifyItemInserted(0);
            notesRecyclerView.smoothScrollToPosition(0);
        }
    }
}