package com.android.mynotes.domain.decorators;

public class PurpleNoteDecorator extends NoteDecorator {
    public PurpleNoteDecorator(NoteComponent noteComponent) { super(noteComponent); }

    @Override
    public String getColor() {
        return "#AF00FF"; // Purple color
    }
}