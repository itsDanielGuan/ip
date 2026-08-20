/**
 * Represents a task that happens from one date/time to another.
 */
public class Event extends Task {
    /** Raw date/time text typed after the /from marker. */
    protected String from;

    /** Raw date/time text typed after the /to marker. */
    protected String to;

    /**
     * Creates an event task with the given description and time range text.
     */
    public Event(String description, String from, String to) {
        super(description);
        this.from = from;
        this.to = to;
    }

    /**
     * Returns the event formatted with its type icon and time range text.
     */
    @Override
    public String toString() {
        return "[E]" + super.toString() + " (from: " + from + " to: " + to + ")";
    }
}
