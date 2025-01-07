package com.android.mynotes.domain.commands;

import java.util.Stack;

/**
 * Manages the execution and history of {@link Command} objects,
 * enabling undo and redo functionality.
 */
public class CommandInvoker {

    /**
     * Holds the history of executed commands.
     * The most recent command is at the top of the stack.
     */
    private static final Stack<Command> commandHistory = new Stack<>();

    /**
     * Holds commands that have been undone, allowing them to be redone.
     * The most recent undone command is at the top of the stack.
     */
    private static final Stack<Command> redoStack = new Stack<>();

    /**
     * Executes a given command, adds it to the history, and clears the redo stack.
     *
     * @param command The {@link Command} to be executed.
     */
    public static void executeCommand(Command command) {
        command.execute();
        commandHistory.push(command);
        redoStack.clear();
    }

    /**
     * Undoes the most recently executed command, if any, and moves it to the redo stack.
     */
    public static void undo() {
        if (!commandHistory.isEmpty()) {
            Command command = commandHistory.pop();
            command.undo();
            redoStack.push(command);
        }
    }

    /**
     * Redoes the most recently undone command, if any,
     * moving it back to the command history.
     */
    public static void redo() {
        if (!redoStack.isEmpty()) {
            Command command = redoStack.pop();
            command.execute();
            commandHistory.push(command);
        }
    }
}