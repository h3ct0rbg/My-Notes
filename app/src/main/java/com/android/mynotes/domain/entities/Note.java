package com.android.mynotes.domain.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.android.mynotes.domain.decorators.NoteComponent;

import java.io.Serializable;

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

    public Note(){ }

    @Override
    public int getId() {
        return this.id;
    }

    @Override
    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String getTitle() {
        return this.title;
    }

    @Override
    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String getDateTime() {
        return this.dateTime;
    }

    @Override
    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    @Override
    public String getSubtitle() {
        return this.subtitle;
    }

    @Override
    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    @Override
    public String getNoteText() {
        return this.noteText;
    }

    @Override
    public void setNoteText(String noteText) {
        this.noteText = noteText;
    }

    @Override
    public String getImagePath() {
        return this.imagePath;
    }

    @Override
    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    @Override
    public String getColor() {
        return this.color;
    }

    @Override
    public void setColor(String color) {
        this.color = color;
    }

    @Override
    public String getWebLink() {
        return this.webLink;
    }

    @Override
    public void setWebLink(String webLink) {
        this.webLink = webLink;
    }

    public static class Builder {
        private int id;
        private String title;
        private String dateTime;
        private String subtitle;
        private String noteText;
        private String imagePath;
        private String color;
        private String webLink;

        public Builder setId(int id) {
            this.id = id;
            return this;
        }

        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        public Builder setDateTime(String dateTime) {
            this.dateTime = dateTime;
            return this;
        }

        public Builder setSubtitle(String subtitle) {
            this.subtitle = subtitle;
            return this;
        }

        public Builder setNoteText(String noteText) {
            this.noteText = noteText;
            return this;
        }

        public Builder setImagePath(String imagePath) {
            this.imagePath = imagePath;
            return this;
        }

        public Builder setColor(String color) {
            this.color = color;
            return this;
        }

        public Builder setWebLink(String webLink) {
            this.webLink = webLink;
            return this;
        }

        public Note build() {
            return new Note(this);
        }

    }
}
