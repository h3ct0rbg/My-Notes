package com.android.mynotes.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.android.mynotes.R;
import com.android.mynotes.adapters.NotesAdapter;
import com.android.mynotes.database.NotesDatabase;
import com.android.mynotes.entities.Note;
import com.android.mynotes.listeners.NotesListener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * MainActivity manages the display of notes in a RecyclerView and the addition of new notes.
 */
public class MainActivity extends AppCompatActivity implements NotesListener {

    private static final int PERMISSION_REQUEST_CODE = 101;
    private static final int REQUEST_CODE_ADD_NOTE = 1;
    private static final int REQUEST_CODE_UPDATE_NOTE = 2;
    private static final int REQUEST_CODE_SHOW_NOTE = 3;

    private RecyclerView notesRecyclerView;
    private final List<Note> noteList = new ArrayList<>();
    private NotesAdapter notesAdapter;

    private int noteClickedPosition = -1;

    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler handler = new Handler(Looper.getMainLooper());

    private ActivityResultLauncher<Intent> addNoteLauncher;
    private ActivityResultLauncher<Intent> updateNoteLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        requestNecessaryPermissions();

        setupUI();
        setupActivityResultLaunchers();
        fetchAndDisplayNotes(REQUEST_CODE_SHOW_NOTE, false);

        EditText inputSearch = findViewById(R.id.inputSearch);
        inputSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

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
    }

    @Override
    public void onNoteClicked(Note note, int position) {
        noteClickedPosition = position;
        Intent intent = new Intent(getApplicationContext(), CreateNoteActivity.class);
        intent.putExtra("isViewOrUpdate", true);
        intent.putExtra("note", note);
        updateNoteLauncher.launch(intent); // Usa el launcher en lugar de startActivityForResult
    }

    /**
     * Request the necessary permissions according to the Android version.
     */
    private void requestNecessaryPermissions() {
        List<String> permissionsNeeded = new ArrayList<>();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
            permissionsNeeded.add(Manifest.permission.READ_MEDIA_IMAGES);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_VIDEO) != PackageManager.PERMISSION_GRANTED) {
            permissionsNeeded.add(Manifest.permission.READ_MEDIA_VIDEO);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            permissionsNeeded.add(Manifest.permission.READ_MEDIA_AUDIO);
        }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R &&
                !Environment.isExternalStorageManager()) {
            requestManageExternalStoragePermission();
            return;
        }

        if (!permissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, permissionsNeeded.toArray(new String[0]), PERMISSION_REQUEST_CODE);
        }
    }

    /**
     * Request the MANAGE_EXTERNAL_STORAGE permission through the system configuration.
     */
    @RequiresApi(api = Build.VERSION_CODES.R)
    private void requestManageExternalStoragePermission() {
        Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
        intent.setData(Uri.parse("package:" + getPackageName()));
        startActivity(intent);
    }

    /**
     * Manages the outcome of permit applications
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_REQUEST_CODE) {
            for (int i = 0; i < permissions.length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                    if (!ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[i])) {
                        showPermissionDeniedPermanentlyDialog();
                    } else {
                        showToast("Permission required to continue: " + permissions[i]);
                    }
                }
            }
        }
    }

    /**
     * Displays a message if a permit was permanently denied.
     */
    private void showPermissionDeniedPermanentlyDialog() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + getPackageName()));
        startActivity(intent);
        showToast("Please enable the permissions manually in the configuration.");
    }

    /**
     * Displays a message from Toast.
     */
    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    /**
     * Sets up the UI components like RecyclerView and the "Add Note" button.
     */
    private void setupUI() {
        notesRecyclerView = findViewById(R.id.notesRecyclerView);
        notesRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));

        notesAdapter = new NotesAdapter(noteList, this);
        notesRecyclerView.setAdapter(notesAdapter);

        ImageView imageAddNoteMain = findViewById(R.id.imageAddNoteMain);
        imageAddNoteMain.setOnClickListener(v -> openCreateNoteActivity());
    }

    /**
     * Sets up the ActivityResultLauncher to handle results from CreateNoteActivity.
     */
    private void setupActivityResultLaunchers() {
        addNoteLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        fetchAndDisplayNotes(REQUEST_CODE_ADD_NOTE, false);
                    }
                }
        );

        updateNoteLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        Intent data = result.getData();
                        boolean isNoteDeleted = false;
                        if (data != null) {
                            isNoteDeleted = data.getBooleanExtra("isNoteDeleted", false);
                        }
                        fetchAndDisplayNotes(REQUEST_CODE_UPDATE_NOTE, isNoteDeleted);
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
    private void fetchAndDisplayNotes(final int requestCode, final boolean isNoteDeleted) {
        executor.execute(() -> {
            List<Note> notes = NotesDatabase.getDataBase(this).noteDao().getAllNotes();
            handler.post(() -> updateNotesList(notes, requestCode, isNoteDeleted));
        });
    }

    /**
     * Updates the notes list and refreshes the RecyclerView.
     * @param notes List of notes fetched from the database.
     */
    @SuppressLint("NotifyDataSetChanged")
    private void updateNotesList(List<Note> notes, int requestCode, boolean isNoteDeleted) {
        if (requestCode == REQUEST_CODE_SHOW_NOTE) {
            noteList.addAll(notes);
            notesAdapter.notifyDataSetChanged();
        } else if (requestCode == REQUEST_CODE_ADD_NOTE) {
            noteList.add(0, notes.get(0));
            notesAdapter.notifyItemInserted(0);
            notesRecyclerView. smoothScrollToPosition(0);
        } else if (requestCode == REQUEST_CODE_UPDATE_NOTE) {
            noteList.remove(noteClickedPosition);
            if (isNoteDeleted){
                notesAdapter.notifyItemRemoved(noteClickedPosition);
            } else {
                noteList.add(noteClickedPosition, notes.get(noteClickedPosition));
                notesAdapter.notifyItemChanged(noteClickedPosition);
            }
        }
    }
}