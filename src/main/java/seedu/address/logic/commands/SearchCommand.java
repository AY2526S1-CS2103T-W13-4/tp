package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import seedu.address.commons.core.Messages;
import seedu.address.model.Model;
import seedu.address.model.person.PersonContainsKeywordPredicate;

/**
 * Searches for persons whose name, phone, or email contains any of the specified keywords.
 * Matching is case-insensitive and allows partial matches.
 */
public class SearchCommand extends Command {

    public static final String COMMAND_WORD = "search";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Finds all persons whose "
            + "name, phone, or email contain any of the specified keywords (case-insensitive) "
            + "and displays them as a list.\n"
            + "Parameters: KEYWORD [MORE_KEYWORDS]...\n"
            + "Example: " + COMMAND_WORD + " alice john";

    private final PersonContainsKeywordPredicate predicate;

    public SearchCommand(PersonContainsKeywordPredicate predicate) {
        this.predicate = predicate;
    }

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        model.updateFilteredPersonList(predicate);
        return new CommandResult(
                String.format(Messages.MESSAGE_PERSONS_LISTED_OVERVIEW,
                        model.getFilteredPersonList().size()));
    }

    @Override
    public boolean equals(Object other) {
        return other == this
                || (other instanceof SearchCommand
                && predicate.equals(((SearchCommand) other).predicate));
    }
}

