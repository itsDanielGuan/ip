/**
 * Entry point of the Yappy chatbot.
 * At this stage the bot simply greets the user and exits.
 */
public class Yappy {
    /** Name the chatbot introduces itself with. */
    private static final String NAME = "Yappy";

    /** Horizontal line used to separate the chatbot's messages from the user's input. */
    private static final String DIVIDER = "____________________________________________________________";

    public static void main(String[] args) {
        // ASCII art logo. Every backslash is written as \\ because \ is Java's escape character.
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
        System.out.println("Bye. Hope to see you again soon!");
        System.out.println(DIVIDER);
    }
}
