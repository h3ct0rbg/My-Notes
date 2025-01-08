package com.android.mynotes.ui;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

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
import com.android.mynotes.domain.strategy.NoteSortingStrategy;
import com.android.mynotes.domain.strategy.SortByColorStrategy;
import com.android.mynotes.domain.strategy.SortByDateStrategy;
import com.android.mynotes.domain.strategy.SortByTitleStrategy;
import com.android.mynotes.domain.viewmodels.NotesViewModel;
import com.google.android.material.button.MaterialButtonToggleGroup;

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

        findViewById(R.id.imageSort).setOnClickListener(v -> showSortDialog());

        findViewById(R.id.imageUndo).setOnClickListener(v -> notesViewModel.undoCommand());
        findViewById(R.id.imageRedo).setOnClickListener(v -> notesViewModel.redoCommand());

        EditText inputSearch = findViewById(R.id.inputSearch);
        inputSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // No action needed here
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Notify the ViewModel about the updated search query
                notesViewModel.updateSearchQuery(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                // No action needed here
            }
        });
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

    private void showSortDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        View view = LayoutInflater.from(this).inflate(
                R.layout.layout_sort_notes,
                findViewById(R.id.layoutAddUrlContainer)
        );
        builder.setView(view);

        AlertDialog dialogSort = builder.create();
        if (dialogSort.getWindow() != null) {
            dialogSort.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }

        view.findViewById(R.id.textCancel).setOnClickListener(v -> dialogSort.dismiss());

        view.findViewById(R.id.textSort).setOnClickListener(v -> {
            MaterialButtonToggleGroup toggleGroup = view.findViewById(R.id.toggleGroupSort);
            int selectedId = toggleGroup.getCheckedButtonId();
            NoteSortingStrategy strategy = null;
            if (selectedId == R.id.buttonTitleSort) {
                strategy = new SortByTitleStrategy();
                Toast.makeText(this, "Title Sort", Toast.LENGTH_SHORT).show();
            } else if (selectedId == R.id.buttonColorSort) {
                strategy = new SortByColorStrategy();
                Toast.makeText(this, "Color Sort", Toast.LENGTH_SHORT).show();
            } else if (selectedId == R.id.buttonDateSort) {
                strategy = new SortByDateStrategy();
                Toast.makeText(this, "Date Sort", Toast.LENGTH_SHORT).show();
            }

            if (strategy != null) {
                notesViewModel.setSortingStrategy(strategy);
            }

            dialogSort.dismiss();
        });

        dialogSort.show();
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