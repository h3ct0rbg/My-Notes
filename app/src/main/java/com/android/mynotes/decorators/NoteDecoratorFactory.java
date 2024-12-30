package com.android.mynotes.decorators;

import com.android.mynotes.entities.Note;

public class NoteDecoratorFactory {

    public static NoteComponent getDecorator(Note note) {
        switch (note.getColor() != null ? note.getColor().trim() : "") {
            case "#FDBE3B": // Yellow
                return new YellowNoteDecorator(note);
            case "#FF4842": // Red
                return new RedNoteDecorator(note);
            case "#3A52Fc": // Blue
                return new BlueNoteDecorator(note);
            case "#17C51E": // Green
                return new GreenNoteDecorator(note);
            default:        // Default
                return new DefaultNoteDecorator(note);
        }
    }
}