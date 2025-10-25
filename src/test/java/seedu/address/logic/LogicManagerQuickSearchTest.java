package seedu.address.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javafx.collections.ObservableList;
import seedu.address.logic.commands.CommandResult;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Person;
import seedu.address.model.person.PersonContainsKeywordPredicate;
import seedu.address.storage.JsonAddressBookStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.Storage;
import seedu.address.storage.StorageManager;
import seedu.address.testutil.AddressBookBuilder;
import seedu.address.testutil.TypicalPersons;

/**
 * Logic-layer tests for Quick Search (live filtering via updateFilteredPersonList).
 * These tests avoid JavaFX UI and therefore run reliably on CI.
 */
public class LogicManagerQuickSearchTest {

    private Logic logic;

    @BeforeEach
    public void setUp() {
        // Use a typical address book to simulate real data
        ReadOnlyAddressBook ab = new AddressBookBuilder()
                .withPerson(TypicalPersons.ALICE)
                .withPerson(TypicalPersons.BENSON)
                .withPerson(TypicalPersons.CARL)
                .withPerson(TypicalPersons.DANIEL)
                .withPerson(TypicalPersons.ELLE)
                .withPerson(TypicalPersons.FIONA)
                .withPerson(TypicalPersons.GEORGE)
                .build();

        Model model = new ModelManager(ab, new UserPrefs());

        // ✅ Properly instantiate StorageManager with both required components
        JsonAddressBookStorage addressBookStorage =
                new JsonAddressBookStorage(Paths.get("test-logic-manager.json"));
        JsonUserPrefsStorage userPrefsStorage =
                new JsonUserPrefsStorage(Paths.get("test-user-prefs.json"));
        Storage storage = new StorageManager(addressBookStorage, userPrefsStorage);

        logic = new LogicManager(model, storage);
    }


    @Test
    public void quickSearch_namePartialMatch_returnsExpected() {
        // "Meier" should match BENSON and DANIEL by name (order preserved)
        logic.updateFilteredPersonList(new PersonContainsKeywordPredicate(Collections.singletonList("Meier")));
        ObservableList<Person> list = logic.getFilteredPersonList();
        assertEquals(Arrays.asList(TypicalPersons.BENSON, TypicalPersons.DANIEL), list);
    }

    @Test
    public void quickSearch_phonePartialMatch_returnsExpected() {
        // "9482" matches ELLE, FIONA, GEORGE (from TypicalPersons)
        logic.updateFilteredPersonList(new PersonContainsKeywordPredicate(Collections.singletonList("9482")));
        ObservableList<Person> list = logic.getFilteredPersonList();
        assertEquals(Arrays.asList(TypicalPersons.ELLE, TypicalPersons.FIONA, TypicalPersons.GEORGE), list);
    }

    @Test
    public void quickSearch_emailPartialMatch_returnsSome() {
        // "example" appears in typical emails (ensure > 0 results but don't depend on exact count here)
        logic.updateFilteredPersonList(new PersonContainsKeywordPredicate(Collections.singletonList("example")));
        assertTrue(logic.getFilteredPersonList().size() > 0);
    }

    @Test
    public void quickSearch_emptyKeywords_showsAll() {
        logic.updateFilteredPersonList(new PersonContainsKeywordPredicate(Collections.emptyList()));
        assertEquals(new AddressBook(TypicalPersons.getTypicalAddressBook()).getPersonList(),
                logic.getFilteredPersonList());
    }

    // Sanity check: defensive call via Logic.execute still works and persists changes
    @Test
    public void execute_listCommand_restoresAllPersons() throws CommandException, ParseException {
        // First, filter to a subset
        logic.updateFilteredPersonList(new PersonContainsKeywordPredicate(Collections.singletonList("Meier")));
        assertEquals(2, logic.getFilteredPersonList().size());

        // Then run "list" (through parser) to ensure logic flow is intact
        CommandResult result = logic.execute("list");
        assertTrue(result.getFeedbackToUser().toLowerCase().contains("listed"));
        assertEquals(new AddressBook(TypicalPersons.getTypicalAddressBook()).getPersonList(),
                logic.getFilteredPersonList());
    }
}
