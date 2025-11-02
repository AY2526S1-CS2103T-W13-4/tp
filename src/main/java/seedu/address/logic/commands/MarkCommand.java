package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Lesson;
import seedu.address.model.person.LessonList;
import seedu.address.model.person.Person;

/**
 * Marks the attendance of a student for a lesson on the current day.
 */
public class MarkCommand extends Command {
    public static final String COMMAND_WORD = "mark";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Marks a lesson for a student as present.\n"
            + "Parameters: INDEX (must be a positive integer) "
            + "lesson/LESSON_INDEX (must be a positive integer)\n"
            + "Example: " + COMMAND_WORD + " 1 lesson/3";

    public static final String MESSAGE_MARK_ATTENDANCE_SUCCESS = "Attendance marked: %1$s, Lesson: %2$s -> Present";
    public static final String MESSAGE_LESSON_ALREADY_MARKED = "This lesson is already marked as present.";

    private static final Logger logger = LogsCenter.getLogger(MarkCommand.class);

    private final Index personIndex;
    private final Index lessonIndex;

    /**
     * Creates a MarkCommand to mark attendance for the specified lesson of a person.
     * @param personIndex of the person in the filtered person list.
     * @param lessonIndex of the lesson in the person's lesson list.
     */
    public MarkCommand(Index personIndex, Index lessonIndex) {
        requireNonNull(personIndex);
        requireNonNull(lessonIndex);
        this.personIndex = personIndex;
        this.lessonIndex = lessonIndex;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        logger.info("Executing MarkCommand for person index: " + personIndex.getOneBased()
                + ", lesson index: " + lessonIndex.getOneBased());
        assert model != null : "model should not be null";
        List<Person> lastShownList = model.getFilteredPersonList();

        if (personIndex.getZeroBased() >= lastShownList.size()) {
            logger.warning("Invalid person index provided: " + personIndex.getOneBased());
            throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }

        Person personToMark = lastShownList.get(personIndex.getZeroBased());
        LessonList lessonList = personToMark.getLessonList();

        if (lessonIndex.getZeroBased() >= lessonList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_LESSON_DISPLAYED_INDEX);
        }

        Lesson lessonToMark = lessonList.get(lessonIndex.getZeroBased());

        if (lessonToMark.isPresent()) {
            throw new CommandException(MESSAGE_LESSON_ALREADY_MARKED);
        }

        Lesson markedLesson = new Lesson(lessonToMark.getStart(), lessonToMark.getEnd(),
                lessonToMark.getDate(), lessonToMark.getEndDate(), lessonToMark.getSub(), true);

        ArrayList<Lesson> newLessons = new ArrayList<>(lessonList.getLessons());
        newLessons.set(lessonIndex.getZeroBased(), markedLesson);
        LessonList updatedLessonList = new LessonList(newLessons);

        Person markedPerson = new Person(
                personToMark.getName(), personToMark.getPhone(), personToMark.getEmail(),
                personToMark.getAddress(), personToMark.getRemark(), personToMark.getTags(),
                personToMark.getAttributes(), updatedLessonList, personToMark.getGradeList());

        model.setPerson(personToMark, markedPerson);
        logger.info("Attendance marked for lesson " + lessonIndex.getOneBased() + " of person: "
                + personToMark.getName().fullName);
        return new CommandResult(String.format(MESSAGE_MARK_ATTENDANCE_SUCCESS, personToMark.getName().fullName,
                markedLesson.getLessonDetails()));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof MarkCommand)) {
            return false;
        }

        MarkCommand otherMarkCommand = (MarkCommand) other;
        return personIndex.equals(otherMarkCommand.personIndex)
                && lessonIndex.equals(otherMarkCommand.lessonIndex);
    }
}
