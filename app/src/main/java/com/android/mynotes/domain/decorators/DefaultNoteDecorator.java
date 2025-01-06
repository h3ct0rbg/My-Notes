package com.android.mynotes.domain.decorators;

public class DefaultNoteDecorator extends NoteDecorator {
    public DefaultNoteDecorator(NoteComponent noteComponent) {
        super(noteComponent);
    }

    @Override
    public String getColor() {
        return "#333333"; // Default color
    }
}