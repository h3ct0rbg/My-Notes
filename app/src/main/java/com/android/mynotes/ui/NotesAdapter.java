package com.android.mynotes.ui;

import android.annotation.SuppressLint;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.mynotes.R;
import com.android.mynotes.domain.decorators.NoteComponent;
import com.android.mynotes.domain.decorators.NoteDecoratorFactory;
import com.android.mynotes.domain.entities.Note;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Adapter class for displaying a list of notes in a RecyclerView.
 * This adapter binds Note objects to the views defined in the item_container_note layout.
 */
public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.NoteViewHolder> {

    private List<Note> notes;
    private final NotesListener notesListener;

    /**
     * Constructs a NotesAdapter with the provided notes list and listener.
     *
     * @param notes         The initial list of notes to be displayed.
     * @param notesListener An implementation of NotesListener to handle note click events.
     */
    public NotesAdapter(List<Note> notes, NotesListener notesListener) {
        this.notes = notes;
        this.notesListener = notesListener;
    }

    /**
     * Called when the RecyclerView needs a new ViewHolder to represent an item.
     *
     * @param parent   The parent ViewGroup into which the new View will be added.
     * @param viewType The view type of the new View.
     * @return A new NoteViewHolder instance.
     */
    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new NoteViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.item_container_note,
                        parent,
                        false
                )
        );
    }

    /**
     * Binds data from the note at the specified position to the given ViewHolder.
     *
     * @param holder   The ViewHolder which should be updated.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        holder.setNote(notes.get(position));
        holder.layoutNote.setOnClickListener(v ->
                notesListener.onNoteClicked(notes.get(position), position)
        );
    }

    /**
     * Returns the total number of notes in the adapter.
     *
     * @return The size of the notes list.
     */
    @Override
    public int getItemCount() {
        return notes.size();
    }

    /**
     * Returns the view type of the item at the given position.
     * Here, it simply returns the position itself.
     *
     * @param position The position of the item.
     * @return The view type (in this case, the position).
     */
    @Override
    public int getItemViewType(int position) {
        return position;
    }

    /**
     * Updates the notes list and refreshes the RecyclerView.
     *
     * @param notes The new list of notes to be displayed.
     */
    @SuppressLint("NotifyDataSetChanged")
    public void updateNotes(List<Note> notes) {
        this.notes = notes;
        notifyDataSetChanged();
    }

    /**
     * ViewHolder for individual notes, binding note data to UI components.
     */
    public static class NoteViewHolder extends RecyclerView.ViewHolder {

        TextView textTitle, textSubtitle, textDateTime;
        LinearLayout layoutNote;
        RoundedImageView imageNote;

        /**
         * Initializes the UI components from the item layout.
         *
         * @param itemView The inflated view of the individual item.
         */
        NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            textTitle = itemView.findViewById(R.id.textTitle);
            textSubtitle = itemView.findViewById(R.id.textSubtitle);
            textDateTime = itemView.findViewById(R.id.textDateTime);
            layoutNote = itemView.findViewById(R.id.layoutNote);
            imageNote = itemView.findViewById(R.id.imageNote);
        }

        /**
         * Sets the note information into the appropriate UI components.
         *
         * @param note The Note object containing data to display.
         */
        void setNote(Note note) {
            textTitle.setText(note.getTitle());

            if (note.getSubtitle().trim().isEmpty()) {
                textSubtitle.setVisibility(View.GONE);
            } else {
                textSubtitle.setText(note.getSubtitle());
                textSubtitle.setVisibility(View.VISIBLE);
            }

            textDateTime.setText(note.getDateTime());

            GradientDrawable gradientDrawable = (GradientDrawable) layoutNote.getBackground();
            NoteComponent decoratedNote = NoteDecoratorFactory.getDecorator(note);
            gradientDrawable.setColor(Color.parseColor(decoratedNote.getColor()));

            if (note.getImagePath() != null) {
                imageNote.setImageBitmap(BitmapFactory.decodeFile(note.getImagePath()));
                imageNote.setVisibility(View.VISIBLE);
            } else {
                imageNote.setVisibility(View.GONE);
            }
        }
    }
}