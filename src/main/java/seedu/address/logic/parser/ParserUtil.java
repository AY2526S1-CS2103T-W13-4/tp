package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.StringUtil;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.Address;
import seedu.address.model.person.Attribute;
import seedu.address.model.person.Email;
import seedu.address.model.person.Name;
import seedu.address.model.person.Phone;
import seedu.address.model.tag.Tag;

/**
 * Contains utility methods used for parsing strings in the various *Parser classes.
 */
public class ParserUtil {

    public static final String MESSAGE_INVALID_INDEX =
            "The person index provided is invalid";
    public static final String MESSAGE_INVALID_LESSON_INDEX =
            "The lesson index provided is invalid";

    /**
     * Parses {@code oneBasedIndex} into an {@code Index} and returns it. Leading and trailing whitespaces will be
     * trimmed.
     * @throws ParseException if the specified index is invalid (not non-zero unsigned integer).
     */
    public static Index parseIndex(String oneBasedIndex) throws ParseException {
        String trimmedIndex = oneBasedIndex.trim();
        if (!StringUtil.isNonZeroUnsignedInteger(trimmedIndex)) {
            throw new ParseException(MESSAGE_INVALID_INDEX);
        }
        return Index.fromOneBased(Integer.parseInt(trimmedIndex));
    }

    /**
     * Validates that the provided {@code rawValue} is a single, non-empty token suitable to be parsed as an index.
     * Leading and trailing whitespaces are trimmed before validation.
     *
     * @throws ParseException if the value is empty or contains whitespace.
     */
    public static String requireSingleIndex(String rawValue, String usageMessage) throws ParseException {
        String trimmedValue = rawValue == null ? "" : rawValue.trim();
        if (trimmedValue.isEmpty() || trimmedValue.contains(" ")) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, usageMessage));
        }
        return trimmedValue;
    }


    /**
     * Parses a {@code String name} into a {@code Name}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code name} is invalid.
     */
    public static Name parseName(String name) throws ParseException {
        requireNonNull(name);
        String trimmedName = name.trim();
        if (!Name.isValidName(trimmedName)) {
            throw new ParseException(Name.MESSAGE_CONSTRAINTS);
        }
        return new Name(trimmedName);
    }

    /**
     * Parses a {@code String phone} into a {@code Phone}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code phone} is invalid.
     */
    public static Phone parsePhone(String phone) throws ParseException {
        requireNonNull(phone);
        String trimmedPhone = phone.trim();
        if (!Phone.isValidPhone(trimmedPhone)) {
            throw new ParseException(Phone.MESSAGE_CONSTRAINTS);
        }
        return new Phone(trimmedPhone);
    }

    /**
     * Parses a {@code String address} into an {@code Address}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code address} is invalid.
     */
    public static Address parseAddress(String address) throws ParseException {
        requireNonNull(address);
        String trimmedAddress = address.trim();
        if (!Address.isValidAddress(trimmedAddress)) {
            throw new ParseException(Address.MESSAGE_CONSTRAINTS);
        }
        return new Address(trimmedAddress);
    }

    /**
     * Parses a {@code String email} into an {@code Email}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code email} is invalid.
     */
    public static Email parseEmail(String email) throws ParseException {
        requireNonNull(email);
        String trimmedEmail = email.trim();
        if (!Email.isValidEmail(trimmedEmail)) {
            throw new ParseException(Email.MESSAGE_CONSTRAINTS);
        }
        return new Email(trimmedEmail);
    }

    /**
     * Parses a {@code String tag} into a {@code Tag}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code tag} is invalid.
     */
    public static Tag parseTag(String tag) throws ParseException {
        requireNonNull(tag);
        String trimmedTag = tag.trim();
        if (!Tag.isValidTagName(trimmedTag)) {
            throw new ParseException(Tag.MESSAGE_CONSTRAINTS);
        }
        return new Tag(trimmedTag);
    }

    /**
     * Parses {@code Collection<String> tags} into a {@code Set<Tag>}.
     */
    public static Set<Tag> parseTags(Collection<String> tags) throws ParseException {
        requireNonNull(tags);
        final Set<Tag> tagSet = new HashSet<>();
        for (String tagName : tags) {
            tagSet.add(parseTag(tagName));
        }
        return tagSet;
    }

    /**
     * Parses {@code Collection<String> attributes} into a {@code Set<Attribute>}.
     */
    public static Set<Attribute> parseAttributes(Collection<String> attributes) throws ParseException {
        requireNonNull(attributes);
        final Set<Attribute> attributeSet = new HashSet<>();
        for (String attrString : attributes) {
            attributeSet.add(parseAttribute(attrString));
        }
        return attributeSet;
    }

    /**
     * Parses a {@code String attribute} into an {@code Attribute}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code attribute} is invalid.
     */
    public static Attribute parseAttribute(String attribute) throws ParseException {
        requireNonNull(attribute);
        String trimmedAttribute = attribute.trim();

        if (!trimmedAttribute.contains("=")) {
            throw new ParseException("Incorrect format. Use key=value[,value2]...");
        }

        String[] keyValue = trimmedAttribute.split("=", 2); // limit to 2 parts only
        String key = keyValue[0].trim().toLowerCase();

        if (key.isEmpty()) {
            throw new ParseException("Attribute key cannot be empty.");
        }

        // Split values by comma, e.g. "math,science"
        Set<String> values = new HashSet<>();
        String[] valueArray = keyValue[1].split(",");
        for (String value : valueArray) {
            String trimmedValue = value.trim();
            if (!trimmedValue.isEmpty()) {
                values.add(trimmedValue);
            }
        }

        if (values.isEmpty()) {
            throw new ParseException("Attribute must have at least one value.");
        }

        return new Attribute(key, values);
    }
}
