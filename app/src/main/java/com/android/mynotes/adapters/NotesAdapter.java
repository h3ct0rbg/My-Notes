package com.android.mynotes.adapters;

import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.mynotes.R;
import com.android.mynotes.decorators.NoteDecorator;
import com.android.mynotes.decorators.BlueNoteDecorator;
import com.android.mynotes.decorators.DefaultNoteDecorator;
import com.android.mynotes.decorators.GreenNoteDecorator;
import com.android.mynotes.decorators.NoteComponent;
import com.android.mynotes.decorators.NoteDecoratorFactory;
import com.android.mynotes.decorators.RedNoteDecorator;
import com.android.mynotes.decorators.YellowNoteDecorator;
import com.android.mynotes.entities.Note;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.List;

/**
 * Adapter class for displaying a list of notes in a RecyclerView.
 * This adapter binds Note objects to the views defined in item_container_note layout.
 */
public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.NoteViewHolder> {

    private List<Note> notes;

    /**
     * Constructor to initialize the adapter with a list of notes.
     * @param notes List of notes to display.
     */
    public NotesAdapter(List<Note> notes) {
        this.notes = notes;
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

    @NonNull
    private static NoteComponent getNoteDecorator(Note note) {
        NoteComponent decoratedNote = new NoteDecorator(note); // Decorador base

        // Apply Decorators
        if (note.getColor() != null && note.getColor().equals("#FDBE3B")) {
            decoratedNote = new YellowNoteDecorator(note);
        } else if (note.getColor() != null && note.getColor().equals("#FF4842")) {
            decoratedNote = new RedNoteDecorator(note);
        } else if (note.getColor() != null && note.getColor().equals("#3A52Fc")) {
            decoratedNote = new BlueNoteDecorator(note);
        } else if (note.getColor() != null && note.getColor().equals("#17C51E")) {
            decoratedNote = new GreenNoteDecorator(note);
        } else {
            decoratedNote = new DefaultNoteDecorator(note);
        }
        return decoratedNote;
    }
}
