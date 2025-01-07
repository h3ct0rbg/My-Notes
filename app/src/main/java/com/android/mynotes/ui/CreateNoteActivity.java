package com.android.mynotes.ui;

// Standard Android libraries
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

// Android support libraries
import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

// External dependencies
import com.android.mynotes.R;
import com.android.mynotes.data.NotesDatabase;
import com.android.mynotes.data.NotesRepository;
import com.android.mynotes.domain.entities.Note;
import com.android.mynotes.domain.viewmodels.NotesViewModel;
import com.android.mynotes.ui.managers.ColorManager;
import com.android.mynotes.ui.managers.ImageManager;
import com.android.mynotes.ui.managers.UrlManager;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

// Java libraries
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Activity responsible for creating and editing notes.
 */
public class CreateNoteActivity extends AppCompatActivity {

    // UI variables
    private EditText inputNoteTitle, inputNoteSubtitle, inputNoteText;
    private TextView textDateTime, textWebURL;
    private View viewSubtitleIndicator;
    private ImageView imageNote;
    private LinearLayout layoutWebURL;

    // Managers/Helpers
    private ColorManager colorManager;
    private ImageManager imageManager;
    private UrlManager urlManager;

    // State
    private Note alreadyAvailableNote;

    // ViewModel
    private NotesViewModel notesViewModel;

    // AlertDialogs
    private AlertDialog dialogDeleteNote;

    // Activity Launchers (for permissions and image selection)
    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.RequestPermission(),
                    isGranted -> {
                        if (isGranted) {
                            imageManager.requestImageSelection();
                        } else {
                            showToast("Permission Denied!");
                        }
                    }
            );

    private final ActivityResultLauncher<Intent> selectImageLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                            imageManager.handleImageResult(result.getData());
                            findViewById(R.id.imageRemoveImage).setVisibility(View.VISIBLE);
                        }
                    }
            );

    /**
     * Called when the activity is first created.
     * Initializes ViewModel, UI, and checks if it's editing an existing note.
     *
     * @param savedInstanceState The previously saved state of this activity.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_note);

        initializeViewModel();
        initializeUI();

        // Check if we are viewing/updating an existing note
        if (getIntent().getBooleanExtra("isViewOrUpdate", false)) {
            alreadyAvailableNote = (Note) getIntent().getSerializableExtra("note");
            setViewOrUpdateNote();
        }
    }

    /**
     * Initializes the ViewModel used for managing notes.
     */
    private void initializeViewModel() {
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
    }

    /**
     * Initializes all UI components, including managers for color, image, and URL handling.
     */
    private void initializeUI() {
        setupBackNavigation();
        setupSaveButton();

        // View references
        inputNoteTitle = findViewById(R.id.inputNoteTitle);
        inputNoteSubtitle = findViewById(R.id.inputNoteSubtitle);
        inputNoteText = findViewById(R.id.inputNote);
        textDateTime = findViewById(R.id.textDateTime);
        viewSubtitleIndicator = findViewById(R.id.viewSubtitleIndicator);
        imageNote = findViewById(R.id.imageNote);
        textWebURL = findViewById(R.id.textWebURL);
        layoutWebURL = findViewById(R.id.layoutWebURL);

        // Initialize Managers
        // Note: The constructor for ColorManager was modified to accept only a View instead of (Context, View)
        //       for illustrative purposes, ensure it aligns with your actual implementation.
        colorManager = new ColorManager(viewSubtitleIndicator);
        imageManager = new ImageManager(this, imageNote, requestPermissionLauncher, selectImageLauncher);
        urlManager = new UrlManager(this, textWebURL, layoutWebURL);

        // Remove URL and Image functionality
        findViewById(R.id.imageRemoveWebURL).setOnClickListener(v -> urlManager.removeURL());
        findViewById(R.id.imageRemoveImage).setOnClickListener(v -> {
            imageManager.removeImage();
            findViewById(R.id.imageRemoveImage).setVisibility(View.GONE);
        });

        // Set current date/time in the TextView
        textDateTime.setText(
                new SimpleDateFormat("EEEE, dd MMMM yyyy HH:mm a", Locale.getDefault()).format(new Date())
        );

        // Only set up miscellaneous UI (BottomSheet) if on Android 13+ (TIRAMISU)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            setupMiscellaneous();
        }

        // If editing an existing note, enable the "Delete Note" option
        if (getIntent().getBooleanExtra("isViewOrUpdate", false)) {
            alreadyAvailableNote = (Note) getIntent().getSerializableExtra("note");
            setViewOrUpdateNote();
            setupDeleteNoteButton();
        }
    }

    /**
     * Sets up the navigation behavior for the Back button.
     * Finishes the activity when the user presses back.
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
     * Sets up the behavior for the Save button.
     * When clicked, it attempts to save the note.
     */
    private void setupSaveButton() {
        ImageView imageSave = findViewById(R.id.imageSave);
        imageSave.setOnClickListener(v -> saveNote());
    }

    /**
     * Sets up various miscellaneous options (color selection, image addition, URL addition)
     * by binding them to a BottomSheet.
     */
    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    private void setupMiscellaneous() {
        LinearLayout layoutMiscellaneous = findViewById(R.id.layoutMiscellaneous);
        BottomSheetBehavior<LinearLayout> bottomSheetBehavior = BottomSheetBehavior.from(layoutMiscellaneous);

        // Toggle the BottomSheet when the text is clicked
        layoutMiscellaneous.findViewById(R.id.textMiscellaneous).setOnClickListener(v -> {
            int newState = (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) ?
                    BottomSheetBehavior.STATE_COLLAPSED : BottomSheetBehavior.STATE_EXPANDED;
            bottomSheetBehavior.setState(newState);
        });

        // Set up color options
        colorManager.setupColorOptions(layoutMiscellaneous);

        // Set up image addition
        layoutMiscellaneous.findViewById(R.id.layoutAddImage).setOnClickListener(v -> {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            imageManager.requestImageSelection();
        });

        // Set up URL addition
        layoutMiscellaneous.findViewById(R.id.layoutAddUrl).setOnClickListener(v -> {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            urlManager.showAddURLDialog();
        });
    }

    /**
     * If the note is being edited, this configures the BottomSheet to show
     * a "Delete Note" option.
     */
    private void setupDeleteNoteButton() {
        LinearLayout layoutMiscellaneous = findViewById(R.id.layoutMiscellaneous);
        BottomSheetBehavior<LinearLayout> bottomSheetBehavior = BottomSheetBehavior.from(layoutMiscellaneous);

        if (alreadyAvailableNote != null) {
            View deleteNoteOption = layoutMiscellaneous.findViewById(R.id.layoutDeleteNote);
            deleteNoteOption.setVisibility(View.VISIBLE);
            deleteNoteOption.setOnClickListener(v -> {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                showDeleteNoteDialog();
            });
        }
    }

    /**
     * Displays a dialog to confirm note deletion.
     * If confirmed, the note is deleted via the ViewModel and the Activity finishes.
     */
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
                notesViewModel.deleteNoteCommand(alreadyAvailableNote);
                dialogDeleteNote.dismiss();
                finish();
            });

            view.findViewById(R.id.textCancel).setOnClickListener(v -> dialogDeleteNote.dismiss());
        }
        dialogDeleteNote.show();
    }

    /**
     * Populates the UI with the content of an existing note if the user is editing.
     * This includes text, image, URL, and color.
     */
    private void setViewOrUpdateNote() {
        inputNoteTitle.setText(alreadyAvailableNote.getTitle());
        inputNoteSubtitle.setText(alreadyAvailableNote.getSubtitle());
        inputNoteText.setText(alreadyAvailableNote.getNoteText());
        textDateTime.setText(alreadyAvailableNote.getDateTime());

        // Load image if it exists
        if (alreadyAvailableNote.getImagePath() != null &&
                !alreadyAvailableNote.getImagePath().trim().isEmpty()) {
            Bitmap bitmap = BitmapFactory.decodeFile(alreadyAvailableNote.getImagePath());
            imageNote.setImageBitmap(bitmap);
            imageNote.setVisibility(View.VISIBLE);
            findViewById(R.id.imageRemoveImage).setVisibility(View.VISIBLE);
            imageManager.setSelectedImagePath(alreadyAvailableNote.getImagePath());
        }

        // Load URL if it exists
        if (alreadyAvailableNote.getWebLink() != null &&
                !alreadyAvailableNote.getWebLink().trim().isEmpty()) {
            urlManager.setWebURL(alreadyAvailableNote.getWebLink());
        }

        // Load color if it exists
        if (alreadyAvailableNote.getColor() != null &&
                !alreadyAvailableNote.getColor().trim().isEmpty()) {
            // Update the UI immediately
            colorManager.updateUIForSelectedColor(alreadyAvailableNote.getColor());
            // Programmatically simulate a click to set the color
            LinearLayout layoutMiscellaneous = findViewById(R.id.layoutMiscellaneous);
            colorManager.selectColor(alreadyAvailableNote.getColor(), layoutMiscellaneous);
        }
    }

    /**
     * Saves the note. If editing an existing note, it updates it; otherwise, it creates a new one.
     * After saving, the Activity finishes.
     */
    private void saveNote() {
        if (!validateFields()) return;

        Note note = new Note.Builder()
                .setTitle(inputNoteTitle.getText().toString())
                .setSubtitle(inputNoteSubtitle.getText().toString())
                .setNoteText(inputNoteText.getText().toString())
                .setDateTime(textDateTime.getText().toString())
                .setColor(colorManager.getCurrentNoteDecorator() != null
                        ? colorManager.getCurrentNoteDecorator().getColor()
                        : null)
                .setImagePath(imageManager.getSelectedImagePath())  // Image handling
                .setWebLink(urlManager.getWebURL())                 // URL handling
                .build();

        if (alreadyAvailableNote != null) {
            note.setId(alreadyAvailableNote.getId());
            notesViewModel.editNoteCommand(alreadyAvailableNote, note);
        } else {
            notesViewModel.addNoteCommand(note);
        }
        finish();
    }

    /**
     * Validates that the required fields are not empty.
     *
     * @return true if the note can be saved, false otherwise.
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
     * Shows a short Toast message.
     *
     * @param message The message to be displayed in the Toast.
     */
    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}