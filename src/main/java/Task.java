/**
 * Represents a task the user has asked the chatbot to remember.
 * Subclasses add the task type and any extra timing information.
 */
public abstract class Task {
    /**
     * Fields are protected so subclasses can format task-specific display text
     * without needing extra getter methods at this early project stage.
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
     * Returns the task status and description, e.g. "[X] read book".
     * Subclasses prepend their type icon and append any extra details.
     * Java calls toString() automatically when a Task is used where a
     * String is expected, such as in string concatenation.
     */
    @Override
    public String toString() {
        return "[" + getStatusIcon() + "] " + description;
    }
}
