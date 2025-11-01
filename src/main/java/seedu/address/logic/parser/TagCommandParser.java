package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ATTRIBUTE;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.TagCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.Attribute;

/**
 * Parses input arguments and creates a new {@code TagCommand} object.
 */
public class TagCommandParser implements Parser<TagCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the {@code TagCommand}
     * and returns a {@code TagCommand} object for execution.
     * @throws ParseException if the user input does not conform to the expected format.
     */
    public TagCommand parse(String args) throws ParseException {
        requireNonNull(args);
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args, PREFIX_ATTRIBUTE);

        Map<String, Attribute> attributesByKey = new LinkedHashMap<>();

        // For each attr/ prefix segment (e.g. attr/subject=math,science)
        for (String attrString : argMultimap.getAllValues(PREFIX_ATTRIBUTE)) {
            if (!attrString.contains("=")) {
                throw new ParseException("Incorrect format. Use attr/key=value[,value2]...");
            }

            String[] keyValue = attrString.split("=", 2); // limit to 2 parts only
            String key = keyValue[0].trim().toLowerCase();

            if (key.isEmpty()) {
                throw new ParseException("Attribute key cannot be empty.");
            }

            // Split values by comma, e.g. "math,science"
            Set<String> values = new HashSet<>();
            Arrays.stream(keyValue[1].split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .forEach(values::add);

            if (values.isEmpty()) {
                throw new ParseException("Attribute must have at least one value.");
            }

            Attribute attribute = new Attribute(key, values);
            attributesByKey.put(key, attribute);
        }

        if (attributesByKey.isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, TagCommand.MESSAGE_USAGE));
        }

        String preamble = ParserUtil.requireSingleIndex(argMultimap.getPreamble(), TagCommand.MESSAGE_USAGE);
        if (preamble.startsWith(PREFIX_ATTRIBUTE.getPrefix())) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, TagCommand.MESSAGE_USAGE));
        }
        Index index = ParserUtil.parseIndex(preamble);

        Set<Attribute> attributesToAdd = new LinkedHashSet<>(attributesByKey.values());

        return new TagCommand(index, attributesToAdd);
    }
}
