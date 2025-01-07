package com.android.mynotes.domain.decorators;

/**
 * A concrete decorator that applies a blue color to a note.
 * Extends {@link NoteDecorator} to add the color functionality.
 */
public class BlueNoteDecorator extends NoteDecorator {

    /**
     * Constructs a BlueNoteDecorator for the specified {@link NoteComponent}.
     *
     * @param noteComponent The NoteComponent to be decorated with a blue color.
     */
    public BlueNoteDecorator(NoteComponent noteComponent) {
        super(noteComponent);
    }

    /**
     * Returns the color associated with this decorator.
     *
     * @return A string representing the blue color code.
     */
    @Override
    public String getColor() {
        return "#3A52Fc"; // Blue color
    }
}