package seedu.address.logic.parser;

import java.util.Arrays;

import seedu.address.logic.commands.SearchCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.PersonContainsKeywordPredicate;

/**
 * Parses input arguments and creates a new {@code SearchCommand}.
 */
public class SearchCommandParser implements Parser<SearchCommand> {

    @Override
    public SearchCommand parse(String args) throws ParseException {
        String trimmedArgs = args.trim();
        if (trimmedArgs.isEmpty()) {
            throw new ParseException("Please provide one or more keywords for search.");
        }

        String[] keywords = trimmedArgs.split("\\s+");
        return new SearchCommand(new PersonContainsKeywordPredicate(Arrays.asList(keywords)));
    }
}

