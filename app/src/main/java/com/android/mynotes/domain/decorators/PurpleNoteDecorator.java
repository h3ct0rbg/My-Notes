package com.android.mynotes.domain.decorators;

public class PurpleNoteDecorator extends NoteDecorator {
    public PurpleNoteDecorator(NoteComponent noteComponent) { super(noteComponent); }

    @Override
    public String getColor() {
        return "#9C27B0"; // Purple color
    }
}