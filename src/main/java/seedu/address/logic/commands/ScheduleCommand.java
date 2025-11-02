package seedu.address.logic.commands;

import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_PERSONS;

import java.util.List;
import java.util.logging.Logger;

import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Lesson;
import seedu.address.model.person.Person;

/**
 * Changes the lesson of an existing person in the address book.
 */
public class ScheduleCommand extends Command {

    public static final String COMMAND_WORD = "schedule";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Adds a lesson to the person identified "
            + "by the index number used in the last person listing. "
            + "Existing student will have new lesson stated in input added.\n"
            + "Parameters: INDEX (must be a positive integer) "
            + "start/ [START TIME] end/ [END TIME} "
            + "date/ [START DATE] [date2/ END DATE] sub/ [SUBJECT]\n"
            + "Example (same day): " + COMMAND_WORD + " 1 "
            + "start/ 09:30 end/ 11:30 "
            + "date/ 2025-09-20 sub/ Maths\n"
            + "Example (overnight): " + COMMAND_WORD + " 1 "
            + "start/ 22:00 end/ 01:00 "
            + "date/ 2025-09-20 date2/ 2025-09-21 sub/ Camp";

    public static final String MESSAGE_DUPLICATE_LESSON = "The student already has the existing lesson!";
    public static final String MESSAGE_ADD_LESSON_SUCCESS = "Scheduled Lesson to Person: %1$s";
    public static final String MESSAGE_OVERLAPPING_LESSON =
            "The student already has a lesson that overlaps with the provided timeslot!";

    private static final Logger logger = LogsCenter.getLogger(ScheduleCommand.class);

    private final Index index;
    private final Lesson lesson;

    /**
     * @param index of the person in the filtered person list to edit the remark
     * @param lesson of the person to be updated to
     */
    public ScheduleCommand(Index index, Lesson lesson) {
        requireAllNonNull(index, lesson);

        this.index = index;
        this.lesson = lesson;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireAllNonNull(model, index, lesson);
        logger.fine(() -> String.format("Executing ScheduleCommand for person index %s with lesson %s",
                index.getOneBased(), lesson));

        List<Person> lastShownList = model.getFilteredPersonList();

        if (index.getZeroBased() >= lastShownList.size()) {
            logger.fine("ScheduleCommand failed due to invalid person index.");
            throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }

        Person personToEdit = lastShownList.get(index.getZeroBased());
        assert personToEdit != null : "Person to edit should not be null";

        if (personToEdit.getLessonList().hasDuplicates(lesson)) {
            logger.fine("ScheduleCommand detected duplicate lesson; aborting.");
            throw new CommandException(MESSAGE_DUPLICATE_LESSON);
        }

        if (personToEdit.getLessonList().hasOverlappingLesson(lesson)) {
            logger.fine("ScheduleCommand detected overlapping lesson; aborting.");
            throw new CommandException(MESSAGE_OVERLAPPING_LESSON);
        }

        int originalLessonCount = personToEdit.getLessonList().size();

        Person editedPerson = new Person(
                personToEdit.getName(), personToEdit.getPhone(), personToEdit.getEmail(),
                personToEdit.getAddress(), personToEdit.getRemark(),
                personToEdit.getTags(), personToEdit.getAttributes(),
                personToEdit.getLessonList().add(lesson), personToEdit.getGradeList());

        assert editedPerson.getLessonList().size() == originalLessonCount + 1
                : "Lesson list should grow by exactly one lesson";

        model.setPerson(personToEdit, editedPerson);
        model.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);

        logger.fine(() -> String.format("ScheduleCommand succeeded for person %s", index.getOneBased()));
        return new CommandResult(generateSuccessMessage(editedPerson));
    }

    /**
     * Generates a command execution success message for
     * the lesson being added to {@code personToEdit}.
     */
    private String generateSuccessMessage(Person personToEdit) {
        String message = MESSAGE_ADD_LESSON_SUCCESS;
        return String.format(message, Messages.format(personToEdit));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof ScheduleCommand)) {
            return false;
        }

        ScheduleCommand e = (ScheduleCommand) other;
        return index.equals(e.index)
                && lesson.equals(e.lesson);
    }
}
