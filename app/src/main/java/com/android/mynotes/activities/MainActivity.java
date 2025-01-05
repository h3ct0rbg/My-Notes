package com.android.mynotes.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.android.mynotes.R;
import com.android.mynotes.adapters.NotesAdapter;
import com.android.mynotes.commands.AddNoteCommand;
import com.android.mynotes.commands.CommandInvoker;
import com.android.mynotes.commands.DeleteNoteCommand;
import com.android.mynotes.commands.EditNoteCommand;
import com.android.mynotes.database.NotesDatabase;
import com.android.mynotes.entities.Note;
import com.android.mynotes.listeners.NotesListener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity implements NotesListener {

    private final List<Note> noteList = new ArrayList<>();
    private NotesAdapter notesAdapter;

    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler handler = new Handler(Looper.getMainLooper());

    private CommandInvoker commandInvoker;
    private NotesDatabase database;

    private ActivityResultLauncher<Intent> addNoteLauncher;
    private ActivityResultLauncher<Intent> editNoteLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        database = NotesDatabase.getDataBase(this);
        commandInvoker = new CommandInvoker();

        setupUI();
        setupActivityResultLaunchers();
        fetchAndDisplayNotes();
    }

    private void setupUI() {
        RecyclerView notesRecyclerView = findViewById(R.id.notesRecyclerView);
        notesRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));

        notesAdapter = new NotesAdapter(noteList, this);
        notesRecyclerView.setAdapter(notesAdapter);

        ImageView imageAddNoteMain = findViewById(R.id.imageAddNoteMain);
        imageAddNoteMain.setOnClickListener(v -> openCreateNoteActivity());

        EditText inputSearch = findViewById(R.id.inputSearch);
        inputSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                notesAdapter.cancelTimer();
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!noteList.isEmpty()) {
                    notesAdapter.searchNotes(s.toString());
                }
            }
        });

        findViewById(R.id.imageUndo).setOnClickListener(v -> undoCommand());
        findViewById(R.id.imageRedo).setOnClickListener(v -> redoCommand());
    }

    private void setupActivityResultLaunchers() {
        addNoteLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Note newNote = (Note) result.getData().getSerializableExtra("note");
                        addNote(newNote);
                    }
                }
        );

        editNoteLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Note oldNote = (Note) result.getData().getSerializableExtra("oldNote");
                        Note newNote = (Note) result.getData().getSerializableExtra("newNote");
                        boolean isDeleted = result.getData().getBooleanExtra("isDeleted", false);

                        if (isDeleted) {
                            deleteNote(oldNote);
                        } else {
                            editNote(oldNote, newNote);
                        }
                    }
                }
        );
    }

    private void openCreateNoteActivity() {
        Intent intent = new Intent(this, CreateNoteActivity.class);
        addNoteLauncher.launch(intent);
    }

    @SuppressLint("NotifyDataSetChanged")
    private void fetchAndDisplayNotes() {
        executor.execute(() -> {
            List<Note> notes = database.noteDao().getAllNotes();
            handler.post(() -> {
                noteList.clear();
                noteList.addAll(notes);
                notesAdapter.notifyDataSetChanged();
            });
        });
    }

    private void addNote(Note note) {
        AddNoteCommand command = new AddNoteCommand(database, note);
        commandInvoker.executeCommand(command);
        fetchAndDisplayNotes();
    }

    private void editNote(Note oldNote, Note newNote) {
        EditNoteCommand command = new EditNoteCommand(database, oldNote, newNote);
        commandInvoker.executeCommand(command);
        fetchAndDisplayNotes();
    }

    private void deleteNote(Note note) {
        DeleteNoteCommand command = new DeleteNoteCommand(database, note);
        commandInvoker.executeCommand(command);
        fetchAndDisplayNotes();
    }

    private void undoCommand() {
        commandInvoker.undo();
        fetchAndDisplayNotes();
    }

    private void redoCommand() {
        commandInvoker.redo();
        fetchAndDisplayNotes();
    }

    @Override
    public void onNoteClicked(Note note, int position) {
        Intent intent = new Intent(getApplicationContext(), CreateNoteActivity.class);
        intent.putExtra("isViewOrUpdate", true);
        intent.putExtra("note", note);
        editNoteLauncher.launch(intent);
    }
}