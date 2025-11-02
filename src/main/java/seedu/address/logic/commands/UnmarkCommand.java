package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.List;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Lesson;
import seedu.address.model.person.LessonList;
import seedu.address.model.person.Person;

/**
 * Unmarks the attendance of a student for a lesson on the current day.
 */
public class UnmarkCommand extends Command {
    public static final String COMMAND_WORD = "unmark";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Unmarks a lesson for a student as not present.\n"
            + "Parameters: INDEX (must be a positive integer) "
            + "lesson/LESSON_INDEX (must be a positive integer)\n"
            + "Example: " + COMMAND_WORD + " 1 lesson/3";

    public static final String MESSAGE_UNMARK_ATTENDANCE_SUCCESS = "Attendance unmarked: %1$s, Lesson: %2$s ->"
            + " Not Present";
    public static final String MESSAGE_LESSON_ALREADY_UNMARKED = "This lesson is already marked as not present.";

    private final Index personIndex;
    private final Index lessonIndex;

    /**
     * Creates an UnmarkCommand to unmark attendance for the specified lesson of a person.
     * @param personIndex of the person in the filtered person list.
     * @param lessonIndex of the lesson in the person's lesson list.
     */
    public UnmarkCommand(Index personIndex, Index lessonIndex) {
        requireNonNull(personIndex);
        requireNonNull(lessonIndex);
        this.personIndex = personIndex;
        this.lessonIndex = lessonIndex;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Person> lastShownList = model.getFilteredPersonList();

        if (personIndex.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }

        Person personToUnmark = lastShownList.get(personIndex.getZeroBased());
        LessonList lessonList = personToUnmark.getLessonList();

        if (lessonIndex.getZeroBased() >= lessonList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_LESSON_DISPLAYED_INDEX);
        }

        Lesson lessonToUnmark = lessonList.get(lessonIndex.getZeroBased());

        if (!lessonToUnmark.isPresent()) {
            throw new CommandException(MESSAGE_LESSON_ALREADY_UNMARKED);
        }

        Lesson unmarkedLesson = new Lesson(lessonToUnmark.getStart(), lessonToUnmark.getEnd(),
                lessonToUnmark.getDate(), lessonToUnmark.getEndDate(), lessonToUnmark.getSub(), false);

        ArrayList<Lesson> newLessons = new ArrayList<>(lessonList.getLessons());
        newLessons.set(lessonIndex.getZeroBased(), unmarkedLesson);
        LessonList updatedLessonList = new LessonList(newLessons);

        Person unmarkedPerson = new Person(
                personToUnmark.getName(), personToUnmark.getPhone(), personToUnmark.getEmail(),
                personToUnmark.getAddress(), personToUnmark.getRemark(), personToUnmark.getTags(),
                personToUnmark.getAttributes(), updatedLessonList, personToUnmark.getGradeList());

        model.setPerson(personToUnmark, unmarkedPerson);

        return new CommandResult(String.format(MESSAGE_UNMARK_ATTENDANCE_SUCCESS, personToUnmark.getName().fullName,
                unmarkedLesson.getLessonDetails()));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof UnmarkCommand)) {
            return false;
        }

        UnmarkCommand otherUnmarkCommand = (UnmarkCommand) other;
        return personIndex.equals(otherUnmarkCommand.personIndex)
                && lessonIndex.equals(otherUnmarkCommand.lessonIndex);
    }
}
