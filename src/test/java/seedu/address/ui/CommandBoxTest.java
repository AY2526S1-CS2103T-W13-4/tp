package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javafx.application.Platform;
import javafx.scene.control.TextField;
import seedu.address.logic.commands.CommandResult;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Integration-style test that ensures {@link CommandBox} never propagates
 * exceptions thrown by the command executor.
 */
public class CommandBoxTest {

    @BeforeEach
    public void setup() {
        JavaFxInitializer.init();
    }

    /**
     * Ensures that CommandBox safely handles CommandException and ParseException
     * without throwing to the caller (defensive behavior).
     */
    @Test
    public void defensive_executeCommand_doesNotThrowForParseOrCommandErrors() {
        // Start with a no-op executor to prevent early listener exceptions
        CommandBox.CommandExecutor safeExecutor = commandText -> new CommandResult("OK");
        CommandBox box = new CommandBox(safeExecutor);

        // Replace the executor field reflectively with a throwing mock
        setExecutor(box, commandText -> {
            if ("parseError".equals(commandText)) {
                throw new ParseException("Simulated parse error");
            } else if ("commandError".equals(commandText)) {
                throw new CommandException("Simulated command error");
            }
            return new CommandResult("OK");
        });

        assertDoesNotThrow(() -> {
            setTextField(box, "parseError");
            invokeHandleCommandEntered(box);

            setTextField(box, "commandError");
            invokeHandleCommandEntered(box);
        });
    }

    // ---------------------------------------------------------------------
    // Reflection helpers
    // ---------------------------------------------------------------------

    private void setTextField(CommandBox box, String value) {
        runOnFxAndWait(() -> {
            try {
                Field f = CommandBox.class.getDeclaredField("commandTextField");
                f.setAccessible(true);
                TextField tf = (TextField) f.get(box);
                tf.setText(value);
            } catch (Exception e) {
                throw new RuntimeException("Failed to set commandTextField via reflection", e);
            }
        });
    }

    private void setExecutor(CommandBox box, CommandBox.CommandExecutor newExecutor) {
        try {
            Field f = CommandBox.class.getDeclaredField("commandExecutor");
            f.setAccessible(true);
            f.set(box, newExecutor);
        } catch (Exception e) {
            throw new RuntimeException("Failed to replace commandExecutor via reflection", e);
        }
    }

    private void invokeHandleCommandEntered(CommandBox box) {
        runOnFxAndWait(() -> {
            try {
                Method m = CommandBox.class.getDeclaredMethod("handleCommandEntered");
                m.setAccessible(true);
                m.invoke(box);
            } catch (Exception e) {
                throw new RuntimeException("Failed to invoke handleCommandEntered via reflection", e);
            }
        });
    }

    private void runOnFxAndWait(Runnable r) {
        if (Platform.isFxApplicationThread()) {
            r.run();
            return;
        }
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            try {
                r.run();
            } finally {
                latch.countDown();
            }
        });
        try {
            if (!latch.await(2, TimeUnit.SECONDS)) {
                throw new RuntimeException("FX task did not complete in time");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Interrupted while waiting for FX task", e);
        }
    }
}

