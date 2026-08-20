import java.util.Scanner;

/**
 * Entry point of the Yappy chatbot.
 * At this stage the bot echoes back whatever the user types,
 * and exits when the user enters the command "bye".
 */
public class Yappy {
    /** Name the chatbot introduces itself with. */
    private static final String NAME = "Yappy";

    /** Horizontal line used to separate the chatbot's messages from the user's input. */
    private static final String DIVIDER = "____________________________________________________________";

    /** Command that ends the conversation. */
    private static final String COMMAND_BYE = "bye";

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

        // Scanner reads the user's input from the keyboard (System.in), one line at a time.
        Scanner scanner = new Scanner(System.in);

        // Keep echoing until the user says "bye". hasNextLine() guards against the
        // input ending unexpectedly (e.g. Ctrl+D, or piping a file that has no "bye").
        while (scanner.hasNextLine()) {
            String input = scanner.nextLine();

            if (input.equals(COMMAND_BYE)) {
                break;
            }

            System.out.println(DIVIDER);
            System.out.println(input);
            System.out.println(DIVIDER);
        }

        System.out.println(DIVIDER);
        System.out.println("Bye. Hope to see you again soon!");
        System.out.println(DIVIDER);
    }
}
