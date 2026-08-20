import java.util.ArrayList;
import java.util.List;
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

    /** Marker separating a deadline description from its deadline text. */
    private static final String BY_MARKER = "/by";

    /** Marker separating an event description from its start text. */
    private static final String FROM_MARKER = "/from";

    /** Marker separating an event start text from its end text. */
    private static final String TO_MARKER = "/to";

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

        // ArrayList grows as tasks are added and makes deletion straightforward.
        List<Task> tasks = new ArrayList<>();

        // Scanner reads the user's input from the keyboard (System.in), one line at a time.
        Scanner scanner = new Scanner(System.in);

        // Keep reading until the user says "bye". hasNextLine() guards against the
        // input ending unexpectedly (e.g. Ctrl+D, or piping a file that has no "bye").
        while (scanner.hasNextLine()) {
            String input = scanner.nextLine().trim();
            Command command = input.isEmpty() ? Command.UNKNOWN : Command.fromInput(input);

            if (command == Command.BYE) {
                break;
            }

            System.out.println(DIVIDER);
            try {
                processInput(input, tasks);
            } catch (YappyException e) {
                System.out.println(e.getMessage());
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
    private static void processInput(String input, List<Task> tasks) throws YappyException {
        if (input.isEmpty()) {
            throw new YappyException("OOPS!!! Please type a command.");
        }

        Command command = Command.fromInput(input);
        switch (command) {
        case LIST:
            printTaskList(tasks);
            break;
        case MARK:
            markTask(input, tasks);
            break;
        case UNMARK:
            unmarkTask(input, tasks);
            break;
        case DELETE:
            deleteTask(input, tasks);
            break;
        case TODO:
            addTodo(tasks, input);
            break;
        case DEADLINE:
            addDeadline(tasks, input);
            break;
        case EVENT:
            addEvent(tasks, input);
            break;
        default:
            throw new YappyException("OOPS!!! I don't know what that means. Try todo, deadline, event, list, mark, unmark, or delete.");
        }
    }

    /**
     * Returns the user's task text after the command word.
     */
    private static String getTextAfterCommand(String input, Command command) {
        return input.substring(command.getWord().length()).trim();
    }

    /**
     * Prints all stored tasks in their current order.
     */
    private static void printTaskList(List<Task> tasks) {
        System.out.println("Here are the tasks in your list:");
        // The numbering shown to the user starts at 1, while ArrayList is 0-indexed.
        for (int i = 0; i < tasks.size(); i++) {
            System.out.println((i + 1) + "." + tasks.get(i));
        }
    }

    /**
     * Adds a todo task after checking that its description is present.
     */
    private static void addTodo(List<Task> tasks, String input) throws YappyException {
        String description = getTextAfterCommand(input, Command.TODO);
        if (description.isEmpty()) {
            throw new YappyException("OOPS!!! The description of a todo cannot be empty.");
        }

        addTask(tasks, new Todo(description));
    }

    /**
     * Adds the given task to the task list.
     */
    private static void addTask(List<Task> tasks, Task task) {
        tasks.add(task);
        System.out.println("Got it. I've added this task:");
        System.out.println("  " + task);
        System.out.println("Now you have " + tasks.size() + " tasks in the list.");
    }

    /**
     * Parses a deadline command and adds the resulting deadline task.
     */
    private static void addDeadline(List<Task> tasks, String input) throws YappyException {
        String taskDetails = getTextAfterCommand(input, Command.DEADLINE);
        int byIndex = taskDetails.indexOf(BY_MARKER);

        if (byIndex == -1) {
            throw new YappyException("OOPS!!! Please use: deadline DESCRIPTION /by WHEN");
        }

        String description = taskDetails.substring(0, byIndex).trim();
        String by = taskDetails.substring(byIndex + BY_MARKER.length()).trim();
        if (description.isEmpty()) {
            throw new YappyException("OOPS!!! The description of a deadline cannot be empty.");
        }
        if (by.isEmpty()) {
            throw new YappyException("OOPS!!! The /by value of a deadline cannot be empty.");
        }

        addTask(tasks, new Deadline(description, by));
    }

    /**
     * Parses an event command and adds the resulting event task.
     */
    private static void addEvent(List<Task> tasks, String input) throws YappyException {
        String taskDetails = getTextAfterCommand(input, Command.EVENT);
        int fromIndex = taskDetails.indexOf(FROM_MARKER);
        int toIndex = fromIndex == -1 ? -1 : taskDetails.indexOf(TO_MARKER, fromIndex + FROM_MARKER.length());

        if (fromIndex == -1 || toIndex == -1) {
            throw new YappyException("OOPS!!! Please use: event DESCRIPTION /from START /to END");
        }

        String description = taskDetails.substring(0, fromIndex).trim();
        String from = taskDetails.substring(fromIndex + FROM_MARKER.length(), toIndex).trim();
        String to = taskDetails.substring(toIndex + TO_MARKER.length()).trim();
        if (description.isEmpty()) {
            throw new YappyException("OOPS!!! The description of an event cannot be empty.");
        }
        if (from.isEmpty()) {
            throw new YappyException("OOPS!!! The /from value of an event cannot be empty.");
        }
        if (to.isEmpty()) {
            throw new YappyException("OOPS!!! The /to value of an event cannot be empty.");
        }

        addTask(tasks, new Event(description, from, to));
    }

    /**
     * Marks the requested task as done.
     */
    private static void markTask(String input, List<Task> tasks) throws YappyException {
        int index = parseTaskIndex(input, Command.MARK, tasks.size());
        Task task = tasks.get(index);
        task.markAsDone();
        System.out.println("Nice! I've marked this task as done:");
        System.out.println("  " + task);
    }

    /**
     * Marks the requested task as not done yet.
     */
    private static void unmarkTask(String input, List<Task> tasks) throws YappyException {
        int index = parseTaskIndex(input, Command.UNMARK, tasks.size());
        Task task = tasks.get(index);
        task.markAsNotDone();
        System.out.println("OK, I've marked this task as not done yet:");
        System.out.println("  " + task);
    }

    /**
     * Deletes the requested task from the task list.
     */
    private static void deleteTask(String input, List<Task> tasks) throws YappyException {
        int index = parseTaskIndex(input, Command.DELETE, tasks.size());
        Task removedTask = tasks.remove(index);
        System.out.println("Noted. I've removed this task:");
        System.out.println("  " + removedTask);
        System.out.println("Now you have " + tasks.size() + " tasks in the list.");
    }

    /**
     * Converts the user's 1-based task number into a valid array index.
     */
    private static int parseTaskIndex(String input, Command command, int taskCount) throws YappyException {
        String numberText = getTextAfterCommand(input, command);
        if (numberText.isEmpty()) {
            throw new YappyException("OOPS!!! Please tell me which task to "
                    + command.getWord() + ", e.g. " + command.getWord() + " 1.");
        }

        int taskNumber;
        try {
            taskNumber = Integer.parseInt(numberText);
        } catch (NumberFormatException e) {
            throw new YappyException("OOPS!!! Task numbers must be whole numbers.");
        }

        if (taskCount == 0) {
            throw new YappyException("OOPS!!! There are no tasks in the list yet.");
        }
        if (taskNumber < 1 || taskNumber > taskCount) {
            throw new YappyException("OOPS!!! Task number must be between 1 and " + taskCount + ".");
        }

        return taskNumber - 1;
    }
}
