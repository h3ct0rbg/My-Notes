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
import com.android.mynotes.domain.entities.Note;
import com.android.mynotes.data.NotesRepository;
import com.android.mynotes.domain.viewmodels.NotesViewModel;

import java.util.ArrayList;

/**
 * Main activity that displays a list of notes. Users can create new notes,
 * update existing ones, or perform undo/redo operations.
 */
public class MainActivity extends AppCompatActivity implements NotesListener {

    private NotesAdapter notesAdapter;
    private NotesViewModel notesViewModel;

    /**
     * Called when the activity is first created.
     * Initializes the ViewModel, sets up the UI, and observes data changes.
     *
     * @param savedInstanceState The previously saved state of the activity, if any.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        NotesDatabase database = NotesDatabase.getDataBase(this);
        NotesRepository repository = new NotesRepository(database);
        notesViewModel = new ViewModelProvider(this, new ViewModelProvider.Factory() {
            @NonNull
            @Override
            @SuppressWarnings("unchecked")
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                return (T) new NotesViewModel(repository);
            }
        }).get(NotesViewModel.class);

        setupUI();
        observeViewModel();
    }

    /**
     * Initializes the UI components, including the RecyclerView for notes,
     * the "Add Note" button, and the Undo/Redo operations.
     */
    private void setupUI() {
        RecyclerView notesRecyclerView = findViewById(R.id.notesRecyclerView);
        notesRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(
                2,
                StaggeredGridLayoutManager.VERTICAL
        ));

        notesAdapter = new NotesAdapter(new ArrayList<>(), this);
        notesRecyclerView.setAdapter(notesAdapter);

        findViewById(R.id.imageAddNoteMain).setOnClickListener(v -> openCreateNoteActivity());

        findViewById(R.id.imageUndo).setOnClickListener(v -> notesViewModel.undoCommand());
        findViewById(R.id.imageRedo).setOnClickListener(v -> notesViewModel.redoCommand());
    }

    /**
     * Observes changes in the notes data from the ViewModel.
     * Updates the adapter whenever the notes list is modified.
     */
    private void observeViewModel() {
        notesViewModel.getNotes().observe(this, notes -> {
            if (notesAdapter != null) {
                notesAdapter.updateNotes(notes);
            }
        });
    }

    /**
     * Opens the CreateNoteActivity to allow the user to create a new note.
     */
    private void openCreateNoteActivity() {
        Intent intent = new Intent(this, CreateNoteActivity.class);
        startActivity(intent);
    }

    /**
     * Invoked when a note is clicked in the RecyclerView.
     * Opens the CreateNoteActivity to view or update the selected note.
     *
     * @param note     The note that was clicked.
     * @param position The position of the clicked note in the list.
     */
    @Override
    public void onNoteClicked(Note note, int position) {
        Intent intent = new Intent(this, CreateNoteActivity.class);
        intent.putExtra("isViewOrUpdate", true);
        intent.putExtra("note", note);
        startActivity(intent);
    }
}