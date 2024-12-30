package com.android.mynotes.decorators;

public class BlueNoteDecorator extends NoteDecorator {
    public BlueNoteDecorator(NoteComponent noteComponent) {
        super(noteComponent);
    }

    @Override
    public String getColor() {
        return "#3A52Fc"; // Blue color
    }
}