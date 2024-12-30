package com.android.mynotes.decorators;

public class YellowNoteDecorator extends NoteDecorator {
    public YellowNoteDecorator(NoteComponent noteComponent) {
        super(noteComponent);
    }

    @Override
    public String getColor() {
        return "#FDBE3B"; // Yellow color
    }
}