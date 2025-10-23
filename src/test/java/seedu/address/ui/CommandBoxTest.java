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
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Minimal tests for {@link CommandBox} that verify defensive behavior.
 * Uses reflection to interact with private members to avoid bringing in TestFX.
 */
public class CommandBoxTest {

    @BeforeEach
    public void setup() {
        JavaFxInitializer.init();
    }

    /**
     * Ensures that when the executor throws parse/command errors, CommandBox
     * handles them defensively (i.e., no exception escapes to the caller).
     */
    @Test
    public void defensive_executeCommand_doesNotThrowForParseOrCommandErrors() {
        // Executor that throws deterministic exceptions based on the input
        CommandBox.CommandExecutor throwingExecutor = commandText -> {
            if ("parseError".equals(commandText)) {
                throw new ParseException("Simulated parse error");
            } else if ("commandError".equals(commandText)) {
                throw new CommandException("Simulated command error");
            }
            return null; // success path not used here
        };

        CommandBox box = new CommandBox(throwingExecutor);

        assertDoesNotThrow(() -> {
            setTextField(box, "parseError");
            invokeHandleCommandEntered(box);

            setTextField(box, "commandError");
            invokeHandleCommandEntered(box);
        });
    }

    // ---------------------------------------------------------------------
    // Helpers
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

    /**
     * Runs the runnable on the JavaFX Application Thread and waits for completion.
     */
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
            // Wait up to 2 seconds to be safe on CI
            if (!latch.await(2, TimeUnit.SECONDS)) {
                throw new RuntimeException("FX task did not complete in time");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Interrupted while waiting for FX task", e);
        }
    }
}


