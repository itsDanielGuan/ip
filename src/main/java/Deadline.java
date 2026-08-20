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
        super(TaskType.DEADLINE, description);
        this.by = by;
    }

    /**
     * Returns the deadline formatted with its deadline text.
     */
    @Override
    public String toString() {
        return super.toString() + " (by: " + by + ")";
    }
}
