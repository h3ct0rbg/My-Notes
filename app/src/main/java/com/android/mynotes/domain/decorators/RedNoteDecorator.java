package com.android.mynotes.domain.decorators;

/**
 * A concrete decorator that applies a red color to a note.
 * Extends {@link NoteDecorator} to add the color functionality.
 */
public class RedNoteDecorator extends NoteDecorator {

    /**
     * Constructs a RedNoteDecorator for the specified {@link NoteComponent}.
     *
     * @param noteComponent The NoteComponent to be decorated with a red color.
     */
    public RedNoteDecorator(NoteComponent noteComponent) {
        super(noteComponent);
    }

    /**
     * Returns the color associated with this decorator.
     *
     * @return A string representing the red color code.
     */
    @Override
    public String getColor() {
        return "#FF4842"; // Red color
    }
}