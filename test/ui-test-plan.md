# UI Test Plan

These tests exercise the console behavior for Duke Level 4. Expected output blocks list fragments that must appear in order; the banner and divider lines may also appear in the actual console output.

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
