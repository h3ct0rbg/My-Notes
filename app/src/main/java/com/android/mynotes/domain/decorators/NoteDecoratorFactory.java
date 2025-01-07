package com.android.mynotes.domain.decorators;

import com.android.mynotes.domain.entities.Note;

import java.util.HashMap;
import java.util.Map;

/**
 * Factory class for creating a note decorator based on a note's color.
 * Looks up the appropriate decorator class from a predefined map
 * and instantiates it via reflection.
 */
public class NoteDecoratorFactory {

    /**
     * Maps color codes to their corresponding {@link NoteDecorator} classes.
     */
    private static final Map<String, Class<? extends NoteDecorator>> decoratorMap = new HashMap<>();

    static {
        // Default
        decoratorMap.put("#333333", DefaultNoteDecorator.class);
        // Yellow
        decoratorMap.put("#FDBE3B", YellowNoteDecorator.class);
        // Red
        decoratorMap.put("#FF4842", RedNoteDecorator.class);
        // Blue
        decoratorMap.put("#3A52Fc", BlueNoteDecorator.class);
        // Green
        decoratorMap.put("#17C51E", GreenNoteDecorator.class);
        // Purple
        decoratorMap.put("#AF00FF", PurpleNoteDecorator.class);
    }

    /**
     * Creates a {@link NoteComponent} decorator based on the note's color code.
     * If the color is not recognized or is null, a {@link DefaultNoteDecorator} is used.
     *
     * @param note The {@link Note} whose color determines the decorator.
     * @return A {@link NoteComponent} with the appropriate decorator applied.
     * @throws RuntimeException if an error occurs while creating the decorator instance.
     */
    public static NoteComponent getDecorator(Note note) {
        String color = note.getColor() != null ? note.getColor().trim() : "#333333";
        Class<? extends NoteDecorator> decoratorClass = decoratorMap.getOrDefault(color, DefaultNoteDecorator.class);

        try {
            // Use reflection to create an instance of the decorator class
            assert decoratorClass != null;
            return decoratorClass.getConstructor(NoteComponent.class).newInstance(note);
        } catch (Exception e) {
            throw new RuntimeException("Error creating NoteDecorator for color: " + color, e);
        }
    }
}