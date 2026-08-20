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

    /** Maximum number of items the bot can remember, as set by the requirements. */
    private static final int MAX_TASKS = 100;

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

        // The array type is Task, but each item is one of its subclasses.
        // taskCount tracks how many slots are actually filled.
        Task[] tasks = new Task[MAX_TASKS];
        int taskCount = 0;

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
                taskCount = processInput(input, tasks, taskCount);
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
     * Runs one non-bye command and returns the updated task count.
     */
    private static int processInput(String input, Task[] tasks, int taskCount) throws YappyException {
        if (input.isEmpty()) {
            throw new YappyException("OOPS!!! Please type a command.");
        }

        if (input.equals(COMMAND_LIST)) {
            printTaskList(tasks, taskCount);
            return taskCount;
        } else if (isCommand(input, COMMAND_MARK)) {
            markTask(input, tasks, taskCount);
            return taskCount;
        } else if (isCommand(input, COMMAND_UNMARK)) {
            unmarkTask(input, tasks, taskCount);
            return taskCount;
        } else if (isCommand(input, COMMAND_TODO)) {
            return addTodo(tasks, taskCount, input);
        } else if (isCommand(input, COMMAND_DEADLINE)) {
            return addDeadline(tasks, taskCount, input);
        } else if (isCommand(input, COMMAND_EVENT)) {
            return addEvent(tasks, taskCount, input);
        } else {
            throw new YappyException("OOPS!!! I don't know what that means. Try todo, deadline, event, list, mark, or unmark.");
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
    private static void printTaskList(Task[] tasks, int taskCount) {
        System.out.println("Here are the tasks in your list:");
        // The numbering shown to the user starts at 1, while the array is 0-indexed.
        for (int i = 0; i < taskCount; i++) {
            System.out.println((i + 1) + "." + tasks[i]);
        }
    }

    /**
     * Adds a todo task after checking that its description is present.
     */
    private static int addTodo(Task[] tasks, int taskCount, String input) throws YappyException {
        String description = getTextAfterCommand(input, COMMAND_TODO);
        if (description.isEmpty()) {
            throw new YappyException("OOPS!!! The description of a todo cannot be empty.");
        }

        return addTask(tasks, taskCount, new Todo(description));
    }

    /**
     * Adds the given task if there is space in the task list.
     */
    private static int addTask(Task[] tasks, int taskCount, Task task) throws YappyException {
        if (taskCount >= MAX_TASKS) {
            throw new YappyException("OOPS!!! I can only remember " + MAX_TASKS + " tasks.");
        }

        tasks[taskCount] = task;
        int newTaskCount = taskCount + 1;
        System.out.println("Got it. I've added this task:");
        System.out.println("  " + task);
        System.out.println("Now you have " + newTaskCount + " tasks in the list.");
        return newTaskCount;
    }

    /**
     * Parses a deadline command and adds the resulting deadline task.
     */
    private static int addDeadline(Task[] tasks, int taskCount, String input) throws YappyException {
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

        return addTask(tasks, taskCount, new Deadline(description, by));
    }

    /**
     * Parses an event command and adds the resulting event task.
     */
    private static int addEvent(Task[] tasks, int taskCount, String input) throws YappyException {
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

        return addTask(tasks, taskCount, new Event(description, from, to));
    }

    /**
     * Marks the requested task as done.
     */
    private static void markTask(String input, Task[] tasks, int taskCount) throws YappyException {
        int index = parseTaskIndex(input, COMMAND_MARK, taskCount);
        tasks[index].markAsDone();
        System.out.println("Nice! I've marked this task as done:");
        System.out.println("  " + tasks[index]);
    }

    /**
     * Marks the requested task as not done yet.
     */
    private static void unmarkTask(String input, Task[] tasks, int taskCount) throws YappyException {
        int index = parseTaskIndex(input, COMMAND_UNMARK, taskCount);
        tasks[index].markAsNotDone();
        System.out.println("OK, I've marked this task as not done yet:");
        System.out.println("  " + tasks[index]);
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
