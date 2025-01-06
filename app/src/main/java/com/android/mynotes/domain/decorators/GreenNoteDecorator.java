package com.android.mynotes.domain.decorators;

public class GreenNoteDecorator extends NoteDecorator {
    public GreenNoteDecorator(NoteComponent noteComponent) {
        super(noteComponent);
    }

    @Override
    public String getColor() {
        return "#17C51E"; // Green color
    }
}