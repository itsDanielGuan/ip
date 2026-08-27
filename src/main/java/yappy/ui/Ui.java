package yappy.ui;

import java.util.Scanner;

import yappy.task.Task;
import yappy.task.TaskList;

/**
 * Handles all console input and output for Yappy.
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
     * Shows Yappy's banner and greeting.
     */
    public void showWelcome() {
        showLine();
        System.out.println(BANNER);
        System.out.println("Hello! I'm " + NAME + ".");
        System.out.println("What can I do for you?");
        showLine();
    }

    /**
     * Shows Yappy's farewell message.
     */
    public void showGoodbye() {
        showLine();
        System.out.println("Bye. Hope to see you again soon!");
        showLine();
    }

    /**
     * Shows the divider between user input and Yappy's response.
     */
    public void showLine() {
        System.out.println(DIVIDER);
    }

    /**
     * Shows a user-friendly command error.
     */
    public void showError(String message) {
        System.out.println(message);
    }

    /**
     * Warns that saved tasks could not be loaded.
     */
    public void showLoadingError() {
        System.out.println("OOPS!!! I could not load your saved tasks. Starting with an empty list.");
    }

    /**
     * Warns that malformed saved records were ignored.
     */
    public void showSkippedRecords(int skippedRecords) {
        System.out.println("OOPS!!! I skipped " + skippedRecords + " invalid saved task record(s).");
    }

    /**
     * Warns that the current task-list change could not be saved.
     */
    public void showSavingError(String details) {
        System.out.println("OOPS!!! I could not save your tasks: " + details);
    }

    /**
     * Shows every task in its current order using one-based numbering.
     */
    public void showTaskList(TaskList tasks) {
        System.out.println("Here are the tasks in your list:");
        showNumberedTasks(tasks);
    }

    /**
     * Shows the tasks whose descriptions matched a find keyword.
     */
    public void showMatchingTasks(TaskList matchingTasks) {
        System.out.println("Here are the matching tasks in your list:");
        showNumberedTasks(matchingTasks);
    }

    /**
     * Shows the supplied tasks using one-based numbering.
     */
    private void showNumberedTasks(TaskList tasks) {
        for (int i = 0; i < tasks.size(); i++) {
            System.out.println((i + 1) + "." + tasks.get(i));
        }
    }

    /**
     * Confirms that a task was added.
     */
    public void showTaskAdded(Task task, int taskCount) {
        System.out.println("Got it. I've added this task:");
        System.out.println("  " + task);
        System.out.println("Now you have " + taskCount + " tasks in the list.");
    }

    /**
     * Confirms that a task was marked as done.
     */
    public void showTaskMarked(Task task) {
        System.out.println("Nice! I've marked this task as done:");
        System.out.println("  " + task);
    }

    /**
     * Confirms that a task was marked as not done.
     */
    public void showTaskUnmarked(Task task) {
        System.out.println("OK, I've marked this task as not done yet:");
        System.out.println("  " + task);
    }

    /**
     * Confirms that a task was deleted.
     */
    public void showTaskDeleted(Task task, int taskCount) {
        System.out.println("Noted. I've removed this task:");
        System.out.println("  " + task);
        System.out.println("Now you have " + taskCount + " tasks in the list.");
    }
}
