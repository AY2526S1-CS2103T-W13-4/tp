package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.logic.parser.ScheduleCommandParser.MESSAGE_END_TIME_BEFORE_START;
import static seedu.address.logic.parser.ScheduleCommandParser.MESSAGE_INVALID_DATE_FORMAT;
import static seedu.address.logic.parser.ScheduleCommandParser.MESSAGE_INVALID_DATE_VALUE;
import static seedu.address.logic.parser.ScheduleCommandParser.MESSAGE_INVALID_END_TIME_FORMAT;
import static seedu.address.logic.parser.ScheduleCommandParser.MESSAGE_INVALID_END_TIME_VALUE;
import static seedu.address.logic.parser.ScheduleCommandParser.MESSAGE_INVALID_START_TIME_FORMAT;
import static seedu.address.logic.parser.ScheduleCommandParser.MESSAGE_INVALID_START_TIME_VALUE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_DATE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_END;
import static seedu.address.logic.parser.CliSyntax.PREFIX_START;
import static seedu.address.logic.parser.CliSyntax.PREFIX_SUB;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;

import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.logic.commands.ScheduleCommand;
import seedu.address.model.person.Lesson;

/**
 * Contains unit tests for {@link ScheduleCommandParser}.
 */
public class ScheduleCommandParserTest {

    private static final String VALID_ARGUMENTS =
            " start/10:00 end/12:00 date/2025-10-20 sub/Mathematics";

    private final ScheduleCommandParser parser = new ScheduleCommandParser();

    @Test
    public void parse_allFieldsPresent_success() {
        String userInput = INDEX_FIRST_PERSON.getOneBased() + VALID_ARGUMENTS;
        Lesson lesson = new Lesson("10:00", "12:00", "2025-10-20", "Mathematics", false);
        ScheduleCommand expectedCommand = new ScheduleCommand(INDEX_FIRST_PERSON, lesson);
        assertParseSuccess(parser, userInput, expectedCommand);
    }

    @Test
    public void parse_missingLessonPrefix_failure() {
        String userInput = INDEX_FIRST_PERSON.getOneBased() + " start/10:00 end/12:00 date/2025-10-20";
        assertParseFailure(parser, userInput,
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, ScheduleCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_invalidPersonIndex_failure() {
        String userInput = "a" + VALID_ARGUMENTS;
        assertParseFailure(parser, userInput,
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, ScheduleCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_invalidStartTimeFormat_failure() {
        String userInput = INDEX_FIRST_PERSON.getOneBased()
                + " start/aa:00 end/12:00 date/2025-10-20 sub/Mathematics";
        assertParseFailure(parser, userInput, MESSAGE_INVALID_START_TIME_FORMAT);
    }

    @Test
    public void parse_endTimeBeforeStartTime_failure() {
        String userInput = INDEX_FIRST_PERSON.getOneBased()
                + " start/12:00 end/10:00 date/2025-10-20 sub/Mathematics";
        assertParseFailure(parser, userInput, MESSAGE_END_TIME_BEFORE_START);
    }

    @Test
    public void parse_invalidStartTimeValue_failure() {
        String userInput = INDEX_FIRST_PERSON.getOneBased()
                + " start/25:00 end/12:00 date/2025-10-20 sub/Mathematics";
        assertParseFailure(parser, userInput, MESSAGE_INVALID_START_TIME_VALUE);
    }

    @Test
    public void parse_invalidEndTimeFormat_failure() {
        String userInput = INDEX_FIRST_PERSON.getOneBased()
                + " start/10:00 end/aa:00 date/2025-10-20 sub/Mathematics";
        assertParseFailure(parser, userInput, MESSAGE_INVALID_END_TIME_FORMAT);
    }

    @Test
    public void parse_invalidEndTimeValue_failure() {
        String userInput = INDEX_FIRST_PERSON.getOneBased()
                + " start/10:00 end/24:00 date/2025-10-20 sub/Mathematics";
        assertParseFailure(parser, userInput, MESSAGE_INVALID_END_TIME_VALUE);
    }

    @Test
    public void parse_invalidDateFormat_failure() {
        String userInput = INDEX_FIRST_PERSON.getOneBased()
                + " start/10:00 end/12:00 date/2025/10/20 sub/Mathematics";
        assertParseFailure(parser, userInput, MESSAGE_INVALID_DATE_FORMAT);
    }

    @Test
    public void parse_invalidDateValue_failure() {
        String userInput = INDEX_FIRST_PERSON.getOneBased()
                + " start/10:00 end/12:00 date/2025-11-31 sub/Mathematics";
        assertParseFailure(parser, userInput, MESSAGE_INVALID_DATE_VALUE);
    }

    @Test
    public void parse_duplicateStartPrefix_failure() {
        String userInput = INDEX_FIRST_PERSON.getOneBased()
                + " start/10:00 start/11:00 end/12:00 date/2025-10-20 sub/Mathematics";
        assertParseFailure(parser, userInput,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_START));
    }

    @Test
    public void parse_duplicateEndPrefix_failure() {
        String userInput = INDEX_FIRST_PERSON.getOneBased()
                + " start/10:00 end/12:00 end/13:00 date/2025-10-20 sub/Mathematics";
        assertParseFailure(parser, userInput,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_END));
    }

    @Test
    public void parse_duplicateDatePrefix_failure() {
        String userInput = INDEX_FIRST_PERSON.getOneBased()
                + " start/10:00 end/12:00 date/2025-10-20 date/2025-10-21 sub/Mathematics";
        assertParseFailure(parser, userInput,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_DATE));
    }

    @Test
    public void parse_duplicateSubjectPrefix_failure() {
        String userInput = INDEX_FIRST_PERSON.getOneBased()
                + " start/10:00 end/12:00 date/2025-10-20 sub/Mathematics sub/Physics";
        assertParseFailure(parser, userInput,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_SUB));
    }
}
