package com.android.mynotes.command;

public interface Command {
    void execute();
    void undo();
}
