package com.android.mynotes.ui.managers;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.android.mynotes.R;
import com.android.mynotes.domain.decorators.BlueNoteDecorator;
import com.android.mynotes.domain.decorators.DefaultNoteDecorator;
import com.android.mynotes.domain.decorators.GreenNoteDecorator;
import com.android.mynotes.domain.decorators.NoteComponent;
import com.android.mynotes.domain.decorators.NoteDecorator;
import com.android.mynotes.domain.decorators.PurpleNoteDecorator;
import com.android.mynotes.domain.decorators.RedNoteDecorator;
import com.android.mynotes.domain.decorators.YellowNoteDecorator;
import com.android.mynotes.domain.entities.Note;

import java.util.HashMap;
import java.util.Map;

/**
 * Manages the color selection logic by mapping different color views to their
 * corresponding NoteDecorator implementations.
 */
public class ColorManager {

    private static final String TAG = "ColorManager";

    private final View viewSubtitleIndicator;
    private NoteComponent currentNoteDecorator;

    /**
     * Maps the ID of each color view to the corresponding NoteDecorator class.
     */
    private final Map<Integer, Class<? extends NoteDecorator>> colorDecoratorMap = new HashMap<>();

    /**
     * Constructs a ColorManager.
     *
     * @param viewSubtitleIndicator The View used to display the selected color indicator (e.g., a subtitle bar).
     */
    public ColorManager(View viewSubtitleIndicator) {
        this.viewSubtitleIndicator = viewSubtitleIndicator;
        setupColorDecoratorMap();
    }

    /**
     * Populates the colorDecoratorMap with default associations between
     * view IDs and NoteDecorator classes.
     */
    private void setupColorDecoratorMap() {
        colorDecoratorMap.put(R.id.viewColor1, DefaultNoteDecorator.class);
        colorDecoratorMap.put(R.id.viewColor2, YellowNoteDecorator.class);
        colorDecoratorMap.put(R.id.viewColor3, RedNoteDecorator.class);
        colorDecoratorMap.put(R.id.viewColor4, BlueNoteDecorator.class);
        colorDecoratorMap.put(R.id.viewColor5, GreenNoteDecorator.class);
        colorDecoratorMap.put(R.id.viewColor6, PurpleNoteDecorator.class);
    }

    /**
     * Sets up click listeners for each color option in a given layout.
     * When a color view is clicked, it initializes the corresponding
     * NoteDecorator and updates the UI with the selected color.
     *
     * @param layoutMiscellaneous The LinearLayout containing the color views.
     */
    public void setupColorOptions(LinearLayout layoutMiscellaneous) {
        for (Map.Entry<Integer, Class<? extends NoteDecorator>> entry : colorDecoratorMap.entrySet()) {
            View colorView = layoutMiscellaneous.findViewById(entry.getKey());
            if (colorView != null) {
                colorView.setOnClickListener(v -> {
                    try {
                        NoteComponent noteComponent = new Note();
                        currentNoteDecorator =
                                entry.getValue().getConstructor(NoteComponent.class)
                                        .newInstance(noteComponent);
                        updateUIForSelectedColor(currentNoteDecorator.getColor());
                    } catch (Exception e) {
                        Log.e(TAG, "Error initializing NoteDecorator", e);
                    }
                });
            }
        }
    }

    /**
     * Updates the UI to reflect the currently selected color by modifying
     * the background of viewSubtitleIndicator.
     *
     * @param colorCode The color code (e.g., "#FFFFFF") to apply.
     */
    public void updateUIForSelectedColor(String colorCode) {
        GradientDrawable gradientDrawable = (GradientDrawable) viewSubtitleIndicator.getBackground();
        gradientDrawable.setColor(Color.parseColor(colorCode));
    }

    /**
     * Selects a color by programmatically simulating a click on the associated view.
     * This method is typically used to restore a previously chosen color (e.g., when
     * editing an existing note).
     *
     * @param colorCode           The color code to select.
     * @param layoutMiscellaneous The LinearLayout containing the color options.
     */
    public void selectColor(String colorCode, LinearLayout layoutMiscellaneous) {
        for (Map.Entry<Integer, Class<? extends NoteDecorator>> entry : colorDecoratorMap.entrySet()) {
            try {
                NoteDecorator decorator =
                        entry.getValue().getConstructor(NoteComponent.class).newInstance(new Note());
                if (decorator.getColor().equalsIgnoreCase(colorCode.trim())) {
                    layoutMiscellaneous.findViewById(entry.getKey()).performClick();
                    break;
                }
            } catch (Exception e) {
                Log.e(TAG, "Error selecting color decorator", e);
            }
        }
    }

    /**
     * Returns the currently selected NoteDecorator.
     *
     * @return The current NoteDecorator instance, or null if none is selected.
     */
    public NoteComponent getCurrentNoteDecorator() {
        return currentNoteDecorator;
    }

    /**
     * Sets the current NoteDecorator.
     *
     * @param currentNoteDecorator The NoteDecorator instance to be set.
     */
    public void setCurrentNoteDecorator(NoteComponent currentNoteDecorator) {
        this.currentNoteDecorator = currentNoteDecorator;
    }
}