package com.android.mynotes.domain.decorators;

/**
 * A base decorator class implementing {@link NoteComponent}.
 * This class is designed to add or modify behavior of another {@link NoteComponent}
 * without changing its existing structure. It forwards method calls to the wrapped component,
 * allowing subclasses to override or augment these methods as needed.
 */
public class NoteDecorator implements NoteComponent {

    /** The underlying NoteComponent being decorated. */
    private final NoteComponent noteComponent;

    /**
     * Constructs a NoteDecorator with the specified {@link NoteComponent}.
     *
     * @param noteComponent The NoteComponent to be decorated.
     */
    public NoteDecorator(NoteComponent noteComponent) {
        this.noteComponent = noteComponent;
    }

    /**
     * {@inheritDoc}
     * Forwards to the decorated component's implementation.
     */
    @Override
    public int getId() {
        return noteComponent.getId();
    }

    /**
     * {@inheritDoc}
     * By default, this decorator does not override the method.
     * Intentionally left blank, forwarding is suppressed.
     */
    @Override
    public void setId(int id) {
        // Intentionally left empty to preserve the decorated component's ID
    }

    /**
     * {@inheritDoc}
     * Forwards to the decorated component's implementation.
     */
    @Override
    public String getTitle() {
        return noteComponent.getTitle();
    }

    /**
     * {@inheritDoc}
     * By default, this decorator does not override the method.
     * Intentionally left blank, forwarding is suppressed.
     */
    @Override
    public void setTitle(String title) {
        // No-op
    }

    /**
     * {@inheritDoc}
     * Forwards to the decorated component's implementation.
     */
    @Override
    public String getDateTime() {
        return noteComponent.getDateTime();
    }

    /**
     * {@inheritDoc}
     * By default, this decorator does not override the method.
     * Intentionally left blank, forwarding is suppressed.
     */
    @Override
    public void setDateTime(String dateTime) {
        // No-op
    }

    /**
     * {@inheritDoc}
     * Forwards to the decorated component's implementation.
     */
    @Override
    public String getSubtitle() {
        return noteComponent.getSubtitle();
    }

    /**
     * {@inheritDoc}
     * By default, this decorator does not override the method.
     * Intentionally left blank, forwarding is suppressed.
     */
    @Override
    public void setSubtitle(String subtitle) {
        // No-op
    }

    /**
     * {@inheritDoc}
     * Forwards to the decorated component's implementation.
     */
    @Override
    public String getNoteText() {
        return noteComponent.getNoteText();
    }

    /**
     * {@inheritDoc}
     * By default, this decorator does not override the method.
     * Intentionally left blank, forwarding is suppressed.
     */
    @Override
    public void setNoteText(String noteText) {
        // No-op
    }

    /**
     * {@inheritDoc}
     * Forwards to the decorated component's implementation.
     */
    @Override
    public String getImagePath() {
        return noteComponent.getImagePath();
    }

    /**
     * {@inheritDoc}
     * By default, this decorator does not override the method.
     * Intentionally left blank, forwarding is suppressed.
     */
    @Override
    public void setImagePath(String imagePath) {
        // No-op
    }

    /**
     * {@inheritDoc}
     * Forwards to the decorated component's implementation.
     */
    @Override
    public String getColor() {
        return noteComponent.getColor();
    }

    /**
     * {@inheritDoc}
     * By default, this decorator does not override the method.
     * Intentionally left blank, forwarding is suppressed.
     */
    @Override
    public void setColor(String color) {
        // No-op
    }

    /**
     * {@inheritDoc}
     * Forwards to the decorated component's implementation.
     */
    @Override
    public String getWebLink() {
        return noteComponent.getWebLink();
    }

    /**
     * {@inheritDoc}
     * By default, this decorator does not override the method.
     * Intentionally left blank, forwarding is suppressed.
     */
    @Override
    public void setWebLink(String webLink) {
        // No-op
    }
}