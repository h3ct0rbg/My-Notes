package com.android.mynotes.domain.decorators;

/**
 * Defines the core properties and methods that a note component should have.
 * This interface can be decorated by various {@link NoteDecorator} implementations
 * to add additional behavior or modify existing properties.
 */
public interface NoteComponent {

    /**
     * Retrieves the unique identifier of the note.
     *
     * @return The note's ID.
     */
    int getId();

    /**
     * Sets the unique identifier for the note.
     *
     * @param id The desired ID for the note.
     */
    void setId(int id);

    /**
     * Retrieves the title of the note.
     *
     * @return The note's title.
     */
    String getTitle();

    /**
     * Sets the title of the note.
     *
     * @param title The desired title for the note.
     */
    void setTitle(String title);

    /**
     * Retrieves the date and time associated with the note.
     *
     * @return A string representing the note's date and time.
     */
    String getDateTime();

    /**
     * Sets the date and time for the note.
     *
     * @param dateTime The desired date/time string.
     */
    void setDateTime(String dateTime);

    /**
     * Retrieves the subtitle of the note.
     *
     * @return The note's subtitle.
     */
    String getSubtitle();

    /**
     * Sets the subtitle of the note.
     *
     * @param subtitle The desired subtitle for the note.
     */
    void setSubtitle(String subtitle);

    /**
     * Retrieves the main text of the note.
     *
     * @return The note's text content.
     */
    String getNoteText();

    /**
     * Sets the main text of the note.
     *
     * @param noteText The desired text content.
     */
    void setNoteText(String noteText);

    /**
     * Retrieves the file path for an image associated with the note.
     *
     * @return A string path pointing to the note's image.
     */
    String getImagePath();

    /**
     * Sets the file path for an image associated with the note.
     *
     * @param imagePath A string path for the image resource.
     */
    void setImagePath(String imagePath);

    /**
     * Retrieves the color code for the note.
     *
     * @return A string representing the note's color code (e.g., "#FFFFFF").
     */
    String getColor();

    /**
     * Sets the color code for the note.
     *
     * @param color The desired color code (e.g., "#FFFFFF").
     */
    void setColor(String color);

    /**
     * Retrieves the web link associated with the note.
     *
     * @return A string URL or link.
     */
    String getWebLink();

    /**
     * Sets the web link for the note.
     *
     * @param webLink The desired URL or link.
     */
    void setWebLink(String webLink);
}