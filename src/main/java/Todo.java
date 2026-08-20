/**
 * Represents a task without any date or time attached to it.
 */
public class Todo extends Task {
    /**
     * Creates a todo task with the given description.
     */
    public Todo(String description) {
        super(description);
    }

    /**
     * Returns the todo formatted with its type icon.
     */
    @Override
    public String toString() {
        return "[T]" + super.toString();
    }
}
