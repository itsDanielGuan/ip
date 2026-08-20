/**
 * Represents a task that should be completed by a specific date or time.
 */
public class Deadline extends Task {
    /** Raw date/time text typed after the /by marker. */
    protected String by;

    /**
     * Creates a deadline task with the given description and deadline text.
     */
    public Deadline(String description, String by) {
        super(description);
        this.by = by;
    }

    /**
     * Returns the deadline formatted with its type icon and deadline text.
     */
    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: " + by + ")";
    }
}
