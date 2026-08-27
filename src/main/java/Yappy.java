import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Coordinates Yappy's user interface, command processing, task list, and storage.
 */
public class Yappy {
    private static final Path DATA_FILE = Paths.get("data", "yappy.txt");

    private final Storage storage;
    private final TaskList tasks;
    private final Ui ui;
    private final int skippedRecordCount;
    private final boolean loadFailed;

    /**
     * Creates a Yappy chatbot backed by the specified data file.
     */
    public Yappy(Path dataFile) {
        this.storage = new Storage(dataFile);
        this.ui = new Ui();

        TaskList loadedTasks;
        int skippedRecords = 0;
        boolean loadingFailed = false;
        try {
            loadedTasks = new TaskList(storage.load());
            skippedRecords = storage.getSkippedRecordCount();
        } catch (IOException e) {
            loadedTasks = new TaskList();
            loadingFailed = true;
        }
        this.tasks = loadedTasks;
        this.skippedRecordCount = skippedRecords;
        this.loadFailed = loadingFailed;
    }

    /**
     * Starts Yappy using its default relative data-file location.
     */
    public static void main(String[] args) {
        new Yappy(DATA_FILE).run();
    }

    /**
     * Reads and executes commands until the user exits or the input stream ends.
     */
    public void run() {
        ui.showWelcome();
        if (loadFailed) {
            ui.showLoadingError();
        } else if (skippedRecordCount > 0) {
            ui.showSkippedRecords(skippedRecordCount);
        }

        while (ui.hasNextCommand()) {
            String input = ui.readCommand();
            if (Parser.getCommand(input) == Command.BYE) {
                break;
            }

            ui.showLine();
            try {
                boolean taskListChanged = processInput(input);
                if (taskListChanged) {
                    storage.save(tasks);
                }
            } catch (YappyException e) {
                ui.showError(e.getMessage());
            } catch (IOException e) {
                ui.showSavingError(e.getMessage());
            } finally {
                ui.showLine();
            }
        }
        ui.showGoodbye();
    }

    /**
     * Executes one non-exit command and reports whether it changed the task list.
     */
    private boolean processInput(String input) throws YappyException {
        if (input.isEmpty()) {
            throw new YappyException("OOPS!!! Please type a command.");
        }

        Command command = Parser.getCommand(input);
        switch (command) {
        case LIST:
            ui.showTaskList(tasks);
            return false;
        case MARK:
            markTask(input);
            return true;
        case UNMARK:
            unmarkTask(input);
            return true;
        case DELETE:
            deleteTask(input);
            return true;
        case TODO:
            addTask(Parser.parseTodo(input));
            return true;
        case DEADLINE:
            addTask(Parser.parseDeadline(input));
            return true;
        case EVENT:
            addTask(Parser.parseEvent(input));
            return true;
        default:
            throw new YappyException("OOPS!!! I don't know what that means. "
                    + "Try todo, deadline, event, list, mark, unmark, or delete.");
        }
    }

    /**
     * Adds a task and tells the user about the updated list.
     */
    private void addTask(Task task) {
        tasks.add(task);
        ui.showTaskAdded(task, tasks.size());
    }

    /**
     * Marks the requested task as done.
     */
    private void markTask(String input) throws YappyException {
        int index = Parser.parseTaskIndex(input, Command.MARK, tasks.size());
        Task task = tasks.get(index);
        task.markAsDone();
        ui.showTaskMarked(task);
    }

    /**
     * Marks the requested task as not done yet.
     */
    private void unmarkTask(String input) throws YappyException {
        int index = Parser.parseTaskIndex(input, Command.UNMARK, tasks.size());
        Task task = tasks.get(index);
        task.markAsNotDone();
        ui.showTaskUnmarked(task);
    }

    /**
     * Deletes the requested task.
     */
    private void deleteTask(String input) throws YappyException {
        int index = Parser.parseTaskIndex(input, Command.DELETE, tasks.size());
        Task removedTask = tasks.remove(index);
        ui.showTaskDeleted(removedTask, tasks.size());
    }
}
