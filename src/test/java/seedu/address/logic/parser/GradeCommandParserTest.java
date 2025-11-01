package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.logic.parser.ParserUtil.MESSAGE_INVALID_INDEX;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.GradeCommand;
import seedu.address.model.person.Grade;

public class GradeCommandParserTest {

    private GradeCommandParser parser = new GradeCommandParser();

    @Test
    public void parse_validArgs_returnsGradeCommand() {
        Set<Grade> expectedGrades = new HashSet<>();
        expectedGrades.add(new Grade("MATH", "WA1", "89"));

        assertParseSuccess(parser, "1 sub/MATH/WA1/89",
                new GradeCommand(INDEX_FIRST_PERSON, expectedGrades, new HashSet<>()));
    }

    @Test
    public void parse_validArgsMultipleGrades_returnsGradeCommand() {
        Set<Grade> expectedGrades = new HashSet<>();
        expectedGrades.add(new Grade("MATH", "WA1", "89"));
        expectedGrades.add(new Grade("SCIENCE", "Quiz1", "95"));

        assertParseSuccess(parser, "1 sub/MATH/WA1/89 sub/SCIENCE/Quiz1/95",
                new GradeCommand(INDEX_FIRST_PERSON, expectedGrades, new HashSet<>()));
    }

    @Test
    public void parse_validDeletion_returnsGradeCommand() {
        Set<Grade> expectedGrades = new HashSet<>();
        Set<GradeCommand.GradeKey> gradesToDelete = new HashSet<>();
        gradesToDelete.add(new GradeCommand.GradeKey("MATH", "WA1"));

        assertParseSuccess(parser, "1 sub/MATH/WA1",
                new GradeCommand(INDEX_FIRST_PERSON, expectedGrades, gradesToDelete));
    }

    @Test
    public void parse_mixedAddAndDelete_returnsGradeCommand() {
        Set<Grade> expectedGrades = new HashSet<>();
        expectedGrades.add(new Grade("SCIENCE", "Quiz1", "95"));
        Set<GradeCommand.GradeKey> gradesToDelete = new HashSet<>();
        gradesToDelete.add(new GradeCommand.GradeKey("MATH", "WA1"));

        assertParseSuccess(parser, "1 sub/MATH/WA1 sub/SCIENCE/Quiz1/95",
                new GradeCommand(INDEX_FIRST_PERSON, expectedGrades, gradesToDelete));
    }

    @Test
    public void parse_invalidIndex_throwsParseException() {
        assertParseFailure(parser, "0 sub/MATH/WA1/89", MESSAGE_INVALID_INDEX);
        assertParseFailure(parser, "a sub/MATH/WA1/89", MESSAGE_INVALID_INDEX);
    }

    @Test
    public void parse_missingIndex_throwsParseException() {
        assertParseFailure(parser, "sub/MATH/WA1/89",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, GradeCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_missingSubPrefix_throwsParseException() {
        assertParseFailure(parser, "1 MATH/WA1/89",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, GradeCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_emptyArgs_throwsParseException() {
        assertParseFailure(parser, "",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, GradeCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_indexWithWhitespace_throwsParseException() {
        assertParseFailure(parser, "1 2 sub/MATH/WA1/89",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, GradeCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_invalidFormat_throwsParseException() {
        // Using = instead of / results in only 1 part after split
        assertParseFailure(parser, "1 sub/MATH=WA1=89",
                "Assessment is missing. Use sub/SUBJECT/ASSESSMENT[/SCORE]");
    }

    @Test
    public void parse_tooManyParts_throwsParseException() {
        assertParseFailure(parser, "1 sub/MATH/WA1/89/EXTRA",
                "Too many parts. Use sub/SUBJECT/ASSESSMENT[/SCORE]");
    }

    @Test
    public void parse_missingScoreWithTrailingSlash_throwsParseException() {
        assertParseFailure(parser, "1 sub/MATH/WA1/",
                "Score cannot be empty.");
    }

    @Test
    public void parse_onlySubject_throwsParseException() {
        assertParseFailure(parser, "1 sub/MATH",
                "Assessment is missing. Use sub/SUBJECT/ASSESSMENT[/SCORE]");
    }

    @Test
    public void parse_emptySubject_throwsParseException() {
        assertParseFailure(parser, "1 sub//WA1/89",
                "Subject cannot be empty.");
    }

    @Test
    public void parse_emptyAssessment_throwsParseException() {
        assertParseFailure(parser, "1 sub/MATH//89",
                "Assessment cannot be empty.");
    }

    @Test
    public void parse_validWithWhitespace_success() {
        Set<Grade> expectedGrades = new HashSet<>();
        expectedGrades.add(new Grade("MATH", "WA1", "89"));

        assertParseSuccess(parser, "1 sub/ MATH / WA1 / 89 ",
                new GradeCommand(INDEX_FIRST_PERSON, expectedGrades, new HashSet<>()));
    }
}
