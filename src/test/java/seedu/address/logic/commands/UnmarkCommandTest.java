package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_PERSON;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Lesson;
import seedu.address.model.person.LessonList;
import seedu.address.model.person.Person;
import seedu.address.testutil.PersonBuilder;

/**
 * Contains integration tests (interaction with the Model) and unit tests for UnmarkCommand.
 */
public class UnmarkCommandTest {

    private final Model model = new ModelManager(new AddressBook(), new UserPrefs());

    @Test
    public void execute_validIndexes_success() {
        // Setup: Create a person with a marked lesson
        Lesson markedLesson = new Lesson("15:00", "16:00", "2025-10-10", "Math", true);
        Person personToUnmark = new PersonBuilder().withLesson(markedLesson).build();
        model.addPerson(personToUnmark);
        Index personIndex = INDEX_FIRST_PERSON;
        Index lessonIndex = Index.fromOneBased(1);

        UnmarkCommand unmarkCommand = new UnmarkCommand(personIndex, lessonIndex);

        // Expected model after unmarking
        Model expectedModel = new ModelManager(new AddressBook(), new UserPrefs());
        expectedModel.addPerson(personToUnmark);

        Lesson unmarkedLesson = new Lesson(markedLesson.getStart(), markedLesson.getEnd(), markedLesson.getDate(),
                markedLesson.getEndDate(), markedLesson.getSub(), false);
        ArrayList<Lesson> newLessons = new ArrayList<>(personToUnmark.getLessonList().getLessons());
        newLessons.set(lessonIndex.getZeroBased(), unmarkedLesson);
        Person unmarkedPerson = new PersonBuilder(personToUnmark).withLessonList(new LessonList(newLessons)).build();
        expectedModel.setPerson(personToUnmark, unmarkedPerson);

        String expectedMessage = String.format(UnmarkCommand.MESSAGE_UNMARK_ATTENDANCE_SUCCESS,
                unmarkedPerson.getName().fullName,
                unmarkedLesson.getLessonDetails());

        assertCommandSuccess(unmarkCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_invalidPersonIndex_throwsCommandException() {
        Index outOfBoundIndex = Index.fromOneBased(model.getFilteredPersonList().size() + 1);
        UnmarkCommand unmarkCommand = new UnmarkCommand(outOfBoundIndex, Index.fromOneBased(1));

        assertCommandFailure(unmarkCommand, model, Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }

    @Test
    public void execute_invalidLessonIndex_throwsCommandException() {
        Person personInList = new PersonBuilder().withLesson(new Lesson("10:00", "11:00", "2025-01-01", "Sub", false))
                .build();
        model.addPerson(personInList);
        Index outOfBoundIndex = Index.fromOneBased(personInList.getLessonList().size() + 1);
        UnmarkCommand unmarkCommand = new UnmarkCommand(INDEX_FIRST_PERSON, outOfBoundIndex);

        assertCommandFailure(unmarkCommand, model, Messages.MESSAGE_INVALID_LESSON_DISPLAYED_INDEX);
    }

    @Test
    public void execute_lessonAlreadyUnmarked_throwsCommandException() {
        // Setup: person with an unmarked lesson
        Person personToUnmark = new PersonBuilder()
                .withLesson(new Lesson("10:00", "11:00", "2025-01-01", "Sub", false)).build();
        model.addPerson(personToUnmark);

        // Attempt to unmark the already unmarked lesson
        UnmarkCommand unmarkCommand = new UnmarkCommand(INDEX_FIRST_PERSON, Index.fromOneBased(1));
        assertCommandFailure(unmarkCommand, model, UnmarkCommand.MESSAGE_LESSON_ALREADY_UNMARKED);
    }

    @Test
    public void equals() {
        Index firstLessonIndex = Index.fromOneBased(1);
        UnmarkCommand unmarkFirstCommand = new UnmarkCommand(INDEX_FIRST_PERSON, firstLessonIndex);
        UnmarkCommand unmarkSecondCommand = new UnmarkCommand(INDEX_SECOND_PERSON, firstLessonIndex);
        UnmarkCommand unmarkThirdCommand = new UnmarkCommand(INDEX_FIRST_PERSON, Index.fromOneBased(2));

        // same object -> returns true
        assertTrue(unmarkFirstCommand.equals(unmarkFirstCommand));

        // same values -> returns true
        UnmarkCommand unmarkFirstCommandCopy = new UnmarkCommand(INDEX_FIRST_PERSON, firstLessonIndex);
        assertTrue(unmarkFirstCommand.equals(unmarkFirstCommandCopy));

        // different types -> returns false
        assertFalse(unmarkFirstCommand.equals(1));

        // null -> returns false
        assertFalse(unmarkFirstCommand.equals(null));

        // different person index -> returns false
        assertFalse(unmarkFirstCommand.equals(unmarkSecondCommand));

        // different lesson index -> returns false
        assertFalse(unmarkFirstCommand.equals(unmarkThirdCommand));
    }
}
