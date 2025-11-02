package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.jupiter.api.Test;

public class LessonListTest {

    private final Lesson lesson1 = new Lesson("10:00", "11:00", "2023-01-01", "Math", false);
    private final Lesson lesson2 = new Lesson("12:00", "13:00", "2023-01-01", "Science", true);
    private final Lesson lesson3 = new Lesson("09:00", "10:00", "2023-01-02", "Physics", true);
    private final Lesson overlappingLesson = new Lesson("10:30", "11:30", "2023-01-01", "English", false);
    private final Lesson adjacentLesson = new Lesson("11:00", "12:00", "2023-01-01", "History", false);
    private final Lesson overnightLesson = new Lesson("23:00", "01:00", "2023-01-01", "2023-01-02", "Camp", false);
    private final Lesson earlyMorningLesson = new Lesson("00:30", "02:00", "2023-01-02", "2023-01-02", "Breakfast", false);

    @Test
    public void checkForDuplicates_haveDuplicate_returnsTrue() {
        ArrayList<Lesson> unsortedList = new ArrayList<>(Arrays.asList(lesson1, lesson2, lesson3));
        LessonList lessonList = new LessonList(unsortedList);

        // Adding lesson1 to lessonList should be a duplicate, should return true
        assertTrue(lessonList.hasDuplicates(lesson1));
    }

    @Test
    public void checkForDuplicates_noDuplicate_returnsFalse() {
        ArrayList<Lesson> unsortedList = new ArrayList<>(Arrays.asList(lesson1, lesson2));
        LessonList lessonList = new LessonList(unsortedList);

        // lesson3 is not in the list, should return false
        assertFalse(lessonList.hasDuplicates(lesson3));
    }

    @Test
    public void hasOverlappingLesson_overlapOnSameDate_returnsTrue() {
        LessonList lessonList = new LessonList().add(lesson1);
        assertTrue(lessonList.hasOverlappingLesson(overlappingLesson));
    }

    @Test
    public void hasOverlappingLesson_adjacentLessons_returnsFalse() {
        LessonList lessonList = new LessonList().add(lesson1);
        assertFalse(lessonList.hasOverlappingLesson(adjacentLesson));
    }

    @Test
    public void hasOverlappingLesson_crossDayOverlap_returnsTrue() {
        LessonList lessonList = new LessonList().add(overnightLesson);
        assertTrue(lessonList.hasOverlappingLesson(earlyMorningLesson));
    }

    @Test
    public void constructor_fromArrayList_sortsLessons() {
        ArrayList<Lesson> unsortedList = new ArrayList<>(Arrays.asList(lesson2, lesson3, lesson1));
        LessonList lessonList = new LessonList(unsortedList);

        // Lessons should be sorted chronologically: lesson1, lesson2, lesson3
        ArrayList<Lesson> expectedList = new ArrayList<>(Arrays.asList(lesson1, lesson2, lesson3));
        assertEquals(expectedList, lessonList.getLessons());
    }

    @Test
    public void add_maintainsChronologicalOrderAndImmutability() {
        LessonList initialList = new LessonList();
        // Add lesson2 first, then lesson1
        LessonList listWithLesson2 = initialList.add(lesson2);
        LessonList finalList = listWithLesson2.add(lesson1);

        // Test immutability
        assertTrue(initialList.isEmpty()); // Original list is unchanged
        assertEquals(1, listWithLesson2.size()); // Intermediate list is unchanged

        // Test chronological order
        ArrayList<Lesson> expectedOrder = new ArrayList<>(Arrays.asList(lesson1, lesson2));
        assertEquals(expectedOrder, finalList.getLessons());
    }

    @Test
    public void getAttendedLessonCount_correctCount() {
        // No lessons
        LessonList emptyList = new LessonList();
        assertEquals(0, emptyList.getAttendedLessonCount());

        // One attended, one not
        LessonList mixedList = new LessonList().add(lesson1).add(lesson2); // lesson2 is attended
        assertEquals(1, mixedList.getAttendedLessonCount());

        // All attended
        LessonList allAttendedList = new LessonList().add(lesson2).add(lesson3); // lesson2 and lesson3 are attended
        assertEquals(2, allAttendedList.getAttendedLessonCount());
    }

    @Test
    public void getLessons_returnsDefensiveCopy() {
        LessonList lessonList = new LessonList().add(lesson1);
        ArrayList<Lesson> lessonsCopy = lessonList.getLessons();

        // Modify the copy
        lessonsCopy.add(lesson2);

        // Original lesson list should not be affected
        assertNotEquals(lessonsCopy, lessonList.getLessons());
        assertEquals(1, lessonList.size());
    }

    @Test
    public void equals() {
        LessonList lessonList = new LessonList(new ArrayList<>(Arrays.asList(lesson1, lesson2)));

        // same values -> returns true
        LessonList lessonListCopy = new LessonList(new ArrayList<>(Arrays.asList(lesson1, lesson2)));
        assertTrue(lessonList.equals(lessonListCopy));

        // same object -> returns true
        assertTrue(lessonList.equals(lessonList));

        // null -> returns false
        assertFalse(lessonList.equals(null));

        // different types -> returns false
        assertFalse(lessonList.equals(5));

        // different lessons -> returns false
        LessonList differentLessonList = new LessonList(new ArrayList<>(Arrays.asList(lesson1, lesson3)));
        assertFalse(lessonList.equals(differentLessonList));
    }
}
