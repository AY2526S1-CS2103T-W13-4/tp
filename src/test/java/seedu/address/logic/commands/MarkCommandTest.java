package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_PERSON;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Lesson;
import seedu.address.model.person.LessonList;
import seedu.address.model.person.Person;
import seedu.address.testutil.PersonBuilder;

/**
 * Contains integration tests (interaction with the Model) and unit tests for MarkCommand.
 */
public class MarkCommandTest {

    private final Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void execute_validIndexes_success() {
        // Setup: Create a person with an unmarked lesson and add to model
        Person personInModel = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Lesson lessonToMark = new Lesson("10:00", "11:00", "2025-01-01", "Math", false);
        Person personToMark = new PersonBuilder(personInModel).withLesson(lessonToMark).build();
        model.setPerson(personInModel, personToMark);

        Index lessonIndex = Index.fromOneBased(1);
        MarkCommand markCommand = new MarkCommand(INDEX_FIRST_PERSON, lessonIndex);

        // Expected model after marking
        Model expectedModel = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        // Setup expected model in the same way
        Person expectedPersonInModel = expectedModel.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Person expectedPersonToMark = new PersonBuilder(expectedPersonInModel).withLesson(lessonToMark).build();
        expectedModel.setPerson(expectedPersonInModel, expectedPersonToMark);

        Lesson markedLesson = new Lesson(lessonToMark.getStart(), lessonToMark.getEnd(),
                lessonToMark.getDate(), lessonToMark.getEndDate(), lessonToMark.getSub(), true);
        ArrayList<Lesson> newLessons = new ArrayList<>(expectedPersonToMark.getLessonList().getLessons());
        newLessons.set(lessonIndex.getZeroBased(), markedLesson);
        Person markedPerson = new PersonBuilder(expectedPersonToMark)
                .withLessonList(new LessonList(newLessons)).build();
        expectedModel.setPerson(expectedPersonToMark, markedPerson);

        String expectedMessage = String.format(MarkCommand.MESSAGE_MARK_ATTENDANCE_SUCCESS,
                                               markedPerson.getName().fullName,
                                               markedLesson.getLessonDetails());
        assertCommandSuccess(markCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_invalidPersonIndex_throwsCommandException() {
        Index outOfBoundIndex = Index.fromOneBased(model.getFilteredPersonList().size() + 1);
        MarkCommand markCommand = new MarkCommand(outOfBoundIndex, Index.fromOneBased(1));

        assertCommandFailure(markCommand, model, Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }

    @Test
    public void execute_invalidLessonIndex_throwsCommandException() {
        Person personInList = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Index outOfBoundIndex = Index.fromOneBased(personInList.getLessonList().size() + 1);
        MarkCommand markCommand = new MarkCommand(INDEX_FIRST_PERSON, outOfBoundIndex);

        assertCommandFailure(markCommand, model, Messages.MESSAGE_INVALID_LESSON_DISPLAYED_INDEX);
    }

    @Test
    public void execute_lessonAlreadyMarked_throwsCommandException() {
        // Setup: mark the lesson first
        Index lessonIndex = Index.fromOneBased(1);
        Person personInModel = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Lesson lessonToMark = new Lesson("10:00", "11:00", "2025-01-01", "Math", false);
        Person personToMark = new PersonBuilder(personInModel).withLesson(lessonToMark).build();
        Lesson markedLesson = new Lesson(lessonToMark.getStart(), lessonToMark.getEnd(),
                lessonToMark.getDate(), lessonToMark.getEndDate(), lessonToMark.getSub(), true);
        ArrayList<Lesson> newLessons = new ArrayList<>(personToMark.getLessonList().getLessons());
        newLessons.set(lessonIndex.getZeroBased(), markedLesson);
        Person markedPerson = new PersonBuilder(personToMark).withLessonList(new LessonList(newLessons)).build();
        model.setPerson(personInModel, markedPerson);

        // Attempt to mark the already marked lesson
        MarkCommand markCommand = new MarkCommand(INDEX_FIRST_PERSON, lessonIndex);
        assertCommandFailure(markCommand, model, MarkCommand.MESSAGE_LESSON_ALREADY_MARKED);
    }

    @Test
    public void equals() {
        Index firstLessonIndex = Index.fromOneBased(1);
        MarkCommand markFirstCommand = new MarkCommand(INDEX_FIRST_PERSON, firstLessonIndex);
        MarkCommand markSecondCommand = new MarkCommand(INDEX_SECOND_PERSON, firstLessonIndex);
        MarkCommand markThirdCommand = new MarkCommand(INDEX_FIRST_PERSON, Index.fromOneBased(2));

        // same object -> returns true
        assertTrue(markFirstCommand.equals(markFirstCommand));

        // same values -> returns true
        MarkCommand markFirstCommandCopy = new MarkCommand(INDEX_FIRST_PERSON, firstLessonIndex);
        assertTrue(markFirstCommand.equals(markFirstCommandCopy));

        // different types -> returns false
        assertFalse(markFirstCommand.equals(1));

        // null -> returns false
        assertFalse(markFirstCommand.equals(null));

        // different person index -> returns false
        assertFalse(markFirstCommand.equals(markSecondCommand));

        // different lesson index -> returns false
        assertFalse(markFirstCommand.equals(markThirdCommand));
    }
}
