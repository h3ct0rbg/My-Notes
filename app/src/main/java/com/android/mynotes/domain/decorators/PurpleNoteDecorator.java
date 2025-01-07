package com.android.mynotes.domain.decorators;

/**
 * A concrete decorator that applies a purple color to a note.
 * Extends {@link NoteDecorator} to add the color functionality.
 */
public class PurpleNoteDecorator extends NoteDecorator {

    /**
     * Constructs a PurpleNoteDecorator for the specified {@link NoteComponent}.
     *
     * @param noteComponent The NoteComponent to be decorated with a purple color.
     */
    public PurpleNoteDecorator(NoteComponent noteComponent) {
        super(noteComponent);
    }

    /**
     * Returns the color associated with this decorator.
     *
     * @return A string representing the purple color code.
     */
    @Override
    public String getColor() {
        return "#AF00FF"; // Purple color
    }
}