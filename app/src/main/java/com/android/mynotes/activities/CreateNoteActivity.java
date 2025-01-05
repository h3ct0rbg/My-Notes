package com.android.mynotes.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.android.mynotes.R;
import com.android.mynotes.database.NotesDatabase;
import com.android.mynotes.decorators.*;
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
    private TextView textWebURL;
    private LinearLayout layoutWebURL;

    private NoteComponent currentNoteDecorator = new DefaultNoteDecorator(new Note());
    private String selectedImagePath;

    private AlertDialog dialogAddURL;
    private AlertDialog dialogDeleteNote;

    private Note alreadyAvailableNote;

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
        setupRemoveButtons();

        if (getIntent().getBooleanExtra("isViewOrUpdate", false)) {
            alreadyAvailableNote = (Note) getIntent().getSerializableExtra("note");
            setViewOrUpdateNote();
        }

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
        textWebURL = findViewById(R.id.textWebURL);
        layoutWebURL = findViewById(R.id.layoutWebURL);

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

    private void setupRemoveButtons() {
        // Remove Web URL
        findViewById(R.id.imageRemoveWebURL).setOnClickListener(v -> { clearWebURL(); });

        // Remove Image
        findViewById(R.id.imageRemoveImage).setOnClickListener(v -> { clearImage(); });
    }

    private void setViewOrUpdateNote() {
        inputNoteTitle.setText(alreadyAvailableNote.getTitle());
        inputNoteSubtitle.setText(alreadyAvailableNote.getSubtitle());
        inputNoteText.setText(alreadyAvailableNote.getNoteText());
        textDateTime.setText(alreadyAvailableNote.getDateTime());

        if (alreadyAvailableNote.getImagePath() != null && !alreadyAvailableNote.getImagePath().trim().isEmpty()) {
            imageNote.setImageBitmap(BitmapFactory.decodeFile(alreadyAvailableNote.getImagePath()));
            imageNote.setVisibility(View.VISIBLE);
            findViewById(R.id.imageRemoveImage).setVisibility(View.VISIBLE);
            selectedImagePath = alreadyAvailableNote.getImagePath();
        }

        if (alreadyAvailableNote.getWebLink() != null && !alreadyAvailableNote.getWebLink().trim().isEmpty()) {
            textWebURL.setText(alreadyAvailableNote.getWebLink());
            layoutWebURL.setVisibility(View.VISIBLE);
        }

        if (alreadyAvailableNote != null &&
                alreadyAvailableNote.getColor() != null &&
                !alreadyAvailableNote.getColor().trim().isEmpty()) {

            Map<Integer, Class<? extends NoteDecorator>> colorMap = getColorMap();

            for (Map.Entry<Integer, Class<? extends NoteDecorator>> entry : colorMap.entrySet()) {
                try {
                    NoteDecorator decorator = entry.getValue().getConstructor(NoteComponent.class)
                            .newInstance(new Note());
                    if (decorator.getColor().equalsIgnoreCase(alreadyAvailableNote.getColor().trim())) {
                        findViewById(entry.getKey()).performClick();
                        break;
                    }
                } catch (Exception e) {
                    Log.e("CreateNoteActivity", "Error initializing NoteDecorator", e);
                }
            }
        }
    }

    /**
     * Handles saving a new note to the database.
     */
    private void saveNote() {
        if (!validateFields()) return;

        Note note = new Note.Builder()
                    .setTitle(inputNoteTitle.getText().toString())
                    .setSubtitle(inputNoteSubtitle.getText().toString())
                    .setNoteText(inputNoteText.getText().toString())
                    .setDateTime(textDateTime.getText().toString())
                    .setColor(currentNoteDecorator.getColor())
                    .setImagePath(selectedImagePath)
                    .setWebLink(textWebURL.getText().toString())
                    .build();

        if (alreadyAvailableNote != null) {
            note.setId(alreadyAvailableNote.getId());
        }

        executor.execute(() -> {
            NotesDatabase.getDataBase(this).noteDao().insertNote(note);
            handler.post(() -> {
                setResult(RESULT_OK);
                finish();
            });
        });
    }

    /**
     * Clears the web URL and hides its layout.
     */
    private void clearWebURL() {
        textWebURL.setText(null);
        layoutWebURL.setVisibility(View.GONE);
    }

    /**
     * Clears the selected image and hides its view.
     */
    private void clearImage() {
        imageNote.setImageBitmap(null);
        imageNote.setVisibility(View.GONE);
        findViewById(R.id.imageRemoveImage).setVisibility(View.GONE);
        selectedImagePath = null;
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
     * Configures miscellaneous options for notes.
     */
    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    private void setupMiscellaneous() {
        LinearLayout layoutMiscellaneous = findViewById(R.id.layoutMiscellaneous);
        BottomSheetBehavior<LinearLayout> bottomSheetBehavior = BottomSheetBehavior.from(layoutMiscellaneous);

        layoutMiscellaneous.findViewById(R.id.textMiscellaneous).setOnClickListener(v -> toggleBottomSheetState(bottomSheetBehavior));

        setupColorOptions(layoutMiscellaneous);
        setupImageSelection(layoutMiscellaneous, bottomSheetBehavior);
        setupURL(layoutMiscellaneous, bottomSheetBehavior);
        setupDeleteNote(layoutMiscellaneous, bottomSheetBehavior);
    }

    private void showDeleteNoteDialog() {
        if (dialogDeleteNote == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(CreateNoteActivity.this);
            View view = LayoutInflater.from(this).inflate(
                    R.layout.layout_delete_note,
                    findViewById(R.id.layoutDeleteNoteContainer)
            );
            builder.setView(view);
            dialogDeleteNote = builder.create();
            if (dialogDeleteNote.getWindow() != null) {
                dialogDeleteNote.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            }

            view.findViewById(R.id.textDeleteNote).setOnClickListener(v -> {
                executor.execute(() -> {
                    NotesDatabase.getDataBase(getApplicationContext()).noteDao()
                            .deleteNote(alreadyAvailableNote);

                    handler.post(() -> {
                        Intent intent = new Intent();
                        intent.putExtra("isNoteDeleted", true);
                        setResult(RESULT_OK, intent);
                        finish();
                    });
                });
            });

            view.findViewById(R.id.textCancel).setOnClickListener(v -> dialogDeleteNote.dismiss());
        }

        dialogDeleteNote.show();
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
        Map<Integer, Class<? extends NoteDecorator>> colorMap = getColorMap();

        for (Map.Entry<Integer, Class<? extends NoteDecorator>> entry : colorMap.entrySet()) {
            View colorView = layoutMiscellaneous.findViewById(entry.getKey());

            colorView.setOnClickListener(v -> {
                try {
                    currentNoteDecorator = entry.getValue().getConstructor(NoteComponent.class).newInstance(new Note());
                    updateUIForSelectedColor(currentNoteDecorator.getColor());
                } catch (Exception e) {
                    Log.e("CreateNoteActivity", "Error initializing NoteDecorator", e);
                }
            });
        }
    }

    private Map<Integer, Class<? extends NoteDecorator>> getColorMap() {
        Map<Integer, Class<? extends NoteDecorator>> colorMap = new HashMap<>();
        colorMap.put(R.id.viewColor1, DefaultNoteDecorator.class);
        colorMap.put(R.id.viewColor2, YellowNoteDecorator.class);
        colorMap.put(R.id.viewColor3, RedNoteDecorator.class);
        colorMap.put(R.id.viewColor4, BlueNoteDecorator.class);
        colorMap.put(R.id.viewColor5, GreenNoteDecorator.class);
        return colorMap;
    }

    /**
     * Updates the UI to reflect the selected color.
     */
    private void updateUIForSelectedColor(String colorCode) {
        GradientDrawable gradientDrawable = (GradientDrawable) viewSubtitleIndicator.getBackground();
        gradientDrawable.setColor(Color.parseColor(colorCode));
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

    private void setupURL(LinearLayout layoutMiscellaneous, BottomSheetBehavior<LinearLayout> bottomSheetBehavior) {
        layoutMiscellaneous. findViewById(R.id. layoutAddUrl).setOnClickListener(v -> {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            showAddURLDialog();
        });
    }

    private void setupDeleteNote(LinearLayout layoutMiscellaneous, BottomSheetBehavior<LinearLayout> bottomSheetBehavior) {
        if (alreadyAvailableNote != null) {
            layoutMiscellaneous.findViewById(R.id.layoutDeleteNote).setVisibility(View.VISIBLE);
            layoutMiscellaneous.findViewById(R.id.layoutDeleteNote).setOnClickListener(v -> {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                showDeleteNoteDialog();
            });
            Log.d("Already Available Note", "The Already Available Note value of the note is (In): " + alreadyAvailableNote);
        }
        Log.d("Already Available Note", "The Already Available Note value of the note is (Out): " + alreadyAvailableNote);
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
            findViewById(R.id.imageRemoveImage).setVisibility(View.VISIBLE);
            selectedImagePath = getPathFromUri(selectedImageUri);
        } catch (Exception e) {
            showToast(e.getMessage());
        }
    }

    /**
     * Converts a URI to a file path.
     */
    private String getPathFromUri(Uri uri) {
        if (uri == null) return null;

        try (Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                int columnIndex = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
                if (columnIndex != -1) {
                    return cursor.getString(columnIndex);
                }
            }
        } catch (Exception e) {
            Log.e("CreateNoteActivity", "Error getting file path from URI", e);
        }

        // Fallback to using the URI path if no valid file path is found
        return uri.toString();
    }

    private void showAddURLDialog() {
        if (dialogAddURL == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(CreateNoteActivity.this);
            View view = LayoutInflater.from(this).inflate(
                    R.layout.layout_add_url,
                    (ViewGroup) findViewById(R.id.layoutAddUrlContainer)
            );
            builder.setView(view);

            dialogAddURL = builder.create();
            if (dialogAddURL.getWindow() != null) {
                dialogAddURL.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            }

            final  EditText inputURL = view.findViewById(R.id.inputURL);
            inputURL.requestFocus();

            view.findViewById(R.id.textAdd).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (inputURL.getText().toString().trim().isEmpty()) {
                        Toast.makeText(CreateNoteActivity.this, "Enter URL", Toast.LENGTH_SHORT).show();
                    } else if (!Patterns.WEB_URL.matcher(inputURL.getText().toString()).matches()) {
                        Toast.makeText(CreateNoteActivity.this, "Enter valid URL", Toast.LENGTH_SHORT).show();
                    } else {
                        textWebURL.setText(inputURL.getText().toString());
                        layoutWebURL.setVisibility(View.VISIBLE);
                        dialogAddURL.dismiss();
                    }
                }
            });

            view.findViewById(R.id.textCancel).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialogAddURL.dismiss();
                }
            });
        }
        dialogAddURL.show();
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