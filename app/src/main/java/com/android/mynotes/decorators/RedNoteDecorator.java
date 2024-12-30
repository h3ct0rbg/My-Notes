package com.android.mynotes.decorators;

public class RedNoteDecorator extends NoteDecorator {
    public RedNoteDecorator(NoteComponent noteComponent) {
        super(noteComponent);
    }

    @Override
    public String getColor() {
        return "#FF4842"; // Red color
    }
}