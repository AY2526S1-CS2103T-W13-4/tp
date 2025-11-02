package seedu.address.model.person;

import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Objects;

/**
 * Represents a Person's Lesson in the address book.
 * Guarantees: immutable; is always valid
 */
public class Lesson implements Comparable<Lesson> {

    private final LocalTime start;
    private final LocalTime end;
    private final LocalDate startDate;
    private final LocalDate endDate;
    private final String sub;
    private final boolean isPresent;

    /**
     * Constructs a {@code Lesson} with identical start and end dates and attendance set to false.
     */
    public Lesson(String start, String end, String date, String sub) {
        this(start, end, date, date, sub, false);
    }

    /**
     * Constructs a {@code Lesson} with identical start and end dates and attendance set to false.
     */
    public Lesson(LocalTime start, LocalTime end, LocalDate date, String sub) {
        this(start, end, date, date, sub, false);
    }

    /**
     * Constructs a {@code Lesson} with identical start and end dates and explicit attendance.
     */
    public Lesson(String start, String end, String date, String sub, boolean isPresent) {
        this(start, end, date, date, sub, isPresent);
    }

    /**
     * Constructs a {@code Lesson} with identical start and end dates and explicit attendance.
     */
    public Lesson(LocalTime start, LocalTime end, LocalDate date, String sub, boolean isPresent) {
        this(start, end, date, date, sub, isPresent);
    }

    /**
     * Constructs a {@code Lesson} with explicit dates.
     */
    public Lesson(String start, String end, String startDate, String endDate, String sub, boolean isPresent) {
        this(LocalTime.parse(start), LocalTime.parse(end), LocalDate.parse(startDate),
                LocalDate.parse(endDate), sub, isPresent);
    }

    /**
     * Constructs a {@code Lesson} with explicit dates.
     */
    public Lesson(LocalTime start, LocalTime end, LocalDate startDate, LocalDate endDate,
                  String sub, boolean isPresent) {
        requireAllNonNull(start, end, startDate, endDate, sub);
        this.start = start;
        this.end = end;
        this.startDate = startDate;
        this.endDate = endDate;
        this.sub = sub;
        this.isPresent = isPresent;
    }

    public LocalTime getStart() {
        return this.start;
    }

    public LocalTime getEnd() {
        return this.end;
    }

    public LocalDate getDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public String getSub() {
        return this.sub;
    }

    public boolean isPresent() {
        return this.isPresent;
    }

    /**
     * Returns true if this lesson overlaps in time with {@code other}.
     * Lessons that end exactly when another begins are not considered overlapping.
     */
    public boolean overlapsWith(Lesson other) {
        requireAllNonNull(other);
        return getStartDateTime().isBefore(other.getEndDateTime())
                && getEndDateTime().isAfter(other.getStartDateTime());
    }

    /**
     * Returns a string with the lesson's details, excluding attendance.
     */
    public String getLessonDetails() {
        StringBuilder builder = new StringBuilder();
        builder.append(sub)
                .append(" from ")
                .append(startDate.toString())
                .append(" ")
                .append(start.toString())
                .append(" to ")
                .append(endDate.toString())
                .append(" ")
                .append(end.toString());
        return builder.toString();
    }

    /**
     * Compares this lesson with another lesson chronologically.
     * Lessons are ordered by start date/time first, then by start time when dates are equal.
     */
    @Override
    public int compareTo(Lesson other) {
        return this.getStartDateTime().compareTo(other.getStartDateTime());
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof Lesson)) {
            return false;
        }

        Lesson otherLesson = (Lesson) other;
        return start.equals(otherLesson.start)
                && end.equals(otherLesson.end)
                && startDate.equals(otherLesson.startDate)
                && endDate.equals(otherLesson.endDate)
                && sub.equals(otherLesson.sub)
                && isPresent == otherLesson.isPresent;
    }

    @Override
    public int hashCode() {
        return Objects.hash(start, end, startDate, endDate, sub, isPresent);
    }

    @Override
    public String toString() {
        String attendance = isPresent ? "[Present]" : "[Not Present]";
        return sub + " : " + startDate.toString() + " " + start.toString()
                + " to " + endDate.toString() + " " + end.toString() + attendance;
    }

    private LocalDateTime getStartDateTime() {
        return startDate.atTime(start);
    }

    private LocalDateTime getEndDateTime() {
        return endDate.atTime(end);
    }
}
