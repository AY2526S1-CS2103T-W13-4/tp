package seedu.address.model.person;

import java.util.List;
import java.util.function.Predicate;
import java.util.logging.Logger;

import seedu.address.commons.core.LogsCenter;

/**
 * Tests that a {@code Person}'s name, phone, or email contains any of the given keywords.
 * Matching is case-insensitive and allows partial matches.
 */
public class PersonContainsKeywordPredicate implements Predicate<Person> {

    private static final Logger logger = LogsCenter.getLogger(PersonContainsKeywordPredicate.class);

    private final List<String> keywords;

    /**
     * Constructs a {@code PersonContainsKeywordPredicate} with the specified list of keywords.
     *
     * @param keywords The list of keywords to match against a person's fields.
     */
    public PersonContainsKeywordPredicate(List<String> keywords) {
        assert keywords != null : "Keywords list should not be null";
        this.keywords = keywords;
    }

    /**
     * Evaluates this predicate on the given {@code Person}.
     * Returns true if the person's name, phone, or email contains any of the given keywords.
     * Matching is case-insensitive and allows partial matches.
     * <p>
     * If the list of keywords is empty or {@code null}, this method returns true for all persons.
     *
     * @param person The {@code Person} to test.
     * @return true if any field (name, phone, or email) contains one of the keywords, or if there are no keywords.
     */
    @Override
    public boolean test(Person person) {
        assert person != null : "Person to test should not be null";

        if (keywords == null) {
            logger.warning("Keywords list is null — returning true defensively");
            return true;
        }

        if (keywords.isEmpty()) {
            return true;
        }

        String name = person.getName().fullName.toLowerCase();
        String phone = person.getPhone().value.toLowerCase();
        String email = person.getEmail().value.toLowerCase();

        boolean matchFound = keywords.stream().anyMatch(keyword -> {
            assert keyword != null : "Individual keyword should not be null";
            String lowerKeyword = keyword.toLowerCase().trim();

            return name.contains(lowerKeyword)
                    || phone.contains(lowerKeyword)
                    || email.contains(lowerKeyword);
        });

        if (!matchFound) {
            logger.fine("No match found for: " + keywords + " in person: " + person.getName());
        }

        return matchFound;
    }

    /**
     * Compares this predicate with another object for equality.
     * Two predicates are equal if they have the same list of keywords.
     *
     * @param other The object to compare to.
     * @return true if the other object is a {@code PersonContainsKeywordPredicate} with the same keywords.
     */
    @Override
    public boolean equals(Object other) {
        return other == this
                || (other instanceof PersonContainsKeywordPredicate
                && keywords.equals(((PersonContainsKeywordPredicate) other).keywords));
    }
}

