package com.android.mynotes.domain.decorators;

import com.android.mynotes.domain.Note;

import java.util.HashMap;
import java.util.Map;

public class NoteDecoratorFactory {

    private static final Map<String, Class<? extends NoteDecorator>> decoratorMap = new HashMap<>();

    static {
        decoratorMap.put("#333333", DefaultNoteDecorator.class); // Default
        decoratorMap.put("#FDBE3B", YellowNoteDecorator.class);  // Yellow
        decoratorMap.put("#FF4842", RedNoteDecorator.class);     // Red
        decoratorMap.put("#3A52Fc", BlueNoteDecorator.class);    // Blue
        decoratorMap.put("#17C51E", GreenNoteDecorator.class);   // Green
        decoratorMap.put("#AF00FF", PurpleNoteDecorator.class);  // Purple
    }

    /**
     * Creates a NoteComponent decorator based on the note's color.
     * @param note The note for which the decorator is created.
     * @return A NoteComponent decorator for the given note.
     */
    public static NoteComponent getDecorator(Note note) {
        String color = note.getColor() != null ? note.getColor().trim() : "#333333";
        Class<? extends NoteDecorator> decoratorClass = decoratorMap.getOrDefault(color, DefaultNoteDecorator.class);

        try {
            assert decoratorClass != null;
            return decoratorClass.getConstructor(NoteComponent.class).newInstance(note);
        } catch (Exception e) {
            throw new RuntimeException("Error creating NoteDecorator for color: " + color, e);
        }
    }
}