import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Base64;
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

        TaskList tasks = new TaskList(loadTasks(DATA_FILE));

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
                boolean taskListChanged = processInput(input, tasks);
                if (taskListChanged) {
                    saveTasks(tasks, DATA_FILE);
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

        Command command = Command.fromInput(input);
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
            addTodo(tasks, input);
            return true;
        case DEADLINE:
            addDeadline(tasks, input);
            return true;
        case EVENT:
            addEvent(tasks, input);
            return true;
        default:
            throw new YappyException("OOPS!!! I don't know what that means. Try todo, deadline, event, list, mark, unmark, or delete.");
        }
    }

    /**
     * Loads all valid task records from the data file.
     * A missing file represents a user who has not saved any tasks yet.
     */
    private static List<Task> loadTasks(Path dataFile) {
        List<Task> tasks = new ArrayList<>();
        if (Files.notExists(dataFile)) {
            return tasks;
        }

        int skippedRecords = 0;
        try {
            for (String line : Files.readAllLines(dataFile, StandardCharsets.UTF_8)) {
                if (line.isBlank()) {
                    continue;
                }
                try {
                    tasks.add(parseStoredTask(line));
                } catch (IllegalArgumentException e) {
                    skippedRecords++;
                }
            }
        } catch (IOException e) {
            System.out.println("OOPS!!! I could not load your saved tasks. Starting with an empty list.");
            return new ArrayList<>();
        }

        if (skippedRecords > 0) {
            System.out.println("OOPS!!! I skipped " + skippedRecords + " invalid saved task record(s).");
        }
        return tasks;
    }

    /**
     * Parses one task record written by {@link Task#toDataString()}.
     */
    private static Task parseStoredTask(String line) {
        String[] fields = line.split(" \\| ", -1);
        if (fields.length < 3) {
            throw new IllegalArgumentException("Too few fields");
        }

        boolean isDone;
        if (fields[1].equals("1")) {
            isDone = true;
        } else if (fields[1].equals("0")) {
            isDone = false;
        } else {
            throw new IllegalArgumentException("Invalid task status");
        }

        Task task;
        switch (fields[0]) {
        case "T":
            requireFieldCount(fields, 3);
            task = new Todo(decodeDescription(fields[2]));
            break;
        case "D":
            requireFieldCount(fields, 4);
            task = new Deadline(decodeDescription(fields[2]), parseStoredDate(fields[3]));
            break;
        case "E":
            requireFieldCount(fields, 5);
            task = new Event(decodeDescription(fields[2]), parseStoredDate(fields[3]),
                    parseStoredDate(fields[4]));
            break;
        default:
            throw new IllegalArgumentException("Unknown task type");
        }

        if (isDone) {
            task.markAsDone();
        }
        return task;
    }

    /**
     * Verifies that a stored task record has exactly the expected number of fields.
     */
    private static void requireFieldCount(String[] fields, int expectedCount) {
        if (fields.length != expectedCount) {
            throw new IllegalArgumentException("Unexpected field count");
        }
    }

    /**
     * Decodes one Base64 text field from a stored task record.
     */
    private static String decode(String text) {
        return new String(Base64.getDecoder().decode(text), StandardCharsets.UTF_8);
    }

    /**
     * Decodes and validates the required description field of a stored task.
     */
    private static String decodeDescription(String text) {
        String description = decode(text);
        if (description.isBlank()) {
            throw new IllegalArgumentException("Empty task description");
        }
        return description;
    }

    /**
     * Parses an ISO date stored in a Base64 data-file field.
     */
    private static LocalDate parseStoredDate(String text) {
        try {
            return LocalDate.parse(decode(text));
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid stored date", e);
        }
    }

    /**
     * Writes the complete task list, creating its parent directory when needed.
     */
    private static void saveTasks(TaskList tasks, Path dataFile) throws IOException {
        Path parentDirectory = dataFile.getParent();
        if (parentDirectory != null) {
            Files.createDirectories(parentDirectory);
        }

        List<String> records = new ArrayList<>();
        for (int i = 0; i < tasks.size(); i++) {
            records.add(tasks.get(i).toDataString());
        }
        Files.write(dataFile, records, StandardCharsets.UTF_8);
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
    private static void printTaskList(TaskList tasks) {
        System.out.println("Here are the tasks in your list:");
        // The numbering shown to the user starts at 1, while ArrayList is 0-indexed.
        for (int i = 0; i < tasks.size(); i++) {
            System.out.println((i + 1) + "." + tasks.get(i));
        }
    }

    /**
     * Adds a todo task after checking that its description is present.
     */
    private static void addTodo(TaskList tasks, String input) throws YappyException {
        String description = getTextAfterCommand(input, Command.TODO);
        if (description.isEmpty()) {
            throw new YappyException("OOPS!!! The description of a todo cannot be empty.");
        }

        addTask(tasks, new Todo(description));
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
     * Parses a deadline command and adds the resulting deadline task.
     */
    private static void addDeadline(TaskList tasks, String input) throws YappyException {
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

        addTask(tasks, new Deadline(description, parseDate(by, BY_MARKER)));
    }

    /**
     * Parses an event command and adds the resulting event task.
     */
    private static void addEvent(TaskList tasks, String input) throws YappyException {
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

        LocalDate fromDate = parseDate(from, FROM_MARKER);
        LocalDate toDate = parseDate(to, TO_MARKER);
        if (toDate.isBefore(fromDate)) {
            throw new YappyException("OOPS!!! An event's /to date cannot be before its /from date.");
        }

        addTask(tasks, new Event(description, fromDate, toDate));
    }

    /**
     * Parses a user-entered ISO date and reports a command-specific error when invalid.
     */
    private static LocalDate parseDate(String dateText, String marker) throws YappyException {
        try {
            return LocalDate.parse(dateText);
        } catch (DateTimeParseException e) {
            throw new YappyException("OOPS!!! Please enter the " + marker
                    + " date as yyyy-MM-dd, e.g. 2019-10-15.");
        }
    }

    /**
     * Marks the requested task as done.
     */
    private static void markTask(String input, TaskList tasks) throws YappyException {
        int index = parseTaskIndex(input, Command.MARK, tasks.size());
        Task task = tasks.get(index);
        task.markAsDone();
        System.out.println("Nice! I've marked this task as done:");
        System.out.println("  " + task);
    }

    /**
     * Marks the requested task as not done yet.
     */
    private static void unmarkTask(String input, TaskList tasks) throws YappyException {
        int index = parseTaskIndex(input, Command.UNMARK, tasks.size());
        Task task = tasks.get(index);
        task.markAsNotDone();
        System.out.println("OK, I've marked this task as not done yet:");
        System.out.println("  " + task);
    }

    /**
     * Deletes the requested task from the task list.
     */
    private static void deleteTask(String input, TaskList tasks) throws YappyException {
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
