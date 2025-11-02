package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_DATE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_DATE_END;
import static seedu.address.logic.parser.CliSyntax.PREFIX_END;
import static seedu.address.logic.parser.CliSyntax.PREFIX_START;
import static seedu.address.logic.parser.CliSyntax.PREFIX_SUB;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.regex.Pattern;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.ScheduleCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.Lesson;


/**
 * Parses input arguments and creates a new {@code ScheduleCommand} object
 */
public class ScheduleCommandParser implements Parser<ScheduleCommand> {

    public static final String MESSAGE_INVALID_START_TIME_FORMAT =
            "Invalid start time format. Use HH:mm (e.g. 14:00).";
    public static final String MESSAGE_INVALID_START_TIME_VALUE =
            "Invalid start time. Hours must be 00-23 and minutes must be 00-59.";
    public static final String MESSAGE_INVALID_END_TIME_FORMAT =
            "Invalid end time format. Use HH:mm (e.g. 14:00).";
    public static final String MESSAGE_INVALID_END_TIME_VALUE =
            "Invalid end time. Hours must be 00-23 and minutes must be 00-59.";
    public static final String MESSAGE_INVALID_DATE_FORMAT =
            "Invalid date format. Use YYYY-MM-DD (e.g. 2025-09-20).";
    public static final String MESSAGE_INVALID_DATE_VALUE =
            "Invalid date. Ensure the day is valid for the given month and year.";
    public static final String MESSAGE_END_BEFORE_START =
            "End time must be after start time.";
    public static final String MESSAGE_END_DATE_BEFORE_START_DATE =
            "End date cannot be before start date.";

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm")
            .withResolverStyle(ResolverStyle.STRICT);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("uuuu-MM-dd")
            .withResolverStyle(ResolverStyle.STRICT);

    private static final Pattern TIME_PATTERN = Pattern.compile("^\\d{2}:\\d{2}$");
    private static final Pattern DATE_PATTERN = Pattern.compile("^\\d{4}-\\d{2}-\\d{2}$");

    /**
     * Parses the given {@code String} of arguments in the context of the {@code ScheduleCommand}
     * and returns a {@code ScheduleCommand} object for execution.
     * @throws ParseException if the user input does not conform the expected format
     */
    public ScheduleCommand parse(String args) throws ParseException {
        requireNonNull(args);
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args,
                PREFIX_START, PREFIX_END, PREFIX_DATE, PREFIX_DATE_END, PREFIX_SUB);

        String preamble = ParserUtil.requireSingleIndex(argMultimap.getPreamble(), ScheduleCommand.MESSAGE_USAGE);

        // Check for missing arguments
        if (!argMultimap.getValue(PREFIX_START).isPresent()
                || !argMultimap.getValue(PREFIX_END).isPresent()
                || !argMultimap.getValue(PREFIX_DATE).isPresent()
                || !argMultimap.getValue(PREFIX_SUB).isPresent()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                    ScheduleCommand.MESSAGE_USAGE));
        }

        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_START, PREFIX_END, PREFIX_DATE, PREFIX_DATE_END, PREFIX_SUB);

        String start = argMultimap.getValue(PREFIX_START).get().trim();
        String end = argMultimap.getValue(PREFIX_END).get().trim();
        String date = argMultimap.getValue(PREFIX_DATE).get().trim();
        String date2 = argMultimap.getValue(PREFIX_DATE_END).map(String::trim).orElse("");
        String sub = argMultimap.getValue(PREFIX_SUB).get().trim();
        if (start.isEmpty() || end.isEmpty() || date.isEmpty() || sub.isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                    ScheduleCommand.MESSAGE_USAGE));
        }
        if (argMultimap.getValue(PREFIX_DATE_END).isPresent() && date2.isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                    ScheduleCommand.MESSAGE_USAGE));
        }

        Index index = ParserUtil.parseIndex(preamble);

        // Validate start time format first, then value
        if (!TIME_PATTERN.matcher(start).matches()) {
            throw new ParseException(MESSAGE_INVALID_START_TIME_FORMAT);
        }
        LocalTime startTime;
        try {
            startTime = LocalTime.parse(start, TIME_FORMATTER);
        } catch (DateTimeParseException e) {
            throw new ParseException(MESSAGE_INVALID_START_TIME_VALUE);
        }

        // Validate end time format first, then value
        if (!TIME_PATTERN.matcher(end).matches()) {
            throw new ParseException(MESSAGE_INVALID_END_TIME_FORMAT);
        }
        LocalTime endTime;
        try {
            endTime = LocalTime.parse(end, TIME_FORMATTER);
        } catch (DateTimeParseException e) {
            throw new ParseException(MESSAGE_INVALID_END_TIME_VALUE);
        }

        // Validate date format first, then value
        if (!DATE_PATTERN.matcher(date).matches()) {
            throw new ParseException(MESSAGE_INVALID_DATE_FORMAT);
        }
        LocalDate startDate;
        try {
            startDate = LocalDate.parse(date, DATE_FORMATTER);
        } catch (DateTimeParseException e) {
            throw new ParseException(MESSAGE_INVALID_DATE_VALUE);
        }

        LocalDate endDate = startDate;
        if (!date2.isEmpty()) {
            if (!DATE_PATTERN.matcher(date2).matches()) {
                throw new ParseException(MESSAGE_INVALID_DATE_FORMAT);
            }
            try {
                endDate = LocalDate.parse(date2, DATE_FORMATTER);
            } catch (DateTimeParseException e) {
                throw new ParseException(MESSAGE_INVALID_DATE_VALUE);
            }

            if (endDate.isBefore(startDate)) {
                throw new ParseException(MESSAGE_END_DATE_BEFORE_START_DATE);
            }
        }

        // Validate end datetime after start datetime
        if (!endDate.atTime(endTime).isAfter(startDate.atTime(startTime))) {
            throw new ParseException(MESSAGE_END_BEFORE_START);
        }

        Lesson lesson = new Lesson(start, end, startDate.toString(), endDate.toString(), sub, false);
        return new ScheduleCommand(index, lesson);
    }
}
