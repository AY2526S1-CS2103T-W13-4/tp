package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class LessonTest {

    private final Lesson lesson = new Lesson("10:00", "11:00", "2023-01-01", "Math", false);

    @Test
    public void equals() {
        // same values -> returns true
        Lesson lessonCopy = new Lesson("10:00", "11:00", "2023-01-01", "Math", false);
        assertTrue(lesson.equals(lessonCopy));

        // same object -> returns true
        assertTrue(lesson.equals(lesson));

        // null -> returns false
        assertFalse(lesson.equals(null));

        // different type -> returns false
        assertFalse(lesson.equals(5));

        // different start time -> returns false
        Lesson editedLesson = new Lesson("10:01", "11:00", "2023-01-01", "Math", false);
        assertFalse(lesson.equals(editedLesson));

        // different date -> returns false
        editedLesson = new Lesson("10:00", "11:00", "2023-01-02", "Math", false);
        assertFalse(lesson.equals(editedLesson));

        // different subject -> returns false
        editedLesson = new Lesson("10:00", "11:00", "2023-01-01", "Science", false);
        assertFalse(lesson.equals(editedLesson));

        // different isPresent status -> returns false
        editedLesson = new Lesson("10:00", "11:00", "2023-01-01", "Math", true);
        assertFalse(lesson.equals(editedLesson));

        // different end date -> returns false
        editedLesson = new Lesson("10:00", "11:00", "2023-01-01", "2023-01-02", "Math", false);
        assertFalse(lesson.equals(editedLesson));
    }

    @Test
    public void toString_formatsCorrectly() {
        // Lesson is not present
        Lesson notPresentLesson = new Lesson("14:00", "15:00", "2023-01-02", "Science", false);
        String expectedNotPresentString = "Science : 2023-01-02 14:00 to 2023-01-02 15:00[Not Present]";
        assertEquals(expectedNotPresentString, notPresentLesson.toString());

        // Lesson is present
        Lesson presentLesson = new Lesson("10:00", "11:00", "2023-01-01", "Math", true);
        String expectedPresentString = "Math : 2023-01-01 10:00 to 2023-01-01 11:00[Present]";
        assertEquals(expectedPresentString, presentLesson.toString());

        // Cross-day lesson formatting
        Lesson overnightLesson = new Lesson("22:00", "01:00", "2023-01-01", "2023-01-02", "Camp", false);
        String expectedCrossDayString = "Camp : 2023-01-01 22:00 to 2023-01-02 01:00[Not Present]";
        assertEquals(expectedCrossDayString, overnightLesson.toString());
    }

    @Test
    public void overlapsWith_sameDateOverlap_returnsTrue() {
        Lesson existingLesson = new Lesson("10:00", "11:00", "2023-01-01", "Math", false);
        Lesson overlappingLesson = new Lesson("10:30", "11:30", "2023-01-01", "Science", false);
        assertTrue(existingLesson.overlapsWith(overlappingLesson));
        assertTrue(overlappingLesson.overlapsWith(existingLesson));
    }

    @Test
    public void overlapsWith_noOverlap_returnsFalse() {
        Lesson existingLesson = new Lesson("10:00", "11:00", "2023-01-01", "Math", false);
        Lesson adjacentLesson = new Lesson("11:00", "12:00", "2023-01-01", "Science", false);
        Lesson differentDateLesson = new Lesson("10:30", "11:30", "2023-01-02", "Science", false);

        assertFalse(existingLesson.overlapsWith(adjacentLesson));
        assertFalse(existingLesson.overlapsWith(differentDateLesson));
    }

    @Test
    public void overlapsWith_crossDayOverlap_returnsTrue() {
        Lesson overnightLesson = new Lesson("23:00", "01:00", "2023-01-01", "2023-01-02", "Camp", false);
        Lesson earlyMorningLesson = new Lesson("00:30", "02:00", "2023-01-02", "2023-01-02", "Breakfast", false);

        assertTrue(overnightLesson.overlapsWith(earlyMorningLesson));
        assertTrue(earlyMorningLesson.overlapsWith(overnightLesson));
    }
}
