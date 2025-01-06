package com.android.mynotes.domain.decorators;

public interface NoteComponent {
    int getId();
    void setId(int id);

    String getTitle();
    void setTitle(String title);

    String getDateTime();
    void setDateTime(String dateTime);

    String getSubtitle();
    void setSubtitle(String subtitle);

    String getNoteText();
    void setNoteText(String noteText);

    String getImagePath();
    void setImagePath(String imagePath);

    String getColor();
    void setColor(String color);

    String getWebLink();
    void setWebLink(String webLink);
}