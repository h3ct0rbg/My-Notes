package com.android.mynotes.domain.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.android.mynotes.domain.decorators.NoteComponent;

import java.io.Serializable;

/**
 * Represents a Note entity stored in the "notes" table.
 * Implements the {@link NoteComponent} interface to allow decorators
 * and additional behavior, and implements {@link Serializable} for easy
 * object passing between Android components.
 */
@Entity(tableName = "notes")
public class Note implements Serializable, NoteComponent {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "title")
    private String title;

    @ColumnInfo(name = "date_time")
    private String dateTime;

    @ColumnInfo(name = "subtitle")
    private String subtitle;

    @ColumnInfo(name = "note_text")
    private String noteText;

    @ColumnInfo(name = "image_path")
    private String imagePath;

    @ColumnInfo(name = "color")
    private String color;

    @ColumnInfo(name = "web_link")
    private String webLink;

    /**
     * Private constructor used by the Builder to create a Note instance.
     *
     * @param builder A {@link Builder} containing the fields to initialize.
     */
    private Note(Builder builder) {
        this.id = builder.id;
        this.title = builder.title;
        this.dateTime = builder.dateTime;
        this.subtitle = builder.subtitle;
        this.noteText = builder.noteText;
        this.imagePath = builder.imagePath;
        this.color = builder.color;
        this.webLink = builder.webLink;
    }

    /**
     * Default constructor required by some frameworks (e.g., Room).
     */
    public Note() { }

    //region NoteComponent implementation

    /**
     * {@inheritDoc}
     */
    @Override
    public int getId() {
        return this.id;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setId(int id) {
        this.id = id;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTitle() {
        return this.title;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDateTime() {
        return this.dateTime;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getSubtitle() {
        return this.subtitle;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getNoteText() {
        return this.noteText;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setNoteText(String noteText) {
        this.noteText = noteText;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getImagePath() {
        return this.imagePath;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getColor() {
        return this.color;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setColor(String color) {
        this.color = color;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getWebLink() {
        return this.webLink;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setWebLink(String webLink) {
        this.webLink = webLink;
    }

    //endregion

    /**
     * Builder class for constructing {@link Note} objects.
     */
    public static class Builder {
        private int id;
        private String title;
        private String dateTime;
        private String subtitle;
        private String noteText;
        private String imagePath;
        private String color;
        private String webLink;

        /**
         * Sets the ID for the Note.
         *
         * @param id The unique identifier for the Note.
         * @return The current Builder instance.
         */
        public Builder setId(int id) {
            this.id = id;
            return this;
        }

        /**
         * Sets the title for the Note.
         *
         * @param title The title string.
         * @return The current Builder instance.
         */
        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        /**
         * Sets the date/time for the Note.
         *
         * @param dateTime The date/time string.
         * @return The current Builder instance.
         */
        public Builder setDateTime(String dateTime) {
            this.dateTime = dateTime;
            return this;
        }

        /**
         * Sets the subtitle for the Note.
         *
         * @param subtitle The subtitle string.
         * @return The current Builder instance.
         */
        public Builder setSubtitle(String subtitle) {
            this.subtitle = subtitle;
            return this;
        }

        /**
         * Sets the main text content of the Note.
         *
         * @param noteText The note text content.
         * @return The current Builder instance.
         */
        public Builder setNoteText(String noteText) {
            this.noteText = noteText;
            return this;
        }

        /**
         * Sets the image path for the Note.
         *
         * @param imagePath A string path pointing to the note's image resource.
         * @return The current Builder instance.
         */
        public Builder setImagePath(String imagePath) {
            this.imagePath = imagePath;
            return this;
        }

        /**
         * Sets the color for the Note.
         *
         * @param color A string representing the color code (e.g. "#FFFFFF").
         * @return The current Builder instance.
         */
        public Builder setColor(String color) {
            this.color = color;
            return this;
        }

        /**
         * Sets the web link for the Note.
         *
         * @param webLink A string URL or link.
         * @return The current Builder instance.
         */
        public Builder setWebLink(String webLink) {
            this.webLink = webLink;
            return this;
        }

        /**
         * Builds and returns a new {@link Note} instance.
         *
         * @return A fully constructed Note.
         */
        public Note build() {
            return new Note(this);
        }
    }
}