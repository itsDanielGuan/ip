import java.util.Scanner;

/**
 * Entry point of the Yappy chatbot.
 * At this stage the bot stores each line the user types, lists them back
 * on the "list" command, and exits on the "bye" command.
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

        // A fixed-size array is enough here: the requirements cap the list at 100 items.
        // taskCount tracks how many slots are actually filled.
        String[] tasks = new String[MAX_TASKS];
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
                // The numbering shown to the user starts at 1, while the array is 0-indexed.
                for (int i = 0; i < taskCount; i++) {
                    System.out.println((i + 1) + ". " + tasks[i]);
                }
            } else if (taskCount < MAX_TASKS) {
                tasks[taskCount] = input;
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
