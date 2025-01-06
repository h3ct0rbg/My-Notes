package com.android.mynotes.domain;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.android.mynotes.data.NotesRepository;
import com.android.mynotes.domain.commands.AddNoteCommand;
import com.android.mynotes.domain.commands.Command;
import com.android.mynotes.domain.commands.CommandInvoker;
import com.android.mynotes.domain.commands.DeleteNoteCommand;
import com.android.mynotes.domain.commands.EditNoteCommand;

import java.util.List;

public class NotesViewModel extends ViewModel {

    private final NotesRepository repository;
    private final LiveData<List<Note>> notesLiveData;
    private final CommandInvoker commandInvoker = new CommandInvoker();

    public NotesViewModel(NotesRepository repository) {
        this.repository = repository;
        this.notesLiveData = repository.getAllNotes();
    }

    public LiveData<List<Note>> getNotes() {
        return notesLiveData;
    }

    public void addNoteCommand(Note note) {
        executeCommand(new AddNoteCommand(repository, note));
    }

    public void editNoteCommand(Note oldNote, Note newNote) {
        executeCommand(new EditNoteCommand(repository, oldNote, newNote));
    }

    public void deleteNoteCommand(Note note) {
        executeCommand(new DeleteNoteCommand(repository, note));
    }

    public void undoCommand() {
        commandInvoker.undo();
    }

    public void redoCommand() {
        commandInvoker.redo();
    }

    private void executeCommand(Command command) {
        commandInvoker.executeCommand(command);
    }
}