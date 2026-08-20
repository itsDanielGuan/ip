import java.util.Scanner;

/**
 * Entry point of the Yappy chatbot.
 * At this stage the bot stores each line the user types as a Task, lists
 * them back, marks them done or not done, and exits on the "bye" command.
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

        // Each Task carries its own description and done-status, so a single
        // array replaces the parallel tasks/isDone arrays used before.
        // taskCount tracks how many slots are actually filled.
        Task[] tasks = new Task[MAX_TASKS];
        int taskCount = 0;

        // Scanner reads the user's input from the keyboard (System.in), one line at a time.
        Scanner scanner = new Scanner(System.in);

        // Keep reading until the user says "bye". hasNextLine() guards against the
        // input ending unexpectedly (e.g. Ctrl+D, or piping a file that has no "bye").
        while (scanner.hasNextLine()) {
            String input = scanner.nextLine();

            if (input.equals(COMMAND_BYE)) {
                break;
            }

            System.out.println(DIVIDER);
            if (input.equals(COMMAND_LIST)) {
                System.out.println("Here are the tasks in your list:");
                // The numbering shown to the user starts at 1, while the array is 0-indexed.
                for (int i = 0; i < taskCount; i++) {
                    System.out.println((i + 1) + "." + tasks[i]);
                }
            } else if (input.startsWith(COMMAND_MARK + " ")) {
                // Convert the number the user typed into the matching array index.
                int index = Integer.parseInt(input.substring(COMMAND_MARK.length()).trim()) - 1;
                tasks[index].markAsDone();
                System.out.println("Nice! I've marked this task as done:");
                System.out.println("  " + tasks[index]);
            } else if (input.startsWith(COMMAND_UNMARK + " ")) {
                int index = Integer.parseInt(input.substring(COMMAND_UNMARK.length()).trim()) - 1;
                tasks[index].markAsNotDone();
                System.out.println("OK, I've marked this task as not done yet:");
                System.out.println("  " + tasks[index]);
            } else if (taskCount < MAX_TASKS) {
                tasks[taskCount] = new Task(input);
                taskCount++;
                System.out.println("added: " + input);
            } else {
                System.out.println("Sorry, I can only remember " + MAX_TASKS + " items.");
            }
            System.out.println(DIVIDER);
        }

        System.out.println(DIVIDER);
        System.out.println("Bye. Hope to see you again soon!");
        System.out.println(DIVIDER);
    }
}
