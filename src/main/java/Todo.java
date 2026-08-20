/**
 * Represents a task without any date or time attached to it.
 */
public class Todo extends Task {
    /**
     * Creates a todo task with the given description.
     */
    public Todo(String description) {
        super(TaskType.TODO, description);
    }

    /**
     * Returns the todo formatted for display.
     */
    @Override
    public String toString() {
        return super.toString();
    }
}
