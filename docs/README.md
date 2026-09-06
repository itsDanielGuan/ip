# Yappy User Guide

Yappy is a desktop and console task manager for people who want a quick way to
record tasks and check approaching deadlines. Run `Yappy.jar`, then type a
command in the console or the GUI input box. Dates use the ISO format
`yyyy-MM-dd`.

![Yappy's full conversation window](Ui.png)

## Commands at a glance

| Command | Purpose |
| --- | --- |
| `todo DESCRIPTION` | Add a task without a date. |
| `deadline DESCRIPTION /by DATE` | Add a task due on a date. |
| `event DESCRIPTION /from DATE /to DATE` | Add an event date range. |
| `list` | List all tasks. |
| `find KEYWORD` | List tasks whose description contains the keyword. |
| `remind DAYS` | List incomplete deadlines due today through the next number of days. |
| `mark NUMBER` / `unmark NUMBER` | Mark a task done or not done. |
| `delete NUMBER` | Delete a task. |
| `bye` | Exit Yappy. |

Task numbers shown by `list`, `find`, and `remind` start at 1. Only numbers
from the main task list can be used with `mark`, `unmark`, or `delete`.

## Adding tasks

Add a todo when it has no date:

```text
todo borrow book
```

Add a deadline using a valid calendar date:

```text
deadline submit report /by 2026-09-18
```

Yappy displays it as:

```text
[D][ ] submit report (by: Sep 18 2026)
```

Add an event with an inclusive start and end date. The end date cannot be
before the start date.

```text
event orientation /from 2026-09-14 /to 2026-09-18
```

## Viewing and finding tasks

Use `list` to view every task. Use `find` to search descriptions without
case sensitivity:

```text
find REPORT
```

## Getting deadline reminders

`remind DAYS` shows incomplete deadlines from today up to and including the
specified number of days ahead. It does not change the task list, and it omits
completed and overdue deadlines so the result focuses on upcoming work.

```text
remind 7
```

If no deadline is due in that period, Yappy confirms that the reminder list is
clear. `DAYS` must be a whole number of zero or more; `remind 0` checks only
today's deadlines.

## Managing tasks

Use task numbers from `list` to change a task's status or remove it:

```text
mark 2
unmark 2
delete 3
```

Yappy saves the task list automatically after adding, marking, unmarking, or
deleting a task. The saved data is stored in `data/yappy.txt` relative to the
folder from which Yappy runs. If the file or its parent folder does not exist,
Yappy creates it when it first needs to save.

## Errors

Yappy reports invalid commands without changing the task list. In particular,
check that task descriptions are present, task numbers are whole numbers in
range, and dates are real `yyyy-MM-dd` dates.
