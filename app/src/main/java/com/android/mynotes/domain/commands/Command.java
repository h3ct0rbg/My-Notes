package com.android.mynotes.domain.commands;

/**
 * Represents an executable command with the ability to be undone.
 */
public interface Command {

    /**
     * Executes the core logic of this command.
     */
    void execute();

    /**
     * Reverses or undoes the effects of this command's execution.
     */
    void undo();
}