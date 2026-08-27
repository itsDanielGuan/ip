package yappy.storage;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import yappy.task.Deadline;
import yappy.task.Event;
import yappy.task.Task;
import yappy.task.TaskList;
import yappy.task.Todo;

public class StorageTest {
    @TempDir
    private Path tempDirectory;

    @Test
    public void load_missingFile_returnsEmptyTaskList() throws Exception {
        Storage storage = new Storage(tempDirectory.resolve("data/yappy.txt"));

        List<Task> loadedTasks = storage.load();

        assertAll(
                () -> assertTrue(loadedTasks.isEmpty()),
                () -> assertEquals(0, storage.getSkippedRecordCount())
        );
    }

    @Test
    public void saveAndLoad_multipleTaskTypes_preservesAllTaskData() throws Exception {
        Path dataFile = tempDirectory.resolve("nested/yappy.txt");
        Storage storage = new Storage(dataFile);
        TaskList originalTasks = new TaskList();
        Todo todo = new Todo("read | review notes");
        Deadline deadline = new Deadline("submit report", LocalDate.of(2026, 8, 31));
        Event event = new Event("camp", LocalDate.of(2026, 9, 1), LocalDate.of(2026, 9, 3));
        deadline.markAsDone();
        originalTasks.add(todo);
        originalTasks.add(deadline);
        originalTasks.add(event);

        storage.save(originalTasks);
        List<Task> loadedTasks = storage.load();

        assertAll(
                () -> assertTrue(Files.exists(dataFile)),
                () -> assertEquals(3, loadedTasks.size()),
                () -> assertEquals(todo.toDataString(), loadedTasks.get(0).toDataString()),
                () -> assertEquals(deadline.toDataString(), loadedTasks.get(1).toDataString()),
                () -> assertEquals(event.toDataString(), loadedTasks.get(2).toDataString())
        );
    }

    @Test
    public void load_corruptRecord_skipsOnlyCorruptRecord() throws Exception {
        Path dataFile = tempDirectory.resolve("yappy.txt");
        String validRecord = new Todo("recover me").toDataString();
        Files.write(dataFile, List.of("not a task", validRecord), StandardCharsets.UTF_8);
        Storage storage = new Storage(dataFile);

        List<Task> loadedTasks = storage.load();

        assertAll(
                () -> assertEquals(1, loadedTasks.size()),
                () -> assertEquals(1, storage.getSkippedRecordCount())
        );
    }
}
