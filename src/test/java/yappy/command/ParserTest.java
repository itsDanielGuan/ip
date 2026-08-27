package yappy.command;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import yappy.exception.YappyException;
import yappy.task.Deadline;
import yappy.task.Event;

public class ParserTest {
    @Test
    public void getCommand_knownUnknownAndBlankInput_returnsMatchingCommand() {
        assertAll(
                () -> assertEquals(Command.TODO, Parser.getCommand("todo read book")),
                () -> assertEquals(Command.UNKNOWN, Parser.getCommand("dance")),
                () -> assertEquals(Command.UNKNOWN, Parser.getCommand("   "))
        );
    }

    @Test
    public void parseDeadline_validIsoDate_returnsFormattedDeadline() throws YappyException {
        Deadline deadline = Parser.parseDeadline("deadline submit report /by 2026-08-31");

        assertEquals("[D][ ] submit report (by: Aug 31 2026)", deadline.toString());
    }

    @Test
    public void parseDeadline_invalidDate_throwsHelpfulException() {
        YappyException exception = assertThrows(YappyException.class,
                () -> Parser.parseDeadline("deadline submit report /by 2026-02-30"));

        assertEquals("OOPS!!! Please enter the /by date as yyyy-MM-dd, e.g. 2019-10-15.",
                exception.getMessage());
    }

    @Test
    public void parseEvent_validDateRange_returnsFormattedEvent() throws YappyException {
        Event event = Parser.parseEvent("event camp /from 2026-08-30 /to 2026-09-01");

        assertEquals("[E][ ] camp (from: Aug 30 2026 to: Sep 01 2026)", event.toString());
    }

    @Test
    public void parseEvent_reversedDateRange_throwsHelpfulException() {
        YappyException exception = assertThrows(YappyException.class,
                () -> Parser.parseEvent("event camp /from 2026-09-01 /to 2026-08-30"));

        assertEquals("OOPS!!! An event's /to date cannot be before its /from date.",
                exception.getMessage());
    }

    @Test
    public void parseTaskIndex_validAndInvalidNumbers_returnsIndexOrThrows() throws YappyException {
        assertEquals(1, Parser.parseTaskIndex("mark 2", Command.MARK, 3));
        assertThrows(YappyException.class,
                () -> Parser.parseTaskIndex("mark", Command.MARK, 3));
        assertThrows(YappyException.class,
                () -> Parser.parseTaskIndex("mark two", Command.MARK, 3));
        assertThrows(YappyException.class,
                () -> Parser.parseTaskIndex("mark 4", Command.MARK, 3));
    }

    @Test
    public void parseFindKeyword_presentAndMissingKeyword_returnsKeywordOrThrows() throws YappyException {
        assertEquals("project book", Parser.parseFindKeyword("find project book"));
        assertThrows(YappyException.class, () -> Parser.parseFindKeyword("find"));
    }
}
