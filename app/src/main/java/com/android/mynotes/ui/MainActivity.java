package com.android.mynotes.ui;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.android.mynotes.R;
import com.android.mynotes.data.NotesDatabase;
import com.android.mynotes.domain.Note;
import com.android.mynotes.data.NotesRepository;
import com.android.mynotes.domain.NotesViewModel;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements NotesListener {

    private NotesAdapter notesAdapter;
    private NotesViewModel notesViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        NotesDatabase database = NotesDatabase.getDataBase(this);
        NotesRepository repository = new NotesRepository(database);
        notesViewModel = new ViewModelProvider(this, new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                return (T) new NotesViewModel(repository);
            }
        }).get(NotesViewModel.class);

        setupUI();

        observeViewModel();
    }

    private void setupUI() {
        RecyclerView notesRecyclerView = findViewById(R.id.notesRecyclerView);
        notesRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));

        notesAdapter = new NotesAdapter(new ArrayList<>(), this);
        notesRecyclerView.setAdapter(notesAdapter);

        findViewById(R.id.imageAddNoteMain).setOnClickListener(v -> openCreateNoteActivity());

        findViewById(R.id.imageUndo).setOnClickListener(v -> notesViewModel.undoCommand());
        findViewById(R.id.imageRedo).setOnClickListener(v -> notesViewModel.redoCommand());
    }


    private void observeViewModel() {
        notesViewModel.getNotes().observe(this, notes -> {
            if (notesAdapter != null) {
                notesAdapter.updateNotes(notes);
            }
        });
    }

    private void openCreateNoteActivity() {
        Intent intent = new Intent(this, CreateNoteActivity.class);
        startActivity(intent);
    }

    @Override
    public void onNoteClicked(Note note, int position) {
        Intent intent = new Intent(this, CreateNoteActivity.class);
        intent.putExtra("isViewOrUpdate", true);
        intent.putExtra("note", note);
        startActivity(intent);
    }
}