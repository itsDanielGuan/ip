/**
 * Represents a single task the user has asked the chatbot to remember.
 * A task bundles its description together with whether it is done,
 * replacing the two parallel arrays used earlier.
 */
public class Task {
    /**
     * Fields are protected rather than private so that future subclasses
     * (e.g. Todo, Deadline, Event) can access them directly.
     */
    protected String description;
    protected boolean isDone;

    /**
     * Creates a task with the given description.
     * A newly created task always starts out as not done.
     */
    public Task(String description) {
        this.description = description;
        this.isDone = false;
    }

    /** Returns "X" if this task is done, or a blank space if it is not. */
    public String getStatusIcon() {
        return (isDone ? "X" : " "); // mark done task with X
    }

    /** Marks this task as done. */
    public void markAsDone() {
        this.isDone = true;
    }

    /** Marks this task as not done yet. */
    public void markAsNotDone() {
        this.isDone = false;
    }

    /**
     * Returns the task formatted for display, e.g. "[X] read book".
     * Java calls toString() automatically when a Task is used where a
     * String is expected, such as in string concatenation.
     */
    @Override
    public String toString() {
        return "[" + getStatusIcon() + "] " + description;
    }
}
