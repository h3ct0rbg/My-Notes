package com.android.mynotes.ui.facade;

import android.app.Activity;
import android.content.Intent;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;

import com.android.mynotes.ui.managers.ColorManager;
import com.android.mynotes.ui.managers.ImageManager;
import com.android.mynotes.ui.managers.UrlManager;

/**
 * Facade that unifies ColorManager, ImageManager and UrlManager operations.
 */
public class NoteFacade {

    private final ColorManager colorManager;
    private final ImageManager imageManager;
    private final UrlManager urlManager;

    /**
     * Constructor that injects all the necessary dependencies for the managers.
     *
     * @param viewSubtitleIndicator View used by ColorManager to indicate the color.
     * @param activity Activity where it will be executed (necessary for ImageManager and UrlManager).
     * @param requestPermissionLauncher Launcher for permission request (ImageManager).
     * @param selectImageLauncher Launcher to select the image (ImageManager).
     * @param imageNote ImageView where the image will be displayed (ImageManager).
     * @param textWebURL TextView where the URL will be displayed (UrlManager).
     * @param layoutWebURL Layout containing the URL view (UrlManager).
     */
    public NoteFacade(
            android.view.View viewSubtitleIndicator,
            Activity activity,
            ActivityResultLauncher<String> requestPermissionLauncher,
            ActivityResultLauncher<Intent> selectImageLauncher,
            ImageView imageNote,
            TextView textWebURL,
            LinearLayout layoutWebURL
    ) {
        this.colorManager = new ColorManager(viewSubtitleIndicator);
        this.imageManager = new ImageManager(activity, imageNote, requestPermissionLauncher, selectImageLauncher);
        this.urlManager = new UrlManager(activity, textWebURL, layoutWebURL);
    }

    /* ==========================================================
     * COLOR RELATED METHODS
     * ========================================================== */

    public void setupColorOptions(LinearLayout layoutMiscellaneous) {
        colorManager.setupColorOptions(layoutMiscellaneous);
    }

    public void updateUIForSelectedColor(String colorCode) {
        colorManager.updateUIForSelectedColor(colorCode);
    }

    public void selectColor(String colorCode, LinearLayout layoutMiscellaneous) {
        colorManager.selectColor(colorCode, layoutMiscellaneous);
    }

    public String getSelectedColor() {
        return (colorManager.getCurrentNoteDecorator() != null)
                ? colorManager.getCurrentNoteDecorator().getColor()
                : null;
    }

    /* ==========================================================
     * IMAGE RELATED METHODS
     * ========================================================== */

    public void requestImageSelection() {
        imageManager.requestImageSelection();
    }

    public void handleImageResult(Intent data) {
        imageManager.handleImageResult(data);
    }

    public void removeImage() {
        imageManager.removeImage();
    }

    public String getSelectedImagePath() {
        return imageManager.getSelectedImagePath();
    }

    public void setSelectedImagePath(String path) {
        imageManager.setSelectedImagePath(path);
    }

    /* ==========================================================
     * URL RELATED METHODS
     * ========================================================== */

    public void showAddURLDialog() {
        urlManager.showAddURLDialog();
    }

    public void removeURL() {
        urlManager.removeURL();
    }

    public String getWebURL() {
        return urlManager.getWebURL();
    }

    public void setWebURL(String webURL) {
        urlManager.setWebURL(webURL);
    }
}