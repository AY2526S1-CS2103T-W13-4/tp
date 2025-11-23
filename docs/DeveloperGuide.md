---
layout: page
title: ClassRosterPro Developer Guide
---

* Table of Contents
{:toc}

--------------------------------------------------------------------------------------------------------------------

## **Acknowledgements**

* This project is adapted from SE-EDU's AddressBook-Level3 (AB3). We reused its architecture, code structure, and portions of documentation with modifications to suit our domain. See: AB3 repository (`https://github.com/se-edu/addressbook-level3`), UG, and DG.
* The command-parsing pattern and some utility classes are reused from AB3 with changes. Parser overview diagram concept adapted from AB3 `ParserClasses`.
* PlantUML is used for UML diagrams. Graphviz is used for rendering.

--------------------------------------------------------------------------------------------------------------------

## **Setting up, getting started**

Refer to the guide [_Setting up and getting started_](SettingUp.md).

--------------------------------------------------------------------------------------------------------------------

## **Design**

### Architecture

<img src="images/ArchitectureDiagram.png" width="280" />

The ***Architecture Diagram*** given above explains the high-level design of the App.

Given below is a quick overview of main components and how they interact with each other.

**Main components of the architecture**

**`Main`** (consisting of classes [`Main`](https://github.com/AY2526S1-CS2103T-W13-4/tp/blob/master/src/main/java/seedu/address/Main.java) and [`MainApp`](https://github.com/AY2526S1-CS2103T-W13-4/tp/blob/master/src/main/java/seedu/address/MainApp.java)) is in charge of the app launch and shut down.
* At app launch, it initializes the other components in the correct sequence, and connects them up with each other.
* At shut down, it shuts down the other components and invokes cleanup methods where necessary.

The bulk of the app's work is done by the following four components:

* [**`UI`**](#ui-component): The UI of the App.
* [**`Logic`**](#logic-component): The command executor.
* [**`Model`**](#model-component): Holds the data of the App in memory.
* [**`Storage`**](#storage-component): Reads data from, and writes data to, the hard disk.

[**`Commons`**](#common-classes) represents a collection of classes used by multiple other components.

**How the architecture components interact with each other**

The *Sequence Diagram* below shows how the components interact with each other for the scenario where the user issues the command `delete 1`.

<img src="images/ArchitectureSequenceDiagram.png" width="574" />

Each of the four main components (also shown in the diagram above),

* defines its *API* in an `interface` with the same name as the Component.
* implements its functionality using a concrete `{Component Name}Manager` class (which follows the corresponding API `interface` mentioned in the previous point.

For example, the `Logic` component defines its API in the `Logic.java` interface and implements its functionality using the `LogicManager.java` class which follows the `Logic` interface. Other components interact with a given component through its interface rather than the concrete class (reason: to prevent outside component's being coupled to the implementation of a component), as illustrated in the (partial) class diagram below.

<img src="images/ComponentManagers.png" width="300" />

The sections below give more details of each component.

### UI component

The **API** of this component is specified in [`Ui.java`](https://github.com/AY2526S1-CS2103T-W13-4/tp/blob/master/src/main/java/seedu/address/ui/Ui.java)

![Structure of the UI Component](images/UiClassDiagram.png)

The UI consists of a `MainWindow` that is made up of parts e.g.`CommandBox`, `ResultDisplay`, `PersonListPanel`, `StatusBarFooter` etc. All these, including the `MainWindow`, inherit from the abstract `UiPart` class which captures the commonalities between classes that represent parts of the visible GUI.

The `UI` component uses the JavaFx UI framework. The layout of these UI parts are defined in matching `.fxml` files that are in the `src/main/resources/view` folder. For example, the layout of the [`MainWindow`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/ui/MainWindow.java) is specified in [`MainWindow.fxml`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/resources/view/MainWindow.fxml)

The `UI` component,

* executes user commands using the `Logic` component.
* listens for changes to `Model` data so that the UI can be updated with the modified data.
* keeps a reference to the `Logic` component, because the `UI` relies on the `Logic` to execute commands.
* depends on some classes in the `Model` component, as it displays `Person` object residing in the `Model`.

### Logic component

**API** : [`Logic.java`](https://github.com/AY2526S1-CS2103T-W13-4/tp/blob/master/src/main/java/seedu/address/logic/Logic.java)

Here's a (partial) class diagram of the `Logic` component:

<img src="images/LogicClassDiagram.png" width="550"/>

The sequence diagram below illustrates the interactions within the `Logic` component, taking `execute("delete 1")` API call as an example.

![Interactions Inside the Logic Component for the `delete 1` Command](images/DeleteSequenceDiagram.png)

<div markdown="span" class="alert alert-info">:information_source: **Note:** The lifeline for `DeleteCommandParser` should end at the destroy marker (X) but due to a limitation of PlantUML, the lifeline continues till the end of diagram.
</div>

How the `Logic` component works:

1. When `Logic` is called upon to execute a command, it is passed to an `AddressBookParser` object which in turn creates a parser that matches the command (e.g., `DeleteCommandParser`) and uses it to parse the command.
2. This results in a `Command` object (more precisely, an object of one of its subclasses e.g., `DeleteCommand`) which is executed by the `LogicManager`.
3. The command can communicate with the `Model` when it is executed (e.g. to delete a person).<br>
   Note that although this is shown as a single step in the diagram above (for simplicity), in the code it can take several interactions (between the command object and the `Model`) to achieve.
4. The result of the command execution is encapsulated as a `CommandResult` object which is returned back from `Logic`.

Here are the other classes in `Logic` (omitted from the class diagram above) that are used for parsing a user command:

<img src="images/ParserClasses.png" width="600"/>

How the parsing works:
* When called upon to parse a user command, the `AddressBookParser` class creates an `XYZCommandParser` (`XYZ` is a placeholder for the specific command name e.g., `AddCommandParser`) which uses the other classes shown above to parse the user command and create a `XYZCommand` object (e.g., `AddCommand`) which the `AddressBookParser` returns back as a `Command` object.
* All `XYZCommandParser` classes (e.g., `AddCommandParser`, `DeleteCommandParser`, ...) inherit from the `Parser` interface so that they can be treated similarly where possible e.g, during testing.

### Model component
**API** : [`Model.java`](https://github.com/AY2526S1-CS2103T-W13-4/tp/blob/master/src/main/java/seedu/address/model/Model.java)

<img src="images/ModelClassDiagram.png" width="450" />


The `Model` component,

* stores the address book data i.e., all `Person` objects (which are contained in a `UniquePersonList` object).
* stores the currently 'selected' `Person` objects (e.g., results of a search query) as a separate _filtered_ list which is exposed to outsiders as an unmodifiable `ObservableList<Person>` that can be 'observed' e.g. the UI can be bound to this list so that the UI automatically updates when the data in the list change.
* stores a `UserPref` object that represents the user’s preferences. This is exposed to the outside as a `ReadOnlyUserPref` objects.
* does not depend on any of the other three components (as the `Model` represents data entities of the domain, they should make sense on their own without depending on other components)

<img src="images/BetterModelClassDiagram.png" width="450" />


### Storage component

**API** : [`Storage.java`](https://github.com/AY2526S1-CS2103T-W13-4/tp/blob/master/src/main/java/seedu/address/storage/Storage.java)

<img src="images/StorageClassDiagram.png" width="550" />

The `Storage` component,
* can save both address book data and user preference data in JSON format, and read them back into corresponding objects.
* inherits from both `AddressBookStorage` and `UserPrefStorage`, which means it can be treated as either one (if only the functionality of only one is needed).
* depends on some classes in the `Model` component (because the `Storage` component's job is to save/retrieve objects that belong to the `Model`)

### Common classes

Classes used by multiple components are in the `seedu.address.commons` package.

--------------------------------------------------------------------------------------------------------------------

## **Implementation**

This section describes some noteworthy details on how certain features are implemented.

### Help Command

#### What it does

Shows the in-app help window with a curated command summary so tutors can quickly recall syntax without leaving the application.

#### Execution walkthrough

When a user enters `help`, `LogicManager` instantiates `HelpCommand`. The command returns a `CommandResult` that sets the `showHelp` flag to `true`, instructing the UI to open (or refocus) the help window. The textual summary displayed comes from `HelpCommand.SHOWING_HELP_MESSAGE`.

#### Design considerations

- Keep help content in code to guarantee the window works even when offline.
- The window opens idempotently—the same command simply refocuses the existing help stage instead of spawning duplicates.
- The `CommandResult` flagging approach keeps UI behaviour configurable without introducing UI dependencies into the logic layer.

### List Command

#### What it does

Resets the student list back to the full roster after filters, searches, or attribute-based queries.

#### Execution walkthrough

`ListCommand#execute(Model)` invokes `model.updateFilteredPersonList(Model.PREDICATE_SHOW_ALL_PERSONS)`, restoring the observable list that backs the UI. The command then returns a simple confirmation message; no data is mutated.

#### Design considerations

- The command runs in O(n) because the filtered list wraps the master list—no deep copies are made.
- Display logic stays in the UI; `ListCommand` only manipulates the predicate to maintain separation of concerns.
- Returning an explicit acknowledgement helps the user confirm that the reset completed.

### Open Command

#### What it does

Expands a student's card in the UI so tutors can inspect lessons, grades, tags, and other extended details.

#### Execution walkthrough

`OpenCommand` resolves the target index against the current filtered list. It ensures the index is valid and that the card is not already expanded, then toggles the `Person`'s `expandedProperty` to `true`. The bound UI updates automatically and the command returns a confirmation message.

#### Design considerations

- Expansion state lives on the `Person` object, keeping UI behaviour consistent even when the list is resorted or filtered.
- Guard clauses prevent redundant state flips and provide clear error messages when the card is already open.
- Operations stay synchronous; no additional events or asynchronous callbacks are required.

### Close Command

#### What it does

Collapses an expanded student card to restore the compact list view.

#### Execution walkthrough

Similar to `OpenCommand`, `CloseCommand` validates the index, checks that the card is currently open, and flips the `expandedProperty` to `false`. A confirmation message indicates success; otherwise a descriptive error is thrown.

#### Design considerations

- Mirroring the open logic keeps the commands complementary and predictable.
- Using the same `expandedProperty` ensures toggling works regardless of how the card was opened (command or future UI triggers).
- Input validation reuses `Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX`, maintaining a consistent error vocabulary across commands.

### Clear Command

#### What it does

Wipes the entire roster, removing every stored student and resetting the dataset to a blank state.

#### Execution walkthrough

`ClearCommand#execute(Model)` constructs a new empty `AddressBook` instance and passes it to `model.setAddressBook(...)`. Because the model exposes an observable list, the UI immediately reflects the cleared roster. A confirmation message is returned to the user.

#### Design considerations

- The operation is destructive; users should be advised to back up data before running it. Undo is not available.
- Creating a fresh `AddressBook` is simpler than iterating through students, keeping the command O(1) relative to roster size.
- By reusing the setter in `Model`, storage and persistence layers automatically pick up the new state on the next save cycle.

### Exit Command

#### What it does

Terminates the application gracefully after acknowledging the user's request.

#### Execution walkthrough

`ExitCommand` returns a `CommandResult` with the `exit` flag set to `true`. `LogicManager` forwards this to the UI, which listens for the flag and triggers application shutdown while allowing final persistence tasks (e.g., saving preferences) to complete.

#### Design considerations

- The command never throws; exiting is always considered successful.
- Using flags in `CommandResult` keeps lifecycle management in the UI layer, avoiding logic-to-UI coupling.
- Any cleanup (saving logs, closing windows) can be centralised in the UI's response to the flag rather than scattered across commands.

### Add Student Command

#### What it does

Registers a new student in the roster. The created `Person` initially has empty remark, grade, and lesson lists; tags may be provided to group students immediately.

#### Parameters

`add n/NAME p/PHONE e/EMAIL a/ADDRESS [t/TAG]...`

- `n/` — required. Full student name; validated by `Name#isValidName`.
- `p/` — required. Contact number; must satisfy `Phone#isValidPhone`.
- `e/` — required. Email address; checked by `Email#isValidEmail`.
- `a/` — required. Free-form postal address.
- `t/` — optional, repeatable. Adds tags; each value must satisfy `Tag#isValidTagName`.

The command rejects missing mandatory prefixes, duplicate occurrences of the same mandatory prefix, and malformed values.

#### Overview

The `add` command follows the standard command pattern of *parse → construct command → execute on the model*.

#### High-level flow

![Add command activity](images/AddCommandActivityDiagram.png)

The activity diagram captures the user journey: the tutor submits the command, the system validates the input, and either reports a duplicate or persists the new student before confirming success.

#### Execution behaviour

![Add command execution sequence](images/AddCommandSequence.png)

The sequence diagram documents the runtime checks when `AddCommand#execute(Model)` is invoked. The command:

1. Calls `model.hasPerson(toAdd)` to guard against duplicates.
2. Throws `CommandException` with `MESSAGE_DUPLICATE_PERSON` if a match exists.
3. Otherwise adds the student via `model.addPerson(toAdd)` and formats the success message through `Messages.format`.

Key classes: `AddCommand`, `Model`, `Messages`.

#### Validation and error handling

- Missing or repeated mandatory prefixes trigger `ParseException` with usage guidance.
- Invalid value formats (e.g., phone, email) are rejected by the respective domain constructors inside `ParserUtil`.
- Duplicate students—based on the `Person#isSamePerson` identity definition—are blocked during execution.
- Warnings are given to warn users that phone number might be wrong when adding alphabets and special characters.

#### Design considerations

- **Input validation**: Happens during parsing so users see errors before the model is mutated.
- **Duplicate detection**: Ensures no two students with the same name can be added into the program.
- **Flexibility**: Phone field allows alphanumeric values and special characters to allow users to add multiple phone numbers and organise them like: (Home) 1234 (Office) 3456. 

### Edit Student Command

#### What it does

Updates selected fields of an existing student without replacing the whole entry. Fields omitted from the command remain unchanged.

#### Parameters

`edit INDEX [n/NAME] [p/PHONE] [e/EMAIL] [a/ADDRESS] [t/TAG]... [attr/KEY=VALUE[, VALUE2]]...`

- `INDEX` — required, 1-based. Identifies the student in the current filtered list.
- `n/`, `p/`, `e/`, `a/` — optional single-occurrence fields; if present, must pass the same validators as `add`.
- `t/` — optional, repeatable; replaces the tag set when provided. Supplying a lone `t/` clears all tags.
- `attr/` — optional, repeatable; replaces the attribute set. Supplying `attr/` with an empty value clears attributes.

At least one field beyond the index must be provided; otherwise the command rejects the input.

#### Overview

The edit command follows the same *parse → command → execute* pattern as other logic features.

#### High-level flow

![Edit command activity](images/EditCommandActivityDiagram.png)

The activity diagram illustrates the conditional checks: field presence, index validation, duplicate detection, and final update.

#### Execution behaviour

![Edit command execution sequence](images/EditCommandSequence.png)

The sequence diagram  captures the runtime flow:

1. Fetch the targeted student from the filtered list and guard against invalid indices.
2. Produce an edited `Person` by merging descriptor values with the original.
3. If the identity changes and clashes with another student, throw `MESSAGE_DUPLICATE_PERSON`.
4. Otherwise replace the entry, refresh the list, and return a success message.

Key classes: `EditCommand`, `Model`, `Messages`.

#### Validation and error handling

- Missing optional fields trigger `MESSAGE_NOT_EDITED`.
- Invalid indices reuse the shared index error message.
- Identity conflicts are blocked before the model is mutated.

#### Design considerations

- **Descriptor pattern**: The `EditPersonDescriptor` aggregates optional field updates, keeping parser and command logic cohesive while preventing partial mutations.
- **Immutability**: Rather than mutating the existing `Person`, the command constructs a new instance, ensuring defensive copies (e.g., tags, attributes) remain isolated.
- **Filter preservation**: After applying edits, the command refreshes the current predicate instead of forcing a full list reset, preserving the user's filtered context.

### Remark Command

#### What it does

Appends a new remark to a student's existing remarks. Remarks are short, free-text notes that allow tutors to record important details, such as a student's progress or learning style. This command can also be used to clear all remarks.

#### Parameters

`remark INDEX r/REMARK [r/REMARK2]...`

-   `INDEX` — required, 1-based. Identifies the student in the current filtered list.
-   `REMARK` — required. The text of the remark to add. Multiple `r/` prefixes can be used, and they will be joined together. If a single, empty `r/` is provided, all existing remarks for the student will be cleared.

#### Overview

The `remark` command follows the standard command pattern of *parse → construct command → execute on the model*. It provides a dedicated and straightforward way to manage textual notes for a student.

#### High-level flow

![Remark command activity](images/RemarkCommandActivityDiagram.png)

The activity diagram shows the user's journey: the tutor provides the student index and the remark text, the system validates the input, and then updates the student's remark in the model before confirming success.

#### Execution behaviour

![Remark command execution sequence](images/RemarkCommandSequence.png)

The sequence diagram documents the runtime checks when `RemarkCommand#execute(Model)` is invoked. The command:

1.  Retrieves the target `Person` from the filtered list using the person index.
2.  Guards against an invalid index by throwing a `CommandException`.
3.  Appends the new remark text to the existing remark, separated by a comma. If the existing remark is empty, it simply sets the new remark.
4.  Creates a new `Person` object with the combined `Remark`.
4.  Calls `model.setPerson(...)` to replace the old person object with the new one.
5.  Returns a `CommandResult` with a success message.

Key classes: `RemarkCommand`, `Model`, `Person`, `Remark`.

#### Validation and error handling

-   Invalid or missing student `INDEX` throws `MESSAGE_INVALID_PERSON_DISPLAYED_INDEX`.
-   Missing `r/` prefix throws a `ParseException` with usage guidance.

#### Design Considerations

-   **Immutability**: The command creates a new `Person` object with the updated `Remark` instead of mutating the existing one. This aligns with the functional programming paradigm and ensures predictable state management.
-   **Append-by-Default**: The command appends new remarks by default. This design choice makes it easy to add running notes over time without accidentally overwriting previous entries.
-   **Explicit Clear**: The ability to clear all remarks is handled by providing an empty `r/` prefix. This provides an intuitive and explicit way to remove notes, which is more user-friendly than requiring a separate `clear-remark` command.

### Delete Student Command

#### What it does

Removes a student from the roster using their displayed index.

#### Parameters

`delete INDEX`

- `INDEX` — required, 1-based. Must reference an entry in the current filtered list.

#### Overview

Deletion is the simplest command flow: parse the index, resolve it against the filtered list, and delete the matching person.

#### High-level flow

![Delete command activity](images/DeleteCommandActivityDiagram.png)

The activity diagram highlights the single decision point—whether the supplied index is valid.

#### Execution behaviour

![Delete command execution sequence](images/DeleteCommandSequence.png)

The sequence diagram  depicts the execution: retrieve the filtered list, guard against invalid indices, remove the student through `model.deletePerson`, and format a confirmation message with `Messages.format`. Key classes: `DeleteCommand`, `Model`, `Messages`.

#### Validation and error handling

- Invalid indices throw `MESSAGE_INVALID_PERSON_DISPLAYED_INDEX`.
- Because the command is irreversible, consider pairing it with undo when available.

#### Design considerations

- **Minimal surface area**: The command only operates on the index and does not expose extra options, reducing accidental deletions.
- **No-op avoidance**: Invalid indices abort before mutating state, ensuring that errors never partially modify the model.
- **Filter awareness**: Deletion relies on the currently filtered list; the command intentionally removes the entry without resetting filters so tutors can continue working within the same subset.

### Schedule Lesson Command

#### What it does

Adds a lesson block to a student, supporting both same-day and cross-day sessions. Optional grade/attendance data remain untouched.

#### Parameters

`schedule INDEX start/START_TIME end/END_TIME date/START_DATE [date2/END_DATE] sub/SUBJECT`

- `INDEX` — required, 1-based. Refers to the student in the current filtered list.
- `start/`, `end/` — required. 24-hour `HH:mm` start and end times.
- `date/` — required. ISO `YYYY-MM-DD` start date.
- `date2/` — optional. When supplied, indicates the lesson ends on a different date; otherwise the start date is reused.
- `sub/` — required. Free-form subject label.

#### Overview

The command mirrors the typical *parse → instantiate command → execute on model* pipeline, extending lesson creation to overnight slots.

#### High-level flow

![Schedule command activity](images/ScheduleCommandActivityDiagram.png)

The diagram shows the tutor supplying index/time/date information, with validation of both indices and temporal overlap before committing the change.

#### Execution behaviour

![Schedule command execution sequence](images/ScheduleCommandSequence.png)

Runtime steps:

1. Retrieve the filtered student list and validate the `INDEX`.
2. Reject duplicates via `LessonList#hasDuplicates`.
3. Reject overlapping intervals via `LessonList#hasOverlappingLesson` (across start/end dates).
4. Append the new `Lesson`, update the `Person`, and reset the filtered list to show all students.
5. Format a success message through `Messages.format`.

Key classes: `ScheduleCommand`, `Lesson`, `LessonList`, `Model`, `Messages`.

#### Validation and error handling

- Invalid student indices throw `MESSAGE_INVALID_PERSON_DISPLAYED_INDEX`.
- End date/time must be strictly after the start; violations throw `ScheduleCommandParser.MESSAGE_END_BEFORE_START`.
- Duplicate or overlapping lessons throw `MESSAGE_DUPLICATE_LESSON` / `MESSAGE_OVERLAPPING_LESSON` respectively.

#### Design considerations

- **Cross-day support**: The optional `date2/` parameter allows overnight lessons without complicating same-day usage.
- **Immutable updates**: A new `Person` with an updated `LessonList` is created, preserving snapshot-based reasoning.
- **Global visibility**: The command intentionally resets the listing to ensure the newly scheduled lesson is visible even if the tutor was previously filtering the roster.

### Unschedule Lesson Command

#### What it does

Removes a specific lesson from a student, identified via the current filtered list and lesson index.

#### Parameters

`unschedule INDEX lesson/LESSON_INDEX`

- `INDEX` — required, 1-based student index within the filtered view.
- `lesson/` — required, 1-based index into the student's lesson list.

#### Overview

The command follows the same three-phase architecture—parsing, command creation, model mutation—as other list-manipulation commands.

#### High-level flow

![Unschedule command activity](images/UnscheduleCommandActivityDiagram.png)

The activity diagram shows the tutor supplying the student and lesson indices, the system validating both, and the lesson removal that follows when the indices are valid.

#### Execution behaviour

1. Resolve the student by `INDEX`, throwing `MESSAGE_INVALID_PERSON_DISPLAYED_INDEX` on failure.
2. Resolve the lesson by `LESSON_INDEX`; invalid values trigger `MESSAGE_INVALID_LESSON_DISPLAYED_INDEX`.
3. Remove the lesson via `LessonList#remove(...)` and construct a replacement `Person`.
4. Call `model.setPerson(...)`, reset the filtered list, and emit a success message.

![Unschedule command execution sequence](images/UnscheduleCommandSequence.png)

Key classes: `UnscheduleCommand`, `LessonList`, `Model`, `Messages`.

#### Validation and error handling

- Out-of-range student or lesson indices produce the shared index error messages.
- Attempting to unschedule when the student has no lessons results in `MESSAGE_NO_LESSONS`.

#### Design considerations

- **Symmetry with scheduling**: Shares the same lesson resolution and immutability patterns, keeping the mental model consistent.
- **Safety checks**: Defensive guards ensure lessons cannot be removed from students lacking any scheduled sessions.
- **Global visibility**: Resets the predicate after removal so tutors can immediately see the updated roster without hidden state.

### Mark Student Attendance Command

#### What it does

Marks a student's attendance for a specific lesson as 'present'. This helps tutors keep an accurate record of class attendance, which is essential for tracking student records and for billing purposes.

#### Parameters

`mark INDEX lesson/LESSON_INDEX`

-   `INDEX` — required, 1-based. Identifies the student in the current filtered list.
-   `LESSON_INDEX` — required, 1-based. Identifies the lesson in the student's lesson list, which is visible when the student's card is expanded.

#### Overview

The `mark` command follows the standard command pattern of *parse → construct command → execute on the model*. It is designed to be a quick and straightforward way to update a single lesson's attendance.

#### High-level flow

![Mark command activity](images/MarkCommandActivityDiagram.png)

The activity diagram shows the user's journey: the tutor provides the student and lesson indices, the system validates them, checks if the lesson is already marked, and then updates the attendance status before confirming success.

#### Execution behaviour

![Mark command execution sequence](images/MarkCommandSequence.png)

The sequence diagram documents the runtime checks when `MarkCommand#execute(Model)` is invoked. The command:

1.  Retrieves the target `Person` from the filtered list using the person index.
2.  Retrieves the target `Lesson` from the person's `LessonList` using the lesson index.
3.  Checks if the lesson is already marked as present. If so, it throws a `CommandException` with `MESSAGE_LESSON_ALREADY_MARKED`.
4.  Creates a new `Lesson` object with the same details but with its attendance status set to `true`.
5.  Creates a new `Person` object with the updated `LessonList`.
6.  Calls `model.setPerson(...)` to replace the old person object with the new one, and returns a success message.

Key classes: `MarkCommand`, `Model`, `Person`, `Lesson`, `LessonList`.

#### Validation and error handling

-   Invalid or missing student `INDEX` throws `MESSAGE_INVALID_PERSON_DISPLAYED_INDEX`.
-   Invalid or missing `LESSON_INDEX` throws `MESSAGE_INVALID_LESSON_DISPLAYED_INDEX`.
-   Attempting to mark a lesson that is already marked as present throws `MESSAGE_LESSON_ALREADY_MARKED`.

#### Design Considerations

-   **Immutability**: The command creates new `Lesson` and `Person` objects instead of mutating existing ones. This aligns with the functional programming paradigm and makes state management more predictable.
-   **Idempotency Check**: The check for whether a lesson is already marked prevents redundant operations and provides clear feedback to the user, avoiding confusion.
-   **User Experience**: The command requires two separate indices, which is clear but requires the user to first identify the student and then the specific lesson. This is a trade-off for precision.
-   **Flexibility**: The command intentionally allows marking lessons in the past, present, or future. This gives tutors the flexibility to catch up on old attendance records or pre-mark attendance for a known upcoming attendance.

### Unmark Student Attendance Command

#### What it does

Reverts a student's attendance for a specific lesson to 'not present'. This is useful for correcting mistakes made during attendance-taking.

#### Parameters

`unmark INDEX lesson/LESSON_INDEX`

-   `INDEX` — required, 1-based. Identifies the student in the current filtered list.
-   `LESSON_INDEX` — required, 1-based. Identifies the lesson in the student's lesson list.

#### Overview

The `unmark` command is the direct counterpart to the `mark` command and follows the same *parse → command → execute* pattern. Its primary function is to reverse an attendance mark.

#### High-level flow

![Unmark command activity](images/UnmarkCommandActivityDiagram.png)

The activity diagram illustrates the flow for unmarking a lesson, which mirrors the `mark` command's logic: validate indices, check the current state, update the lesson, and confirm the change.

#### Execution behaviour

![Unmark command execution sequence](images/UnmarkCommandSequence.png)

The sequence diagram captures the runtime flow of `UnmarkCommand#execute(Model)`. The command:

1.  Retrieves the target `Person` and `Lesson` using the provided indices.
2.  Guards against invalid indices by throwing a `CommandException` if either is out of bounds.
3.  Checks if the lesson is already marked as 'not present'. If so, it throws a `CommandException` with `MESSAGE_LESSON_ALREADY_UNMARKED`.
4.  Creates a new `Lesson` with its attendance status set to `false`.
5.  Creates a new `Person` with the updated `LessonList`.
6.  Updates the model via `model.setPerson(...)` and returns a success message confirming the change.

Key classes: `UnmarkCommand`, `Model`, `Person`, `Lesson`, `LessonList`.

#### Validation and error handling

-   Invalid student `INDEX` throws `MESSAGE_INVALID_PERSON_DISPLAYED_INDEX`.
-   Invalid `LESSON_INDEX` throws `MESSAGE_INVALID_LESSON_DISPLAYED_INDEX`.
-   Attempting to unmark a lesson that is already 'not present' throws `MESSAGE_LESSON_ALREADY_UNMARKED`.

#### Design Considerations

-   **Symmetry with `mark`**: The command's structure, validation, and execution flow are intentionally symmetric with `MarkCommand` to ensure a consistent and predictable developer and user experience.
-   **Error-Correction Focus**: This command serves as a simple and direct way to correct errors, which is a critical usability feature for data entry applications.
-   **State-Awareness**: By checking if the lesson is already unmarked, the command avoids unnecessary model updates and provides precise feedback, preventing user confusion about the state of the data.
-   **Flexibility**: Mirroring the `mark` command, `unmark` also works on past, present, and future lessons. This is crucial for correcting historical attendance errors or adjusting plans for future lessons.

### Grade Command

#### What it does

Adds or updates grades for a student by subject and assessment. This enables tutors to maintain a detailed grade book per student, tracking performance across different subjects and assessment types over time.

#### Parameters

`grade INDEX sub/SUBJECT/ASSESSMENT/SCORE [sub/SUBJECT2/ASSESSMENT2/SCORE2]…`

- `INDEX` — required, 1-based. Identifies the student in the current filtered list.
- `sub/SUBJECT/ASSESSMENT/SCORE` — required, at least one. Each triplet specifies a grade entry:
  - `SUBJECT` — subject name (e.g., MATH, SCIENCE); validated for format.
  - `ASSESSMENT` — assessment type (e.g., WA1, Quiz1); validated for format.
  - `SCORE` — numeric score; validated to be a valid score value.

#### Overview

The `grade` command follows the standard command pattern of *parse → construct command → execute on the model*. It supports adding or updating multiple grades in a single command. If a subject-assessment combination already exists for the student, the new score overwrites the existing one.

#### High-level flow

![Grade command activity](images/GradeCommandActivityDiagram.png)

1. User provides student index and one or more grade triplets.
2. System validates index and parses each grade triplet.
3. System detects and prevents duplicate subject-assessment pairs within the same command.
4. System updates the student's grade list and confirms success.

#### Execution behaviour

![Grade command execution sequence](images/GradeCommandSequence.png)

When `GradeCommand#execute(Model)` is invoked, the command:

1. Retrieves the target `Person` from the filtered list using the index.
2. Guards against invalid indices by throwing `CommandException` if out of bounds.
3. Iterates through the grades to add, calling `GradeList#addGrade` for each.
4. Creates a new `Person` with the updated `GradeList`.
5. Updates the model via `model.setPerson(...)` and returns a success message with the updated grades.

Key classes: `GradeCommand`, `Model`, `Person`, `GradeList`, `Grade`.

#### Validation and error handling

- Invalid or missing student `INDEX` throws `MESSAGE_INVALID_PERSON_DISPLAYED_INDEX`.
- Missing or malformed grade triplets (missing parts, wrong format) throw `ParseException` with specific guidance.
- Duplicate subject-assessment pairs within the same command throw `ParseException` with a descriptive error message.
- Invalid subject, assessment, or score formats throw `ParseException` during parsing.

#### Design Considerations

- **Duplicate Prevention**: Duplicate detection at the parser level prevents ambiguous behavior and provides immediate feedback before model mutation.
- **Overwrite Behavior**: Existing grades with the same subject-assessment combination are overwritten, following the principle that the latest data is authoritative.
- **Immutability**: The command creates new `GradeList` and `Person` objects, maintaining immutability and predictable state management.
- **Batch Operations**: Supporting multiple grades per command improves efficiency when recording several assessments at once.

### Delete Grade Command

#### What it does

Removes a specific grade entry (identified by subject and assessment) from a student's grade list. This enables tutors to correct mistakes, such as accidentally recording a grade for a test a student did not take.

#### Parameters

`delgrade INDEX sub/SUBJECT/ASSESSMENT`

- `INDEX` — required, 1-based. Identifies the student in the current filtered list.
- `sub/SUBJECT/ASSESSMENT` — required. Specifies which grade to remove:
  - `SUBJECT` — subject name (must match exactly, including case).
  - `ASSESSMENT` — assessment type (must match exactly, including case).

#### Overview

The `delgrade` command follows the standard command pattern of *parse → construct command → execute on the model*. It is the inverse operation to the `grade` command, allowing tutors to maintain accurate grade records by removing erroneous entries.

#### High-level flow

![Delete Grade command activity](images/DeleteGradeCommandActivityDiagram.png)

1. User provides student index and subject-assessment identifier.
2. System validates index and parses the subject-assessment pair.
3. System checks if the grade exists for the student.
4. System removes the grade and confirms success.

#### Execution behaviour

![Delete Grade command execution sequence](images/DeleteGradeCommandSequence.png)

When `DeleteGradeCommand#execute(Model)` is invoked, the command:

1. Retrieves the target `Person` from the filtered list using the index.
2. Guards against invalid indices by throwing `CommandException` if out of bounds.
3. Checks if the grade exists using `GradeList#hasGrade(subject, assessment)`.
4. Throws `CommandException` with `MESSAGE_GRADE_NOT_FOUND` if the grade does not exist.
5. Creates a new `Person` with the grade removed via `GradeList#removeGrade(subject, assessment)`.
6. Updates the model via `model.setPerson(...)` and returns a success message.

Key classes: `DeleteGradeCommand`, `Model`, `Person`, `GradeList`.

#### Validation and error handling

- Invalid or missing student `INDEX` throws `MESSAGE_INVALID_PERSON_DISPLAYED_INDEX`.
- Missing or malformed `sub/` prefix throws `ParseException` with usage guidance.
- Empty subject or assessment throws `ParseException`.
- Non-existent grade throws `CommandException` with `MESSAGE_GRADE_NOT_FOUND`.

#### Design Considerations

- **Error Prevention**: Checking for grade existence before removal provides clear feedback and prevents silent failures.
- **Immutability**: The command creates new `GradeList` and `Person` objects, maintaining immutability consistent with other commands.
- **Precision**: Requiring exact subject-assessment match ensures tutors remove the intended grade without ambiguity.
- **Case Sensitivity**: Both subject and assessment must match exactly (including case) to the originally recorded grade. This ensures precise identification and prevents accidental deletions due to capitalization mismatches.
- **Symmetry**: The command complements the `grade` command, providing complete CRUD operations for grade management.


### Add Attribute Command

#### What it does

Adds or updates custom key–value attributes for a student so tutors can capture metadata such as subjects, school level, or age. Reusing a key replaces the previous values for that key.

#### Parameters

`addattr INDEX attr/KEY=VALUE[,VALUE2]... [attr/KEY2=VALUE2]...`

- `INDEX` — required, 1-based. Targets a student in the currently shown list.
- `attr/KEY=VALUE[,VALUE2]...` — required, at least one occurrence. Each prefix defines a key and one or more comma-separated values.
- Additional `attr/` prefixes apply AND semantics: every listed key/value set is attached to the student.

#### Overview

`addattr` follows the standard *parse → build command → execute on the model* pipeline. It enriches the `Person`'s attribute set without affecting other fields, replacing existing entries that share the same key.

#### High-level flow

![Add Attribute command activity](images/AddAttributeActivityDiagram.png)


#### Execution behaviour

![Add Attribute command execution sequence](images/AddAttributeSequence.png)

<div markdown="span" class="alert alert-info">:information_source: **Note:** The current implementation class is `TagCommand` along with `TagCommandParser`. We plan to rename them to `AddAttributeCommand` and `AddAttributeCommandParser` to avoid confusion with person _tags_.  
</div>

#### Validation and error handling

- Missing `attr/` prefixes or index produces a `ParseException` with `TagCommand.MESSAGE_USAGE`.
- Malformed segments without `=` trigger `ParseException` ("Incorrect format. Use attr/key=value[,value2]...").
- Empty keys or values throw dedicated `ParseException`s ("Attribute key cannot be empty.", "Attribute must have at least one value.").
- An index outside the displayed range throws `CommandException` with `MESSAGE_INVALID_PERSON_DISPLAYED_INDEX`.

#### Design Considerations

- **Override semantics**: Removing existing attributes with the same key before adding new ones keeps the latest tutor input authoritative.
- **Immutability**: The command constructs a new `Person` instance, aligning with the project's immutable model strategy and simplifying undo/redo in the future.
- **Case insensitivity**: Lowercasing keys and values ensures consistent matching across commands such as `filter`, preventing duplicate attributes that differ only in casing.
- **Deterministic ordering**: Using an insertion-ordered map during parsing keeps attribute application predictable and stable for testing.


### Delete Attribute Command

#### What it does

Removes attribute keys (and their values) from a student so tutors can retire outdated metadata such as old subjects, levels, or preferences. If none of the requested keys exist, the command stops and informs the tutor.

#### Parameters

`delattr INDEX attr/KEY [attr/KEY2]...`

- `INDEX` — required, 1-based. Targets a student in the current filtered list.
- `attr/KEY` — required, at least one occurrence. Each prefix designates an attribute key to remove; values are omitted.

#### Overview

`delattr` follows the usual *parse → construct command → execute on the model* pattern. It locates the target student, filters their attribute set, and persists the updated `Person`.

#### High-level flow

![Delete Attribute command activity](images/DeleteAttributeActivityDiagram.png)


#### Execution behaviour

![Delete Attribute command execution sequence](images/DeleteAttributeSequence.png)


#### Validation and error handling

- Missing index or `attr/` prefixes triggers a `ParseException` with `DeleteAttributeCommand.MESSAGE_USAGE`.
- Blank keys (after trimming) cause the parser to reject the command.
- Out-of-bounds indices throw `CommandException` with `MESSAGE_INVALID_PERSON_DISPLAYED_INDEX`.
- Attempting to delete non-existent keys throws `CommandException` with `MESSAGE_NO_ATTRIBUTES_REMOVED`.

#### Design Considerations

- **Case insensitivity**: Normalising keys during parsing keeps behaviour aligned with how attributes are stored and filtered.
- **Immutable updates**: Creating a new `Person` maintains the project's immutable model discipline and eases future undo/redo support.



### Filter Command

#### What it does

Filters and displays students whose attributes match specified criteria. This enables tutors to quickly find students matching specific attributes, such as all students in a particular subject or age group, using AND logic between different attributes and OR logic within the same attribute.

#### Parameters

`filter attr/KEY=VALUE[,VALUE2]… [attr/KEY2=VALUE2]…`

- `attr/KEY=VALUE` — required, at least one. Specifies an attribute filter:
  - `KEY` — attribute key (e.g., subject, age); case-insensitive.
  - `VALUE` — attribute value(s); multiple values separated by commas; case-insensitive.
- Multiple `attr/` prefixes can be provided for AND logic between different keys.

#### Overview

The `filter` command follows the standard command pattern of *parse → construct command → execute on the model*. It updates the filtered list predicate without mutating the underlying data, allowing the UI to display only matching students.

#### High-level flow

![Filter command activity](images/FilterCommandActivityDiagram.png)

1. User provides one or more attribute filters.
2. System parses each filter, extracting keys and values.
3. System constructs an `AttributeContainsPredicate` with the filter criteria.
4. System applies the predicate to the filtered list and displays matching students with a count.

#### Execution behaviour

![Filter command execution sequence](images/FilterCommandSequence.png)

When `FilterCommand#execute(Model)` is invoked, the command:

1. Updates the filtered list predicate via `model.updateFilteredPersonList(predicate)`.
2. Retrieves the filtered list size.
3. Returns a success message with the count of matching students.

Key classes: `FilterCommand`, `Model`, `AttributeContainsPredicate`.

#### Validation and error handling

- Missing `attr/` prefix throws `ParseException` with usage guidance.
- Malformed attribute format (missing `=`) throws `ParseException`.
- Empty attribute key throws `ParseException`.
- Empty attribute values (after trimming and filtering) throws `ParseException`.
- At least one `attr/` prefix must be provided.

#### Design Considerations

- **Predicate-Based Filtering**: Using a predicate allows the filtered list to automatically update when underlying data changes, maintaining consistency.
- **AND/OR Logic**: AND logic between different attribute keys and OR logic within the same key provides intuitive filtering behavior for tutors.
- **Case Insensitivity**: Both keys and values are matched case-insensitively, reducing user frustration from capitalization mismatches.
- **No Data Mutation**: The command only updates the view predicate; no student data is modified, making it safe and reversible via the `list` command.
- **Performance**: Filtering is efficient as it leverages JavaFX `FilteredList`, which wraps the master list without creating deep copies.

### Search Command

#### What it does

Finds and lists all students whose name, email or phone number contains any of the specified keywords.  
The search is **case-insensitive** and supports multiple keywords, returning all matches where any field partially matches at least one keyword.

#### Parameters

`search KEYWORD [MORE_KEYWORDS]...`

- `KEYWORD` — required, at least one. Case-insensitive substring matched against the student's name, email, or phone number fields.

#### Overview

The `search` command follows the standard command pattern of *parse → construct command → execute on the model*.  
It uses a dedicated predicate, `PersonContainsKeywordPredicate`, which encapsulates the matching logic across all searchable fields.

#### High-level flow

![Search command activity](images/SearchCommmandActivityDiagram.png)

1. The tutor enters a `search` command with one or more keywords in the Command Box.
2. `LogicManager` forwards the input string to `AddressBookParser` for processing.
3. `AddressBookParser` identifies the command word `search` and delegates parsing to `SearchCommandParser`.
4. `SearchCommandParser` tokenises the input string into individual keywords, validates that at least one keyword exists, and normalises them to lowercase.
5. A `PersonContainsKeywordPredicate` is created using the list of cleaned keywords.
6. `LogicManager` constructs and executes a `SearchCommand` object containing the predicate.
7. `SearchCommand#execute(Model)` invokes `model.updateFilteredPersonList(predicate)` to update the filtered list of persons.
8. The JavaFX `FilteredList` observes this change and automatically refreshes the UI to display only the matching students.
9. The command returns a `CommandResult` summarising how many students were found.

#### Execution behaviour

![Search command execution sequence](images/SearchCommandSequenceDiagram.png)

The sequence diagram captures the flow of `SearchCommand#execute(Model)`:

1. Calls `model.updateFilteredPersonList(predicate)`, passing in the predicate constructed from the keywords.
2. The model updates the observable list used by the UI.
3. The UI automatically refreshes to display only the matching students.
4. Returns a `CommandResult` summarising the number of students found.

**Key classes:**  
`SearchCommand`, `Model`, `PersonContainsKeywordPredicate`, `Messages`

#### Validation and error handling

- Missing keywords (empty input) trigger `ParseException` with usage guidance.
- Excessive whitespace is ignored during tokenisation.
- Search is non-destructive—no model mutations occur.
- The command is resilient to malformed input such as mixed spacing or special characters in keywords.

#### Design Considerations
- **Search Scope**: Includes name, email, and phone number. Provides flexibility and matches user expectations of a global search
- **Case Sensitivity**:  Case-insensitive.  Reduces user friction; tutors do not need to match exact capitalization
- **Partial Match**: Substring-based. Enables fast, natural filtering without requiring exact terms
- **Predicate Reuse**: Uses `PersonContainsKeywordPredicate`. Keeps filtering logic encapsulated and testable 
- **Performance**: Uses JavaFX `FilteredList`. Efficient, as no deep copies or re-parsing occur
- **User Experience**: Provides both CLI pathways for filtering.

--------------------------------------------------------------------------------------------------------------------

## **Documentation, logging, testing, configuration, dev-ops**

* [Documentation guide](Documentation.md)
* [Testing guide](Testing.md)
* [Logging guide](Logging.md)
* [Configuration guide](Configuration.md)
* [DevOps guide](DevOps.md)

--------------------------------------------------------------------------------------------------------------------

## **Appendix: Requirements**

## **1. Product Scope (Target User & Value Proposition)**

### **Target User Profile**

Private tutors who manage many students without the infrastructure of a tuition agency and need a fast, keyboard-driven desktop tool to keep contacts, lessons, attendance, and grades record organized.

### **Value Proposition**

ClassRosterPro reduces tutors' admin load by consolidating contacts, tagging/filtering, lesson scheduling, attendance tracking, grade recording, and quick search into one command-first desktop app—so time goes to teaching, not record-keeping.

---

## **2. User Stories**

| Priority | As a …   | I want to …                                                                  | So that I can …                                                                                 |
| -------- | -------- |------------------------------------------------------------------------------| ----------------------------------------------------------------------------------------------- |
| ***      | New user | Access the help page                                                         | Familiarise myself with the commands available in the program                                   |
| ***      | Tutor    | Add a new student contact                                                    | Easily contact them for class updates                                                           |
| ***      | Tutor    | Delete a student contact                                                     | Keep my contacts updated                                                                        |
| ***      | Tutor    | Edit a student's contact details                                             | Update their information when it changes                                                        |
| ***      | Tutor    | Search for a student contact by name                                         | Quickly find their contact                                                                      |
| ***      | Tutor    | Tag and filter students by their attributes (e.g. class, subject, age, etc.) | Categorise students with similar attributes and look them up more easily                        |
| ***      | Tutor    | Delete specific attributes from students                                     | Remove outdated or incorrect information                                                        |
| ***      | Tutor    | Schedule lessons for students                                                | Keep track of upcoming classes and avoid scheduling conflicts                                   |
| ***      | Tutor    | Unschedule lessons                                                           | Remove cancelled or rescheduled lessons from my roster                                          |
| ***      | Tutor    | Record student attendance for specific lessons                               | Track which students attended which classes                                                     |
| ***      | Tutor    | Unmark attendance                                                            | Correct attendance records if marked incorrectly                                                |
| ***      | Tutor    | Record grades for students by subject and assessment                         | Maintain a detailed grade book for each student                                                 |
| ***      | Tutor    | Open/close student contact cards                                             | View detailed or summary information as needed                                                  |
| **       | Tutor    | List all students (reset filters)                                            | Always return to the full view                                                                  |
| **       | Tutor    | Tag with multi-values (e.g., subject=math,science)                           | Group students flexibly by multiple criteria                                                    |
| **       | Tutor    | Use quick search by name, email, or phone                                    | Instantly find students during lessons                                                          |
| **       | Tutor    | See search counts (e.g., "2 students found")                                 | Get quick feedback on filter results                                                            |

### **Could-Have**

* Undo/redo last action to recover from mistakes (in backlog; aligns with productivity)
* Export/import roster to a file for backup
* Bulk updates for multiple students

### **Considered (Not for current release)**

* Calendar sync with external providers
* Dashboard analytics (future epics)
* Automated reminders for upcoming lessons

---

## **3. Use Cases**

### **UC01: Add a student**

**System:** ClassRosterPro\
**Use Case:** UC01 - Add student\
**Actor:** Tutor\
**Preconditions:** None\
**Guarantees:**
  - Student is added to roster if all fields are valid and no duplicate exists

**MSS:**

1. Tutor enters command to add student with corresponding student details.
2. ClassRosterPro adds the student successfully.\
   Use case ends.

**Extensions:**

* 1a. ClassRosterPro detects invalid index.
    * 1a1. ClassRosterPro shows error message for invalid index\
      Use case ends.
* 1b. ClassRosterPro detects invalid student details.
  * 1b1. ClassRosterPro shows error message.\
    Use case ends.
* 1c. ClassRosterPro detects duplicate contact.
  * 1c1. ClassRosterPro shows error message.\
    Use case ends.

---

### **UC02 - Schedule Lesson for Student**

**System:** ClassRosterPro\
**Use Case:** UC02 - Schedule Lesson for Student\
**Actor:** Tutor\
**Preconditions:** Student exists in the roster\
**Guarantees:** Lesson is scheduled if no overlaps/duplicates and all validations pass

**MSS:**

1. Tutor enters command to schedule lesson with corresponding lesson details.
2. ClassRosterPro schedules the lesson successfully.\
   Use case ends.

**Extensions:**

* 1a. ClassRosterPro detects invalid time format.
  * 1a1. ClassRosterPro shows error message for incorrect format.\
    Use case ends.
* 1b. ClassRosterPro detects invalid time values.
  * 1b1. ClassRosterPro shows error message for incorrect format.\
    Use case ends.
* 1c. ClassRosterPro detects invalid date format.
   * 1c1. ClassRosterPro shows error message for incorrect format.\
     Use case ends.
* 1d. ClassRosterPro detects invalid date values (e.g., 2025-11-31).
   * 1d1. ClassRosterPro shows error message for invalid date.\
     Use case ends.
* 1e. ClassRosterPro detects end time ≤ start time.
   * 1e1. ClassRosterPro shows error message for invalid end time.\
     Use case ends.
* 1f. ClassRosterPro detects missing required fields.
   * 1f1. ClassRosterPro shows error message indicating which fields are missing.\
     Use case ends.
* 1g. ClassRosterPro detects lesson overlaps with existing lesson.
   * 1g1. ClassRosterPro shows error message for overlapping lesson.\
     Use case ends.
* 1h. ClassRosterPro detects duplicate lesson.
   * 1h1. ClassRosterPro shows error message for lesson duplication.\
     Use case ends.

---

### **UC03 - Record Attendance for Lesson**

**System:** ClassRosterPro\
**Use Case:** UC03 - Record Attendance for Lesson\
**Actor:** Tutor\
**Preconditions:** Student exists with at least one scheduled lesson\
**Guarantees:** Attendance is recorded if both indices are valid and lesson isn't already marked

**MSS:**

1. Tutor enters command to mark a lesson with the corresponding student index and lesson index.
2. ClassRosterPro updates attendance count successfully.\
   Use case ends.

**Extensions:**
* 1a. ClassRosterPro detects invalid student index.
  * 1a1. ClassRosterPro shows error message for invalid index.\
    Use case ends.
* 1b. ClassRosterPro detects invalid lesson index.
  * 1b1. ClassRosterPro shows error message for invalid index.\
    Use case ends.
* 1c. ClassRosterPro detects lesson already marked.
  * 1c1. ClassRosterPro shows error message for marked lesson.\
    Use case ends.

---

### **UC04 - Record Grades for Student**

**System:** ClassRosterPro\
**Use Case:** UC04 - Record Grades for Student\
**Actor:** Tutor\
**Preconditions:** Student exists in the roster\
**Guarantees:**
- Grades are saved with proper validation
- Existing subject-assessment records are overwritten with new scores
- Data integrity is maintained

**MSS:**

1. Tutor enters command to record grade with corresponding student index and grade details.
2. ClassRosterPro records grades successfully.\
  Use case ends.

**Extensions:**

* 1a. Tutor enters invalid command format.
   * 1a1. ClassRosterPro shows correct usage format.\
     Use case ends.
* 1b. ClassRosterPro detects invalid index.
   * 1b1. ClassRosterPro shows error message for invalid index.\
     Use case ends.
* 1c. ClassRosterPro detects invalid grade format.
   * 1c1. ClassRosterPro shows error message for invalid format.\
     Use case ends.
* 1d. ClassRosterPro detects invalid subject-assessment format.
   * 1d1. ClassRosterPro shows error message indicating missing components.\
     Use case ends.
* 1e. ClassRosterPro detects duplicate subject-assessment in one command.
  * 1e1. ClassRosterPro shows error message indicating duplicate grade detected.\
    Use case ends.
* 1f. ClassRosterPro detects invalid score value.
  * 1f1. ClassRosterPro shows error message for invalid score value.\
    Use case ends. 

---

### **UC04a - Delete Grade from Student**

**System:** ClassRosterPro\
**Use Case:** UC04a - Delete Grade from Student\
**Actor:** Tutor\
**Preconditions:** Student exists in the roster with at least one grade\
**Guarantees:**
- Grade is removed if index and subject-assessment are valid
- Data integrity is maintained
- Error message displayed if grade does not exist

**MSS:**

1. Tutor enters command to delete grade with corresponding student index and subject-assessment details.
2. ClassRosterPro removes the grade successfully.\
  Use case ends.

**Extensions:**

* 1a. Tutor enters invalid command format.
   * 1a1. ClassRosterPro shows correct usage format.\
     Use case ends.
* 1b. ClassRosterPro detects invalid index.
   * 1b1. ClassRosterPro shows error message for invalid index.\
     Use case ends.
* 1c. ClassRosterPro detects invalid subject-assessment format.
   * 1c1. ClassRosterPro shows error message for invalid format.\
     Use case ends.
* 1d. ClassRosterPro detects grade does not exist.
   * 1d1. ClassRosterPro shows error message for grade not found.\
     Use case ends.

---

### **UC05 - Unschedule Lesson**

**System**: ClassRosterPro\
**Use Case**: UC05 - Unschedule Lesson\
**Actor**: Tutor\
**Preconditions**: Student exists with at least one scheduled lesson\
**Guarantees**:
- Lesson is removed if both indices are valid
- Attendance records for the lesson are also removed
- Data consistency is maintained

**MSS:**

1. Tutor enters command to view list of students.
2. Tutor enters command to unschedule lesson with corresponding student index and lesson index.
3. ClassRosterPro removes the lesson successfully.\
  Use case ends.

**Extensions:**

* 1a. Tutor cannot find desired student in current view.
   * 1a1. Tutor uses find or filter commands to locate student.\
     Use case resumes from step 1.
* 2a. ClassRosterPro detects invalid command format.
   * 2a1. ClassRosterPro shows correct usage format.\
     Use case ends.
* 2b. ClassRosterPro detects invalid student index.
   * 2b1. ClassRosterPro shows error message for invalid student index.\
     Use case ends.
* 2c. ClassRosterPro detects invalid lesson index.
   * 2c1. ClassRosterPro shows error message for invalid lesson index.\
     Use case ends.
* 2d. ClassRosterPro detects student has no lessons.
   * 2d1. ClassRosterPro shows error message for no lessons scheduled.\
     Use case ends.



---

### **UC06 - Add Attributes to Student**

**System**: ClassRosterPro\
**Use Case**: UC06 - Add Attributes to Student\
**Actor**: Tutor\
**Preconditions**: Student exists in the roster\
**Guarantees**:
- Specified attributes are added or updated for the selected student.
- If the same key already exists, its values are replaced with the new ones.
- Data integrity is maintained

**MSS:**

1. Tutor enters the command to add attributes to a student with the corresponding index and attribute details.
2. ClassRosterPro adds or updates the attributes for the student successfully.\
   Use case ends.

**Extensions:**

* 1a. Tutor enters invalid command format.
    * 1a1. ClassRosterPro shows correct usage format.\
      Use case ends.
* 1b. ClassRosterPro detects invalid index.
    * 1b1. ClassRosterPro shows error message for invalid index.\
      Use case ends.
* 1c. ClassRosterPro detects no attributes specified.
    * 1c1. ClassRosterPro shows error message for no attributes specified.\
      Use case ends.


---

### **UC07 - Delete Attributes from Student**

**System**: ClassRosterPro\
**Use Case**: UC07 - Delete Attributes from Student\
**Actor**: Tutor\
**Preconditions**: Student exists with at least one attribute\
**Guarantees**:
- Specified attributes are removed if they exist
- Non-existent attributes are ignored
- Data integrity is maintained

**MSS:**

1. Tutor enters command to delete student's attribute with corresponding student index and attribute keys.
2. ClassRosterPro removes the specified attributes successfully.\
   Use case ends.

**Extensions:**

* 1a. Tutor enters invalid command format.
   * 1a1. ClassRosterPro shows correct usage format.\
     Use case ends.
* 1b. ClassRosterPro detects invalid index.
   * 1b1. ClassRosterPro shows error message for invalid index.\
     Use case ends.
* 1c. ClassRosterPro detects attribute doesn't exist.
   * 1c1. ClassRosterPro shows error message for no matching attributes found.\
     Use case ends.
* 1d. ClassRosterPro detects multiple attributes specified.
   * 1d1. ClassRosterPro removes all valid attributes and proceeds.\
     Use case ends.
* 1e. ClassRosterPro detects no attributes specified.
   * 1e1. ClassRosterPro shows error message for no attributes specified.\
     Use case ends.
  
---

### **UC08 - Filter Students by Attributes**

**System**: ClassRosterPro\
**Use Case**: UC08 - Filter Students by Attributes\
**Actor**: Tutor\
**Preconditions**: None\
**Guarantees**: Students matching filter criteria are displayed with count

**MSS:**

1. Tutor enters command to filter with corresponding attribute criteria.
2. ClassRosterPro applies filter and displays filtered list successfully.\
   Use case ends.

**Extensions:**
* 1a. ClassRosterPro detects no attribute provided.
    * 1a1. ClassRosterPro shows error message for invalid format.\
      Use case ends.

* 2a. No students match filter criteria.
   * 2a1. ClassRosterPro displays empty list.\
     Use case ends.

---

### **UC09 - Search Student**

**System**: ClassRosterPro\
**Use Case**: UC09 - Search Student\
**Actor**: Tutor\
**Preconditions**: None\
**Guarantees**: Students matching search criteria in name, phone, or email are displayed

**MSS:**

1. Tutor types command to search with corresponding matching details.
2. ClassRosterPro displays matching results successfully.\
   Use case ends.

**Extensions:**

* 1a. No matches found.
   * 1a1. ClassRosterPro displays empty list.\
     Use case ends.

---

### **UC10 - Delete Student**

**System**: ClassRosterPro\
**Use Case**: UC10 - Delete Student\
**Actor**: Tutor\
**Preconditions**: Student exists in roster\
**Guarantees**: Student is permanently removed from roster

**MSS:**

1. Tutor enters command to delete student with corresponding student index.
2. ClassRosterPro deletes student successfully.\
   Use case ends.

**Extensions:**

* 1a. ClassRosterPro detects invalid index.
   * 1a1. ClassRosterPro shows error message for invalid index.\
     Use case ends.


---
### **UC11 - Unmark Attendance for Lesson**

**System**: ClassRosterPro\
**Use Case**: UC11 - Unmark Attendance for Lesson\
**Actor**: Tutor\
**Preconditions**: Student exists with at least one scheduled lesson\
**Guarantees**: Attendance record is updated to not attended

**MSS:**

1. Tutor enters command to unmark with corresponding student index and lesson index.
2. ClassRosterPro marks the lesson as not attended successfully.
   Use case ends.

**Extensions:**

* 1a. ClassRosterPro detects invalid student index.
   * 1a1. ClassRosterPro shows error message for invalid student index.\
     Use case ends.
* 1b. ClassRosterPro detects invalid lesson index.
   * 1b1. ClassRosterPro shows error message for invalid lesson index.\
     Use case ends.
* 1c. ClassRosterPro detects lesson already unmarked.
   * 1c1. ClassRosterPro shows error message.\
     Use case ends.

---

### **UC12 - Open Student Contact Card**

**System**: ClassRosterPro\
**Use Case**: UC12 - Open Student Contact Card\
**Actor**: Tutor\
**Preconditions**: Student exists in roster with student card closed\
**Guarantees**: Student card expands to show all details

**MSS:**

1. Tutor enters command to view list of students.
2. Tutor enters open command with corresponding student index.
3. ClassRosterPro expands student card successfully.\
   Use case ends.

**Extensions:**

* 2a. ClassRosterPro detects invalid index.
   * 2a1. ClassRosterPro shows error message for invalid index.\
     Use case ends.
* 3a. ClassRosterPro detects student card is already open.
   * 3a1. ClassRosterPro shows error message.\
     Use case ends.

---

### **UC-13: Close a student's contact card**

**System**: ClassRosterPro\
**Use Case**: UC13 - Open Student Contact Card\
**Actor**: Tutor\
**Preconditions**: Student exists in roster with student card open\
**Guarantees**:  Student card collapses to summary view

**MSS:**

1. Tutor enters command to view list of students.
2. Tutor enters close command with corresponding student index.
3. ClassRosterPro collapses the student card successfully.\
   Use case ends.

**Extensions:**

* 2a. ClassRosterPro detects invalid index.
   * 2a1. ClassRosterPro shows error message for invalid student index.\
     Use case ends.
* 2b. ClassRosterPro detects student card is already closed.
   * 2b1. ClassRosterPro shows error message.\
     Use case ends.

---

### **UC14 - Edit Student**

**System**: ClassRosterPro\
**Use Case**: UC14 - Edit Student\
**Actor**: Tutor\
**Preconditions**: Student exists in roster\
**Guarantees**:
- Specified fields are updated when valid
- Unspecified fields remain unchanged
- Data integrity and uniqueness constraints are preserved

**MSS:**

1. Tutor enters command to edit the student with the corresponding student index and updated details.
2. ClassRosterPro updates the student record successfully.\
   Use case ends.

**Extensions:**

* 1a. ClassRosterPro detects invalid index.
   * 1a1. ClassRosterPro shows error message for invalid index.\
     Use case ends.
* 1b. ClassRosterPro detects no fields provided.
   * 1b1. ClassRosterPro shows error message indicating required fields.\
     Use case ends.
* 1c. ClassRosterPro detects invalid detail value (e.g., malformed email or phone).
   * 1c1. ClassRosterPro shows error message for invalid value.\
     Use case ends.
* 1d. ClassRosterPro detects the updated details would duplicate another student.
   * 1d1. ClassRosterPro shows error message for duplicate student.\
     Use case ends.

---

## **4. Non-Functional Requirements (NFRs)**

| Category               | Requirement                                                                                                                       |
| ---------------------- |-----------------------------------------------------------------------------------------------------------------------------------|
| **Portability**        | Runs on mainstream OS (Windows/macOS/Linux) with Java 17+.                                                                        |
| **Performance**        | Responsive command execution (≤150ms parse/dispatch). Handles ~1,000 students smoothly.                                           |
| **Usability**          | Keyboard-first with GUI support; all actions via commands with clear error messages. Help window lists all commands and examples. |
| **Reliability**        | Atomic saves with JSON backup. Detects corrupted storage and continues running with empty data file.                              |
| **Data Validation**    | Strict validation for time (00:00-23:59), date (valid calendar dates), and email format.                                          |
| **Maintainability**    | Command pattern structure. Each new feature = new Command + parser + model update. Unit tests mandatory.                          |
| **Security & Privacy** | Data stored locally in JSON format. No auto-sync. Contact details remain private.                                                 |
| **Scalability**        | Support for multiple lessons per student, multiple attributes, multiple grades per subject.                                       |

---

## **5. Glossary**

| Term          | Definition                                                                                                           |
| ------------- |----------------------------------------------------------------------------------------------------------------------|
| **Command**   | A typed instruction (e.g., `add`, `delete`, `find`, `schedule`) processed by the logic layer.                        |
| **Index**     | 1-based position of a student in the current displayed list (after `list`/`find`/`filter`).                          |
| **Lesson Index** | 1-based position of a lesson in a student's lesson list.                                                             |
| **Attribute** | Key–value metadata attached to a student (e.g., `subject=math`, `age=16`). Can have multiple comma-separated values. |
| **Remark**    | Short free-text note attached to a student for remembering important details.                                        |
| **Attendance** | Mark/unmark record indicating if a student attended a specific lesson.                                               |
| **Lesson**    | A scheduled time block (date, start time, end time, subject) tied to a student.                                      |
| **Grade**     | A subject-assessment-score triplet recorded for a student (e.g., MATH/WA1/89).                                       |
| **List**      | Default roster view showing all students; also the command that resets filters.                                      |
| **Filter**    | Command to display only students matching specified attribute criteria.                                              |
| **Quick Search** | Search feature that finds students by name, email, or phone number.                                                  |
| **Help**      | Window listing all supported commands with examples and grouped by category.                                         |
| **Overlap**   | When two lessons for the same student share any time period on the same date.                                        |
| **Duplicate Lesson**| An exact match of student, date, start time, end time, and subject with an existing lesson.                          |

## **6. Acknowledgements** 
During the development of ClassRosterPro, the team made limited use of AI-assisted tools to support coding, debugging, documentation refinement, and explanation of technical concepts. In particular:

ChatGPT (OpenAI) was used by Michelle for:
* Generating initial drafts of small code snippets when exploring design alternatives (e.g., prototypes for the Search feature logic, predicates, UI wiring).
* Assisting in debugging issues such as Checkstyle, Gradle test failures, and class/method signature mismatches.
* Clarifying JavaFX behaviour, Java collections usage, and AB3 architectural patterns.
* Providing guidance when writing and refining parts of the User Guide (UG) and Developer Guide (DG), including phrasing, formatting, and consistency checks.
* Helping review and rephrase error messages, exception handling, and command behaviour descriptions.
* Explaining test failures and suggesting possible fixes (e.g., LogicManager test issues, Search tests, filter behaviour).
* Extent of use:
AI assistance was advisory and supplementary. All final code, design decisions, test updates, and documentation were reviewed, verified, edited, and integrated manually by the team.
* No AI-generated content was added without manual review. All logic, models, UI components, command implementations, and design decisions were ultimately crafted, validated, and maintained by the team.
---

## **Appendix: Instructions for Manual Testing**

This section provides guidance to manually verify the new or modified features of ClassRosterPro. Each subsection gives a test path and copy‑pasteable inputs. Refer to the User Guide for full syntax. Ensure sample data in `data/addressbook.json` is loaded before testing.

### 1. Search

**Command name**: `search`

**Purpose**: Instantly locate students by typing into the search box.

**Steps to test**:
1. Launch the app and ensure several students are listed.
2. Enter `search alex` in the command box.

**Expected**: The list updates instantly to show only contacts whose name, phone, or email contains "alex".

### 2. Add Attribute and Filter

**Command name**: `addattr` and `filter`

**Purpose**: Add attribute to students and filter the student list based on them.

**Steps to test**:
1. Tag a student with attributes:
   - `addattr 1 attr/subject=Math attr/age=16`
2. Verify attributes appear under the student's details.
3. Filter students by multiple attributes:
   - `filter attr/subject=Math attr/age=16`

**Expected**: Only students with both `subject=Math` and `age=16` are displayed.

### 3. Schedule and Unschedule Lessons

**Command name**: `schedule` and `unschedule`

**Purpose**: Create and remove scheduled lessons for a student.

**Steps to test**:
1. Schedule a lesson for the first student:
   - `schedule 1 start/14:00 end/15:00 date/2025-11-01 sub/Science`
2. Remove the lesson:
   -`unschedule 1 lesson/1`.

**Expected**: A lesson entry appears after scheduling command in the expanded student card and is removed after unschedule command.

### 4. Attendance Tracking

**Command name**: `mark` and `unmark`

**Purpose**: Mark and unmark lesson attendance.

**Steps to test**:
1. Ensure a student has a scheduled lesson (see previous test).
2. Mark attendance:
   - `mark 1 lesson/1`
3. Unmark attendance:
- `unmark 1 lesson/1`

**Expected**: The lesson is marked as "Present" after mark command and marked as "Not Present" after unmark command.

### 5. Grade Recording and Deletion

**Command name**: `grade` and `delgrade`

**Purpose**: Record, update, and delete grades for various subjects and assessments.

**Steps to test**:
1. Add grades to a student:
   - `grade 1 sub/MATH/WA1/85 sub/SCIENCE/Quiz1/92`

**Expected**: The grades appear under the student's card.

2. Add another grade for the same subject/assessment to test overwrite behavior:
   - `grade 1 sub/MATH/WA1/90`

**Expected**: The score for MATH/WA1 updates to 90.

3. Delete a grade:
   - `delgrade 1 sub/SCIENCE/Quiz1`

**Expected**: The SCIENCE/Quiz1 grade is removed from the student's grade list, while MATH/WA1 remains.

### 6. Add/Clear Remarks

**Command name**: `remark`

**Purpose**: Add, append, or clear remarks for a student.

**Steps to test**:
1. Add an initial remark to the first student:
   - `remark 1 r/Needs extra help with algebra`
2. Clear the remark for the same student:
    - `remark 1 r/`

**Expected**: The remark "Needs extra help with algebra" appears under the student's card. After clear remark, the remark for the student is cleared and no longer visible.


### 7. Open and Close Student Cards

**Command name**: `open` and `close`

**Purpose**: Expand or collapse individual student cards to view or hide details.

**Steps to test**:
1. Enter `open 1`
2. Enter `close 1`.

**Expected**: The first student's card expands to show all details (lessons, grades, tags) after open command and collapses back to its summary view after close command.

### 8. Add Attribute and Delete Attribute

**Command name**: `delattr`

**Purpose**: Remove specific attribute from a student.

**Steps to test**:
1. Add attributes first (if not present):
   - `addattr 1 attr/subject=Math attr/level=Sec3`
2. Delete an attribute:
   - `delattr 1 attr/level`

**Expected**: Only the `level` attribute is removed; `subject=Math` remains.

### 9. Data Persistence Verification

**Purpose**: Confirm that data modifications are saved correctly after closing and reopening the application.

**Steps to test**:
1. Modify data using any commands above (e.g., add attributes, schedule a lesson, add grades).
2. Exit the app.
3. Reopen the app.

**Expected**: All changes persist (e.g., added attributes, scheduled lessons, grades).

### 10. Schedule Cross-Day Lesson

**Command name**: `schedule`

**Purpose**: Create a cross-day lesson for a student.

**Steps to test**:
1. Schedule a cross-day lesson for the first student:
  - `schedule 1 start/23:00 end/01:00 date/2025-11-02 date2/2025-11-03 sub/Science`

**Expected**: A cross-day lesson entry appears after scheduling command in the expanded student card.

---

## **Appendix: Planned Enhancements**

**Team Size : 5**

1. Make quick search result ordering deterministic: Right now, search results can appear in different orders between keystrokes. We plan to stabilise the ordering so prefix matches surface first and other matches follow consistently.

2. Tighten duplicate detection in the grade command: Adding the same subject/assessment pair twice silently overwrites the previous score. An upcoming fix will detect this scenario and either block the duplicate or provide a clear confirmation prompt.

3. Restore the help window after minimising: Tutors who minimise the help window cannot bring it back in the same session. The enhancement will ensure the window is reopened or refocused whenever `help` is invoked again.

4. Restrict schedule date extremes: The scheduler currently accepts extreme year values (e.g., `9999`). We will clamp the allowable range and return a precise validation error for unrealistic dates.

5. Preserve filtered lists across commands: Executing follow-up commands such as `edit` or `schedule` resets the roster to show all students. We plan to retain the active predicate so tutors stay within their filtered context.

6. More specific warning messages shown for phone number inputs: Phone validation currently allows a broad range of characters; we intend to surface clearer guidance when users enter suspicious formats so they can correct potential mistakes quickly.

7. Preserve search results across commands: Executing follow-up commands such as `edit` or `schedule` resets the roster to show all students. We plan to retain the active predicate so tutors stay within their filtered context.

8. Redesign the `edit` command so that it applies a last-value-wins rule for duplicate attribute keys in command input.

9. Tighten grade score validation (e.g., reject negative or out-of-range values) so that only sensible numeric scores are persisted.

10. Relax the current identity rule that treats names as unique. Allow tutors to add students who share the same name but differ in other identifying fields (phone/email), while ensuring downstream features continue to resolve identity correctly.

---

## **Appendix: Effort**

**Difficulty relative to AB3**: ClassRosterPro extends beyond the single-entity focus of AB3 (persons) by layering additional aggregates—lessons, attendance, grades, attributes, and remarks—onto each student record. Every command must resolve and update nested structures while preserving immutability, which increases cognitive load and testing effort. Features such as cross-day scheduling, contextual filtering, and bulk attribute manipulation required more complex validation and data transformations than AB3’s straightforward CRUD operations.

**Key challenges**

- **Lesson scheduling across boundaries**: Supporting cross-day lessons (via optional `date2/`) demanded redesigning the `Lesson` model to track end dates and updating overlap detection, serialization, and UI display without breaking existing commands.
- **Filter-aware workflows**: Commands needed to honour the filtered view that tutors work in. Balancing usability (keeping context) with predictability (resetting to show all when appropriate) required careful coordination between the `Model` layer and command implementations.
- **Rich attribute management**: Multi-valued attributes, dynamic tag operations, and remark appends mandated defensive copying and descriptor patterns to avoid mutating shared references.

**Effort highlights**

- **Implementation breadth**: Most commands have both parsing and execution variants, with bespoke validation (e.g. duplicate detection for grades, overlap checks for lessons, attendance toggles). Each uses immutable replacements for `Person`/`LessonList`, leading to more boilerplate but clearer state transitions.
- **Testing**: Extensive command, parser, and model tests cover edge cases (invalid indices, duplicate grades, lesson conflicts, help window behaviour). Sequence/activity diagrams were refreshed to mirror the code, supporting maintainability.
- **Tooling & documentation**: The Developer Guide now documents each core command in depth, aided by newly created PUML diagrams (e.g., `ScheduleCommandSequence`, `UnscheduleCommandSequence`). This documentation effort parallels the code changes and helps future contributors ramp up faster.

**Reuse and adaptation**

- Some foundational pieces were adapted from AB3 (e.g., the command architecture, `AddressBookParser`, descriptor patterns). However, new features such as lesson scheduling, attendance, quick search, and attribute filtering were implemented largely from scratch.
- UI components (e.g., `PersonCard.fxml`) were extended to surface lessons, grades, and attributes. No external libraries beyond the original tech stack (JavaFX, Jackson, JUnit) were introduced. Consequently, reuse savings were minimal (<5%), and most effort went into integrating new domain concepts.

**Achievements**

- Delivered a multi-faceted student management tool covering lessons, grades, attendance, and notes—all driven by keyboard-centric commands.
- Implemented cross-day scheduling, quick search, rich filtering, and grade management atop the AB3 foundation without regressing baseline functionality.
- Augmented documentation with updated command write-ups and diagrams, clarifying the architecture for evaluators and future developers.
