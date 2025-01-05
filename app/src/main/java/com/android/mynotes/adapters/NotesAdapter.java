package com.android.mynotes.adapters;

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
import com.android.mynotes.decorators.NoteComponent;
import com.android.mynotes.decorators.NoteDecoratorFactory;
import com.android.mynotes.entities.Note;
import com.android.mynotes.listeners.NotesListener;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Adapter class for displaying a list of notes in a RecyclerView.
 * This adapter binds Note objects to the views defined in item_container_note layout.
 */
public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.NoteViewHolder> {

    private List<Note> notes;
    private NotesListener notesListener;
    private Timer timer;
    private List<Note> notesSource;

    /**
     * Constructor to initialize the adapter with a list of notes.
     * @param notes List of notes to display.
     */
    public NotesAdapter(List<Note> notes, NotesListener notesListener) {
        this.notes = notes;
        this.notesListener = notesListener;
        notesSource = notes;
    }

    /**
     * Called when RecyclerView needs a new ViewHolder.
     * @param parent The ViewGroup into which the new View will be added.
     * @param viewType The view type of the new View.
     * @return A new NoteViewHolder.
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
     * Called by RecyclerView to display data at a specified position.
     * @param holder The ViewHolder that should be updated.
     * @param position The position of the item in the dataset.
     */
    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        holder.setNote(notes.get(position));
        holder.layoutNote.setOnClickListener(v ->
                notesListener.onNoteClicked(notes.get(position), position)
        );
    }

    /**
     * Returns the total number of items in the dataset.
     * @return The size of the notes list.
     */
    @Override
    public int getItemCount() {
        return notes.size();
    }

    /**
     * Returns the view type of the item at a specific position.
     * @param position Position of the item.
     * @return View type (in this case, simply the position).
     */
    @Override
    public int getItemViewType(int position) {
        return position;
    }

    /**
     * ViewHolder class for holding and binding note data to the UI components.
     */
    public static class NoteViewHolder extends RecyclerView.ViewHolder {

        TextView textTitle, textSubtitle, textDateTime;
        LinearLayout layoutNote;
        RoundedImageView imageNote;

        /**
         * Constructor for initializing the views from the item layout.
         * @param itemView The view of the individual item.
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
         * Sets the note details to the respective views.
         * @param note The Note object containing the details to be displayed.
         */

        void setNote(Note note) {
            textTitle.setText(note.getTitle());
            if(note.getSubtitle().trim().isEmpty()) {
                textSubtitle.setVisibility(View.GONE);
            } else {
                textSubtitle.setText(note.getSubtitle());
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

    public void searchNotes(final String searchKeyword) {
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void run() {
                if (searchKeyword.trim().isEmpty()) {
                    notes = notesSource;
                } else {
                    ArrayList<Note> temp = new ArrayList<>();
                    for (Note note : notesSource) {
                        if (note.getTitle().toLowerCase().contains(searchKeyword.toLowerCase())
                                || note.getSubtitle().toLowerCase().contains(searchKeyword. toLowerCase())
                                || note.getNoteText().toLowerCase().contains(searchKeyword.toLowerCase())) {
                            temp.add(note);
                        }
                    }
                    notes = temp;
                }
                new Handler(Looper.getMainLooper()).post(() -> notifyDataSetChanged());
            }
        }, 500);
    }

    public void cancelTimer() {
        if (timer != null) {
            timer.cancel();
        }
    }
}
