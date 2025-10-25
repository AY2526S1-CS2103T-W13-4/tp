package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.TypicalPersons.BENSON;
import static seedu.address.testutil.TypicalPersons.CARL;
import static seedu.address.testutil.TypicalPersons.DANIEL;
import static seedu.address.testutil.TypicalPersons.ELLE;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.util.Arrays;
import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import seedu.address.commons.core.Messages;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.PersonContainsKeywordPredicate;

/**
 * Contains integration tests (interaction with the Model) for {@code SearchCommand}.
 */
public class SearchCommandTest {

    private Model model;
    private Model expectedModel;

    @BeforeEach
    public void setUp() {
        model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        expectedModel = new ModelManager(getTypicalAddressBook(), new UserPrefs());
    }

    @Test
    public void equals() {
        PersonContainsKeywordPredicate firstPredicate =
                new PersonContainsKeywordPredicate(Collections.singletonList("first"));
        PersonContainsKeywordPredicate secondPredicate =
                new PersonContainsKeywordPredicate(Collections.singletonList("second"));

        SearchCommand firstCommand = new SearchCommand(firstPredicate);
        SearchCommand secondCommand = new SearchCommand(secondPredicate);

        // same object -> true
        assertEquals(firstCommand, firstCommand);

        // same values -> true
        SearchCommand firstCommandCopy = new SearchCommand(firstPredicate);
        assertEquals(firstCommand, firstCommandCopy);

        // different predicate -> false
        assertNotEquals(firstCommand, secondCommand);
    }

    /**
     * Note: The parser rejects empty args, but if a SearchCommand is constructed directly
     * with an empty predicate, our predicate is defensive and returns all persons.
     */
    @Test
    public void execute_zeroKeywords_returnsAllPersons() {
        int total = getTypicalAddressBook().getPersonList().size();
        String expectedMessage = String.format(Messages.MESSAGE_PERSONS_LISTED_OVERVIEW, total);

        PersonContainsKeywordPredicate predicate = new PersonContainsKeywordPredicate(Collections.emptyList());
        SearchCommand command = new SearchCommand(predicate);

        expectedModel.updateFilteredPersonList(predicate);
        assertCommandSuccess(command, model, expectedMessage, expectedModel);

        // ensure we actually listed everyone
        assertEquals(expectedModel.getFilteredPersonList(), model.getFilteredPersonList());
        assertEquals(total, model.getFilteredPersonList().size());
    }

    @Test
    public void execute_multipleKeywords_multiplePersonsFound() {
        // "Kurz" -> CARL, "Meier" -> BENSON & DANIEL, "Elle" -> ELLE
        // Order must follow underlying list order in TypicalPersons: BENSON, CARL, DANIEL, ELLE
        String expectedMessage = String.format(Messages.MESSAGE_PERSONS_LISTED_OVERVIEW, 4);

        PersonContainsKeywordPredicate predicate =
                new PersonContainsKeywordPredicate(Arrays.asList("Kurz", "Meier", "Elle"));
        SearchCommand command = new SearchCommand(predicate);

        expectedModel.updateFilteredPersonList(predicate);
        assertCommandSuccess(command, model, expectedMessage, expectedModel);

        assertEquals(Arrays.asList(BENSON, CARL, DANIEL, ELLE), model.getFilteredPersonList());
    }
}

