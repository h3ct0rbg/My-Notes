package com.android.mynotes.domain.decorators;

/**
 * A concrete decorator that applies a green color to a note.
 * Extends {@link NoteDecorator} to add the color functionality.
 */
public class GreenNoteDecorator extends NoteDecorator {

    /**
     * Constructs a GreenNoteDecorator for the specified {@link NoteComponent}.
     *
     * @param noteComponent The NoteComponent to be decorated with a green color.
     */
    public GreenNoteDecorator(NoteComponent noteComponent) {
        super(noteComponent);
    }

    /**
     * Returns the color associated with this decorator.
     *
     * @return A string representing the green color code.
     */
    @Override
    public String getColor() {
        return "#17C51E"; // Green color
    }
}