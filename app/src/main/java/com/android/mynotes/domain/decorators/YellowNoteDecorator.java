package com.android.mynotes.domain.decorators;

/**
 * A concrete decorator that applies a yellow color to a note.
 * Extends {@link NoteDecorator} to add the color functionality.
 */
public class YellowNoteDecorator extends NoteDecorator {

    /**
     * Constructs a YellowNoteDecorator for the specified {@link NoteComponent}.
     *
     * @param noteComponent The NoteComponent to be decorated with a yellow color.
     */
    public YellowNoteDecorator(NoteComponent noteComponent) {
        super(noteComponent);
    }

    /**
     * Returns the color associated with this decorator.
     *
     * @return A string representing the yellow color code.
     */
    @Override
    public String getColor() {
        return "#FDBE3B"; // Yellow color
    }
}