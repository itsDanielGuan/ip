package yappy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class YappyTest {
    @TempDir
    private Path tempDirectory;

    @Test
    public void getResponse_addListAndReload_returnsPersistentGuiMessages() {
        Path dataFile = tempDirectory.resolve("data/yappy.txt");
        Yappy yappy = new Yappy(dataFile);

        String welcomeMessage = yappy.getWelcomeMessage();
        String addResponse = yappy.getResponse("todo read book");
        String addCommandType = yappy.getCommandType();
        String listResponse = yappy.getResponse("list");
        String reloadedListResponse = new Yappy(dataFile).getResponse("list");

        assertTrue(welcomeMessage.contains("Hello! I'm Yappy."));
        assertTrue(addResponse.contains("[T][ ] read book"));
        assertEquals("AddCommand", addCommandType);
        assertTrue(listResponse.contains("1.[T][ ] read book"));
        assertEquals(listResponse, reloadedListResponse);
    }

    @Test
    public void getResponse_invalidAndByeCommands_returnsExpectedGuiMessages() {
        Yappy yappy = new Yappy(tempDirectory.resolve("yappy.txt"));

        String invalidResponse = yappy.getResponse("todo");
        String goodbyeResponse = yappy.getResponse("bye");

        assertEquals("OOPS!!! The description of a todo cannot be empty.", invalidResponse);
        assertEquals("Bye. Hope to see you again soon!", goodbyeResponse);
    }
}
