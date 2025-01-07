package com.android.mynotes.ui.managers;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.core.content.ContextCompat;

import java.io.InputStream;

/**
 * Manages image selection and display within an Activity.
 */
public class ImageManager {

    private final Activity activity;
    private final ImageView imageNote;
    private String selectedImagePath;
    private final String TAG = "ImageManager";

    // Launchers for permission and image selection
    private final ActivityResultLauncher<String> requestPermissionLauncher;
    private final ActivityResultLauncher<Intent> selectImageLauncher;

    /**
     * Constructs an ImageManager.
     *
     * @param activity                 The Activity instance where this manager is used.
     * @param imageNote                The ImageView where the selected image will be displayed.
     * @param requestPermissionLauncher Launcher responsible for handling permission requests.
     * @param selectImageLauncher      Launcher responsible for selecting an image from the gallery.
     */
    public ImageManager(
            Activity activity,
            ImageView imageNote,
            ActivityResultLauncher<String> requestPermissionLauncher,
            ActivityResultLauncher<Intent> selectImageLauncher
    ) {
        this.activity = activity;
        this.imageNote = imageNote;
        this.requestPermissionLauncher = requestPermissionLauncher;
        this.selectImageLauncher = selectImageLauncher;
    }

    /**
     * Requests permission to select an image, then opens the image picker if granted.
     * It checks different permissions based on the Android version.
     */
    public void requestImageSelection() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_MEDIA_IMAGES)
                    == PackageManager.PERMISSION_GRANTED) {
                selectImage();
            } else {
                requestPermissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES);
            }
        } else {
            // For older versions, use READ_EXTERNAL_STORAGE
            if (ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                selectImage();
            } else {
                requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
            }
        }
    }

    /**
     * Opens the device's gallery to select an image.
     */
    private void selectImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        selectImageLauncher.launch(intent);
    }

    /**
     * Handles the result after an image has been selected from the gallery.
     *
     * @param data The Intent data containing the selected image URI.
     */
    public void handleImageResult(Intent data) {
        if (data != null) {
            Uri selectedImageUri = data.getData();
            if (selectedImageUri != null) {
                displaySelectedImage(selectedImageUri);
            }
        }
    }

    /**
     * Displays the selected image in the associated ImageView and stores its path.
     *
     * @param selectedImageUri The URI of the selected image.
     */
    private void displaySelectedImage(Uri selectedImageUri) {
        try {
            ContentResolver contentResolver = activity.getContentResolver();
            InputStream inputStream = contentResolver.openInputStream(selectedImageUri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            imageNote.setImageBitmap(bitmap);
            imageNote.setVisibility(ImageView.VISIBLE);
            selectedImagePath = getPathFromUri(selectedImageUri);
        } catch (Exception e) {
            Toast.makeText(activity, e.getMessage(), Toast.LENGTH_SHORT).show();
            Log.e(TAG, "displaySelectedImage error", e);
        }
    }

    /**
     * Retrieves the filesystem path from the given URI.
     *
     * @param uri The URI to be converted to a file path.
     * @return The file path, or the URI string if not found.
     */
    private String getPathFromUri(Uri uri) {
        if (uri == null) return null;

        try (Cursor cursor = activity.getContentResolver().query(uri, null, null, null, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                int columnIndex = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
                if (columnIndex != -1) {
                    return cursor.getString(columnIndex);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting file path from URI", e);
        }
        return uri.toString();
    }

    /**
     * Removes the currently selected image from the ImageView and resets its path.
     */
    public void removeImage() {
        imageNote.setImageBitmap(null);
        imageNote.setVisibility(ImageView.GONE);
        selectedImagePath = null;
    }

    /**
     * Returns the path of the currently selected image.
     *
     * @return The selected image path, or null if none is selected.
     */
    public String getSelectedImagePath() {
        return selectedImagePath;
    }

    /**
     * Sets the path of the currently selected image.
     *
     * @param selectedImagePath The desired image path.
     */
    public void setSelectedImagePath(String selectedImagePath) {
        this.selectedImagePath = selectedImagePath;
    }
}