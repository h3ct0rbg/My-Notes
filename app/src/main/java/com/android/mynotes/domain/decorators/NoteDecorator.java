package com.android.mynotes.domain.decorators;

public class NoteDecorator implements NoteComponent {
    private final NoteComponent noteComponent;

    public NoteDecorator(NoteComponent noteComponent) {
        this.noteComponent = noteComponent;
    }

    @Override
    public int getId() {
        return noteComponent.getId();
    }

    @Override
    public void setId(int id) {}

    @Override
    public String getTitle() {
        return noteComponent.getTitle();
    }

    @Override
    public void setTitle(String title) {}

    @Override
    public String getDateTime() {
        return noteComponent.getDateTime();
    }

    @Override
    public void setDateTime(String dateTime) {}

    @Override
    public String getSubtitle() {
        return noteComponent.getSubtitle();
    }

    @Override
    public void setSubtitle(String subtitle) {}

    @Override
    public String getNoteText() {
        return noteComponent.getNoteText();
    }

    @Override
    public void setNoteText(String noteText) {}

    @Override
    public String getImagePath() {
        return noteComponent.getImagePath();
    }

    @Override
    public void setImagePath(String imagePath) {}

    @Override
    public String getColor() {
        return noteComponent.getColor();
    }

    @Override
    public void setColor(String color) {

    }

    @Override
    public String getWebLink() {
        return noteComponent.getWebLink();
    }

    @Override
    public void setWebLink(String webLink) {}
}