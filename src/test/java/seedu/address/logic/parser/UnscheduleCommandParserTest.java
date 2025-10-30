package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.logic.parser.CliSyntax.PREFIX_LESSON;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_PERSON;

import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.UnscheduleCommand;

/**
 * Contains unit tests for {@link UnscheduleCommandParser}.
 */
public class UnscheduleCommandParserTest {

    private final UnscheduleCommandParser parser = new UnscheduleCommandParser();

    @Test
    public void parse_validArgs_returnsUnscheduleCommand() {
        UnscheduleCommand expectedCommand = new UnscheduleCommand(INDEX_FIRST_PERSON, Index.fromOneBased(2));
        assertParseSuccess(parser, "1 lesson/2", expectedCommand);
    }

    @Test
    public void parse_missingLessonPrefix_failure() {
        assertParseFailure(parser, "1 2",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, UnscheduleCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_invalidPersonIndex_failure() {
        assertParseFailure(parser, "a lesson/1",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, UnscheduleCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_invalidLessonIndex_failure() {
        assertParseFailure(parser, INDEX_SECOND_PERSON.getOneBased() + " lesson/a",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, UnscheduleCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_duplicateLessonPrefix_failure() {
        assertParseFailure(parser, "1 lesson/1 lesson/2",
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_LESSON));
    }
}
