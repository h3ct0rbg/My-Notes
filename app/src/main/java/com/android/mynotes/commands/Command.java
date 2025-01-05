package com.android.mynotes.commands;

public interface Command {
    void execute();
    void undo();
}