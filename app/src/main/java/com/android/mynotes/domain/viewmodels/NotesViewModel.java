package com.android.mynotes.domain.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.android.mynotes.data.NotesRepository;
import com.android.mynotes.domain.entities.Note;
import com.android.mynotes.domain.commands.AddNoteCommand;
import com.android.mynotes.domain.commands.Command;
import com.android.mynotes.domain.commands.CommandInvoker;
import com.android.mynotes.domain.commands.DeleteNoteCommand;
import com.android.mynotes.domain.commands.EditNoteCommand;

import java.util.List;

/**
 * ViewModel responsible for managing Note-related operations.
 * Utilizes command patterns (undo/redo) and a repository for data persistence.
 */
public class NotesViewModel extends ViewModel {

    private final NotesRepository repository;
    private final LiveData<List<Note>> notesLiveData;

    /**
     * Constructs a NotesViewModel with the specified repository.
     *
     * @param repository The repository handling data persistence for notes.
     */
    public NotesViewModel(NotesRepository repository) {
        this.repository = repository;
        this.notesLiveData = repository.getAllNotes();
    }

    /**
     * Retrieves a LiveData list of all notes.
     *
     * @return LiveData containing the list of Note objects.
     */
    public LiveData<List<Note>> getNotes() {
        return notesLiveData;
    }

    /**
     * Creates and executes a command to add a new note.
     *
     * @param note The note to be added.
     */
    public void addNoteCommand(Note note) {
        executeCommand(new AddNoteCommand(repository, note));
    }

    /**
     * Creates and executes a command to edit an existing note.
     *
     * @param oldNote The original note to be edited.
     * @param newNote The updated note with new data.
     */
    public void editNoteCommand(Note oldNote, Note newNote) {
        executeCommand(new EditNoteCommand(repository, oldNote, newNote));
    }

    /**
     * Creates and executes a command to delete a note.
     *
     * @param note The note to be deleted.
     */
    public void deleteNoteCommand(Note note) {
        executeCommand(new DeleteNoteCommand(repository, note));
    }

    /**
     * Performs an undo operation on the last executed command, if available.
     */
    public void undoCommand() {
        CommandInvoker.undo();
    }

    /**
     * Performs a redo operation on the last undone command, if available.
     */
    public void redoCommand() {
        CommandInvoker.redo();
    }

    /**
     * Executes a given command via the CommandInvoker.
     *
     * @param command The command to be executed.
     */
    private void executeCommand(Command command) {
        CommandInvoker.executeCommand(command);
    }
}