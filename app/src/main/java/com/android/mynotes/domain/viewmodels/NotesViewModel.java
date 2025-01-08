package com.android.mynotes.domain.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.android.mynotes.data.NotesRepository;
import com.android.mynotes.domain.entities.Note;
import com.android.mynotes.domain.commands.Command;
import com.android.mynotes.domain.commands.CommandInvoker;
import com.android.mynotes.domain.commands.AddNoteCommand;
import com.android.mynotes.domain.commands.EditNoteCommand;
import com.android.mynotes.domain.commands.DeleteNoteCommand;
import com.android.mynotes.domain.strategy.NoteSortingStrategy;

import java.util.ArrayList;
import java.util.List;

/**
 * ViewModel responsible for managing Note-related operations.
 * Utilizes command patterns (undo/redo) and a repository for data persistence.
 */
public class NotesViewModel extends ViewModel {

    private final NotesRepository repository;
    private final LiveData<List<Note>> allNotes;
    private final MediatorLiveData<List<Note>> filteredNotes = new MediatorLiveData<>();
    private final MutableLiveData<String> searchQuery = new MutableLiveData<>();

    private final android.os.Handler handler = new android.os.Handler();
    private Runnable debounceRunnable;
    private NoteSortingStrategy sortingStrategy;

    /**
     * Constructs a NotesViewModel with the specified repository.
     *
     * @param repository The repository handling data persistence for notes.
     */
    public NotesViewModel(NotesRepository repository) {
        this.repository = repository;
        this.allNotes = repository.getAllNotes();

        // Combine the logic of filtering with the original data
        filteredNotes.addSource(allNotes, notes -> filterNotes(searchQuery.getValue()));
        filteredNotes.addSource(searchQuery, this::debounceSearch);
    }

    /**
     * Retrieves a LiveData list of filtered notes based on the search query.
     *
     * @return LiveData containing the list of filtered Note objects.
     */
    public LiveData<List<Note>> getNotes() {
        return filteredNotes;
    }

    /**
     * Updates the search query and triggers the filtering process.
     *
     * @param query The search query entered by the user.
     */
    public void updateSearchQuery(String query) {
        searchQuery.setValue(query);
    }

    /**
     * Handles debouncing for the search query to prevent excessive filtering.
     *
     * @param query The current search query entered by the user.
     */
    private void debounceSearch(String query) {
        if (debounceRunnable != null) {
            handler.removeCallbacks(debounceRunnable);
        }

        debounceRunnable = () -> filterNotes(query);
        // Delay in milliseconds to debounce search
        handler.postDelayed(debounceRunnable, 500);
    }

    /**
     * Filters the notes based on the search query.
     * If the query is empty, all notes are displayed.
     *
     * @param query The search query entered by the user.
     */
    private void filterNotes(String query) {
        List<Note> notes = allNotes.getValue();
        if (notes == null) {
            filteredNotes.setValue(new ArrayList<>());
            return;
        }

        if (query == null || query.trim().isEmpty()) {
            filteredNotes.setValue(notes); // Show all notes if the query is empty
        } else {
            List<Note> matchingNotes = new ArrayList<>();
            for (Note note : notes) {
                if (note.getTitle().toLowerCase().contains(query.toLowerCase()) ||
                        note.getSubtitle().toLowerCase().contains(query.toLowerCase()) ||
                        note.getNoteText().toLowerCase().contains(query.toLowerCase())) {
                    matchingNotes.add(note);
                }
            }
            filteredNotes.setValue(matchingNotes); // Publish the filtered notes
        }
    }

    public void setSortingStrategy(NoteSortingStrategy strategy) {
        this.sortingStrategy = strategy;
        applySorting();
    }

    private void applySorting() {
        List<Note> currentNotes = filteredNotes.getValue();
        if (currentNotes != null && sortingStrategy != null) {
            filteredNotes.setValue(sortingStrategy.sort(new ArrayList<>(currentNotes)));
        }
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