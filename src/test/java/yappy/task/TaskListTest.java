package yappy.task;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class TaskListTest {
    @Test
    public void find_keywordWithDifferentCase_returnsMatchingDescriptionsOnly() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("Read Book"));
        tasks.add(new Todo("book flights"));
        tasks.add(new Todo("buy groceries"));

        TaskList matchingTasks = tasks.find("BOOK");

        assertEquals(2, matchingTasks.size());
        assertEquals("[T][ ] Read Book", matchingTasks.get(0).toString());
        assertEquals("[T][ ] book flights", matchingTasks.get(1).toString());
    }

    @Test
    public void find_missingKeyword_returnsEmptyTaskList() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));

        assertEquals(0, tasks.find("report").size());
    }
}
