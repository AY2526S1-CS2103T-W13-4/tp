package seedu.address.logic;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Paths;
import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.PersonContainsKeywordPredicate;
import seedu.address.storage.JsonAddressBookStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.StorageManager;
import seedu.address.testutil.TypicalPersons;

/**
 * Defensive tests for LogicManager focusing on Quick Search behavior and
 * robustness against null/empty predicates.
 */
public class LogicManagerDefensiveTest {

    private Logic logic;
    private Model model;

    @BeforeEach
    public void setUp() {
        model = new ModelManager(TypicalPersons.getTypicalAddressBook(), new UserPrefs());
        logic = new LogicManager(
                model,
                new StorageManager(
                        new JsonAddressBookStorage(Paths.get("defensive-logic.json")),
                        new JsonUserPrefsStorage(Paths.get("defensive-prefs.json"))
                )
        );
    }

    @Test
    public void updateFilteredPersonList_nullPredicate_throwsNullPointerException() {
        // Null predicate should throw NPE (fail-fast behavior)
        org.junit.jupiter.api.Assertions.assertThrows(
                NullPointerException.class, () -> logic.updateFilteredPersonList(null));
    }

    @Test
    public void updateFilteredPersonList_emptyPredicate_returnsAllPersons() {
        // Predicate that accepts all persons (empty keyword list)
        PersonContainsKeywordPredicate emptyPredicate =
                new PersonContainsKeywordPredicate(Collections.emptyList());

        assertDoesNotThrow(() -> logic.updateFilteredPersonList(emptyPredicate));
        assertEquals(TypicalPersons.getTypicalPersons().size(), model.getFilteredPersonList().size());
    }
}

