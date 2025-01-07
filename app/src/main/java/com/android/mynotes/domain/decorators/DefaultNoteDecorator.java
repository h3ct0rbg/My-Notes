package com.android.mynotes.domain.decorators;

/**
 * A concrete decorator that applies a default color to a note.
 * Extends {@link NoteDecorator} to add the color functionality.
 */
public class DefaultNoteDecorator extends NoteDecorator {

    /**
     * Constructs a DefaultNoteDecorator for the specified {@link NoteComponent}.
     *
     * @param noteComponent The NoteComponent to be decorated with a default color.
     */
    public DefaultNoteDecorator(NoteComponent noteComponent) {
        super(noteComponent);
    }

    /**
     * Returns the color associated with this decorator.
     *
     * @return A string representing the default color code.
     */
    @Override
    public String getColor() {
        return "#333333"; // Default color
    }
}