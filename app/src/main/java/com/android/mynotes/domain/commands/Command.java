package com.android.mynotes.domain.commands;

public interface Command {
    void execute();
    void undo();
}