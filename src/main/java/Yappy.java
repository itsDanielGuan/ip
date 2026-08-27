import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

/**
 * Entry point of the Yappy chatbot.
 * At this stage the bot stores todos, deadlines, and events, lists them back,
 * marks them done or not done, and exits on the "bye" command.
 */
public class Yappy {
    /** Name the chatbot introduces itself with. */
    private static final String NAME = "Yappy";

    /** Horizontal line used to separate the chatbot's messages from the user's input. */
    private static final String DIVIDER = "____________________________________________________________";

    /** Relative, OS-independent location of Yappy's persistent task data. */
    private static final Path DATA_FILE = Paths.get("data", "yappy.txt");

    public static void main(String[] args) {
        // ASCII art logo. Each backslash is doubled, since backslash is the Java escape character.
        String banner = "__   __                            \n"
                + "\\ \\ / /  __ _  _ __   _ __   _   _ \n"
                + " \\ V /  / _` || '_ \\ | '_ \\ | | | |\n"
                + "  | |  | (_| || |_) || |_) || |_| |\n"
                + "  |_|   \\__,_|| .__/ | .__/  \\__, |\n"
                + "              |_|    |_|      |___/ ";

        System.out.println(DIVIDER);
        System.out.println(banner);
        System.out.println("Hello! I'm " + NAME + ".");
        System.out.println("What can I do for you?");
        System.out.println(DIVIDER);

        Storage storage = new Storage(DATA_FILE);
        TaskList tasks;
        try {
            tasks = new TaskList(storage.load());
            if (storage.getSkippedRecordCount() > 0) {
                System.out.println("OOPS!!! I skipped " + storage.getSkippedRecordCount()
                        + " invalid saved task record(s).");
            }
        } catch (IOException e) {
            System.out.println("OOPS!!! I could not load your saved tasks. Starting with an empty list.");
            tasks = new TaskList();
        }

        // Scanner reads the user's input from the keyboard (System.in), one line at a time.
        Scanner scanner = new Scanner(System.in);

        // Keep reading until the user says "bye". hasNextLine() guards against the
        // input ending unexpectedly (e.g. Ctrl+D, or piping a file that has no "bye").
        while (scanner.hasNextLine()) {
            String input = scanner.nextLine().trim();
            Command command = Parser.getCommand(input);

            if (command == Command.BYE) {
                break;
            }

            System.out.println(DIVIDER);
            try {
                boolean taskListChanged = processInput(input, tasks);
                if (taskListChanged) {
                    storage.save(tasks);
                }
            } catch (YappyException e) {
                System.out.println(e.getMessage());
            } catch (IOException e) {
                System.out.println("OOPS!!! I could not save your tasks: " + e.getMessage());
            }
            System.out.println(DIVIDER);
        }

        System.out.println(DIVIDER);
        System.out.println("Bye. Hope to see you again soon!");
        System.out.println(DIVIDER);
    }

    /**
     * Runs one non-bye command.
     */
    private static boolean processInput(String input, TaskList tasks) throws YappyException {
        if (input.isEmpty()) {
            throw new YappyException("OOPS!!! Please type a command.");
        }

        Command command = Parser.getCommand(input);
        switch (command) {
        case LIST:
            printTaskList(tasks);
            return false;
        case MARK:
            markTask(input, tasks);
            return true;
        case UNMARK:
            unmarkTask(input, tasks);
            return true;
        case DELETE:
            deleteTask(input, tasks);
            return true;
        case TODO:
            addTask(tasks, Parser.parseTodo(input));
            return true;
        case DEADLINE:
            addTask(tasks, Parser.parseDeadline(input));
            return true;
        case EVENT:
            addTask(tasks, Parser.parseEvent(input));
            return true;
        default:
            throw new YappyException("OOPS!!! I don't know what that means. Try todo, deadline, event, list, mark, unmark, or delete.");
        }
    }

    /**
     * Prints all stored tasks in their current order.
     */
    private static void printTaskList(TaskList tasks) {
        System.out.println("Here are the tasks in your list:");
        // The numbering shown to the user starts at 1, while ArrayList is 0-indexed.
        for (int i = 0; i < tasks.size(); i++) {
            System.out.println((i + 1) + "." + tasks.get(i));
        }
    }

    /**
     * Adds the given task to the task list.
     */
    private static void addTask(TaskList tasks, Task task) {
        tasks.add(task);
        System.out.println("Got it. I've added this task:");
        System.out.println("  " + task);
        System.out.println("Now you have " + tasks.size() + " tasks in the list.");
    }

    /**
     * Marks the requested task as done.
     */
    private static void markTask(String input, TaskList tasks) throws YappyException {
        int index = Parser.parseTaskIndex(input, Command.MARK, tasks.size());
        Task task = tasks.get(index);
        task.markAsDone();
        System.out.println("Nice! I've marked this task as done:");
        System.out.println("  " + task);
    }

    /**
     * Marks the requested task as not done yet.
     */
    private static void unmarkTask(String input, TaskList tasks) throws YappyException {
        int index = Parser.parseTaskIndex(input, Command.UNMARK, tasks.size());
        Task task = tasks.get(index);
        task.markAsNotDone();
        System.out.println("OK, I've marked this task as not done yet:");
        System.out.println("  " + task);
    }

    /**
     * Deletes the requested task from the task list.
     */
    private static void deleteTask(String input, TaskList tasks) throws YappyException {
        int index = Parser.parseTaskIndex(input, Command.DELETE, tasks.size());
        Task removedTask = tasks.remove(index);
        System.out.println("Noted. I've removed this task:");
        System.out.println("  " + removedTask);
        System.out.println("Now you have " + tasks.size() + " tasks in the list.");
    }

}
