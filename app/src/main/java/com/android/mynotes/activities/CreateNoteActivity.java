package com.android.mynotes.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Pair;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.android.mynotes.R;
import com.android.mynotes.database.NotesDatabase;
import com.android.mynotes.decorators.NoteComponent;
import com.android.mynotes.decorators.NoteDecorator;
import com.android.mynotes.decorators.NoteDecoratorFactory;
import com.android.mynotes.decorators.RedNoteDecorator;
import com.android.mynotes.entities.Note;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Handles the creation and saving of new notes.
 */
public class CreateNoteActivity extends AppCompatActivity {

    private EditText inputNoteTitle, inputNoteSubtitle, inputNoteText;
    private TextView textDateTime;
    private View viewSubtitleIndicator;
    private ImageView imageNote;

    private String selectedNoteColor = "#333333";
    private String selectedImagePath;

    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler handler = new Handler(Looper.getMainLooper());

    private final ActivityResultLauncher<String> requestPermissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(), this::handlePermissionResult
    );

    private final ActivityResultLauncher<Intent> selectImageLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), this::handleImageSelectionResult
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_note);
        initializeUI();
    }

    /**
     * Initializes UI components and sets up listeners.
     */
    private void initializeUI() {
        setupBackNavigation();
        setupFields();
        setupSaveButton();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            setupMiscellaneous();
        }
    }

    /**
     * Configures the back navigation behavior.
     */
    private void setupBackNavigation() {
        ImageView imageBack = findViewById(R.id.imageBack);
        imageBack.setOnClickListener(v -> finish());

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                finish();
            }
        });
    }

    /**
     * Configures note input fields and sets the current date and time.
     */
    private void setupFields() {
        inputNoteTitle = findViewById(R.id.inputNoteTitle);
        inputNoteSubtitle = findViewById(R.id.inputNoteSubtitle);
        inputNoteText = findViewById(R.id.inputNote);
        textDateTime = findViewById(R.id.textDateTime);
        viewSubtitleIndicator = findViewById(R.id.viewSubtitleIndicator);
        imageNote = findViewById(R.id.imageNote);

        textDateTime.setText(
                new SimpleDateFormat("EEEE, dd MMMM yyyy HH:mm a", Locale.getDefault()).format(new Date())
        );
    }

    /**
     * Configures the save button behavior.
     */
    private void setupSaveButton() {
        ImageView imageSave = findViewById(R.id.imageSave);
        imageSave.setOnClickListener(v -> saveNote());
    }

    /**
     * Handles saving a new note to the database.
     */
    private void saveNote() {
        if (!validateFields()) return;

        Note note = createNote();
        saveNoteToDatabase(note);
    }

    /**
     * Validates the input fields before saving.
     * @return true if fields are valid, false otherwise.
     */
    private boolean validateFields() {
        if (inputNoteTitle.getText().toString().trim().isEmpty()) {
            showToast("Note title can't be empty!");
            return false;
        }
        if (inputNoteSubtitle.getText().toString().trim().isEmpty() &&
                inputNoteText.getText().toString().trim().isEmpty()) {
            showToast("Note can't be empty!");
            return false;
        }
        return true;
    }

    /**
     * Creates a note object based on user input.
     * @return A new Note object.
     */
    private Note createNote() {
        return new Note.Builder()
                .setTitle(inputNoteTitle.getText().toString())
                .setSubtitle(inputNoteSubtitle.getText().toString())
                .setNoteText(inputNoteText.getText().toString())
                .setDateTime(textDateTime.getText().toString())
                .setColor(selectedNoteColor)
                .setImagePath(selectedImagePath)
                .build();
    }

    /**
     * Saves the note to the database.
     * @param note The note to save.
     */
    private void saveNoteToDatabase(Note note) {
        NoteComponent decoratedNote = NoteDecoratorFactory.getDecorator(note);
        note.setColor(decoratedNote.getColor());

        executor.execute(() -> {
            NotesDatabase.getDataBase(this).noteDao().insertNote(note);
            handler.post(() -> {
                setResult(RESULT_OK);
                finish();
            });
        });
    }

    /**
     * Configures miscellaneous options for notes.
     */
    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    private void setupMiscellaneous() {
        LinearLayout layoutMiscellaneous = findViewById(R.id.layoutMiscellaneous);
        BottomSheetBehavior<LinearLayout> bottomSheetBehavior = BottomSheetBehavior.from(layoutMiscellaneous);

        layoutMiscellaneous.findViewById(R.id.textMiscellaneous).setOnClickListener(v -> toggleBottomSheetState(bottomSheetBehavior));

        setupColorOptions(layoutMiscellaneous);
        setupImageSelection(layoutMiscellaneous, bottomSheetBehavior);
    }

    /**
     * Toggles the state of the bottom sheet.
     */
    private void toggleBottomSheetState(BottomSheetBehavior<LinearLayout> bottomSheetBehavior) {
        int newState = (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) ?
                BottomSheetBehavior.STATE_COLLAPSED : BottomSheetBehavior.STATE_EXPANDED;
        bottomSheetBehavior.setState(newState);
    }

    /**
     * Configures color selection options for notes.
     */
    private void setupColorOptions(LinearLayout layoutMiscellaneous) {
        Map<Integer, Pair<String, Integer>> colorMap = getColorMap();

        for (Map.Entry<Integer, Pair<String, Integer>> entry : colorMap.entrySet()) {
            View colorView = layoutMiscellaneous.findViewById(entry.getKey());
            ImageView imageView = layoutMiscellaneous.findViewById(entry.getValue().second);

            colorView.setOnClickListener(v -> updateSelectedColor(entry.getValue().first, imageView, colorMap));
        }
    }

    private Map<Integer, Pair<String, Integer>> getColorMap() {
        Map<Integer, Pair<String, Integer>> colorMap = new HashMap<>();
        colorMap.put(R.id.viewColor1, new Pair<>("#333333", R.id.imageColor1));
        colorMap.put(R.id.viewColor2, new Pair<>("#FDBE3B", R.id.imageColor2));
        colorMap.put(R.id.viewColor3, new Pair<>("#FF4842", R.id.imageColor3));
        colorMap.put(R.id.viewColor4, new Pair<>("#3A52Fc", R.id.imageColor4));
        colorMap.put(R.id.viewColor5, new Pair<>("#17C51E", R.id.imageColor5));
        return colorMap;
    }

    /**
     * Updates the selected color and refreshes UI.
     */
    private void updateSelectedColor(String color, ImageView selectedImageView, Map<Integer, Pair<String, Integer>> colorMap) {
        selectedNoteColor = color;

        colorMap.values().forEach(pair -> {
            ImageView imageView = findViewById(pair.second);
            imageView.setImageResource(0);
        });

        selectedImageView.setImageResource(R.drawable.ic_done);
        setSubtitleIndicatorColor();
    }

    /**
     * Configures the image selection option.
     */
    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    private void setupImageSelection(LinearLayout layoutMiscellaneous, BottomSheetBehavior<LinearLayout> bottomSheetBehavior) {
        layoutMiscellaneous.findViewById(R.id.layoutAddImage).setOnClickListener(v -> {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED) {
                selectImage();
            } else {
                requestPermissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES);
            }
        });
    }

    /**
     * Updates the color of the subtitle indicator.
     */
    private void setSubtitleIndicatorColor() {
        GradientDrawable gradientDrawable = (GradientDrawable) viewSubtitleIndicator.getBackground();
        gradientDrawable.setColor(Color.parseColor(selectedNoteColor));
    }

    /**
     * Opens the image picker.
     */
    private void selectImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        selectImageLauncher.launch(intent);
    }

    /**
     * Handles the result of the image selection.
     */
    private void handleImageSelectionResult(ActivityResult result) {
        if (result.getResultCode() == RESULT_OK && result.getData() != null) {
            Uri selectedImageUri = result.getData().getData();
            displaySelectedImage(selectedImageUri);
        }
    }

    private void displaySelectedImage(Uri selectedImageUri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(selectedImageUri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            imageNote.setImageBitmap(bitmap);
            imageNote.setVisibility(View.VISIBLE);
            selectedImagePath = getPathFromUri(selectedImageUri);
        } catch (Exception e) {
            showToast(e.getMessage());
        }
    }

    /**
     * Converts a URI to a file path.
     */
    private String getPathFromUri(Uri contentUri) {
        try (Cursor cursor = getContentResolver().query(contentUri, null, null, null, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                int index = cursor.getColumnIndex("data");
                return cursor.getString(index);
            }
        }
        return contentUri.getPath();
    }

    /**
     * Handles the result of the permission request.
     */
    private void handlePermissionResult(boolean isGranted) {
        if (isGranted) {
            selectImage();
        } else {
            showToast("Permission Denied!");
        }
    }

    /**
     * Displays a toast message.
     */
    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}