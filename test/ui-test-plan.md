# UI Test Plan

These tests exercise the console behavior for Duke Level 5. Expected output blocks list fragments that must appear in order; the banner and divider lines may also appear in the actual console output.

## Test Case 1: Add and List the Three Task Types

Aim: Verify that todos, deadlines, and events are added with the correct type icons and displayed by `list`.

Commands:
```text
todo borrow book
deadline return book /by Sunday
event project meeting /from Mon 2pm /to 4pm
list
bye
```

Expected output fragments:
```text
Got it. I've added this task:
  [T][ ] borrow book
Now you have 1 tasks in the list.
Got it. I've added this task:
  [D][ ] return book (by: Sunday)
Now you have 2 tasks in the list.
Got it. I've added this task:
  [E][ ] project meeting (from: Mon 2pm to: 4pm)
Now you have 3 tasks in the list.
Here are the tasks in your list:
1.[T][ ] borrow book
2.[D][ ] return book (by: Sunday)
3.[E][ ] project meeting (from: Mon 2pm to: 4pm)
Bye. Hope to see you again soon!
```

## Test Case 2: Mark and Unmark Typed Tasks

Aim: Verify that inherited done-status behavior works for todo, deadline, and event subclasses.

Commands:
```text
todo read book
deadline submit report /by 11/10/2019 5pm
event orientation week /from 4/10/2019 /to 11/10/2019
mark 2
unmark 2
mark 3
list
bye
```

Expected output fragments:
```text
Nice! I've marked this task as done:
  [D][X] submit report (by: 11/10/2019 5pm)
OK, I've marked this task as not done yet:
  [D][ ] submit report (by: 11/10/2019 5pm)
Nice! I've marked this task as done:
  [E][X] orientation week (from: 4/10/2019 to: 11/10/2019)
Here are the tasks in your list:
1.[T][ ] read book
2.[D][ ] submit report (by: 11/10/2019 5pm)
3.[E][X] orientation week (from: 4/10/2019 to: 11/10/2019)
Bye. Hope to see you again soon!
```

## Test Case 3: Keep Deadline Date/Time as Raw Text

Aim: Verify that deadline date/time text is stored and printed exactly as typed, without date parsing.

Commands:
```text
deadline do homework /by no idea :-p
list
bye
```

Expected output fragments:
```text
Got it. I've added this task:
  [D][ ] do homework (by: no idea :-p)
Now you have 1 tasks in the list.
Here are the tasks in your list:
1.[D][ ] do homework (by: no idea :-p)
Bye. Hope to see you again soon!
```

## Test Case 4: Reject Unknown and Empty Todo Inputs Without Changing State

Aim: Verify that empty commands, unknown commands, and empty todo descriptions report errors and do not add tasks.

Commands:
```text
todo keep state

todo
blah
list
bye
```

Expected output fragments:
```text
Got it. I've added this task:
  [T][ ] keep state
Now you have 1 tasks in the list.
OOPS!!! Please type a command.
OOPS!!! The description of a todo cannot be empty.
OOPS!!! I don't know what that means. Try todo, deadline, event, list, mark, or unmark.
Here are the tasks in your list:
1.[T][ ] keep state
Bye. Hope to see you again soon!
```

## Test Case 5: Reject Invalid Deadline and Event Inputs Without Changing State

Aim: Verify that missing or empty deadline/event fields report specific errors and only valid typed tasks are stored.

Commands:
```text
deadline /by Friday
deadline pay bills
deadline pay bills /by
deadline pay bills /by Friday
event /from Mon /to Tue
event meeting /from /to Tue
event meeting /from Mon
event meeting /from Mon /to
event meeting /from Mon /to Tue
list
bye
```

Expected output fragments:
```text
OOPS!!! The description of a deadline cannot be empty.
OOPS!!! Please use: deadline DESCRIPTION /by WHEN
OOPS!!! The /by value of a deadline cannot be empty.
Got it. I've added this task:
  [D][ ] pay bills (by: Friday)
Now you have 1 tasks in the list.
OOPS!!! The description of an event cannot be empty.
OOPS!!! The /from value of an event cannot be empty.
OOPS!!! Please use: event DESCRIPTION /from START /to END
OOPS!!! The /to value of an event cannot be empty.
Got it. I've added this task:
  [E][ ] meeting (from: Mon to: Tue)
Now you have 2 tasks in the list.
Here are the tasks in your list:
1.[D][ ] pay bills (by: Friday)
2.[E][ ] meeting (from: Mon to: Tue)
Bye. Hope to see you again soon!
```

## Test Case 6: Reject Invalid Mark and Unmark Inputs Without Changing State

Aim: Verify that missing, non-numeric, and out-of-range task numbers report errors and do not change task status.

Commands:
```text
mark 1
todo alpha
mark
mark two
mark 5
mark 1
unmark
unmark 0
unmark one
unmark 1
list
bye
```

Expected output fragments:
```text
OOPS!!! There are no tasks in the list yet.
Got it. I've added this task:
  [T][ ] alpha
Now you have 1 tasks in the list.
OOPS!!! Please tell me which task to mark, e.g. mark 1.
OOPS!!! Task numbers must be whole numbers.
OOPS!!! Task number must be between 1 and 1.
Nice! I've marked this task as done:
  [T][X] alpha
OOPS!!! Please tell me which task to unmark, e.g. unmark 1.
OOPS!!! Task number must be between 1 and 1.
OOPS!!! Task numbers must be whole numbers.
OK, I've marked this task as not done yet:
  [T][ ] alpha
Here are the tasks in your list:
1.[T][ ] alpha
Bye. Hope to see you again soon!
```
