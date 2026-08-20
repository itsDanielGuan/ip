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

    /** Command that ends the conversation. */
    private static final String COMMAND_BYE = "bye";

    /** Command that lists everything stored so far. */
    private static final String COMMAND_LIST = "list";

    /** Command that marks a task as done, e.g. "mark 2". */
    private static final String COMMAND_MARK = "mark";

    /** Command that reverses a task back to not done, e.g. "unmark 2". */
    private static final String COMMAND_UNMARK = "unmark";

    /** Command that deletes a task, e.g. "delete 2". */
    private static final String COMMAND_DELETE = "delete";

    /** Command that adds a todo task, e.g. "todo borrow book". */
    private static final String COMMAND_TODO = "todo";

    /** Command that adds a deadline task, e.g. "deadline return book /by Sunday". */
    private static final String COMMAND_DEADLINE = "deadline";

    /** Command that adds an event task, e.g. "event meeting /from Mon 2pm /to 4pm". */
    private static final String COMMAND_EVENT = "event";

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

            if (input.equals(COMMAND_BYE)) {
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

        if (input.equals(COMMAND_LIST)) {
            printTaskList(tasks);
        } else if (isCommand(input, COMMAND_MARK)) {
            markTask(input, tasks);
        } else if (isCommand(input, COMMAND_UNMARK)) {
            unmarkTask(input, tasks);
        } else if (isCommand(input, COMMAND_DELETE)) {
            deleteTask(input, tasks);
        } else if (isCommand(input, COMMAND_TODO)) {
            addTodo(tasks, input);
        } else if (isCommand(input, COMMAND_DEADLINE)) {
            addDeadline(tasks, input);
        } else if (isCommand(input, COMMAND_EVENT)) {
            addEvent(tasks, input);
        } else {
            throw new YappyException("OOPS!!! I don't know what that means. Try todo, deadline, event, list, mark, unmark, or delete.");
        }
    }

    /**
     * Returns true if input is exactly the command or starts with the command followed by a space.
     */
    private static boolean isCommand(String input, String command) {
        return input.equals(command) || input.startsWith(command + " ");
    }

    /**
     * Returns the user's task text after the command word.
     */
    private static String getTextAfterCommand(String input, String command) {
        return input.substring(command.length()).trim();
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
        String description = getTextAfterCommand(input, COMMAND_TODO);
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
        String taskDetails = getTextAfterCommand(input, COMMAND_DEADLINE);
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
        String taskDetails = getTextAfterCommand(input, COMMAND_EVENT);
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
        int index = parseTaskIndex(input, COMMAND_MARK, tasks.size());
        Task task = tasks.get(index);
        task.markAsDone();
        System.out.println("Nice! I've marked this task as done:");
        System.out.println("  " + task);
    }

    /**
     * Marks the requested task as not done yet.
     */
    private static void unmarkTask(String input, List<Task> tasks) throws YappyException {
        int index = parseTaskIndex(input, COMMAND_UNMARK, tasks.size());
        Task task = tasks.get(index);
        task.markAsNotDone();
        System.out.println("OK, I've marked this task as not done yet:");
        System.out.println("  " + task);
    }

    /**
     * Deletes the requested task from the task list.
     */
    private static void deleteTask(String input, List<Task> tasks) throws YappyException {
        int index = parseTaskIndex(input, COMMAND_DELETE, tasks.size());
        Task removedTask = tasks.remove(index);
        System.out.println("Noted. I've removed this task:");
        System.out.println("  " + removedTask);
        System.out.println("Now you have " + tasks.size() + " tasks in the list.");
    }

    /**
     * Converts the user's 1-based task number into a valid array index.
     */
    private static int parseTaskIndex(String input, String command, int taskCount) throws YappyException {
        String numberText = getTextAfterCommand(input, command);
        if (numberText.isEmpty()) {
            throw new YappyException("OOPS!!! Please tell me which task to " + command + ", e.g. " + command + " 1.");
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
