package seedu.address.model.person;

import static java.util.Objects.requireNonNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Represents a collection of grades for a person.
 * Guarantees: immutable; grades are always valid
 */
public class GradeList {
    private final Map<String, Grade> grades;

    /**
     * Constructs an empty GradeList.
     */
    public GradeList() {
        this.grades = new HashMap<>();
    }

    /**
     * Constructs a GradeList with the given grades.
     */
    public GradeList(Map<String, Grade> grades) {
        requireNonNull(grades);
        this.grades = new HashMap<>(grades);
    }

    /**
     * Adds or updates a grade for a subject and assessment.
     * If the subject and assessment combination already exists, the grade will be overwritten.
     */
    public GradeList addGrade(Grade grade) {
        requireNonNull(grade);
        Map<String, Grade> newGrades = new HashMap<>(this.grades);
        String key = grade.getSubject() + "/" + grade.getAssessment();
        newGrades.put(key, grade);
        return new GradeList(newGrades);
    }

    /**
     * Removes a grade for the specified subject and assessment if it exists.
     */
    public GradeList removeGrade(String subject, String assessment) {
        requireNonNull(subject);
        requireNonNull(assessment);
        Map<String, Grade> newGrades = new HashMap<>(this.grades);
        String key = subject + "/" + assessment;
        newGrades.remove(key);
        return new GradeList(newGrades);
    }

    /**
     * Returns the grade for the specified subject and assessment, or null if not found.
     */
    public Grade getGrade(String subject, String assessment) {
        String key = subject + "/" + assessment;
        return grades.get(key);
    }

    /**
     * Returns true if a grade exists for the specified subject and assessment.
     */
    public boolean hasGrade(String subject, String assessment) {
        String key = subject + "/" + assessment;
        return grades.containsKey(key);
    }

    /**
     * Returns all grades as a map.
     */
    public Map<String, Grade> getGrades() {
        return new HashMap<>(grades);
    }

    /**
     * Returns true if the grade list is empty.
     */
    public boolean isEmpty() {
        return grades.isEmpty();
    }

    /**
     * Returns the number of grades.
     */
    public int size() {
        return grades.size();
    }

    @Override
    public String toString() {
        if (grades.isEmpty()) {
            return "No grades recorded";
        }
        return grades.values().toString();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof GradeList)) {
            return false;
        }
        GradeList otherGradeList = (GradeList) other;
        return grades.equals(otherGradeList.grades);
    }

    @Override
    public int hashCode() {
        return Objects.hash(grades);
    }
}
