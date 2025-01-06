package com.android.mynotes.domain;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.android.mynotes.data.NotesRepository;

import java.util.List;

public class NotesViewModel extends ViewModel {

    private final NotesRepository repository;
    private final LiveData<List<Note>> notesLiveData;

    public NotesViewModel(NotesRepository repository) {
        this.repository = repository;
        this.notesLiveData = repository.getAllNotes();
    }

    public LiveData<List<Note>> getNotes() {
        return notesLiveData;
    }

    public void addNoteCommand(Note note) {
        repository.addNote(note);
    }

    public void deleteNoteCommand(Note note) {
        repository.deleteNote(note);
    }
}