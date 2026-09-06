package yappy.task;

import java.util.ArrayList;
import java.util.List;

/**
 * Owns Yappy's ordered collection of tasks and the operations that modify it.
 */
public class TaskList {
    private final List<Task> tasks;

    /**
     * Creates an empty task list.
     */
    public TaskList() {
        this.tasks = new ArrayList<>();
    }

    /**
     * Creates a task list containing the supplied tasks in their existing order.
     */
    public TaskList(List<Task> tasks) {
        this.tasks = new ArrayList<>(tasks);
    }

    /**
     * Returns the number of tasks in the list.
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Returns the task at the specified zero-based index.
     */
    public Task get(int index) {
        return tasks.get(index);
    }

    /**
     * Appends a task to the end of the list.
     */
    public void add(Task task) {
        assert task != null : "A task to add must not be null";
        tasks.add(task);
    }

    /**
     * Removes and returns the task at the specified zero-based index.
     */
    public Task remove(int index) {
        assert index >= 0 && index < tasks.size() : "Task index must be valid";
        return tasks.remove(index);
    }

    /**
     * Returns a new task list containing tasks whose descriptions match the keyword.
     */
    public TaskList find(String keyword) {
        List<Task> matchingTasks = new ArrayList<>();
        for (Task task : tasks) {
            if (task.containsKeyword(keyword)) {
                matchingTasks.add(task);
            }
        }
        return new TaskList(matchingTasks);
    }
}
