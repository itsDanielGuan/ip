package yappy.ui;

import java.util.Scanner;

import yappy.task.Task;
import yappy.task.TaskList;

/**
 * Builds Yappy's messages and handles input and output for the retained console UI.
 */
public class Ui {
    private static final String NAME = "Yappy";
    private static final String DIVIDER = "____________________________________________________________";
    private static final String BANNER = "__   __\n"
            + "\\ \\ / /  __ _  _ __   _ __   _   _\n"
            + " \\ V /  / _` || '_ \\ | '_ \\ | | | |\n"
            + "  | |  | (_| || |_) || |_) || |_| |\n"
            + "  |_|   \\__,_|| .__/ | .__/  \\__, |\n"
            + "              |_|    |_|      |___/";

    private final Scanner scanner;

    /**
     * Creates a console UI that reads from standard input.
     */
    public Ui() {
        this.scanner = new Scanner(System.in);
    }

    /**
     * Returns whether another command is available from the input stream.
     */
    public boolean hasNextCommand() {
        return scanner.hasNextLine();
    }

    /**
     * Reads the next command and removes surrounding whitespace.
     */
    public String readCommand() {
        return scanner.nextLine().trim();
    }

    /**
     * Returns Yappy's greeting.
     */
    public String getWelcome() {
        return "Hello! I'm " + NAME + ".\nWhat can I do for you?";
    }

    /**
     * Returns Yappy's farewell message.
     */
    public String getGoodbye() {
        return "Bye. Hope to see you again soon!";
    }

    /**
     * Returns a user-friendly command error.
     */
    public String getError(String message) {
        return message;
    }

    /**
     * Returns a warning that saved tasks could not be loaded.
     */
    public String getLoadingError() {
        return "OOPS!!! I could not load your saved tasks. Starting with an empty list.";
    }

    /**
     * Returns a warning that malformed saved records were ignored.
     */
    public String getSkippedRecords(int skippedRecords) {
        return "OOPS!!! I skipped " + skippedRecords + " invalid saved task record(s).";
    }

    /**
     * Returns a warning that the current task-list change could not be saved.
     */
    public String getSavingError(String details) {
        return "OOPS!!! I could not save your tasks: " + details;
    }

    /**
     * Returns every task in its current order using one-based numbering.
     */
    public String getTaskList(TaskList tasks) {
        return "Here are the tasks in your list:" + getNumberedTasks(tasks);
    }

    /**
     * Returns the tasks whose descriptions matched a find keyword.
     */
    public String getMatchingTasks(TaskList matchingTasks) {
        return "Here are the matching tasks in your list:" + getNumberedTasks(matchingTasks);
    }

    /**
     * Returns the confirmation that a task was added.
     */
    public String getTaskAdded(Task task, int taskCount) {
        return "Got it. I've added this task:\n  " + task
                + "\nNow you have " + taskCount + " tasks in the list.";
    }

    /**
     * Returns the confirmation that a task was marked as done.
     */
    public String getTaskMarked(Task task) {
        return "Nice! I've marked this task as done:\n  " + task;
    }

    /**
     * Returns the confirmation that a task was marked as not done.
     */
    public String getTaskUnmarked(Task task) {
        return "OK, I've marked this task as not done yet:\n  " + task;
    }

    /**
     * Returns the confirmation that a task was deleted.
     */
    public String getTaskDeleted(Task task, int taskCount) {
        return "Noted. I've removed this task:\n  " + task
                + "\nNow you have " + taskCount + " tasks in the list.";
    }

    /**
     * Shows Yappy's banner and greeting.
     */
    public void showWelcome() {
        showLine();
        System.out.println(BANNER);
        showMessage(getWelcome());
        showLine();
    }

    /**
     * Shows Yappy's farewell message.
     */
    public void showGoodbye() {
        showLine();
        showMessage(getGoodbye());
        showLine();
    }

    /**
     * Shows the divider between user input and Yappy's response.
     */
    public void showLine() {
        System.out.println(DIVIDER);
    }

    /**
     * Shows a prepared Yappy message in the console.
     */
    public void showMessage(String message) {
        System.out.println(message);
    }

    /**
     * Shows a user-friendly command error.
     */
    public void showError(String message) {
        showMessage(getError(message));
    }

    /**
     * Warns that saved tasks could not be loaded.
     */
    public void showLoadingError() {
        showMessage(getLoadingError());
    }

    /**
     * Warns that malformed saved records were ignored.
     */
    public void showSkippedRecords(int skippedRecords) {
        showMessage(getSkippedRecords(skippedRecords));
    }

    /**
     * Warns that the current task-list change could not be saved.
     */
    public void showSavingError(String details) {
        showMessage(getSavingError(details));
    }

    /**
     * Shows every task in its current order using one-based numbering.
     */
    public void showTaskList(TaskList tasks) {
        showMessage(getTaskList(tasks));
    }

    /**
     * Shows the tasks whose descriptions matched a find keyword.
     */
    public void showMatchingTasks(TaskList matchingTasks) {
        showMessage(getMatchingTasks(matchingTasks));
    }

    /**
     * Shows the supplied tasks using one-based numbering.
     */
    private String getNumberedTasks(TaskList tasks) {
        StringBuilder numberedTasks = new StringBuilder();
        for (int i = 0; i < tasks.size(); i++) {
            numberedTasks.append("\n").append(i + 1).append(".").append(tasks.get(i));
        }
        return numberedTasks.toString();
    }

    /**
     * Confirms that a task was added.
     */
    public void showTaskAdded(Task task, int taskCount) {
        showMessage(getTaskAdded(task, taskCount));
    }

    /**
     * Confirms that a task was marked as done.
     */
    public void showTaskMarked(Task task) {
        showMessage(getTaskMarked(task));
    }

    /**
     * Confirms that a task was marked as not done.
     */
    public void showTaskUnmarked(Task task) {
        showMessage(getTaskUnmarked(task));
    }

    /**
     * Confirms that a task was deleted.
     */
    public void showTaskDeleted(Task task, int taskCount) {
        showMessage(getTaskDeleted(task, taskCount));
    }
}
