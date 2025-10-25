package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.testutil.Assert.assertThrows;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.SearchCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.PersonContainsKeywordPredicate;

/**
 * Tests parsing of input arguments by {@code SearchCommandParser}.
 */
public class SearchCommandParserTest {

    private SearchCommandParser parser = new SearchCommandParser();

    @Test
    public void parse_validArgs_returnsSearchCommand() throws Exception {
        SearchCommand expectedCommand =
                new SearchCommand(new PersonContainsKeywordPredicate(Arrays.asList("Alice", "Bob")));
        assertEquals(expectedCommand, parser.parse("Alice Bob"));
    }

    @Test
    public void parse_extraSpaces_returnsSearchCommand() throws Exception {
        SearchCommand expectedCommand =
                new SearchCommand(new PersonContainsKeywordPredicate(Arrays.asList("Alice", "Bob")));
        assertEquals(expectedCommand, parser.parse("  Alice   Bob  "));
    }

    @Test
    public void parse_emptyArgs_throwsParseException() {
        assertThrows(ParseException.class, () -> parser.parse("   "));
    }
}

